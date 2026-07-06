package io.crnk.gen.asciidoc;

import io.crnk.core.boot.CrnkBoot;
import io.crnk.core.module.discovery.EmptyServiceDiscovery;
import io.crnk.gen.asciidoc.internal.AsciidocGeneratorModule;
import io.crnk.meta.MetaModule;
import io.crnk.meta.MetaModuleConfig;
import io.crnk.meta.provider.resource.ResourceMetaProvider;
import io.crnk.test.mock.TestModule;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class AsciidocGeneratorTest {


    private MetaModule metaModule;

    private AsciidocGeneratorModule module;

    @BeforeEach
    public void setup() throws IOException {
        File buildDir = new File("build/tmp/asciidoc");

        InputStream xmlDoc = getClass().getClassLoader().getResourceAsStream("javadoc.xml");
        FileUtils.copyInputStreamToFile(xmlDoc, new File(buildDir, "crnk-xml-docs/javadoc.xml"));

        MetaModuleConfig metaConfig = new MetaModuleConfig();
        metaConfig.addMetaProvider(new ResourceMetaProvider());
        metaModule = MetaModule.createServerModule(metaConfig);

        CrnkBoot boot = new CrnkBoot();
        boot.setServiceDiscovery(new EmptyServiceDiscovery());
        boot.addModule(metaModule);
        boot.addModule(new TestModule());
        boot.boot();

        module = new AsciidocGeneratorModule();
        module.getConfig().setBuildDir(buildDir);
        // Graphviz SVG rendering relies on the graphviz-java JS engines (Nashorn/V8) or a `dot`
        // binary, none of which are available on Java 21 in this environment. The graph is an
        // optional, separately-gated feature; disable it so the asciidoc generation can be verified.
        module.getConfig().setGraphEnabled(false);
        module.initDefaults(buildDir);
    }

    @Test
    public void test() throws IOException {
        module.generate(metaModule.getLookup());
    }
}
