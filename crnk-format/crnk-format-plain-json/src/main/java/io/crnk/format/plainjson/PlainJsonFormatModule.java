package io.crnk.format.plainjson;

import tools.jackson.core.Version;
import tools.jackson.databind.module.SimpleModule;
import io.crnk.core.module.InitializingModule;
import io.crnk.format.plainjson.internal.PlainJsonDocument;
import io.crnk.format.plainjson.internal.PlainJsonDocumentDeserializer;
import io.crnk.format.plainjson.internal.PlainJsonDocumentSerializer;
import io.crnk.format.plainjson.internal.PlainJsonRequestProcessor;

/**
 * Support for a simplified JSON format does includes relationships directly rather than through a normalized include section and does also not
 * have wrapper elements like &qout;attributes&qout; and &qout;relationships&qout;.
 */
public class PlainJsonFormatModule implements InitializingModule {

    private ModuleContext context;

    @Override
    public String getModuleName() {
        return "plain-json";
    }

    @Override
    public void setupModule(ModuleContext context) {
        this.context = context;
        context.addHttpRequestProcessor(
                new PlainJsonRequestProcessor(context));

        SimpleModule jacksonModule = new SimpleModule("plain-json", new Version(1, 0, 0, null, null, null));
        jacksonModule.addSerializer(PlainJsonDocument.class, new PlainJsonDocumentSerializer());
        jacksonModule.addDeserializer(PlainJsonDocument.class, new PlainJsonDocumentDeserializer());
        context.addJacksonModule(jacksonModule);
    }

    @Override
    public void init() {
        // Jackson module registration moved to setupModule() for Jackson 3 compatibility
        // (ObjectMapper is immutable in Jackson 3, modules must be registered before build)
    }
}
