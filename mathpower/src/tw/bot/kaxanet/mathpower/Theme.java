package tw.bot.kaxanet.mathpower;

public class Theme {
	private String themeID;
	private String subjectID;
	private String degreeID;
	private String themeTit;
	public String getThemeID() {
		return themeID;
	}
	public void setThemeID(String themeID) {
		this.themeID = themeID;
	}
	public String getSubjectID() {
		return subjectID;
	}
	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}
	public String getDegreeID() {
		return degreeID;
	}
	public void setDegreeID(String degreeID) {
		this.degreeID = degreeID;
	}
	public String getThemeTit() {
		return themeTit;
	}
	public void setThemeTit(String themeTit) {
		this.themeTit = themeTit;
	}
	@Override
	public String toString() {
		return "Theme [themeID=" + themeID + ", subjectID=" + subjectID
				+ ", degreeID=" + degreeID + ", themeTit=" + themeTit + "]";
	}
}
