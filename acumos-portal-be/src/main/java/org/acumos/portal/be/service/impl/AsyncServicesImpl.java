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
import java.util.ArrayList;
import java.util.Arrays;
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
import org.acumos.portal.be.logging.ONAPLogConstants;
import org.acumos.portal.be.service.AsyncServices;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.MessagingService;
import org.acumos.portal.be.service.NotificationService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.MLNotification;
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
import org.slf4j.MDC;
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
	public Future<HttpResponse> callOnboarding(String uuid, MLPUser user, UploadSolution solution, String provider, String access_token)
			throws InterruptedException, ClientProtocolException, IOException {

			log.info("CallOnboarding service start");
		//File directory = new File(env.getProperty("model.storage.folder.name") + File.separator + userId);
		String directory = env.getProperty("model.storage.folder.name") + File.separator + user.getUserId();
		List<File> fileList = new ArrayList<>();
		File modelFile = null;
		File schemaFile = null;
		File metadataFile = null;
		//File[] fList = directory.listFiles();
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = null;
		MLPNotification notification = new MLPNotification();
		fileList = getListOfFiles(directory, fileList);
	
		if(fileList != null){
			for(File file : fileList){
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
				
				String tokenMode = env.getProperty("onboarding.tokenmode");
				if(tokenMode != null && tokenMode.equals("jwtToken")) {
					if(StringUtils.isEmpty(provider)) {
						post.setHeader("Authorization", user.getAuthToken());
					} else {
						post.setHeader("Authorization", access_token);
					}
				} else if(tokenMode != null && tokenMode.equals("apiToken")) {
					post.setHeader("Authorization", user.getLoginName() + ":" + user.getApiToken());
				}
				
				if(StringUtils.isNotEmpty(provider)) {
					post.setHeader("provider", provider);
				}
				if(!StringUtils.isEmpty(uuid)){
					post.addHeader("tracking_id", uuid);
				}

				post.setHeader("Request-ID", (String) MDC.get(ONAPLogConstants.MDCs.REQUEST_ID));
				log.info("CallOnboarding wit request Id : "+ (String) MDC.get(ONAPLogConstants.MDCs.REQUEST_ID));

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
					
					log.info("Response From Onboarding : {}", resp.toString());
					
					String notifMsg = "Solution " + solutionStr.get("name") + " On-boarded Successfully";
					notification.setMessage(notifMsg);
					notification.setTitle(NOTIFICATION_TITLE);
					notificationService.generateNotification(notification, user.getUserId());
					
					//Send notification to user according to preference
					Map<String, String> notifyBody = new HashMap<String, String>();
					notifyBody.put("solutionName", (String) solutionStr.get("name"));
					notifyOnboardingStatus(user.getUserId(), "HI", notifMsg, notifyBody, "ONBD_SUCCESS");
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
					
					String errorLog = getErrorLogArtiffact(uuid, user.getUserId());
					String notifMsg = "On-boarding Failed"
					+ ". Please restart the process again to upload the solution. " + errorLog;

					notification.setMessage(notifMsg);
					notification.setTitle(NOTIFICATION_TITLE);
					log.info("inside callOnboarding else before generateNotification ---->>>");
					notificationService.generateNotification(notification, user.getUserId());
					
					sendTrackerNotification(uuid, user.getUserId(), (String) resp.get("errorMessage"));
					
					//Send notification to user according to preference
					Map<String, String> notifyBody = new HashMap<String, String>();
					notifyBody.put("errorMessage", (String) resp.get("errorMessage"));
					notifyOnboardingStatus(user.getUserId(), "HI", "On-boarding Failed for solution ", notifyBody, "ONBD_FAIL");
				}
			}
		// If disconnected from onboarding service, catch related exceptions here
		} catch (ConnectException|NoHttpResponseException e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Onboarding the solution - No response ", e);
			
			// Send a bell notification to the user to alert of failure
			sendBellNotification(user.getUserId(), solution);
			
			// Update the on-screen progress tracker status to alert of failure
			sendTrackerNotification(uuid, user.getUserId(), "Failed to connect to onboarding");
			
			// Email) a notification to the user based on notification preference
			sendEmailNotification(user.getUserId(), solution, e.getMessage());
		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Onboarding the solution ", e);
		} finally {
			httpclient.getConnectionManager().shutdown();
			// Remove all files once the process is completed
			log.info("inside finallly callOnboarding ---->>>");
			
			fileSystemStorageService.deleteAll(user.getUserId());
		}

		return new AsyncResult<HttpResponse>(response);
	}

	private String getErrorLogArtiffact(String trackingId, String userId) {
		
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		String erlog = "";
		// Get All the status for the tracking Id
		List<MLStepResult> status = messagingService.callOnBoardingStatusList(userId, trackingId);
		
		MLStepResult resultStatus = status.stream().filter(stepResult -> stepResult.getRevisionId() != null && stepResult.getSolutionId() != null).findFirst().get();
		if(resultStatus != null) {
			List<MLPArtifact> artifactList = dataServiceRestClient.getSolutionRevisionArtifacts(resultStatus.getSolutionId(), resultStatus.getRevisionId());

			if(artifactList != null && !PortalUtils.isEmptyList(artifactList)) {
				MLPArtifact logArtifact = artifactList.stream().filter(artifact -> (artifact.getUri()).contains(".txt")).findFirst().orElse(null);
				if(logArtifact != null) {
					//generate the download log href as String
					erlog = "Click " + "<a href=\"/api/downloads/" + resultStatus.getSolutionId() + "?artifactId="
							+ logArtifact.getArtifactId() + "&revisionId=" + resultStatus.getRevisionId() + "&userId="
							+ userId + "&jwtToken={{auth}}\" >here</a> to download logs.";
				}
			}
			
		}
		return erlog;
	}

	public void sendEmailNotification(String userId, UploadSolution solution, String errorMessage) {
		try {
			//Send notification to user according to preference
			Map<String, String> notifyBody = new HashMap<String, String>();
			notifyBody.put("errorMessage", errorMessage);
			notifyOnboardingStatus(userId, "HI", "Add To Catalog Failed for solution ", notifyBody, "ONBD_FAIL");
		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred sending notification email ", e);
		}
	}
  
	private List<File> getListOfFiles(String directoryName, List<File> files) throws IOException {
        File directory = new File(directoryName);
        // get all the files from a directory
        File[] fList = directory.listFiles();
        files.addAll(Arrays.asList(fList));
        if(fList != null){
            for (File file : fList) {
                if (file.isFile()) {
                    //files.add(file);
                } else if (file.isDirectory()) {
                	getListOfFiles(file.getAbsolutePath(), files);
                }
            }
        }
        return files;
    } 
	public MLPStepResult sendTrackerNotification(String uuid, String userId, String message) {
		List<String> steps = new ArrayList<String>(5);
		steps.add("CreateSolution");
		steps.add("AddArtifact");
		steps.add("CreateTOSCA");
		steps.add("Dockerize");
		steps.add("AddDockerImage");
		
		MLPStepResult stepResult = new MLPStepResult();
		stepResult.setTrackingId(uuid);
		stepResult.setUserId(userId);
		stepResult.setName(steps.get(0));
		stepResult.setStatusCode("FA");
		stepResult.setStepCode("OB");
		stepResult.setResult(message);

		// If there are existing statuses, use last status for information
		List<MLStepResult> status = messagingService.callOnBoardingStatusList(userId, uuid);
		if (status.size() > 0) {
			MLStepResult lastResult = status.get(status.size()-1);
			
			if (lastResult.getStatusCode().equals("FA")) {
				return null;
			}
			
			stepResult.setStepCode(lastResult.getStepCode());
			stepResult.setSolutionId(lastResult.getSolutionId());
			stepResult.setRevisionId(lastResult.getRevisionId());
			stepResult.setArtifactId(lastResult.getArtifactId());
			
			if (lastResult.getStatusCode().equals(STEP_SUCCESS)) {
				String step = lastResult.getName();
				int index = steps.indexOf(step);
				// If in list, will print next step, or first step if last step
				// If not in list (index -1), should still print first step
				String next_step = steps.get((index+1)%steps.size());
				stepResult.setName(next_step);
			} else {
				stepResult.setName(lastResult.getName());
			}
		}
		
		return messagingService.createStepResult(stepResult);
	}

	public MLNotification sendBellNotification(String userId, UploadSolution solution) {
		String notifMsg = "Add To Catalog Failed for solution "
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

	@Override
	public Boolean checkONAPCompatible(String solutionId, String revisionId) {
		return checkONAPCompatible(solutionId, revisionId, null, null);
	}

	// Only Python models are considered to be Compatible with ONAP
	@Override
	public Boolean checkONAPCompatible(String solutionId, String revisionId, String userId, String tracking_id) {
		log.debug(EELFLoggerDelegate.debugLogger, "checkONAPCompatible");
		
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		
		MLPStepResult stepResult = new MLPStepResult();

		if(tracking_id != null) {
			stepResult.setTrackingId(tracking_id);
			stepResult.setUserId(userId);
			stepResult.setName("CheckCompatibility");
			stepResult.setStatusCode("ST");
			stepResult.setStepCode("OB");
			stepResult.setSolutionId(solutionId);
			stepResult.setRevisionId(revisionId);
		}

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
			} catch (Exception e) {
				log.error(EELFLoggerDelegate.errorLogger, "Failed to get artifact for SolutionId={} and RevisionId ={}",
						solutionId, revisionId);
				if(tracking_id != null) {
					stepResult.setStatusCode("FA");
					stepResult.setResult("Cannot Fetch MetaData Json");
					messagingService.createStepResult(stepResult);
				}
				return isCompatible;
			}

			if(byteArrayOutputStream == null) {
				log.debug(EELFLoggerDelegate.debugLogger, "Artifact not for SolutionId={} and RevisionId ={}",
						solutionId, revisionId);
				if(tracking_id != null) {
					stepResult.setStatusCode("FA");
					stepResult.setResult("Cannot Fetch MetaData Json");
					messagingService.createStepResult(stepResult);
				}
				return isCompatible;
			}

			String metaDatajsonString = byteArrayOutputStream.toString();
			log.debug(EELFLoggerDelegate.debugLogger, "MetaData Json : " + metaDatajsonString);
			
			if(PortalUtils.isEmptyOrNullString(metaDatajsonString)) {
				if(tracking_id != null) {
					stepResult.setStatusCode("FA");
					stepResult.setResult("Cannot Fetch MetaData Json");
					messagingService.createStepResult(stepResult);
				}
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
				if(tracking_id != null) {
					stepResult.setStatusCode(STEP_SUCCESS);
					messagingService.createStepResult(stepResult);
				}
			}
		}
		
		if(!isCompatible && tracking_id != null) {
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
			if(!StringUtils.isEmpty(modName) && !("null".equalsIgnoreCase(modName))){
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
			builder.setParameter("deployment_env", "2");
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
			post.setHeader("deployment_env", "2");
		}
		
		post.setHeader("Request-ID", (String) MDC.get(ONAPLogConstants.MDCs.REQUEST_ID));
		log.info("Call on-boarding to convertSolutioToONAP wit request Id : "+ (String) MDC.get(ONAPLogConstants.MDCs.REQUEST_ID));

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
