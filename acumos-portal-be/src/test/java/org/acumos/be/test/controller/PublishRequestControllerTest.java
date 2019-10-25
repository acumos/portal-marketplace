package org.acumos.be.test.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.common.CredentialsService;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.PagableResponse;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.controller.PublishRequestController;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.PublishRequestService;
import org.acumos.portal.be.transport.MLPublishRequest;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.acumos.securityverification.domain.Workflow;
import org.acumos.securityverification.service.SecurityVerificationClientServiceImpl;
import org.acumos.securityverification.utils.SVConstants;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;


public class PublishRequestControllerTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	PublishRequestController publishRequestController;

	@Mock
	PublishRequestService publishRequestService;

	@Mock
	SecurityVerificationClientServiceImpl sv;

	@Mock
	CredentialsService credentials;

	@Mock
	Environment env;

	@Mock
	MarketPlaceCatalogService catalog;


	@Before
	public void setup() {
		System.out.println("SecurityContextHolder" + SecurityContextHolder.getContext().toString());

	}

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Test
	public void searchPublishRequestByRevIdTest() {
		MLPublishRequest publishRequest = getMLPPublishRequest();
		when(publishRequestService.searchPublishRequestByRevId("revisionId123"))
				.thenReturn(publishRequest);
		JsonResponse<MLPublishRequest> dataPass =
				publishRequestController.searchPublishRequestByRevId(request, "revisionId123", response);
		assertNotNull(dataPass);
		assertEquals(publishRequest, dataPass.getResponseBody());
		publishRequestController.searchPublishRequestByRevId(request, "revisionId123", response);
	}

	@Test
	public void searchPublishRequestByRevCatIdTest() {
		MLPublishRequest publishRequest = getMLPPublishRequest();
		when(publishRequestService.searchPublishRequestByRevAndCatId(publishRequest.getRevisionId(),
				publishRequest.getCatalogId())).thenReturn(publishRequest);
		JsonResponse<MLPublishRequest> dataPass =
				publishRequestController.searchPublishRequestByRevCatId(request,
						publishRequest.getRevisionId(), publishRequest.getCatalogId(), response);
		assertNotNull(dataPass);
		assertEquals(publishRequest, dataPass.getResponseBody());
		publishRequestController.searchPublishRequestByRevCatId(request, publishRequest.getRevisionId(),
				publishRequest.getCatalogId(), response);
	}

	@Test
	public void getAllPublishRequestTest() {
		JsonRequest<RestPageRequestPortal> reqObj = new JsonRequest<>();
		RestPageRequestPortal restpagerequestPortal = new RestPageRequestPortal();
		restpagerequestPortal.setActive(true);
		restpagerequestPortal.setPublished(true);
		restpagerequestPortal.setSortBy("MR");
		Map<String, String> fieldToDirectionMap = new HashMap<>();
		RestPageRequest pageRequest = new RestPageRequest();
		pageRequest.setSize(9);
		pageRequest.setPage(0);
		fieldToDirectionMap.put("modified", "DESC");
		pageRequest.setFieldToDirectionMap(fieldToDirectionMap);
		restpagerequestPortal.setPageRequest(pageRequest);
		reqObj.setBody(restpagerequestPortal);
		PagableResponse<List<MLPublishRequest>> mlPublishRequestList = new PagableResponse<>();
		MLPublishRequest publishRequest = getMLPPublishRequest();
		List<MLPublishRequest> list = new ArrayList<>();
		list.add(publishRequest);
		mlPublishRequestList.setResponseBody(list);
		when(publishRequestService.getAllPublishRequest(pageRequest)).thenReturn(mlPublishRequestList);
		PagableResponse<List<MLPublishRequest>> mlPublishRequestListPass =
				publishRequestController.getAllPublishRequest(request, reqObj, response);
		assertNotNull(mlPublishRequestListPass);
		publishRequestController.getAllPublishRequest(request, reqObj, response);

	}

	@Test
	public void getPublishRequestTest() {
		MLPublishRequest publishRequest = getMLPPublishRequest();
		when(publishRequestService.getPublishRequestById(publishRequest.getPublishRequestId()))
				.thenReturn(publishRequest);
		JsonResponse<MLPublishRequest> dataPass = publishRequestController.getPublishRequest(request,
				publishRequest.getPublishRequestId(), response);
		assertNotNull(dataPass);
		assertEquals(publishRequest, dataPass.getResponseBody());
		publishRequestController.getPublishRequest(request, publishRequest.getPublishRequestId(),
				response);
	}

	@Test
	public void updatePublishRequestTest() {
		MLPublishRequest publishRequest = getMLPPublishRequest();
		JsonRequest<MLPublishRequest> mlPublishRequest = new JsonRequest<>();
		mlPublishRequest.setBody(publishRequest);
		when(publishRequestService.getPublishRequestById(publishRequest.getPublishRequestId()))
				.thenReturn(publishRequest);
		Workflow workflow = new Workflow();
		workflow.setReason("Workflow Reason");
		workflow.setWorkflowAllowed(true);
		when(credentials.getLoggedInUserId()).thenReturn("admin");
		when(sv.securityVerificationScan(publishRequest.getSolutionId(), publishRequest.getRevisionId(),
				SVConstants.PUBLISHPUBLIC, "admin")).thenReturn(workflow);
		Workflow resultWorkflow = null;
		try {
			resultWorkflow = publishRequestController.performSVScan(publishRequest.getSolutionId(),
					publishRequest.getRevisionId(), SVConstants.PUBLISHPUBLIC, "admin").get();
		} catch (InterruptedException | ExecutionException e) {
			fail(e.getMessage());
		}
		assertNotNull(resultWorkflow);
		publishRequestController.updatePublishRequest(request,
				String.valueOf(publishRequest.getPublishRequestId()), mlPublishRequest, response);
	}

	@Test
	public void updatePublishRequestByIdTest() throws AcumosServiceException {
		MLPublishRequest publishRequest = getMLPPublishRequest();
		request.setAttribute("loginUserId", publishRequest.getRequestUserId());
		// request.setAttribute(publishRequest.getRequestUserId(), publishRequest);
		when(publishRequestService.withdrawPublishRequest(publishRequest.getPublishRequestId(),
				"UserId123")).thenReturn(publishRequest);
		JsonResponse<MLPublishRequest> dataPass = publishRequestController.updatePublishRequest(request,
				publishRequest.getPublishRequestId(), response);
		assertNotNull(dataPass);
		assertEquals(publishRequest, dataPass.getResponseBody());
	}

	private MLPublishRequest getMLPPublishRequest() {
		MLPublishRequest publishRequest = new MLPublishRequest();
		publishRequest.setCatalogId("CatId123");
		publishRequest.setRevisionId("revisionId123");
		publishRequest.setSolutionId("solutionId123");
		publishRequest.setRequestUserId("UserId123");
		publishRequest.setSolutionName("MySolution");
		publishRequest.setRevisionName("MyRev123");
		publishRequest.setPublishRequestId(101);
		publishRequest.setRequestStatusCode("AP");
		return publishRequest;
	}
}
