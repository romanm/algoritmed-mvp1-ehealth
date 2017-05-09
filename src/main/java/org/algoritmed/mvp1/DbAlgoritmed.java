package org.algoritmed.mvp1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

public class DbAlgoritmed {
	@Autowired
	protected JdbcTemplate db1JdbcTemplate;

	/**
	 * SQL select - повертає наступний ID единий для всієй БД.
	 */
	private @Value("${sql.nextdbid}") String sqlNextDbId;
	/**
	 * Генератор наступного ID единого для всієї БД.
	 * @return Наступний ID единий для всієй БД.
	 */
	protected Integer nextDbId() {
		Integer nextDbId = db1JdbcTemplate.queryForObject(sqlNextDbId, Integer.class);
		return nextDbId;
	}

}
