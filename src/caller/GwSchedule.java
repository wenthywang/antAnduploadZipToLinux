package caller;

import modules.Modules;
import base.Single;

public class GwSchedule {

	public static void main(String[] args) {
		Single.app = Modules.M.jiaxin_gw_schedule;
		Single.doBuild = true;
		Single.onlyReboot = false;
		Single.clearLog = true;
		Single.seeRemoteLogAfterXSeconds = 10;
		Single.main(args);
	}
}
