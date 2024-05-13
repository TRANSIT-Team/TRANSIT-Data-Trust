package com.transit.geoservice.config.config.filter;

import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


public class ForwardRemoveFilter extends OncePerRequestFilter {
	private static final Set<String> FORWARDED_HEADER_NAMES_FOR_DELETING =
			Collections.newSetFromMap(new LinkedCaseInsensitiveMap<>(10, Locale.ENGLISH));
	
	static {
		FORWARDED_HEADER_NAMES_FOR_DELETING.add("Forwarded");
		FORWARDED_HEADER_NAMES_FOR_DELETING.add("X-Forwarded-Ssl");
		FORWARDED_HEADER_NAMES_FOR_DELETING.add("X-Forwarded-For");
	}
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		ForwardedHeaderRemovingRequest wrappedRequest = new ForwardedHeaderRemovingRequest(request);
		filterChain.doFilter(wrappedRequest, response);
		
	}
	
	private static class ForwardedHeaderRemovingRequest extends HttpServletRequestWrapper {
		
		private final Map<String, List<String>> headers;
		
		public ForwardedHeaderRemovingRequest(HttpServletRequest request) {
			super(request);
			this.headers = initHeaders(request);
		}
		
		private static Map<String, List<String>> initHeaders(HttpServletRequest request) {
			Map<String, List<String>> headers = new LinkedCaseInsensitiveMap<>(Locale.ENGLISH);
			Enumeration<String> names = request.getHeaderNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				if (!FORWARDED_HEADER_NAMES_FOR_DELETING.contains(name)) {
					headers.put(name, Collections.list(request.getHeaders(name)));
				}
			}
			headers.put("X-Forwarded-Ssl", List.of("on"));
			return headers;
		}
		
		// Override header accessors to not expose forwarded headers
		
		@Override
		@Nullable
		public String getHeader(String name) {
			List<String> value = this.headers.get(name);
			return (CollectionUtils.isEmpty(value) ? null : value.get(0));
		}
		
		@Override
		public Enumeration<String> getHeaders(String name) {
			List<String> value = this.headers.get(name);
			return (Collections.enumeration(value != null ? value : Collections.emptySet()));
		}
		
		@Override
		public Enumeration<String> getHeaderNames() {
			return Collections.enumeration(this.headers.keySet());
		}
	}
}
