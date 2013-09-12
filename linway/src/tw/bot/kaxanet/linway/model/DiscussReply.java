package tw.bot.kaxanet.linway.model;

public class DiscussReply {
	private String id;
	private String Avatar;
	private String Nickname;
	private String Gender;
	private String content;
	private String post_time;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAvatar() {
		return Avatar;
	}
	public void setAvatar(String avatar) {
		Avatar = avatar;
	}
	public String getNickname() {
		return Nickname;
	}
	public void setNickname(String nickname) {
		Nickname = nickname;
	}
	public String getGender() {
		return Gender;
	}
	public void setGender(String gender) {
		Gender = gender;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPost_time() {
		return post_time;
	}
	public void setPost_time(String post_time) {
		this.post_time = post_time;
	}
	@Override
	public String toString() {
		return "DiscussReply [id=" + id + ", Avatar=" + Avatar + ", Nickname="
				+ Nickname + ", Gender=" + Gender + ", content=" + content
				+ ", post_time=" + post_time + "]";
	}

}
