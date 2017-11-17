package base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import conf.LocalEnv;
import conf.ServerEnv;
import modules.Modules.M_70_63;
import modules.Modules.M_74;
import modules.Modules.M_76;

public class Single {
	// 选中应用
	public static M_74 app_74 = null;

	public static M_76 app_76 = null;
	
	public static M_70_63 app_63 = null;

	// 是否清除日志
	public static boolean clearLog = true;
	// 是否仅重启[没有上传zip包]
	public static boolean onlyReboot = true;
	// 是否仅重启[没有上传zip包]
	public static boolean doBuild = false;
	// [启动后等待X秒，取回启动日志]
	public static int seeRemoteLogAfterXSeconds = 0;

	public static void main(String[] args) {
		String appName = "";
		if (app_74 != null) {
			appName = app_74.toString();
			ServerEnv.ip = "172.16.54.74";
			ServerEnv.password = "aixocm";
			if (M_74.jiaxin_gw_dataconf.name().equals(appName)) {
				ServerEnv.dest = "/usr/local/jiaxin_gw_container-1.0-conf";
			}  
		}
		
		if (app_63 != null) {
			appName = app_63.toString();
			ServerEnv.ip = "172.16.70.63";
			ServerEnv.password = "excomm@123";
			ServerEnv.dest = "/usr/local/jiaxin/jiaxin_gw_container-1.0";
		}
		if (app_76 != null) {
			appName = app_76.toString();
		}
		if (appName.equals("")) {
			System.out.println("appName is null!");
			return;
		}
		Integer appCode = getAppCode(appName);
		boolean hasUploadMod = false;
		if (appCode == null) {
			System.out.println("appCode  is null!");
			System.out.println("need to upload app to mod dir!");
			boolean result = uploadAppMod(appName);
			if (result) {
				appCode = getAppCode(appName);
				System.out.println(appName + "->" + appCode);
				doBuild = false;
				hasUploadMod = true;
			}
		} else {
			System.out.println(appName + "->" + appCode);
		}
		if (doBuild) {
			Ant.build = LocalEnv.path + appName + "/build.xml";
			if (!Ant.doAnt()) {
				System.exit(-1);
			}
		}
		Ssh ssh = new Ssh();
		String file = LocalEnv.path + appName + "/" + appName + ".zip";
		boolean b = false;
		if (!hasUploadMod) {
			b = onlyReboot || ssh.uploadFile(file);
		} else {
			onlyReboot = true;
			b = true;
		}
		if (!b) {
			System.out.println("Copy file fail...");
			System.exit(-1);
		}
		ssh.doit(clearLog, appName, appCode, onlyReboot);
		try {
			if (seeRemoteLogAfterXSeconds > 0) {
				if (StringUtils.isBlank(LocalEnv.tempLocalLog)) {
					System.out.println("no local log path exist :" + LocalEnv.tempLocalLog);
				} else {
					System.out.println("waiting for see result log of " + appName + " ...");
					Thread.sleep(seeRemoteLogAfterXSeconds * 1000);
					ssh.getLog(appName);
					File log = new File(LocalEnv.tempLocalLog + "/" + appName + ".out");
					InputStream io = new FileInputStream(log);
					byte[] result = new byte[io.available()];
					io.read(result);
					System.out.println(new String(result));
					io.close();
					log.delete();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
	}

	public static Integer getAppCode(String appName) {
		Ssh ssh = new Ssh();
		Integer appCode = null;
		List<String> cmdList = new ArrayList<String>();
		cmdList.add("cd " + ServerEnv.dest);
		cmdList.add("./admin.sh");
		cmdList.add("1");

		String[] cmd = new String[cmdList.size()];
		cmdList.toArray(cmd);
		ssh.executeCommands2(cmd);
		String result = ssh.getResponse();
		String[] resultList = result.split("-------------------------------------------------------------------------");
		for (String s : resultList) {
			if (s.contains(appName)) {
				if (s.contains("down")) {
					appCode = Integer.parseInt(s.split("-")[0].trim());
				} else {
					appCode = Integer.parseInt(s.split("root")[0].trim());
				}
			}
		}
		ssh.disconnect();
		return appCode;
	}

	public static boolean uploadAppMod(String appName) {
		Ssh ssh = new Ssh();
		String file = LocalEnv.path + appName + "/" + appName + ".zip";
		Ant.build = LocalEnv.path + appName + "/build.xml";
		if (!Ant.doAnt()) {
			System.exit(-1);
		}
		ssh.uploadFile(file);
		ssh.disconnect();
		return true;
	}
}
