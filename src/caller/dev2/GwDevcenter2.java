package caller.dev2;

import base.Single;
import modules.Modules;

public class GwDevcenter2 {

	public static void main(String[] args) {
		Single.app_63 = Modules.M_70_63.jiaxin_web_devcenter;
		Single.doBuild = true;
		Single.onlyReboot = false;
		Single.clearLog = true;
		Single.seeRemoteLogAfterXSeconds = 10;
		Single.main(args);
	}
}