package com.transit.graphbased_v2.repository;

import com.transit.graphbased_v2.config.StartConfiguration;
import com.transit.graphbased_v2.domain.graph.nodes.ObjectAttributeExtendedClazz;
import com.transit.graphbased_v2.service.helper.ParseRightsResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.*;

@Component
@Slf4j
public class RightsRepository {
	
	private static String uri;
	
	private static String username;
	private static String password;
	
	private Driver driver;
	
	@Autowired
	private ParseRightsResult parseResult;
	
	
	public RightsRepository() {
		uri = StartConfiguration.getProperty("spring.neo4j.uri");
		if (!uri.equals("")) {
			username = StartConfiguration.getProperty("spring.neo4j.authentication.username");
			password = StartConfiguration.getProperty("spring.neo4j.authentication.password");
			//connect to database
			connect();
		}
	}
	
	
	public void connect() {
		
		this.driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
		System.out.println("Connected to Neo4j");
		
	}
	
	public List<ObjectAttributeExtendedClazz> getRightsList(Set<UUID> objectIds, UUID identityId, UUID requestedById) {
		if (objectIds.isEmpty()) {
			return new ArrayList<>();
		}
		List<ObjectAttributeExtendedClazz> rightsList = new ArrayList<>();
		
		for (UUID id : objectIds) {
			Optional<ObjectAttributeExtendedClazz> result = getRights(identityId, requestedById, id);
			result.ifPresent(rightsList::add);
		}
		
		return rightsList;
	}
	
	
	public Optional<ObjectAttributeExtendedClazz> getRights(UUID identityID, UUID requestedById, UUID oId) {
		StringBuilder builder = new StringBuilder();
		builder.append("MATCH (ua2:UA {id: '").append(identityID).append("'})-[rel:relation {access:'1'}]->" + "(oa:OA)<-[:assigned]-(o:O {id: '").append(oId).append("'})");
		if (!identityID.equals(requestedById)) {
			builder.append(" ,(ua1:UA {id: '").append(requestedById).append("'})-[rel2:relation {control:'1'}" + "]->(oa)");
		}
		builder.append(" RETURN oa, o.id, o.entityClass");
		var result = execute(builder.toString(), false);
		if (!result.hasNext()) {
			return Optional.empty();
		}
		var r = parseResult.parseResult(result, true, false);
		return Optional.of(r.get(0));
	}
	
	
	public Result execute(String cypher, boolean commit) {
		Result result;
		final StopWatch stopWatch = new StopWatch();
		
		stopWatch.start();
		var session = driver.session();
		if (!commit) {
			result = session.run(cypher);
		} else {
			var transaction = session.beginTransaction();
			result = transaction.run(cypher);
			transaction.commit();
		}
		stopWatch.stop();
		log.error("\"{}\" executed in {}", "Time to execute Cypher Query", DurationFormatUtils.formatDurationHMS(stopWatch.getTotalTimeMillis()));
		return result;
		
	}
	
	
	public List<ObjectAttributeExtendedClazz> getRightsListOld(Set<UUID> objectIds, UUID identityId, UUID requestedById) {
		StringBuilder builder = new StringBuilder();
		if (objectIds.isEmpty()) {
			return new ArrayList<>();
		}
		builder.append("MATCH (ua:UA {id:'").append(identityId).append("'})-[rel:relation {access:'1'}]->(oa:OA)<-[:assigned]-(o:O) ");
		if (!identityId.equals(requestedById)) {
			builder.append(" , (ua1:UA {id: '").append(requestedById).append("'})-[a:relation {control:'1'}" + "]->(oa)");
		}
		
		builder.append(" WHERE o.id IN [");
		
		for (UUID id : objectIds) {
			builder.append(" '").append(id).append("', ");
		}
		String cypher = builder.toString();
		cypher = cypher.substring(0, cypher.length() - 2);
		cypher += "] RETURN oa, o.id, o.entityClass";
		var result = execute(cypher, false);
		return parseResult.parseResult(result, false, true);
	}
	
	public List<ObjectAttributeExtendedClazz> getRightsClass(String entityClazz, UUID requestedById, boolean createdByMyOwn, UUID identityId) {
		StringBuilder builder = new StringBuilder();
		
		if (identityId == null) {
			if (createdByMyOwn) {
				builder.append("MATCH (ua:UA {id: '").append(requestedById).append("'})-[rel:relation {owns:'1'}]-" + "(oa:OA)-[:assigned]-(o:O)-[:entity]-(e:E {entityClass:'").append(entityClazz).append("'})");
			} else {
				builder.append("MATCH (ua:UA {id: '").append(requestedById).append("'})-[rel:relation {access:'1'}]-" + "(oa:OA)-[:assigned]-(o:O)-[:entity]-(e:E {entityClass:'").append(entityClazz).append("'})");
				
			}
		} else {
			if (createdByMyOwn) {
				builder.append("MATCH (ua:UA {id: '").append(requestedById).append("'})-[relOWS:relation {owns:'1'}]-" + "(oaOWS:OA)-[:assigned]-(o:O)-[:entity]-(e:E {entityClass:'").append(entityClazz).append("'})");
				builder.append(", (ua2:UA {id: '").append(identityId).append("'})-[rel:relation {access:'1'}]-" + "(oa:OA)-[:assigned]-(o:O)-[:entity]-(e:E {entityClass:'").append(entityClazz).append("'})");
				builder.append(", (ua:UA {id: '").append(requestedById).append("'})-[rel2:relation {control:'1'}" + "]->(oa) ");
			} else {
				builder.append("MATCH (ua:UA {id: '").append(identityId).append("'})-[rel:relation {access:'1'}]-" + "(oa:OA)-[:assigned]-(o:O)-[:entity]-(e:E {entityClass:'").append(entityClazz).append("'})");
				builder.append(", (ua1:UA {id: '").append(requestedById).append("'})-[rel2:relation {control:'1'}" + "]->(oa) ");
			}
		}
		
		builder.append(" RETURN oa, o.id, o.entityClass");
		log.error(builder.toString());
		var result = execute(builder.toString(), false);
		return parseResult.parseResult(result, false, true);
	}
	
	public List<ObjectAttributeExtendedClazz> getAllMyRights(UUID requestedById) {
		StringBuilder builder = new StringBuilder();
		builder.append("MATCH (ua:UA {id: '").append(requestedById).append("'})-[rel:relation {access:'1'}]->(oa:OA)<-[:assigned]-(o:O)");
		
		builder.append(" RETURN oa, o.id, o.entityClass");
		var result = execute(builder.toString(), false);
		return parseResult.parseResult(result, false, true);
	}
	
	
}
