package io.crnk.spring.app;

import tools.jackson.databind.ObjectMapper;
import io.crnk.core.boot.CrnkBoot;
import io.crnk.core.boot.CrnkProperties;
import io.crnk.core.engine.properties.PropertiesProvider;
import io.crnk.core.engine.url.ConstantServiceUrlProvider;
import io.crnk.core.queryspec.mapper.DefaultQuerySpecUrlMapper;
import io.crnk.spring.internal.SpringServiceDiscovery;
import io.crnk.spring.setup.boot.core.CrnkCoreAutoConfiguration;
import io.crnk.spring.setup.boot.core.CrnkCoreProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

public class CrnkCoreAutoConfigurationTest {


    @Test
    public void checkProperties() {
        ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
        Mockito.when(applicationContext.getEnvironment()).thenReturn(Mockito.mock(Environment.class));

        CrnkCoreProperties properties = new CrnkCoreProperties();
        properties.setDomainName("testDomain");
        properties.setDefaultPageLimit(12L);
        properties.setMaxPageLimit(20L);
        properties.setPathPrefix("/prefix");
        properties.setAllowUnknownAttributes(true);
        properties.setReturn404OnNull(true);

        ObjectMapper objectMapper = new ObjectMapper();

        CrnkCoreAutoConfiguration config = new CrnkCoreAutoConfiguration(properties, objectMapper);
        config.setApplicationContext(applicationContext);

        CrnkBoot boot = config.crnkBoot();
        boot.boot();

        PropertiesProvider propertiesProvider = boot.getPropertiesProvider();
        Assertions.assertEquals("testDomain", propertiesProvider.getProperty(CrnkProperties.RESOURCE_DEFAULT_DOMAIN));
        Assertions.assertEquals("/prefix", propertiesProvider.getProperty(CrnkProperties.WEB_PATH_PREFIX));
        Assertions.assertEquals("true", propertiesProvider.getProperty(CrnkProperties.ALLOW_UNKNOWN_ATTRIBUTES));
        Assertions.assertEquals("true", propertiesProvider.getProperty(CrnkProperties.RETURN_404_ON_NULL));

        DefaultQuerySpecUrlMapper deserializer = (DefaultQuerySpecUrlMapper) boot.getUrlMapper();
        Assertions.assertTrue(deserializer.getAllowUnknownAttributes());

        ConstantServiceUrlProvider constantServiceUrlProvider = (ConstantServiceUrlProvider) boot.getServiceUrlProvider();
        Assertions.assertEquals("testDomain/prefix", constantServiceUrlProvider.getUrl());

        // In Jackson 3, ObjectMapper is immutable, so CrnkBoot rebuilds it with
        // additional configuration. We verify it's not null instead of identity check.
        Assertions.assertNotNull(boot.getObjectMapper());

        Assertions.assertNotNull(boot.getModuleRegistry().getSecurityProvider());
    }
}
