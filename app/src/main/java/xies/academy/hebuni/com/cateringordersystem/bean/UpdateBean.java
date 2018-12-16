package xies.academy.hebuni.com.cateringordersystem.bean;

public class UpdateBean {

	private String version;
	private String versionName;
	private String des;
	private String apkUrl;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getApkUrl() {
		return apkUrl;
	}

	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}

	@Override
	public String toString() {
		return "UpdateBean [version=" + version + ", versionName" + versionName + ", des=" + des + ", apkUrl="
				+ apkUrl + "]";
	}


	
	

}
