package com.transit.backend.rightlayers.service.helper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Component

@Slf4j
@Getter
public class CheckEntityTypeClass {
	
	private Set<String> entityClassNames;
	private Set<Class<?>> entityClasses;
	
	public CheckEntityTypeClass() {
		this.entityClassNames = new HashSet<>();
		this.entityClasses = new HashSet<>();
//		Reflections reflections = new Reflections("com.transit.backend.domain");
//
//		Set<Class<?>> allClasses =
//				reflections.getSubTypesOf(Object.class);
//		for(var clazz: allClasses){
//			entityClassNames.add(clazz.getSimpleName());
//		}
//
		final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));
		
		final Set<BeanDefinition> classes = provider.findCandidateComponents("com.transit.backend.domain");
		for (BeanDefinition bean : classes) {
			try {
				Class<?> clazz = Class.forName(bean.getBeanClassName());
				entityClassNames.add(clazz.getSimpleName());
				entityClasses.add(clazz);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	
	public boolean checkIfEntityClassExists(String name) {
		return entityClassNames.contains(name);
	}
}
