package io.github.tobiashochguertel;

import java.util.List;
import java.util.Vector;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twdata.maven.mojoexecutor.MojoExecutor;
import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.dependencies;
import static org.twdata.maven.mojoexecutor.MojoExecutor.dependency;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

/**
 * Generates the document out of the different documentations.
 */
public abstract class Document extends AbstractMojo {

	static final String UNPACK_LOCATION = "${project.build.directory}/documentation-maven-plugin";
	private static final String SCRIPT_DIRECTORY = "sh";
	static final String SCRIPT_LOCATION = UNPACK_LOCATION + "/" + SCRIPT_DIRECTORY;
	private final static Logger LOGGER = LoggerFactory.getLogger(Document.class);
	private static final List<String> preparedDocs = new Vector<>();
	/**
	 * The docDir where the arc42 document is located.
	 */
	@Parameter(property = Plugin.Configuration.POMConfigurationParameter.DOC_DIR, defaultValue = Plugin.Configuration.Defaultvalue.DOC_DIR)
	protected String docDir;
	/**
	 * The docsOutputDirectory of the docs resources.
	 */
	@Parameter(property = Plugin.Configuration.POMConfigurationParameter.DOCS_OUTPUT_DIRECTORY, defaultValue = Plugin.Configuration.Defaultvalue.DOCS_OUTPUT_DIRECTORY)
	protected String docsOutputDirectory;
	/**
	 * The docsInputDirectory of the docs resources.
	 */
	@Parameter(property = Plugin.Configuration.POMConfigurationParameter.DOCS_INPUT_DIRECTORY, defaultValue = Plugin.Configuration.Defaultvalue.DOCS_INPUT_DIRECTORY)
	protected String docsInputDirectory;
	/**
	 * The drawioDocDir where arc42/drawio xml documents are located.
	 */
	@Parameter(property = Plugin.Configuration.POMConfigurationParameter.DRAWIO_DOC_DIR, defaultValue = Plugin.Configuration.Defaultvalue.DRAWIO_DOC_DIR)
	protected String drawioDocDir;
	/**
	 * The project currently being build.
	 */
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	MavenProject mavenProject;
	/**
	 * The current Maven session.
	 */
	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	MavenSession mavenSession;
	/**
	 * The Maven BuildPluginManager component.
	 */
	@Component
	BuildPluginManager pluginManager;
	/**
	 * The workingDirectory for this plugin.
	 */
	@Parameter(property = Plugin.Configuration.POMConfigurationParameter.WORKING_DIRECTORY, defaultValue = Plugin.Configuration.Defaultvalue.WORKING_DIRECTORY)
	private String workingDirectory;

	private boolean isPrepared() {
		return preparedDocs.contains(this.docsInputDirectory);
	}

	public void execute() throws MojoExecutionException {
		prepare();
		//		buildApiGuide();
		generate();
	}

	private void prepare() throws MojoExecutionException {
		if (!isPrepared()) {
			setPrepared();
			executeGoal(Goal.PREPARE);
		}
	}

	private void setPrepared() {
		preparedDocs.add(this.docsInputDirectory);
	}

	void executeGoal(Goal goal) throws MojoExecutionException {
		executeMojo(
				plugin(
						groupId(Plugin.GROUPID),
						artifactId(Plugin.ARTIFACTID),
						version(Plugin.VERSION)
				),
				goal(String.valueOf(goal)),
				configuration(
						element(Plugin.Configuration.DOC_DIR.identifier(), this.docDir),
						element(Plugin.Configuration.WORKING_DIRECTORY.identifier(), this.workingDirectory),
						element(Plugin.Configuration.DOCS_INPUT_DIRECTORY.identifier(), this.docsInputDirectory),
						element(Plugin.Configuration.DOCS_OUTPUT_DIRECTORY.identifier(), this.docsOutputDirectory),
						element(Plugin.Configuration.DRAWIO_DOC_DIR.identifier(), this.drawioDocDir)
				),
				executionEnvironment(
						mavenProject,
						mavenSession,
						pluginManager
				)
		);
	}

	protected abstract void generate() throws MojoExecutionException;

	private void buildApiGuide() throws MojoExecutionException {
		executeMojo(
				plugin(
						groupId("org.asciidoctor"),
						artifactId("asciidoctor-maven-plugin"),
						version("1.5.3"),
						dependencies(
								dependency(
										groupId("org.springframework.restdocs"),
										artifactId("spring-restdocs-asciidoctor"),
										version("2.0.2.BUILD-SNAPSHOT")
								)
						)
				),
				goal("process-asciidoc"),
				configuration(
						element("sourceDirectory", "${basedir}/src/main/docs/api-guide"),
						element("outputDirectory", "${basedir}/target/docs/api-guide/build"),
						element("backend", "html"),
						element("doctype", "book")
				),
				executionEnvironment(
						mavenProject,
						mavenSession,
						pluginManager
				)
		);
	}

	void generateDocumentationDocument(Documenttype type) throws MojoExecutionException {
		final MojoExecutor.Element executable = element("executable", SCRIPT_LOCATION + "/" + type.getScriptname());
		final MojoExecutor.Element arguments = element("arguments", element("argument", this.docDir));
		execCommandOnShell(arguments, executable);
	}

	void execCommandOnShell(MojoExecutor.Element arguments, MojoExecutor.Element executable) throws MojoExecutionException {
		final MojoExecutor.Element workingDirectory = element(Plugin.Configuration.WORKING_DIRECTORY.identifier(), this.workingDirectory);
		execCommandOnShell(arguments, executable, workingDirectory);
	}

	private void execCommandOnShell(MojoExecutor.Element arguments, MojoExecutor.Element executable, MojoExecutor.Element workingDirectory) throws MojoExecutionException {
		executeMojo(
				plugin(
						groupId("org.codehaus.mojo"),
						artifactId("exec-maven-plugin"),
						version("1.6.0")
				),
				goal("exec"),
				configuration(
						executable,
						workingDirectory,
						arguments
				),
				executionEnvironment(
						mavenProject,
						mavenSession,
						pluginManager
				)
		);
	}

	boolean sessionContains(Goal goal) {
		return mavenSession.getGoals().contains(Plugin.and(goal));
	}

}
