package tw.bot.kaxanet.linway.model;

public class DiscussContent {
	private String DisID;
	private String Mbid;
	private String MbNickname;
	private String MbAvatar;
	private String MbGender;
	private String Title;
	private String Content;
	private String DisEnb;
	private String DisRpyEnb;
	private String PostTime;

	public String getDisID() {
		return DisID;
	}

	public void setDisID(String disID) {
		DisID = disID;
	}

	public String getMbid() {
		return Mbid;
	}

	public void setMbid(String mbid) {
		Mbid = mbid;
	}

	public String getMbNickname() {
		return MbNickname;
	}

	public void setMbNickname(String mbNickname) {
		MbNickname = mbNickname;
	}

	public String getMbAvatar() {
		return MbAvatar;
	}

	public void setMbAvatar(String mbAvatar) {
		MbAvatar = mbAvatar;
	}

	public String getMbGender() {
		return MbGender;
	}

	public void setMbGender(String mbGender) {
		MbGender = mbGender;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public String getDisEnb() {
		return DisEnb;
	}

	public void setDisEnb(String disEnb) {
		DisEnb = disEnb;
	}

	public String getDisRpyEnb() {
		return DisRpyEnb;
	}

	public void setDisRpyEnb(String disRpyEnb) {
		DisRpyEnb = disRpyEnb;
	}

	public String getPostTime() {
		return PostTime;
	}

	public void setPostTime(String postTime) {
		PostTime = postTime;
	}

	@Override
	public String toString() {
		return "DiscussContent [DisID=" + DisID + ", Mbid=" + Mbid
				+ ", MbNickname=" + MbNickname + ", MbAvatar=" + MbAvatar
				+ ", MbGender=" + MbGender + ", Title=" + Title + ", Content="
				+ Content + ", DisEnb=" + DisEnb + ", DisRpyEnb=" + DisRpyEnb
				+ ", PostTime=" + PostTime + "]";
	}

}
