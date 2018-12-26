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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.Application;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.AdminService;
import org.acumos.portal.be.service.MailJet;
import org.acumos.portal.be.service.MailService;
import org.acumos.portal.be.service.UserRoleService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.PeerGroup;
import org.acumos.portal.be.transport.MLPeerSolAccMap;
import org.acumos.portal.be.transport.MLRequest;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLSolutionGroup;
import org.acumos.portal.be.transport.MailData;
import org.acumos.portal.be.transport.Peer;
import org.acumos.portal.be.transport.TransportData;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalConstants;
import org.acumos.portal.be.util.PortalUtils;
import org.acumos.portal.be.util.SanitizeUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(APINames.ADMIN)
public class AdminServiceController extends AbstractController {

    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;

	@Autowired
	private UserRoleService userRoleService;

    @Autowired
    MailService mailservice;

    @Autowired
    MailJet mailJet;

    @Autowired
    private Environment env;

    private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(AdminServiceController.class);


    public AdminServiceController() {
        // TODO Auto-generated constructor stub
    }


    @ApiOperation(value = "Gets paginated list of All Peers.", response = MLPPeer.class, responseContainer = "List")
    @RequestMapping(value = { APINames.PEERS_PAGINATED }, method = RequestMethod.POST, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<RestPageResponse<MLPPeer>> getPeerList(@RequestBody RestPageRequest restPageReq) {
        log.debug(EELFLoggerDelegate.debugLogger, "getPeerList"); 
        RestPageResponse<MLPPeer> mlpPeers = null;
        JsonResponse<RestPageResponse<MLPPeer>> data = new JsonResponse<>();
        try {
            
            mlpPeers = adminService.getAllPeers(restPageReq);
            if (mlpPeers != null) { 
                data.setResponseBody(mlpPeers);
                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
                data.setResponseDetail("Peers fetched Successfully");
            }
        } catch (Exception e) {
            
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Exception Occurred Fetching Peers for Admin Configuration");
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching Peers for Admin Configuration", e);
        }
        return data;
    }

@ApiOperation(value = "find out if a perticular model is downloadable by specific user.", response = MLPSiteConfig.class, responseContainer = "List")
    @RequestMapping(value = { APINames.GET_MODEL_DOWNLOADABLE}, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<Boolean> isModelDownloadable(@PathVariable("userId") String userId, @PathVariable("solutionId") String solutionId, HttpServletResponse response) {
        log.debug(EELFLoggerDelegate.debugLogger, "getSiteConfig");
		
        userId = SanitizeUtils.sanitize(userId);
        Boolean respFlag = Boolean.FALSE;
        MLPSiteConfig mlpSiteConfig = null;
        JsonResponse<Boolean> data = null;
        try {
            data = new JsonResponse<>();
            mlpSiteConfig = adminService.getSiteConfig("user_downloadmodel_mapping");
            if (mlpSiteConfig != null) {
            	String jsonVal = mlpSiteConfig.getConfigValue();
            	Map<String, Object> slidesConfigJson = mapper.readValue(jsonVal, Map.class);
            	for (Map.Entry<String, Object> entry : slidesConfigJson.entrySet()) {
            	//ArrayList userMap = (ArrayList)slidesConfigJson.get(userId);
					ArrayList userMap = (ArrayList) entry.getValue();
					System.out.println(userMap);
					for (Object object : userMap) {
						Map<String, Object> modelUserMap =(Map<String, Object>) object;
						for (Map.Entry<String, Object> modelUserEntry : modelUserMap.entrySet()) {
							String userKey = modelUserEntry.getKey();
							System.out.println("modelUserEntry key "+userKey);
							if(userId.equals(userKey)) {
								System.out.println("modelUserEntry value"+modelUserEntry.getValue());
								List<String> models = (ArrayList<String>)modelUserEntry.getValue();
								for (String model : models) {
									if(model.equals(solutionId)) {
										respFlag = true;
										 data.setResponseBody(respFlag);
							                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
							                data.setResponseDetail("SiteConfiguration fetched Successfully");
							                return data;
									}
								}
								
								System.out.println(models);
							}else {
								continue;
							}
							
							
						}
						System.out.println(modelUserMap);
					}
            	}
                data.setResponseBody(respFlag);
                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
                data.setResponseDetail("SiteConfiguration fetched Successfully");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Exception Occurred Fetching SiteConfiguration for Admin Configuration");
            log.error(EELFLoggerDelegate.errorLogger,
                    "Exception Occurred Fetching Site Configuration for Admin Configuration with Id user_downloadmodel_mapping");
        }
        return data;
    }

    @ApiOperation(value = "Gets peer details.", response = MLPPeer.class)
    @RequestMapping(value = { APINames.PEER_DETAILS }, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<MLPPeer> getPeerDetails(@PathVariable("peerId") String peerId) {
        log.debug(EELFLoggerDelegate.debugLogger, "getPeerDetails");
        MLPPeer mlpPeer = null;
        
        peerId = SanitizeUtils.sanitize(peerId);

        JsonResponse<MLPPeer> data = new JsonResponse<>();
        try {
            mlpPeer = adminService.getPeerDetail(peerId);
            if (mlpPeer != null) {
                data.setResponseBody(mlpPeer);
                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
                data.setResponseDetail("Peers fetched Successfully");
            }
        } catch (Exception e) {            
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Exception Occurred Fetching Peer for Admin Configuration");
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching Peer for Admin Configuration", e);
        }
        return data;
    }


    @ApiOperation(value = "Add a new peer", response = MLPPeer.class)
    @RequestMapping(value = { APINames.PEERS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
    @PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).ADMIN)")
    @ResponseBody
    public JsonResponse<Object> createPeer(@RequestBody JsonRequest<MLPPeer> peer) {
        log.debug(EELFLoggerDelegate.debugLogger, "createPeer={}", peer);
        JsonResponse<Object> data = new JsonResponse<>();
        MLPPeer newPeer = null;
        try {
            if (peer != null) {
                
                //First check if the Peer urls exists
                boolean isPeerExists = false;
                try {
                    MLPPeer mlpPeer = adminService.findPeerByApiAndWebUrl(peer.getBody().getApiUrl(), peer.getBody().getWebUrl());
                    if (mlpPeer != null) {
                        isPeerExists = true;
                    }
                } catch (Exception e) {
                    isPeerExists = false;
                }
                if (!isPeerExists) {
                    newPeer = adminService.savePeer(peer.getBody());
                    data.setResponseBody(newPeer);
                    data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
                    data.setResponseDetail("Success");
                } else {
                    data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
                    data.setResponseDetail("Reset_Content");
                } 
            } else {
                log.debug(EELFLoggerDelegate.errorLogger, "createPeer: Invalid Parameters");
                data.setErrorCode(JSONTags.TAG_ERROR_CODE);
                data.setResponseDetail("Create Peer Failed");
            } 

        }
        catch (Exception e) {
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Failed");
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while createPeer()", e);
        }
        return data;
    }


    @ApiOperation(value = "Update Peer details.", response = JsonResponse.class)
    @RequestMapping(value = { APINames.PEER_DETAILS }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
    @PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).ADMIN)")
    @ResponseBody
    public JsonResponse<Object> updatePeer(@PathVariable("peerId") String peerId, @RequestBody JsonRequest<MLPPeer> peer) {
        log.debug(EELFLoggerDelegate.debugLogger, "updatePeer={}", peer);
        JsonResponse<Object> data = new JsonResponse<>();
        try {
            if (peer != null && peer.getBody() != null) {
               adminService.updatePeer(peer.getBody());
               data.setStatus(true);
               data.setResponseDetail("Success");
               data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
            } else {  
               log.debug(EELFLoggerDelegate.errorLogger, "updatePeer: Invalid Parameters");
               data.setErrorCode(JSONTags.TAG_ERROR_CODE);
               data.setResponseDetail("Update Peer Failed");
            }
        }catch(Exception e) {
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Failed");
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while updatePeer()", e);
        }
        return data;
    }


    @ApiOperation(value = "Remove Peer.", response = MLPPeer.class)
    @RequestMapping(value = { APINames.PEER_DETAILS }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
    @PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).ADMIN)")
    @ResponseBody
    public JsonResponse<Object> removePeer(@PathVariable("peerId") String peerId) {
    	
    	peerId = SanitizeUtils.sanitize(peerId);
    	
        log.debug(EELFLoggerDelegate.debugLogger, "removePeer={}", peerId);
        JsonResponse<Object> data = new JsonResponse<>();
        try {
            
            if (PortalUtils.isEmptyOrNullString(peerId)) {
                log.debug(EELFLoggerDelegate.errorLogger, "removePeer: Invalid Parameters");
                data.setErrorCode(JSONTags.TAG_ERROR_CODE);
                data.setResponseDetail("Remove Peer Failed");
            } else {
                adminService.removePeer(peerId);
                data.setStatus(true);
                data.setResponseDetail("Success");
                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
            }
        }catch(Exception e) {
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Failed");
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while removePeer()", e);
        }
        return data;
    }

    @ApiOperation(value = "Gets paginated list of Peer Subscriptions.", response = MLPPeerSubscription.class, responseContainer = "List")
    @RequestMapping(value = { APINames.PEERSUBSCRIPTION_PAGINATED }, method = RequestMethod.POST, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<List<MLPPeerSubscription>> getPeerSubscriptions(
            @PathVariable("peerId") String peerId) {
        log.debug(EELFLoggerDelegate.debugLogger, "getPeerList");
        
        peerId = SanitizeUtils.sanitize(peerId);
        
        List<MLPPeerSubscription> subscriptionList = null;
        JsonResponse<List<MLPPeerSubscription>> data = new JsonResponse<>();
        try {
            subscriptionList = adminService.getPeerSubscriptions(peerId);
            if (subscriptionList != null) {
                data.setResponseBody(subscriptionList);
                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
                data.setResponseDetail("PeerSubscription fetched Successfully");
            }
        } catch (Exception e) {
            
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Exception Occurred Fetching PeerSubscription for Admin Configuration");
            log.error(EELFLoggerDelegate.errorLogger,
                    "Exception Occurred Fetching PeerSubscription for Admin Configuration", e);
        }
        return data;
    }
    

    @ApiOperation(value = "Gets counts of Peer Subscriptions for all peers.")
    @RequestMapping(value = { APINames.PEERSUBSCRIPTION_COUNTS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<Map<String,Integer>> getPeerSubscriptionCounts(
            @RequestBody JsonRequest<List<String>> jsonPeerIds) {
        log.debug(EELFLoggerDelegate.debugLogger, "getPeerSubscriptionCounts");
        List<String> peerIds = jsonPeerIds.getBody();
        
        if (peerIds != null) {
        	for (int i = 0; i < peerIds.size(); i++) {
        		String peerId = peerIds.get(i);
        		if (peerId != null) {
            		peerIds.set(i, SanitizeUtils.sanitize(peerId));
        		}
        	}
        }
        
        Map<String,Integer> subscriptionCounts = null;
        JsonResponse<Map<String,Integer>> data = new JsonResponse<>();
        try {
            subscriptionCounts = adminService.getPeerSubscriptionCounts(peerIds);
            if (subscriptionCounts != null) {
                data.setResponseBody(subscriptionCounts);
                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
                data.setResponseDetail("PeerSubscriptionCounts fetched Successfully");
            }
        } catch (Exception e) {
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Exception Occurred Fetching PeerSubscriptionCounts for Admin Configuration");
            log.error(EELFLoggerDelegate.errorLogger,
                    "Exception Occurred Fetching PeerSubscriptionCounts for Admin Configuration", e);
        }
        return data;
    }

    @ApiOperation(value = "Gets Subscription details.", response = MLPPeerSubscription.class)
    @RequestMapping(value = { APINames.SUBSCRIPTION_DETAILS }, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<MLPPeerSubscription> getPeerSubscriptionDetails(@PathVariable("subId") Long subId) {
        log.debug(EELFLoggerDelegate.debugLogger, "getPeerDetails");
        MLPPeerSubscription mlpSubscription = null;
        JsonResponse<MLPPeerSubscription> data = new JsonResponse<>();
        try {
            mlpSubscription = adminService.getPeerSubscription(subId);
            if (mlpSubscription != null) {
                data.setResponseBody(mlpSubscription);
                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
                data.setResponseDetail("Subscription fetched Successfully");
            }
        } catch (Exception e) {
            
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Exception Occurred Fetching mlpSubscription for Admin Configuration");
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching mlpSubscription for Admin Configuration", e);
        }
        return data;
    }
    
    @ApiOperation(value = "Add a new peer subscription", response = MLPPeerSubscription.class)
    @RequestMapping(value = { APINames.SUBSCRIPTION_CREATE }, method = RequestMethod.POST, produces = APPLICATION_JSON)
    @PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).ADMIN)")
    @ResponseBody
    public JsonResponse<MLPPeerSubscription> createPeerSubscription(@RequestBody JsonRequest<MLPPeerSubscription> peerSub) {
        log.debug(EELFLoggerDelegate.debugLogger, "createPeer={}", peerSub);
        JsonResponse<MLPPeerSubscription> data = new JsonResponse<>();
        MLPPeerSubscription peerSubscription = null;
        try {
            if (peerSub != null) {
                peerSubscription = adminService.createPeerSubscription(peerSub.getBody());
                data.setResponseBody(peerSubscription);
                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
                data.setResponseDetail("Success");
            } else {
                log.debug(EELFLoggerDelegate.errorLogger, "createPeerSubscription: Invalid Parameters");
                data.setErrorCode(JSONTags.TAG_ERROR_CODE);
                data.setResponseDetail("Create PeerSubscription Failed");
            }

        } catch (Exception e) {
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Failed");
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while createPeerSubscription()", e);
        }
        return data;
    }
    
    @ApiOperation(value = "Update Peer subscription details.", response = JsonResponse.class)
    @RequestMapping(value = { APINames.SUBSCRIPTION_UPDATE }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
    @PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).ADMIN)")
    @ResponseBody
    public JsonResponse<Object> updatePeerSubscription(@RequestBody JsonRequest<MLPPeerSubscription> peerSub) {
        log.debug(EELFLoggerDelegate.debugLogger, "updatePeerSubscription={}", peerSub);
        JsonResponse<Object> data = new JsonResponse<>();
        try {
            if (peerSub!= null && peerSub.getBody() != null) {
                adminService.updatePeerSubscription(peerSub.getBody());
                data.setStatus(true);
                data.setResponseDetail("Success");
                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
            } else {
                log.debug(EELFLoggerDelegate.errorLogger, "updatePeer: Invalid Parameters");
                data.setErrorCode(JSONTags.TAG_ERROR_CODE);
                data.setResponseDetail("Update Peer subscription Failed");
            }
        }catch(Exception e) {
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Failed");
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while updatePeerSubscription()", e);
        }
        return data;
    }
    
    @ApiOperation(value = "Remove Peer Subscription.", response = JsonResponse.class)
    @RequestMapping(value = { APINames.SUBSCRIPTION_DELETE }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
    @PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).ADMIN)")
    @ResponseBody
    public JsonResponse<Object> deletePeerSubscription(@PathVariable("subId") Long subId) {
        log.debug(EELFLoggerDelegate.debugLogger, "deletePeerSubscription={}", subId);
        JsonResponse<Object> data = new JsonResponse<>();
        try {
             if (subId != null) {
                 adminService.deletePeerSubscription(subId);
                 data.setStatus(true);
                 data.setResponseDetail("Success");
                 data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
             } else {
                 log.debug(EELFLoggerDelegate.errorLogger, "removePeer: Invalid Parameters");
                 data.setErrorCode(JSONTags.TAG_ERROR_CODE);
                 data.setResponseDetail("Remove Peer Subscription Failed");
             }
        }catch(Exception e) {
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Failed");
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while deletePeerSubscription()", e);
        }
        return data;
    }
    
    
    @ApiOperation(value = "Gets list of Site configuration.", response = MLPSiteConfig.class, responseContainer = "List")
    @RequestMapping(value = { APINames.GET_SITE_CONFIG}, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<MLPSiteConfig> getSiteConfiguration(@PathVariable("configKey") String configKey, HttpServletResponse response) {
        log.debug(EELFLoggerDelegate.debugLogger, "getSiteConfig");
		
        configKey = SanitizeUtils.sanitize(configKey);

        MLPSiteConfig mlpSiteConfig = null;
        JsonResponse<MLPSiteConfig> data = null;
        try {
            data = new JsonResponse<>();
            mlpSiteConfig = adminService.getSiteConfig(configKey);
            if (mlpSiteConfig != null) {
                data.setResponseBody(mlpSiteConfig);
                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
                data.setResponseDetail("SiteConfiguration fetched Successfully");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Exception Occurred Fetching SiteConfiguration for Admin Configuration");
            log.error(EELFLoggerDelegate.errorLogger,
                    "Exception Occurred Fetching Site Configuration for Admin Configuration with Id " + configKey);
        }
        return data;
    }
    
    
    @ApiOperation(value = "Gets list of Site configuration filtered with user's preferred tags.", response = MLPSiteConfig.class, responseContainer = "List")
    @RequestMapping(value = { APINames.GET_USER_CAROUSE_CONFIG }, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
	public JsonResponse<List<Map>> getUserCarousalConfiguration(
			@RequestParam(value = "userId", required = false) String userId, HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "getSiteConfig");

		MLPSiteConfig mlpSiteConfig = null;
		JsonResponse<List<Map>> data = null;
		try {
			data = new JsonResponse<>();
			List<Map> prefConfigResp = new ArrayList<>();
			mlpSiteConfig = adminService.getSiteConfig(PortalConstants.CAROUSEL_CONFIG_KEY);
			if (mlpSiteConfig != null) {

				ObjectMapper mapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String, Object> slidesConfigJson = mapper.readValue(mlpSiteConfig.getConfigValue(), Map.class);
				Set<MLPTag> prefTags = null;
				if (!StringUtils.isEmpty(userId)) {
					MLPUser user = userService.findUserByUserId(userId);
					prefTags = user.getTags();

				}
				for (Map.Entry<String, Object> entry : slidesConfigJson.entrySet()) {
					Map fields = (Map) entry.getValue();
					if (prefTags != null && prefTags.size() > 0) {
						for (MLPTag prefTag : prefTags) {
							if (fields.get(PortalConstants.TAG_NAME) != null && ((String) fields.get(PortalConstants.TAG_NAME)).equals(prefTag.getTag())) {
								Map<String, Object> resp = new HashMap<String, Object>();
								resp.put(entry.getKey(), entry.getValue());
								prefConfigResp.add(resp);

							}
						}
					} else {
						Map<String, Object> resp = new HashMap<String, Object>();
						resp.put(entry.getKey(), entry.getValue());
						prefConfigResp.add(resp);
					}
				}
				data.setResponseBody(prefConfigResp);

				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("getUserCarousalConfiguration fetched Successfully");
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Fetching SiteConfiguration for Admin Configuration");
			log.error(EELFLoggerDelegate.errorLogger,
					"Exception Occurred Fetching Site Configuration for Admin Configuration with Id " + PortalConstants.CAROUSEL_CONFIG_KEY);
		}
		return data;
	}
 

    @ApiOperation(value = "Create site configuration", response = MLPSiteConfig.class)
    @RequestMapping(value = { APINames.CREATE_SITE_CONFIG}, method = RequestMethod.POST, produces = APPLICATION_JSON)
    @PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).ADMIN)")
    @ResponseBody
    public JsonResponse<MLPSiteConfig> createSiteConfig(@RequestBody JsonRequest<MLPSiteConfig> mlpSiteConfig, HttpServletResponse response) {
        log.debug(EELFLoggerDelegate.debugLogger, "createSiteConfig={}", mlpSiteConfig);
        JsonResponse<MLPSiteConfig> data = null;
        MLPSiteConfig siteConfiguration = null;
        try {
            data = new JsonResponse<>();
            if(mlpSiteConfig != null) {
                adminService.createSiteConfig(mlpSiteConfig.getBody());
                data.setResponseBody(siteConfiguration);
                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
                data.setResponseDetail("Successfully create siteconfig");
            } else {
                data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            }
            log.debug(EELFLoggerDelegate.debugLogger, "createSiteConfig :  ");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Exception occured while createSiteConfig");
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred createSiteConfig :", e);
        }
        return data;
    }
    
    @ApiOperation(value = "Update site configuration", response = MLPSiteConfig.class)
    @RequestMapping(value = { APINames.UPDATE_SITE_CONFIG}, method = RequestMethod.PUT, produces = APPLICATION_JSON)
    @PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).ADMIN)")
    @ResponseBody
    public JsonResponse<MLPSiteConfig> updateSiteConfig(@PathVariable ("configKey") String configKey,@RequestBody JsonRequest<MLPSiteConfig> mlpSiteConfig, HttpServletResponse response) {
        
    	configKey = SanitizeUtils.sanitize(configKey);
    	
    	log.debug(EELFLoggerDelegate.debugLogger, "updateSiteConfig={}", mlpSiteConfig);
        JsonResponse<MLPSiteConfig> data = null;
        try {
            data = new JsonResponse<>();
            if(mlpSiteConfig != null){
                adminService.updateSiteConfig(mlpSiteConfig.getBody());
                //adminService.createSiteConfig(mlpSiteConfig.getBody());
                //data.setResponseBody(adminService.updateSiteConfig(mlpSiteConfig.getBody()));
                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
                data.setResponseDetail("Successfully updated siteconfig");
                log.debug(EELFLoggerDelegate.debugLogger, "updateSiteConfig :  ");
            } else {
                data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Exception occured while updateSiteConfig");
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred updateSiteConfig :", e);
        }
        return data;
    }
    
    
    
    @ApiOperation(value = "Remove Site Configuraion.", response = JsonResponse.class)
    @RequestMapping(value = { APINames.DELETE_SITE_CONFIG }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
    @PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).ADMIN)")
    @ResponseBody
    public JsonResponse<Object> deleteSiteConfig(@PathVariable("configKey") String configKey, HttpServletResponse response) {
        
    	configKey = SanitizeUtils.sanitize(configKey);
    	
    	log.debug(EELFLoggerDelegate.debugLogger, "deleteSiteConfig={}", configKey);
        JsonResponse<Object> data = null;
        try {
            data = new JsonResponse<>();
                adminService.deleteSiteConfig(configKey);
                data.setStatus(true);
                data.setResponseDetail("Success");
                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
        }catch(Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            data.setErrorCode(JSONTags.TAG_ERROR_RESPONSE);
            data.setResponseDetail("Failed while deleting site config");
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while deleteSiteConfig()", e);
        }
        return data;
    }
    
    @ApiOperation(value = "Gets the value of the MANIFEST.MF property Implementation-Version as written by maven.", response = TransportData.class)
    @RequestMapping(value = { APINames.GET_VERSION}, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
    public TransportData getVersion() {
         String className = this.getClass().getSimpleName() + ".class";
         String classPath = this.getClass().getResource(className).toString();
         String version = classPath.startsWith("jar") ? Application.class.getPackage().getImplementationVersion()
                 : "no version, classpath is not jar";
         return new TransportData(200, version);
     }
    
    @ApiOperation(value = "Get Dashboard URL", response = JsonResponse.class)
    @RequestMapping(value = { APINames.DASHBOARD}, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<String> getDocurl(HttpServletRequest request, HttpServletResponse response) {
        
        String docUrl = env.getProperty("portal.dashboard.url", "");
        JsonResponse<String> responseVO = new JsonResponse<String>();
        responseVO.setResponseBody(docUrl);
        responseVO.setStatus(true);
        responseVO.setResponseDetail("Success");
        responseVO.setStatusCode(HttpServletResponse.SC_OK);
        return responseVO;
    }
    
    @ApiOperation(value = "Gets a list of Requests ", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.GET_REQUESTS}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE> getAllRequests(@RequestBody RestPageRequest restPageReq) {
		log.debug(EELFLoggerDelegate.debugLogger, "getRequests");
		List<MLRequest> requestList = new ArrayList<>();
		JsonResponse<RestPageResponseBE> data = new JsonResponse<>();
		requestList = adminService.getAllRequests(restPageReq);
		if (requestList != null) {
			List test = new ArrayList<>();
			RestPageResponseBE responseBody = new RestPageResponseBE<>(test);
			responseBody.setRequestList(requestList);
			data.setResponseBody(responseBody);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Requests fetched  Successfully");
		} else {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Fetching requests");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching requests");
		}
		return data;
	}
    
    @ApiOperation(value = "Update Request details.", response = JsonResponse.class)
    @RequestMapping(value = { APINames.UPDATE_REQUEST}, method = RequestMethod.PUT, produces = APPLICATION_JSON)
    @PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).ADMIN)")
    @ResponseBody
    public JsonResponse<Object> updateRequest(@RequestBody JsonRequest<MLRequest> mlrequest) {
        log.debug(EELFLoggerDelegate.debugLogger, "updateRequest={}", mlrequest);
        JsonResponse<Object> data = new JsonResponse<>();
        try {
            if (mlrequest != null && mlrequest.getBody() != null) {
               adminService.updateMLRequest(mlrequest.getBody());
               data.setStatus(true);
               data.setResponseDetail("Success");
               data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
            } else {  
               log.debug(EELFLoggerDelegate.errorLogger, "updateRequest: Invalid Parameters");
               data.setErrorCode(JSONTags.TAG_ERROR_CODE);
               data.setResponseDetail("Update Request Failed");
            }
        }catch(Exception e) {
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Failed");
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while updaterequest()", e);
        }
        return data;
    }
    
