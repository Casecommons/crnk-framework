package io.crnk.gen.gradle;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import io.crnk.gen.gradle.task.InMemoryGeneratorTask;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class GeneratorPluginTest {

    
    @TempDir
    public File testProjectDir;

    private GeneratorExtension extension;

    private InMemoryGeneratorTask task;


    @BeforeEach
    public void setup() throws IOException {
    	GeneratorPlugin.APPLY_DOCLET_BY_DEFAULT = false;

        new File(testProjectDir, "src/main/java").mkdirs();

        File outputDir = testProjectDir;

        Project project = ProjectBuilder.builder().withName("crnk-gen-typescript-test").withProjectDir(outputDir).build();
        project.setVersion("0.0.1");

        project.getPluginManager().apply(JavaPlugin.class);
        project.getPluginManager().apply(GeneratorPlugin.class);

        extension = project.getExtensions().getByType(GeneratorExtension.class);
        extension.getRuntime().setConfiguration("test");
        extension.setResourcePackages(Arrays.asList("io.crnk.test.mock"));
        extension.setForked(false);
        extension.init();

        task = (InMemoryGeneratorTask) project.getTasks().getByName("generateTypescript");
        Assertions.assertNotNull(task);


    }

    @Test
    public void checkGenerate() throws IOException {
        task.generate();

        File genDir = new File(testProjectDir, "build/generated/sources/typescript");
        Assertions.assertTrue(genDir.exists());
        Assertions.assertTrue(new File(genDir, "projects.ts").exists());
    }
}
