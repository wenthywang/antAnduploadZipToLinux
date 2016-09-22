package base;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class Ant {
	public static String build = "";

	public static boolean doAnt() {
		if (!StringUtils.isEmpty(build)) {
			File buildFile = new File(build);
			return exeBuildFile(buildFile, 3);
		} else {
			System.out.println("build.xml文件不存在");
		}
		return false;
	}

	public static boolean exeBuildFile(File buildFile, int level) {
		Project p = new Project();
		// 添加日志输出
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		// 输出信息级别
		consoleLogger.setMessageOutputLevel(level);
		p.addBuildListener(consoleLogger);
		try {
			p.fireBuildStarted();
			//p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			helper.parse(p, buildFile);
			p.executeTarget(p.getDefaultTarget());
			p.fireBuildFinished(null);
			return true;
		} catch (BuildException e) {
			p.fireBuildFinished(e);
		}
		return false;
	}
}
