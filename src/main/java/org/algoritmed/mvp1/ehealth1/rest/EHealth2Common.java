package org.algoritmed.mvp1.ehealth1.rest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EHealth2Common {
	void patchUri(String uri) {

//		String uri = "http://ehealth.com/api/legal_entities/{id}/actions/mis_verify";
		Builder header = getInvocationBuilder(uri);
//		Response response = header.patch();
	}
	Builder getInvocationBuilder(String uri) {
		Client client = ClientBuilder.newClient();
		Builder header = client.target(uri)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("api-key", env.getProperty("config.mis_client_secret_client_id"))
				.header("Authorization", "Bearer "+env.getProperty("config.token_bearer"));
		return header;
	}
	@Autowired protected Environment env;
}
