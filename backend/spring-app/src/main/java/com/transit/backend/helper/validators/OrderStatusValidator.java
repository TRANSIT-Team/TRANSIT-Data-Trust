package com.transit.backend.helper.validators;


import com.transit.backend.datalayers.domain.enums.OrderStatus;
import com.transit.backend.helper.contraints.OrderStatusConstraint;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

@Component
public class OrderStatusValidator implements ConstraintValidator<OrderStatusConstraint, String> {
	
	private List<String> values;
	
	@Override
	public void initialize(final OrderStatusConstraint constraintAnnotation) {
		
		values = Arrays.stream(OrderStatus.values()).map(Enum::name).toList();
	}
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return values.contains(value);
	}
	
}
