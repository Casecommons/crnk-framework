package io.crnk.core.engine.internal.jackson;

import tools.jackson.core.Version;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.module.SimpleModule;
import io.crnk.core.engine.document.ErrorData;
import io.crnk.core.module.Module;
import io.crnk.core.resource.links.LinksInformation;

public class JacksonModule implements Module {

	private static final String JSON_API_JACKSON_MODULE_NAME = "crnk";

	private final boolean serializeLinksAsObjects;

	public JacksonModule(ObjectMapper objectMapper, boolean serializeLinksAsObjects) {
		this.serializeLinksAsObjects = serializeLinksAsObjects;
	}

	@Override
	public String getModuleName() {
		return "jackson";
	}

	@Override
	public void setupModule(ModuleContext context) {
		context.addJacksonModule(createJacksonModule(serializeLinksAsObjects));
	}


	/**
	 * Creates Crnk Jackson module with all required serializers
	 *
	 * @return {@link tools.jackson.databind.JacksonModule} with custom serializers
	 */
	public static SimpleModule createJacksonModule() {
		return createJacksonModule(false);
	}

	/**
	 * Creates Crnk Jackson module with all required serializers.<br />
	 * Adds the {@link LinksInformationSerializer} if <code>serializeLinksAsObjects</code> is set to <code>true</code>.
	 *
	 * @param serializeLinksAsObjects flag which decides whether the {@link LinksInformationSerializer} should be added as
	 *                                additional serializer or not.
	 * @return {@link tools.jackson.databind.JacksonModule} with custom serializers
	 */
	public static SimpleModule createJacksonModule(boolean serializeLinksAsObjects) {
		SimpleModule simpleModule = new SimpleModule(JSON_API_JACKSON_MODULE_NAME,
				new Version(1, 0, 0, null, null, null));
		simpleModule.addSerializer(ErrorData.class, new ErrorDataSerializer());
		simpleModule.addDeserializer(ErrorData.class, new ErrorDataDeserializer());
		simpleModule.addSerializer(LinksInformation.class, new LinksInformationSerializer(serializeLinksAsObjects));

		return simpleModule;
	}
}
