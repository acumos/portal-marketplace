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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPAccessType;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPArtifactType;
import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPDeploymentStatus;
import org.acumos.cds.domain.MLPLoginProvider;
import org.acumos.cds.domain.MLPModelType;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPasswordChangeRequest;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.acumos.cds.domain.MLPSolutionDownload;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPSolutionValidation;
import org.acumos.cds.domain.MLPSolutionWeb;
import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.domain.MLPStepStatus;
import org.acumos.cds.domain.MLPStepType;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPThread;
import org.acumos.cds.domain.MLPToolkitType;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.domain.MLPUserLoginProvider;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.domain.MLPValidationSequence;
import org.acumos.cds.domain.MLPValidationStatus;
import org.acumos.cds.domain.MLPValidationType;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.cds.transport.SuccessTransport;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.transport.MLNotification;

public class MockCommonDataServiceRestClientImpl implements ICommonDataServiceRestClient {

	public MockCommonDataServiceRestClientImpl() {
		// TODO Auto-generated method stub
	}

	@Override
	public SuccessTransport getHealth() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SuccessTransport getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MLPAccessType> getAccessTypes() {
		MLPAccessType mlpAccessType = new MLPAccessType();
		mlpAccessType.setTypeCode("OR");
		mlpAccessType.setTypeName("Organization");

		MLPAccessType mlpAccessType1 = new MLPAccessType();
		mlpAccessType1.setTypeCode("PR");
		mlpAccessType1.setTypeName("Private");

		MLPAccessType mlpAccessType2 = new MLPAccessType();
		mlpAccessType2.setTypeCode("PB");
		mlpAccessType2.setTypeName("Public");

		List<MLPAccessType> mlpAccessTypeList = new ArrayList<MLPAccessType>();
		mlpAccessTypeList.add(mlpAccessType);
		mlpAccessTypeList.add(mlpAccessType1);
		mlpAccessTypeList.add(mlpAccessType2);

		return mlpAccessTypeList;
	}

	@Override
	public List<MLPArtifactType> getArtifactTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MLPLoginProvider> getLoginProviders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MLPModelType> getModelTypes() {

		MLPModelType mlpModelType1 = new MLPModelType();
		mlpModelType1.setTypeCode("CL");
		mlpModelType1.setTypeName("Classification");

		MLPModelType mlpModelType2 = new MLPModelType();
		mlpModelType2.setTypeCode("DT");
		mlpModelType2.setTypeName("Data Transformer");

		MLPModelType mlpModelType3 = new MLPModelType();
		mlpModelType3.setTypeCode("PR");
		mlpModelType3.setTypeName("Prediction");

		MLPModelType mlpModelType4 = new MLPModelType();
		mlpModelType4.setTypeCode("RG");
		mlpModelType4.setTypeName("Regression");

		List<MLPModelType> mlpModelTypeTest = new ArrayList<MLPModelType>();
		mlpModelTypeTest.add(mlpModelType1);
		mlpModelTypeTest.add(mlpModelType2);
		mlpModelTypeTest.add(mlpModelType3);
		mlpModelTypeTest.add(mlpModelType4);
		return mlpModelTypeTest;
	}

	@Override
	public List<MLPToolkitType> getToolkitTypes() {
		MLPToolkitType mlpToolKit = new MLPToolkitType();
		mlpToolKit.setTypeCode("CP");
		mlpToolKit.setTypeName("Composite Solution");

		MLPToolkitType mlpToolKit1 = new MLPToolkitType();
		mlpToolKit1.setTypeCode("DS");
		mlpToolKit1.setTypeName("Design Studio");

		MLPToolkitType mlpToolKit2 = new MLPToolkitType();
		mlpToolKit2.setTypeCode("H2");
		mlpToolKit2.setTypeName("H2O");

		MLPToolkitType mlpToolKit3 = new MLPToolkitType();
		mlpToolKit3.setTypeCode("RC");
		mlpToolKit3.setTypeName("RCloud");

		MLPToolkitType mlpToolKit4 = new MLPToolkitType();
		mlpToolKit4.setTypeCode("SK");
		mlpToolKit4.setTypeName("Scikit-Learn");

		MLPToolkitType mlpToolKit5 = new MLPToolkitType();
		mlpToolKit5.setTypeCode("TF");
		mlpToolKit5.setTypeName("TensorFlow");

		List<MLPToolkitType> mlpToolKitList = new ArrayList<>();
		mlpToolKitList.add(mlpToolKit);
		mlpToolKitList.add(mlpToolKit1);
		mlpToolKitList.add(mlpToolKit2);
		mlpToolKitList.add(mlpToolKit3);
		mlpToolKitList.add(mlpToolKit4);
		mlpToolKitList.add(mlpToolKit5);
		return mlpToolKitList;
	}

