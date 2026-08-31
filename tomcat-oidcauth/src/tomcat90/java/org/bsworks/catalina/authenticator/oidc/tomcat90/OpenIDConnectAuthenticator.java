package org.bsworks.catalina.authenticator.oidc.tomcat90;

import java.io.IOException;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.apache.catalina.util.ServerInfo;
import org.bsworks.catalina.authenticator.oidc.BaseOpenIDConnectAuthenticator;


/**
 * <em>OpenID Connect</em> authenticator implementation for <em>Tomcat 9.0</em>.
 *
 * @author Lev Himmelfarb
 */
public class OpenIDConnectAuthenticator
	extends BaseOpenIDConnectAuthenticator {

	@Override
	protected void ensureTomcatVersion()
		throws LifecycleException {

		final Integer[] versionParts = Stream.of(ServerInfo.getServerNumber().split("\\."))
			.map(v -> Integer.parseInt(v))
			.toArray(Integer[]::new);
		if ((versionParts[0].intValue() != 9)
				|| (versionParts[1].intValue() != 0)
				|| (versionParts[2].intValue() < 30))
			throw new LifecycleException("OpenIDConnectAuthenticator requires"
				+ " Apache Tomcat 9.0 version 9.0.30 or higher.");
	}

	@Override
	protected boolean restoreOriginalRequest(final Request request,
			final Session session)
		throws IOException {

		return this.restoreRequest(request, session).getAuthenticated();
	}

	@Override
	protected boolean doAuthenticate(final Request request,
			final HttpServletResponse response)
		throws IOException {

		return this.performAuthentication(request, response);
	}

	/**
	 * Extended authentication entry point. Overridden because Tomcat 9.0.30+
	 * calls {@code doAuthenticateExtended()} from
	 * {@link org.apache.catalina.authenticator.AuthenticatorBase#invoke} and
	 * {@code FormAuthenticator} overrides it with its own form-only logic that
	 * only recognizes {@link org.apache.catalina.authenticator.Constants#FORM_ACTION}
	 * ({@code /j_security_check}) as an authentication submission. Without this
	 * override, the OpenID Connect callback URL is treated as an ordinary
	 * unauthenticated request: the user is redirected to the login page (which
	 * redirects to the OP), and the browser ends up in an endless redirect loop
	 * (ERR_TOO_MANY_REDIRECTS). Route to the OpenID Connect authentication
	 * logic instead.
	 *
	 * @param request The request.
	 * @param response The response.
	 *
	 * @return Authentication result.
	 *
	 * @throws IOException If an I/O error happens.
	 */
	@Override
	protected AuthenticationResult doAuthenticateExtended(final Request request,
			final HttpServletResponse response)
		throws IOException {

		if (this.performAuthentication(request, response))
			return AuthenticationResult.PASSED;

		return AuthenticationResult.FAILED;
	}
}
