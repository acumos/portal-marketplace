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

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.acumos.portal.be.service.impl.FilterCategoriesServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class FilterCategoriesServiceImplTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	@Mock
	FilterCategoriesServiceImpl impl = new FilterCategoriesServiceImpl();
	
	@Test
	public void dummy() {
		Assert.assertEquals("true", "true");
	}
	
	/*@Test
	public void getSolutionCategoryTypes(){
		try{
			List<MLPModelType> mlpModelTypesList = new  ArrayList<>();
			MLPModelType mlPModelType = new MLPModelType();
			mlPModelType.setCode("200");
			mlPModelType.setName("abc");
			mlpModelTypesList.add(mlPModelType);
			Mockito.when(impl.getSolutionCategoryTypes()).thenReturn(mlpModelTypesList);
			if(mlpModelTypesList !=null) {
				Assert.assertEquals(mlpModelTypesList, mlpModelTypesList);
				logger.info("Successfully fetched all MLPModelType");
			}
		} catch (Exception e) {
			logger.error("Exception occured while getSolutionCategoryTypes: " + e);	
		}
	}
	
	@Test
	public void getSolutionAccessTypes(){
		try{
			List<MLPAccessType> mlpAccessTypesList = new ArrayList<>();
			MLPAccessType mlpAccessType = new MLPAccessType();
			mlpAccessType.setCode("200");
			mlpAccessType.setName("xyz");
			mlpAccessTypesList.add(mlpAccessType);
			Mockito.when(impl.getSolutionAccessTypes()).thenReturn(mlpAccessTypesList);
			if(mlpAccessTypesList !=null) {
				Assert.assertEquals(mlpAccessTypesList, mlpAccessTypesList);
				logger.info("Successfully fetched all MLPAccessType");
			}
		} catch (Exception e) {
			logger.error("Exception occured while getSolutionAccessTypes: " + e);	
		}
	}
	
	@Test
	public void getToolkitTypes(){
		try{
			List<MLPToolkitType> mlpToolkitTypesList = new ArrayList<>();
			MLPToolkitType mlpToolkitType = new MLPToolkitType();
			mlpToolkitType.setCode("400");
			mlpToolkitType.setName("test");
			mlpToolkitTypesList.add(mlpToolkitType);
			Mockito.when(impl.getToolkitTypes()).thenReturn(mlpToolkitTypesList);
			if(mlpToolkitTypesList !=null) {	
				Assert.assertEquals(mlpToolkitTypesList, mlpToolkitTypesList);
				logger.info("Successfully fetched all MLPToolkitType");
			}
		} catch (Exception e) {
			logger.error("Exception occured while getToolkitTypes: " + e);	
		}
	}*/
	
	
}