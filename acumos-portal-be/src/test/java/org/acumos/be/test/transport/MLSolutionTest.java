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
package org.acumos.be.test.transport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPThread;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.User;
import org.junit.Assert;
import org.junit.Test;

public class MLSolutionTest {

	@Test	
	public void testMLSolutionParameter(){
		String solutionId = "02a87750-7ba3-4ea7-8c20-c1286930f57c";
		String name = "Test1";
		String description = "ImageMoodClassifier";
		String ownerId = "83d5259f-48b7-4fe1-9fd6-d1166f8f3691";
		String ownerName = "abc";
		String metadata = "sffsf";
		boolean active = true;
		String accessType = "PR";
		Date created = new Date();
		Date modified = new Date();
		String tookitType = "SK";
		String tookitTypeName = "sfs";
		List<MLPSolutionRevision> revisions = new ArrayList<MLPSolutionRevision>();
		String loginName = "abcd";
		int pageNo = 9;
		int size = 1;
		String sortingOrder = "desc";
		String modelTypeName = "sfsf";
		int downloadCount = 23;
		int solutionRating = 4;
		String solutionTag = "fsf";
		List<MLPTag> solutionTagList = new ArrayList<MLPTag>();
		int viewCount = 25;
		int ratingAverageTenths = 20;
		int ratingCount = 45;
	    int privateModelCount =33;
		int publicModelCount =22;
		 int companyModelCount=11;
		 int deletedModelCount=4;
		 List<User> ownerListForSol=new ArrayList<User>();
		 String threadId= "th1001";
		 String commentId="cm2001";
		 List<MLPThread> threadList = new ArrayList<MLPThread>();
		 int solutionRatingAvg=3;
				
		MLPSolutionRevision mlPSolutionRevision = new MLPSolutionRevision();
		mlPSolutionRevision.setCreated(new Date());
		mlPSolutionRevision.setDescription("fafaf");
		mlPSolutionRevision.setMetadata("sfsfs");
		mlPSolutionRevision.setModified(new Date());
		mlPSolutionRevision.setOwnerId("83d5259f-48b7-4fe1-9fd6-d1166f8f3691");
		mlPSolutionRevision.setRevisionId("fsf24");
		mlPSolutionRevision.setSolutionId("02a87750-7ba3-4ea7-8c20-c1286930f57c");
		mlPSolutionRevision.setVersion("1.2");
		MLPTag mlPTag =new MLPTag();
		mlPTag.setTag("faf");
		solutionTagList.add(mlPTag);
		revisions.add(mlPSolutionRevision);

		MLSolution mlRole = new  MLSolution();
		mlRole.setAccessType(accessType);
		mlRole.setActive(active);
		mlRole.setCreated(created);
		mlRole.setDescription(description);
		mlRole.setDownloadCount(downloadCount);
		mlRole.setLoginName(loginName);
		mlRole.setMetadata(metadata);
		mlRole.setModelType(modelTypeName);
		mlRole.setModelTypeName(modelTypeName);
		mlRole.setModified(modified);
		mlRole.setName(name);
		mlRole.setOwnerId(ownerId);
		mlRole.setOwnerName(ownerName);
		mlRole.setPageNo(pageNo);
		mlRole.setRatingAverageTenths(ratingAverageTenths);
		mlRole.setRatingCount(ratingCount);
		mlRole.setRevisions(revisions);
		mlRole.setSize(size);
		mlRole.setSolutionId(solutionId);
		mlRole.setSolutionRating(solutionRating);
		mlRole.setSolutionTag(solutionTag);
		mlRole.setSolutionTagList(solutionTagList);
		mlRole.setSortingOrder(sortingOrder);
		mlRole.setTookitType(tookitType);
		mlRole.setTookitTypeName(tookitTypeName);
		mlRole.setViewCount(viewCount);
		mlRole.setPrivateModelCount(privateModelCount);
		mlRole.setPublicModelCount(publicModelCount);
		mlRole.setCommentId(commentId);
		mlRole.setCompanyModelCount(companyModelCount);
		mlRole.setThreadId(threadId);
		mlRole.setDeletedModelCount(deletedModelCount);
		mlRole.setOwnerListForSol(ownerListForSol);
		mlRole.setThreadList(threadList);
		mlRole.setSolutionRatingAvg(solutionRatingAvg);
		
		Assert.assertEquals(privateModelCount, mlRole.getPrivateModelCount());
		Assert.assertEquals(publicModelCount, mlRole.getPublicModelCount());
		Assert.assertEquals(companyModelCount, mlRole.getCompanyModelCount());
		Assert.assertEquals(deletedModelCount, mlRole.getDeletedModelCount());
		Assert.assertEquals(solutionRatingAvg, mlRole.getSolutionRatingAvg());
		Assert.assertEquals(threadId, mlRole.getThreadId());
		Assert.assertEquals(commentId, mlRole.getCommentId());
		Assert.assertNotNull(mlRole.getOwnerListForSol());
		Assert.assertNotNull(mlRole.getThreadList());
		
		Assert.assertEquals(accessType, mlRole.getAccessType());
		Assert.assertEquals(created, mlRole.getCreated());
		Assert.assertEquals(modified, mlRole.getModified());
		Assert.assertEquals(description, mlRole.getDescription());
		Assert.assertEquals(downloadCount, mlRole.getDownloadCount());
		Assert.assertEquals(loginName, mlRole.getLoginName());
		Assert.assertEquals(metadata, mlRole.getMetadata());		
		Assert.assertEquals(modelTypeName, mlRole.getModelType());
		Assert.assertEquals(modelTypeName, mlRole.getModelTypeName());
		Assert.assertEquals(name, mlRole.getName());
		Assert.assertEquals(ownerId, mlRole.getOwnerId());
		Assert.assertEquals(ownerName, mlRole.getOwnerName());
		Assert.assertEquals(pageNo, mlRole.getPageNo());
		Assert.assertEquals(ratingAverageTenths, mlRole.getRatingAverageTenths());		
		Assert.assertEquals(ratingCount, mlRole.getRatingCount());
		Assert.assertEquals(revisions, mlRole.getRevisions());
		Assert.assertEquals(size, mlRole.getSize());
		Assert.assertEquals(solutionId, mlRole.getSolutionId());
		Assert.assertEquals(solutionRating, mlRole.getSolutionRating());
		Assert.assertEquals(solutionTag, mlRole.getSolutionTag());
		Assert.assertEquals(solutionTagList, mlRole.getSolutionTagList());		
		Assert.assertEquals(sortingOrder, mlRole.getSortingOrder());
		Assert.assertEquals(tookitType, mlRole.getTookitType());
		Assert.assertEquals(viewCount, mlRole.getViewCount());
		Assert.assertEquals(tookitTypeName, mlRole.getTookitTypeName());
		Assert.assertEquals(active, mlRole.isActive());
	}
}
