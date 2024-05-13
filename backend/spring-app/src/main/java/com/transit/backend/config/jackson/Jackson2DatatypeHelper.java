package com.transit.backend.config.jackson;

import org.springframework.context.annotation.Configuration;

@Configuration
public class Jackson2DatatypeHelper {

//	private static final boolean IS_HIBERNATE_AVAILABLE = ClassUtils.isPresent("org.hibernate.Version",
//			Jackson2DatatypeHelper.class.getClassLoader());
//	private static final boolean IS_HIBERNATE4_MODULE_AVAILABLE = ClassUtils.isPresent(
//			"com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module", Jackson2DatatypeHelper.class.getClassLoader());
//	private static final boolean IS_HIBERNATE5_MODULE_AVAILABLE = ClassUtils.isPresent(
//			"com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module", Jackson2DatatypeHelper.class.getClassLoader());
//
//	private static final boolean IS_JODA_MODULE_AVAILABLE = ClassUtils
//			.isPresent("com.fasterxml.jackson.datatype.joda.JodaModule", Jackson2DatatypeHelper.class.getClassLoader());
//
//	@Bean
//	public static ObjectMapper configureObjectMapper(ObjectMapper mapper) {
//
//		// Hibernate types
//		if (IS_HIBERNATE_AVAILABLE && HibernateVersions.isHibernate4() && IS_HIBERNATE4_MODULE_AVAILABLE) {
//			mapper = new Hibernate4ModuleRegistrar().registerModule(mapper);
//		}
//
//		if (IS_HIBERNATE_AVAILABLE && HibernateVersions.isHibernate5() && IS_HIBERNATE5_MODULE_AVAILABLE) {
//			mapper = new Hibernate5ModuleRegistrar().registerModule(mapper);
//		}
//
//		// JODA time
//		if (IS_JODA_MODULE_AVAILABLE) {
//			mapper = JodaModuleRegistrar.registerModule(mapper);
//		}
//		return mapper;
//	}
	
	//	private static class HibernateVersions {
//
//		public static boolean isHibernate4() {
//			return Version.getVersionString().startsWith("4");
//		}
//
//		public static boolean isHibernate5() {
//			return Version.getVersionString().startsWith("5");
//		}
//	}


//	@Bean
//	public Hibernate5Module registerModule5() {
//
//		Hibernate5Module module = new Hibernate5Module();
//		module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
//		module.enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);
//		return module;
//	}


//	private static class Hibernate4ModuleRegistrar {
//
//		public ObjectMapper registerModule(ObjectMapper mapper) {
//
//			Hibernate4Module module = new Hibernate4Module();
//			module.disable(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION);
//			module.enable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
//
//			mapper.registerModule(module);
//			return mapper;
//		}
//	}

//	private static class Hibernate5ModuleRegistrar {
//
//		public ObjectMapper registerModule(ObjectMapper mapper) {
//
//			Hibernate5Module module = new Hibernate5Module();
//			module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
//			module.enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);
//
//			mapper.registerModule(module);
//			return mapper;
//		}
//	}
//
//	private static class JodaModuleRegistrar {
//
//		public static ObjectMapper registerModule(ObjectMapper mapper) {
//			mapper.registerModule(new JodaModule());
//			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//			return mapper;
//		}
//	}
}

