package com.transit.backend.config.mail;

import com.transit.backend.datalayers.repository.CompanyPropertyRepository;
import com.transit.backend.datalayers.repository.CompanyRepository;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.datalayers.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import javax.mail.MessagingException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.transit.backend.config.mail.EmailConstants.DEFAULT_SUBJECT;
import static com.transit.backend.config.mail.EmailConstants.companyEmailProperty;

@Component
@Slf4j
public class SendMail {
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private CompanyPropertyRepository companyPropertyRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public void sendMailForQuestion(UUID id) throws MessagingException {
		final MainDataForMail defaultDataForMail = getMainDataForMail(id, null);
		companyPropertyRepository.findCompanyPropertiesByCompanyId(defaultDataForMail.subCompany().getId()).forEach(prop -> {
			if (prop.getKey().equals(companyEmailProperty)) {
				try {
					emailService.sendEmailRequest(defaultDataForMail.finalSub_company_name(), defaultDataForMail.finalParent_company_name(), defaultDataForMail.finalLink_to_order(), DEFAULT_SUBJECT, prop.getValue(), defaultDataForMail.destinationZip(), defaultDataForMail.destinationPlace());
					defaultDataForMail.alreadysend().set(true);
				} catch (MessagingException e) {
					log.error(e.getMessage());
				}
			}
		});
		if (!defaultDataForMail.alreadysend().get()) {
			userRepository.findAllByCompanyId(defaultDataForMail.subCompany().getId()).forEach(user -> {
				try {
					emailService.sendEmailRequest(defaultDataForMail.finalSub_company_name(), defaultDataForMail.finalParent_company_name(), defaultDataForMail.finalLink_to_order(), DEFAULT_SUBJECT, user.getKeycloakEmail(), defaultDataForMail.destinationZip(), defaultDataForMail.destinationPlace());
				} catch (MessagingException e) {
					log.error(e.getMessage());
				}
			});
		}
	}
	
	public void sendMailForResendQuestion(UUID id, UriComponents uriComponents) throws MessagingException {
		final MainDataForMail defaultDataForMail = getMainDataForMail(id, uriComponents);
		companyPropertyRepository.findCompanyPropertiesByCompanyId(defaultDataForMail.subCompany().getId()).forEach(prop -> {
			if (prop.getKey().equals(companyEmailProperty)) {
				try {
					emailService.sendEmailResendRequest(defaultDataForMail.finalSub_company_name(), defaultDataForMail.finalParent_company_name(), defaultDataForMail.finalLink_to_order(), DEFAULT_SUBJECT, prop.getValue(), defaultDataForMail.destinationZip(), defaultDataForMail.destinationPlace());
					defaultDataForMail.alreadysend().set(true);
				} catch (MessagingException e) {
					log.error(e.getMessage());
				}
			}
		});
		if (!defaultDataForMail.alreadysend().get()) {
			userRepository.findAllByCompanyId(defaultDataForMail.subCompany().getId()).forEach(user -> {
				try {
					emailService.sendEmailResendRequest(defaultDataForMail.finalSub_company_name(), defaultDataForMail.finalParent_company_name(), defaultDataForMail.finalLink_to_order(), DEFAULT_SUBJECT, user.getKeycloakEmail(), defaultDataForMail.destinationZip(), defaultDataForMail.destinationPlace());
				} catch (MessagingException e) {
					log.error(e.getMessage());
				}
			});
		}
	}
	
	@NotNull
	private MainDataForMail getMainDataForMail(UUID id, UriComponents uriComponents) throws MessagingException {
		return getMainDataForMail(id, false, uriComponents);
	}
	
	@NotNull
	private MainDataForMail getMainDataForMail(UUID id, boolean canceled, UriComponents uriComponents) throws MessagingException {
		String sub_company_name;
		String parent_company_name;
		var order = orderRepository.findById(id).get();
		var subCompany = companyRepository.findById(order.getCompany().getId()).get();
		sub_company_name = subCompany.getName();
		var parentCompany = companyRepository.findById(order.getParentOrder().getCompany().getId()).get();
		parent_company_name = parentCompany.getName();
		if (uriComponents == null) {
			var servletBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
			var requestOpt = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
					.filter(ServletRequestAttributes.class::isInstance)
					.map(ServletRequestAttributes.class::cast)
					.map(ServletRequestAttributes::getRequest);
			if (requestOpt.isEmpty()) {
				throw new MessagingException("Cannot send Email.");
			}
			var request = requestOpt.get();
			
			uriComponents = servletBuilder.build();
		}
		String link_to_order;
		
		if (uriComponents.getHost().contains("api.transit-project")) {
			link_to_order = "https://app.transit-project.de/orders/order/" + id;
			
			
		} else {
			link_to_order = "http://localhost:4200/orders/order/" + id;
		}
		AtomicBoolean alreadysend = new AtomicBoolean(false);
		
		String finalSub_company_name = sub_company_name;
		String finalParent_company_name = parent_company_name;
		String finalLink_to_order = link_to_order;
		String destinationZip = order.getAddressTo().getZip();
		String destinationPlace = order.getAddressTo().getCity();
		String reasonToCancel = order.getReasonForCancel();
		
		return new MainDataForMail(subCompany, parentCompany, alreadysend, finalSub_company_name, finalParent_company_name, finalLink_to_order, destinationZip, destinationPlace, reasonToCancel);
	}
	
