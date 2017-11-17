package base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.oro.text.regex.MalformedPatternException;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import conf.LocalEnv;
import conf.ServerEnv;
import expect4j.Closure;
import expect4j.Expect4j;
import expect4j.ExpectState;
import expect4j.matches.EofMatch;
import expect4j.matches.Match;
import expect4j.matches.RegExpMatch;
import expect4j.matches.TimeoutMatch;

public class SshForImserver {
	private ServerEnv senv = new ServerEnv();

	private Session session;
	private ChannelShell channel;
	private static Expect4j expect = null;
	private static final long defaultTimeOut = 1000;
	private StringBuffer buffer = new StringBuffer();

	public static final int COMMAND_EXECUTION_SUCCESS_OPCODE = -2;
	public static final String BACKSLASH_R = "\r";
	public static final String BACKSLASH_N = "\n";
	public static final String COLON_CHAR = ":";
	public static String ENTER_CHARACTER = BACKSLASH_R;
	public static final int SSH_PORT = 22;

	// 正则匹配，用于处理服务器返回的结果
	public static String[] linuxPromptRegEx = new String[] { "~]#", "~#", "#", ":~#", "/$", ">" };

	public static String[] errorMsg = new String[] { "could not acquire the config lock " };

	public SshForImserver() {
		expect = getExpect();
	}

	/**
	 * 关闭SSH远程连接
	 */
	public void disconnect() {
		if (channel != null) {
			channel.disconnect();
		}
		if (session != null) {
			session.disconnect();
		}
	}

	/**
	 * 获取服务器返回的信息
	 * 
	 * @return 服务端的执行结果
	 */
	public String getResponse() {
		return buffer.toString();
	}

