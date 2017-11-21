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

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPAccessType;
import org.acumos.cds.domain.MLPModelType;
import org.acumos.cds.domain.MLPToolkitType;
import org.acumos.portal.be.service.impl.FilterCategoriesServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 *   
 * @author VT00325492
 *
 */
public class FilterCategoriesServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(FilterCategoriesServiceImplTest.class);

	@Mock
	Environment env;

	@Mock
	AdminServiceImplTest test;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	 
	private final String url = "http://localhost:8002/ccds/";
	private final String user = "ccds_client";
	private final String pass = "ccds_client";

	private ICommonDataServiceRestClient dataServiceRestClient;
	
	@Test
	public void getSolutionCategoryTypes(){
		try{
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			FilterCategoriesServiceImpl impl = new FilterCategoriesServiceImpl();
			impl.setEnvironment(env);
			List<MLPModelType> mlpModelTypes = new  ArrayList<>();
			mlpModelTypes = impl.getSolutionCategoryTypes();
			if(mlpModelTypes !=null) {
				logger.info("getSolutionCategoryTypes : ", mlpModelTypes.size());
			}
		} catch (Exception e) {
			logger.error("Exception occured while getSolutionCategoryTypes: " + e);	
		}
	}
	
	@Test
	public void getSolutionAccessTypes(){
		try{
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			FilterCategoriesServiceImpl impl = new FilterCategoriesServiceImpl();
			impl.setEnvironment(env);
			List<MLPAccessType> mlpAccessTypes = new ArrayList<>();
			mlpAccessTypes = impl.getSolutionAccessTypes();
			if(mlpAccessTypes !=null) {
				logger.info("getSolutionCategoryTypes : ", mlpAccessTypes.size());
			}
		} catch (Exception e) {
			logger.error("Exception occured while getSolutionAccessTypes: " + e);	
		}
	}
	
	@Test
	public void getToolkitTypes(){
		try{
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			FilterCategoriesServiceImpl impl = new FilterCategoriesServiceImpl();
			impl.setEnvironment(env);
			List<MLPToolkitType> mlpToolkitTypes = new ArrayList<>();
			mlpToolkitTypes = impl.getToolkitTypes();
			if(mlpToolkitTypes !=null) {
				logger.info("getToolkitTypes : ", mlpToolkitTypes.size());
			}
		} catch (Exception e) {
			logger.error("Exception occured while getToolkitTypes: " + e);	
		}
	}
	
	
}
