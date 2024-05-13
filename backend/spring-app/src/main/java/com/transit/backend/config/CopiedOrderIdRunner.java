package com.transit.backend.config;

import com.transit.backend.datalayers.service.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CopiedOrderIdRunner implements CommandLineRunner {
	
	@Autowired
	private OrderHelperStartup orderHelperStartup;
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Override
	public void run(String... args) throws Exception {
		
		try {
			
			orderHelperStartup.loadOrders().forEach(o -> {
				orderHelperStartup.saveOrder(o);
			});
		} catch (Exception ex) {
		
		}
		
		
	}
	
	
}
