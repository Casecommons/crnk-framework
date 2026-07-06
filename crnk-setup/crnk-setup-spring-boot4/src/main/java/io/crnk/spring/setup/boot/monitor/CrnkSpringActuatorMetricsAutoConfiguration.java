package io.crnk.spring.setup.boot.monitor;

import io.crnk.core.boot.CrnkBoot;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.observation.ServerRequestObservationConvention;

/**
 * Registers a custom {@link ServerRequestObservationConvention} so that Micrometer HTTP server request observations
 * (the Spring Boot 3+ replacement for the removed {@code WebMvcMetrics} / {@code WebMvcTagsProvider} infrastructure)
 * use proper Crnk resource {@code uri} tags.
 */
@AutoConfiguration
@ConditionalOnClass(ServerRequestObservationConvention.class)
@ConditionalOnProperty(prefix = "crnk.monitor.metrics", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CrnkSpringActuatorMetricsAutoConfiguration {

	@Bean
	CrnkServerRequestObservationConvention crnkServerRequestObservationConvention(CrnkBoot boot) {
		return new CrnkServerRequestObservationConvention(boot);
	}
}
