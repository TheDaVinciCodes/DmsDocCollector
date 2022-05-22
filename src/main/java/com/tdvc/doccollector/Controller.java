package com.tdvc.doccollector;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tdvc.doccollector.service.CbkService;
import com.tdvc.doccollector.service.SftpService;

/**
 * @author TheDaVinciCodes
 *
 */
@RestController
public class Controller {

	private final CbkService cbkService;
	private final SftpService transferService;

	public Controller(CbkService cbkService, SftpService transferService) {
		this.cbkService = cbkService;
		this.transferService = transferService;
	}


	// http://localhost:8080/customer-list
	@GetMapping(path = "/customer-list")
	public List<String> collectCustomerList() {
		return cbkService.getCustomerList();
	}

	// http://localhost:8080/collect-as-list?customer-name={customer name}
	@GetMapping(path = "/collect-as-list")
	public List<Map<String, Object>> collectClaimList(@RequestParam(name = "customer-name") String customer) {
		return cbkService.getDocIdsForClaim(customer);
	}

	// http://localhost:8080/persist-documents?customer-name={customer name}
	@PostMapping(path = "/persist-documents")
	public void collectDocuments(@RequestParam(name = "customer-name") String customer) {
		cbkService.persistDocuments(customer);
	}

	// http://localhost:8080/file-transfer
	@PostMapping(path = "/file-transfer")
	public void transferDocuments() {
		transferService.uploadSftp();
	}

	// http://localhost:8080/doc-to-claim?customer-name={customer name}
	@PostMapping(path = "/doc-to-claim")
	public void transferDocuments(@RequestParam(name = "customer-name") String customer) {
		cbkService.persistDocuments(customer);
		transferService.uploadSftp();
	}

}