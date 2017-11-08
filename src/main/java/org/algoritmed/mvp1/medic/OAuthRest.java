package org.algoritmed.mvp1.medic;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OAuthRest  extends OAuthRestCommon {
//	public class OAuthRest  extends DbAlgoritmed {
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
		System.err.println(uri);
		String oauth_tokens_body = getBodyForOAuthTokenRequest(code);
		System.err.println("oauth_tokens_body");
		System.err.println(oauth_tokens_body);
		System.err.println("oauth_tokens_body END");
		Entity payload = Entity.json(oauth_tokens_body);
		System.err.println("payload");
		System.err.println(payload);

		Client client = ClientBuilder.newClient();
		Builder invocationBuilder = client.target(uri)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("cache-control", "no-cache")
				;
		Response response = invocationBuilder.post(payload);

		//			.header("postman-token", "560ff187-848c-467a-d1b5-d4383ecfa911")
		//			.header("Authorization", "Bearer c490c936651a0f6badeb426721076437")
		//				.header("content-type", "application/json")
		System.out.println("\n status: " + response.getStatus());
		System.out.println("\n headers: " + response.getHeaders());
		System.out.println("\n body:" + response.readEntity(String.class));
		System.out.println("\n body: END --------------------------------------" );

		String bashCommand = "curl -X POST \\ \n"
				+ uri
				+ "\\ \n"
				+ "-H  \\\"cache-control: no-cache\\\" \\ \n"
				+ "-H \\\"content-type: application/json\\\" \\ \n"
				//				+ "-H 'postman-token: 560ff187-848c-467a-d1b5-d4383ecfa911' \\ \n"
				+ "-d '"
				+ oauth_tokens_body
				+ "' \n"
				+ "";
		System.err.println(bashCommand);
		runBashCommand(bashCommand);
		return "redirect:/v/admin-msp";
	}

	private void runBashCommand(String bashCommand) {
		String s;
        Process p;
        try {
            p = Runtime.getRuntime().exec(bashCommand);
            BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()));
            System.out.println("result");
            while ((s = br.readLine()) != null)
                System.out.println(s);
            System.out.println("result END --------------------------");
            p.waitFor();
            System.out.println ("\n exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {}
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
