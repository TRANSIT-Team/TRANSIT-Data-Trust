package com.transit.backend.datalayers.domain.old;

import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Logger")
@Table(name = "logger")
@Audited(withModifiedFlag = true)
public class Logger extends AbstractEntity implements Serializable {
	
	
	@OneToMany
	private Set<LoggerProperties> Properties;
	
}
