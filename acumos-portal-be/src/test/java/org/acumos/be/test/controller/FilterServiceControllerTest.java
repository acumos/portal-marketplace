package org.acumos.be.test.controller;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.acumos.portal.be.controller.FilterCategoriesServiceController;
import org.acumos.portal.be.service.impl.FilterCategoriesServiceImpl;
import org.acumos.portal.be.service.impl.MockCommonDataServiceRestClientImpl;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class FilterServiceControllerTest {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(FilterServiceControllerTest.class);

	
	@Mock
	private FilterCategoriesServiceImpl service;

	private MockMvc mockMvc;

	@InjectMocks
	private FilterCategoriesServiceController controller;

	@Before
	public void setUp() throws Exception {
		mockMvc = standaloneSetup(controller).build();

	}

	@Test
	public void getSolutionsCategoryTypes() throws Exception {

		MockCommonDataServiceRestClientImpl mockCommonDataService = new MockCommonDataServiceRestClientImpl();

		when(service.getSolutionCategoryTypes()).thenReturn(mockCommonDataService.getModelTypes());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/filter/modeltype")
				.accept(MediaType.APPLICATION_JSON)
				.content(
						"{\"response_body\": [{\"typeCode\": \"CL\",\"typeName\": \"Classification\"},{\"typeCode\": \"DT\",\"typeName\": \"Data Transformer\"},{\"typeCode\": \"PR\",\"typeName\": \"Prediction\"},{\"typeCode\": \"RG\",\"typeName\": \"Regression\"}]}")
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk())
				.andExpect(jsonPath("$.response_detail").exists()).andDo(print()).andReturn();
		String content = result.getResponse().getContentAsString();
		logger.info(content);
		org.junit.Assert.assertNotNull(content);
	}

	@Test
	public void getSolutionsAccessTypesTest() throws Exception {
		MockCommonDataServiceRestClientImpl mockCommonDataService = new MockCommonDataServiceRestClientImpl();

		when(service.getSolutionAccessTypes()).thenReturn(mockCommonDataService.getAccessTypes());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/filter/accesstype")
				.accept(MediaType.APPLICATION_JSON)
				.content(
						 "{\"response_body\": [{\"accessCode\": \"OR\",\"accessName\": \"Organization\"},{\"accessCode\": \"PB\",\"accessName\": \"Public\"},{\"accessCode\": \"PR\",\"accessName\": \"Private\"}]}").contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk())
				.andExpect(jsonPath("$.response_detail").exists()).andDo(print()).andReturn();
		String content = result.getResponse().getContentAsString();
		org.junit.Assert.assertNotNull(content);
		logger.info(content);
	}
	
	@Test
	public void getToolKitTest() throws Exception{
		MockCommonDataServiceRestClientImpl mockCommonDataService = new MockCommonDataServiceRestClientImpl();

		when(service.getToolkitTypes()).thenReturn(mockCommonDataService.getToolkitTypes());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/filter/toolkitType")
				.accept(MediaType.APPLICATION_JSON)
				.content("{\"response_body\": [{\"toolkitCode\": \"CP\",\"toolkitName\": \"Composite Solution\"},{\"toolkitCode\": \"DS\",\"toolkitName\": \"Design Studio\"},{\"toolkitCode\": \"H2\",\"toolkitName\": \"H2O\"},{\"toolkitCode\": \"RC\",\"toolkitName\": \"RCloud\"},{\"toolkitCode\": \"SK\",\"toolkitName\": \"Scikit-Learn\"},{\"toolkitCode\": \"TF\",\"toolkitName\": \"TensorFlow\"} ]}")
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk())
				.andExpect(jsonPath("$.response_detail").exists()).andDo(print()).andReturn();
		String content = result.getResponse().getContentAsString();
		org.junit.Assert.assertNotNull(content);
		logger.info(content);
	}
}
