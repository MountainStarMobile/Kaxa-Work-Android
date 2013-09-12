package tw.bot.kaxanet.linway.model;

import java.util.List;

public class InsidelistItem {
	private String id;
	private String title;
	private String status;
	private List<InsideSubTitleListItem> subTitleList;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<InsideSubTitleListItem> getSubTitleList() {
		return subTitleList;
	}
	public void setSubTitleList(List<InsideSubTitleListItem> subTitleList) {
		this.subTitleList = subTitleList;
	}

	
}