	// 获得Expect4j对象，该对用可以往SSH发送命令请求
	private Expect4j getExpect() {
		try {
			System.out
					.println(String.format("Start logging to %s@%s:%s", senv.getUser(), senv.getIp(), senv.getPort()));
			JSch jsch = new JSch();
			session = jsch.getSession(senv.getUser(), senv.getIp(), senv.getPort());
			session.setPassword(senv.getPassword());
			Hashtable<String, String> config = new Hashtable<String, String>();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			localUserInfo ui = new localUserInfo();
			session.setUserInfo(ui);
			session.connect();
			channel = (ChannelShell) session.openChannel("shell");
			Expect4j expect = new Expect4j(channel.getInputStream(), channel.getOutputStream());
			channel.connect();
			System.out.println(
					String.format("Logging to %s@%s:%s successfully!", senv.getUser(), senv.getIp(), senv.getPort()));
			return expect;
		} catch (Exception ex) {
			System.out.println("Connect to " + senv.getIp() + ":" + senv.getPort()
					+ " failed,please check your username and password!");
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 执行配置命令
	 * 
	 * @param commands
	 *            要执行的命令，为字符数组
	 * @return 执行是否成功
	 */
	public boolean executeCommands(String[] commands) {
		// 如果expect返回为0，说明登入没有成功
		if (expect == null) {
			return false;
		}

		Closure closure = new Closure() {
			public void run(ExpectState expectState) throws Exception {
				buffer.append(expectState.getBuffer());
				expectState.exp_continue();

			}
		};
		List<Match> lstPattern = new ArrayList<Match>();
		String[] regEx = linuxPromptRegEx;
		if (regEx != null && regEx.length > 0) {
			synchronized (regEx) {
				for (String regexElement : regEx) {
					try {
						RegExpMatch mat = new RegExpMatch(regexElement, closure);
						lstPattern.add(mat);
					} catch (MalformedPatternException e) {
						return false;
					} catch (Exception e) {
						return false;
					}
				}
				lstPattern.add(new EofMatch(new Closure() {
					public void run(ExpectState state) {
					}
				}));
				lstPattern.add(new TimeoutMatch(defaultTimeOut, new Closure() {
					public void run(ExpectState state) {
					}
				}));
			}
		}
		try {
			boolean isSuccess = true;
			for (String strCmd : commands) {
				System.out.println("----------------------------------------------");
				isSuccess = isSuccess(lstPattern, strCmd);
				System.out.println(buffer.toString().toLowerCase());
				Thread.sleep(1000);

			}
			// 防止最后一个命令执行不了
			isSuccess = !checkResult(expect.expect(lstPattern));

			// 找不到错误信息标示成功
			String response = buffer.toString().toLowerCase();
			for (String msg : errorMsg) {
				if (response.indexOf(msg) > -1) {
					return false;
				}
			}

			return isSuccess;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	// 检查执行是否成功
	private boolean isSuccess(List<Match> objPattern, String strCommandPattern) {
		try {
			boolean isFailed = checkResult(expect.expect(objPattern));
			if (!isFailed) {
				expect.send(strCommandPattern);
				expect.send("\r");
				return true;
			}
			return false;
		} catch (MalformedPatternException ex) {
			return false;
		} catch (Exception ex) {
			return false;
		}
	}

	// 检查执行返回的状态
	private boolean checkResult(int intRetVal) {
		if (intRetVal == COMMAND_EXECUTION_SUCCESS_OPCODE) {
			return true;
		}
		return false;
	}

	// 登入SSH时的控制信息
	// 设置不提示输入密码、不显示登入信息等
	public static class localUserInfo implements UserInfo {
		String passwd;

		public String getPassword() {
			return passwd;
		}

		public boolean promptYesNo(String str) {
			return true;
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			return true;
		}

		public void showMessage(String message) {

		}
	}

	public boolean uploadFile(String fileName,boolean upload_imserver_and_lib_core) {
		Connection con = new Connection(senv.getIp(), senv.getPort());
		try {
			con.connect();
			// 远程服务器的用户名密码
			boolean isAuthed = con.authenticateWithPassword(senv.getUser(), senv.getPassword());
			if (isAuthed) {
				SCPClient scpClient = con.createSCPClient();
				String path= ServerEnv.dest+"/plugins/";
				if(upload_imserver_and_lib_core){
					path= ServerEnv.dest+"/lib/";
					scpClient.put(fileName, path);
				}else{
					scpClient.put(fileName, path);
				}
				System.out.println("now to upload file " + fileName + " to server path " + path);
				System.out.println("file upload success.");
				return true;
			} else {
				System.out.println("---error user password for scp");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}

	public List<String> getCmdList(boolean deleteLog, String appName, int appCode, boolean onlyReboot) {
		List<String> cmdList = new ArrayList<String>();
//		if (!onlyReboot) {
//			cmdList.add("cd " + ServerEnv.dest);
//			cmdList.add("rm -rf " + appName);
//		}
		cmdList.add("cd " + ServerEnv.dest);
		if (deleteLog) {
			cmdList.add("cd logs");
			cmdList.add("> imserver_out.log");
			cmdList.add("> debug.log");
			cmdList.add("> info.log");
			cmdList.add("> warn.log");
			cmdList.add("> error.log");

		}
		cmdList.add("cd " + ServerEnv.dest);
		cmdList.add("./admin.sh");
		cmdList.add("3");
		cmdList.add("y");
		cmdList.add("2");
		return cmdList;
	}

	public void doit(boolean deleteLog, String appName, int appCode, boolean onlyReboot) {
		List<String> cmdList = this.getCmdList(deleteLog, appName, appCode, onlyReboot);
		String[] cmd = new String[cmdList.size()];
		cmdList.toArray(cmd);
		this.executeCommands(cmd);
		System.out.println(this.getResponse());
		this.disconnect();
	}

	public boolean getLog(String appName) {
		if (StringUtils.isBlank(LocalEnv.tempLocalLog)) {
			System.out.println("no local log for boot result.");
			return false;
		}
		Connection con = new Connection(senv.getIp(), senv.getPort());
		try {
			con.connect();
			// 远程服务器的用户名密码
			boolean isAuthed = con.authenticateWithPassword(senv.getUser(), senv.getPassword());
			if (isAuthed) {
				SCPClient scpClient = con.createSCPClient();
				scpClient.get(ServerEnv.dest + "/logs/imserver_out.log", LocalEnv.tempLocalLog+"/");
				return true;
			} else {
				System.out.println("---error user password for scp");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}



}
