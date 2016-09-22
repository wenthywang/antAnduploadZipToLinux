package caller;

import modules.Modules;
import modules.Modules.M;
import base.Single;

public class RebootAll {

	/**
	 * 重启所有的gw网关
	 * @param args
	 */
	public static void main(String[] args) {
		M[] all = Modules.M.values();
		for (int i = 0; i < all.length; i++) {
			Single.app = all[i];
			Single.doBuild = false;
			Single.onlyReboot = true;
			Single.clearLog = false;
			Single.seeRemoteLogAfterXSeconds = 0;
			Single.main(args);
		}
	}
}
