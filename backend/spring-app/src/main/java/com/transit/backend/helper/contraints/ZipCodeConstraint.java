package com.transit.backend.helper.contraints;

import com.transit.backend.helper.validators.ZipCodeAddressValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ZipCodeAddressValidator.class)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ZipCodeConstraint {
	
	String message() default "ZipCode Validation failed for zip and isoCode.";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
	
}
