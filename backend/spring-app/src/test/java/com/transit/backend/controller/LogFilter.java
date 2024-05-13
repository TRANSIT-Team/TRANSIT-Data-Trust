package com.transit.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Slf4j
public class LogFilter implements Filter {
	
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		log.info("[IN]{}:{}", req.getMethod(), req.getRequestURI());
		try {
			chain.doFilter(request, response);
		} finally {
			log.info("[OUT]{}:{}", req.getMethod(), req.getRequestURI());
		}
	}
}