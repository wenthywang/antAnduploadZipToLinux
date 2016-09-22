package caller;

import modules.Modules;
import base.Single;

public class GwStatistics {

	public static void main(String[] args) {
		Single.app = Modules.M.jiaxin_gw_statistics;
		Single.doBuild = true;
		Single.onlyReboot = false;
		Single.clearLog = true;
		Single.seeRemoteLogAfterXSeconds = 10;
		Single.main(args);
	}
}
