/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package caller;

import java.util.ArrayList;
import java.util.List;

import base.Ssh;

/**
 * <pre>
 * 程序的中文名称。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * @date 2017年9月28日 
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class TestSsh {
	public static void main(String[] args) {
		Ssh ssh = new Ssh();
		
		doit(ssh);
	}
	
	public static void doit(Ssh ssh) {
		List<String> cmdList = getCmdList();
		String[] cmd = new String[cmdList.size()];
		cmdList.toArray(cmd);
		ssh.executeCommands2(cmd);
		String result=ssh.getResponse();
		String[] resultList=result.split("-------------------------------------------------------------------------");
		for (String s : resultList) {
		if(s.contains("jiaxin_gw_config")){
			System.out.println(s.split("root")[0].trim());
		}
		
		}
	
		ssh.disconnect();
	}
	
	public static List<String> getCmdList() {
		List<String> cmdList = new ArrayList<String>();
		cmdList.add("cd /usr/local/jiaxin_gw_container-1.0/");
		cmdList.add("./admin.sh");
		cmdList.add("1");
		return cmdList;
	}

}
