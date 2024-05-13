package com.transit.backend.transferentities;

import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyAddress;
import com.transit.backend.datalayers.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@Data
@AllArgsConstructor
public class UserRegistrationObject {
	private User user;
	private UserRepresentation userRepresentation;
	private Company company;
	private List<CompanyAddress> companyAddresses;
}