	@Override
	public List<MLPValidationStatus> getValidationStatuses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MLPValidationType> getValidationTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MLPDeploymentStatus> getDeploymentStatuses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSolutionCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RestPageResponse<MLPSolution> getSolutions(RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RestPageResponse<MLPSolution> findSolutionsBySearchTerm(String searchTerm, RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RestPageResponse<MLPSolution> findSolutionsByTag(String tag, RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPSolution getSolution(String solutionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPSolution createSolution(MLPSolution solution) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateSolution(MLPSolution solution) {
		// TODO Auto-generated method stub

	}

	@Override
	public void incrementSolutionViewCount(String solutionId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSolution(String solutionId) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MLPSolutionRevision> getSolutionRevisions(String solutionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MLPSolutionRevision> getSolutionRevisions(String[] solutionIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPSolutionRevision getSolutionRevision(String solutionId, String revisionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MLPSolutionRevision> getSolutionRevisionsForArtifact(String artifactId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPSolutionRevision createSolutionRevision(MLPSolutionRevision revision) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateSolutionRevision(MLPSolutionRevision revision) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSolutionRevision(String solutionId, String revisionId) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MLPArtifact> getSolutionRevisionArtifacts(String solutionId, String revisionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addSolutionRevisionArtifact(String solutionId, String revisionId, String artifactId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropSolutionRevisionArtifact(String solutionId, String revisionId, String artifactId) {
		// TODO Auto-generated method stub

	}

	@Override
	public RestPageResponse<MLPTag> getTags(RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPTag createTag(MLPTag tag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteTag(MLPTag tag) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MLPTag> getSolutionTags(String solutionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addSolutionTag(String solutionId, String tag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropSolutionTag(String solutionId, String tag) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getArtifactCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RestPageResponse<MLPArtifact> getArtifacts(RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RestPageResponse<MLPArtifact> findArtifactsBySearchTerm(String searchTerm, RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MLPArtifact> searchArtifacts(Map<String, Object> queryParameters, boolean isOr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPArtifact getArtifact(String artifactId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPArtifact createArtifact(MLPArtifact artifact) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateArtifact(MLPArtifact artifact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteArtifact(String artifactId) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getUserCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RestPageResponse<MLPUser> getUsers(RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RestPageResponse<MLPUser> findUsersBySearchTerm(String searchTerm, RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		RestPageResponse<MLPUser> response = new RestPageResponse();
		return response;
	}

	@Override
	public List<MLPUser> searchUsers(Map<String, Object> queryParameters, boolean isOr) {
		// TODO Auto-generated method stub
		List<MLPUser> mlpUserList = new ArrayList<MLPUser>();
		return mlpUserList;
	}

	@Override
	public MLPUser loginUser(String name, String pass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPUser getUser(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPUser createUser(MLPUser user) {
		
		 /** System.out.println("################In Mock Data Service####################"
		 * ); MLPUser mlpUser = new MLPUser();
		 * mlpUser.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
		 * mlpUser.setActive(true); mlpUser.setFirstName("UserFirstName");
		 * mlpUser.setLastName("UserLastName"); mlpUser.setLoginName("User1");
		 * mlpUser.setEmail("user1@emial.com"); mlpUser.setLoginHash("User1"); return
		 * mlpUser;*/
		 
		return null;
	}

	@Override
	public void updateUser(MLPUser user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteUser(String userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MLPRole> getUserRoles(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addUserRole(String userId, String roleId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateUserRoles(String userId, List<String> roleIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropUserRole(String userId, String roleId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addUsersInRole(List<String> userIds, String roleId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropUsersInRole(List<String> userIds, String roleId) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getRoleUsersCount(String roleId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MLPUserLoginProvider getUserLoginProvider(String userId, String providerCode, String providerLogin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MLPUserLoginProvider> getUserLoginProviders(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPUserLoginProvider createUserLoginProvider(MLPUserLoginProvider provider) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateUserLoginProvider(MLPUserLoginProvider provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteUserLoginProvider(MLPUserLoginProvider provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getRoleCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<MLPRole> searchRoles(Map<String, Object> queryParameters, boolean isOr) {
		List<MLPRole> mlpRoleList = new ArrayList<MLPRole>();
		return mlpRoleList;
	}

	@Override
	public RestPageResponse<MLPRole> getRoles(RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPRole getRole(String roleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPRole createRole(MLPRole role) {
		MLPRole mlpRole = new MLPRole();
		return mlpRole;
	}

	@Override
	public void updateRole(MLPRole role) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteRole(String roleId) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MLPRoleFunction> getRoleFunctions(String roleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPRoleFunction getRoleFunction(String roleId, String roleFunctionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPRoleFunction createRoleFunction(MLPRoleFunction roleFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateRoleFunction(MLPRoleFunction roleFunction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteRoleFunction(String roleId, String roleFunctionId) {
		// TODO Auto-generated method stub

	}

	@Override
	public RestPageResponse<MLPPeer> getPeers(RestPageRequest pageRequest) {
		MLPPeer mlpPeer = new MLPPeer();
		mlpPeer.setApiUrl("http://peer-api");
		mlpPeer.setContact1("Contact1");
		Date created = new Date();
		mlpPeer.setCreated(created);
		mlpPeer.setDescription("Peer description");
		mlpPeer.setName("Peer-1509357629935");
		mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
		mlpPeer.setSelf(false);
		mlpPeer.setSubjectName("peer Subject name");
		mlpPeer.setWebUrl("https://web-url");
		JsonResponse<RestPageResponse<MLPPeer>> peerRes = new JsonResponse<>();
		RestPageResponse<MLPPeer> responseBody = new RestPageResponse<>();
		peerRes.setResponseBody(responseBody);
		RestPageRequest restPageReq = new RestPageRequest();
		restPageReq.setPage(0);
		restPageReq.setSize(2);
		List<MLPPeer> peerList = new ArrayList<>();
		if (restPageReq.getPage() != null && restPageReq.getSize() != null) {
			peerList.add(mlpPeer);
		}
		return responseBody;
	}

	@Override
	public List<MLPPeer> searchPeers(Map<String, Object> queryParameters, boolean isOr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPPeer getPeer(String peerId) {
		MLPPeer mlpPeer = new MLPPeer();
		mlpPeer.setApiUrl("http://peer-api");
		mlpPeer.setContact1("Contact1");
		Date created = new Date();
		mlpPeer.setCreated(created);
		mlpPeer.setDescription("Peer description");
		mlpPeer.setName("Peer-1509357629935");
		mlpPeer.setPeerId("62e46a5a-2c26-4dee-b320-b4e48303d24d");
		mlpPeer.setSelf(false);
		mlpPeer.setSubjectName("peer Subject name");
		mlpPeer.setWebUrl("https://web-url");
		return mlpPeer;
	}

	@Override
	public MLPPeer createPeer(MLPPeer peer) {
		MLPPeer mlpPeer = new MLPPeer();
		mlpPeer.setApiUrl("http://peer-api");
		mlpPeer.setContact1("Contact1");
		Date created = new Date();
		mlpPeer.setCreated(created);
		mlpPeer.setDescription("Peer description");
		mlpPeer.setName("Peer-1509357629935");
		mlpPeer.setPeerId("c17c0562-c6df-4a0c-9702-ba8175eb23fd");
		mlpPeer.setSelf(false);
		mlpPeer.setSubjectName("peer Subject name");
		mlpPeer.setWebUrl("https://web-url");
		return mlpPeer;
	}

	@Override
	public void updatePeer(MLPPeer user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deletePeer(String peerId) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MLPPeerSubscription> getPeerSubscriptions(String peerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPPeerSubscription getPeerSubscription(Long subscriptionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPPeerSubscription createPeerSubscription(MLPPeerSubscription peerSub) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updatePeerSubscription(MLPPeerSubscription peerSub) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deletePeerSubscription(Long subscriptionId) {
		// TODO Auto-generated method stub

	}

	@Override
	public RestPageResponse<MLPSolutionDownload> getSolutionDownloads(String solutionId, RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPSolutionDownload createSolutionDownload(MLPSolutionDownload download) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteSolutionDownload(MLPSolutionDownload download) {
		// TODO Auto-generated method stub

	}

	@Override
	public RestPageResponse<MLPSolution> getFavoriteSolutions(String userId, RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPSolutionFavorite createSolutionFavorite(MLPSolutionFavorite fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteSolutionFavorite(MLPSolutionFavorite fs) {
		// TODO Auto-generated method stub

	}

	@Override
	public RestPageResponse<MLPSolutionRating> getSolutionRatings(String solutionId, RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPSolutionRating createSolutionRating(MLPSolutionRating rating) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateSolutionRating(MLPSolutionRating rating) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSolutionRating(MLPSolutionRating rating) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getNotificationCount() {
		MLNotification mlNotification = new MLNotification();
		mlNotification.setNotificationId("037ad773-3ae2-472b-89d3-9e185a2cbfc9");
		mlNotification.setCount(1);
		mlNotification.setMessage("notification");
		mlNotification.setTitle("Notification");
		mlNotification.setUrl("http://notify.com");

		long count = mlNotification.getCount();
		return count;
	}

	@Override
	public RestPageResponse<MLPNotification> getNotifications(RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPNotification createNotification(MLPNotification notification) {
		MLPNotification mlpNotification = new MLPNotification();
		Date created = new Date();
		mlpNotification.setCreated(created);
		mlpNotification.setMessage("notification created");
		Date modified = new Date();
		mlpNotification.setModified(modified);
		mlpNotification.setNotificationId("037ad773-3ae2-472b-89d3-9e185a2cbrt");
		mlpNotification.setTitle("Notification");
		mlpNotification.setUrl("http://notify.com");
		mlpNotification.setStart(created);
		Date end = new Date();
		mlpNotification.setEnd(end);
		return mlpNotification;
	}

	@Override
	public void updateNotification(MLPNotification notification) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteNotification(String notificationId) {
		// TODO Auto-generated method stub

	}

	@Override
	public RestPageResponse<MLPUserNotification> getUserNotifications(String userId, RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addUserToNotification(String notificationId, String userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropUserFromNotification(String notificationId, String userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUserViewedNotification(String notificationId, String userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public MLPSolutionWeb getSolutionWebMetadata(String solutionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MLPUser> getSolutionAccessUsers(String solutionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RestPageResponse<MLPSolution> getUserAccessSolutions(String userId, RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addSolutionUserAccess(String solutionId, String userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropSolutionUserAccess(String solutionId, String userId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePassword(MLPUser user, MLPPasswordChangeRequest changeRequest) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MLPSolutionValidation> getSolutionValidations(String solutionId, String revisionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPSolutionValidation createSolutionValidation(MLPSolutionValidation validation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateSolutionValidation(MLPSolutionValidation validation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSolutionValidation(MLPSolutionValidation validation) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MLPValidationSequence> getValidationSequences() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPValidationSequence createValidationSequence(MLPValidationSequence sequence) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteValidationSequence(MLPValidationSequence sequence) {
		// TODO Auto-generated method stub

	}

	@Override
	public RestPageResponse<MLPSolutionDeployment> getUserDeployments(String userId, RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RestPageResponse<MLPSolutionDeployment> getSolutionDeployments(String solutionId, String revisionId,
			RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RestPageResponse<MLPSolutionDeployment> getUserSolutionDeployments(String solutionId, String revisionId,
			String userId, RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPSolutionDeployment createSolutionDeployment(MLPSolutionDeployment deployment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateSolutionDeployment(MLPSolutionDeployment deployment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSolutionDeployment(MLPSolutionDeployment deployment) {
		// TODO Auto-generated method stub

	}

	@Override
	public MLPSiteConfig getSiteConfig(String configKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPSiteConfig createSiteConfig(MLPSiteConfig config) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateSiteConfig(MLPSiteConfig config) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSiteConfig(String configKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public MLPSolutionRating getSolutionRating(String solutionId, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getThreadCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RestPageResponse<MLPThread> getThreads(RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPThread getThread(String threadId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPThread createThread(MLPThread thread) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateThread(MLPThread thread) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteThread(String threadId) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getThreadCommentCount(String threadId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RestPageResponse<MLPComment> getThreadComments(String threadId, RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPComment getComment(String threadId, String commentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPComment createComment(MLPComment comment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateComment(MLPComment comment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteComment(String threadId, String commentId) {
		// TODO Auto-generated method stub

	}

	@Override
	public RestPageResponse<MLPSolution> findPortalSolutions(String[] nameKeywords, String[] descriptionKeywords,
			boolean active, String[] ownerIds, String[] accessTypeCodes, String[] modelTypeCodes,
			String[] validationStatusCodes, String[] tags, RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RestPageResponse<MLPThread> getSolutionRevisionThreads(String solutionId, String revisionId,
			RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RestPageResponse<MLPComment> getSolutionRevisionComments(String solutionId, String revisionId,
			RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MLPSolution> searchSolutions(Map<String, Object> queryParameters, boolean isOr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RestPageResponse<MLPStepResult> getStepResults(RestPageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPStepResult createStepResult(MLPStepResult stepResult) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateStepResult(MLPStepResult stepResult) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteStepResult(Long stepResultId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<MLPStepStatus> getStepStatuses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MLPStepType> getStepTypes() {
		// TODO Auto-generated method stub
		return null;
	}

}
