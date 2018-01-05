package org.acumos.be.test.controller;

import org.acumos.portal.be.controller.UserServiceController;
import org.acumos.portal.be.security.jwt.JwtTokenUtil;
import org.acumos.portal.be.service.impl.MockCommonDataServiceRestClientImpl;
import org.acumos.portal.be.service.impl.UserServiceImpl;
import org.acumos.portal.be.transport.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(MockitoJUnitRunner.class)
public class TestController {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Mock
	private UserServiceImpl userImpl;
	
	@InjectMocks
	private UserServiceController userController;
	
	@Mock
	private JwtTokenUtil jwtTokenUtil;
	
	@Mock
	private Environment env;
	
	
	@Before
	public void setUp() throws Exception {
		mockMvc = standaloneSetup(userController).build();

	}
	
	String userId = "601f8aa5-5978-44e2-996e-2dbfc321ee73";
	
	
	@Test
	public void getUserImageTest() throws Exception{
		Assert.assertNotNull(userId);
		MockCommonDataServiceRestClientImpl mockCommonDataService = new MockCommonDataServiceRestClientImpl();
		when(userImpl.findUserByUserId(userId)).thenReturn(mockCommonDataService.getUser(userId));
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/userProfileImage/601f8aa5-5978-44e2-996e-2dbfc321ee73")
				.accept(MediaType.APPLICATION_JSON)
				.content("{\"status\": true,\"status_code\": 200,\"response_detail\": \"Success\",\"response_body\": \"AQIDBAU=\"}")
				.contentType(MediaType.APPLICATION_JSON);
		
		MvcResult result = mockMvc.perform(requestBuilder)
				          .andExpect(status().isOk())
				          .andExpect(jsonPath("$.response_detail").exists())
				          .andDo(print()).andReturn();
		String content = result.getResponse().getContentAsString();
		Assert.assertNotNull(content);
		
	}
	
	/*@Test
	public void userProfileTest(){
		MockCommonDataServiceRestClientImpl mockCommonDataService = new MockCommonDataServiceRestClientImpl();
		String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJWZW5rYXRTcmluaXZhc2FuMTIiLCJyb2xlIjpudWxsLCJjcmVhdGVkIjoxNTA4MTQ4Njk1NTE4LCJleHAiOjE1MDgyMzUwOTUsIm1scHVzZXIiOnsidXNlcklkIjoiNjE1MjUyMTEtNmFhYi00ZmRlLTllM2UtYTE4ZjBjNDhiNWQzIiwiZmlyc3ROYW1lIjoiVmVua2F0IiwibWlkZGxlTmFtZSI6bnVsbCwibGFzdE5hbWUiOm51bGwsIm9yZ05hbWUiOm51bGwsImVtYWlsIjoidmVua3lAdGVjaC5jb20iLCJsb2dpbk5hbWUiOiJWZW5rYXRTcmluaXZhc2FuMTIiLCJsb2dpbkhhc2giOm51bGwsImxvZ2luUGFzc0V4cGlyZSI6bnVsbCwiYXV0aFRva2VuIjpudWxsLCJhY3RpdmUiOnRydWUsImxhc3RMb2dpbiI6bnVsbCwicGljdHVyZSI6bnVsbCwiY3JlYXRlZCI6MTUwODE0ODY3ODAwMCwibW9kaWZpZWQiOjE1MDgxNDg2NzgwMDB9fQ.qOce0mapjkXYwNBjLbEfKmiJCnQ9IvuKXkIlmWUFWeGGn1D0VOOf-HI7AzPIvkegnrQfk_MZVG4EZUohBJvGKw";
		String value = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJWZW5rYXRTcmluaXZhc2FuMTIiLCJyb2xlIjpudWxsLCJjcmVhdGVkIjoxNTA4MTQ4Njk1NTE4LCJleHAiOjE1MDgyMzUwOTUsIm1scHVzZXIiOnsidXNlcklkIjoiNjE1MjUyMTEtNmFhYi00ZmRlLTllM2UtYTE4ZjBjNDhiNWQzIiwiZmlyc3ROYW1lIjoiVmVua2F0IiwibWlkZGxlTmFtZSI6bnVsbCwibGFzdE5hbWUiOm51bGwsIm9yZ05hbWUiOm51bGwsImVtYWlsIjoidmVua3lAdGVjaC5jb20iLCJsb2dpbk5hbWUiOiJWZW5rYXRTcmluaXZhc2FuMTIiLCJsb2dpbkhhc2giOm51bGwsImxvZ2luUGFzc0V4cGlyZSI6bnVsbCwiYXV0aFRva2VuIjpudWxsLCJhY3RpdmUiOnRydWUsImxhc3RMb2dpbiI6bnVsbCwicGljdHVyZSI6bnVsbCwiY3JlYXRlZCI6MTUwODE0ODY3ODAwMCwibW9kaWZpZWQiOjE1MDgxNDg2NzgwMDB9fQ.qOce0mapjkXYwNBjLbEfKmiJCnQ9IvuKXkIlmWUFWeGGn1D0VOOf-HI7AzPIvkegnrQfk_MZVG4EZUohBJvGKw";;
		when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(value);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/userProfile")
				.accept(MediaType.APPLICATION_JSON)
				.content("{\"status\": true,\"status_code\": 200,\"response_detail\": \"Success\",\"response_body\": \"AQIDBAU=\"}")
				.contentType(MediaType.APPLICATION_JSON);
		
		
	}*/
	
	@Test
	public void getQandAurlTest() throws Exception {
		String url = "http://localhost:9083";
		when(env.getProperty("qanda.url", "")).thenReturn(url);
		Assert.assertNotNull(url);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/qAUrl").accept(MediaType.APPLICATION_JSON)
				.content("{\"status\": true,\"status_code\": 200,\"response_detail\": \"Success\",\"response_body\": \"http://localhost:9083\"}")
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk())
				                   .andExpect(jsonPath("$.response_detail").exists()).andDo(print()).andReturn();
		String content = result.getResponse().getContentAsString();
		Assert.assertNotNull(content);
	}
	
	@Test
	public void updateBulkUsersTest() throws Exception {
		MockCommonDataServiceRestClientImpl mockCommonDataService = new MockCommonDataServiceRestClientImpl();
		when(userImpl.findUserByUserId(userId)).thenReturn(mockCommonDataService.getUser(userId));
		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/users/updateUser")
				.accept(MediaType.APPLICATION_JSON)
				.content("{\"status\": true,\"status_code\":0,\"response_detail\": \"Success\",\"response_body\": null,\"error_code\": \"100\"}")
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk())
				.andExpect(jsonPath("$.response_detail").exists()).andDo(print()).andReturn();
		String content = result.getResponse().getContentAsString();
		Assert.assertNotNull(content);
	}
	
	/*@Test
	public void deleteBulkUsers() throws Exception{
		MockCommonDataServiceRestClientImpl mockCommonDataService = new MockCommonDataServiceRestClientImpl();
		when(userImpl.findUserByUserId(userId)).thenReturn(mockCommonDataService.getUser(userId));
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/user/deleteUser")
				.accept(MediaType.APPLICATION_JSON)
				.content("{\"status\": true,\"status_code\":0,\"response_detail\": \"Success\",\"response_body\": null,\"error_code\": \"100\"}")
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk())
				.andExpect(jsonPath("$.response_detail").exists()).andDo(print()).andReturn();
		String content = result.getResponse().getContentAsString();
		Assert.assertNotNull(content);
	}*/
}
