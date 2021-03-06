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

/**
 * 
 */
package org.acumos.portal.be.transport;

import java.time.Instant;
import java.util.List;

import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPThread;
  
/**
 *	MLSolution Class to hold the Machine Learning Solution Information
 */
public class MLSolution {

	private String solutionId;
	private String name;
	private String ownerId;
	private String ownerName;
	private String metadata;
	private boolean active;
	private List<String> catalogNames;
	private Instant created;
	private Instant modified;
	private String tookitType;
	private String tookitTypeName;
    private List<MLPSolutionRevision> revisions;
	private String loginName;
	private int pageNo;
	private int size;
	private String sortingOrder;
	private String modelType;
	private String modelTypeName;
	private int downloadCount;
	private int solutionRating;
	private float solutionRatingAvg;
	private String solutionTag;
	private List<MLPTag> solutionTagList;
	private int viewCount;
	private int ratingAverageTenths;
	private int ratingCount;
	private int PrivateModelCount;
	private int PublicModelCount;
	private int CompanyModelCount;
	private int DeletedModelCount;
	private List<User> ownerListForSol;
	private String threadId;
	private String commentId;
	private List<MLPThread> threadList ;
	private String validationStatusCode;	
	private Long refreshInterval;
	private String selector;
	private boolean onboardingStatusFailed;
	private String errorDetails;
	private String latestRevisionId;
	private long commentsCount;
	private String publisher;
	private List<Author> authors;
	private boolean pendingApproval;
	private Instant lastDownload;
	private boolean featured;
	private String catalogId;
	private String sourceId;
	private MLPeer mPeer;
	
	/**
	 * @return the onboardingStatusFailed
	 */
	public boolean isOnboardingStatusFailed() {
		return onboardingStatusFailed;
	}

	/**
	 * @param onboardingStatusFailed the onboardingStatusFailed to set
	 */
	public void setOnboardingStatusFailed(boolean onboardingStatusFailed) {
		this.onboardingStatusFailed = onboardingStatusFailed;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * 
	 */
	public MLSolution() {}

	/**
	 * @return the solutionId
	 */
	public String getSolutionId() {
		return solutionId;
	}

	/**
	 * @param solutionId the solutionId to set
	 */
	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the ownerId
	 */
	public String getOwnerId() {
		return ownerId;
	}

	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * @return the ownerName
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @param ownerName the ownerName to set
	 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	/**
	 * @return the metadata
	 */
	public String getMetadata() {
		return metadata;
	}

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the catalog names
	 */
	public List<String> getCatalogNames() {
		return catalogNames;
	}
	
	/**
	 * @param catalogNames the catalog names to set
	 */
	public void setCatalogNames(List<String> catalogNames) {
		this.catalogNames = catalogNames;
	}

	/**
	 * @return the created
	 */
	public Instant getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Instant created) {
		this.created = created;
	}

	/**
	 * @return the modified
	 */
	public Instant getModified() {
		return modified;
	}

	/**
	 * @param modified the modified to set
	 */
	public void setModified(Instant modified) {
		this.modified = modified;
	}

	/**
	 * @return the revisions
	 */
	public List<MLPSolutionRevision> getRevisions() {
		return revisions;
	}

	/**
	 * @param revisions the revisions to set
	 */
	public void setRevisions(List<MLPSolutionRevision> revisions) {
		this.revisions = revisions;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getSortingOrder() {
		return sortingOrder;
	}

	public void setSortingOrder(String sortingOrder) {
		this.sortingOrder = sortingOrder;
	}

	public String getTookitType() {
		return tookitType;
	}

	public void setTookitType(String tookitType) {
		this.tookitType = tookitType;
	}
	
	public List<Author> getAuthors() {
		return authors;
	}
	
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}
	

	/**
	 * @return the mPeer
	 */
	public MLPeer getmPeer() {
		return mPeer;
	}

