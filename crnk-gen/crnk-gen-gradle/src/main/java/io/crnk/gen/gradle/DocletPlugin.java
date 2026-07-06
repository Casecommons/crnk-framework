package io.crnk.gen.gradle;

import java.util.ArrayList;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.JavadocMemberLevel;
import org.gradle.external.javadoc.MinimalJavadocOptions;

/**
 * Sets up task to creata XML doclet. Used by Asciidoc to gain access to JavaDoc.
 */
public class DocletPlugin implements Plugin<Project> {

	static final String TASK_NAME = "crnkJavaDocToXml";

	@Override
	public void apply(final Project project) {
		Configuration config = project.getConfigurations().create("crnkJavaDocToXml");

		project.getDependencies().add("crnkJavaDocToXml", "com.github.markusbernhardt:xml-doclet:1.0.5");

		project.getTasks().register(TASK_NAME, Javadoc.class, task -> {
			task.setTitle(null);
			task.setDestinationDir(project.getLayout().getBuildDirectory().dir("crnk-xml-docs").get().getAsFile());

			MinimalJavadocOptions options = task.getOptions();
			options.setDoclet("com.github.markusbernhardt.xmldoclet.XmlDoclet");
			options.setMemberLevel(JavadocMemberLevel.PRIVATE);
			options.setDocletpath(new ArrayList<>(config.getFiles()));

			SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
			SourceSet mainSourceSet = sourceSets.getByName("main");
			task.source(mainSourceSet.getAllJava());
			task.setClasspath(mainSourceSet.getCompileClasspath());
		});
	}
}
