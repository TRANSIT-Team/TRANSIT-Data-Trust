package com.transit.backend.helper;

import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.transferentities.FilterExtra;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.transit.backend.config.FilterExtraConstants.No_COMPANY_ADDRESS;


@Component
public class GetFilterExtra {
	
	public static FilterExtra getPageableExtras(Pageable pageable, int skip, int take, String FilterExtra) {
		FilterExtra extras = new FilterExtra();
		extras.setPageable(pageable);
		extras.setSkip(skip);
		extras.setTake(take);
		extras.setPage(true);
		if (take < 1 || skip < 0) {
			extras.setUseOtherParameters(false);
			return extras;
		}
		
		extras.setUseOtherParameters(true);
		extras.setNoCompanyAddress(getNoCompanyAddress(FilterExtra));
		return extras;
	}
	
	private static boolean getNoCompanyAddress(String FilterExtra) {
		AtomicBoolean noCompanyAddress = new AtomicBoolean(false);
		
		try {
			
			if (FilterExtra.isBlank()) {
				return false;
			}
			String[] parts = FilterExtra.split("&");
			Arrays.stream(parts).forEach(part -> {
				String[] partPart = part.split("==");
				if (partPart[0].equals(No_COMPANY_ADDRESS)) {
					noCompanyAddress.set(Boolean.parseBoolean(partPart[1]));
				}
			});
		} catch (Exception ex) {
			throw new UnprocessableEntityExeption("Cannot parse FilterExtra String: \n" + FilterExtra);
		}
		return noCompanyAddress.get();
	}
	
	public static FilterExtra getCollectionFilterExtra(int skip, int take, String FilterExtra, boolean createdByMyCompany) {
		FilterExtra extras = new FilterExtra();
		extras.setPageable(Pageable.unpaged());
		extras.setPage(false);
		extras.setSkip(skip);
		extras.setTake(take);
		if (take < 1 || skip < 0) {
			extras.setUseOtherParameters(false);
			extras.setPage(true);
			return extras;
		}
		extras.setUseOtherParameters(true);
		extras.setNoCompanyAddress(getNoCompanyAddress(FilterExtra));
		extras.setCreatedByMyCompany(createdByMyCompany);
		return extras;
	}
	
	public static FilterExtra getEmptyFilterExtra() {
		FilterExtra extras = new FilterExtra();
		extras.setPageable(Pageable.unpaged());
		extras.setPage(false);
		extras.setSkip(-1);
		extras.setTake(0);
		extras.setUseOtherParameters(false);
		extras.setNoCompanyAddress(false);
		return extras;
	}
	
	
}
