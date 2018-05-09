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

package org.acumos.portal.be.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

import org.acumos.cds.MessageSeverityCode;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.domain.MLPUser;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.AsyncServices;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.MessagingService;
import org.acumos.portal.be.service.NotificationService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLStepResult;
import org.acumos.portal.be.transport.NotificationRequestObject;
import org.acumos.portal.be.transport.UploadSolution;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.JsonUtils;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.wagon.ConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AsyncServicesImpl extends AbstractServiceImpl implements AsyncServices {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(AsyncServicesImpl.class);

	@Autowired
	private Environment env;

	@Autowired
	private NotificationService notificationService;

	@Autowired 
	private FileSystemStorageService fileSystemStorageService; 
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private MarketPlaceCatalogService catalogService;
	
	@Autowired
	private MessagingService messagingService;
	
	private static final String NOTIFICATION_TITLE = "Web Based Onboarding";
	private static final String STEP_SUCCESS = "SU";

	@Async
	public Future<String> initiateAsyncProcess() throws InterruptedException {
		log.info("###Start Processing with Thread id: " + Thread.currentThread().getId());
		log.debug(EELFLoggerDelegate.debugLogger, "process");

		String processInfo = String.format("Processing is Done with Thread id= %d", Thread.currentThread().getId());
		return new AsyncResult<>(processInfo);
	}

	@Override
	public Future<HttpResponse> callOnboarding(String uuid, String userId, UploadSolution solution, String provider, String access_token)
			throws InterruptedException, ClientProtocolException, IOException {

			log.info("inside callOnboarding start ---->>>");
		File directory = new File(env.getProperty("model.storage.folder.name") + File.separator + userId);
		File modelFile = null;
		File schemaFile = null;
		File metadataFile = null;
		File[] fList = directory.listFiles();
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = null;
		MLPNotification notification = new MLPNotification();
		
		if (fList != null) {
			for (File file : fList) {
				if (file.isFile() && file.getName().contains(".zip") || file.getName().contains(".jar") || file.getName().contains(".bin") || file.getName().contains(".tar") || file.getName().toUpperCase().contains(".R")) {
					modelFile = new File(file.getAbsolutePath());
				}
				if (file.isFile() && file.getName().contains(".proto")) {
					schemaFile = new File(file.getAbsolutePath());
				}
				if (file.isFile() && file.getName().contains(".json")) {
					metadataFile = new File(file.getAbsolutePath());
				}
			}
		}

		try {
			if (modelFile != null && schemaFile != null && metadataFile != null) {

				HttpPost post = new HttpPost(env.getProperty("onboarding.push.model.url"));

				if(StringUtils.isEmpty(provider)) {
					MLPUser user = userService.findUserByUserId(userId);
					String jwtToken = user.getAuthToken();
					post.setHeader("Authorization", jwtToken);
				} else {
					post.setHeader("Authorization", access_token);
				}
				if(StringUtils.isNotEmpty(provider)) {
					post.setHeader("provider", provider);
				}
				if(!StringUtils.isEmpty(uuid)){
					post.addHeader("tracking_id", uuid);
				}

				MultipartEntityBuilder builder = MultipartEntityBuilder.create();

				builder.setBoundary(UUID.randomUUID().toString());
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

				builder.addBinaryBody("model", new FileInputStream(modelFile), ContentType.MULTIPART_FORM_DATA,
						modelFile.getName());
				builder.addBinaryBody("metadata", new FileInputStream(metadataFile), ContentType.MULTIPART_FORM_DATA,
						metadataFile.getName());
				builder.addBinaryBody("schema", new FileInputStream(schemaFile), ContentType.MULTIPART_FORM_DATA,
						schemaFile.getName());

				HttpEntity entity = builder.build();
				post.setEntity(entity);

				response = httpclient.execute(post);
				log.info("inside callOnboarding response.getStatusLine().getStatusCode() ---->>>"+response.getStatusLine().getStatusCode());
				//MLPNotification notification = new MLPNotification();
				notification.setMsgSeverityCode(MessageSeverityCode.ME.toString());
				if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {
					InputStream instream = response.getEntity().getContent();
					String result = convertStreamToString(instream);

					ObjectMapper mapper = new ObjectMapper();
					log.info("inside callOnboarding if before readValue ---->>>");
					Map<String, Object> resp = mapper.readValue(result, Map.class);
					log.info("inside callOnboarding if after readValue ---->>>");
					log.info("inside callOnboarding if after resp.toString() ---->>>"+resp.toString());
					Map<String, Object> solutionStr = (Map<String, Object>) resp.get("result");
					
					 String newSolutionId = (String) solutionStr.get("solutionId");
					 String newSolutionName = (String) solutionStr.get("name");
					log.info(resp.toString());
					
					
					log.debug(resp.toString());
					log.debug(newSolutionId);
					log.debug(newSolutionName);
					
					//String newSolutionId = (String) solutionMap.get("solutionId");
					//String newSolutionName = (String) solutionMap.get("name");
					if (StringUtils.isNotEmpty(newSolutionName) && !solution.getName().equals(newSolutionName)) {
						MLSolution solutionDetail = catalogService.getSolution(newSolutionId);
						solutionDetail.setName(solution.getName());
						log.info("inside callOnboarding if before updateSolution ---->>>");
						catalogService.updateSolution(solutionDetail, newSolutionId);
						log.info("inside callOnboarding if after updateSolution ---->>>");
					}
					String notifMsg = "Solution " + solution.getName() + " Added to Catalog Successfully";
					notification.setMessage(notifMsg);
					notification.setTitle(NOTIFICATION_TITLE);
					notificationService.generateNotification(notification, userId);
					
					//Send notification to user according to preference
					Map<String, String> notifyBody = new HashMap<String, String>();
					notifyBody.put("solutionName", solution.getName());
					notifyOnboardingStatus(userId, "HI", notifMsg, notifyBody, "ONBD_SUCCESS");
				} else {
					InputStream instream = response.getEntity().getContent();
					String result = convertStreamToString(instream);

					ObjectMapper mapper = new ObjectMapper();
					log.info("inside callOnboarding else before readValue ---->>>");
					Map<String, Object> resp = mapper.readValue(result, Map.class);
					log.info("inside callOnboarding else after readValue ---->>>");
					log.info("inside callOnboarding else after resp.toString() ---->>>"+resp.toString());
					log.info(resp.toString());
					log.info((String) resp.get("errorMessage"));
					String notifMsg = "Add To Catalog Failed for solution " + solution.getName()
					+ ". Please restart the process again to upload the solution";
					notification.setMessage(notifMsg);
					notification.setTitle(NOTIFICATION_TITLE);
					log.info("inside callOnboarding else before generateNotification ---->>>");
					notificationService.generateNotification(notification, userId);
					
					//Send notification to user according to preference
					Map<String, String> notifyBody = new HashMap<String, String>();
					notifyBody.put("solutionName", solution.getName());
					notifyBody.put("errorMessage", (String) resp.get("errorMessage"));
					notifyOnboardingStatus(userId, "HI", "Add To Catalog Failed for solution " + solution.getName(), notifyBody, "ONBD_FAIL");
				}
			}
		// If disconnected from onboarding service, catch related exceptions here
		} catch (ConnectException|NoHttpResponseException e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Onboarding the solution - No response ", e);
			
			// Send a bell notification to the user to alert of failure
			sendBellNotification(userId, solution);
			
			// Update the on-screen progress tracker status to alert of failure
			sendTrackerNotification(uuid, userId);
			
			// Email) a notification to the user based on notification preference
			sendEmailNotification(userId, solution, e.getMessage());
		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Onboarding the solution ", e);
		} finally {
			httpclient.getConnectionManager().shutdown();
			// Remove all files once the process is completed
			log.info("inside finallly callOnboarding ---->>>");
			
			fileSystemStorageService.deleteAll(userId);
		}

		return new AsyncResult<HttpResponse>(response);
	}

	public void sendEmailNotification(String userId, UploadSolution solution, String errorMessage) {
		try {
			//Send notification to user according to preference
			Map<String, String> notifyBody = new HashMap<String, String>();
			notifyBody.put("solutionName", solution.getName());
			notifyBody.put("errorMessage", errorMessage);
			notifyOnboardingStatus(userId, "HI", "Add To Catalog Failed for solution " + solution.getName(), notifyBody, "ONBD_FAIL");
		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred sending notification email ", e);
		}
	}

	public MLPStepResult sendTrackerNotification(String uuid, String userId) {
		MLPStepResult stepResult = new MLPStepResult();
		stepResult.setTrackingId(uuid);
		stepResult.setUserId(userId);
		stepResult.setName("CreateMicroservice");
		stepResult.setStatusCode("FA");
		stepResult.setStepCode("OB");
		stepResult.setResult("Disconnected from onboarding");

		// If there are existing statuses, use last status for information
		List<MLStepResult> status = messagingService.callOnBoardingStatusList(userId, uuid);
		if (status.size() > 0) {
			MLStepResult lastResult = status.get(status.size()-1);
			
			stepResult.setStepCode(lastResult.getStepCode());
			stepResult.setSolutionId(lastResult.getSolutionId());
			stepResult.setRevisionId(lastResult.getRevisionId());
			stepResult.setArtifactId(lastResult.getArtifactId());
			
			if (lastResult.getStatusCode().equals(STEP_SUCCESS)) {
				switch(lastResult.getName()) {
					case "CreateMicroservice": stepResult.setName("Dockerize"); break;
					case "Dockerize": stepResult.setName("AddToRepository"); break;
					case "AddToRepository": stepResult.setName("CreateTOSCA"); break;
					case "CreateTOSCA": stepResult.setName("CreateSolution"); break;
				}
			} else {
				stepResult.setName(lastResult.getName());
			}
		}
		
		return messagingService.createStepResult(stepResult);
	}

	public MLNotification sendBellNotification(String userId, UploadSolution solution) {
		String notifMsg = "Add To Catalog Failed for solution " + solution.getName()
			+ ". Please restart the process again to upload the solution.";
		MLPNotification notification = new MLPNotification();
		notification.setMessage(notifMsg);
		notification.setTitle(NOTIFICATION_TITLE);
		notification.setMsgSeverityCode(MessageSeverityCode.ME.toString());
		log.info("inside callOnboarding Connect/NoHttpResponse catch before generateNotification ---->>>");
		notificationService.generateNotification(notification, userId);
		
		List<MLNotification> list = notificationService.getNotifications();
		return list.get(list.size()-1);
	}
	
	private void notifyOnboardingStatus(String userId, String severity, String notifySubject, Map<String, String> notifyBody, String messageStatusType) throws AcumosServiceException {
		NotificationRequestObject mailRequest = new NotificationRequestObject();
        mailRequest.setMessageType(messageStatusType);
        mailRequest.setSeverity(severity);
        mailRequest.setSubject(notifySubject);
        mailRequest.setUserId(userId);
        mailRequest.setNotificationData(notifyBody);
        notificationService.sendUserNotification(mailRequest);
	}
	
	// Only Python models are considered to be Compatible with ONAP
	@Override
	public Boolean checkONAPCompatible(String solutionId, String revisionId, String userId, String tracking_id) {
		log.debug(EELFLoggerDelegate.debugLogger, "checkONAPCompatible");
		
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		
		MLPStepResult stepResult = new MLPStepResult();

		stepResult.setTrackingId(tracking_id);
		stepResult.setUserId(userId);
		stepResult.setName("CheckCompatibility");
		stepResult.setStatusCode("ST");
		stepResult.setStepCode("OB");
		stepResult.setSolutionId(solutionId);
		stepResult.setRevisionId(revisionId);

		List<MLPArtifact> revisionArtifacts = dataServiceRestClient.getSolutionRevisionArtifacts(solutionId, revisionId);
		Boolean isCompatible = false;
		String metaDataUrl = null;
		ByteArrayOutputStream byteArrayOutputStream  = null;
		String name = null;

		for(MLPArtifact artifact : revisionArtifacts) {
			if (artifact.getArtifactTypeCode().equalsIgnoreCase("MD")) {
				metaDataUrl = artifact.getUri();
				stepResult.setArtifactId(artifact.getArtifactId());
			}
		}

		//messagingService.createStepResult(stepResult);

		if (metaDataUrl != null && !PortalUtils.isEmptyOrNullString(metaDataUrl)) {
			NexusArtifactClient artifactClient = getNexusClient();

			try {
				byteArrayOutputStream = artifactClient.getArtifact(metaDataUrl);
			} catch (ConnectionException e) {
				log.error(EELFLoggerDelegate.errorLogger, "Error Occured while fetching the aftifact for SolutionId={} and RevisionId ={}",
						solutionId, revisionId);
				stepResult.setStatusCode("FA");
				stepResult.setResult("Cannot Fetch MetaData Json");
				messagingService.createStepResult(stepResult);
				return isCompatible;
			}

			if(byteArrayOutputStream == null) {
				log.debug(EELFLoggerDelegate.debugLogger, "Artifact not for SolutionId={} and RevisionId ={}",
						solutionId, revisionId);
				stepResult.setStatusCode("FA");
				stepResult.setResult("Cannot Fetch MetaData Json");
				messagingService.createStepResult(stepResult);
				return isCompatible;
			}

			String metaDatajsonString = byteArrayOutputStream.toString();
			log.debug(EELFLoggerDelegate.debugLogger, "MetaData Json : " + metaDatajsonString);
			
			if(PortalUtils.isEmptyOrNullString(metaDatajsonString)) {
				stepResult.setStatusCode("FA");
				stepResult.setResult("Cannot Fetch MetaData Json");
				messagingService.createStepResult(stepResult);
				return isCompatible;
			}

			Map<String, Object> resp = JsonUtils.serializer().mapFromJson(metaDatajsonString);
			Map<String, Object> metaRuntime = (Map<String, Object>) resp.get("runtime");
			
			if (metaRuntime != null && metaRuntime.size() > 0) {
				name = (String) metaRuntime.get("name");
				log.debug("Type of model : " + name);
			}
			if (name != null && "PYTHON".equalsIgnoreCase(name)) {
				isCompatible = true;
				stepResult.setStatusCode(STEP_SUCCESS);
				messagingService.createStepResult(stepResult);
			}
		}
		
		if(!isCompatible) {
			stepResult.setStatusCode("FA");
			stepResult.setResult("Solution not a Python Model");
			messagingService.createStepResult(stepResult);
		}

		return isCompatible;
	}
	
	
	public HttpResponse convertSolutioToONAP(String solutionId, String revisionId, String userId, String tracking_id, String modName) {

		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		HttpResponse response = null;
		HttpClient httpclient = new DefaultHttpClient();
		
		URIBuilder builder = null;
		try {
			builder = new URIBuilder(env.getProperty("onboarding.push.model.dcae_url"));
		} catch (URISyntaxException e1) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while calling onboarding convertSolutioToONAP ", e1);
		}

		if (!StringUtils.isEmpty(solutionId)) {
			builder.setParameter("solutioId", solutionId);
			if(!StringUtils.isEmpty(modName) && "null".equalsIgnoreCase(modName)){
				builder.setParameter("modName", modName);
				log.debug("ONAP model name from user : " + modName);
			}else{
				MLPSolution solution = dataServiceRestClient.getSolution(solutionId);
				if(solution != null) {
					log.debug("No Solution Name given by user");
					String solutionName = env.getProperty("dcae.model.name.prefix") + "_" + solution.getName();
					builder.setParameter("modName", solutionName);
				}
			}
		}
		if (!StringUtils.isEmpty(revisionId)) {
			builder.setParameter("revisionId", revisionId);
		}
		
		HttpPost post = null;
		try {
			post = new HttpPost(builder.build());
		} catch (URISyntaxException e1) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while calling onboarding convertSolutioToONAP ", e1);
		}
		
		if (!StringUtils.isEmpty(userId)) {
			MLPUser user = userService.findUserByUserId(userId);
			String jwtToken = user.getAuthToken();
			post.setHeader("Authorization", jwtToken);
		}

		if (!StringUtils.isEmpty(tracking_id)) {
			post.addHeader("tracking_id", tracking_id);
		}

		try {
			log.debug(EELFLoggerDelegate.debugLogger, "Call Onboarding URI : " + post.getURI());
			response = httpclient.execute(post);
		} catch (UnsupportedEncodingException e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while convertSolutioToONAP ", e);
		} catch (ClientProtocolException e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while convertSolutioToONAP ", e);
		} catch (IOException e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while convertSolutioToONAP ", e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}

		return response;
	}

	private String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				
			}
		}
		return sb.toString();
	}

	public void setEnvironment(Environment environment){
		env = environment;
	}
}