	/**
	 * @param mPeer the mPeer to set
	 */
	public void setmPeer(MLPeer mPeer) {
		this.mPeer = mPeer;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MLSolution [solutionId=");
		builder.append(solutionId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", ownerId=");
		builder.append(ownerId);
		builder.append(", ownerName=");
		builder.append(ownerName);
		builder.append(", metadata=");
		builder.append(metadata);
		builder.append(", active=");
		builder.append(active);
		builder.append(", created=");
		builder.append(created);
		builder.append(", modified=");
		builder.append(modified);
		builder.append(", tookitType=");
		builder.append(tookitType);
		builder.append(", revisions=");
		builder.append(revisions);
		builder.append(", loginName=");
		builder.append(loginName);
		builder.append(", pageNo=");
		builder.append(pageNo);
		builder.append(", size=");
		builder.append(size);
		builder.append(", sortingOrder=");
		builder.append(sortingOrder);
		builder.append(", modelType=");
		builder.append(modelType);
		builder.append(", mPeer=");
		builder.append(mPeer.toString());
		builder.append("]");
		return builder.toString();
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public int getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}

	public int getSolutionRating() {
		return solutionRating;
	}

	public void setSolutionRating(int solutionRating) {
		this.solutionRating = solutionRating;
	}

	public String getSolutionTag() {
		return solutionTag;
	}

	public void setSolutionTag(String solutionTag) {
		this.solutionTag = solutionTag;
	}

	public List<MLPTag> getSolutionTagList() {
		return solutionTagList;
	}

	public void setSolutionTagList(List<MLPTag> solutionTagList) {
		this.solutionTagList = solutionTagList;
	}

	public String getTookitTypeName() {
		return tookitTypeName;
	}

	public void setTookitTypeName(String tookitTypeName) {
		this.tookitTypeName = tookitTypeName;
	}

	public String getModelTypeName() {
		return modelTypeName;
	}

	public void setModelTypeName(String modelTypeName) {
		this.modelTypeName = modelTypeName;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public int getRatingAverageTenths() {
		return ratingAverageTenths;
	}

	public void setRatingAverageTenths(int ratingAverageTenths) {
		this.ratingAverageTenths = ratingAverageTenths;
	}

	public int getRatingCount() {
		return ratingCount;
	}

	public void setRatingCount(int ratingCount) {
		this.ratingCount = ratingCount;
	}

	public int getPrivateModelCount() {
		return PrivateModelCount;
	}

	public void setPrivateModelCount(int privateModelCount) {
		PrivateModelCount = privateModelCount;
	}

	public int getPublicModelCount() {
		return PublicModelCount;
	}

	public void setPublicModelCount(int publicModelCount) {
		PublicModelCount = publicModelCount;
	}

	public int getCompanyModelCount() {
		return CompanyModelCount;
	}

	public void setCompanyModelCount(int companyModelCount) {
		CompanyModelCount = companyModelCount;
	}

	public int getDeletedModelCount() {
		return DeletedModelCount;
	}

	public void setDeletedModelCount(int deletedModelCount) {
		DeletedModelCount = deletedModelCount;
	}

	public float getSolutionRatingAvg() {
		return solutionRatingAvg;
	}

	public void setSolutionRatingAvg(float solutionRatingAvg) {
		this.solutionRatingAvg = solutionRatingAvg;
	}

	public List<User> getOwnerListForSol() {
		return ownerListForSol;
	}

	public void setOwnerListForSol(List<User> ownerListForSol) {
		this.ownerListForSol = ownerListForSol;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public List<MLPThread> getThreadList() {
		return threadList;
	}

	public void setThreadList(List<MLPThread> threadList) {
		this.threadList = threadList;
	}

	public String getValidationStatusCode() {
		return validationStatusCode;
	}

	public void setValidationStatusCode(String validationStatusCode) {
		this.validationStatusCode = validationStatusCode;
	}

	public Long getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(Long refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}
	public String getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(String errorDetails) {
		this.errorDetails = errorDetails;
	}

	/**
	 * @return the latestRevisionId
	 */
	public String getLatestRevisionId() {
		return latestRevisionId;
	}

	/**
	 * @param latestRevisionId the latestRevisionId to set
	 */
	public void setLatestRevisionId(String latestRevisionId) {
		this.latestRevisionId = latestRevisionId;
	}
	public long getCommentsCount() {
       	     return commentsCount;
    	}

        public void setCommentsCount(long commentsCount) {
            this.commentsCount = commentsCount;
        }

		/**
		 * @return the publisher
		 */
		public String getPublisher() {
			return publisher;
		}

		/**
		 * @param publisher the publisher to set
		 */
		public void setPublisher(String publisher) {
			this.publisher = publisher;
		}

		/**
		 * @return the pendingApproval
		 */
		public boolean isPendingApproval() {
			return pendingApproval;
		}

		/**
		 * @param pendingApproval the pendingApproval to set
		 */
		public void setPendingApproval(boolean pendingApproval) {
			this.pendingApproval = pendingApproval;
		}

		public Instant getLastDownload() {
			return lastDownload;
		}

		public void setLastDownload(Instant lastDownload) {
			this.lastDownload = lastDownload;
		}

		public boolean isFeatured() {
			return featured;
		}

		public void setFeatured(boolean featured) {
			this.featured = featured;
		}

		public String getCatalogId() {
			return catalogId;
		}

		public void setCatalogId(String catalogId) {
			this.catalogId = catalogId;
		}

}
