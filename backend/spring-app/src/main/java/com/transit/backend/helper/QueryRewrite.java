package com.transit.backend.helper;

import io.github.perplexhub.rsql.RSQLOperators;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class QueryRewrite {
	
	public static String queryRewriteAll(String query) {
		return query.replaceAll("==[\\s]*all", "=in=(true,false)");
	}
	
	public static Matcher queryDefaultMatcher(String query, int finalLongestConditionName) {
		return Pattern.compile("(?<!=[a-zA-Z]{0," + finalLongestConditionName + "})[\\.]?[a-zA-Z]+=").matcher(query);
	}
	
	public static String queryRewritePackageItemToPackagePackageProperty(Matcher m) {
		return m.replaceAll((match) -> {
					String replaceFilterQuery = match.group();
					if (replaceFilterQuery.startsWith(".")) {
						return replaceFilterQuery;
					} else {
						return "packageItem." + replaceFilterQuery;
					}
				})
				.replaceAll("packageClass", "packageItem.packageClass")
				.replaceAll("packagePackageProperties.", "")
				.replaceAll("order", "packageItem.order");
	}
	
	public static String queryRewriteCompanyToCompanyProperty(Matcher m) {
		return m.replaceAll((match) -> {
					String replaceFilterQuery = match.group();
					if (replaceFilterQuery.startsWith(".")) {
						return replaceFilterQuery;
					} else {
						return "company." + replaceFilterQuery;
					}
				})
				.replaceAll("companyProperties.", "")
				.replaceAll("order", "company.order")
				.replaceAll("subOrder", "company.subOrder")
				.replaceAll("companyUsers", "company.companyUsers")
				.replaceAll("companyAddresses", "company.companyAddresses");
	}
	
	public static String queryRewriteUserToUserProperty(Matcher m) {
		return m.replaceAll((match) -> {
					String replaceFilterQuery = match.group();
					if (replaceFilterQuery.startsWith(".")) {
						return replaceFilterQuery;
					} else {
						return "user." + replaceFilterQuery;
					}
				})
				.replaceAll("userProperties.", "")
				.replaceAll("order", "user.order")
				.replaceAll("subOrder", "user.subOrder")
				.replaceAll("company", "user.company");
	}
	
	public static String queryRewriteCarToCarProperties(Matcher m) {
		return m.replaceAll((match) -> {
					String replaceFilterQuery = match.group();
					if (replaceFilterQuery.startsWith(".")) {
						return replaceFilterQuery;
					} else {
						return "car." + replaceFilterQuery;
					}
				})
				.replaceAll("orderLegs", "car.orderLegs")
				.replaceAll("carProperties.", "")
				.replaceAll("locations", "car.locations");
	}
	
	public static String queryRewriteWarehouseToWarehouseProperties(Matcher m) {
		return m.replaceAll((match) -> {
					String replaceFilterQuery = match.group();
					if (replaceFilterQuery.startsWith(".")) {
						return replaceFilterQuery;
					} else {
						return "warehouse." + replaceFilterQuery;
					}
				})
				.replaceAll("orderLegs", "warehouse.orderLegs")
				.replaceAll("warehouseProperties.", "")
				.replaceAll("address", "warehouse.address");
	}
	
	public static String queryRewriteOrderToOrderProperties(Matcher m) {
		return m.replaceAll((match) -> {
					String replaceFilterQuery = match.group();
					if (replaceFilterQuery.startsWith(".")) {
						return replaceFilterQuery;
					} else {
						return "order." + replaceFilterQuery;
					}
				})
				.replaceAll("orderProperties.", "")
				.replaceAll("addressFrom", "order.addressFrom")
				.replaceAll("addressTo", "order.addressTo")
				.replaceAll("addressBilling", "order.addressBilling")
				.replaceAll("status.", "order.status")
				.replaceAll("parentOrder", "order.parentOrder")
				.replaceAll("company", "order.company")
				.replaceAll("orderStatus!=", "order.orderStatus!=");
		
	}
	
	public static String queryRewritePaymentToPaymentProperties(Matcher m) {
		return m.replaceAll((match) -> {
					String replaceFilterQuery = match.group();
					if (replaceFilterQuery.startsWith(".")) {
						return replaceFilterQuery;
					} else {
						return "payment." + replaceFilterQuery;
					}
				})
				.replaceAll("paymentStatus", "payment.paymentStatus")
				.replaceAll("paymentProperties.", "");
	}
	
	public static String queryRewriteCostToCostProperties(Matcher m) {
		return m.replaceAll((match) -> {
					String replaceFilterQuery = match.group();
					if (replaceFilterQuery.startsWith(".")) {
						return replaceFilterQuery;
					} else {
						return "cost." + replaceFilterQuery;
					}
				})
				.replaceAll("costProperties.", "");
	}
	
	public static String queryById(UUID id) {
		return "id==" + id;
	}
	
	public static void finalLongestConditionNameArrayFunction(int[] finalLongestConditionNameArray) {
		if (finalLongestConditionNameArray[0] == -1) {
			RSQLOperators.supportedOperators().forEach(operator -> {
				for (var symbol : operator.getSymbols()) {
					if (symbol.replaceAll("=", "").length() > finalLongestConditionNameArray[0]) {
						finalLongestConditionNameArray[0] = symbol.replaceAll("=", "").length();
					}
				}
			});
		}
	}
	/*public static RSQLCustomPredicate<User> customPredicate() {
		return  new RSQLCustomPredicate<>(new ComparisonOperator("=isEmptySortedSet="), User.class, input -> {
			CriteriaBuilder cb = input.getCriteriaBuilder();
			CriteriaQuery<User> cq = cb.createQuery(User.class);
			Root<User> test = cq.from(User.class);
			var testContact = cb.treat(test, User.class);
			Join<User, UserProperty> groupPath1 = testContact.join("userProperties", JoinType.LEFT);
			//ListJoin<User, UserProperty> groupPath2 = testContact.joinList("userProperties",JoinType.LEFT);
			return cb.isEmpty(groupPath1.getCorrelationParent().get("userProperties"));
			//return input.getCriteriaBuilder().isEmpty(input.getPath().as(SortedSet.class));
		});
	}*/
}