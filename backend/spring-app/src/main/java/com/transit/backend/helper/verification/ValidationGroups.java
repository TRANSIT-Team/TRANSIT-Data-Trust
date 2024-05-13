package com.transit.backend.helper.verification;

import javax.validation.groups.Default;

public interface ValidationGroups {
	interface Post extends Default {
	}
	
	interface Put extends Default {
	}
	
	interface Patch extends Default {
	}
}