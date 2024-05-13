package com.transit.backend.rightlayers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.List;
import java.util.UUID;

public interface RightsManageService {
	
	
	void registerCompany(UUID companyUUID);
	
	void createEntityAndConnectIt(UUID targetId, String typeClazz, Class<?> clazz);
	
	@Transactional
	void createEntityAndConnectIt(UUID targetId, String typeClazz, Class<?> clazz, UUID userID);
	
	
	@Transactional
	void connectEntityToNewCompany(UUID id, UUID companyID, UUID subOrderId, List<String> readProperties, List<String> writeProperties) throws ClassNotFoundException, MessagingException, InterruptedException;
	
	@Transactional
	void connectEntityToNewCompany(UUID id, UUID companyID, UUID subOrderId, List<String> readProperties, List<String> writeProperties, boolean outsideClass) throws ClassNotFoundException, MessagingException, InterruptedException;
	
	@Transactional
	void updateEntityConnection(UUID id, UUID companyId, UUID orderId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException, ClassNotFoundException, MessagingException, InterruptedException;
	
	@Transactional
	void updateEntityConnectionByPUT(UUID id, UUID companyId, UUID orderId, List<String> readProperties, List<String> writeProperties) throws ClassNotFoundException, MessagingException, InterruptedException;
	
	@Transactional
	void updateEntityConnectionByPUT(UUID id, UUID companyId, UUID orderId, List<String> readProperties, List<String> writeProperties, UUID parentCompanyId) throws ClassNotFoundException, MessagingException, InterruptedException;
	
	
}
