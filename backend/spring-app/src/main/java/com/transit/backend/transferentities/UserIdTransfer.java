package com.transit.backend.transferentities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserIdTransfer {
	private UUID userId;
}
