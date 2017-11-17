package caller;

import base.Single;
import modules.Modules;

public class GwExam {

	public static void main(String[] args) {
		
		Single.app_74 = Modules.M_74.jiaxin_gw_exam;
		Single.doBuild = true;
		Single.onlyReboot = false;
		Single.clearLog = true;
		Single.seeRemoteLogAfterXSeconds = 10;
		Single.main(args);
	}
}
