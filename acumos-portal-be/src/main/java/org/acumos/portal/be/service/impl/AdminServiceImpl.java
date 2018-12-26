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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerGroup;
import org.acumos.cds.domain.MLPPeerGrpMemMap;
import org.acumos.cds.domain.MLPPeerSolAccMap;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionGroup;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.AdminService;
import org.acumos.portal.be.transport.Peer;
import org.acumos.portal.be.transport.PeerGroup;
import org.acumos.portal.be.transport.MLPeerSolAccMap;
import org.acumos.portal.be.transport.MLRequest;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLSolutionGroup;
import org.acumos.portal.be.transport.PeerGroup;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.springframework.stereotype.Service;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;

@Service
public class AdminServiceImpl extends AbstractServiceImpl implements AdminService {


    private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(AdminServiceImpl.class);

    private static List<MLRequest> mlRequestList = requestList();
    
    @Override
    public RestPageResponse<MLPPeer> getAllPeers(RestPageRequest restPageReq) {
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        RestPageResponse<MLPPeer> mlPPeers = dataServiceRestClient.getPeers(restPageReq);
        return mlPPeers;
    }

    @Override
    public MLPPeer getPeerDetail(String peerId) {
        log.debug(EELFLoggerDelegate.debugLogger, "savePeer ={}", peerId);
        MLPPeer mlpPeer = null;
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        if(peerId != null) {
            mlpPeer = dataServiceRestClient.getPeer(peerId);
        }
        return mlpPeer;
    }

    @Override
    public MLPPeer findPeerByApiAndWebUrl(String apiUrl, String webUrl) {
        log.debug(EELFLoggerDelegate.debugLogger, "findPeerByApiAndWebUrl ={}", apiUrl + " and " + webUrl);
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        MLPPeer mlpPeer = null;
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("apiUrl", apiUrl);
        queryParams.put("webUrl", webUrl);
        RestPageRequest pageRequest = new RestPageRequest();
        pageRequest.setPage(0);
        pageRequest.setSize(100);
        RestPageResponse<MLPPeer> peerList = dataServiceRestClient.searchPeers(queryParams, false, pageRequest);
        List<MLPPeer> mlpPeers = peerList.getContent();
        for(MLPPeer peer : mlpPeers) {
            if(peer != null) {
            	if(!"RN".equalsIgnoreCase(peer.getStatusCode()) && !PortalUtils.isEmptyOrNullString(peer.getApiUrl()) && peer.getApiUrl().equalsIgnoreCase(apiUrl)
                        && !PortalUtils.isEmptyOrNullString(peer.getWebUrl()) && peer.getWebUrl().equalsIgnoreCase(webUrl)) {
                    mlpPeer = peer;
                    break;
                }
            }
        }
        
        return mlpPeer;
    }

    @Override
    public MLPPeer savePeer(MLPPeer peer) {
        log.debug(EELFLoggerDelegate.debugLogger, "savePeer ={}", peer);
        MLPPeer mlpPeer = null;
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        if(peer != null) {
            mlpPeer = dataServiceRestClient.createPeer(peer);
        }
        return mlpPeer;
    }

    @Override
    public void updatePeer(MLPPeer peer) {
        log.debug(EELFLoggerDelegate.debugLogger, "updatePeer ={}", peer);
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        if(peer != null) {
            dataServiceRestClient.updatePeer(peer);
        }
    }

    @Override
    public void removePeer(String peerId) {
        log.debug(EELFLoggerDelegate.debugLogger, "removePeer ={}", peerId);
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        if(peerId != null) {
            dataServiceRestClient.deletePeer(peerId);
        }
        
    }

	@Override

