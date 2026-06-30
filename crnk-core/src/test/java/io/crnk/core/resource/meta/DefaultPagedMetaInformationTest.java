package io.crnk.core.resource.meta;


import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultPagedMetaInformationTest {

	@Test
	public void nullMustNotBeSerialized() {
		ObjectMapper mapper = JsonMapper.builder().build();
		ObjectWriter writer = mapper.writerFor(DefaultPagedMetaInformation.class);

		DefaultPagedMetaInformation metaInformation = new DefaultPagedMetaInformation();
		String json = writer.writeValueAsString(metaInformation);
		Assertions.assertEquals("{}", json);
	}

	@Test
	public void nonNullMustBeSerialized() {
		ObjectMapper mapper = JsonMapper.builder().build();
		ObjectWriter writer = mapper.writerFor(DefaultPagedMetaInformation.class);

		DefaultPagedMetaInformation metaInformation = new DefaultPagedMetaInformation();
		metaInformation.setTotalResourceCount(12L);

		String json = writer.writeValueAsString(metaInformation);
		Assertions.assertEquals("{\"totalResourceCount\":12}", json);
	}
}
