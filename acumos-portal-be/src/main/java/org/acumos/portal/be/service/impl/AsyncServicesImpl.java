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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.controller.UserServiceController;
import org.acumos.portal.be.service.AsyncServices;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.NotificationService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.UploadSolution;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPUser;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AsyncServicesImpl implements AsyncServices {

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

	@Async
	public Future<String> initiateAsyncProcess() throws InterruptedException {
		log.info("###Start Processing with Thread id: " + Thread.currentThread().getId());
		log.debug(EELFLoggerDelegate.debugLogger, "process");
		// Sleep 3s for simulating the processing
		Thread.sleep(3000);

		String processInfo = String.format("Processing is Done with Thread id= %d", Thread.currentThread().getId());
		return new AsyncResult<>(processInfo);
	}

	@Override
	public Future<HttpResponse> callOnboarding(String uuid, String userId, UploadSolution solution, String provider, String access_token)
			throws InterruptedException, ClientProtocolException, IOException {

		File directory = new File(env.getProperty("model.storage.folder.name") + File.separator + userId);
		File modelFile = null;
		File schemaFile = null;
		File metadataFile = null;
		File[] fList = directory.listFiles();
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = null;

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
				

				if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {
					InputStream instream = response.getEntity().getContent();
					String result = convertStreamToString(instream);

					ObjectMapper mapper = new ObjectMapper();
					Map<String, Object> resp = mapper.readValue(result, Map.class);
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
						catalogService.updateSolution(solutionDetail, newSolutionId);
					}
					// Temporary solution to notify user, as we dont have intermediate results from
					// onbaording
					generateNotification("Solution " + solution.getName() + " Added to Catalog Successfully", userId);
				} else {
					InputStream instream = response.getEntity().getContent();
					String result = convertStreamToString(instream);

					ObjectMapper mapper = new ObjectMapper();
					Map<String, Object> resp = mapper.readValue(result, Map.class);
					log.info(resp.toString());
					log.info((String) resp.get("errorMessage"));
					// Temporary solution to notify user, as we dont have intermediate results from
					// onbaording
					generateNotification("Add To Catalog Failed for solution " + solution.getName()
							+ ". Please restart the process again to upload the solution", userId);
					// throw new RuntimeException("Failed : HTTP error code : " +
					// response.getStatusLine().getStatusCode());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Onboarding the solution ", e);

		} finally {
			httpclient.getConnectionManager().shutdown();
			// Remove all files once the process is completed
			fileSystemStorageService.deleteAll(userId);
		}

		return new AsyncResult<HttpResponse>(response);
	}

	void generateNotification(String msg, String userId) {
		MLPNotification notification = new MLPNotification();
		try {
			if (msg != null) {
				notification.setTitle("Web Based Onboarding");
				notification.setMessage(msg);
				Date startDate = new Date();
				Date endDate = new Date(startDate.getTime() + (1000 * 60 * 60 * 24));
				notification.setStart(startDate);
				notification.setEnd(endDate);
				MLNotification mlNotification = notificationService.createNotification(notification);
				notificationService.addNotificationUser(mlNotification.getNotificationId(), userId);
			}
		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getNotifications", e);
		}
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
