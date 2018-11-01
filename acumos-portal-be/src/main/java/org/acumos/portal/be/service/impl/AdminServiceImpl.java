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

import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.service.AdminService;
import org.acumos.portal.be.transport.MLRequest;
import org.acumos.portal.be.transport.MLSolution;
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
}
