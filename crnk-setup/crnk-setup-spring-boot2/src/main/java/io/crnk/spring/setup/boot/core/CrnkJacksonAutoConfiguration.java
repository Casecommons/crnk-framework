package io.crnk.spring.setup.boot.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot 4 auto-configures a Jackson 3 ({@code tools.jackson}) {@code ObjectMapper}, while crnk is
 * built on Jackson 2 ({@code com.fasterxml.jackson}). This provides a Jackson 2 {@link ObjectMapper}
 * bean for crnk when the application has not defined one itself.
 */
@AutoConfiguration
public class CrnkJacksonAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(ObjectMapper.class)
	public ObjectMapper crnkObjectMapper() {
		return new ObjectMapper();
	}
}
