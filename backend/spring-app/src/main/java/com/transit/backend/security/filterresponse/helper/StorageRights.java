package com.transit.backend.security.filterresponse.helper;


import com.transit.backend.rightlayers.domain.AccessResponseDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Data
@Component
public class StorageRights {
	
	private Map<UUID, Optional<AccessResponseDTO>> rightsForResponse;
	
	
}