    @ApiOperation(value = "Add peer subscription for models", response = MLPPeerSubscription.class)
    @RequestMapping(value = { APINames.CREATE_SUBSCREPTION }, method = RequestMethod.POST, produces = APPLICATION_JSON)
    @PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).ADMIN)")
    @ResponseBody
       public JsonResponse<MLPPeerSubscription> createSubscription(@RequestBody JsonRequest<List<MLSolution>> solList,@PathVariable("peerId") String peerId) {
           log.debug(EELFLoggerDelegate.debugLogger, "createSubscription={}");
           JsonResponse<MLPPeerSubscription> data = new JsonResponse<>();
           try {
               if (!solList.getBody().isEmpty() && peerId != null) {              
                   adminService.createSubscription(solList.getBody(),peerId);
                   data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
                   data.setResponseDetail("Success");
               } else {
                   log.debug(EELFLoggerDelegate.errorLogger, "createPeerSubscription: Invalid Parameters");
                   data.setErrorCode(JSONTags.TAG_ERROR_CODE);
                   data.setResponseDetail("Create PeerSubscription Failed");
               }

           } catch (Exception e) {
               e.printStackTrace();
               data.setErrorCode(JSONTags.TAG_ERROR_CODE);
               data.setResponseDetail("Failed");
               log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while createPeerSubscription()", e);
           }
           return data;
       }

    @ApiOperation(value = "Get SignUp Enabled", response = JsonResponse.class)
    @RequestMapping(value = {APINames.SIGNUP_ENABLED}, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
	public JsonResponse<String> isSignUpEnabled(HttpServletRequest request, HttpServletResponse response) {
		
		String isSignUpEnabled = env.getProperty("portal.feature.signup_enabled", "true");
		JsonResponse<String> responseVO = new JsonResponse<String>();
		responseVO.setResponseBody(isSignUpEnabled);
		responseVO.setStatus(true);
		responseVO.setResponseDetail("Success");
		responseVO.setStatusCode(HttpServletResponse.SC_OK);
		return responseVO;
	}
    

	@ApiOperation(value = "Add User from Admin", response = MLPRole.class)
	@RequestMapping(value = { APINames.ADD_USER }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).ADMIN)")
	@ResponseBody
	public JsonResponse<MLPRole> addUser(HttpServletRequest request, @RequestBody JsonRequest<User> user,
			HttpServletResponse response) {
		JsonResponse<MLPRole> data = new JsonResponse<>();
		User userDetails = user.getBody();
		User newUser = null;
		try {
			if (userDetails != null) {
				boolean isUserExists = false;

				MLPUser mlpUser = userService.findUserByEmail(userDetails.getEmailId());
				if (mlpUser != null) {
					isUserExists = true;
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_RESET_EMAILID);
					data.setResponseDetail("Reset_EmailId");
				}
				if (mlpUser == null) {
					mlpUser = userService.findUserByUsername(userDetails.getUsername());
					if (mlpUser != null) {
						isUserExists = true;
						data.setErrorCode(JSONTags.TAG_ERROR_CODE_RESET_USERNAME);
						data.setResponseDetail("Reset_UserName");
					}
				}
				if (!isUserExists) {
					//Create active to true when create from Admin
					userDetails.setActive("Y");
					newUser = userService.save(userDetails);

					if (newUser.getUserId() != null && user.getBody().getUserNewRoleList() != null) {
						for (String roleId : userDetails.getUserNewRoleList()) {
							userRoleService.addUserRole(newUser.getUserId(), roleId);
						}
						data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
						data.setResponseDetail("Role created Successfully");
						log.debug(EELFLoggerDelegate.debugLogger, "addUserRole :  ");
						
						sendCredentialsmail(userDetails);
					} else {
						data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
						data.setResponseDetail("Error Occurred while addUserRole()");
					}
				} else {
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
					data.setResponseDetail("User already exist");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Error occured while creating role");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while creating role :", e);
		}
		return data;
	}

    private void sendCredentialsmail(User mlpUser) { 
        //Send mail to user
        MailData mailData = new MailData();
        mailData.setSubject("Acumos New User Credentials");
        mailData.setFrom("support@acumos.org");
        mailData.setTemplate("newuserCredentials.ftl");
        List<String> to = new ArrayList<String>();
        to.add(mlpUser.getEmailId());
        mailData.setTo(to);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", mlpUser);
        model.put("signature", "Acumos Customer Service");
        mailData.setModel(model);

        try {
        	if(!PortalUtils.isEmptyOrNullString(env.getProperty("portal.feature.email_service")) 
        		&& env.getProperty("portal.feature.email_service").equalsIgnoreCase("smtp")) {
        		//Use SMTP setup
                mailservice.sendMail(mailData);
        		}else {
        			if(!PortalUtils.isEmptyOrNullString(env.getProperty("portal.feature.email_service")) 
                    		&& env.getProperty("portal.feature.email_service").equalsIgnoreCase("mailjet")) 
            			mailJet.sendMail(mailData);
        		}
            } catch (MailException ex) {
                log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while Sending Mail to user ={}", ex);
            }
        }

	// 4) Get All Solution Groups
	@ApiOperation(value = "Get All Solution Groups", response = JsonResponse.class)
	@RequestMapping(value = { APINames.SOLUTION_GROUP_LIST }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLSolutionGroup>> getSolutionGroups(HttpServletRequest request,
			@RequestBody JsonRequest<RestPageRequestBE> restPageReq, HttpServletResponse response) {

		JsonResponse<List<MLSolutionGroup>> responseVO = new JsonResponse<List<MLSolutionGroup>>();

		try {
			List<MLSolutionGroup> mlSolutionGroupList = adminService.getSolutionGroupList(restPageReq.getBody());

			if (mlSolutionGroupList != null) {
				responseVO.setContent(mlSolutionGroupList);
				responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				responseVO.setResponseDetail("Solution Group fetched Successfully");
				responseVO.setStatus(true);
				responseVO.setStatusCode(HttpServletResponse.SC_OK);
			}
		} catch (Exception e) {
			responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE);
			responseVO.setResponseDetail("Exception Occurred Fetching Peer for Admin Configuration");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching Peer for Admin Configuration", e);
		}

		return responseVO;
	}

	// 5) Get Solution Group with Id
	@ApiOperation(value = "Gets a Solution Group for the given GroupId. ", response = MLSolution.class)
	@RequestMapping(value = {
			APINames.SOLUTIONS_GROUP_DETAILS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLSolution> getSolutionsGroupDetails(HttpServletRequest request,
			@RequestBody JsonRequest<RestPageRequestBE> restPageReqBE, @PathVariable String groupId,
			HttpServletResponse response) {

		JsonResponse<MLSolution> responseVO = new JsonResponse<>();

		try {
			MLSolution mlSolutionGroupDetail = adminService.getSolutionGroupDetails(groupId, restPageReqBE.getBody());

			if (mlSolutionGroupDetail != null) {
				responseVO.setContent(mlSolutionGroupDetail);
				responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				responseVO.setResponseDetail("Solution Group fetched Successfully");
				responseVO.setStatus(true);
				responseVO.setStatusCode(HttpServletResponse.SC_OK);
			}
		} catch (Exception e) {
			responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE);
			responseVO.setResponseDetail("Exception Occurred Fetching Peer for Admin Configuration");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching Peer for Admin Configuration", e);
		}

		return responseVO;
	}

	// 1) Create Solution Group
	// createSolutionGroup
	@ApiOperation(value = "Add a new Solution Group", response = MLSolutionGroup.class)
	@RequestMapping(value = {
			APINames.CREATE_SOLUTION_GROUP }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> createSolutionGroup(@RequestBody JsonRequest<MLSolutionGroup> mlSolutionGroup) {
		log.debug(EELFLoggerDelegate.debugLogger, "createSolutionGroup={}", mlSolutionGroup);
		JsonResponse<Object> data = new JsonResponse<>();

		MLSolutionGroup newMLSolutionGroup = null;
		try {
			if (mlSolutionGroup != null) {

				newMLSolutionGroup = adminService.createSolutionGroup(mlSolutionGroup.getBody());
				data.setResponseBody(newMLSolutionGroup);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Success");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
				data.setResponseDetail("Reset_Content");
			}

		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Failed");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while createSolutionGroup()", e);
		}

		return data;
	}

	// 2) Update Solution Group
	@ApiOperation(value = "Update Solution Group details.", response = JsonResponse.class)
	@RequestMapping(value = { APINames.UPDATE_SOLUTION_GROUP }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> updateSolutionGroup(@RequestBody JsonRequest<MLSolutionGroup> mlSolutionGroup) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateSolutionGroup={}", mlSolutionGroup);
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			if (mlSolutionGroup != null && mlSolutionGroup.getBody() != null) {
				adminService.updateSolutionGroup(mlSolutionGroup.getBody());
				data.setStatus(true);
				data.setResponseDetail("Success");
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			} else {
				log.debug(EELFLoggerDelegate.errorLogger, "updateSolutionGroup: Invalid Parameters");
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("update Solution Group Failed");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Failed");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while updateSolutionGroup()", e);
		}
		return data;
	}

	// 3) Delete Solution Group
	@ApiOperation(value = "Remove Solution Group.", response = JsonResponse.class)
	@RequestMapping(value = {
			APINames.DELETE_SOLUTION_GROUP }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> deleteSolutionGroup(@PathVariable("groupId") Long groupId) {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteSolutionGroup={}", groupId);
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			if (groupId != null) {
				adminService.deleteSolutionGroup(groupId);
				data.setStatus(true);
				data.setResponseDetail("Success");
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			} else {
				log.debug(EELFLoggerDelegate.errorLogger, "deleteSolutionGroup: Invalid Parameters");
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("delete Solution Group Failed");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Failed");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while deleteSolutionGroup()", e);
		}
		return data;
	}

	// 1) Create Peer Group
	@ApiOperation(value = "Add a new Peer Group", response = PeerGroup.class ,responseContainer="List")
	@RequestMapping(value = { APINames.CREATE_PEER_GROUP }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<PeerGroup> createPeerGroup(@RequestBody JsonRequest<PeerGroup> peerGrpRequest) {
		log.debug(EELFLoggerDelegate.debugLogger, "createPeerGroup={}", peerGrpRequest);
		JsonResponse<PeerGroup> data = new JsonResponse<>();
		PeerGroup newMLPeerGroup = null;
		try {
			if (peerGrpRequest != null) {
				newMLPeerGroup = adminService.savePeerGroup(peerGrpRequest.getBody());
				newMLPeerGroup.setPeers(peerGrpRequest.getBody().getPeers());
				data.setResponseBody(newMLPeerGroup);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setStatusCode(200);
				data.setResponseDetail("Success");
			} 
		}
		catch(AcumosServiceException ae){
			data.setStatusCode(400);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail(ae.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while createPeerGroup()", ae);
		}
		catch (Exception e) {
			data.setStatusCode(400);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Failed");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while createPeerGroup()", e);
		}
		
		return data;
	}

	// 2) Update Peer Group
	@ApiOperation(value = "Update Peer Group.", response = JsonResponse.class)
	@RequestMapping(value = { APINames.UPDATE_PEER_GROUP }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> updatePeerGroup(@RequestBody JsonRequest<PeerGroup> peerGrpRequest, @PathVariable("peerGroupId") Long peerGroupId) {
		log.debug(EELFLoggerDelegate.debugLogger, "updatePeerGroup={}", peerGrpRequest);
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			if (peerGrpRequest != null && peerGroupId !=null  && peerGrpRequest.getBody() != null) {
				adminService.updatePeerGroup(peerGroupId,peerGrpRequest.getBody());
				data.setStatus(true);
				data.setResponseDetail("Success");
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			} else {
				log.debug(EELFLoggerDelegate.errorLogger, "updatePeerGroup: Invalid Parameters");
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("Update Peer Group Failed");
			}
		} 
		catch(AcumosServiceException ae){
			data.setStatusCode(400);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail(ae.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while updatePeerGroup()", ae);
		 }
		 catch (Exception e) {
			data.setStatusCode(400);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Failed");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while updatePeerGroup()", e);
		 }
		return data;
	}

	// 3) Delete Peer Group
	@ApiOperation(value = "Remove Peer Group.", response = JsonResponse.class)
	@RequestMapping(value = { APINames.DELETE_PEER_GROUP }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> deletePeerGroup(@RequestBody JsonRequest<PeerGroup> peerGrpRequest, @PathVariable("peerGroupId") Long peerGroupId ) {
		log.debug(EELFLoggerDelegate.debugLogger, "deletePeerGroup={}", peerGroupId);
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			if (peerGroupId != null && peerGrpRequest.getBody() !=null) {
				adminService.deletePeerGroup(peerGroupId,peerGrpRequest.getBody());
				data.setStatus(true);
				data.setResponseDetail("Success");
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			} else {
				log.debug(EELFLoggerDelegate.errorLogger, "deletePeerGroup: Invalid Parameters");
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("Remove Peer Group Failed");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Failed");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while deletePeerGroup()", e);
		}
		return data;
	}



	//2)	Update Peer Solution Group Mapping 
	@ApiOperation(value = "Update Peer Solution Group.", response = JsonResponse.class)
	@RequestMapping(value = { APINames.UPDATE_PEER_SOLUTION_GROUP }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> updatePeerSolutionGroup(@PathVariable("peerGroupId") String peerGroupId,
			@PathVariable("solutionGroupId") String solutionGroupId) {
		log.debug(EELFLoggerDelegate.debugLogger, "updatePeerSolutionGroup={}", peerGroupId, solutionGroupId);
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			if (peerGroupId != null && solutionGroupId != null) {
				adminService.updatePeerSolutionGroup( peerGroupId, solutionGroupId);
				data.setStatus(true);
				data.setResponseDetail("Success");
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			} else {
				log.debug(EELFLoggerDelegate.errorLogger, "updatePeerSolutionGroup: Invalid Parameters");
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("Update Peer Solution Group Failed");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Failed");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while updatePeerSolutionGroup()", e);
		}
		return data;
	}
	
	//4)	Get All Peers Groups 
	@ApiOperation(value = "Gets a list of PeerGroups ", response = PeerGroup.class ,responseContainer="List")
    @RequestMapping(value = { APINames.PEER_GROUPS_LIST}, method = RequestMethod.POST, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<List<PeerGroup>> getPeerGroups(@RequestBody RestPageRequestBE restPageReqBE ) {
    	log.debug(EELFLoggerDelegate.debugLogger, "getPeerGroups");
    	JsonResponse<List<PeerGroup>> data = new JsonResponse<>();
    	try{
    		List<PeerGroup> PeerGroupList = adminService.getPeerGroups(restPageReqBE);
    		if (PeerGroupList != null) {
    			data.setResponseBody(PeerGroupList);
    			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
    			data.setResponseDetail("PeerGroups fetched  Successfully"); 
    		}
    	}
    	catch (Exception e){
    		data.setErrorCode(JSONTags.TAG_ERROR_CODE);
    		data.setResponseDetail("Exception Occurred in Fetching PeerGroups for Admin Configuration");
    		log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred in Fetching MLPPeerGroups for Admin Configuration");
    	}

    	return data; 	
    }

/*
	//3)	Get All Peer Solution Group
	@ApiOperation(value = "Get the peers of the specified PeerGroup ", response = MLPeer.class ,responseContainer="List")
    @RequestMapping(value = { APINames.GET_PEER_GROUP}, method = RequestMethod.POST, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<List<MLPeer>> getPeersInGroup(@PathVariable("groupId") Long groupId, @RequestBody JsonRequest<RestPageRequestBE> restPageReqBE ) {
    	log.debug(EELFLoggerDelegate.debugLogger, "getPeersInGroup");

    	JsonResponse<List<MLPeer>> data = new JsonResponse<>();
    	try{
    		
    		List<MLPeer> mlPeerList = adminService.getPeersInGroup(groupId,restPageReqBE.getBody());
    		if (mlPeerList != null) {
    			data.setResponseBody(mlPeerList);
    			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
    			data.setResponseDetail("MLPPeers from specified Group fetched  Successfully"); 
    		}
    	}
    	catch (Exception e){
    		data.setErrorCode(JSONTags.TAG_ERROR_CODE);
    		data.setResponseDetail("Exception Occurred in Fetching getPeersInGroup for Admin Configuration");
    		log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred in Fetching getPeersInGroup for Admin Configuration");
    	}

    	return data; 	
    }
     */
	//1)	Create Peer Solution Group Mapping 
    @ApiOperation(value = "Create Peer Group and Solution Group Mapping.", response = JsonResponse.class)
    @RequestMapping(value = { APINames.CREATE_PEER_SOLUTION_GROUP }, method = RequestMethod.POST, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<Object> mapPeerGroupSolutionGroup(@PathVariable("peerGroupId") Long peerGroupId , @PathVariable("solutionGroupId") Long solutionGroupId) {
        log.debug(EELFLoggerDelegate.debugLogger, "mapPeerGroupSolutionGroup={}", peerGroupId , solutionGroupId);
        JsonResponse<Object> data = new JsonResponse<>();
        try {
            if (peerGroupId != null && solutionGroupId != null) {
                adminService.mapPeerSolutionGroups(peerGroupId, solutionGroupId);
                data.setStatus(true);
                data.setResponseDetail("Success");
                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
            } else {
                log.debug(EELFLoggerDelegate.errorLogger, "mapPeerGroupSolutionGroup: Invalid Parameters");
                data.setErrorCode(JSONTags.TAG_ERROR_CODE);
                data.setResponseDetail("map PeerGroup and SolutionGroup Failed");
            }
        }catch(Exception e) {
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Failed");
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while mapPeerGroupSolutionGroup()", e);
        }
        return data;
    }
    
    //4)	Delete Peer Solution Group
    @ApiOperation(value = "Remove Peer Group and Solution Group Mapping.", response = JsonResponse.class)
    @RequestMapping(value = { APINames.DELETE_PEER_SOLUTION_GROUP }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<Object> unmapPeerGroupSolutionGroup(@PathVariable("peerGroupId") Long peerGroupId , @PathVariable("solutionGroupId") Long solutionGroupId) {
        log.debug(EELFLoggerDelegate.debugLogger, "unmapPeerGroupSolutionGroup={}", peerGroupId , solutionGroupId);
        JsonResponse<Object> data = new JsonResponse<>();
        try {
            if (peerGroupId != null && solutionGroupId != null) {
                adminService.unmapPeerSolutionGroups(peerGroupId, solutionGroupId);
                data.setStatus(true);
                data.setResponseDetail("Success");
                data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
            } else {
                log.debug(EELFLoggerDelegate.errorLogger, "unmapPeerGroupSolutionGroup: Invalid Parameters");
                data.setErrorCode(JSONTags.TAG_ERROR_CODE);
                data.setResponseDetail("unmap PeerGroup and SolutionGroup Failed");
            }
        }catch(Exception e) {
            data.setErrorCode(JSONTags.TAG_ERROR_CODE);
            data.setResponseDetail("Failed");
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while unmapPeerGroupSolutionGroup()", e);
        }
        return data;
    }
    
    //3)	Get All Peer Solution Group
	// getPeerSolutionGroupMaps
	@ApiOperation(value = "Gets Peer Solution Group ", response = MLSolution.class)
	@RequestMapping(value = { APINames.PEER_SOLUTION_GROUP }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE> getPeerSolutionGroupMaps(HttpServletRequest request,
			@RequestBody JsonRequest<RestPageRequestBE> restPageReqBE, HttpServletResponse response) {

		JsonResponse<RestPageResponseBE> responseVO = new JsonResponse<>();
		try {

			RestPageResponseBE<MLPeerSolAccMap> mlSolutionGroupDetail = adminService
					.getPeerSolutionGroupMaps(restPageReqBE.getBody());

			if (mlSolutionGroupDetail != null) {
				responseVO.setContent(mlSolutionGroupDetail);
				responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				responseVO.setResponseDetail("Peer Solution Group fetched Successfully");
				responseVO.setStatus(true);
				responseVO.setStatusCode(HttpServletResponse.SC_OK);
			}
		} catch (Exception e) {
			responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE);
			responseVO.setResponseDetail("Exception Occurred Fetching Peer Solution Group");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching Peer Solution Group", e);
		}

		return responseVO;
	}
}
