package com.transit.backend.config;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public
class RestResponsePage<T> extends PageImpl<T> {
	
	private static final long serialVersionUID = 3248189030448292002L;
	
	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public RestResponsePage(@JsonProperty("content") List<T> content, @JsonProperty("number") int number, @JsonProperty("size") int size,
	                        @JsonProperty("totalElements") Long totalElements, @JsonProperty("pageable") JsonNode pageable, @JsonProperty("last") boolean last,
	                        @JsonProperty("totalPages") int totalPages, @JsonProperty("sort") JsonNode sort, @JsonProperty("first") boolean first,
	                        @JsonProperty("numberOfElements") int numberOfElements) {
		super(content, PageRequest.of(number, size), totalElements);
	}
	
	public RestResponsePage(List<T> content, Pageable pageable, long total) {
		super(content, pageable, total);
	}
	
	public RestResponsePage(List<T> content) {
		super(content);
	}
	
	public RestResponsePage() {
		super(new ArrayList<T>());
	}
	
}

//public class HelperPage extends PageImpl<OrderDTO> {
//
//	@JsonCreator
//	// Note: I don't need a sort, so I'm not including one here.
//	// It shouldn't be too hard to add it in tho.
//	public HelperPage(@JsonProperty("content") List<OrderDTO> content,
//	                  @JsonProperty("number") int number,
//	                  @JsonProperty("size") int size,
//	                  @JsonProperty("totalElements") Long totalElements) {
//		super(content, new PageRequest(number, size), totalElements);
//	}
//}