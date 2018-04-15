package io.github.tobiashochguertel;

final class Plugin {

	static final String NAME = "documentation";
	static final String GROUPID = "io.github.tobiashochguertel";
	static final String ARTIFACTID = "documentation-maven-plugin";
	static final String VERSION = "0.0.5-SNAPSHOT";
	static final String TYPE = "maven-plugin";

	static String and(Goal goal) {
		return new StringBuffer().append(NAME).append(":").append(goal).toString();
	}

	enum Configuration {
		DOC_DIR(ConfigurationConstants.DOC_DIR, DefaultvalueConstants.DOC_DIR),
		WORKING_DIRECTORY(ConfigurationConstants.WORKING_DIRECTORY, DefaultvalueConstants.WORKING_DIRECTORY),
		DOCS_OUTPUT_DIRECTORY(ConfigurationConstants.DOCS_OUTPUT_DIRECTORY, DefaultvalueConstants.DOCS_OUTPUT_DIRECTORY),
		DOCS_INPUT_DIRECTORY(ConfigurationConstants.DOCS_INPUT_DIRECTORY, DefaultvalueConstants.DOCS_INPUT_DIRECTORY);

		private final String identifier;
		private final String defaultvalue;

		Configuration(String identifier, String defaultvalue) {
			this.identifier = identifier;
			this.defaultvalue = defaultvalue;
		}

		public String identifier() {
			return identifier;
		}

		public String defaultvalue() {
			return defaultvalue;
		}

		static class ConfigurationConstants {

			static final String DOC_DIR = "docDir";
			static final String WORKING_DIRECTORY = "workingDirectory";
			static final String DOCS_OUTPUT_DIRECTORY = "docsOutputDirectory";
			static final String DOCS_INPUT_DIRECTORY = "docsInputDirectory";
		}

		static class DefaultvalueConstants {

			static final String DOC_DIR = "${basedir}/target/docs/arc42";
			static final String DOCS_OUTPUT_DIRECTORY = "${basedir}/target/docs/arc42";
			static final String DOCS_INPUT_DIRECTORY = "${basedir}/src/main/docs/arc42/";
			static final String WORKING_DIRECTORY = "${project.basedir}";

		}

		static class ParameterConstants {

			static final String DOC_DIR = "document.docDir";
			static final String WORKING_DIRECTORY = "document.workingDirectory";
			static final String DOCS_OUTPUT_DIRECTORY = "document.docsOutputDirectory";
			static final String DOCS_INPUT_DIRECTORY = "document.docsInputDirectory";
		}
	}

}
