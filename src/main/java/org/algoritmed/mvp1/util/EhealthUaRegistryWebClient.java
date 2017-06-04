package org.algoritmed.mvp1.util;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class EhealthUaRegistryWebClient {

	public Map legal_entitiesPut(Map<String, Object> data) {
		System.err.println("EhealthUaRegistryWebClient.legal_entitiesPut");
		System.out.println(data);
		System.err.print("data.keySet -> ");
		System.err.println(data.keySet());
		String readEntity_body = null;
		try {
			String dataStr = mapper.writeValueAsString(data);
			System.out.print("dataStr -> ");
			System.out.println(dataStr);
			
			/*
			byte[] encodedBytes = Base64.getEncoder().encode(dataStr.getBytes());
//			System.out.println("encodedBytes " + new String(encodedBytes));
			HashMap<String, Object> encodintBase64Map = new HashMap<>();
			encodintBase64Map.put("signed_content_encoding", "base64");
			encodintBase64Map.put("signed_legal_entity_request", new String(encodedBytes));
			System.err.println(encodintBase64Map);
//			dataStr = mapper.writeValueAsString(encodintBase64Map);
			 * */

			Entity<String> dataJson = Entity.json(dataStr);
			
			Builder header = getInvocationBuilder();
			Response response = header.put(dataJson);
			
			System.out.println("status: " + response.getStatus());
			System.out.println("headers: " + response.getHeaders());
			readEntity_body = response.readEntity(String.class);
			System.err.println("body:" + readEntity_body);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if(readEntity_body!=null){
			try {
				Map readValue_mapBody = mapper.readValue(readEntity_body, Map.class);
				System.out.println("readValue_mapBody: ");
				System.out.println(readValue_mapBody);
				return readValue_mapBody;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private @Value("${config.path_registry_msp}")	String path_registry_msp;
	private @Value("${config.uri_registry}")		String uri_registry;

	private Builder getInvocationBuilder() {
		Client client = ClientBuilder.newClient();
		System.err.println(client);
		//		Response response = client.target("https://private-anon-902000152e-ehealthapi1.apiary-mock.com/api/legal_entities")
		//		.header("Authorization", "Bearer mF_9.B5f-4.1JqM")
		//		.header("Authorization", "access_token c490c936651a0f6badeb426721076437")
		//		Response response = client.target("http://104.199.16.248:80/api/legal_entities")
		Builder header = client.target(uri_registry + path_registry_msp)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("Authorization", "Bearer c490c936651a0f6badeb426721076437");
		return header;
	}


	@Autowired
	ObjectMapper mapper = new ObjectMapper();

}
