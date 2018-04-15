package io.github.tobiashochguertel;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
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
@Mojo(
		name = "document",
		defaultPhase = LifecyclePhase.VERIFY,
		requiresDependencyResolution = ResolutionScope.TEST
)
public class Document extends AbstractMojo {

	private static final String UNPACK_LOCATION = "${project.build.directory}/documentation-maven-plugin";
	private static final String SCRIPT_DIRECTORY = "sh";
	private static final String SCRIPT_LOCATION = UNPACK_LOCATION + "/" + SCRIPT_DIRECTORY;

	private final static Logger LOGGER = LoggerFactory.getLogger(Document.class);
	@Component
	private MavenProject mavenProject;
	@Component
	private MavenSession mavenSession;
	@Component
	private BuildPluginManager pluginManager;
	/**
	 * The docDir where the arc42 document is located.
	 */
	@Parameter(property = "document.docDir", defaultValue = "${basedir}/target/docs/arc42")
	private String docDir;
	/**
	 * The workingDirectory for this plugin.
	 */
	@Parameter(property = "document.workingDirectory", defaultValue = "${project.basedir}")
	private String workingDirectory;

	@Override
	public void execute() throws MojoExecutionException {
		provideShellScripts();
		copyDocsApiGuideResource();
		copyDocsArc42Resource();
		buildApiGuide();
		generateArc42Document();
	}

	private void generateArc42Document() throws MojoExecutionException {
		generateHTML();
		generatePDF();
		generateDocx();
		publishToConfluence();
	}

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

	private void copyDocsArc42Resource() throws MojoExecutionException {
		final Xpp3Dom configurationDocsArc42Resource = configuration(
				element("outputDirectory", "${basedir}/target/docs/arc42"),
				element("resources",
				        element("resource",
				                element("directory", "${basedir}/src/main/docs/arc42/")))
		);
		copyResources(configurationDocsArc42Resource);
	}

	private void copyDocsApiGuideResource() throws MojoExecutionException {
		final Xpp3Dom configurationDocsApiGuideResource = configuration(
				element("outputDirectory", "${basedir}/target/docs/api-guide"),
				element("resources",
				        element("resource",
				                element("directory", "${basedir}/src/main/docs/api-guide/")))
		);
		copyResources(configurationDocsApiGuideResource);
	}

	private void copyResources(Xpp3Dom configuration) throws MojoExecutionException {
		executeMojo(
				plugin(
						groupId("org.apache.maven.plugins"),
						artifactId("maven-resources-plugin"),
						version("2.7")
				),
				goal("copy-resources"),
				configuration,
				executionEnvironment(
						mavenProject,
						mavenSession,
						pluginManager
				)
		);
	}

	private void provideShellScripts() throws MojoExecutionException {
		unpackShellScripts();
		fixScriptPermissions();
	}

	private void unpackShellScripts() throws MojoExecutionException {
		executeMojo(
				plugin(
						groupId("org.apache.maven.plugins"),
						artifactId("maven-dependency-plugin"),
						version("3.1.0")
				),
				goal("unpack"),
				configuration(
						element("artifactItems",
						        element("artifactItem",
						                element("groupId", "io.github.tobiashochguertel"),
						                element("artifactId", "documentation-maven-plugin"),
						                element("version", "0.0.3-SNAPSHOT"),
						                element("type", "maven-plugin"),
						                element("outputDirectory", UNPACK_LOCATION)
						        )
						)
				),
				executionEnvironment(
						mavenProject,
						mavenSession,
						pluginManager
				)
		);
	}

	private void generateDocumentationDocument(Documenttype type) throws MojoExecutionException {
		final MojoExecutor.Element executable = element("executable", SCRIPT_LOCATION + "/" + type.getScriptname());
		final MojoExecutor.Element arguments = element("arguments",
		                                               element("argument", this.docDir)
		);
		execCommandOnShell(arguments, executable);
	}

	private void generateHTML() throws MojoExecutionException {
		generateDocumentationDocument(Documenttype.HTML);
	}

	private void generateDocx() throws MojoExecutionException {
		generateDocumentationDocument(Documenttype.DOCX);
	}

	private void generatePDF() throws MojoExecutionException {
		generateDocumentationDocument(Documenttype.PDF);
	}

	private void publishToConfluence() throws MojoExecutionException {
		generateDocumentationDocument(Documenttype.CONFLUENCE);
	}

	private void execCommandOnShell(MojoExecutor.Element arguments, MojoExecutor.Element executable) throws MojoExecutionException {
		final MojoExecutor.Element workingDirectory = element("workingDirectory", this.workingDirectory);
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

	private void fixScriptPermissions() throws MojoExecutionException {
		final MojoExecutor.Element arguments = element("arguments",
		                                               element("argument", "-c"),
		                                               element("argument", "set -x\n"
				                                               + "SRC=\"" + SCRIPT_LOCATION + "/*\"\n"
				                                               + "chmod +x $SRC")
		);
		final MojoExecutor.Element executable = element("executable", "/bin/sh");
		execCommandOnShell(arguments, executable);
	}

	enum Documenttype {
		PDF("generatePDF.sh"), HTML("generateHTML.sh"), DOCX("generateDocx.sh"), CONFLUENCE("publishToConfluence.sh");

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
}
