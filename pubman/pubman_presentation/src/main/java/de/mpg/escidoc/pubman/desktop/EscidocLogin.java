package de.mpg.escidoc.pubman.desktop;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.ws.rs.core.UriBuilder;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.services.framework.AdminHelper;

public class EscidocLogin extends FacesBean{

	private String username;
	
	private String password;
	
	private String target;
	
	
	public EscidocLogin()
	{
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> parameters = context.getExternalContext().getRequestParameterMap();
        this.target = parameters.get("target");
        
        
	}
	
	
	
	public void login()
	{
		try {
			
			URI targetUrl = new URL(target).toURI();
			String userHandle = AdminHelper.loginUser(getUsername(), getPassword());
			String base64UserHandle = Base64.getEncoder().encodeToString(userHandle.getBytes());
			
			URI redirectUri = UriBuilder.fromUri(targetUrl).queryParam("eSciDocUserHandle", base64UserHandle).build();
			FacesContext.getCurrentInstance().getExternalContext().redirect(redirectUri.toString());
		} catch (Exception e) {
			error(getMessage("LoginError"));
		} 
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
