package caller;

import base.Single;
import modules.Modules;
import modules.Modules.M_76;

public class RebootAll {

	/**
	 * 重启76所有的gw网关
	 * @param args
	 */
	public static void main(String[] args) {
		M_76[] all = Modules.M_76.values();
		for (int i = 0; i < all.length; i++) {
			Single.app_76 = all[i];
			Single.doBuild = false;
			Single.onlyReboot = true;
			Single.clearLog = false;
			Single.seeRemoteLogAfterXSeconds = 0;
			Single.main(args);
		}
	}
}
