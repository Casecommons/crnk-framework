package io.crnk.security;

import org.eclipse.jetty.security.Constraint;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.util.security.Credential;

/**
 * A simple identity manager implementation, that just takes a map of users to their password.
 * <p>
 * This is in now way suitable for real world production use.
 * <p>
 * Migrated from the removed Jetty 9 {@code ConstraintSecurityHandler}/{@code ConstraintMapping}/{@code Constraint}
 * API to the Jetty 12 model: {@link SecurityHandler.PathMapped} mapping path specs directly to {@link Constraint}
 * instances built via {@link Constraint.Builder}.
 */
public class InMemoryIdentityManager {

	private static final String PATH_SPEC = "/*";

	private final SecurityHandler.PathMapped securityHandler;

	private final HashLoginService loginService;

	private UserStore userStore;

	private final String realm = "myrealm";

	public InMemoryIdentityManager() {
		loginService = new HashLoginService(realm);
		// Always provide an explicit (initially empty) UserStore. Otherwise Jetty 12's
		// HashLoginService.doStart() auto-creates a PropertyUserStore backed by a (null) config
		// file, which fails at server start with "No config path set" because users are added
		// later, inside the test methods, after the server has already been started.
		userStore = new UserStore();
		loginService.setUserStore(userStore);

		securityHandler = new SecurityHandler.PathMapped();
		securityHandler.setAuthenticator(new BasicAuthenticator());
		securityHandler.setRealmName(realm);
		securityHandler.setLoginService(loginService);

		applyConstraint();
	}

	private void applyConstraint() {
		// Require any authenticated user. The fine-grained, role-based authorization is performed by the
		// crnk SecurityModule itself (see SecurityModuleIntTest), so Jetty only needs to enforce that a
		// caller has authenticated. This matches the Jetty 9 BASIC constraint which used authenticate=true
		// (rather than restricting to specific roles, which would 403 legitimate role-less callers that
		// crnk's permitAll(...) rules are meant to allow).
		Constraint constraint = new Constraint.Builder()
				.name("BASIC")
				.authorization(Constraint.Authorization.ANY_USER)
				.build();
		securityHandler.put(PATH_SPEC, constraint);
	}

	public void addUser(String userId, String password, String... roles) {
		// Add into the existing (already started) UserStore so that users registered from within a
		// test method, after the Jetty server has started, take effect immediately.
		userStore.addUser(userId, Credential.getCredential(password), roles);
	}

	public void clear() {
		securityHandler.remove(org.eclipse.jetty.http.pathmap.PathSpec.from(PATH_SPEC));
		applyConstraint();
	}

	public SecurityHandler getSecurityHandler() {
		return securityHandler;
	}
}
