package myRestIIQ4;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Priority;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.integration.AuthenticationUtil;
import sailpoint.object.Identity;
import sailpoint.object.OAuthClient;
import sailpoint.rest.AuthenticationFilter;
import sailpoint.rest.AuthenticationResult;
import sailpoint.rest.HttpSessionStorage;
import sailpoint.server.Auditor;
import sailpoint.service.LoginService;
import sailpoint.service.oauth.OAuthAccessToken;
import sailpoint.service.oauth.OAuthClientService;
import sailpoint.service.oauth.OAuthTokenExpiredException;
import sailpoint.service.oauth.OAuthTokenValidator;
import sailpoint.tools.GeneralException;
import sailpoint.web.LoginBean;


@Provider
@Priority(Priorities.AUTHENTICATION)
public class MyAuthenticationFilter extends AuthenticationFilter {

	private static final Logger logger = Logger.getLogger(MyAuthenticationFilter.class);
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain filter) throws IOException, ServletException {
		logger.info("Inside doFilter");
		logger.error("Inside doFilter");
	}
	public boolean isAuthRequest(HttpServletRequest httpRequest) {
		logger.info("Inside isAuthRequest");
		logger.error("Inside isAuthRequest");
		boolean isBearerAuth = false;

		// Instead of using getAuthHeader OOB method which takes httpRequest you need to
		// get it from your SOAP request and set it as a string

		String authHeader = getAuthHeader(httpRequest);
		isBearerAuth = AuthenticationUtil.isBearerAuth(authHeader);
		return isBearerAuth;
	}

	public Map<String, Object> bearerAuthenticate(HttpServletRequest httpRequest) {
		logger.info("Inside bearerAuthenticate");
		boolean success = false;
		Map<String, Object> result = new HashMap<String, Object>();
		AuthenticationResult.Reason reason = AuthenticationResult.Reason.UNSPECIFIED;

		result.put("success", success);

		// Instead of using getAuthHeader OOB method which takes httpRequest you need to
		// get it from your SOAP request and set it as a string
		String authHeader = getAuthHeader(httpRequest);
		OAuthAccessToken token = null;
		try {
			logger.info("Creating context for MyContext");
			SailPointContext ctx = SailPointFactory.createContext("MyContext"); // Create a new context if not available
																				// else use the available context

			OAuthTokenValidator validator = new OAuthTokenValidator(ctx);
			token = validator.authenticate(authHeader);

			if (null != token) {
				String proxyUser = token.getIdentityId();

				Identity user = ctx.getObjectByName(Identity.class, proxyUser);
				OAuthClientService oAuthClientSvc = new OAuthClientService(ctx);
				OAuthClient client = oAuthClientSvc.getClient(token.getClientId());
				logger.info("Proxy Identity: " + user + "Client name: " + client.getName());

				if (user != null) {
					result.put("success", true);
					ctx.setUserName(user.getName());
					HttpSession httpSession = httpRequest.getSession(true);
					LoginService.writeIdentitySession(new HttpSessionStorage(httpSession), user);
					Auditor.log("login", user.getName());
					result.put("apiClient", client.getName());
				} else {
					result.put("success", false);
					result.put("reason", "Unable to resolve proxy user");
				}

			}
		} catch (OAuthTokenExpiredException e) {
			reason = AuthenticationResult.Reason.OAUTH_TOKEN_EXPIRED;
			result.put("success", false);
			result.put("reason", reason.toString());
			logger.error("OAuth Token Expired: " + reason.toString());
		} catch (GeneralException localGeneralException) {

			result.put("success", false);
			result.put("reason", localGeneralException.getMessage());
			logger.error("Local general Exp occured: " + localGeneralException);
		} catch (GeneralSecurityException e) {
			result.put("success", false);
			result.put("reason", e.getMessage());
			logger.error("Unable to authenticate using Bearer Authentication: ", e);
		}
		return result;
	}
}
