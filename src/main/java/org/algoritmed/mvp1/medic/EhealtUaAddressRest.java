package org.algoritmed.mvp1.medic;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.algoritmed.mvp1.util.EhealthUaRegistryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${config.security_prefix}/gcc")
public class EhealtUaAddressRest {

	private Map<String, Object> goCC(HttpServletRequest request, String add_uri) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("add_uri", add_uri);
		map.put("requestURI", request.getRequestURI());
		logger.info(""
				+ "\n"+21
				+ "\n"+map
				+ "\n"+request.getQueryString()
				);
		if(null!=request.getQueryString())
			map.put("queryString", request.getQueryString());
		System.err.println(map);
		registryWebClient.apiGet(map);
		return map;
	}

	@GetMapping(value = "/{u1}/{u2}/{u3}/{u4}/{u5}")
	public @ResponseBody Map<String, Object>  u5(@PathVariable String u1 ,@PathVariable String u2 ,@PathVariable String u3 
			,@PathVariable String u4 ,@PathVariable String u5
			,HttpServletRequest request
			) {
		return goCC(request, "/"+u1+"/"+u2+"/"+u3+"/"+u4+"/"+u5);
	}
	@GetMapping(value = "/{u1}/{u2}/{u3}/{u4}")
	public @ResponseBody Map<String, Object>  u4(@PathVariable String u1 ,@PathVariable String u2 ,@PathVariable String u3 ,@PathVariable String u4
			,HttpServletRequest request
			) {
		return goCC(request, "/"+u1+"/"+u2+"/"+u3+"/"+u4);
	}
	@GetMapping(value = "/{u1}/{u2}/{u3}")
	public @ResponseBody Map<String, Object>  u3(@PathVariable String u1 ,@PathVariable String u2 ,@PathVariable String u3
			,HttpServletRequest request
			) {
		return goCC(request, "/"+u1+"/"+u2+"/"+u3);
	}
	@GetMapping(value = "/{u1}/{u2}")
	public @ResponseBody Map<String, Object>  u2(@PathVariable String u1,@PathVariable String u2
			,HttpServletRequest request) {
		return goCC(request, "/"+u1+"/"+u2);
	}
	@GetMapping(value = "/{u1}")
	public @ResponseBody Map<String, Object>  u1(@PathVariable String u1,HttpServletRequest request) {
		System.err.println("/{u1}");
		return goCC(request, "/"+u1);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EhealtUaAddressRest.class);
	private @Autowired EhealthUaRegistryWebClient registryWebClient;
}
