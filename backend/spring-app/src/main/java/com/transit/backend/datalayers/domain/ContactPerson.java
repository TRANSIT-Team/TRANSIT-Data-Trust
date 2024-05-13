package com.transit.backend.datalayers.domain;

import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ContactPerson")
@Table(name = "contactPersons")
@Audited(withModifiedFlag = true)
public class ContactPerson extends AbstractEntity implements Serializable {
	
	private String name;
	
	private String email;
	
	private String phone;
	
	private UUID companyId;
	
}
