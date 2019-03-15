package org.acumos.be.test.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.PagableResponse;
import org.acumos.portal.be.controller.OnboardingHistoryController;
import org.acumos.portal.be.service.OnboardingHistoryService;
import org.acumos.portal.be.transport.MLStepResult;
import org.acumos.portal.be.transport.MLTask;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import java.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OnboardingHistoryControllerTest {
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate
			.getLogger(MarketPlaceServiceControllerTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	OnboardingHistoryService onboardingHistoryService;

	@InjectMocks
	private OnboardingHistoryController onboardingHistoryController;

	@Test
	public void getTasksTest() {
		MLTask task = new MLTask();
		String userId = "c11482fc-7527-4391-ae42-81d21ca6e0e0";
		task.setTaskCode("OB");
		task.setTaskId(771l);
		task.setCreatedtDate(Date.from(Instant.now()));
		task.setModifiedDate(Date.from(Instant.now()));
		task.setName("OnBoarding");
		task.setStatusCode("SU");
		task.setUserId(userId);
		task.setTrackingId("1fc1428f-3baf-418b-ba4e");
		task.setSolutionId("e065ef81-890f-4689-9298-d75c91860ac2");
		task.setRevisionId("529a6744-a9e0-4534-8066-bc78e35723dd");
		PagableResponse<List<MLTask>> data = new PagableResponse<>();
		List<MLTask> taskList = new ArrayList<MLTask>();
		taskList.add(task);

		Assert.assertNotNull(userId);
		Assert.assertNotNull(task);
		Assert.assertNotNull(taskList);
		data.setResponseBody(taskList);

		JsonRequest<RestPageRequestPortal> reqObj = new JsonRequest<>();
		RestPageRequestPortal restpagerequestPortal = new RestPageRequestPortal();

		RestPageRequest pageRequest = new RestPageRequest();
		pageRequest.setSize(9);
		pageRequest.setPage(0);
		restpagerequestPortal.setPageRequest(pageRequest);
		reqObj.setBody(restpagerequestPortal);

		Assert.assertNotNull(data);
		when(onboardingHistoryService.getTasks(reqObj.getBody(), userId)).thenReturn(data);
		data = onboardingHistoryController.getTasks(request, userId, reqObj, response);
		logger.info("getTasks fectched Successfully for the specified user..");
		Assert.assertNotNull(data);

	}

	@Test
	public void getStepResultsTest() {
		String userId = "c11482fc-7527-4391-ae42-81d21ca6e0e0";
		Long taskId = 771l;
		MLStepResult stepResult = new MLStepResult();
		stepResult.setTaskId(taskId);
		stepResult.setStepResultId(2653l);
		stepResult.setTrackingId("");
		stepResult.setSolutionId("e065ef81-890f-4689-9298-d75c91860ac2");
		stepResult.setRevisionId("529a6744-a9e0-4534-8066-bc78e35723dd");
		stepResult.setUserId(userId);
		stepResult.setName("CreateTOSCA");
		stepResult.setStatusCode("ST");
		stepResult.setResult("TOSCA Generation Started");
		stepResult.setStartDate(Date.from(Instant.now()));
		stepResult.setEndDate(Date.from(Instant.now()));
		Assert.assertNotNull(userId);
		Assert.assertNotNull(stepResult);
		List<MLStepResult> stepResultsList = new ArrayList<>();

		stepResultsList.add(stepResult);
		Assert.assertNotNull(stepResultsList);
		JsonResponse<List<MLStepResult>> data = new JsonResponse<>();
		data.setResponseBody(stepResultsList);
		when(onboardingHistoryService.getStepResults(taskId)).thenReturn(stepResultsList);

		data = onboardingHistoryController.getStepResults(request, taskId.toString(), response);
		logger.info("fectched Successfully stepResults for the particular taskId..");
		Assert.assertNotNull(data);

	}

}
