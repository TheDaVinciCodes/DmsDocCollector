package com.tdvc.doccollector.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tdvc.dms.rest.client.DmsRestClient;
import com.tdvc.dms.rest.model.LoginRequest;

/**
 * @author TheDaVinciCodes
 *
 */
@Service
public class DmsService {

	private final DmsRestClient dmsRestClient;
	private static String DMS_TECHNICAL_USER;

	DmsService(DmsRestClient dmsRestClient, @Value("${dms.technical.user}") String user) {
		this.dmsRestClient = dmsRestClient;
		DMS_TECHNICAL_USER = user;
	}

	String getDmsSession(String system, String mandate) {
		return dmsRestClient.logon(DmsRequestBuilder.getLoginRequest(system, mandate)).block();
	}

	JsonNode getDocument(String docId, String sessionId) {
		return dmsRestClient.getDocument(docId, sessionId).block();
	}

	private static final class DmsRequestBuilder {

		private static LoginRequest getLoginRequest(String system, String mandate) {
			return LoginRequest.builder()
					.clientId(mandate)
					.edoksSystem(system)
					.userId(DMS_TECHNICAL_USER)
					.build();
		}

	}

}
