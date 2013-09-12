package tw.bot.kaxanet.linway.model;

public class DiscussListItem {
	private String id;
	private String theme_id;
	private String nickname;
	private String title;
	private String post_time;
	private String ReplyCount;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTheme_id() {
		return theme_id;
	}
	public void setTheme_id(String theme_id) {
		this.theme_id = theme_id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPost_time() {
		return post_time;
	}
	public void setPost_time(String post_time) {
		this.post_time = post_time;
	}
	

	public String getReplyCount() {
		return ReplyCount;
	}
	public void setReplyCount(String replyCount) {
		this.ReplyCount = replyCount;
	}
	@Override
	public String toString() {
		return "DiscussListItem [id=" + id + ", theme_id=" + theme_id
				+ ", nickname=" + nickname + ", title=" + title
				+ ", post_time=" + post_time 
				+ "]";
	}
	
}
