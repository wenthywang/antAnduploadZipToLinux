package caller;

import base.Single;
import modules.Modules;

public class GwStatistics {

	public static void main(String[] args) {
		Single.app_76 = Modules.M_76.jiaxin_gw_statistics;
		Single.doBuild = true;
		Single.onlyReboot = false;
		Single.clearLog = true;
		Single.seeRemoteLogAfterXSeconds = 10;
		Single.main(args);
	}
}
