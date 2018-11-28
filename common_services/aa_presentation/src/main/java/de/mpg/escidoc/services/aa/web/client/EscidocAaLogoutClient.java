package de.mpg.escidoc.services.aa.web.client;

import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.aa.Aa;
import de.mpg.escidoc.services.aa.AuthenticationVO;
import de.mpg.escidoc.services.aa.Config;
import de.mpg.escidoc.services.framework.AdminHelper;

/**
 * 
 * @author haarlaender
 *
 */
public class EscidocAaLogoutClient extends LogoutClient {
	
	private static final Logger logger = Logger.getLogger(EscidocAaLogoutClient.class);
	
	@Override
	protected String getLogoutUrl(HttpServletRequest request,
			HttpServletResponse response, AuthenticationVO authVO) throws Exception {

		String originalTarget = URLDecoder.decode(request.getParameter("target"), "UTF-8");
		String userHandle = authVO.getData().get("eSciDocUserHandle");
		logger.debug("Logging out CoNE user with handle: " + userHandle);
		AdminHelper.logoutUser(userHandle);
		

		/*
		String redirectUrl =  Config.getProperty("escidoc.framework_access.login.url")
                + "/aa/logout"
                + "?target=" + URLEncoder.encode(originalTarget, "UTF-8");
		
		return redirectUrl;
		*/
		return originalTarget;
		

	}


}
