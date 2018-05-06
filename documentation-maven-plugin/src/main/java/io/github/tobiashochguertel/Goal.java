package io.github.tobiashochguertel;

public enum Goal {
	PDF(Constants.PDFDOCUMENT),
	HTML(Constants.HTMLDOCUMENT),
	DOCX(Constants.DOCXDOCUMENT),
	CONFLUENCE(Constants.PUBLISH_TO_CONFLUENCE),
	PREPARE(Constants.PREPARE),
	CONVERT_DRAWIO(Constants.CONVERT_DRAWIO);

	public final String goal;

	Goal(String goal) {
		this.goal = goal;
	}

	public String getGoal() {
		return goal;
	}

	@Override
	public String toString() {
		return getGoal();
	}

	public static class Constants {

		static final String PDFDOCUMENT = "pdfdocument";
		static final String HTMLDOCUMENT = "htmldocument";
		static final String DOCXDOCUMENT = "docxdocument";
		static final String PUBLISH_TO_CONFLUENCE = "publishtoconfluence";
		static final String PREPARE = "prepare";
		static final String CONVERT_DRAWIO = "convertdrawio";
	}
}