	public List<MLPPeerSubscription> getPeerSubscriptions(String peerId) {
		log.debug(EELFLoggerDelegate.debugLogger, "getPeerSubscriptions ={}", peerId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPPeerSubscription> PeerSubscriptionList = dataServiceRestClient.getPeerSubscriptions(peerId); 
		return PeerSubscriptionList;
	}

	@Override
	public MLPPeerSubscription getPeerSubscription(Long subId) {
		log.debug(EELFLoggerDelegate.debugLogger, "getPeerSubscription ={}", subId);
		MLPPeerSubscription peerSubscription = null;
		ICommonDataServiceRestClient dataServiceRestClient = getClient(); 
		if (subId != null) {
			peerSubscription = dataServiceRestClient.getPeerSubscription(subId);
		}
		return peerSubscription;
	}
	
	@Override
	public Map<String,Integer> getPeerSubscriptionCounts(List<String> peerIds) {
		log.debug(EELFLoggerDelegate.debugLogger, "getPeerSubscriptionCounts ={}", peerIds);
		HashMap<String,Integer> counts = new HashMap<>();
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		if (peerIds != null) {
			for (String peerId : peerIds) {
				if (peerId != null) {
					Integer count = dataServiceRestClient.getPeerSubscriptions(peerId).size();
					counts.put(peerId, count);
				}
			}
		}
		return counts;
	}

	@Override
	public MLPPeerSubscription createPeerSubscription(MLPPeerSubscription peerSub) {
		log.debug(EELFLoggerDelegate.debugLogger, "createPeerSubscription ={}", peerSub);
		MLPPeerSubscription peerSubscription = null;
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		if (peerSub != null) {
			peerSubscription = dataServiceRestClient.createPeerSubscription(peerSub);
		}
		return peerSubscription;
	}

	@Override
	public void updatePeerSubscription(MLPPeerSubscription peerSub) {
		log.debug(EELFLoggerDelegate.debugLogger, "updatePeerSubscription ={}", peerSub);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		if (peerSub != null) {
			dataServiceRestClient.updatePeerSubscription(peerSub);
		}
	}

	@Override
	public void deletePeerSubscription(Long subId) {
		log.debug(EELFLoggerDelegate.debugLogger, "deletePeerSubscription ={}", subId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		if (subId != null) {
			dataServiceRestClient.deletePeerSubscription(subId);
		}
	}

	@Override
	public MLPSiteConfig getSiteConfig(String configKey) {
		log.debug(EELFLoggerDelegate.debugLogger, "getSiteConfig ={}", configKey);
		MLPSiteConfig siteConfig = null;
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		if (configKey != null) {
			siteConfig = dataServiceRestClient.getSiteConfig(configKey);
		}
		return siteConfig;
		
	}

	 @Override
	    public MLPSiteConfig createSiteConfig(MLPSiteConfig mlpSiteConfig){
	    	log.debug(EELFLoggerDelegate.debugLogger, "createSiteConfig");
	    	ICommonDataServiceRestClient dataServiceRestClient = getClient();
	    	MLPSiteConfig mlSiteConfig = PortalUtils.convertMLSiteConfigToMLPSiteConfig(dataServiceRestClient.createSiteConfig(mlpSiteConfig));
	      return mlSiteConfig;
	    	
	    }
	
	@Override
	public void updateSiteConfig(MLPSiteConfig config) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateSiteConfig ={}", config);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		if (config != null) {
			//MLPSiteConfig mlpSiteConfig = PortalUtils.convertMLSiteConfigToMLPSiteConfig(config);
			dataServiceRestClient.updateSiteConfig(config);
		}
		
	}

