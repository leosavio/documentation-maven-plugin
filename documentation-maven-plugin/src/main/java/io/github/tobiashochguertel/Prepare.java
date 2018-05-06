package io.github.tobiashochguertel;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twdata.maven.mojoexecutor.MojoExecutor;
import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
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
		name = Goal.Constants.PREPARE,
		defaultPhase = LifecyclePhase.VERIFY,
		requiresDependencyResolution = ResolutionScope.TEST
)
public class Prepare extends Document {

	private final static Logger LOGGER = LoggerFactory.getLogger(Prepare.class);

	@Override
	protected void generate() throws MojoExecutionException {
		provideShellScripts();
		copyDocsResource();
		generateDrawioImages();
	}

	private void generateDrawioImages() throws MojoExecutionException {
		final MojoExecutor.Element executable = element("executable", SCRIPT_LOCATION + "/" + Documenttype.DRAWIO.getScriptname());
		final MojoExecutor.Element arguments = element("arguments", element("argument", this.drawioDocDir));
		execCommandOnShell(arguments, executable);
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
						                element("groupId", Plugin.GROUPID),
						                element("artifactId", Plugin.ARTIFACTID),
						                element("version", Plugin.VERSION),
						                element("type", Plugin.TYPE),
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

	private void copyDocsResource() throws MojoExecutionException {
		final Xpp3Dom configurationDocsResource = configuration(
				element("outputDirectory", this.docsOutputDirectory),
				element("resources",
				        element("resource",
				                element("directory", this.docsInputDirectory)))
		);
		copyResources(configurationDocsResource);
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

}
