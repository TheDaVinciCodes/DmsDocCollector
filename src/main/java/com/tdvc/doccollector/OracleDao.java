package com.tdvc.doccollector;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author TheDaVinciCodes
 *
 */
@Repository
public class OracleDao {

	private final JdbcTemplate jdbcTemplate;

	OracleDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * 
	 * @return a list of available customers
	 */
	public List<String> queryCustomer() {

		final String sql = "SELECT CLEARING_NAME AS CUSTOMER FROM (SELECT * FROM "
				+ "	(SELECT * FROM CBKACQ.cbk_item WHERE HHOSTID LIKE 'EDOKS%' and DIRECTION = 'I') item "
				+ "	INNER JOIN (SELECT * FROM CBKACQ.cbk_claim) claim ON claim.id = item.CBK_CLAIMID) d "
				+ "	inner join (SELECT * FROM CBKACQ.CUS_MERCHANT "
				+ "ORDER BY CLEARING_NAME) c on d.MERCHANTID = c.MERCHANTID";

		return jdbcTemplate.queryForList(sql, String.class);
	}

	/**
	 * 
	 * @param  customer 	 >> The Company Inc.
	 * @return CUSTOMER_NAME >> The Company Inc.
	 * 		   CLAIMID 		 >> 123.456.789.012
	 * 		   EHOSTID		 >> CompanyDMS~CBK_ACQ-/12~1234567
	 */
	public List<Map<String, Object>> queryDocIds(String customer) {

		final String sql = "SELECT QCSJ_C000000000600003 AS CLAIMID, CLEARING_NAME AS CUSTOMER_NAME, EHOSTID FROM "
				+ "(SELECT * FROM (SELECT * FROM CBKACQ.cbk_item WHERE HHOSTID LIKE 'EDOKS%' and "
				+ "DIRECTION = 'I') item INNER JOIN (SELECT * FROM CBKACQ.cbk_claim) claim "
				+ "ON claim.id = item.CBK_CLAIMID) d inner join (SELECT * FROM CBKACQ.CUS_MERCHANT "
				+ "WHERE CLEARING_NAME LIKE '%" + customer.toUpperCase() + "%') c on d.MERCHANTID = c.MERCHANTID";

		return jdbcTemplate.queryForList(sql);
	}
}
