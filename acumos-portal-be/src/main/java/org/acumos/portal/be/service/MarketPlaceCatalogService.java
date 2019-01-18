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
 
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.transport.Author;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLSolutionFavorite;
import org.acumos.portal.be.transport.MLSolutionRating;
import org.acumos.portal.be.transport.MLSolutionWeb;
import org.acumos.portal.be.transport.User;
import org.springframework.web.multipart.MultipartFile;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPRevisionDescription;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPSolutionWeb;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.AuthorTransport;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.acumos.portal.be.transport.RevisionDescription;
import org.acumos.cds.transport.RestPageResponse;

/**
 * Interface for Supporting Market Place Catalog and Manage models modules
 */
public interface MarketPlaceCatalogService {
	 
	List<MLSolution> getAllPublishedSolutions() throws AcumosServiceException;
	
	RestPageResponse<MLPSolution> getAllPaginatedSolutions(Integer page, Integer size, String sortingOrder) throws AcumosServiceException;
	
	MLSolution getSolution(String solutionId) throws AcumosServiceException;

	MLSolution getSolution(String solutionId, String loginUserId) throws AcumosServiceException;

	List<MLSolution> getAllSolutions() throws AcumosServiceException;
	
	//List<MLSolution> getAllMySolutions(String userId);
	
	MLSolution deleteSolution(MLSolution mlSolution) throws AcumosServiceException;
	
	MLSolution updateSolution(MLSolution mlSolution, String solutionId) throws AcumosServiceException;
	
	MLSolution deleteSolutionArtifacts(MLSolution mlSolution, String solutionId, String revisionId)
			throws AcumosServiceException, URISyntaxException;

	//List<MLSolution> searchSolution(String searchTerm);

	List<MLSolution> getSearchSolution(String search) throws AcumosServiceException;

	//RestPageResponseBE<MLSolution> getSearchSolution(JsonRequest<RestPageRequestBE> restPageReqBe) throws AcumosServiceException;

	//RestPageResponseBE<MLSolution> getAllMySolutions(String userId, JsonRequest<RestPageRequestBE> restPageReqBe) throws AcumosServiceException;
	
	/**
	 * @param solutionId : SolutionId for which Solution Revision Needs to be returned
	 * 
	 * @return List of the Solution Revision for the specified solution Id
	 * @throws AcumosServiceException On failure
	 */
	List<MLPSolutionRevision> getSolutionRevision(String solutionId) throws AcumosServiceException;
	
	/**
	 * @param solutionId : SolutionId for which Solution Revision Artifacts Needs to be returned
	 * @param revisionId : RevisionId of the Solution for which List of Artifacts are needed.
	 * 
	 * @return List of the Solution Artifacts for the specified solution Id and revisionId
	 * @throws AcumosServiceException On failure
	 */
	List<MLPArtifact> getSolutionArtifacts(String solutionId, String revisionId) throws AcumosServiceException;
	
	void addSolutionTag(String solutionId, String tag) throws AcumosServiceException;

    void dropSolutionTag(String solutionId, String tag) throws AcumosServiceException;
    
    List<String> getTags(JsonRequest<RestPageRequest> restPageReq) throws AcumosServiceException;
    
    List<User> getSolutionUserAccess(String solutionId) throws AcumosServiceException;

    void dropSolutionUserAccess(String solutionId, String userId) throws AcumosServiceException;

	void incrementSolutionViewCount(String solutionId) throws AcumosServiceException;

	void updateSolutionRating(MLPSolutionRating body) throws AcumosServiceException;
 
	List<MLSolution> getMySharedModels(String userId, RestPageRequest restPageReq) throws AcumosServiceException;

	RestPageResponseBE<MLSolution> getTagBasedSolutions(String tags, JsonRequest<RestPageRequestBE> restPageReq) throws AcumosServiceException;

	MLSolutionRating createSolutionrating(MLPSolutionRating mlpSolutionRating) throws AcumosServiceException;

	MLSolutionFavorite createSolutionFavorite(MLPSolutionFavorite mlpSolutionFavorite) throws AcumosServiceException;

	void deleteSolutionFavorite(MLPSolutionFavorite mlpSolutionFavorite) throws AcumosServiceException;

	List<MLSolution> getFavoriteSolutions(String userId, RestPageRequest restPageReqBe) throws AcumosServiceException;

	RestPageResponseBE<MLSolution> getRelatedMySolutions(JsonRequest<RestPageRequestBE> restPageReq) throws AcumosServiceException;

	//void addSolutionUserAccess(String solutionId, JsonRequest<List<User>> userId);

	void addSolutionUserAccess(String solutionId, List<String> userId) throws AcumosServiceException;

	RestPageResponse<MLPSolutionRating> getSolutionRating(String solutionId, RestPageRequest pageRequest) throws AcumosServiceException;

	MLPTag createTag(MLPTag body) throws AcumosServiceException;	

	MLPSolutionRating getUserRatings(String solutionId, String userId); 

	RestPageResponseBE<MLSolution> findPortalSolutions(RestPageRequestPortal pageRequestPortal); 

	RestPageResponseBE<MLSolution> findPortalSolutions(RestPageRequestPortal pageRequestPortal, Set<MLPTag> prefTags); 

	RestPageResponseBE<MLSolution> findUserSolutions(RestPageRequestPortal pageRequestPortal); 

	RestPageResponse<MLPSolution> getUserAccessSolutions(String userId, RestPageRequest pageRequest);

	MLSolutionWeb getSolutionWebMetadata(String solutionId); 

	String getProtoUrl(String solutionId, String version, String artifactType, String fileExtension) throws AcumosServiceException;

	boolean checkUniqueSolName(String solutionId, String solName);

	List<Author> getSolutionRevisionAuthors(String solutionId, String revisionId);

	List<Author> addSolutionRevisionAuthors(String solutionId, String revisionId, Author author) throws AcumosServiceException;

	List<Author> removeSolutionRevisionAuthors(String solutionId, String revisionId, Author author) throws AcumosServiceException;

	MLPDocument addRevisionDocument(String solutionId, String revisionId, String accessType, String userId, MultipartFile file) throws AcumosServiceException;

	MLPDocument removeRevisionDocument(String solutionId, String revisionId, String accessType, String string,
			String documentId) throws AcumosServiceException;

	List<MLPDocument> getRevisionDocument(String solutionId, String revisionId, String accessType, String string) throws AcumosServiceException;

	List<MLPDocument> copyRevisionDocuments(String solutionId, String revisionId, String accessType, String userId,
			String fromRevisionId) throws AcumosServiceException;

	RevisionDescription getRevisionDescription(String revisionId, String accessType) throws AcumosServiceException;

	RevisionDescription addUpdateRevisionDescription(String revisionId, String accessType, RevisionDescription description) throws AcumosServiceException;
	List<Map<String, String>> getPreferredTagsList(JsonRequest<RestPageRequest> restPageReq, String userId) throws AcumosServiceException;
	void createUserTag(String userId, List<String> mlpTag, List<String> dropTagList) throws AcumosServiceException;

	RestPageResponseBE<MLSolution> searchSolutionsByKeyword(RestPageRequestPortal pageReqPortal);
	
}
