package tw.bot.kaxanet.linway.model;


public class Account {
	private String email;
	private String passwd;
	private String userid;
	private String pushid;
	private String mailValidate;
	private String accountLock;
	private String permission;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPushid() {
		return pushid;
	}
	public void setPushid(String pushid) {
		this.pushid = pushid;
	}
	public String getMailValidate() {
		return mailValidate;
	}
	public void setMailValidate(String mailValidate) {
		this.mailValidate = mailValidate;
	}
	public String getAccountLock() {
		return accountLock;
	}
	public void setAccountLock(String accountLock) {
		this.accountLock = accountLock;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	@Override
	public String toString() {
		return "Account [email=" + email + ", passwd=" + passwd + ", userid="
				+ userid + ", pushid=" + pushid + ", mailValidate="
				+ mailValidate + ", accountLock=" + accountLock
				+ ", permission=" + permission + "]";
	}
	
	/**
	 * 可不可以查看即發表文章
	 * @return
	 */
	public boolean isAccountCanPost(){
		if (mailValidate.equals("y") && accountLock.equals("y")){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 可不可以貼圖
	 * @return
	 */
	public boolean isAccountCanPostPic(){
		if (permission.equals("g")){
			return false;
		}else{
			return true;
		}
	}	

}
