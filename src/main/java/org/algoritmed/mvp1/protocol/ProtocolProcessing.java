package org.algoritmed.mvp1.protocol;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.algoritmed.mvp1.DbAlgoritmed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProtocolProcessing extends DbAlgoritmed{
	private static final Logger logger = LoggerFactory.getLogger(ProtocolProcessing.class);
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	private @Value("${sql.count.doc.to.check}")			String sqlCountDocToCheck;
	private @Value("${sql.doc.to.check}")				String sqlDocToCheck;
	private @Value("${sql.docchecked.update.checked}")	String sqlDoccheckedUpdateChecked;
	private @Value("${sql.docchecked.insert}")			String sqlDoccheckedInsert;
	
	@Scheduled(fixedRate = 15000)
	private void reportCurrentTime() {
		List<Map<String, Object>> listDocToCheck = db1JdbcTemplate.queryForList(sqlDocToCheck);
		if(listDocToCheck.size()>0){
			logger.info("The time is now {}", dateFormat.format(new Date()));
			Map<String, Object> mapDocToCheck = listDocToCheck.get(0);
			if(!mapDocToCheck.containsKey("docchecked_id")||null==mapDocToCheck.get("docchecked_id")){//insert
				Object object = mapDocToCheck.get("updated");
				mapDocToCheck.put("checked", object);
				mapDocToCheck.put("doc_id", mapDocToCheck.get("doctimestamp_id"));
				mapDocToCheck.put("docchecked_id", mapDocToCheck.get("doctimestamp_id"));
				db1ParamJdbcTemplate.update(sqlDoccheckedInsert, mapDocToCheck);
			}else{//update
				db1ParamJdbcTemplate.update(sqlDoccheckedUpdateChecked, mapDocToCheck);
			}
		}else{
//			logger.info("Nothing to update. The time is now {}", dateFormat.format(new Date()));
		}
	}
}
