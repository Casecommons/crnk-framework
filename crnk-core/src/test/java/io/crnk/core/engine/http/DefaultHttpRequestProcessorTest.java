package io.crnk.core.engine.http;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultHttpRequestProcessorTest {

	private HttpRequestProcessor processor = new HttpRequestProcessor() {
		@Override
		public boolean supportsAsync() {
			return false;
		}
	};

	@Test
	public void test() {
	    assertThrows(UnsupportedOperationException.class, () -> {
    		processor.accepts(null);
	    });
	}


	@Test
	public void processAsync() {
	    assertThrows(UnsupportedOperationException.class, () -> {
    		processor.processAsync(null);
	    });
	}
}
