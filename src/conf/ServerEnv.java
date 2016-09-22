package conf;

public class ServerEnv {
	String ip = "172.16.54.76";
	int port = 22;
	String user = "root";
	String password = "suntek";

	String dest = "/usr/local/jiaxin_gw_container-1.0";
	String destMod = dest + "/mod";
	String destLog = dest + "/log";

	public String getDestLog() {
		return destLog;
	}

	public void setDestLog(String destLog) {
		this.destLog = destLog;
	}

	public String getDestMod() {
		return destMod;
	}

	public void setDestMod(String destMod) {
		this.destMod = destMod;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
