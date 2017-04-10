package de.mpg.escidoc.services.aa.web.client;

import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.aa.Aa;
import de.mpg.escidoc.services.aa.AuthenticationVO;
import de.mpg.escidoc.services.aa.crypto.RSAEncoder;

public class LogoutClient extends Client {

	private static final Logger logger = Logger.getLogger(LogoutClient.class);
	
	@Override
	protected void process(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String[] encryptedAuthVOString = request.getParameterValues("auth");
		String authVOXml = RSAEncoder.rsaDecrypt(encryptedAuthVOString);
		AuthenticationVO authVO = new AuthenticationVO(authVOXml);
		
		request.getSession().removeAttribute("authentication");
		
		try
        {
            response.sendRedirect(getLogoutUrl(request, response, authVO));
        }
        catch (IllegalStateException ise)
        {
            logger.warn("Caught IllegalStateException: DEBUG for more info");
            logger.debug("LogoutClient tried to send a redirect, but there was probably already a header defined.");
        }
		

	}
	
	protected String getLogoutUrl(HttpServletRequest request, HttpServletResponse response, AuthenticationVO authVO) throws Exception
	{
		String target = request.getParameter("target");
		
		if(target != null)
		{
			return target;
		}
		else
		{
			logger.warn("No query parameter 'target' found for logging out.");
			return null;
		}
	}

}
