package org.algoritmed.mvp1.medic;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.algoritmed.mvp1.DbAlgoritmed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OAuthRest  extends DbAlgoritmed {
	private static final Logger logger = LoggerFactory.getLogger(OAuthRest.class);
	
	@GetMapping(value = "/r/from_oauth_tokens")
	public String  from_oauth_tokens(){
		logger.info("---------------\n"
				+ "/r/from_oauth_tokens"
				+ "\n" 
				);
		return "redirect:/v/admin-msp";
	}
	@GetMapping(value = "/r/to_oauth_tokens")
	public String  to_oauth_tokens(@RequestParam("code") String code){
		Client client = ClientBuilder.newClient();
		String oauth_tokens_body = "{  'token': "
				+ "{"
				+ "    'grant_type': 'authorization_code'"
				+ ",    'code': '"+ code+ "'"
				+ ",    'client_id': 'bf48fba2-e4e8-4a06-aeaa-345d8346d7bb'"
				+ ",    'client_secret': 'ZHdqTTVGWjYrMFRRa0hoYmpGVTFldz09'"
				+ ",    'redirect_uri': 'https://medic.algoritmed.com/r/from_oauth_tokens'"
				+ ",    'scope': 'employee:read employee:write"
				+ " employee_request:approve employee_request:read employee_request:write employee_request:reject"
				+ " legal_entity:read"
				+ " division:read division:write"
				+ " declaration_request:write declaration_request:read employee:deactivate"
				+ " otp:read otp:write' "
				+ "}}";
		System.err.println(oauth_tokens_body);
		Entity payload = Entity.json(oauth_tokens_body);
		String server = env.getProperty("config.uri_oauth2");
		String uri = server	+ "/oauth/tokens";
		System.err.println(uri);
		uri = "https://api.ehealth.world/oauth/tokens/";
		System.err.println(uri);
		Response response = client.target(uri)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("cache-control", "no-cache")
				.post(payload);
//				.header("postman-token", "560ff187-848c-467a-d1b5-d4383ecfa911")
//				.header("content-type", "application/json")

				System.out.println("status: " + response.getStatus());
				System.out.println("headers: " + response.getHeaders());
				System.out.println("body:" + response.readEntity(String.class));
		return "redirect:/v/admin-msp";
	}

	@GetMapping(value = "/r/test_ask_owner_token")
	public @ResponseBody Map<String, Object>  test_ask_owner_token() throws IOException {
		logger.info("---------------\n"
				+ "/r/test_ask_owner_token"
				+ "\n" 
				);

		Client client = ClientBuilder.newClient();
		//config.uri_registry: https://demo.ehealth.world/api
		String server = env.getProperty("config.uri_oauth2");
		String uri = server
				+ "/sign-in"
				+ "?client_id=bf48fba2-e4e8-4a06-aeaa-345d8346d7bb"
				+ "&redirect_url=https://medic.algoritmed.com/"
				+ "&scope=&response_type=";
		System.err.println(uri);
		Response response = client.target(uri)
				.request(MediaType.TEXT_PLAIN_TYPE)
				.header("Authorization", "Bearer c490c936651a0f6badeb426721076437")
				.header("X-CSRF-Token", "Bearer c490c936651a0f6badeb426721076437")
				.get();

		System.out.println("status: " + response.getStatus());
		System.out.println("headers: " + response.getHeaders());
//		System.out.println("body:" + response.readEntity(String.class));


		return null;
	}
	
	@GetMapping(value = "/r/ehealt_sing_in_redirect")
	public String  ehealt_sing_in_redirect(HttpServletRequest request){
		logger.info("---------------\n"
				+ "/r/ehealt_sing_in_redirect"
				+ "\n"
				+ "\n" 
				+ request.getHeader("Location")
				);
		while (request.getHeaderNames().hasMoreElements()) {
			String headerName = (String) request.getHeaderNames().nextElement();
			String header = request.getHeader(headerName);
			System.err.println(headerName+": "+header);
		}
		return "redirect:/v/admin-msp";
	}
	
}
