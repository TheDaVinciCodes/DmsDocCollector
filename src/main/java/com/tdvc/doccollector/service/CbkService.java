package com.tdvc.doccollector.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tdvc.doccollector.OracleDao;

/**
 * @author TheDaVinciCodes
 *
 */
@Service
public class CbkService {

	private final OracleDao dao;
	private final DmsService dmsService;
	private final FileService fileService;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HHmmssSSS");

	CbkService(OracleDao dao, DmsService dmsService,
						   FileService fileService) {
		this.dao = dao;
		this.dmsService = dmsService;
		this.fileService = fileService;
	}

	public List<String> getCustomerList() {
		logger.debug(toString());
		logger.info("Retrieving customer List...");
		return dao.queryCustomer();
	}

	public List<Map<String, Object>> getDocIdsForClaim(String customer) {
		logger.debug(toString());

		if (customer.isEmpty()) {
			throw new RuntimeException("Empty customer value");
		}

		if (!dao.queryCustomer().toString().contains(customer.toUpperCase())) {
			throw new RuntimeException("'" + customer + "' is not a valid customer");
		}

		logger.info("Retrieving documents with their corresponding claims...");
		return dao.queryDocIds(customer);
	}
	
	public void persistDocuments(String customer) {

		final String date = LocalDateTime.now().format(formatter);

		for (Map<String, Object> map : getDocIdsForClaim(customer)) {
			final String claimId = map.get("CLAIMID").toString();
			final String hostId = map.get("EHOSTID").toString();
			final String customerName = map.get("CUSTOMER_NAME").toString();
			final String docId = hostId.substring(hostId.lastIndexOf("~") + 1);
			final String system = hostId.substring(hostId.indexOf("~") + 1, hostId.indexOf("-"));
			final String mandate = hostId.substring(hostId.indexOf("-") + 1, hostId.lastIndexOf("~"));
			logger.info("Persisting claim: " + claimId + ", docId: " + docId);
			final String sessionId = dmsService.getDmsSession(system, mandate);
			final JsonNode document = dmsService.getDocument(docId, sessionId);
			fileService.persistFile(claimId, document, date, customerName);
		}

		fileService.protectFile();
	}

}
