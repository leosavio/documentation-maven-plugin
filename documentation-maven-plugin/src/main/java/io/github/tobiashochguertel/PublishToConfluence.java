package io.github.tobiashochguertel;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates the document out of the different documentations.
 */
@Mojo(
		name = Goal.Constants.PUBLISH_TO_CONFLUENCE,
		defaultPhase = LifecyclePhase.VERIFY,
		requiresDependencyResolution = ResolutionScope.TEST
)
public class PublishToConfluence extends Document {

	private final static Logger LOGGER = LoggerFactory.getLogger(PublishToConfluence.class);

	@Override
	public void generate() throws MojoExecutionException {
		generateHTML();
		generateDocumentationDocument(Documenttype.CONFLUENCE);
	}

	private void generateHTML() throws MojoExecutionException {
		if (!sessionContains(Goal.HTML)) {
			executeGoal(Goal.HTML);
		}
	}
}
