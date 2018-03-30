package org.acumos.be.test.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.controller.CASAuthController;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class CASAuthControllerTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	CASAuthController casAuthController;
	
	@Mock
	Environment env;
	
	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	@Test
	public void getDocurl(){
		JsonResponse<String> responseVO =casAuthController.getDocurl(request, response);
		Assert.assertNotNull(responseVO);
	}  
}
