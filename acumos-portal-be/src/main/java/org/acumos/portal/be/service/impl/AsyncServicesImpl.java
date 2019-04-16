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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPTask;
import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.cds.domain.MLPUser;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.logging.ONAPLogConstants;
import org.acumos.portal.be.service.AsyncServices;
import org.acumos.portal.be.service.MessagingService;
import org.acumos.portal.be.service.NotificationService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.transport.MLStepResult;
import org.acumos.portal.be.transport.NotificationRequestObject;
import org.acumos.portal.be.transport.UploadSolution;
import org.acumos.portal.be.util.DockerUploadResult;
import org.acumos.portal.be.util.JsonUtils;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AsyncServicesImpl extends AbstractServiceImpl implements AsyncServices {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private Environment env;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private FileSystemStorageService fileSystemStorageService;

	@Autowired
	private UserService userService;

	@Autowired
	private MessagingService messagingService;

	private static final String NOTIFICATION_TITLE = "Web Based Onboarding";
	private static final String STEP_SUCCESS = "SU";
	private static final String MSG_SEVERITY_ME = "ME";

	private static final String ENV_MODELSTORAGE = "model.storage.folder.name";
	private static final String ENV_MODELURL = "onboarding.push.model.url";
	private static final String ENV_TOKENMODE = "onboarding.tokenmode";
	private static final String ENV_BLACKLIST = "onboarding.directory.blacklist";
	private static final String ENV_ADVANCED_MODELURL = "onboarding.push.advancedmodel.url";

	@Async
	public Future<String> initiateAsyncProcess() throws InterruptedException {
		log.info("###Start Processing with Thread id: " + Thread.currentThread().getId());
		log.debug("process");

		String processInfo = String.format("Processing is Done with Thread id= %d", Thread.currentThread().getId());
		return new AsyncResult<>(processInfo);
	}

	@Override
	public HttpResponse callOnboarding(String uuid, MLPUser user, UploadSolution solution, String provider,
			String access_token, String modelName, String dockerfileURI,
			String deploymentEnv) throws InterruptedException, ClientProtocolException, IOException {

		log.info("CallOnboarding service Async");
		HttpClientBuilder hcbuilder = HttpClientBuilder.create();
		CloseableHttpClient httpclient = hcbuilder.build();
		HttpResponse response = null;

		HttpResponse retObject = processService(uuid, user, solution, provider, access_token, modelName, dockerfileURI,
				deploymentEnv, httpclient, response,null);

		return retObject;
	}
	
	@Override
	public HttpResponse callOnboarding(String uuid, MLPUser user, UploadSolution solution, String provider,
			String access_token, String modelName,  String dockerfileURI,
			String deploymentEnv, DockerUploadResult dockerUploadResult)
			throws InterruptedException, FileNotFoundException, ClientProtocolException, IOException {
		
		log.info("CallOnboarding service Async");
		HttpClientBuilder hcbuilder = HttpClientBuilder.create();
		CloseableHttpClient httpclient = hcbuilder.build();
		HttpResponse response = null;

		HttpResponse retObject = processService(uuid, user, solution, provider, access_token, modelName, dockerfileURI,
				deploymentEnv, httpclient, response,dockerUploadResult);

		return retObject;
	}	

	private HttpResponse processService(String uuid, MLPUser user, UploadSolution solution, String provider,
			String access_token, String modelName, String dockerfileURI, String deploymentEnv,
			CloseableHttpClient httpclient, HttpResponse response,DockerUploadResult dockerUploadResult) throws IOException {
		
		try {
			String directory = PortalUtils.getEnvProperty(env, ENV_MODELSTORAGE) + File.separator + user.getUserId();
			List<File> fileList = new ArrayList<>();
			File modelFile = null;
			File schemaFile = null;
			File metadataFile = null;
			File onnxFile = null;
			File pfaFile = null;
			File licenseFile = null;
			MLPNotification notification = new MLPNotification();
			if (StringUtils.isEmpty(dockerfileURI) || ("null".equalsIgnoreCase(dockerfileURI))) {
				dockerfileURI = null;
			}
			fileList = getListOfFiles(directory, fileList);

			if (fileList != null) {
				for (File file : fileList) {
					if (file.isFile() && file.getName().contains(".zip") || file.getName().contains(".jar")
							|| file.getName().contains(".bin") || file.getName().contains(".tar")
							|| file.getName().toUpperCase().contains(".R")) {
						modelFile = new File(file.getAbsolutePath());
					}
					if (file.isFile() && file.getName().contains(".proto")) {
						schemaFile = new File(file.getAbsolutePath());
					}
					if (file.isFile() && file.getName().contains(".json")) {
						metadataFile = new File(file.getAbsolutePath());
					}
					if (file.isFile() && file.getName().contains(".onnx")) {
						onnxFile = new File(file.getAbsolutePath());
					}
					if (file.isFile() && file.getName().contains(".pfa")) {
						pfaFile = new File(file.getAbsolutePath());
					}
					if (file.isFile() && file.getName().contains("license.json")) {
						licenseFile = new File(file.getAbsolutePath());
					}

				}
			}

			if ((modelFile != null && schemaFile != null && metadataFile != null)
					|| (onnxFile != null || pfaFile != null) || dockerfileURI != null) {
				HttpPost post = null;

				if ((onnxFile != null || pfaFile != null || dockerfileURI != null)) {
					post = new HttpPost(PortalUtils.getEnvProperty(env, ENV_ADVANCED_MODELURL));
				} else {
					post = new HttpPost(PortalUtils.getEnvProperty(env, ENV_MODELURL));
					post.setHeader("isCreateMicroservice", "true");
					post.setHeader("deployment_env", deploymentEnv);
				}

				String tokenMode = PortalUtils.getEnvProperty(env, ENV_TOKENMODE);
				if (tokenMode.equals("jwtToken")) {
					if (StringUtils.isEmpty(provider)) {
						post.setHeader("Authorization", user.getAuthToken());
					} else {
						post.setHeader("Authorization", access_token);
					}
				} else if (tokenMode.equals("apiToken")) {
					if (user.getApiToken() == null || user.getApiToken().isEmpty()) {
						throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_TOKEN,
								"API token invalid. Please refresh your API token in Account Settings.");
					} else {
						post.setHeader("Authorization", user.getLoginName() + ":" + user.getApiToken());
					}
				}

				if (StringUtils.isNotEmpty(provider)) {
					post.setHeader("provider", provider);
				}
				if (!StringUtils.isEmpty(uuid)) {
					post.addHeader("tracking_id", uuid);
				}

				if (StringUtils.isEmpty(deploymentEnv) || deploymentEnv == null) {
					post.setHeader("isCreateMicroservice", "false");					
				}

				post.setHeader("X-ACUMOS-Request-Id", (String) MDC.get(ONAPLogConstants.MDCs.REQUEST_ID));
				log.info("CallOnboarding wit request Id : " + (String) MDC.get(ONAPLogConstants.MDCs.REQUEST_ID));

				MultipartEntityBuilder builder = MultipartEntityBuilder.create();

				builder.setBoundary(UUID.randomUUID().toString());
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				if (dockerfileURI != null) {
					post.setHeader("modelname", modelName);
					if (!dockerfileURI.equals("DockerModel")) {
						post.setHeader("dockerfileURL", dockerfileURI);
						post.setHeader("isCreateMicroservice", "true");
					}

				} else if (onnxFile != null) {
					builder.addBinaryBody("model", new FileInputStream(onnxFile), ContentType.MULTIPART_FORM_DATA,
							onnxFile.getName());
					post.setHeader("modelname", modelName);

				} else if (pfaFile != null) {
					builder.addBinaryBody("model", new FileInputStream(pfaFile), ContentType.MULTIPART_FORM_DATA,
							pfaFile.getName());
					post.setHeader("modelname", modelName);
				} else {
					builder.addBinaryBody("model", new FileInputStream(modelFile), ContentType.MULTIPART_FORM_DATA,
							modelFile.getName());
					builder.addBinaryBody("metadata", new FileInputStream(metadataFile),
							ContentType.MULTIPART_FORM_DATA, metadataFile.getName());
					builder.addBinaryBody("schema", new FileInputStream(schemaFile), ContentType.MULTIPART_FORM_DATA,
							schemaFile.getName());

				}
				if (licenseFile != null) {
					builder.addBinaryBody("license", new FileInputStream(licenseFile), ContentType.MULTIPART_FORM_DATA,
							licenseFile.getName());
				}
				HttpEntity entity = builder.build();
				post.setEntity(entity);

				response = httpclient.execute(post);
				log.info("inside callOnboarding response.getStatusLine().getStatusCode() ---->>>"
						+ response.getStatusLine().getStatusCode());
				notification.setMsgSeverityCode(MSG_SEVERITY_ME);
				if (response.getStatusLine().getStatusCode() == 200
						|| response.getStatusLine().getStatusCode() == 201) {
					InputStream instream = response.getEntity().getContent();
					String result = convertStreamToString(instream);

					ObjectMapper mapper = new ObjectMapper();
					Map<String, Object> resp = mapper.readValue(result, Map.class);
					log.info("inside callOnboarding if after resp.toString() ---->>>" + resp.toString());
					Map<String, Object> solutionStr = (Map<String, Object>) resp.get("result");
					
					if(dockerUploadResult != null) {

					   String dockerfileURIStr = resp.entrySet().stream().filter(x -> "dockerImageUri".equals(x.getKey()))
							.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).get("dockerImageUri")
							.toString();
					   dockerUploadResult.setDockerArtifcatUrl( dockerfileURIStr);
					}

					log.info("Response From Onboarding : {}", resp.toString());

					String notifMsg = "Solution " + solutionStr.get("name") + " On-boarded Successfully";
					notification.setMessage(notifMsg);
					notification.setTitle(NOTIFICATION_TITLE);
					notificationService.generateNotification(notification, user.getUserId());

					// Send notification to user according to preference
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
					log.info("inside callOnboarding else after resp.toString() ---->>>" + resp.toString());
					log.info(resp.toString());
					log.info((String) resp.get("errorMessage"));

					String errorLog = getErrorLogArtiffact(uuid, user.getUserId());
					String notifMsg = "On-boarding Failed"
							+ ". Please restart the process again to upload the solution. " + errorLog;

					notification.setMessage(notifMsg);
					notification.setTitle(NOTIFICATION_TITLE);
					log.info("inside callOnboarding else before generateNotification ---->>>");
					notificationService.generateNotification(notification, user.getUserId());

					sendTrackerErrorNotification(uuid, user.getUserId(), (String) resp.get("errorMessage"));

					// Send notification to user according to preference
					Map<String, String> notifyBody = new HashMap<String, String>();
					notifyBody.put("errorMessage", (String) resp.get("errorMessage"));
					notifyOnboardingStatus(user.getUserId(), "HI", "On-boarding Failed for solution ", notifyBody,
							"ONBD_FAIL");
				}
			} else { // Invalid model bundle, does not contain all three parts
				List<String> files = new ArrayList<String>();
				if (modelFile == null) {
					files.add("model zip");
				}
				if (schemaFile == null) {
					files.add("schema proto");
				}
				if (metadataFile == null) {
					files.add("metadata json");
				}
				if (onnxFile == null) {
					files.add("onnx file");
				}
				if (pfaFile == null) {
					files.add("pfa file");
				}
				if (dockerfileURI == null) {
					files.add("docker file URI");
				}
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION,
						"Malformed bundle, missing required files: " + String.join(", ", files)
								+ ". Check your model and try again.");
			}
			// If disconnected from onboarding service, catch related exceptions
			// here
		} catch (ConnectException | NoHttpResponseException e) {
			log.error("Exception Occurred Onboarding the solution - No response ", e);

			String reason = "Failed to connect to onboarding";

			// Send a bell notification to the user to alert of failure
			sendBellNotification(user.getUserId(), reason);

			// Update the on-screen progress tracker status to alert of failure
			sendTrackerErrorNotification(uuid, user.getUserId(), reason);

			// Email) a notification to the user based on notification
			// preference
			sendEmailNotification(user.getUserId(), solution, e.getMessage());
		} catch (Exception e) {
			log.error("Exception Occurred Onboarding the solution ", e);

			// Send a bell notification to the user to alert of failure
			sendBellNotification(user.getUserId(), e.getMessage());

			// Update the on-screen progress tracker status to alert of failure
			sendTrackerErrorNotification(uuid, user.getUserId(), e.getMessage());

			// Email) a notification to the user based on notification
			// preference
			sendEmailNotification(user.getUserId(), solution, e.getMessage());
		} finally {
			httpclient.close();
			// Remove all files once the process is completed
			log.info("inside finallly callOnboarding ---->>>");

			fileSystemStorageService.deleteAll(user.getUserId());
		}
		return response;
	}
	
	private String getErrorLogArtiffact(String trackingId, String userId) {

		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		String erlog = "";
		// Get All the status for the tracking Id
		List<MLStepResult> status = messagingService.callOnBoardingStatusList(userId, trackingId);

		if (status != null && !status.isEmpty()) {
			MLStepResult resultStatus = status.stream()
					.filter(stepResult -> stepResult.getRevisionId() != null && stepResult.getSolutionId() != null)
					.findFirst().get();
			if (resultStatus != null) {
				List<MLPArtifact> artifactList = dataServiceRestClient
						.getSolutionRevisionArtifacts(resultStatus.getSolutionId(), resultStatus.getRevisionId());

				if (artifactList != null && !PortalUtils.isEmptyList(artifactList)) {
					MLPArtifact logArtifact = artifactList.stream()
							.filter(artifact -> (artifact.getDescription())
									.contains(env.getProperty("onboarding.errorlog.filename")))
							.findFirst().orElse(null);

					MLPArtifact microserviceLog = artifactList.stream()
							.filter(artifact -> (artifact.getDescription())
									.contains(env.getProperty("microservice.errorlog.filename")))
							.findFirst().orElse(null);

					if (logArtifact != null) {
						// generate the download log href as String
						erlog = "Click " + "<a href=\"/api/downloads/" + resultStatus.getSolutionId() + "?artifactId="
								+ logArtifact.getArtifactId() + "&revisionId=" + resultStatus.getRevisionId()
								+ "&userId=" + userId + "&jwtToken={{auth}}\" >here</a> to download Onboarding logs.";
					}

					if (microserviceLog != null) {
						// generate the download log href as String
						erlog = erlog + " Click " + "<a href=\"/api/downloads/" + resultStatus.getSolutionId()
								+ "?artifactId=" + microserviceLog.getArtifactId() + "&revisionId="
								+ resultStatus.getRevisionId() + "&userId=" + userId
								+ "&jwtToken={{auth}}\" >here</a> to download Microservice logs.";
					}
				}

			}
		}
		return erlog;
	}

	public void sendEmailNotification(String userId, UploadSolution solution, String errorMessage) {
		try {
			// Send notification to user according to preference
			Map<String, String> notifyBody = new HashMap<String, String>();
			notifyBody.put("errorMessage", errorMessage);
			notifyOnboardingStatus(userId, "HI", "Add To Catalog Failed for solution ", notifyBody, "ONBD_FAIL");
		} catch (Exception e) {
			log.error("Exception Occurred sending notification email ", e);
		}
	}

	private List<File> getListOfFiles(String directoryName, List<File> files)
			throws AcumosServiceException, IOException {
		File directory = new File(directoryName);
		String blacklist = PortalUtils.getEnvProperty(env, ENV_BLACKLIST);
		// get all the files from a directory
		File[] fList = directory.listFiles();
		if (fList != null) {
			for (File file : fList) {
				if (file.isFile()) {
					files.add(file);
				} else if (file.isDirectory() && file.getName().matches("(?!^(" + blacklist + ")$)^.*$")) {
					getListOfFiles(file.getAbsolutePath(), files);
				}
			}
		}
		return files;
	}

	public MLPTaskStepResult sendTrackerErrorNotification(String uuid, String userId, String message) {
		log.debug("inside sendTrackerErrorNotification");
		MLPTaskStepResult res = null;
		MLPTask task = null;
		List<MLPTask> tasks = messagingService.findTasksByTrackingId(uuid);
		if (PortalUtils.isEmptyList(tasks)) {
			task = new MLPTask();
			task.setUserId(userId);
			task.setTrackingId(uuid);
			task.setName("CreateSolution");
			task.setStatusCode("FA");
			task.setTaskCode("OB");
			task = messagingService.createTask(task);
		} else {
			task = tasks.get(0);
		}

		List<MLStepResult> status = messagingService.callOnBoardingStatusList(userId, uuid);
		if (status == null || status.isEmpty()) {
			MLPTaskStepResult stepResult = new MLPTaskStepResult();
			stepResult.setTaskId(task.getTaskId());
			stepResult.setName("CreateSolution");
			stepResult.setStatusCode("FA");
			stepResult.setResult(message);
			res = messagingService.createStepResult(stepResult);
		}
		return res;
	}

	public MLNotification sendBellNotification(String userId, String reason) {
		String notifMsg = "On-boarding failed: " + reason + " Please restart the process again to upload the solution.";
		MLPNotification notification = new MLPNotification();
		notification.setMessage(notifMsg);
		notification.setTitle(NOTIFICATION_TITLE);
		notification.setMsgSeverityCode(MSG_SEVERITY_ME);
		notificationService.generateNotification(notification, userId);

		List<MLNotification> list = notificationService.getNotifications();
		return list.get(list.size() - 1);
	}

	private void notifyOnboardingStatus(String userId, String severity, String notifySubject,
			Map<String, String> notifyBody, String messageStatusType) throws AcumosServiceException {
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
		log.debug("checkONAPCompatible");

		ICommonDataServiceRestClient dataServiceRestClient = getClient();

		MLPTaskStepResult stepResult = new MLPTaskStepResult();
		MLPTask task = null;

		if (tracking_id != null) {
			List<MLPTask> tasks = messagingService.findTasksByTrackingId(tracking_id);
			if (PortalUtils.isEmptyList(tasks)) {
				task = new MLPTask();
				task.setTrackingId(tracking_id);
				task.setUserId(userId);
				if (!PortalUtils.isEmptyOrNullString(solutionId)) {
					task.setSolutionId(solutionId);
				}
				if (!PortalUtils.isEmptyOrNullString(revisionId)) {
					task.setRevisionId(revisionId);
				}
				task.setName("CheckCompatibility");
				task.setStatusCode("ST");
				task.setTaskCode("SV");
				task = messagingService.createTask(task);
			} else {
				task = tasks.get(0);
			}
			stepResult.setTaskId(task.getTaskId());
			stepResult.setName("CheckCompatibility");
			stepResult.setStatusCode("ST");
		}

		List<MLPArtifact> revisionArtifacts = dataServiceRestClient.getSolutionRevisionArtifacts(solutionId,
				revisionId);
		Boolean isCompatible = false;
		String metaDataUrl = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		String name = null;

		for (MLPArtifact artifact : revisionArtifacts) {
			if (artifact.getArtifactTypeCode().equalsIgnoreCase("MD")) {
				metaDataUrl = artifact.getUri();
				// stepResult.setArtifactId(artifact.getArtifactId());
			}
		}

		// messagingService.createStepResult(stepResult);

		if (metaDataUrl != null && !PortalUtils.isEmptyOrNullString(metaDataUrl)) {
			NexusArtifactClient artifactClient = getNexusClient();

			try {
				byteArrayOutputStream = artifactClient.getArtifact(metaDataUrl);
			} catch (Exception e) {
				log.error("Failed to get artifact for SolutionId={} and RevisionId ={}", solutionId, revisionId);
				if (tracking_id != null) {
					stepResult.setStatusCode("FA");
					stepResult.setResult("Cannot Fetch MetaData Json");
					messagingService.createStepResult(stepResult);
				}
				return isCompatible;
			}

			if (byteArrayOutputStream == null) {
				log.debug("Artifact not for SolutionId={} and RevisionId ={}", solutionId, revisionId);
				if (tracking_id != null) {
					stepResult.setStatusCode("FA");
					stepResult.setResult("Cannot Fetch MetaData Json");
					messagingService.createStepResult(stepResult);
				}
				return isCompatible;
			}

			String metaDatajsonString = byteArrayOutputStream.toString();
			log.debug("MetaData Json : " + metaDatajsonString);

			if (PortalUtils.isEmptyOrNullString(metaDatajsonString)) {
				if (tracking_id != null) {
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
				if (tracking_id != null) {
					stepResult.setStatusCode(STEP_SUCCESS);
					messagingService.createStepResult(stepResult);
				}
			}
		}

		if (!isCompatible && tracking_id != null) {
			stepResult.setStatusCode("FA");
			stepResult.setResult("Solution not a Python Model");
			messagingService.createStepResult(stepResult);
		}

		return isCompatible;
	}

	public HttpResponse convertSolutioToONAP(String solutionId, String revisionId, String userId, String tracking_id,
			String modName) {
		HttpResponse response = null;
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			HttpClientBuilder hcbuilder = HttpClientBuilder.create();
			CloseableHttpClient httpclient = hcbuilder.build();

			URIBuilder builder = null;
			try {
				builder = new URIBuilder(env.getProperty("onboarding.push.model.dcae_url"));
			} catch (URISyntaxException e1) {
				log.error("Exception Occurred while calling onboarding convertSolutioToONAP ", e1);
			}

			if (!StringUtils.isEmpty(solutionId)) {
				builder.setParameter("solutioId", solutionId);
				if (!StringUtils.isEmpty(modName) && !("null".equalsIgnoreCase(modName))) {
					builder.setParameter("modName", modName);
					log.debug("ONAP model name from user : " + modName);
				} else {
					MLPSolution solution = dataServiceRestClient.getSolution(solutionId);
					if (solution != null) {
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
				log.error("Exception Occurred while calling onboarding convertSolutioToONAP ", e1);
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

			post.setHeader("X-ACUMOS-Request-Id", (String) MDC.get(ONAPLogConstants.MDCs.REQUEST_ID));
			log.info("Call on-boarding to convertSolutioToONAP wit request Id : "
					+ (String) MDC.get(ONAPLogConstants.MDCs.REQUEST_ID));

			try {
				log.debug("Call Onboarding URI : " + post.getURI());
				response = httpclient.execute(post);
			} catch (UnsupportedEncodingException e) {
				log.error("Exception Occurred while convertSolutioToONAP ", e);
			} catch (ClientProtocolException e) {
				log.error("Exception Occurred while convertSolutioToONAP ", e);
			} catch (IOException e) {
				log.error("Exception Occurred while convertSolutioToONAP ", e);
			} finally {
				httpclient.close();
			}
		} catch (IOException e) {
			log.error("Exception Occurred while convertSolutioToONAP ", e);
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

	public void setEnvironment(Environment environment) {
		env = environment;
	}

	
}