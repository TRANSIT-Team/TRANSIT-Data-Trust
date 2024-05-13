package com.transit.backend.helper.validators;

import com.programmingskillz.zipcode.ZipCodeUtil;
import com.transit.backend.datalayers.controller.dto.AddressDTO;
import com.transit.backend.datalayers.controller.dto.CompanyAddressDTO;
import com.transit.backend.datalayers.controller.dto.registration.CompanyAddressDTORegistration;
import com.transit.backend.helper.contraints.ZipCodeConstraint;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.requireNonNull;

@Component
public class ZipCodeAddressValidator implements ConstraintValidator<ZipCodeConstraint, Object> {
	//Prüfe ob geladen
	private ZipCodeUtil zipCodeUtil;
	
	@Override
	public void initialize(final ZipCodeConstraint constraintAnnotation) {
		
		zipCodeUtil = ZipCodeUtil.getInstance();
	}
	
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		try {
			if (value == null) {
				return false;
			} else if (value instanceof CompanyAddressDTO comAddress) {
				return zipCodeUtil.isValid(requireNonNull(comAddress.getIsoCode()), requireNonNull(comAddress.getZip()));
			} else if (value instanceof CompanyAddressDTORegistration comAddressReg) {
				return zipCodeUtil.isValid(requireNonNull(comAddressReg.getIsoCode()), requireNonNull(comAddressReg.getZip()));
			} else if (value instanceof AddressDTO address) {
				return zipCodeUtil.isValid(requireNonNull(address.getIsoCode()), requireNonNull(address.getZip()));
			}
		} catch (Exception exp) {
			return false;
		}
		return false;
	}
	
}
