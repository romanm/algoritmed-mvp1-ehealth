package org.algoritmed.mvp1.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WebClient {
	private static final Logger logger = LoggerFactory.getLogger(WebClient.class);
	/**
	 * Зчитування даних з сервера 
	 * @param url REST адреса для зчитування даних
	 * @return данні згідно наданого запиту
	 */
	public Map<String, Object> getFromUrl(String url) {
		Map mapResponsed = null;
		logger.info(" ---------url------\n " + url);
		try {
			URL obj = new URL(url);
			logger.info(" -----------obj----\n " + obj);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			logger.info(" ------con-----con----\n " + con);
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json"); 
			con.setRequestProperty("charset", "utf-8");
			InputStream requestBody = con.getInputStream();
			mapResponsed = mapper.readValue(requestBody, Map.class);
			logger.info(" ----------mapResponsed-----\n " + mapResponsed);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mapResponsed;
	}

	@Autowired
	ObjectMapper mapper = new ObjectMapper();

}
