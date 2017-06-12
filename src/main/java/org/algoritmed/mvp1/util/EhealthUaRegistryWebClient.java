package org.algoritmed.mvp1.util;

import java.io.IOException;
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
		String readEntity_body = null;
		try {
			String dataStr = mapper.writeValueAsString(data);
			System.err.println(26);
			System.err.println(dataStr);
			System.err.println(28);
			Entity<String> dataJson = Entity.json(dataStr);
			Builder wsClientInvocation = getInvocationBuilder();
			Response response = wsClientInvocation.put(dataJson);
			readEntity_body = response.readEntity(String.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if(readEntity_body!=null){
			try {
				Map readValue_mapBody = mapper.readValue(readEntity_body, Map.class);
				String writeValueAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readValue_mapBody);
				System.err.println(writeValueAsString);
				return readValue_mapBody;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Autowired ObjectMapper mapper = new ObjectMapper();
	/*
config.uri_registry: http://demo.ehealth.world
config.path_registry_msp: /api/legal_entities
	 * */
	private @Value("${config.path_registry_msp}")	String path_registry_msp;
	private @Value("${config.uri_registry}")		String uri_registry;

	private Builder getInvocationBuilder() {
		Client client = ClientBuilder.newClient();
		Builder header = client.target(uri_registry + path_registry_msp)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("Authorization", "Bearer c490c936651a0f6badeb426721076437");
		return header;
	}

}
