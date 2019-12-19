/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */
package org.acumos.be.test.service.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.exception.UserServiceException;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.service.impl.UserRoleServiceImpl;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.MLRoleFunction;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(MockitoJUnitRunner.class)		
public class UserRoleServiceImplTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	UserRoleServiceImpl userRoleService;
	@Mock
	private UserService userService;
	@Mock
	Environment env;
	
	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	private final String url = "http://localhost:8000/ccds";
	private final String user = "ccds_client";
	private final String pass = "ccds_client";
	private final String GET_ALL_ROLE="/ccds/role/search?active=true&_j=o&page=0&size=1000";
	private final String GET_USER_ROLES="/ccds/user/testuser/role";
	private final String GET_ROLE="/ccds/role/testroleid";
	private final String CREATE_ROLE="/ccds/role";
	private final String GET_FUNCTION="/function/testrolefunid";
	
	@Test
	public void getAllRolesTest() throws JsonProcessingException, UserServiceException{
		MLPRole mlpRole=getMLPRole();
		List<MLPRole> roleList=new ArrayList<>();
		roleList.add(mlpRole);
		ObjectMapper Obj = new ObjectMapper();
		setCdsProperty();
		RestPageResponse<MLPRole> roleRes = new RestPageResponse<>(roleList, PageRequest.of(0, 1), 1);
		String jsonStrRes = Obj.writeValueAsString(roleRes);
		stubFor(get(urlEqualTo(GET_ALL_ROLE)).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStrRes)));
		
		List<MLRole> mlRoleList=userRoleService.getAllRoles();
		assertNotNull(mlRoleList);
	}
	
	@Test
	public void getRolesForUserTest() throws JsonProcessingException{
		MLPRole mlpRole=getMLPRole();
		List<MLPRole> roleList=new ArrayList<>();
		roleList.add(mlpRole);
		ObjectMapper Obj = new ObjectMapper();
		setCdsProperty();
		String jsonStrRes = Obj.writeValueAsString(roleList);
		stubFor(get(urlEqualTo(GET_USER_ROLES)).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStrRes)));
		List<MLRole> mlRoleList=userRoleService.getRolesForUser("testuser");
		assertNotNull(mlRoleList);
		
	}
	
	@Test
	public void getRoleTest() throws JsonProcessingException{
		MLPRole mlpRole=getMLPRole();
		ObjectMapper Obj = new ObjectMapper();
		setCdsProperty();
		String jsonStrRes = Obj.writeValueAsString(mlpRole);
		stubFor(get(urlEqualTo(GET_ROLE)).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStrRes)));
		MLPRole role=userRoleService.getRole(mlpRole.getRoleId());
		assertNotNull(role);
	}
	
	@Test
	public void createRole() throws JsonProcessingException{
		MLPRole mlpRole=getMLPRole();
		MLRole mlRole=PortalUtils.convertToMLRole(mlpRole);
		ObjectMapper Obj = new ObjectMapper();
		setCdsProperty();
		String jsonStrRes = Obj.writeValueAsString(mlpRole);
		stubFor(post(urlEqualTo(CREATE_ROLE)).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStrRes)));
		mlpRole = userRoleService.createRole(mlRole);
		assertNotNull(mlpRole);
	}
	
	@Test
	public void updateRole(){
		setCdsProperty();
		stubFor(put(urlEqualTo(GET_ROLE)).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
		userRoleService.updateRole("testroleid", "testrolename");
	}
	
	@Test
	public void deleteRole(){
		setCdsProperty();
		stubFor(delete(urlEqualTo(GET_ROLE)).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
		userRoleService.deleteRole("testroleid");
	}
	
	@Test
	public void getRoleFunction() throws JsonProcessingException{
		MLPRoleFunction roleFunction=getMLPRoleFunction();
		ObjectMapper Obj = new ObjectMapper();
		setCdsProperty();
		String jsonStrRes = Obj.writeValueAsString(roleFunction);
		stubFor(get(urlEqualTo(GET_ROLE + GET_FUNCTION )).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStrRes)));
		MLRoleFunction mlroleFunction=userRoleService.getRoleFunction(roleFunction.getRoleId(), roleFunction.getRoleFunctionId());
		assertNotNull(mlroleFunction);
	}
	
	@Test
	public void createRoleFunction() throws JsonProcessingException{
		MLPRoleFunction roleFunction=getMLPRoleFunction();
		ObjectMapper Obj = new ObjectMapper();
		setCdsProperty();
		String jsonStrRes = Obj.writeValueAsString(roleFunction);
		stubFor(post(urlEqualTo(GET_ROLE + "/function" )).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStrRes)));
		MLPRoleFunction mlroleFunction=userRoleService.createRoleFunction(roleFunction);
		assertNotNull(mlroleFunction);
	}
	
	@Test
	public void updateRoleFunction(){
		MLPRoleFunction roleFunction=getMLPRoleFunction();
		setCdsProperty();
		stubFor(put(urlEqualTo(GET_ROLE + GET_FUNCTION)).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
		userRoleService.updateRoleFunction(roleFunction);
	}
	
	@Test
	public void deleteRoleFunction(){
		setCdsProperty();
		stubFor(delete(urlEqualTo(GET_ROLE + GET_FUNCTION)).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
		userRoleService.deleteRoleFunction("testroleid", "testrolefunid");
	}
	
	@Test
	public void addUserRole(){
		setCdsProperty();
		stubFor(post(urlEqualTo(GET_USER_ROLES+"/testroleid")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
		userRoleService.addUserRole("testuser", "testroleid");
	}
	
	private void setCdsProperty() {
		when(env.getProperty("cdms.client.url")).thenReturn(url);
		when(env.getProperty("cdms.client.username")).thenReturn(user);
		when(env.getProperty("cdms.client.password")).thenReturn(pass);
	}
	
	private MLPRole getMLPRole() {
		MLPRole role=new MLPRole();
		role.setRoleId("testroleid");
		role.setName("testname");
		role.setActive(true);
		return role;
	}
	
	private MLPRoleFunction getMLPRoleFunction() {
		MLPRoleFunction roleFunction=new MLPRoleFunction();
		roleFunction.setRoleFunctionId("testrolefunid");
		roleFunction.setRoleId("testroleid");
		roleFunction.setName("testrolefunname");
		return roleFunction;
	}
}