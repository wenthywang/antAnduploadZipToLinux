package caller;

import base.Single;
import modules.Modules;

public class GwBill {

	public static void main(String[] args) {
		Single.app_74 = Modules.M_74.jiaxin_gw_bill;
		Single.doBuild = true;
		Single.onlyReboot = false;
		Single.clearLog = true;
		Single.seeRemoteLogAfterXSeconds = 10;
		Single.main(args);
	}
}
