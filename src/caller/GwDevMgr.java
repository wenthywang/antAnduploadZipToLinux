package caller;

import base.Single;
import modules.Modules;

public class GwDevMgr {

	public static void main(String[] args) {
		Single.app_76 = Modules.M_76.jiaxin_web_devmgr;
		Single.doBuild = true;
		Single.onlyReboot = false;
		Single.clearLog = true;
		Single.seeRemoteLogAfterXSeconds = 10;
		Single.main(args);
	}
}
