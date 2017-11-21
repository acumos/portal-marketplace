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

package org.acumos.be.test.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPAccessType;
import org.acumos.cds.domain.MLPModelType;
import org.acumos.cds.domain.MLPToolkitType;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.controller.FilterCategoriesServiceController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class FilterServiceControllerTest {

	private static Logger logger = LoggerFactory.getLogger(FilterServiceControllerTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	FilterCategoriesServiceController filterController = new FilterCategoriesServiceController();

	@Test
	public void getSolutionsCategoryTypesTest() {
		try {
			MLPModelType mlpModelType1 = new MLPModelType();
			mlpModelType1.setTypeCode("CL");
			mlpModelType1.setTypeName("Classification");

			MLPModelType mlpModelType2 = new MLPModelType();
			mlpModelType2.setTypeCode("DT");
			mlpModelType2.setTypeName("Data Transformer");

			MLPModelType mlpModelType3 = new MLPModelType();
			mlpModelType3.setTypeCode("PR");
			mlpModelType3.setTypeName("Prediction");

			MLPModelType mlpModelType4 = new MLPModelType();
			mlpModelType4.setTypeCode("RG");
			mlpModelType4.setTypeName("Regression");

			List<MLPModelType> mlpModelTypeTest = new ArrayList<MLPModelType>();
			mlpModelTypeTest.add(mlpModelType1);
			mlpModelTypeTest.add(mlpModelType2);
			mlpModelTypeTest.add(mlpModelType3);
			mlpModelTypeTest.add(mlpModelType4);

			JsonResponse<List<MLPModelType>> modelTypeRes = new JsonResponse<>();
			modelTypeRes.setResponseBody(mlpModelTypeTest);

			Mockito.when(filterController.getSolutionsCategoryTypes(request, response)).thenReturn(modelTypeRes);
			logger.info("Filter Category Types : " + modelTypeRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getSolutionsAccessTypes() {
		try {
			MLPAccessType mlpAccessType = new MLPAccessType();
			mlpAccessType.setAccessCode("OR");
			mlpAccessType.setAccessName("Organization");

			MLPAccessType mlpAccessType1 = new MLPAccessType();
			mlpAccessType1.setAccessCode("PR");
			mlpAccessType1.setAccessName("Private");

			MLPAccessType mlpAccessType2 = new MLPAccessType();
			mlpAccessType2.setAccessCode("PB");
			mlpAccessType2.setAccessName("Public");

			List<MLPAccessType> mlpAccessTypeList = new ArrayList<MLPAccessType>();
			mlpAccessTypeList.add(mlpAccessType);
			mlpAccessTypeList.add(mlpAccessType1);
			mlpAccessTypeList.add(mlpAccessType2);

			JsonResponse<List<MLPAccessType>> accessRes = new JsonResponse<>();
			accessRes.setResponseBody(mlpAccessTypeList);

			MLPModelType mlpModelType1 = new MLPModelType();
			mlpModelType1.setTypeCode("CL");
			mlpModelType1.setTypeName("Classification");

			MLPModelType mlpModelType2 = new MLPModelType();
			mlpModelType2.setTypeCode("DT");
			mlpModelType2.setTypeName("Data Transformer");

			MLPModelType mlpModelType3 = new MLPModelType();
			mlpModelType3.setTypeCode("PR");
			mlpModelType3.setTypeName("Prediction");

			MLPModelType mlpModelType4 = new MLPModelType();
			mlpModelType4.setTypeCode("RG");
			mlpModelType4.setTypeName("Regression");

			List<MLPModelType> mlpModelTypeTest = new ArrayList<MLPModelType>();
			mlpModelTypeTest.add(mlpModelType1);
			mlpModelTypeTest.add(mlpModelType2);
			mlpModelTypeTest.add(mlpModelType3);
			mlpModelTypeTest.add(mlpModelType4);

			JsonResponse<List<MLPModelType>> modelTypeRes = new JsonResponse<>();
			modelTypeRes.setResponseBody(mlpModelTypeTest);

			Mockito.when(filterController.getSolutionsCategoryTypes(request, response)).thenReturn(modelTypeRes);

			logger.info("List of Access Type : " + accessRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getToolKitTypeTest() {
		try {
			MLPToolkitType mlpToolKit = new MLPToolkitType();
			mlpToolKit.setToolkitCode("CP");
			mlpToolKit.setToolkitName("Composite Solution");

			MLPToolkitType mlpToolKit1 = new MLPToolkitType();
			mlpToolKit1.setToolkitCode("DS");
			mlpToolKit1.setToolkitName("Design Studio");

			MLPToolkitType mlpToolKit2 = new MLPToolkitType();
			mlpToolKit2.setToolkitCode("H2");
			mlpToolKit2.setToolkitName("H2O");

			MLPToolkitType mlpToolKit3 = new MLPToolkitType();
			mlpToolKit3.setToolkitCode("RC");
			mlpToolKit3.setToolkitName("RCloud");

			MLPToolkitType mlpToolKit4 = new MLPToolkitType();
			mlpToolKit4.setToolkitCode("SK");
			mlpToolKit4.setToolkitName("Scikit-Learn");

			MLPToolkitType mlpToolKit5 = new MLPToolkitType();
			mlpToolKit5.setToolkitCode("TF");
			mlpToolKit5.setToolkitName("TensorFlow");

			List<MLPToolkitType> mlpToolKitList = new ArrayList<>();
			mlpToolKitList.add(mlpToolKit);
			mlpToolKitList.add(mlpToolKit1);
			mlpToolKitList.add(mlpToolKit2);
			mlpToolKitList.add(mlpToolKit3);
			mlpToolKitList.add(mlpToolKit4);
			mlpToolKitList.add(mlpToolKit5);

			JsonResponse<List<MLPToolkitType>> toolkitRes = new JsonResponse<>();
			toolkitRes.setResponseBody(mlpToolKitList);

			Mockito.when(filterController.getToolkitTypes(request, response)).thenReturn(toolkitRes);
			logger.info("Tool Kit values :  " + toolkitRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
