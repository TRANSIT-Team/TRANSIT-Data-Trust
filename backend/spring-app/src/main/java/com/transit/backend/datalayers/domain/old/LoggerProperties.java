package com.transit.backend.datalayers.domain.old;

import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "LoggerProperties")
@Table(name = "loggerPropeties")
@Audited(withModifiedFlag = true)
public class LoggerProperties extends AbstractEntity implements Serializable {
	
	
	private String key;
	
	
	private String value;
}
