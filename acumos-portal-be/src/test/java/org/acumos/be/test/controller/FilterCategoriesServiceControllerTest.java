package org.acumos.be.test.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.controller.FilterCategoriesServiceController;
import org.acumos.portal.be.service.FilterCategoriesService;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class FilterCategoriesServiceControllerTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	FilterCategoriesServiceController filterCategoriesServiceController;
	@Mock
	FilterCategoriesService filterCategoriesService;
	
	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	@Test
	public void getSolutionsCategoryTypesTest() {
		List<MLPCodeNamePair> list=getInput();
		when(filterCategoriesService.getSolutionCategoryTypes()).thenReturn(list);
		JsonResponse<List<MLPCodeNamePair>> dataPass=filterCategoriesServiceController.getSolutionsCategoryTypes(request, response);
		assertNotNull(dataPass);
		assertEquals(list, dataPass.getResponseBody());
		
		when(filterCategoriesService.getSolutionCategoryTypes());
		filterCategoriesServiceController.getSolutionsCategoryTypes(request, response);
	}
	
	@Test
	public void getSolutionsAccessTypesTest() {
		List<MLPCodeNamePair> list=getInput();
		when(filterCategoriesService.getSolutionAccessTypes()).thenReturn(list);
		JsonResponse<List<MLPCodeNamePair>> dataPass=filterCategoriesServiceController.getSolutionsAccessTypes();
		assertNotNull(dataPass);
		assertEquals(list, dataPass.getResponseBody());
		
		when(filterCategoriesService.getSolutionAccessTypes());
		filterCategoriesServiceController.getSolutionsAccessTypes();
	}
	
	@Test
	public void getToolkitTypesTest() {
		List<MLPCodeNamePair> list=getInput();
		when(filterCategoriesService.getToolkitTypes()).thenReturn(list);
		JsonResponse<List<MLPCodeNamePair>> dataPass=filterCategoriesServiceController.getToolkitTypes(request, response);
		assertNotNull(dataPass);
		assertEquals(list, dataPass.getResponseBody());
		
		when(filterCategoriesService.getToolkitTypes());
		filterCategoriesServiceController.getToolkitTypes(request, response);
	}
	
	private List<MLPCodeNamePair> getInput() {
		MLPCodeNamePair codeNamePair=new MLPCodeNamePair();
		codeNamePair.setCode("MyCode");
		codeNamePair.setName("MyName");
		List<MLPCodeNamePair> list=new ArrayList<>();
		list.add(codeNamePair);
		return list;
	}
	
}
