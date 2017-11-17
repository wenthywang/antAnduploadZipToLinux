package base;

import java.io.File;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;

public class AntForImserver {
	public static String build = "";

	public static boolean doAnt(String pluginName) {
		if (!StringUtils.isEmpty(build)) {
			File buildFile = new File(build);
			return exeBuildFile(buildFile, 3,pluginName);
		} else {
			System.out.println("build.xml文件不存在");
		}
		return false;
	}

	public static boolean exeBuildFile(File buildFile, int level,String pluginName) {
		Project p = new Project();
		// 添加日志输出
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		// 输出信息级别
		consoleLogger.setMessageOutputLevel(level);
		p.addBuildListener(consoleLogger);
		p.setProperty("ant.home", System.getProperty("user.dir"));
		p.setProperty("ant.java.version", "1.8");
		p.setProperty("ant.version", "1.10");
		try {
			p.fireBuildStarted();
			//p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			helper.parse(p, buildFile);
			Vector<String>targets=new Vector<String>(2);
			targets.add(p.getDefaultTarget());
			if(StringUtils.isNotEmpty(pluginName)){
				Target t=p.getTargets().get("plugin.one");
				t.getProject().setProperty("plugin", pluginName);
				targets.add("plugin.one");
			}	
			p.executeTargets(targets);
			p.fireBuildFinished(null);
			return true;
		} catch (BuildException e) {
			p.fireBuildFinished(e);
		}
		return false;
	}
	
	public static void main(String[] args) {
		File f=new File("D:\\project\\git\\jiaxin_im_server\\build.xml");
		exeBuildFile(f, 3,"robotgw");
	}
}