	@Override
	public void deleteSiteConfig(String configKey) {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteSiteConfig ={}", configKey);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		if (configKey != null) {
			dataServiceRestClient.deleteSiteConfig(configKey);
		}
	}
	
	@Override
	public List<MLRequest> getAllRequests(RestPageRequest restPageReq){
		log.debug(EELFLoggerDelegate.debugLogger, "getRequests ={}");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return getMlRequestList();
				
	}

	@Override
	public void updateMLRequest(MLRequest mlRequest) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateMLRequest ={}", mlRequest);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		if (mlRequest != null) {
			// MLPSiteConfig mlpSiteConfig =
			// PortalUtils.convertMLSiteConfigToMLPSiteConfig(config);
			// dataServiceRestClient.updateMLRequest(mlRequest);
			for (MLRequest mlRequest2 : mlRequestList) {
				if (mlRequest2.getRequestId().equals(mlRequest.getRequestId())) {
					if (JSONTags.REQUEST_DENIED.equals(mlRequest.getAction())) {
						mlRequest2.setStatus(JSONTags.REQUEST_DENIED);
					} else {
						mlRequest2.setStatus(JSONTags.REQUEST_APPROVED);
					}
				}
			}
			System.out.println("Testing");
		}
	}
	
	private static List<MLRequest> requestList(){
		
		List<MLRequest> mlRequestList = new ArrayList<MLRequest>();
		for (int i = 1; i <= 10; i++) {
			MLRequest mlrequest = new MLRequest();
			Date date = new Date();
			mlrequest.setDate(date);
			mlrequest.setRequestedDetails("Requested Details" + i);
			mlrequest.setRequestId("REQID " +i);
			if(i%2 == 0){
				mlrequest.setRequestType("Federation");
				mlrequest.setSender("Acumous " +i);
			}else{
				mlrequest.setRequestType("Model Download");
				mlrequest.setSender("TechM " +i);
			}
			mlrequest.setStatus("Pending");
			mlRequestList.add(mlrequest);
		}
		return mlRequestList;
		
	}

	public List<MLRequest> getMlRequestList() {
		return mlRequestList;
	}
	
	@Override
    public void createSubscription(List<MLSolution> solList, String peerId) {
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        
        for (MLSolution sol : solList) {
            MLPPeerSubscription sub = new MLPPeerSubscription();
            sub.setSelector(sol.getSelector());
            sub.setAccessType(sol.getAccessType());
            sub.setPeerId(peerId);
            sub.setScopeType("FL");
            sub.setUserId(sol.getOwnerId());
            if (sol.getRefreshInterval() != null){
                sub.setRefreshInterval(sol.getRefreshInterval());
            }
            dataServiceRestClient.createPeerSubscription(sub);
        }
    }
	
	@Override
	public List<PeerGroup> getPeerGroups(RestPageRequestBE restPageReqBE) {
		log.debug(EELFLoggerDelegate.debugLogger, "getPeerGroups ={}");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		RestPageRequest restPageReq = PortalUtils.convertFromRestPageRequestBEToRestPageRequest(restPageReqBE);
		RestPageResponse<MLPPeerGroup> mlpPeerGroups = dataServiceRestClient.getPeerGroups(restPageReq);

		List<PeerGroup> mLPeerGroupList = new ArrayList<>();

		for (MLPPeerGroup mlpPeerGrp : mlpPeerGroups.getContent()) {
			mLPeerGroupList.add(PortalUtils.convertFromMLPPeerGroupToMLPeerGroup(mlpPeerGrp));
		}
		List<PeerGroup> mLPeerGroupListwithPeers = new ArrayList<>();
		for(PeerGroup pgroup : mLPeerGroupList){
			List<Peer> peerlistPerGroup=getPeersInGroup(pgroup.getGroupId(),restPageReqBE);
			pgroup.setPeers(peerlistPerGroup);
			mLPeerGroupListwithPeers.add(pgroup);
		}

		return mLPeerGroupListwithPeers;
	}

	@Override
	public MLPPeerGroup createPeerGroup(MLPPeerGroup peerGroup) {
		log.debug(EELFLoggerDelegate.debugLogger, "createPeerGroup ={}", peerGroup);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPPeerGroup mLPPeerGroup = dataServiceRestClient.createPeerGroup(peerGroup);
		return mLPPeerGroup;

	}


	@Override
	public void deletePeerGroup(Long peerGroupId,PeerGroup peerGroup) {
		log.debug(EELFLoggerDelegate.debugLogger, "deletePeerGroup ={}", peerGroupId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		if(peerGroup.getPeers()!=null && peerGroup.getPeers().size()>0){
			for(Peer peer : peerGroup.getPeers()){
				dataServiceRestClient.dropPeerFromGroup(peer.getPeerId(), peerGroup.getGroupId());
			}
		}
		if (peerGroupId != null) {
			dataServiceRestClient.deletePeerGroup(peerGroupId);
		}
	}

	@Override
	public List<Peer> getPeersInGroup(Long peerGroupId, RestPageRequestBE restPageReqBE) {
		log.debug(EELFLoggerDelegate.debugLogger, "getPeersInGroup ={}", peerGroupId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		RestPageRequest pageRequest = PortalUtils.convertFromRestPageRequestBEToRestPageRequest(restPageReqBE);
		RestPageResponse<MLPPeer> MLPPeerResponse = dataServiceRestClient.getPeersInGroup(peerGroupId, pageRequest);
		
		List<Peer> mLPeerList = new ArrayList<>();
		for (MLPPeer mlpPeer : MLPPeerResponse.getContent()) {
			mLPeerList.add(PortalUtils.convertFromMLPPeerToMLPeer(mlpPeer));
		}
		 
		return mLPeerList;
	}

	@Override
	public void addPeerToGroup(String peerId, Long peerGroupId) {
		log.debug(EELFLoggerDelegate.debugLogger, "addPeerToGroup ={}", peerId, peerGroupId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.addPeerToGroup(peerId, peerGroupId);

	}

	@Override
	public void dropPeerFromGroup(String peerId, Long peerGroupId) {
		log.debug(EELFLoggerDelegate.debugLogger, "dropPeerFromGroup={}", peerId, peerGroupId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		if (peerId != null && peerGroupId != null) {
			dataServiceRestClient.dropPeerFromGroup(peerId, peerGroupId);
		}
	}

	@Override
	public RestPageResponseBE<MLPeerSolAccMap> getPeerSolutionGroupMaps(RestPageRequestBE restPageReqBE) {
		log.debug(EELFLoggerDelegate.debugLogger, "getPeerSolutionGroupMaps={}");
		
		RestPageRequest restPageReq = PortalUtils.convertFromRestPageRequestBEToRestPageRequest(restPageReqBE);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		RestPageResponse<MLPPeerSolAccMap> mlpPeerSolAccMapList = dataServiceRestClient.getPeerSolutionGroupMaps(restPageReq);
		
		RestPageResponseBE<MLPeerSolAccMap> mlPeerSolAccMap = PortalUtils.convertFromMLPPeerSolAccMapToMLPeerSolAccMap(mlpPeerSolAccMapList);
				
		return mlPeerSolAccMap;
	}

	@Override
	public void mapPeerSolutionGroups(Long peerGroupId, Long solutionGroupId) {
		log.debug(EELFLoggerDelegate.debugLogger, "mapPeerSolutionGroups={}", peerGroupId, solutionGroupId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.mapPeerSolutionGroups(peerGroupId, solutionGroupId);
	}

	@Override
	public void unmapPeerSolutionGroups(Long peerGroupId, Long solutionGroupId) {
		log.debug(EELFLoggerDelegate.debugLogger, "unmapPeerSolutionGroups={}", peerGroupId, solutionGroupId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.unmapPeerSolutionGroups(peerGroupId, solutionGroupId);
	}
 

	// getSolutionGroups
	@Override
	public List<MLSolutionGroup> getSolutionGroupList(RestPageRequestBE restPageReqBE) {
		log.debug(EELFLoggerDelegate.debugLogger, "getSolutionGroupList={}", restPageReqBE);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();

		RestPageRequest restPageReq = PortalUtils.convertFromRestPageRequestBEToRestPageRequest(restPageReqBE);

		RestPageResponse<MLPSolutionGroup> restPageRes = dataServiceRestClient.getSolutionGroups(restPageReq);

		List<MLSolutionGroup> mLSolutionGroupList = new ArrayList<>();
		for (MLPSolutionGroup mlpSolutionGrp : restPageRes.getContent()) {
			mLSolutionGroupList.add(PortalUtils.convertFromMLPSolutionGroupToMLSolutionGroup(mlpSolutionGrp));
		}

		return mLSolutionGroupList;
	}

	@Override
	public MLSolution getSolutionGroupDetails(String solutionGroupId, RestPageRequestBE restPageReqBE) {
		log.debug(EELFLoggerDelegate.debugLogger, "getSolutionGroupDetails={}", solutionGroupId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		RestPageRequest restPageReq = PortalUtils.convertFromRestPageRequestBEToRestPageRequest(restPageReqBE);

		RestPageResponse<MLPSolution> restPageRes = dataServiceRestClient
				.getSolutionsInGroup(Long.parseLong(solutionGroupId), restPageReq);

		// List<MLSolutionGroup> mLSolutionGroupList = new ArrayList<>();
		MLSolution mlSolution = null;
		for (MLPSolution mlpSolution : restPageRes.getContent()) {
			mlSolution = PortalUtils.convertFromMLPSolutionToMLSolution(mlpSolution);
		}

		return mlSolution;

	}
 
	@Override
	public MLSolutionGroup createSolutionGroup(MLSolutionGroup mlSolutionGroup) {
		log.debug(EELFLoggerDelegate.debugLogger, "saveMLSolutionGroup ={}", mlSolutionGroup);

		MLPSolutionGroup mlpSolutionGroup = PortalUtils.convertFromMLSolutionGroupToMLPSolutionGroup(mlSolutionGroup);
		MLSolutionGroup newMLSolutionGroup = new MLSolutionGroup();
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		if (mlpSolutionGroup != null) {
			newMLSolutionGroup = PortalUtils.convertFromMLPSolutionGroupToMLSolutionGroup(dataServiceRestClient.createSolutionGroup(mlpSolutionGroup));
		}
		return newMLSolutionGroup;
	}
	 
	@Override
	public void updateSolutionGroup(MLSolutionGroup mlSolutionGroup) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateSolutionGroup ={}", mlSolutionGroup);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		
		if (mlSolutionGroup != null) {
			dataServiceRestClient.updateSolutionGroup(PortalUtils.convertFromMLSolutionGroupToMLPSolutionGroup(mlSolutionGroup));
		}
	}
	
	@Override
	public void deleteSolutionGroup(Long groupId) {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteSolutionGroup ={}", groupId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		if (groupId != null) {
			dataServiceRestClient.deleteSolutionGroup(groupId);
		}
	}
	
	@Override
	public PeerGroup savePeerGroup(PeerGroup peerGroup) throws Exception {
		log.debug(EELFLoggerDelegate.debugLogger, "saveMLPeerGroup ={}", peerGroup);
		boolean groupExisted=false;
		PeerGroup newMLPeerGroup = new PeerGroup();
		RestPageRequestBE restRequestBE=new RestPageRequestBE();
		restRequestBE.setPage(0);
		restRequestBE.setSize(100);
		MLPPeerGroup mlpPeerGroup = PortalUtils.convertFromPeerGroupToMLPPeerGroup(peerGroup);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		if (mlpPeerGroup != null) {
			List<PeerGroup> existedPeerGroups= getPeerGroups(restRequestBE);
			for(PeerGroup peerGrp : existedPeerGroups){
				if(peerGrp.getName().equalsIgnoreCase(peerGroup.getName())){
					groupExisted=true;
					log.error(EELFLoggerDelegate.errorLogger, "PeerGroup "+peerGroup.getName()+" Already exists...");
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.CONSTRAINT_VIOLATION, "PeerGroup Already exists with the same name.");
				}
			}
			if(!groupExisted)
				newMLPeerGroup = PortalUtils.convertFromMLPPeerGroupToMLPeerGroup(dataServiceRestClient.createPeerGroup(mlpPeerGroup));
		}
		try{
			if(peerGroup.getPeers()!=null){
				for(Peer peer : peerGroup.getPeers()){
					dataServiceRestClient.addPeerToGroup(peer.getPeerId(), newMLPeerGroup.getGroupId());
				}
			}

		}catch(Exception e){
			log.error(EELFLoggerDelegate.errorLogger, "Error : Exception in addPeerToGroup() : Failed to add peer to the group", e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, "Failed to add peer to the group.");
		}
			
		return newMLPeerGroup;
	}
	
	@Override
	public void updatePeerGroup(Long groupId,PeerGroup peerGroup) throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "updatePeerGroup ={}", peerGroup);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		RestPageRequestBE restRequestBE=new RestPageRequestBE();
		restRequestBE.setPage(0);
		restRequestBE.setSize(100);
		MLPPeerGroup mlpPeerGroup = PortalUtils.convertFromPeerGroupToMLPPeerGroup(peerGroup);
		try{
			if (mlpPeerGroup != null && mlpPeerGroup.getName() !=null) {
				dataServiceRestClient.updatePeerGroup(mlpPeerGroup);
			}
		}
		catch(Exception e){
			log.error(EELFLoggerDelegate.errorLogger, "Error : Exception in updatePeerGroup() : Failed to updatePeerGroup",e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, "updatePeerGroup failed.");
		}
	
		try{
			if(peerGroup.getGroupId()!=null && peerGroup.getPeers()!=null && peerGroup.getPeers().size()>0 ){
				List<Peer> peerlistPerGroup=getPeersInGroup(groupId,restRequestBE);
				for(Peer peer : peerlistPerGroup){
					dataServiceRestClient.dropPeerFromGroup(peer.getPeerId(), peerGroup.getGroupId());
				}
				for(Peer peer : peerGroup.getPeers()){
					dataServiceRestClient.addPeerToGroup(peer.getPeerId(), peerGroup.getGroupId());
				}
			}
		}
		catch(Exception e){
			log.error(EELFLoggerDelegate.errorLogger, "Error : Exception in updatePeerGroup() : Failed to update peer to the group", e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, "Failed to update peer to the group.");
		}
	
	}
	
	//mapPeerSolutionGroups
	@Override
	public void updatePeerSolutionGroup(String peerGroupId, String solutionGroupId) {
		log.debug(EELFLoggerDelegate.debugLogger, "updatePeerSolutionGroup ={}", peerGroupId, solutionGroupId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		 
		if (peerGroupId != null && solutionGroupId != null) {
			dataServiceRestClient.mapPeerSolutionGroups(Long.parseLong(peerGroupId), Long.parseLong(solutionGroupId));
		}
	}
}
