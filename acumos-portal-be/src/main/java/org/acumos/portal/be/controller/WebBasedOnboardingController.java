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

package org.acumos.portal.be.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.domain.MLPStepStatus;
import org.acumos.cds.domain.MLPStepType;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.service.AsyncServices;
import org.acumos.portal.be.service.MessagingService;
import org.acumos.portal.be.transport.Broker;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLStepResult;
import org.acumos.portal.be.transport.UploadSolution;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/webBasedOnBoarding")
public class WebBasedOnboardingController  extends AbstractController {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate
			.getLogger(MarketPlaceCatalogServiceController.class);


	@Autowired
	private AsyncServices asyncService;

	@Autowired
	private MessagingService messagingService;
	
	
	//@Async
	@ApiOperation(value = "adding Solution for Market Place Catalog.", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.ADD_TO_CATALOG}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE<MLSolution>> addToCatalog(@RequestHeader("Authorization") String authorization, @RequestHeader(value="provider", required=false) String provider ,@RequestBody JsonRequest<UploadSolution> restPageReq, @PathVariable("userId") String userId ) {
		
		log.debug(EELFLoggerDelegate.debugLogger, "addToCatalog");
		JsonResponse<RestPageResponseBE<MLSolution>> data = new JsonResponse<>();	    
		String uuid = UUID.randomUUID().toString();
		
		
		try {
			if (restPageReq != null) {
				UploadSolution solution = restPageReq.getBody();
				//this will just call the async service and 
				//futher that async service will proceed untill the task is not completed.
				//restPageReq.getBody() will get( modelType, modelToolkitType, name) which required to proceed
				//String provider = request.getHeader("provider");
				String access_token = authorization;
				ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);				
				FutureTask<HttpResponse> futureTask_1 = new FutureTask<HttpResponse>(new Callable<HttpResponse>() {
		            @Override
		            public HttpResponse call() throws FileNotFoundException, ClientProtocolException, InterruptedException, IOException {
		                return (HttpResponse) asyncService.callOnboarding(uuid, userId, solution, provider, access_token);
		            }
		        });				
				executor.execute(futureTask_1);
				executor.shutdown();
				
				/*Callable<Integer> task = new Callable<Integer>() {
		            public Integer call() {
		                // fake computation time
		                try {
		                    Thread.sleep(5000);
		                } catch (InterruptedException ex) {
		                    ex.printStackTrace();
		                }
		                
		                //asyncService.callOnboarding(uuid, userId, solution, provider, access_token);
		 
		                return asyncService.callOnboarding(uuid, userId, solution, provider, access_token);
		            }
		        }; 
				
				
				Callable<String> callableTask = () -> {
				    TimeUnit.MILLISECONDS.sleep(300);
				    return "Task's execution";
				};
				
				Future<HttpResponse> future = executor.submit(new Callable());
		        try {
		        	future = asyncService.callOnboarding(uuid, userId, solution, provider, access_token);
		        } catch (InterruptedException | ExecutionException e) {
		            e.printStackTrace();
		        }				
				//executor.submit(task)
				//asyncService.callOnboarding(uuid, userId, solution, provider, access_token);
*/				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail(uuid);
			}
			/*if (mlSolutions != null) {
				
				data.setResponseBody(mlSolutions);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions OnBoarded Successfully");
			}*/
		} /*catch (FileNotFoundException e) {
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("FileNotFoundException Occurred OnBoarding Solutions for Market Place Catalog");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred OnBoarding Solutions for Market Place Catalog",
					e);
		}catch (ClientProtocolException e) {
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("ClientProtocolException Occurred OnBoarding Solutions for Market Place Catalog");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred OnBoarding Solutions for Market Place Catalog",
					e);
		}catch (InterruptedException e) {
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("InterruptedException Occurred OnBoarding Solutions for Market Place Catalog");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred OnBoarding Solutions for Market Place Catalog",
					e);
		}catch (ConnectException e) {
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("ConnectException Occurred OnBoarding Solutions for Market Place Catalog");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred OnBoarding Solutions for Market Place Catalog",
					e);
		}*/catch (Exception e) {
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred OnBoarding Solutions for Market Place Catalog");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred OnBoarding Solutions for Market Place Catalog",
					e);
		}
		return data;
	}
	
	
	
	@ApiOperation(value = "getting message for the OnBoarded Solution.", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.MESSAGING_STATUS}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLStepResult>> messagingStatus(@PathVariable("userId") String userId, @PathVariable("trackingId") String trackingId) {
		
		log.debug(EELFLoggerDelegate.debugLogger, "messagingStatus");
		JsonResponse<List<MLStepResult>> data = new JsonResponse<>();
	    	     
		try {
			 		
			List<MLStepResult> responseBody =  messagingService.callOnBoardingStatusList(userId, trackingId);
			data.setResponseBody(responseBody);			 
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Solutions OnBoarded Successfully");
			 			 
		}catch (Exception e) {
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred while providing Status of the Solutions OnBoarded for Market Place Catalog");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while providing Status of the Solutions OnBoarded for Market Place Catalog",
					e);
		}
		return data;
	}
	
	/**
	 * 
	 * @param stepResult
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "Create StepResult", response = MLPStepResult.class)
	@RequestMapping(value = { APINames.CREATE_STEP_RESULT }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPStepResult> createStepResult(@RequestBody MLPStepResult stepResult,
			HttpServletResponse response) {

		JsonResponse<MLPStepResult> data = new JsonResponse<>();
		try {
			if (stepResult != null) {
				MLPStepResult result = messagingService.createStepResult(stepResult);
				if (result != null) {
					data.setResponseBody(result);
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
					data.setResponseDetail("Step result created Successfully");
					log.debug(EELFLoggerDelegate.debugLogger, "Step result created Successfully :  ");
				} else {
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
					data.setResponseDetail("Error occured while createStepResult");
					log.error(EELFLoggerDelegate.errorLogger, "Error Occurred createStepResult :");
				}
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error occured while createStepResult");
				log.error(EELFLoggerDelegate.errorLogger, "Error Occurred createStepResult :");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception occured while createStepResult");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred createStepResult :", e);
		}
		return data;
	}
	
	/**
	 * 
	 * @param stepResult
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "Create StepResult", response = MLPStepResult.class)
	@RequestMapping(value = { APINames.UPDATE_STEP_RESULT }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPStepResult> updateStepResult(@RequestBody MLPStepResult stepResult,
			HttpServletResponse response) {

		JsonResponse<MLPStepResult> data = new JsonResponse<>();
		try {
			if (stepResult != null) {
				messagingService.updateStepResult(stepResult);
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
					data.setResponseDetail("Step result updated Successfully");
					log.debug(EELFLoggerDelegate.debugLogger, "Step result updated Successfully :  ");		
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error occured while updateStepResult");
				log.error(EELFLoggerDelegate.errorLogger, "Error Occurred updateStepResult :");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception occured while updateStepResult");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred updateStepResult :", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Create StepResult", response = MLPStepResult.class)
	@RequestMapping(value = { APINames.DELETE_STEP_RESULT }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPStepResult> deleteStepResult(HttpServletRequest request,
			@PathVariable("userId") Long stepResultId,
			HttpServletResponse response) {

		JsonResponse<MLPStepResult> data = new JsonResponse<>();
		try {
			if (stepResultId != null) {
				messagingService.deleteStepResult(stepResultId);
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
					data.setResponseDetail("Step result deleted Successfully");
					log.debug(EELFLoggerDelegate.debugLogger, "Step result deleted Successfully :  ");		
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error occured while deleteStepResult");
				log.error(EELFLoggerDelegate.errorLogger, "Error Occurred deleteStepResult :");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception occured while deleteStepResult");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred deleteStepResult :", e);
		}
		return data;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "Fetch Step statuses", response = MLPStepStatus.class)
	@RequestMapping(value = { APINames.GET_STEP_STATUSES }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLPStepStatus>> getStepStatuses(HttpServletRequest request, HttpServletResponse response) {
		JsonResponse<List<MLPStepStatus>> data = new JsonResponse<>();
		try {
			List<MLPStepStatus> stepStatusesList = messagingService.getStepStatuses();
			if (stepStatusesList != null) {
				data.setResponseBody(stepStatusesList);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Step result created Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "Step result created Successfully :  ");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error occured while createStepResult");
				log.error(EELFLoggerDelegate.errorLogger, "Error Occurred createStepResult :");
			}

		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception occured while createStepResult");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred createStepResult :", e);
		}
		return data;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "Fetch Step types", response = MLPStepType.class)
	@RequestMapping(value = { APINames.GET_STEP_TYPES }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLPStepType>> getStepTypes(HttpServletRequest request, HttpServletResponse response) {
		JsonResponse<List<MLPStepType>> data = new JsonResponse<>();
		try {
			List<MLPStepType> stepStatusesList = messagingService.getStepTypes();
			if (stepStatusesList != null) {
				data.setResponseBody(stepStatusesList);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Step statuses fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "Step statuses fetched Successfully :  ");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error occured while getStepTypes");
				log.error(EELFLoggerDelegate.errorLogger, "Error Occurred getStepTypes :");
			}

		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception occured while getStepTypes");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred getStepTypes :", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Searching step result with solution id", response = MLPStepResult.class)
	   @RequestMapping(value = {APINames.SEARCH_STEP_RESULT}, method = RequestMethod.GET, produces = APPLICATION_JSON)
	   @ResponseBody
	    public JsonResponse<List<MLPStepResult>> findStepresultBySolutionId(@PathVariable("solutionId") String solutionId, @PathVariable("revisionId") String revisionId) {
	        JsonResponse<List<MLPStepResult>> data = new JsonResponse<>();
	        if (solutionId != null) {
	            try {
	                List<MLPStepResult> mlpStepresult = messagingService.findStepresultBySolutionId(solutionId,revisionId);
	                if (mlpStepresult != null) {
	                    data.setResponseBody(mlpStepresult);
	                    data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
	                    data.setResponseDetail("Step result fetched Successfully");
	                    log.debug(EELFLoggerDelegate.debugLogger, "Step result fetched Successfully :  ");
	                }
	            } catch (Exception e) {
	                data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
	                data.setResponseDetail("Exception occured while searchStepResults");
	                log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred searchStepResults :", e);
	            }
	        }
	        return data;
	    }
	
	/*@ApiOperation(value = "getting message for the OnBoarded Solution.", response = MLStepResult.class)
	@RequestMapping(value = { APINames.MESSAGING_STATUS}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLStepResult> messagingStatus(@PathVariable("userId") String userId, @PathVariable("trackingId") String trackingId) {
		
		log.debug(EELFLoggerDelegate.debugLogger, "messagingStatus");
		JsonResponse<MLStepResult> data = new JsonResponse<>();	     
		try {			 
			
			MLStepResult responseBody =  null;
			responseBody = messagingService.callOnBoardingStatus(userId, trackingId);
			data.setResponseBody(responseBody);			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Solutions OnBoarded Successfully");		 
			 
		}catch (Exception e) {
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred while providing Status of the Solutions OnBoarded for Market Place Catalog");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while providing Status of the Solutions OnBoarded for Market Place Catalog",
					e);
		}
		return data;
	}*/
	
	
	@ApiOperation(value = "dummy api for Broker.", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.BROKER}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Broker> messagingStatus(@RequestBody JsonRequest<Broker> brokerDetail) {
		
		log.debug(EELFLoggerDelegate.debugLogger, "broker details");
		
		JsonResponse<Broker> data = new JsonResponse<>();
	    	     
		try {
			 		Broker responseBody = new Broker();
			 		responseBody.setResponseName("test_name");
			 		responseBody.setResponseContent("test_COntent");
					data.setResponseBody(responseBody );
			 
			 			 
		}catch (Exception e) {
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred while providing Status of the Solutions OnBoarded for Market Place Catalog");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while providing Status of the Solutions OnBoarded for Market Place Catalog",
					e);
		}
		return data;
	}
	
	@RequestMapping(value = { APINames.CONVERT_TO_ONAP}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLStepResult>> convertToOnap(@PathVariable("solutionId") String solutionId, @PathVariable("revisionId") String revisionId, 
			@PathVariable("userId") String userId,@PathVariable("modName") String modName) {
		JsonResponse<List<MLStepResult>> data = new JsonResponse<>();
		Boolean isONAPCompatible = false;
		String tracking_id = UUID.randomUUID().toString();

		isONAPCompatible = asyncService.checkONAPCompatible(solutionId, revisionId, userId, tracking_id);

		if(isONAPCompatible) {
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);				
			FutureTask<HttpResponse> futureTask_1 = new FutureTask<HttpResponse>(new Callable<HttpResponse>() {
				@Override
				public HttpResponse call() throws FileNotFoundException, ClientProtocolException, InterruptedException, IOException {
				     return (HttpResponse) asyncService.convertSolutioToONAP(solutionId, revisionId, userId, tracking_id, modName);
						}
			});	
			executor.execute(futureTask_1);
			executor.shutdown();
		} else {
			//Create failed step result
		}

		data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
		data.setResponseDetail(tracking_id.toString());
		return data;
	}
}
