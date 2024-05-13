package com.transit.backend.security.authmodel;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Getter
@Component
public class CheckRightsMatrix {
	@Autowired
	private KeycloakServiceManager keycloakServiceManager;
	
	
	private Map<TransitAuthorities, Map<TransitAuthorities, Boolean>> rightMatrix;
	
	
	public CheckRightsMatrix() {
		this.rightMatrix = new HashMap<>();
		Map<TransitAuthorities, Boolean> adminGlobalRights = new HashMap<>();
		Map<TransitAuthorities, Boolean> ownerCompanyRights = new HashMap<>();
		Map<TransitAuthorities, Boolean> adminCompanyRights = new HashMap<>();
		Map<TransitAuthorities, Boolean> creatorOrderRights = new HashMap<>();
		Map<TransitAuthorities, Boolean> plannerOrderRights = new HashMap<>();
		Map<TransitAuthorities, Boolean> supplierRights = new HashMap<>();
		Map<TransitAuthorities, Boolean> managerWarehouseRights = new HashMap<>();
		Map<TransitAuthorities, Boolean> workerWarehosueRights = new HashMap<>();
		Map<TransitAuthorities, Boolean> registrationUserRights = new HashMap<>();
		adminGlobalRights.put(TransitAuthorities.ADMIN_GLOBAL, true);
		adminGlobalRights.put(TransitAuthorities.OWNER_COMPANY, false);
		adminGlobalRights.put(TransitAuthorities.ADMIN_COMPANY, false);
		adminGlobalRights.put(TransitAuthorities.CREATOR_ORDER, false);
		adminGlobalRights.put(TransitAuthorities.PLANNER_ORDER, false);
		adminGlobalRights.put(TransitAuthorities.SUPPLIER, false);
		adminGlobalRights.put(TransitAuthorities.MANAGER_WAREHOUSE, false);
		adminGlobalRights.put(TransitAuthorities.WORKER_WAREHOUSE, false);
		
		ownerCompanyRights.put(TransitAuthorities.ADMIN_GLOBAL, false);
		ownerCompanyRights.put(TransitAuthorities.OWNER_COMPANY, true);
		ownerCompanyRights.put(TransitAuthorities.ADMIN_COMPANY, true);
		ownerCompanyRights.put(TransitAuthorities.CREATOR_ORDER, true);
		ownerCompanyRights.put(TransitAuthorities.PLANNER_ORDER, true);
		ownerCompanyRights.put(TransitAuthorities.SUPPLIER, true);
		ownerCompanyRights.put(TransitAuthorities.MANAGER_WAREHOUSE, true);
		ownerCompanyRights.put(TransitAuthorities.WORKER_WAREHOUSE, true);
		
		adminCompanyRights.put(TransitAuthorities.ADMIN_GLOBAL, false);
		adminCompanyRights.put(TransitAuthorities.OWNER_COMPANY, false);
		adminCompanyRights.put(TransitAuthorities.ADMIN_COMPANY, true);
		adminCompanyRights.put(TransitAuthorities.CREATOR_ORDER, true);
		adminCompanyRights.put(TransitAuthorities.PLANNER_ORDER, true);
		adminCompanyRights.put(TransitAuthorities.SUPPLIER, true);
		adminCompanyRights.put(TransitAuthorities.MANAGER_WAREHOUSE, true);
		adminCompanyRights.put(TransitAuthorities.WORKER_WAREHOUSE, true);
		
		creatorOrderRights.put(TransitAuthorities.ADMIN_GLOBAL, false);
		creatorOrderRights.put(TransitAuthorities.OWNER_COMPANY, false);
		creatorOrderRights.put(TransitAuthorities.ADMIN_COMPANY, false);
		creatorOrderRights.put(TransitAuthorities.CREATOR_ORDER, false);
		creatorOrderRights.put(TransitAuthorities.PLANNER_ORDER, false);
		creatorOrderRights.put(TransitAuthorities.SUPPLIER, false);
		creatorOrderRights.put(TransitAuthorities.MANAGER_WAREHOUSE, false);
		creatorOrderRights.put(TransitAuthorities.WORKER_WAREHOUSE, false);
		
		plannerOrderRights.put(TransitAuthorities.ADMIN_GLOBAL, false);
		plannerOrderRights.put(TransitAuthorities.OWNER_COMPANY, false);
		plannerOrderRights.put(TransitAuthorities.ADMIN_COMPANY, false);
		plannerOrderRights.put(TransitAuthorities.CREATOR_ORDER, false);
		plannerOrderRights.put(TransitAuthorities.PLANNER_ORDER, false);
		plannerOrderRights.put(TransitAuthorities.SUPPLIER, false);
		plannerOrderRights.put(TransitAuthorities.MANAGER_WAREHOUSE, false);
		plannerOrderRights.put(TransitAuthorities.WORKER_WAREHOUSE, false);
		
		supplierRights.put(TransitAuthorities.ADMIN_GLOBAL, false);
		supplierRights.put(TransitAuthorities.OWNER_COMPANY, false);
		supplierRights.put(TransitAuthorities.ADMIN_COMPANY, false);
		supplierRights.put(TransitAuthorities.CREATOR_ORDER, false);
		supplierRights.put(TransitAuthorities.PLANNER_ORDER, false);
		supplierRights.put(TransitAuthorities.SUPPLIER, false);
		supplierRights.put(TransitAuthorities.MANAGER_WAREHOUSE, false);
		supplierRights.put(TransitAuthorities.WORKER_WAREHOUSE, false);
		
		managerWarehouseRights.put(TransitAuthorities.ADMIN_GLOBAL, false);
		managerWarehouseRights.put(TransitAuthorities.OWNER_COMPANY, false);
		managerWarehouseRights.put(TransitAuthorities.ADMIN_COMPANY, false);
		managerWarehouseRights.put(TransitAuthorities.CREATOR_ORDER, false);
		managerWarehouseRights.put(TransitAuthorities.PLANNER_ORDER, false);
		managerWarehouseRights.put(TransitAuthorities.SUPPLIER, false);
		managerWarehouseRights.put(TransitAuthorities.MANAGER_WAREHOUSE, false);
		managerWarehouseRights.put(TransitAuthorities.WORKER_WAREHOUSE, false);
		
		workerWarehosueRights.put(TransitAuthorities.ADMIN_GLOBAL, false);
		workerWarehosueRights.put(TransitAuthorities.OWNER_COMPANY, false);
		workerWarehosueRights.put(TransitAuthorities.ADMIN_COMPANY, false);
		workerWarehosueRights.put(TransitAuthorities.CREATOR_ORDER, false);
		workerWarehosueRights.put(TransitAuthorities.PLANNER_ORDER, false);
		workerWarehosueRights.put(TransitAuthorities.SUPPLIER, false);
		workerWarehosueRights.put(TransitAuthorities.MANAGER_WAREHOUSE, false);
		workerWarehosueRights.put(TransitAuthorities.WORKER_WAREHOUSE, false);
		
		registrationUserRights.put(TransitAuthorities.ADMIN_GLOBAL, false);
		registrationUserRights.put(TransitAuthorities.OWNER_COMPANY, true);
		registrationUserRights.put(TransitAuthorities.ADMIN_COMPANY, true);
		registrationUserRights.put(TransitAuthorities.CREATOR_ORDER, true);
		registrationUserRights.put(TransitAuthorities.PLANNER_ORDER, true);
		registrationUserRights.put(TransitAuthorities.SUPPLIER, true);
		registrationUserRights.put(TransitAuthorities.MANAGER_WAREHOUSE, true);
		registrationUserRights.put(TransitAuthorities.WORKER_WAREHOUSE, true);
		
		
		rightMatrix.put(TransitAuthorities.ADMIN_GLOBAL, adminGlobalRights);
		rightMatrix.put(TransitAuthorities.OWNER_COMPANY, ownerCompanyRights);
		rightMatrix.put(TransitAuthorities.ADMIN_COMPANY, adminCompanyRights);
		rightMatrix.put(TransitAuthorities.CREATOR_ORDER, creatorOrderRights);
		rightMatrix.put(TransitAuthorities.PLANNER_ORDER, plannerOrderRights);
		rightMatrix.put(TransitAuthorities.SUPPLIER, supplierRights);
		rightMatrix.put(TransitAuthorities.MANAGER_WAREHOUSE, managerWarehouseRights);
		rightMatrix.put(TransitAuthorities.WORKER_WAREHOUSE, workerWarehosueRights);
		rightMatrix.put(TransitAuthorities.REGISTRATION, registrationUserRights);
	}
	
	public boolean canGiveRole(String newRole, List<TransitAuthorities> hasRoles) {
		
		TransitAuthorities newAuthority = TransitAuthorities.getAuthorityForString(newRole);
		
		boolean returnValue = false;
		for (TransitAuthorities role : hasRoles) {
			if (Boolean.TRUE.equals(rightMatrix.get(role).get(newAuthority))) {
				returnValue = true;
			}
		}
		
		return returnValue;
	}
	
	public List<TransitAuthorities> getAuthoritiesForString(List<String> realmRoles) {
		List<TransitAuthorities> hasAuthorities = new ArrayList<>();
		for (String role : realmRoles) {
			hasAuthorities.add(TransitAuthorities.getAuthorityForString(role));
		}
		return hasAuthorities;
	}
	
	
	public boolean checkIfHaveNoTransitRole(UUID keycloakId) {
		var user = this.keycloakServiceManager.getUsersResource().get(keycloakId.toString()).toRepresentation();
		for (String role : TransitAuthorities.ADMIN_COMPANY.getStringValues()) {
			if (user.getRealmRoles().contains(role)) {
				return false;
			}
		}
		return true;
	}
	
}
