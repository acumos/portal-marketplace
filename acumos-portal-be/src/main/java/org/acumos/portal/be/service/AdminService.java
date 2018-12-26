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

package org.acumos.portal.be.service;

import java.util.List;
import java.util.Map;

import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerGroup;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.transport.MLPeerSolAccMap;
import org.acumos.portal.be.transport.MLRequest;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLSolutionGroup;
import org.acumos.portal.be.transport.Peer;
import org.acumos.portal.be.transport.PeerGroup;

public interface AdminService {

	RestPageResponse<MLPPeer> getAllPeers(RestPageRequest restPageReq);

    MLPPeer findPeerByApiAndWebUrl(String ApiUrl, String WebUrl); 

    MLPPeer savePeer(MLPPeer peer);

    void updatePeer(MLPPeer peer);

    void removePeer(String peerId);

    MLPPeer getPeerDetail(String peerId);

    List<MLPPeerSubscription> getPeerSubscriptions(String peerId); 

	MLPPeerSubscription getPeerSubscription(Long subId);
	
	Map<String,Integer> getPeerSubscriptionCounts(List<String> peerIds);

	MLPPeerSubscription createPeerSubscription(MLPPeerSubscription peerSub);

	void updatePeerSubscription(MLPPeerSubscription peerSub);

	void deletePeerSubscription(Long subId);
	
	MLPSiteConfig getSiteConfig(String configKey);
	
	void updateSiteConfig(MLPSiteConfig config);
	
	void deleteSiteConfig(String configKey);

	MLPSiteConfig createSiteConfig(MLPSiteConfig mlSiteConfig);

	List<MLRequest> getAllRequests(RestPageRequest pageRequest);
	
	void updateMLRequest(MLRequest mlRequest);
	
	void createSubscription(List<MLSolution> body, String peerId);
	
	MLPPeerGroup createPeerGroup(MLPPeerGroup peerGroup);
	
	void updatePeerGroup(Long groupId,PeerGroup peerGroup) throws AcumosServiceException;
	
	void deletePeerGroup(Long peerGroupId,PeerGroup peerGroup);
	
	List<Peer> getPeersInGroup(Long peerGroupId, RestPageRequestBE pageRequestBE);
	
	void addPeerToGroup(String peerId, Long peerGroupId);
	
	void dropPeerFromGroup(String peerId, Long peerGroupId);
	
	RestPageResponseBE<MLPeerSolAccMap> getPeerSolutionGroupMaps(RestPageRequestBE pageRequestBE);
	
	void mapPeerSolutionGroups(Long peerGroupId, Long solutionGroupId);
	
	void unmapPeerSolutionGroups(Long peerGroupId, Long solutionGroupId);
	
	//void mapPeerPeerGroups(Long principalGroupId, Long resourceGroupId);
	
	

	List<MLSolutionGroup> getSolutionGroupList(RestPageRequestBE restPageReqBE);

	MLSolution getSolutionGroupDetails(String solutionGroupId, RestPageRequestBE pageRequest);
	MLSolutionGroup createSolutionGroup(MLSolutionGroup solutionGroup);
	 
	void updateSolutionGroup(MLSolutionGroup body);

	void deleteSolutionGroup(Long groupId);

	PeerGroup savePeerGroup(PeerGroup body) throws Exception;

	void updatePeerSolutionGroup(String peerGroupId, String solutionGroupId);

	List<PeerGroup> getPeerGroups(RestPageRequestBE restPageReqBE);
}
