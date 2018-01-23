package org.algoritmed.mvp1.ehealth;

import java.security.Principal;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EmployeeRegistryRest  extends RestTemplateCommon {
	@PostMapping("/mspEmployeeToEHealth")
	public @ResponseBody Map<String, Object> mspDivisionToEHealth(
			@RequestBody Map<String, Object> data
			,HttpServletRequest request
			,Principal principal
			) {
		long timeInMillis = Calendar.getInstance().getTimeInMillis();
		data.put("timeInMillis", timeInMillis);
		return data;
	}
}
