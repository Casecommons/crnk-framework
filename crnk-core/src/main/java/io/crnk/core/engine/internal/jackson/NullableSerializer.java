package io.crnk.core.engine.internal.jackson;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import io.crnk.core.utils.Nullable;

public class NullableSerializer extends ValueSerializer<Nullable<Object>> {

	@Override
	public void serialize(Nullable<Object> value, JsonGenerator gen, SerializationContext ctxt) {
		if (value.isPresent()) {
			Object object = value.get();
			if (object == null) {
				gen.writeNull();
			} else {
				gen.writePOJO(object);
			}
		}
	}

	@Override
	public boolean isEmpty(SerializationContext ctxt, Nullable<Object> value) {
		return !value.isPresent();
	}
}
