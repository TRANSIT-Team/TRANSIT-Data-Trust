package com.transit.backend.helper;

import com.transit.backend.config.mail.SendMail;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.repository.ChatEntryRepository;
import com.transit.backend.datalayers.repository.CompanyPropertyRepository;
import com.transit.backend.datalayers.repository.CompanyRepository;
import com.transit.backend.datalayers.service.ChatEntryService;
import com.transit.backend.security.preauthaccess.GetFilterExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicBoolean;


import static com.transit.backend.config.Constants.UNREAD_MESSAGE_NOTIFICATION_TIME;

@Component
@Slf4j
public class MessageEmail {
	
	
	@Autowired
	private ChatEntryService chatEntryService;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private GetFilterExpression getFilterExpression;
	
	
	@Autowired
	private SendMail sendMail;
	
	@Autowired
	private ChatEntryRepository chatEntryRepository;
	
	@Autowired
	private CompanyPropertyRepository companyPropertyRepository;
	
	@Scheduled(fixedDelay = 30000)
	public void sendMessageForNewMessage() {
		//var isBefore = OffsetDateTime.now().minusHours(1);
		
		companyRepository.findAll().forEach(comp -> {
			var compProperties = companyPropertyRepository.findCompanyPropertiesByCompanyId(comp.getId());
			var time = Long.valueOf(compProperties.stream().filter(pr -> pr.getKey().equals(UNREAD_MESSAGE_NOTIFICATION_TIME)).findFirst().get().getValue());
			var isBefore = OffsetDateTime.now().minusMinutes(time);
			
			var chatEntries = chatEntryService.readFilter(PageRequest.of(0, Integer.MAX_VALUE), getFilterExpression.overwriteQueryWithEntityIdWithURINumber("", null, false, comp.getId(), Order.class.getSimpleName()));
			AtomicBoolean haveToSendMail = new AtomicBoolean(false);
			
			chatEntries.forEach(chatEntry -> {
				if (!chatEntry.isReadStatus() && chatEntry.getCreateDate().isBefore(isBefore)) {
					if (!chatEntry.isEmailSendAlready()) {
						if (!chatEntry.getCompanyId().equals(comp.getId())) {
							haveToSendMail.set(true);
						}
					}
				}
			});
			if (haveToSendMail.get()) {
				try {
					sendMail.sendMailForMessageCenter(comp.getId());
					chatEntries.forEach(chatEntry -> {
						if (!chatEntry.isReadStatus() && chatEntry.getCreateDate().isBefore(isBefore)) {
							chatEntry.setEmailSendAlready(true);
							chatEntryRepository.saveAndFlush(chatEntry);
						}
					});
					
				} catch (MessagingException e) {
					log.error("Failed to send Message." + e.getMessage());
				}
			}
			
			
		});
		
		
	}
}