	public void sendMailForRevoked(UUID id) throws MessagingException {
		final MainDataForMail defaultDataForMail = getMainDataForMail(id, null);
		companyPropertyRepository.findCompanyPropertiesByCompanyId(defaultDataForMail.subCompany().getId()).forEach(prop -> {
			if (prop.getKey().equals(companyEmailProperty)) {
				try {
					emailService.sendEmailRevoked(defaultDataForMail.finalSub_company_name, defaultDataForMail.finalParent_company_name, DEFAULT_SUBJECT, prop.getValue(), defaultDataForMail.destinationZip(), defaultDataForMail.destinationPlace());
					defaultDataForMail.alreadysend.set(true);
				} catch (MessagingException e) {
					log.error(e.getMessage());
				}
			}
		});
		if (!defaultDataForMail.alreadysend.get()) {
			userRepository.findAllByCompanyId(defaultDataForMail.subCompany().getId()).forEach(user -> {
				try {
					emailService.sendEmailRevoked(defaultDataForMail.finalSub_company_name, defaultDataForMail.finalParent_company_name, DEFAULT_SUBJECT, user.getKeycloakEmail(), defaultDataForMail.destinationZip(), defaultDataForMail.destinationPlace());
				} catch (MessagingException e) {
					log.error(e.getMessage());
				}
			});
		}
	}
	
	public void sendMailForAcceptedToOpen(UUID id) throws MessagingException {
		final MainDataForMail defaultDataForMail = getMainDataForMail(id, null);
		companyPropertyRepository.findCompanyPropertiesByCompanyId(defaultDataForMail.subCompany().getId()).forEach(prop -> {
			if (prop.getKey().equals(companyEmailProperty)) {
				try {
					emailService.sendEmailAcceptedToOpen(defaultDataForMail.finalSub_company_name, defaultDataForMail.finalParent_company_name, defaultDataForMail.finalLink_to_order, DEFAULT_SUBJECT, prop.getValue(), defaultDataForMail.destinationZip(), defaultDataForMail.destinationPlace());
					defaultDataForMail.alreadysend.set(true);
				} catch (MessagingException e) {
					log.error(e.getMessage());
				}
			}
		});
		if (!defaultDataForMail.alreadysend.get()) {
			userRepository.findAllByCompanyId(defaultDataForMail.subCompany().getId()).forEach(user -> {
				try {
					emailService.sendEmailAcceptedToOpen(defaultDataForMail.finalSub_company_name, defaultDataForMail.finalParent_company_name, defaultDataForMail.finalLink_to_order, DEFAULT_SUBJECT, user.getKeycloakEmail(), defaultDataForMail.destinationZip(), defaultDataForMail.destinationPlace());
				} catch (MessagingException e) {
					log.error(e.getMessage());
				}
			});
		}
	}
	
	public void sendMailForMessageCenter(UUID companyId) throws MessagingException {
		
		var linkToMessages = "https://app.transit-project.de/chat";
		var comp = companyRepository.findById(companyId).get();
		AtomicBoolean alreadysend = new AtomicBoolean(false);
		companyPropertyRepository.findCompanyPropertiesByCompanyId(comp.getId()).forEach(prop -> {
			if (prop.getKey().equals(companyEmailProperty)) {
				try {
					emailService.sendEmailNewMessages(comp.getName(), DEFAULT_SUBJECT, prop.getValue(), linkToMessages);
					alreadysend.set(true);
				} catch (MessagingException e) {
					log.error(e.getMessage());
				}
			}
		});
		if (!alreadysend.get()) {
			
			userRepository.findAllByCompanyId(companyId).forEach(user -> {
				try {
					emailService.sendEmailNewMessages(comp.getName(), DEFAULT_SUBJECT, user.getKeycloakEmail(), linkToMessages);
				} catch (MessagingException e) {
					log.error(e.getMessage());
				}
			});
		}
	}
	
	public void sendMailForCanceledSuborder(UUID id) throws MessagingException {
		final MainDataForMail defaultDataForMail = getMainDataForMail(id, null);
		defaultDataForMail.parentCompany.getCompanyProperties().forEach(prop -> {
			if (prop.getKey().equals(companyEmailProperty)) {
				try {
					emailService.sendEmailToCANCELED(defaultDataForMail.finalSub_company_name, defaultDataForMail.finalParent_company_name, defaultDataForMail.finalLink_to_order, DEFAULT_SUBJECT, prop.getValue(), defaultDataForMail.destinationZip(), defaultDataForMail.destinationPlace(), defaultDataForMail.reasonToCancel());
					defaultDataForMail.alreadysend.set(true);
				} catch (MessagingException e) {
					log.error(e.getMessage());
				}
			}
		});
		if (!defaultDataForMail.alreadysend.get()) {
			defaultDataForMail.parentCompany.getCompanyUsers().forEach(user -> {
				try {
					emailService.sendEmailToCANCELED(defaultDataForMail.finalSub_company_name, defaultDataForMail.finalParent_company_name, defaultDataForMail.finalLink_to_order, DEFAULT_SUBJECT, user.getKeycloakEmail(), defaultDataForMail.destinationZip(), defaultDataForMail.destinationPlace(), defaultDataForMail.reasonToCancel);
				} catch (MessagingException e) {
					log.error(e.getMessage());
				}
			});
		}
	}
	
	private record MainDataForMail(com.transit.backend.datalayers.domain.Company subCompany,
	                               
	                               com.transit.backend.datalayers.domain.Company parentCompany,
	                               AtomicBoolean alreadysend,
	                               String finalSub_company_name, String finalParent_company_name,
	                               String finalLink_to_order, String destinationZip, String destinationPlace,
	                               String reasonToCancel) {
	}
	
	
}
