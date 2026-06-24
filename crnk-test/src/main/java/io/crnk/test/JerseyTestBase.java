package io.crnk.test;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.ServerSocket;

public class JerseyTestBase extends JerseyTest {

	@BeforeClass
	public static void selectPort() {
		// Retry to reduce TOCTOU race window when selecting a free port
		for (int attempt = 0; attempt < 10; attempt++) {
			try {
				ServerSocket s = new ServerSocket(0);
				int port = s.getLocalPort();
				s.close();
				// Verify the port is still free before committing to it
				ServerSocket verify = new ServerSocket(port);
				verify.close();
				System.setProperty("jersey.config.test.container.port", Integer.toString(port));
				return;
			} catch (IOException e) {
				// Port unavailable, retry with another port
			}
		}
		throw new IllegalStateException("Could not find a free port after 10 attempts");
	}
}
