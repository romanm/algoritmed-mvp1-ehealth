package org.algoritmed.mvp1.util ;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class EhealthUaRegistryWebClient {
	private static final Logger logger = LoggerFactory.getLogger(EhealthUaRegistryWebClient.class);

//	String prefix_uri = "/api";
//	String prefix_uri = "";

	public Map apiGet(Map<String, Object> data) {
//		String path_uri = prefix_uri+data.get("add_uri");
		String path_uri = ""+data.get("add_uri");
		if(data.containsKey("queryString"))
			path_uri += "?"+data.get("queryString");
		System.err.println(path_uri);
		String test_api_URL = domain_registry  + path_uri;
		data.put("test_api_URL", test_api_URL);
		logger.info(""
				+ "\n"+36
				+ "\n"+data.toString().replaceAll(", ", "\n , ")
				+ "\n"+39
				+ "\n"+uri_registry_legal_entities
				);
		Builder wsClientInvocation = getInvocationBuilder(test_api_URL);
//		Builder wsClientInvocation = getInvocationBuilder(path_uri);
		Response response = wsClientInvocation.get();
		String readEntity_body = response.readEntity(String.class);
//		System.err.println(readEntity_body);
		Map readValue_mapBody = strToMap(readEntity_body);
		data.put("response", readValue_mapBody);
		return data;
	}

	public Map legal_entitiesPut(Map<String, Object> data, String uri) {
		String readEntity_body = legal_entitiesPutStr(data, uri);
		Map readValue_mapBody = strToMap(readEntity_body);
		return readValue_mapBody;
	}

	public String legal_entitiesPutStr(Map<String, Object> data, String uri) {
		String readEntity_body = null;
		try {
			String dataStr = mapper.writeValueAsString(data);
			System.err.println(58);
			System.err.println(uri);
			System.err.println(uri.indexOf("divisions"));
			System.err.println(uri.indexOf("divisions")>=0);
//			System.err.println(dataStr);
//			System.err.println(28);
			Entity<String> dataJson = Entity.json(dataStr);
//			Builder wsClientInvocation = getInvocationBuilder();
			Builder wsClientInvocation = getInvocationBuilder(uri);
//			Builder wsClientInvocation = getInvocationBuilder(path_uri_registry_msp);
			Response response ;
			if(uri.indexOf("divisions")>=0) {
				System.err.println(70);
				response = wsClientInvocation.post(dataJson);
			}else {
				response = wsClientInvocation.put(dataJson);
			}
			readEntity_body = response.readEntity(String.class);
//			System.err.println(53);
//			System.err.println(readEntity_body);
//			System.err.println(readEntity_body.length());
//			System.err.println(55);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return readEntity_body;
	}

	private Map strToMap(String readEntity_body) {
		Map readValue_mapBody = null;
		if(readEntity_body!=null){
			try {
				readValue_mapBody = mapper.readValue(readEntity_body, Map.class);
				String writeValueAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readValue_mapBody);
//				System.err.println(writeValueAsString);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return readValue_mapBody;
	}

	@Autowired ObjectMapper mapper = new ObjectMapper();
	/*
config.uri_registry: http://demo.ehealth.world
config.path_registry_msp: /api/legal_entities
	 * */
	private @Value("${config.domain_registry}")				String domain_registry;
	private @Value("${config.uri_registry_legal_entities}")	String uri_registry_legal_entities;
	private @Value("${config.path_registry_msp}")			String path_uri_registry_msp;
	private @Value("${config.token_bearer}")				String token_bearer;

	private Builder getInvocationBuilder(String path_uri) {
//	private Builder getInvocationBuilder() {
		Client client = ClientBuilder.newClient();
		Builder header = client.target(path_uri)
//		Builder header = client.target(uri_registry + path_uri)
//		Builder header = client.target(uri_registry_legal_entities)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Bearer "+token_bearer);
		//test token
//		.header("Authorization", "Bearer c490c936651a0f6badeb426721076437");
		return header;
	}

}
