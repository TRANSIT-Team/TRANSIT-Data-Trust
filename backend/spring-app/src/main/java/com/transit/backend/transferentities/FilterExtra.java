package com.transit.backend.transferentities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterExtra {
	private Pageable pageable;
	private int skip;
	
	private int take;
	
	private boolean useOtherParameters;
	
	private boolean isPage;
	private boolean noCompanyAddress;
	
	private boolean createdByMyCompany;
}
