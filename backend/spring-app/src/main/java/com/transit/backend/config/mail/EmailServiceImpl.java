package com.transit.backend.config.mail;


import io.rocketbase.mail.EmailTemplateBuilder;
import io.rocketbase.mail.model.HtmlTextEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

import static com.transit.backend.config.mail.EmailConstants.DEFAULT_SENDING_FROM;

@Component
public class EmailServiceImpl implements EmailService {
	
	
	@Autowired
	private JavaMailSender emailSender;
	
	
	@Autowired
	private SpringTemplateEngine thymeleafTemplateEngine;
	
	
	@Value("classpath:/mail-logo.png")
	private Resource resourceFile;
	
	public void sendSimpleMessage(String to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(DEFAULT_SENDING_FROM);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			
			emailSender.send(message);
		} catch (MailException exception) {
			exception.printStackTrace();
		}
	}
	
	
	@Override
	public void sendEmailRequest(String subCompanyName, String parentCompanyName, String linkToOrder, String subject, String to, String destinationZip, String destinationPlace) throws MessagingException {
		HtmlTextEmail htmlTextEmail = EmailTemplateBuilder.builder()
				.header()
				.logo("https://app.transit-project.de/assets/img/transit-logo.png")
				.logoWidth(100)
				.and()
				.text("Guten Tag " + subCompanyName + ",").h1().center().and()
				.text("Auf der Transit-Projekt Plattform wurde Ihnen ein neuer Auftrag angefragt.").center().and()
				.text("Der Auftraggeber ist die " + parentCompanyName + ".").center().and()
				.text("Die Zieladresse befindet sich in " + destinationZip + " " + destinationPlace + ".").center().and()
				.button("Auftrag ansehen", linkToOrder).blue()
				.and()
				.text("Mit freundlichen Grüßen").center().and()
				.text("Ihr Transit Project Team").center().and()
				.copyright("Professur für Informationsmanagement - IWI - Universität Leipzig").url("https://www.wifa.uni-leipzig.de/institut-fuer-wirtschaftsinformatik/professuren/informationsmanagement").suffix(".").and()
				.footerText("Institut für Wirtschaftsinformatik – Universität Leipzig\n" +
						"Grimmaische Straße 12 \n" +
						"04109 Leipzig").and()
				.footerImage("https://app.transit-project.de/assets/img/transit-logo.png").width(100).linkUrl("https://transit-project.de").and()
				.footerImage("https://transit-project.de/wp-content/uploads/2022/01/BMBF-Logo-2-300x213.png").width(100).linkUrl("https://www.bmbf.de").and()
				.footerText("Förderkennzeichen: 16DTM109A-C").center().and()
				.footerImage("https://transit-project.de/wp-content/uploads/2023/05/DE-Finanziert-von-der-Europaeischen-Union_POS-1024x265.png").width(100).and()
				.build();

// sent email
		sendMail(subject, to, htmlTextEmail);
	}
	
	@Override
	public void sendEmailResendRequest(String subCompanyName, String parentCompanyName, String linkToOrder, String subject, String to, String destinationZip, String destinationPlace) throws MessagingException {
		HtmlTextEmail htmlTextEmail = EmailTemplateBuilder.builder()
				.header()
				.logo("https://app.transit-project.de/assets/img/transit-logo.png")
				.logoWidth(100)
				.and()
				.text("Guten Tag " + subCompanyName + ",").h1().center().and()
				.text("Auf der Transit-Projekt Plattform wurde Ihnen ein Auftrag erneut angefragt.").center().and()
				.text("Der Auftraggeber ist die " + parentCompanyName + ".").center().and()
				.text("Die Zieladresse befindet sich in " + destinationZip + " " + destinationPlace + ".").center().and()
				.button("Auftrag ansehen", linkToOrder).blue()
				.and()
				.text("Mit freundlichen Grüßen").center().and()
				.text("Ihr Transit Project Team").center().and()
				.copyright("Professur für Informationsmanagement - IWI - Universität Leipzig").url("https://www.wifa.uni-leipzig.de/institut-fuer-wirtschaftsinformatik/professuren/informationsmanagement").suffix(".").and()
				.footerText("Institut für Wirtschaftsinformatik – Universität Leipzig\n" +
						"Grimmaische Straße 12 \n" +
						"04109 Leipzig").and()
				.footerImage("https://app.transit-project.de/assets/img/transit-logo.png").width(100).linkUrl("https://transit-project.de").and()
				.footerImage("https://transit-project.de/wp-content/uploads/2022/01/BMBF-Logo-2-300x213.png").width(100).linkUrl("https://www.bmbf.de").and()
				.footerText("Förderkennzeichen: 16DTM109A-C").center().and()
				.footerImage("https://transit-project.de/wp-content/uploads/2023/05/DE-Finanziert-von-der-Europaeischen-Union_POS-1024x265.png").width(100).and()
				.build();

// sent email
		sendMail(subject, to, htmlTextEmail);
	}
	
	
	@Override
	public void sendEmailRevoked(String subCompanyName, String parentCompanyName, String subject, String to, String destinationZip, String destinationPlace) throws MessagingException {
		HtmlTextEmail htmlTextEmail = EmailTemplateBuilder.builder()
				.header()
				.logo("https://app.transit-project.de/assets/img/transit-logo.png")
				.logoWidth(100)
				.and()
				.text("Guten Tag " + subCompanyName + ",").h1().center().and()
				.text("Auf der Transit-Projekt Plattform wurde ein angefragter Auftrag widerrufen.").center().and()
				.text("Der Auftraggeber war die " + parentCompanyName + ".").center().and()
				.text("Die Zieladresse befand sich in " + destinationZip + " " + destinationPlace + ".").center().and()
				.text("Mit freundlichen Grüßen").center().and()
				.text("Ihr Transit Project Team").center().and()
				.copyright("Professur für Informationsmanagement - IWI - Universität Leipzig").url("https://www.wifa.uni-leipzig.de/institut-fuer-wirtschaftsinformatik/professuren/informationsmanagement").suffix(".").and()
				.footerText("Institut für Wirtschaftsinformatik – Universität Leipzig\n" +
						"Grimmaische Straße 12 \n" +
						"04109 Leipzig").and()
				.footerImage("https://app.transit-project.de/assets/img/transit-logo.png").width(100).linkUrl("https://transit-project.de").and()
				.footerImage("https://transit-project.de/wp-content/uploads/2022/01/BMBF-Logo-2-300x213.png").width(100).linkUrl("https://www.bmbf.de").and()
				.footerText("Förderkennzeichen: 16DTM109A-C").center().and()
				.footerImage("https://transit-project.de/wp-content/uploads/2023/05/DE-Finanziert-von-der-Europaeischen-Union_POS-1024x265.png").width(100).and()
				.build();
// sent email
		sendMail(subject, to, htmlTextEmail);
	}
	
	@Override
	public void sendEmailAcceptedToOpen(String subCompanyName, String parentCompanyName, String linkToOrder, String subject, String to, String destinationZip, String destinationPlace) throws MessagingException {
		HtmlTextEmail htmlTextEmail = EmailTemplateBuilder.builder()
				.header()
				.logo("https://app.transit-project.de/assets/img/transit-logo.png")
				.logoWidth(100)
				.and()
				.text("Guten Tag " + subCompanyName + ",").h1().center().and()
				.text("Auf der Transit-Projekt Plattform wurde Ihnen ein Auftrag zugeteilt.").center().and()
				.text("Sie wurden ausgewählt den entsprechenden Auftrag auszuführen.").and()
				.text("Der Auftraggeber ist die " + parentCompanyName + ".").center().and()
				.text("Die Zieladresse befindet sich in " + destinationZip + " " + destinationPlace + ".").center().and()
				.button("Auftrag ansehen", linkToOrder).blue()
				.and()
				.text("Mit freundlichen Grüßen").center().and()
				.text("Ihr Transit Project Team").center().and()
				.copyright("Professur für Informationsmanagement - IWI - Universität Leipzig").url("https://www.wifa.uni-leipzig.de/institut-fuer-wirtschaftsinformatik/professuren/informationsmanagement").suffix(".").and()
				.footerText("Institut für Wirtschaftsinformatik – Universität Leipzig\n" +
						"Grimmaische Straße 12 \n" +
						"04109 Leipzig").and()
				.footerImage("https://app.transit-project.de/assets/img/transit-logo.png").width(100).linkUrl("https://transit-project.de").and()
				.footerImage("https://transit-project.de/wp-content/uploads/2022/01/BMBF-Logo-2-300x213.png").width(100).linkUrl("https://www.bmbf.de").and()
				.footerText("Förderkennzeichen: 16DTM109A-C").center().and()
				.footerImage("https://transit-project.de/wp-content/uploads/2023/05/DE-Finanziert-von-der-Europaeischen-Union_POS-1024x265.png").width(100).and()
				.build();

// sent email
		sendMail(subject, to, htmlTextEmail);
	}
	
	@Override
	public void sendEmailToCANCELED(String subCompanyName, String parentCompanyName, String linkToOrder, String subject, String to, String destinationZip, String destinationPlace, String reasonToCancel) throws MessagingException {
		
		HtmlTextEmail htmlTextEmail = null;
		
		if (reasonToCancel != null && !reasonToCancel.isBlank()) {
			htmlTextEmail = EmailTemplateBuilder.builder()
					.header()
					.logo("https://app.transit-project.de/assets/img/transit-logo.png")
					.logoWidth(100)
					.and()
					.text("Guten Tag " + parentCompanyName + ",").h1().center().and()
					.text("Auf der Transit-Projekt Plattform wurde ein Auftrag von einer beauftragten Firma auf abgebrochen gesetzt.").center().and()
					.text("Der Auftragnehmer ist die " + subCompanyName + ".").center().and()
					.text("Die Zieladresse des Auftrages befand sich in " + destinationZip + " " + destinationPlace + ".").center().and()
					.text("Folgender Grund zum Abbrechen wurde angegeben: \n" + reasonToCancel).center().and()
					.button("Auftrag ansehen", linkToOrder).blue()
					.and()
					.text("Mit freundlichen Grüßen").center().and()
					.text("Ihr Transit Project Team").center().and()
					.copyright("Professur für Informationsmanagement - IWI - Universität Leipzig").url("https://www.wifa.uni-leipzig.de/institut-fuer-wirtschaftsinformatik/professuren/informationsmanagement").suffix(".").and()
					.footerText("Institut für Wirtschaftsinformatik – Universität Leipzig\n" +
							"Grimmaische Straße 12 \n" +
							"04109 Leipzig").and()
					.footerImage("https://app.transit-project.de/assets/img/transit-logo.png").width(100).linkUrl("https://transit-project.de").and()
					.footerImage("https://transit-project.de/wp-content/uploads/2022/01/BMBF-Logo-2-300x213.png").width(100).linkUrl("https://www.bmbf.de").and()
					.footerText("Förderkennzeichen: 16DTM109A-C").center().and()
					.footerImage("https://transit-project.de/wp-content/uploads/2023/05/DE-Finanziert-von-der-Europaeischen-Union_POS-1024x265.png").width(100).and()
					.build();
		} else {
			htmlTextEmail = EmailTemplateBuilder.builder()
					.header()
					.logo("https://app.transit-project.de/assets/img/transit-logo.png")
					.logoWidth(100)
					.and()
					.text("Guten Tag " + parentCompanyName + ",").h1().center().and()
					.text("Auf der Transit-Projekt Plattform wurde ein Auftrag von einer beauftragten Firma auf abgebrochen gesetzt").center().and()
					.text("Der Auftragnehmer ist die " + subCompanyName + ".").center().and()
					.text("Die Zieladresse des Auftrages befand sich in " + destinationZip + " " + destinationPlace + ".").center().and()
					.text("Es wurde kein Grund für den Abbruch der Lieferung angegeben").center().and()
					.button("Auftrag ansehen", linkToOrder).blue()
					.and()
					.text("Mit freundlichen Grüßen").center().and()
					.text("Ihr Transit Project Team").center().and()
					.copyright("Professur für Informationsmanagement - IWI - Universität Leipzig").url("https://www.wifa.uni-leipzig.de/institut-fuer-wirtschaftsinformatik/professuren/informationsmanagement").suffix(".").and()
					.footerText("Institut für Wirtschaftsinformatik – Universität Leipzig\n" +
							"Grimmaische Straße 12 \n" +
							"04109 Leipzig").and()
					.footerImage("https://app.transit-project.de/assets/img/transit-logo.png").width(100).linkUrl("https://transit-project.de").and()
					.footerImage("https://transit-project.de/wp-content/uploads/2022/01/BMBF-Logo-2-300x213.png").width(100).linkUrl("https://www.bmbf.de").and()
					.footerText("Förderkennzeichen: 16DTM109A-C").center().and()
					.footerImage("https://transit-project.de/wp-content/uploads/2023/05/DE-Finanziert-von-der-Europaeischen-Union_POS-1024x265.png").width(100).and()
					.build();
		}

// sent email
		sendMail(subject, to, htmlTextEmail);
	}
	
	@Override
	public void sendEmailNewMessages(String subCompanyName, String subject, String to, String linkToMessages) throws MessagingException {
		HtmlTextEmail htmlTextEmail = EmailTemplateBuilder.builder()
				.header()
				.logo("https://app.transit-project.de/assets/img/transit-logo.png")
				.logoWidth(100)
				.and()
				.text("Guten Tag " + subCompanyName + ",").h1().center().and()
				.text("Auf der Transit-Projekt Plattform haben Sie ungelesene Nachrichten.").center().and()
				.button("Nachrichtenübersicht", linkToMessages).blue()
				.and()
				.text("Mit freundlichen Grüßen").center().and()
				.text("Ihr Transit Project Team").center().and()
				.copyright("Professur für Informationsmanagement - IWI - Universität Leipzig").url("https://www.wifa.uni-leipzig.de/institut-fuer-wirtschaftsinformatik/professuren/informationsmanagement").suffix(".").and()
				.footerText("Institut für Wirtschaftsinformatik – Universität Leipzig\n" +
						"Grimmaische Straße 12 \n" +
						"04109 Leipzig").and()
				.footerImage("https://app.transit-project.de/assets/img/transit-logo.png").width(100).linkUrl("https://transit-project.de").and()
				.footerImage("https://transit-project.de/wp-content/uploads/2022/01/BMBF-Logo-2-300x213.png").width(100).linkUrl("https://www.bmbf.de").and()
				.footerText("Förderkennzeichen: 16DTM109A-C").center().and()
				.footerImage("https://transit-project.de/wp-content/uploads/2023/05/DE-Finanziert-von-der-Europaeischen-Union_POS-1024x265.png").width(100).and()
				.build();

// sent email
		sendMail(subject, to, htmlTextEmail);
	}
	
	
	private void sendMail(String subject, String to, HtmlTextEmail htmlTextEmail) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message,
				MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
				StandardCharsets.UTF_8.name());
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlTextEmail.getText(), htmlTextEmail.getHtml());
		helper.setFrom(DEFAULT_SENDING_FROM);
		
		emailSender.send(message);
	}
	
	
	private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
		
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		helper.setFrom(DEFAULT_SENDING_FROM);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlBody, true);
		helper.addInline("attachment.png", resourceFile);
		emailSender.send(message);
	}
	
}