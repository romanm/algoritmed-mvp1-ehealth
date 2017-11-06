package org.algoritmed.mvp1.medic;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.algoritmed.mvp1.DbAlgoritmed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OAuthRest  extends DbAlgoritmed {
	private static final Logger logger = LoggerFactory.getLogger(OAuthRest.class);

	@GetMapping(value = "/r/test_ask_owner_token")
	public @ResponseBody Map<String, Object>  test_ask_owner_token() throws IOException {
		logger.info("---------------\n"
				+ "/r/test_ask_owner_token"
				+ "\n" 
				);

		Client client = ClientBuilder.newClient();
		//config.uri_registry: https://demo.ehealth.world/api
		String server = env.getProperty("config.uri_registry");
		String uri = "http://auth.demo.ehealth.world"
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
}
