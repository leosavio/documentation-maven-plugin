package io.github.tobiashochguertel;

enum Documenttype {
	PDF("generatePDF.sh"),
	HTML("generateHTML.sh"),
	DOCX("generateDocx.sh"),
	CONFLUENCE("publishToConfluence.sh"),
	DRAWIO("convertDrawIoImagesToPng.sh");

	private String scriptname;

	Documenttype(String scriptname) {
		this.scriptname = scriptname;
	}

	public String getScriptname() {
		return scriptname;
	}

	@Override
	public String toString() {
		return scriptname;
	}
}