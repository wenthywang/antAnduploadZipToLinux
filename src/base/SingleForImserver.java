package base;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;

import conf.LocalEnv;
import conf.ServerEnv;
import modules.Modules.M_74;
import modules.Modules.M_76;

public class SingleForImserver {
	// 选中应用
	public static M_74 app_74 = null;

	public static M_76 app_76 = null;

	// 是否清除日志
	public static boolean clearLog = true;
	// 是否仅重启[没有上传zip包]
	public static boolean onlyReboot = true;
	// 是否仅重启[没有上传zip包]
	public static boolean doBuild = false;
	// [启动后等待X秒，取回启动日志]
	public static int seeRemoteLogAfterXSeconds = 0;
	
	public static String pluginName="";
	
	public static boolean upload_imserver_and_lib_core=false;

	public static void run() {
		String appName = app_76.name();
		ServerEnv.dest = "/usr/local/jiaxin_im_server";
			
	
		if (appName.equals("")) {
			System.out.println("appName is null!");
			return;
		}
		boolean result = uploadAppMod(appName);
		
		if(result){
			SshForImserver ssh = new SshForImserver();
			ssh.doit(clearLog, appName, 1, onlyReboot);
			try {
				if (seeRemoteLogAfterXSeconds > 0) {
					if (StringUtils.isBlank(LocalEnv.tempLocalLog)) {
						System.out.println("no local log path exist :" + LocalEnv.tempLocalLog);
					} else {
						System.out.println("waiting for see result log of " + appName + " ...");
						Thread.sleep(seeRemoteLogAfterXSeconds * 1000);
						ssh.getLog(appName);
						File log = new File(LocalEnv.tempLocalLog + "/imserver_out.log");
						InputStream io = new FileInputStream(log);
						byte[] result2 = new byte[io.available()];
						io.read(result2);
						System.out.println(new String(result2));
						io.close();
						log.delete();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}


	public static boolean uploadAppMod(String appName) {
		SshForImserver ssh = new SshForImserver();
		String file = LocalEnv.path + appName + "/target/"+appName+"/plugins/" + pluginName + ".jar";
		AntForImserver.build = LocalEnv.path + appName + "/build.xml";
		if (!AntForImserver.doAnt(pluginName)) {
			System.exit(-1);
		}
		
		if(upload_imserver_and_lib_core){
			String file2 =LocalEnv.path + appName + "/target/"+appName+"/lib/imserver.jar";
			 ssh.uploadFile(file2,upload_imserver_and_lib_core);
			 file2 =LocalEnv.path + appName + "/target/"+appName+"/lib/jiaxin_lib_core.jar";
			 ssh.uploadFile(file2,upload_imserver_and_lib_core);
		}
		ssh.uploadFile(file,false);
		ssh.disconnect();
		return true;
	}
}
