package com.transit.backend.config;


import com.transit.backend.config.mail.EmailService;
import com.transit.backend.datalayers.domain.*;
import com.transit.backend.datalayers.repository.*;
import com.transit.backend.rightlayers.service.RightsManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.transit.backend.config.Constants.EMAIL_STRING;
import static com.transit.backend.config.Constants.UNREAD_MESSAGE_NOTIFICATION_TIME;
import static com.transit.backend.config.mail.EmailConstants.DEFAULT_SUBJECT;

@Component
@Slf4j
public class DefaultCompanyPropertyRunner implements CommandLineRunner {
	
	@Autowired
	private GlobalCompanyPropertiesRepository globalCompanyPropertiesRepository;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private CompanyPropertyRepository companyPropertyRepository;
	
	@Autowired
	private RightsManageService rightsManageService;
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private DefaultSharingRightsRepository defaultSharingRightsRepository;
	
	@Override
	public void run(String... args) throws Exception {
		try {
			GlobalCompanyProperties property = new GlobalCompanyProperties();
			property.setName("default");
			property.setType("default");
			property.setCreatedBy("default");
			property.setLastModifiedBy("default");
			
			
			var opt = globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(property.getName());
			if (opt.isEmpty()) {
				globalCompanyPropertiesRepository.saveAndFlush(property);
			}
			
			GlobalCompanyProperties propertyEmail = new GlobalCompanyProperties();
			propertyEmail.setName(EMAIL_STRING);
			propertyEmail.setType("text");
			propertyEmail.setCreatedBy("default");
			propertyEmail.setLastModifiedBy("default");
			
			var opt2 = globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(propertyEmail.getName());
			if (opt2.isEmpty()) {
				globalCompanyPropertiesRepository.saveAndFlush(propertyEmail);
			}
			
			GlobalCompanyProperties propertyUnread = new GlobalCompanyProperties();
			propertyUnread.setName(UNREAD_MESSAGE_NOTIFICATION_TIME);
			propertyUnread.setType("number");
			propertyUnread.setCreatedBy("default");
			propertyUnread.setLastModifiedBy("default");
			
			var opt3 = globalCompanyPropertiesRepository.findGlobalCompanyPropertiesByName(propertyUnread.getName());
			if (opt3.isEmpty()) {
				globalCompanyPropertiesRepository.saveAndFlush(propertyUnread);
			}
			
			var result = companyRepository.findAll();
			for (Company comp : result) {
				var props = companyPropertyRepository.findCompanyPropertiesByCompanyId(comp.getId());
				boolean defaultInside = false;
				for (var prop : props) {
					if (prop.getKey().equals(property.getName())) {
						defaultInside = true;
					}
				}
				if (!defaultInside) {
					CompanyProperty prop = new CompanyProperty();
					prop.setCompany(comp);
					prop.setCreateDate(comp.getCreateDate());
					prop.setModifyDate(comp.getModifyDate());
					prop.setCreatedBy(comp.getCreatedBy());
					prop.setLastModifiedBy(comp.getLastModifiedBy());
					prop.setKey(property.getName());
					prop.setValue(property.getName());
					prop.setType(property.getType());
					Optional<User> user = Optional.empty();
					var output = companyPropertyRepository.saveAndFlush(prop);
					var tempUsers = userRepository.findAllByKeycloakEmail(comp.getCreatedBy());
					AtomicReference<User> tempUser = new AtomicReference<>(new User());
					AtomicReference<OffsetDateTime> tempUserCREATE = new AtomicReference<>(OffsetDateTime.MIN);
					tempUsers.forEach(userFor -> {
						if (tempUser.get().getId() != null) {
							if (tempUser.get().getCreateDate().isAfter(tempUserCREATE.get())) {
								tempUser.set(userFor);
								tempUserCREATE.set(userFor.getCreateDate());
							}
						} else {
							tempUser.set(userFor);
							tempUserCREATE.set(userFor.getCreateDate());
						}
					});
					if (tempUser.get() != null) {
						user = Optional.of(tempUser.get());
					}
					
					try {
						user.ifPresent(value -> rightsManageService.createEntityAndConnectIt(output.getId(), CompanyProperty.class.getSimpleName(), CompanyProperty.class, value.getId()));
					} catch (Exception ex) {
					
					}
				}
				
				if (!defaultSharingRightsRepository.existsById(comp.getId())) {
					var companyDefaultSharProperties = new DefaultSharingRights();
					companyDefaultSharProperties.setId(comp.getId());
					companyDefaultSharProperties.setDefaultSharingRights("");
					companyDefaultSharProperties.setCreatedBy(comp.getCreatedBy());
					companyDefaultSharProperties.setLastModifiedBy(comp.getLastModifiedBy());
					defaultSharingRightsRepository.saveAndFlush(companyDefaultSharProperties);
				}
				
				
				defaultInside = false;
				for (var prop : props) {
					if (prop.getKey().equals(propertyUnread.getName())) {
						defaultInside = true;
					}
				}
				if (!defaultInside) {
					CompanyProperty prop = new CompanyProperty();
					prop.setCompany(comp);
					prop.setCreateDate(comp.getCreateDate());
					prop.setModifyDate(comp.getModifyDate());
					prop.setCreatedBy(comp.getCreatedBy());
					prop.setLastModifiedBy(comp.getLastModifiedBy());
					prop.setKey(propertyUnread.getName());
					prop.setValue(String.valueOf(60L));
					prop.setType(propertyUnread.getType());
					Optional<User> user = Optional.empty();
					var output = companyPropertyRepository.saveAndFlush(prop);
					var tempUsers = userRepository.findAllByKeycloakEmail(comp.getCreatedBy());
					AtomicReference<User> tempUser = new AtomicReference<>(new User());
					AtomicReference<OffsetDateTime> tempUserCREATE = new AtomicReference<>(OffsetDateTime.MIN);
					tempUsers.forEach(userFor -> {
						if (tempUser.get().getId() != null) {
							if (tempUser.get().getCreateDate().isAfter(tempUserCREATE.get())) {
								tempUser.set(userFor);
								tempUserCREATE.set(userFor.getCreateDate());
							}
						} else {
							tempUser.set(userFor);
							tempUserCREATE.set(userFor.getCreateDate());
						}
					});
					if (tempUser.get() != null) {
						user = Optional.of(tempUser.get());
					}
					
					try {
						user.ifPresent(value -> rightsManageService.createEntityAndConnectIt(output.getId(), CompanyProperty.class.getSimpleName(), CompanyProperty.class, value.getId()));
					} catch (Exception ex) {
					
					}
				}
				
			}
//		log.error("updateOrderProperties");
//		orderPropertyRepository.findAll().stream().map(orderProperty -> {
//			try {
//				var re = rightsService.readOne(orderProperty.getId());
//				if (re.isEmpty()) {
//					rightsManageService.createEntityAndConnectIt(orderProperty.getId(), OrderProperty.class.getSimpleName(), OrderProperty.class, userRepository.findByKeycloakEmail(orderProperty.getCreatedBy()).get().getId());
//
//				}
//			} catch (Exception e) {
//				rightsManageService.createEntityAndConnectIt(orderProperty.getId(), OrderProperty.class.getSimpleName(), OrderProperty.class, userRepository.findByKeycloakEmail(orderProperty.getCreatedBy()).get().getId());
//
//
//			}
//			return orderProperty;
//		});
			
			
			emailService.sendSimpleMessage("koch@infai.org", DEFAULT_SUBJECT, "Start Nachricht von Backend");
		} catch (Exception ex) {
		
		}
	}
}
