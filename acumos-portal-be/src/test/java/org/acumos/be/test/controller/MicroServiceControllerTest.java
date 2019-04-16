package org.acumos.be.test.controller;

import static org.mockito.Mockito.when;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.controller.MicroServiceController;
import org.acumos.portal.be.service.MSGenService;
import org.acumos.portal.be.service.impl.MSGenServiceImpl;
import org.acumos.portal.be.transport.MSGeneration;
import org.acumos.portal.be.transport.MSResponse;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class MicroServiceControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	MicroServiceController microServiceController;
	@Mock
	private MSGenService mSGenService;
	@Mock
	MSGenServiceImpl mSGenServiceImpl;

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Test
	public void generateMicroserviceTest() {
		try {
			MSResponse msResponse = new MSResponse();
			HashMap<String, Object> parms = new HashMap<>();
			parms.put("solutionId", "3a8f6092-989d-4320-9df7-91e168859438");
			parms.put("name", "Customer_segmentation");
			parms.put("userId", "19a554b1-4b00-4135-a122-2b6061480185");
			JsonResponse<MSResponse> data = new JsonResponse<>();
			msResponse.setStatus("SUCCESS");
			msResponse.setModelName("Customer_segmentation");
			msResponse.setResult(parms);

			MSGeneration mSGeneration = new MSGeneration();
			mSGeneration.setDeploymentEnv("X86");
			mSGeneration.setModName("Customer_segmentation");
			mSGeneration.setSolutioId("3a8f6092-989d-4320-9df7-91e168859438");
			mSGeneration.setRevisionId("73b72601-b961-4f4e-9a91-c31a097858ce");
			String Authorization="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZWNobWRldiIsInJvbGUiOlt7InBlcm1pc3Npb25MaXN0IjpudWxsLCJyb2xlQ291bnQiOjAsInJvbGVJZCI6IjEyMzQ1Njc4LWFiY2QtOTBhYi1jZGVmLTEyMzQ1Njc4OTBhYiIsIm5hbWUiOiJNTFAgU3lzdGVtIFVzZXIiLCJhY3RpdmUiOnRydWUsImNyZWF0ZWQiOnsiZXBvY2hTZWNvbmQiOjE1NDU0MDQzNjIsIm5hbm8iOjB9LCJtb2RpZmllZCI6bnVsbH1dLCJjcmVhdGVkIjoxNTU0ODIxMzQ4NTM2LCJleHAiOjE1NTQ4MjMxNDgsIm1scHVzZXIiOnsiY3JlYXRlZCI6eyJlcG9jaFNlY29uZCI6MTU0ODM1NjAyNCwibmFubyI6MH0sIm1vZGlmaWVkIjp7ImVwb2NoU2Vjb25kIjoxNTU0ODIxMzMxLCJuYW5vIjoxNzAwMDAwMDB9LCJ1c2VySWQiOiIxOWE1NTRiMS00YjAwLTQxMzUtYTEyMi0yYjYwNjE0ODAxODUiLCJmaXJzdE5hbWUiOiJNdWtlc2giLCJtaWRkbGVOYW1lIjpudWxsLCJsYXN0TmFtZSI6Ik1hbnRhbiIsIm9yZ05hbWUiOm51bGwsImVtYWlsIjoiTU0wMDU0MjIzN0BUZWNoTWFoaW5kcmEuY29tIiwibG9naW5OYW1lIjoidGVjaG1kZXYiLCJsb2dpbkhhc2giOm51bGwsImxvZ2luUGFzc0V4cGlyZSI6bnVsbCwiYXV0aFRva2VuIjpudWxsLCJhY3RpdmUiOnRydWUsImxhc3RMb2dpbiI6eyJlcG9jaFNlY29uZCI6MTU1NDgyMTMzMSwibmFubyI6MTY5MDAwMDAwfSwibG9naW5GYWlsQ291bnQiOm51bGwsImxvZ2luRmFpbERhdGUiOm51bGwsInBpY3R1cmUiOm51bGwsImFwaVRva2VuIjoiN2QyZWViNzlhZGU1NDdmNmFjMDdjOTdiNDg2NDJiMjUiLCJ2ZXJpZnlUb2tlbkhhc2giOm51bGwsInZlcmlmeUV4cGlyYXRpb24iOm51bGwsInRhZ3MiOlt7InRhZyI6IjEuMC4wIn1dfX0.PaB_4n4zUgtr1iDzXGJCmadpHDMzt57yyYkG03hAUIYiNQvp67goLaWNtUbTGhYSFvYKdeDl4m9ShJiWS2aXUQ";
			JsonRequest<MSGeneration> mSGenerationReq = new JsonRequest<>();
			mSGenerationReq.setBody(mSGeneration);

			data.setResponseBody(msResponse);

			ResponseEntity<MSResponse> responseEntity = ResponseEntity.ok().build();

			when(mSGenService.generateMicroservice(mSGeneration)).thenReturn(responseEntity);

			data = microServiceController.generateMicroservice(request, mSGenerationReq, response,Authorization);

			data.setStatusCode(201);
			data.setResponseDetail("generated microservice Successfully");
		
		} catch (Exception e) {
			logger.error("Error while generateMicroserviceTest", e);
		}

	}

}
