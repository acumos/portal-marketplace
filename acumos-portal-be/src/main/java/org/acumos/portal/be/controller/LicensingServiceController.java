package org.acumos.portal.be.controller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acumos.cds.domain.MLPLicenseProfileTemplate;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.acumos.licensemanager.profilevalidator.exceptions.LicenseProfileException;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.CredentialsService;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.common.exception.StorageException;
import org.acumos.portal.be.service.LicensingService;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.PushAndPullSolutionService;
import org.acumos.portal.be.transport.RightToUseDetails;
import org.acumos.portal.be.transport.RtuUser;
import org.acumos.portal.be.util.PortalConstants;
import org.acumos.portal.be.util.PortalUtils;
import org.acumos.portal.be.util.SanitizeUtils;
import org.acumos.securityverification.domain.Workflow;
import org.acumos.securityverification.utils.SVConstants;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/")
public class LicensingServiceController extends AbstractController{

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private LicensingService licensingService;

	@Autowired
	private MarketPlaceCatalogService marketPlaceService;

	@Autowired
	private PushAndPullSolutionService pushAndPullSolutionService;

	@Autowired
	CredentialsService credentialService;

	@Autowired
	private Environment env;

	@Autowired
	CredentialsService credentials;

	@ApiOperation(value = "Gets Solutions and Users details for the given RTU ReferenceId.", response = RightToUseDetails.class)
	@RequestMapping(value = {APINames.RTU_SOLUTION_USER_DETAILS }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RightToUseDetails> getRtuSolutionsAndUsers(HttpServletRequest request,
			@RequestHeader(value = "rtuReferenceId", required = true) String rtuReferenceId,
			@RequestHeader(value = "solutionName", required = false) String solutionName, HttpServletResponse response) {

		RightToUseDetails rightToUseDetails = new RightToUseDetails();
		MLPSolution mlpSolutionAssociatedWithRtuId = null;
		List<MLPUser> mlpUsersAssociatedWithRtuId = null;
		JsonResponse<RightToUseDetails> data = new JsonResponse<>();
		
		if(rtuReferenceId == null || rtuReferenceId.isEmpty()) {			
			data.setResponseCode(String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("RTU ReferenceId is null");
			data.setStatus(false);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return data;		
		}
		
		try {
			if (rtuReferenceId != null && solutionName == null) {
				log.debug("Getting Associated Solutions and Users details for RTU ReferenceId:  ", rtuReferenceId);
				//Getting List of MLPRightToUse objects from CDS
				List<MLPRightToUse> rtus = licensingService.getRtusByReference(rtuReferenceId);
				//Extracting only RtuIds as List from MLPRightToUse
				List<Long> rtuIds =new ArrayList<Long>();
				if(rtus.size() != 0) {
					MLPRightToUse rtu = rtus.get(0);
					rtuIds.add(rtu.getRtuId());
					rightToUseDetails.setRightToUse(rtu);
				}
					log.debug("List of RTUIDs associated to RTU ReferenceId:  ", rtuReferenceId);
					// Get Solution Details associated with RtuID. On UI Solution Name and Solution Id will be displayed
					List<MLPSolution> listOfSolutions = new ArrayList<MLPSolution>();
					for (Long rtuId : rtuIds) {
						mlpSolutionAssociatedWithRtuId = licensingService.getMLPSolutions(rtuId);
						listOfSolutions.add(mlpSolutionAssociatedWithRtuId);
					}
					Set<MLPSolution> allSolutionSet = new HashSet<MLPSolution>(listOfSolutions);
					//Convert back to List
					List<MLPSolution> uniqueAllSolutions = new ArrayList<MLPSolution>(allSolutionSet);	
					rightToUseDetails.setMlpSolutionAssociatedWithRtuId(uniqueAllSolutions);
					// Get All Active Users
					List<RtuUser> allUsers = licensingService.getAllActiveUsers();
					
					// Get users associated with RTUID.
					List<RtuUser> usersListAssociatedWithRtuId = new ArrayList<RtuUser>();
					for (Long rtuId : rtuIds) {
						mlpUsersAssociatedWithRtuId = licensingService.getMLPUsersAssociatedWithRtuId(rtuId);
						if (!PortalUtils.isEmptyList(mlpUsersAssociatedWithRtuId)) {
							for (MLPUser mlpuser : mlpUsersAssociatedWithRtuId) {
								RtuUser user = PortalUtils.convertToRtuUser(mlpuser,true);
								usersListAssociatedWithRtuId.add(user);
							}
						}
					}

					//Removing duplicate users from All users, if any
					Set<RtuUser> allUsersSet = new HashSet<RtuUser>(allUsers);
					//Convert back to List
					List<RtuUser> uniqueAllUsers = new ArrayList<RtuUser>(allUsersSet);	
					
					//Removing duplicate users related to usersListAssociatedWithRtuId
					Set<RtuUser> set = new HashSet<RtuUser>(usersListAssociatedWithRtuId);
					//Convert back to List
					List<RtuUser> uniqueUsersListAssociatedWithRtuId = new ArrayList<RtuUser>(set);
					//Removing Users associated to Rtuids from All users list which has associatedWithRtuFlag as false 
					uniqueAllUsers.removeAll(uniqueUsersListAssociatedWithRtuId);
					//Adding users associated to RtuIds to All users list with associatedWithRtuFlag as true
					uniqueAllUsers.addAll(uniqueUsersListAssociatedWithRtuId);				
					rightToUseDetails.setRtuUsers(uniqueAllUsers);
					
									
					data.setResponseBody(rightToUseDetails);
					if(rightToUseDetails.getMlpSolutionAssociatedWithRtuId().size() != 0)
						data.setResponseDetail("Solutions and Users details are fetched successfully for the given RTU ReferenceId:  "+rtuReferenceId);
					else
						data.setResponseDetail("There are no Solutions associated with the rtuReferenceId: "+rtuReferenceId);

						data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
					log.debug("Solutions and Users details are fetched successfully for the given RTU ReferenceId:  ", rtuReferenceId);
					
				/*} else {					
					log.debug("There are no RTUIDs associated with the rtuReferenceId: "+ rtuReferenceId);
					data.setResponseDetail("There are no RTUIDs associated with the rtuReferenceId: "+rtuReferenceId);				
				}*/
				
			} else if (solutionName != null) { 
				  log.debug("Getting list of solutions with exact solution name: "+ solutionName);
				  // Searching solution with exact solution name
				  Map<String, Object> solutoinNameParameter =  new HashMap<>(); 
				  solutoinNameParameter.put("name", solutionName);
			      RestPageResponse<MLPSolution> mlpSolutions =  licensingService.getMLPSolutionBySolutionName(solutoinNameParameter, false,  new RestPageRequest());
				  rightToUseDetails.setSolutionsByName(mlpSolutions); 
				  List<RtuUser> allUsers = licensingService.getAllActiveUsers();
                  rightToUseDetails.setRtuUsers(allUsers);			  
				  
				  data.setResponseBody(rightToUseDetails);
				  data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				  data.setResponseDetail("Solutions are fetched successfully for the solution name: "+solutionName);
				  log.debug("Solutions are fetched successfully for the solution name:   ", solutionName);
			  }
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			log.error("Exception occurred while fetching solutions and users details for RTU ReferenceId :" + rtuReferenceId, e);
		}
		return data;
	}
	
	
	
	@ApiOperation(value = "Create RTU User based on the RTU_ID, Solution_ID and list of Users of the local Acumos Instance .",  responseContainer = "List")
	@RequestMapping(value = { APINames.CREATE_RTU_USER }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLPRightToUse>> createRtuUser(HttpServletRequest request,
			HttpServletResponse response, 
			@PathVariable("rtuRefId") String rtuRefId,
			@PathVariable("solutionId") String solutionId,
			@PathVariable(value = "siteWideRtu", required = false) boolean siteWideRtu,
			@RequestBody(required = false) JsonRequest<List<String>> userList) {
		
		log.debug( "createRtuUser={}");
		JsonResponse<List<MLPRightToUse>> responseVO = new JsonResponse<>();
		List<MLPRightToUse> responseBody = null;
		try {
			// site wide rtu
			if (rtuRefId !=null && solutionId != null && siteWideRtu == true) {
				responseBody = licensingService.createRtuUser(rtuRefId, solutionId, siteWideRtu);
				
				if(!responseBody.isEmpty()) {
					responseVO.setContent(responseBody );
					responseVO.setStatus(true);
					responseVO.setResponseDetail("Saved successfully");
					responseVO.setStatusCode(HttpServletResponse.SC_OK);
				}else {
					responseVO.setStatus(false);
					responseVO.setResponseDetail("Failed");
					responseVO.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}else if (userList !=null && rtuRefId !=null && solutionId != null) {
				
				responseBody = licensingService.createRtuUser(rtuRefId, solutionId, userList.getBody());
				
				if(!responseBody.isEmpty()) {
					responseVO.setContent(responseBody );
					responseVO.setStatus(true);
					responseVO.setResponseDetail("Saved successfully");
					responseVO.setStatusCode(HttpServletResponse.SC_OK);
				}else {
					responseVO.setStatus(false);
					responseVO.setResponseDetail("Failed");
					responseVO.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
				
			}else {
				responseVO.setStatus(false);
				responseVO.setResponseDetail("Failed");
				responseVO.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (RightToUseException rtuExp){
			responseVO.setStatus(false);
			responseVO.setResponseDetail(rtuExp.getMessage());
			responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
			log.error( "Exception Occurred while createRtuUser()", rtuExp);
		} catch (Exception e) {
			responseVO.setStatus(false);
			responseVO.setResponseDetail(e.getMessage());
			responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
			log.error( "Exception Occurred while createRtuUser()", e);
		}
		return responseVO;
		
	}
	
	@ApiOperation(value = "API to Upload the license to the server")
	@RequestMapping(value = { APINames.UPLOAD_LICENSE_MODEL }, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public JsonResponse<String> uploadLicense(@RequestParam("file") MultipartFile file,
			@PathVariable("userId") String userId, @PathVariable String solutionId, @PathVariable String revisionId,
			@PathVariable String versionId, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String loggedInUserId = credentialService.getLoggedInUserId();
		userId = SanitizeUtils.sanitize(userId);
		solutionId = SanitizeUtils.sanitize(solutionId);
		revisionId = SanitizeUtils.sanitize(revisionId);
		versionId = SanitizeUtils.sanitize(versionId);

		JsonResponse<String> responseVO = new JsonResponse<>();
		String validationResponse=null;
		log.debug("upload License for user " + userId);

		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(solutionId) || StringUtils.isEmpty(revisionId) || StringUtils.isEmpty(versionId)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.info("UserId, SolutionId, RevisionId, versionId are required to upload the license");
			responseVO.setStatus(false);
			responseVO.setResponseDetail("UserId, SolutionId, RevisionId, versionId are required to upload the license");
			responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			try {
			
				String input= new String(file.getBytes());
				String filename = StringUtils.cleanPath(file.getOriginalFilename());
				
				if (!filename.endsWith(".json")) {				
					log.error("json File Required. Original File :  " + filename );
					throw new StorageException("json File Required. Original File : " + filename);
				}
				
				validationResponse=licensingService.validate(input);
				if(validationResponse=="SUCCESS") {
					FileItem fileItem = new DiskFileItemFactory().createItem("file", file.getContentType(), false, PortalConstants.LICENSE_FILENAME);
					try (InputStream in = file.getInputStream(); OutputStream out = fileItem.getOutputStream()) {
					    in.transferTo(out);
					} catch (Exception e) {
					    throw new IllegalArgumentException("Invalid file: " + e);
					}
					MultipartFile licenseFile = new CommonsMultipartFile(fileItem);
					boolean uploadedFile = pushAndPullSolutionService.uploadLicense(licenseFile, userId, solutionId, revisionId, versionId);

					if (uploadedFile) {
						Workflow workflow = performSVScan(solutionId, revisionId, SVConstants.UPDATED, loggedInUserId).get();
						if (workflow.isWorkflowAllowed()) {
							String licenseContent = marketPlaceService.getLicenseUrl(solutionId, versionId,
									PortalConstants.LICENSE_ARTIFACT_TYPE, PortalConstants.LICENSE_FILENAME_PREFIX);

							responseVO.setStatus(uploadedFile);
							responseVO.setResponseDetail("Success");
							responseVO.setResponseBody(licenseContent);
							responseVO.setStatusCode(HttpServletResponse.SC_OK);
						} else {
							responseVO.setStatus(false);
							responseVO.setErrorCode((isReasonInfo(workflow.getReason())) ? JSONTags.TAG_INFO_SV : JSONTags.TAG_ERROR_SV);
							responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
							responseVO.setResponseDetail(workflow.getReason());
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							log.error("SV failure while uploadLicense() : " + workflow.getReason());
						}
					} else {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						log.info("License file failed to upload in nexus.");
						responseVO.setStatus(false);
						responseVO.setResponseDetail("License file failed to upload in nexus.");
						responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					}
				}else {
					responseVO.setStatus(false);
					responseVO.setResponseDetail(validationResponse);
					responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					log.error("Error Occurred during validation of license file (else) "+validationResponse);
				}
			}catch(AcumosServiceException ae) {
				responseVO.setStatus(false);
				responseVO.setResponseDetail(ae.getMessage());
				responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write(ae.getMessage());
				response.flushBuffer();
				log.error("Exception Occurred during validation of license file", ae);
			}catch (StorageException e) {
				responseVO.setStatus(false);
				responseVO.setResponseDetail(e.getMessage());
				responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write(e.getMessage());
				response.flushBuffer();
				log.error("Exception Occurred while uploading the license in Push and Pull Solution service", e);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				responseVO.setStatus(false);
				responseVO.setResponseDetail(e.getMessage());
				responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				log.error("Exception Occurred while uploading the license in Push and Pull Solution service", e);
			}
		}
		return responseVO;
	}
	
	
	@RequestMapping(value = { APINames.UPLOAD_LICENSE_TEMPLATE },method = RequestMethod.POST)
	@ResponseBody
	public JsonResponse<Boolean> createJsonFile(@RequestBody String json,
			@PathVariable("userId") String userId,
			@PathVariable String solutionId, @PathVariable String revisionId,
			@PathVariable String versionId, HttpServletRequest request,
			HttpServletResponse response)throws IOException {
		JsonResponse<Boolean> responseVO = new JsonResponse<>();
		
		 try {      
			 	FileItem fileItem = new DiskFileItemFactory().createItem("file", "application/json", false, PortalConstants.LICENSE_FILENAME);
				try (InputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)); OutputStream out = fileItem.getOutputStream()) {
				    in.transferTo(out);
				} catch (Exception e) {
				    throw new IllegalArgumentException("Invalid file: " + e);
				}
				MultipartFile licenseFile = new CommonsMultipartFile(fileItem);
				uploadLicense( licenseFile, userId, solutionId, revisionId, versionId, request, response);
				responseVO.setResponseDetail("Success");
				responseVO.setStatusCode(HttpServletResponse.SC_OK);
	 
	        } catch (IOException e) {
	        	responseVO.setStatus(false);
				responseVO.setResponseDetail(e.getMessage());
				responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write(e.getMessage());
				response.flushBuffer();
	        }

		catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error(
					"Exception Occurred while creating json file", e);
		}
		return responseVO;
	}
	
	@ApiOperation(value = "Fetches all License Profiles",  responseContainer = "List")
	@RequestMapping(value = { APINames.GET_ALL_LICENSE_PROFILE }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLPLicenseProfileTemplate>> getTemplates(HttpServletRequest request,HttpServletResponse response) {
		JsonResponse<List<MLPLicenseProfileTemplate>> responseVO=new JsonResponse<>();
		List<MLPLicenseProfileTemplate> templateList=new ArrayList<>();
		try {
			templateList=licensingService.getTemplates();
			if (templateList != null) {
				responseVO.setResponseBody(templateList);
				responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				responseVO.setResponseDetail("License Profiles fetched successfully");
			} else {
				responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				responseVO.setResponseDetail("Error occured while fetching License Profiles");
				log.error("Error Occurred in Fetching License Profiles");
			}
		} catch (LicenseProfileException licExp){
			responseVO.setStatus(false);
			responseVO.setResponseDetail(licExp.getMessage());
			responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
			log.error( "Exception Occurred while fetching License Profiles", licExp);
		} catch (Exception e) {
			responseVO.setStatus(false);
			responseVO.setResponseDetail(e.getMessage());
			responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
			log.error( "Exception Occurred while fetching License Profiles", e);
		}
		return responseVO;
	}
	
	@ApiOperation(value = "Fetches License Profile By License ID", response = MLPLicenseProfileTemplate.class)
	@RequestMapping(value = { APINames.GET_LICENSE_PROFILE }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPLicenseProfileTemplate> getTemplate(HttpServletRequest request,@PathVariable long templateId,
			HttpServletResponse response) {
		JsonResponse<MLPLicenseProfileTemplate> responseVO=new JsonResponse<>();
		MLPLicenseProfileTemplate licenseProfileTemplate=null;
		try {
			licenseProfileTemplate=licensingService.getTemplate(templateId);
			if (licenseProfileTemplate != null) {
				responseVO.setResponseBody(licenseProfileTemplate);
				responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				responseVO.setResponseDetail("License Profile fetched successfully");
			} else {
				responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				responseVO.setResponseDetail("Error occured while fetching License Profile");
				log.error("Error Occurred in Fetching License Profile");
			}
		} catch (LicenseProfileException licExp){
			responseVO.setStatus(false);
			responseVO.setResponseDetail(licExp.getMessage());
			responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
			log.error( "Exception Occurred while fetching License Profile", licExp);
		} catch (Exception e) {
			responseVO.setStatus(false);
			responseVO.setResponseDetail(e.getMessage());
			responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
			log.error( "Exception Occurred while fetching License Profile", e);
		}
		return responseVO;
	}
	
    @ApiOperation(value = "Get License Profile URL", response = JsonResponse.class)
    @RequestMapping(value = {APINames.LICENSE_PROFILE_URL}, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
	public JsonResponse<String> getDocurl(HttpServletRequest request, HttpServletResponse response) {
		
		String licenseProfileUrl = env.getProperty("license_profile.url", "");
		JsonResponse<String> responseVO = new JsonResponse<String>();
		responseVO.setResponseBody(licenseProfileUrl);
		responseVO.setStatus(true);
		responseVO.setResponseDetail("Success");
		responseVO.setStatusCode(HttpServletResponse.SC_OK);
		return responseVO;
	}

}
