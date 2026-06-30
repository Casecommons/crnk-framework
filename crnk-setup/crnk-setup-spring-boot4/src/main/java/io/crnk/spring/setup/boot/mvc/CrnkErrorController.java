package io.crnk.spring.setup.boot.mvc;

import tools.jackson.databind.ObjectMapper;
import io.crnk.core.engine.document.Document;
import io.crnk.core.engine.document.ErrorData;
import io.crnk.core.engine.document.ErrorDataBuilder;
import io.crnk.core.engine.http.HttpHeaders;
import io.crnk.core.engine.internal.jackson.JacksonModule;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.autoconfigure.error.BasicErrorController;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorViewResolver;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CrnkErrorController extends BasicErrorController {

	/**
	 * Crnk is built on Jackson 2 ({@code com.fasterxml.jackson}) and relies on Jackson 2 annotations such as
	 * {@code @JsonSerialize}/{@code @JsonInclude} on {@link Document} (e.g. to omit the empty {@code data}
	 * {@code Nullable}). Spring Boot 4's MVC layer serializes response bodies with Jackson 3
	 * ({@code tools.jackson}), which ignores those Jackson 2 annotations and would leak the {@code Nullable}
	 * internals (a stray {@code "present"} property) into the JSON:API document. We therefore serialize the
	 * document ourselves with a Jackson 2 mapper configured with crnk's module and return the rendered body.
	 */
	private final ObjectMapper objectMapper;

	public CrnkErrorController(ErrorAttributes errorAttributes,
							   ErrorProperties errorProperties) {
		super(errorAttributes, errorProperties);
		this.objectMapper = createObjectMapper();
	}

	public CrnkErrorController(ErrorAttributes errorAttributes,
							   ErrorProperties errorProperties,
							   List<ErrorViewResolver> errorViewResolvers) {
		super(errorAttributes, errorProperties, errorViewResolvers);
		this.objectMapper = createObjectMapper();
	}

	private static ObjectMapper createObjectMapper() {
		return tools.jackson.databind.json.JsonMapper.builder()
				.addModule(JacksonModule.createJacksonModule())
				.build();
	}

	// TODO for whatever reason this is not called directly
	@RequestMapping(produces = HttpHeaders.JSONAPI_CONTENT_TYPE)
	@ResponseBody
	public ResponseEntity<String> errorToJsonApi(HttpServletRequest request) {
		// Spring Boot >= 2.3 omits the "message" error attribute by default. crnk surfaces it as the
		// JSON:API error "detail", so request it explicitly to retain the historical behaviour (e.g. the
		// "No message available" placeholder for errors without an associated exception message).
		ErrorAttributeOptions options = getErrorAttributeOptions(request, MediaType.ALL)
				.including(ErrorAttributeOptions.Include.MESSAGE);
		Map<String, Object> body = getErrorAttributes(request, options);
		HttpStatus status = getStatus(request);

		ErrorDataBuilder errorDataBuilder = ErrorData.builder();
		for (Map.Entry<String, Object> attribute : body.entrySet()) {
			if (attribute.getKey().equals("status")) {
				errorDataBuilder.setStatus(attribute.getValue().toString());
			} else if (attribute.getKey().equals("error")) {
				errorDataBuilder.setTitle(attribute.getValue().toString());
			} else if (attribute.getKey().equals("message")) {
				errorDataBuilder.setDetail(attribute.getValue().toString());
			} else {
				errorDataBuilder.addMetaField(attribute.getKey(), attribute.getValue());
			}
		}
		Document document = new Document();
		document.setErrors(Arrays.asList(errorDataBuilder.build()));

		try {
			String json = objectMapper.writeValueAsString(document);
			return ResponseEntity.status(status)
					.contentType(MediaType.parseMediaType(HttpHeaders.JSONAPI_CONTENT_TYPE))
					.body(json);
		} catch (tools.jackson.core.JacksonException e) {
			throw new IllegalStateException("failed to serialize error document", e);
		}
	}


	@RequestMapping
	@ResponseBody
	public ResponseEntity error(HttpServletRequest request) {
		return errorToJsonApi(request);
	}
}
