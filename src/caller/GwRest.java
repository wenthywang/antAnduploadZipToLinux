package caller;

import base.Single;
import modules.Modules;

public class GwRest {

	public static void main(String[] args) {
		
		Single.app_74= Modules.M_74.jiaxin_gw_rest;
		Single.doBuild = true;
		Single.onlyReboot = false;
		Single.clearLog = true;
		Single.seeRemoteLogAfterXSeconds = 10;
		Single.main(args);
	}
}
