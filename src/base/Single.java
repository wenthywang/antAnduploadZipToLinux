package base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import modules.Modules;
import modules.Modules.M;

import org.apache.commons.lang3.StringUtils;

import conf.LocalEnv;

public class Single {
	//选中应用
	public static M app = Modules.M.jiaxin_gw_ccaccess;
	//是否清除日志
	public static boolean clearLog = true;
	//是否仅重启[没有上传zip包]
	public static boolean onlyReboot = true;
	//是否仅重启[没有上传zip包]
	public static boolean doBuild = false;
	//[启动后等待X秒，取回启动日志]
	public static int seeRemoteLogAfterXSeconds = 0;
	
	public static void main(String[] args) {
		if(doBuild) {
			Ant.build = LocalEnv.path + app.toString() + "/build.xml";
			if(!Ant.doAnt()) {
				System.exit(-1);
			}
		}
		Ssh ssh = new Ssh();
		String file = LocalEnv.path + app.toString() + "/" + app.toString() + ".zip";
		boolean b = onlyReboot || ssh.uploadFile(file);
		if (!b) {
			System.out.println("Copy file fail...");
			System.exit(-1);
		}
		ssh.doit(clearLog, app.toString(), app.ordinal() + 1, onlyReboot);
		try {
			if(seeRemoteLogAfterXSeconds > 0) {
				if(StringUtils.isBlank(LocalEnv.tempLocalLog)) {
					System.out.println("no local log path exist :" + LocalEnv.tempLocalLog);
				} else {
					System.out.println("waiting for see result log of " + app.toString() + " ...");
					Thread.sleep(seeRemoteLogAfterXSeconds * 1000);
					ssh.getLog(app.toString());
					File log = new File(LocalEnv.tempLocalLog + "/" + app.toString() + ".out");
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
		};
	}
}
