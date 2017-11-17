package caller;

import base.SingleForImserver;
import modules.Modules;

public class Jiaxin_im_server {

	public static void main(String[] args) {
		
		SingleForImserver.app_76 = Modules.M_76.jiaxin_im_server;
		SingleForImserver.doBuild = true;
		SingleForImserver.onlyReboot = false;
		SingleForImserver.clearLog = true;
		SingleForImserver.seeRemoteLogAfterXSeconds = 20;
		/**
		 * 是否需要升级imserver.jar 和jixin_lib_core
		 * 若为false 则不升级
		 * 若为true 则升级两个jar
		 */
		SingleForImserver.upload_imserver_and_lib_core=true;
		/**
		 * 需要升级的插件name
		 * 如要升级fastpath 则填fastpath
		 */
		SingleForImserver.pluginName="fastpath";
		
		SingleForImserver.run();
	}
}
