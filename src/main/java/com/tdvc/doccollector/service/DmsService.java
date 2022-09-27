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

	DmsService(DmsRestClient dmsRestClient) {
		this.dmsRestClient = dmsRestClient;
	}

	@Value("${dms.technical.user}")
	private static String DMS_TECHNICAL_USER;
	
	String getDmsSession(String system, String mandate) {
		return dmsRestClient.logon(DmsRequestBuilder.getLoginRequest(system, mandate)).block();
	}

	JsonNode getDocument(String docId, String sessionId) {
		return dmsRestClient.getDocument(docId, sessionId).block();
	}

	private final static class DmsRequestBuilder {

		private static LoginRequest getLoginRequest(String system, String mandate) {
			return LoginRequest.builder()
					.clientId(mandate)
					.edoksSystem(system)
					.userId(DMS_TECHNICAL_USER)
					.build();
		}
	}
}
