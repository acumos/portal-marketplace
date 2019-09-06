package org.acumos.be.test.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.controller.ElkController;
import org.acumos.portal.be.service.ElkService;
import org.acumos.portal.be.service.impl.ElkServiceImpl;
import org.acumos.portal.be.transport.CreateSnapshot;
import org.acumos.portal.be.transport.DeleteSnapshot;
import org.acumos.portal.be.transport.ELkRepositoryMetaData;
import org.acumos.portal.be.transport.ElasticStackIndiceResponse;
import org.acumos.portal.be.transport.ElasticStackIndices;
import org.acumos.portal.be.transport.ElasticsearchGetSnapshotsResponse;
import org.acumos.portal.be.transport.ElasticsearchSnapshotsResponse;
import org.acumos.portal.be.transport.ElkArchive;
import org.acumos.portal.be.transport.ElkArchiveInfo;
import org.acumos.portal.be.transport.ElkArchiveResponse;
import org.acumos.portal.be.transport.ElkCreateSnapshotRequest;
import org.acumos.portal.be.transport.ElkDeleteSnapshotRequest;
import org.acumos.portal.be.transport.ElkGetRepositoriesResponse;
import org.acumos.portal.be.transport.ElkGetSnapshotMetaData;
import org.acumos.portal.be.transport.ElkGetSnapshotsResponse;
import org.acumos.portal.be.transport.ElkRepositoriesRequest;
import org.acumos.portal.be.transport.ElkRepositoriesResponse;
import org.acumos.portal.be.transport.ElkRestoreSnapshotRequest;
import org.acumos.portal.be.transport.ElkSnapshotsResponse;
import org.acumos.portal.be.transport.RestoreSnapshot;
import org.acumos.portal.be.transport.Setting;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class ElkControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	ElkController elkController;
	@Mock
	ElkService elkService;
	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	@Test
	public void createRepositoryTest() {
		ElkRepositoriesRequest elkRepositoriesRequest=new ElkRepositoriesRequest();
		elkRepositoriesRequest.setNodeTimeout("1");
		elkRepositoriesRequest.setRepositoryName("MyRepo");
		JsonRequest<ElkRepositoriesRequest> jsonRequest=new JsonRequest<>();
		jsonRequest.setBody(elkRepositoriesRequest);
		ElkRepositoriesResponse elkRepositoriesResponse = new ElkRepositoriesResponse(true);
		JsonResponse<ElkRepositoriesResponse> jsonResponse=new JsonResponse<>();
		jsonResponse.setResponseBody(elkRepositoriesResponse);
		
		when(elkService.createRepository(elkRepositoriesRequest)).thenReturn(elkRepositoriesResponse);
		JsonResponse<ElkRepositoriesResponse> jsonResponseSuccess=elkController.createRepository(request, jsonRequest, response);
		assertNotNull(jsonResponseSuccess);
		assertEquals(jsonResponse.getResponseBody(), jsonResponseSuccess.getResponseBody());
		jsonResponse.setResponseBody(null);
		when(elkService.createRepository(elkRepositoriesRequest)).thenReturn(null);
		JsonResponse<ElkRepositoriesResponse> jsonResponseFail=elkController.createRepository(request, jsonRequest, response);
		assertEquals(jsonResponse.getResponseBody(), jsonResponseFail.getResponseBody());
		assertNull(jsonResponseFail.getResponseBody());
	}
	
	@Test
	public void getAllRepositoriesTest() {
		Setting setting=new Setting();
		setting.setCompress("true");
		setting.setLocation("test101");
		ELkRepositoryMetaData elkRepositoryMetaData=new ELkRepositoryMetaData();
		elkRepositoryMetaData.setName("test101");
		elkRepositoryMetaData.setType("fs");
		elkRepositoryMetaData.setSettings(setting);
		ElkGetRepositoriesResponse elkGetRepositoriesResponse=new ElkGetRepositoriesResponse();
		List<ELkRepositoryMetaData> eLkRepositoryMetaDataList=new ArrayList<>();
		eLkRepositoryMetaDataList.add(elkRepositoryMetaData);
		elkGetRepositoriesResponse.setRepositories(eLkRepositoryMetaDataList);
		JsonResponse<ElkGetRepositoriesResponse> jsonResponse=new JsonResponse();
		jsonResponse.setResponseBody(elkGetRepositoriesResponse);
		
		when(elkService.getAllRepositories()).thenReturn(elkGetRepositoriesResponse);
		JsonResponse<ElkGetRepositoriesResponse> jsonResponseSuccess=elkController.getAllRepositories(request, response);
		assertNotNull(jsonResponseSuccess);
		assertEquals(jsonResponse.getResponseBody(), jsonResponseSuccess.getResponseBody());
		jsonResponse.setResponseBody(null);
		when(elkService.getAllRepositories()).thenReturn(null);
		JsonResponse<ElkGetRepositoriesResponse> jsonResponseFail=elkController.getAllRepositories(request, response);
		assertNull(jsonResponseFail.getResponseBody());
		assertEquals(jsonResponse.getResponseBody(), jsonResponseFail.getResponseBody());
	}
	
	@Test
	public void deleteRepositoryTest() {
		ElkRepositoriesRequest elkRepositoriesRequest=new ElkRepositoriesRequest();
		elkRepositoriesRequest.setNodeTimeout("1");
		elkRepositoriesRequest.setRepositoryName("MyRepo");
		JsonRequest<ElkRepositoriesRequest> jsonRequest=new JsonRequest<>();
		jsonRequest.setBody(elkRepositoriesRequest);
		ElkRepositoriesResponse elkRepositoriesResponse = new ElkRepositoriesResponse(true);
		JsonResponse<ElkRepositoriesResponse> jsonResponse=new JsonResponse<>();
		jsonResponse.setResponseBody(elkRepositoriesResponse);
		
		when(elkService.deleteRepository(elkRepositoriesRequest)).thenReturn(elkRepositoriesResponse);
		JsonResponse<ElkRepositoriesResponse> jsonResponseSuccess=elkController.deleteRepository(request, jsonRequest, response);
		assertNotNull(jsonResponseSuccess);
		assertEquals(jsonResponse.getResponseBody(), jsonResponseSuccess.getResponseBody());
		jsonResponse.setResponseBody(null);
		when(elkService.deleteRepository(elkRepositoriesRequest)).thenReturn(null);
		JsonResponse<ElkRepositoriesResponse> jsonResponseFail=elkController.deleteRepository(request, jsonRequest, response);
		assertEquals(jsonResponse.getResponseBody(), jsonResponseFail.getResponseBody());
		assertNull(jsonResponseFail.getResponseBody());
	}
	
	@Test
	public void createSnapshotsTest() {
		CreateSnapshot createSnapshot=new CreateSnapshot();
		List<String> indicesList=new ArrayList<>();
		indicesList.add("testIndices");
		createSnapshot.setIndices(indicesList);
		createSnapshot.setRepositoryName("MyRepo");
		List<CreateSnapshot> createSnapshots=new ArrayList<>();
		createSnapshots.add(createSnapshot);
		JsonRequest<ElkCreateSnapshotRequest> requestJson=new JsonRequest<>();
		ElkCreateSnapshotRequest elkCreateSnapshotRequest=new ElkCreateSnapshotRequest();
		elkCreateSnapshotRequest.setCreateSnapshots(createSnapshots);
		elkCreateSnapshotRequest.setNodeTimeout("1");
		requestJson.setBody(elkCreateSnapshotRequest);
		JsonResponse<ElkSnapshotsResponse> jsonResponse=new JsonResponse<>();
		ElkGetSnapshotMetaData elkGetSnapshotMetaData=new ElkGetSnapshotMetaData();
		elkGetSnapshotMetaData.setEndTime("2019-03-28 08-53-41");
		elkGetSnapshotMetaData.setStartTime("2019-03-28 08-53-41");
		elkGetSnapshotMetaData.setSnapShotId("123");
		elkGetSnapshotMetaData.setState("testSate");
		elkGetSnapshotMetaData.setStatus("SUCCESS");
		List<String> list=new ArrayList<>();
		list.add("test");
		elkGetSnapshotMetaData.setIndices(list);
		ElasticsearchSnapshotsResponse elasticsearchSnapshotsResponse=new ElasticsearchSnapshotsResponse();
		List<ElkGetSnapshotMetaData> elkGetSnapshotMetaDataList=new ArrayList<>();
		elkGetSnapshotMetaDataList.add(elkGetSnapshotMetaData);
		elasticsearchSnapshotsResponse.setRepositoryName("MyRepo");
		elasticsearchSnapshotsResponse.setSnapshots(elkGetSnapshotMetaDataList);
		List<ElasticsearchSnapshotsResponse> elasticsearchSnapshots=new ArrayList<>();
		elasticsearchSnapshots.add(elasticsearchSnapshotsResponse);
		ElkSnapshotsResponse elkSnapshotsResponse=new ElkSnapshotsResponse();
		elkSnapshotsResponse.setElasticsearchSnapshots(elasticsearchSnapshots);
		jsonResponse.setResponseBody(elkSnapshotsResponse);
		
		when(elkService.createSnapshots(elkCreateSnapshotRequest)).thenReturn(elkSnapshotsResponse);
		JsonResponse<ElkSnapshotsResponse> jsonResponseSuccess=elkController.createSnapshots(request, requestJson, response);
		assertNotNull(jsonResponseSuccess);
		assertEquals(jsonResponse.getResponseBody(), jsonResponseSuccess.getResponseBody());
		jsonResponse.setResponseBody(null);;
		when(elkService.createSnapshots(elkCreateSnapshotRequest)).thenReturn(null);
		JsonResponse<ElkSnapshotsResponse> jsonResponseFail=elkController.createSnapshots(request, requestJson, response);
		assertEquals(jsonResponse.getResponseBody(), jsonResponseFail.getResponseBody());
		assertNull(jsonResponseFail.getResponseBody());
	}
	
	@Test
	public void getAllSnapshotsTest() {
		ElkGetSnapshotMetaData elkGetSnapshotMetaData=new ElkGetSnapshotMetaData();
		elkGetSnapshotMetaData.setEndTime("2019-03-28 08-53-41");
		elkGetSnapshotMetaData.setStartTime("2019-03-28 08-53-41");
		elkGetSnapshotMetaData.setSnapShotId("123");
		elkGetSnapshotMetaData.setState("testSate");
		elkGetSnapshotMetaData.setStatus("SUCCESS");
		List<String> list=new ArrayList<>();
		list.add("test");
		elkGetSnapshotMetaData.setIndices(list);
		List<ElkGetSnapshotMetaData> snapshots= new ArrayList<>();
		snapshots.add(elkGetSnapshotMetaData);
		ElasticsearchGetSnapshotsResponse elasticsearchGetSnapshotsResponse=new ElasticsearchGetSnapshotsResponse();
		elasticsearchGetSnapshotsResponse.setRepositoryName("MyRepo");
		elasticsearchGetSnapshotsResponse.setSnapshots(snapshots);
		List<ElasticsearchGetSnapshotsResponse> elasticsearchSnapshots=new ArrayList<>();
		elasticsearchSnapshots.add(elasticsearchGetSnapshotsResponse);
		ElkGetSnapshotsResponse elkGetSnapshotsResponse=new ElkGetSnapshotsResponse();
		elkGetSnapshotsResponse.setElasticsearchSnapshots(elasticsearchSnapshots);
		JsonResponse<ElkGetSnapshotsResponse> jsonResponse=new JsonResponse<>();
		jsonResponse.setResponseBody(elkGetSnapshotsResponse);
		
		when(elkService.getAllSnapshots()).thenReturn(elkGetSnapshotsResponse);
		JsonResponse<ElkGetSnapshotsResponse> jsonResponseSuccess=elkController.getAllSnapshots(request, response);
		assertNotNull(jsonResponseSuccess);
		assertEquals(jsonResponse.getResponseBody(), jsonResponseSuccess.getResponseBody());
		jsonResponse.setResponseBody(null);
		when(elkService.getAllSnapshots()).thenReturn(null);
		JsonResponse<ElkGetSnapshotsResponse> jsonResponseFail=elkController.getAllSnapshots(request, response);
		assertNull(jsonResponseFail.getResponseBody());
		assertEquals(jsonResponse.getResponseBody(), jsonResponseFail.getResponseBody());
	}
	
	@Test
	public void deleteSnapshotsTest() {
		DeleteSnapshot deleteSnapshot=new DeleteSnapshot();
		deleteSnapshot.setRepositoryName("MyRepo");
		deleteSnapshot.setSnapShotId("123");
		List<DeleteSnapshot> deleteSnapshots=new ArrayList<>();
		deleteSnapshots.add(deleteSnapshot);
		ElkDeleteSnapshotRequest elkDeleteSnapshotRequest=new ElkDeleteSnapshotRequest();
		elkDeleteSnapshotRequest.setDeleteSnapshots(deleteSnapshots);
		elkDeleteSnapshotRequest.setNodeTimeout("1");
		JsonRequest<ElkDeleteSnapshotRequest> requestJson=new JsonRequest<>();
		requestJson.setBody(elkDeleteSnapshotRequest);
		JsonResponse<ElkSnapshotsResponse> jsonResponse=new JsonResponse<>();
		ElkGetSnapshotMetaData elkGetSnapshotMetaData=new ElkGetSnapshotMetaData();
		elkGetSnapshotMetaData.setEndTime("2019-03-28 08-53-41");
		elkGetSnapshotMetaData.setStartTime("2019-03-28 08-53-41");
		elkGetSnapshotMetaData.setSnapShotId("123");
		elkGetSnapshotMetaData.setState("testSate");
		elkGetSnapshotMetaData.setStatus("SUCCESS");
		List<String> list=new ArrayList<>();
		list.add("test");
		elkGetSnapshotMetaData.setIndices(list);
		ElasticsearchSnapshotsResponse elasticsearchSnapshotsResponse=new ElasticsearchSnapshotsResponse();
		List<ElkGetSnapshotMetaData> elkGetSnapshotMetaDataList=new ArrayList<>();
		elkGetSnapshotMetaDataList.add(elkGetSnapshotMetaData);
		elasticsearchSnapshotsResponse.setRepositoryName("MyRepo");
		elasticsearchSnapshotsResponse.setSnapshots(elkGetSnapshotMetaDataList);
		List<ElasticsearchSnapshotsResponse> elasticsearchSnapshots=new ArrayList<>();
		elasticsearchSnapshots.add(elasticsearchSnapshotsResponse);
		ElkSnapshotsResponse elkSnapshotsResponse=new ElkSnapshotsResponse();
		elkSnapshotsResponse.setElasticsearchSnapshots(elasticsearchSnapshots);
		jsonResponse.setResponseBody(elkSnapshotsResponse);
		
		when(elkService.deleteSnapshots(elkDeleteSnapshotRequest)).thenReturn(elkSnapshotsResponse);
		JsonResponse<ElkSnapshotsResponse> jsonResponseSuccess=elkController.deleteSnapshots(request, requestJson, response);
		assertNotNull(jsonResponseSuccess);
		assertEquals(jsonResponse.getResponseBody(), jsonResponseSuccess.getResponseBody());
		jsonResponse.setResponseBody(null);
		when(elkService.deleteSnapshots(elkDeleteSnapshotRequest)).thenReturn(null);
		JsonResponse<ElkSnapshotsResponse> jsonResponseFail=elkController.deleteSnapshots(request, requestJson, response);
		assertEquals(jsonResponse.getResponseBody(), jsonResponseFail.getResponseBody());
		assertNull(jsonResponseFail.getResponseBody());
	}
	
	@Test
	public void restoreSnapshotsTest() {
		RestoreSnapshot restoreSnapshot=new RestoreSnapshot();
		restoreSnapshot.setSnapshotName("tempSnapshot");
		List<RestoreSnapshot> restoreSnapshots=new ArrayList<>();
		restoreSnapshots.add(restoreSnapshot);
		ElkRestoreSnapshotRequest elkRestoreSnapshotRequest=new ElkRestoreSnapshotRequest();
		elkRestoreSnapshotRequest.setNodeTimeout("1");
		elkRestoreSnapshotRequest.setRepositoryName("MyRepo");
		elkRestoreSnapshotRequest.setRestoreSnapshots(restoreSnapshots);
		JsonRequest<ElkRestoreSnapshotRequest> jsonRequest=new JsonRequest<>();
		jsonRequest.setBody(elkRestoreSnapshotRequest);
		ElasticStackIndiceResponse elasticStackIndiceResponse=new ElasticStackIndiceResponse();
		elasticStackIndiceResponse.setMessage("Done");
		elasticStackIndiceResponse.setStatus("SUCCESS");
		JsonResponse<ElasticStackIndiceResponse> jsonResponse=new JsonResponse<>();
		jsonResponse.setResponseBody(elasticStackIndiceResponse);
		
		when(elkService.restoreSnapshots(elkRestoreSnapshotRequest)).thenReturn(elasticStackIndiceResponse);
		JsonResponse<ElasticStackIndiceResponse> jsonResponseSuccess=elkController.restoreSnapshots(request, jsonRequest, response);
		assertNotNull(jsonResponseSuccess);
		assertEquals(jsonResponse.getResponseBody(), jsonResponseSuccess.getResponseBody());
		jsonResponse.setResponseBody(null);
		when(elkService.restoreSnapshots(elkRestoreSnapshotRequest)).thenReturn(null);
		JsonResponse<ElasticStackIndiceResponse> jsonResponseFail=elkController.restoreSnapshots(request, jsonRequest, response);
		assertEquals(jsonResponse.getResponseBody(), jsonResponseFail.getResponseBody());
		assertNull(jsonResponseFail.getResponseBody());
	}
	
	@Test
	public void getIndicesTest() {
		ElasticStackIndices elasticStackIndices=new ElasticStackIndices();
		List<String> indices=new ArrayList<>();
		indices.add("MyIndices");
		elasticStackIndices.setIndices(indices);
		JsonResponse<ElasticStackIndices> jsonResponse=new JsonResponse<>();
		jsonResponse.setResponseBody(elasticStackIndices);
		
		when(elkService.getAllIndices()).thenReturn(elasticStackIndices);
		JsonResponse<ElasticStackIndices> jsonResponseSuccess=elkController.getIndices(request, response);
		assertNotNull(jsonResponseSuccess);
		assertEquals(jsonResponse.getResponseBody(), jsonResponseSuccess.getResponseBody());
		jsonResponse.setResponseBody(null);
		when(elkService.getAllIndices()).thenReturn(null);
		JsonResponse<ElasticStackIndices> jsonResponseFail=elkController.getIndices(request, response);
		assertNull(jsonResponseFail.getResponseBody());
		assertEquals(jsonResponse.getResponseBody(), jsonResponseFail.getResponseBody());
	}
	
	@Test
	public void deleteIndicesTest() {
		ElasticStackIndices elasticStackIndices=new ElasticStackIndices();
		List<String> indices=new ArrayList<>();
		indices.add("MyIndices");
		elasticStackIndices.setIndices(indices);
		JsonRequest<ElasticStackIndices> jsonRequest=new JsonRequest<>();
		jsonRequest.setBody(elasticStackIndices);
		ElasticStackIndiceResponse elasticStackIndiceResponse=new ElasticStackIndiceResponse();
		elasticStackIndiceResponse.setMessage("MyMessage");
		elasticStackIndiceResponse.setStatus("SUCCESS");
		JsonResponse<ElasticStackIndiceResponse> jsonResponse=new JsonResponse<>();
		jsonResponse.setResponseBody(elasticStackIndiceResponse);
		
		when(elkService.deleteIndices(elasticStackIndices)).thenReturn(elasticStackIndiceResponse);
		JsonResponse<ElasticStackIndiceResponse> jsonResponseSuccess=elkController.deleteIndices(request, jsonRequest, response);
		assertNotNull(jsonResponseSuccess);
		assertEquals(jsonResponse.getResponseBody(), jsonResponseSuccess.getResponseBody());
		jsonResponse.setResponseBody(null);
		when(elkService.deleteIndices(elasticStackIndices)).thenReturn(null);
		JsonResponse<ElasticStackIndiceResponse> jsonResponseFail=elkController.deleteIndices(request, jsonRequest, response);
		assertEquals(jsonResponse.getResponseBody(), jsonResponseFail.getResponseBody());
		assertNull(jsonResponseFail.getResponseBody());
	}
	@Test
	public void getArchiveTest() {
		ElkArchiveResponse elkArchiveResponse=new ElkArchiveResponse();
		List<ElkArchiveInfo> elkArchiveInfoList=new ArrayList<>();
		ElkArchiveInfo elkArchiveInfo=new ElkArchiveInfo();
		elkArchiveInfo.setRepositoryName("MyBackup");
		elkArchiveInfo.setDate("2019-08-20:14:24:28Z");
		ElkGetSnapshotMetaData elkGetSnapshotMetaData=new ElkGetSnapshotMetaData();
		elkGetSnapshotMetaData.setEndTime("2019-03-28 08-53-41");
		elkGetSnapshotMetaData.setStartTime("2019-03-28 08-53-41");
		elkGetSnapshotMetaData.setSnapShotId("123");
		elkGetSnapshotMetaData.setState("testSate");
		elkGetSnapshotMetaData.setStatus("SUCCESS");
		List<String> list=new ArrayList<>();
		list.add("test");
		elkGetSnapshotMetaData.setIndices(list);
		List<ElkGetSnapshotMetaData> snapshots= new ArrayList<>();
		snapshots.add(elkGetSnapshotMetaData);
		elkArchiveInfo.setSnapshots(snapshots);
		Assert.assertNotNull(elkArchiveInfo);
		elkArchiveInfoList.add(elkArchiveInfo);
		Assert.assertNotNull(elkArchiveInfoList);
		elkArchiveResponse.setArchiveInfo(elkArchiveInfoList);
		elkArchiveResponse.setMsg("Action:INFO done");
		elkArchiveResponse.setStatus("success");
		Assert.assertNotNull(elkArchiveResponse);
		JsonResponse<ElkArchiveResponse> elkResponse = new JsonResponse<>();
		elkResponse.setResponseBody(elkArchiveResponse);
		
		when(elkService.getAllArchive()).thenReturn(elkArchiveResponse);
		JsonResponse<ElkArchiveResponse> elkResponseSuccess=elkController.getArchive(request, response);
		assertNotNull(elkResponseSuccess);
		assertEquals(elkResponse.getResponseBody(), elkResponseSuccess.getResponseBody());
		elkResponse.setResponseBody(null);
		when(elkService.getAllArchive()).thenReturn(null);
		JsonResponse<ElkArchiveResponse> elkResponseFail=elkController.getArchive(request, response);
		assertEquals(elkResponse.getResponseBody(), elkResponseFail.getResponseBody());
		assertNull(elkResponseFail.getResponseBody());
	}
	
	@Test
	public void archiveActionTest() {
		ElkArchive elkArchive=new ElkArchive();
		elkArchive.setAction("archive");
		List<String> repositoryName=new ArrayList<>();
		repositoryName.add("my_backup");
		elkArchive.setRepositoryName(repositoryName);
		JsonRequest<ElkArchive> elkArchiveRequest = new JsonRequest<>();
		elkArchiveRequest.setBody(elkArchive);
		ElkArchiveResponse elkArchiveResponse=new ElkArchiveResponse();
		List<ElkArchiveInfo> elkArchiveInfoList=new ArrayList<>();
		ElkArchiveInfo elkArchiveInfo=new ElkArchiveInfo();
		elkArchiveInfo.setRepositoryName("MyBackup");
		elkArchiveInfo.setDate("2019-08-20:14:24:28Z");
		ElkGetSnapshotMetaData elkGetSnapshotMetaData=new ElkGetSnapshotMetaData();
		elkGetSnapshotMetaData.setEndTime("2019-03-28 08-53-41");
		elkGetSnapshotMetaData.setStartTime("2019-03-28 08-53-41");
		elkGetSnapshotMetaData.setSnapShotId("123");
		elkGetSnapshotMetaData.setState("testSate");
		elkGetSnapshotMetaData.setStatus("SUCCESS");
		List<String> list=new ArrayList<>();
		list.add("test");
		elkGetSnapshotMetaData.setIndices(list);
		List<ElkGetSnapshotMetaData> snapshots= new ArrayList<>();
		snapshots.add(elkGetSnapshotMetaData);
		elkArchiveInfo.setSnapshots(snapshots);
		elkArchiveInfoList.add(elkArchiveInfo);
		elkArchiveResponse.setArchiveInfo(elkArchiveInfoList);
		elkArchiveResponse.setMsg("Action:INFO done");
		elkArchiveResponse.setStatus("success");
		
		JsonResponse<ElkArchiveResponse> elkResponse = new JsonResponse<>();
		elkResponse.setResponseBody(elkArchiveResponse);
		
		when(elkService.archiveAction(elkArchive)).thenReturn(elkArchiveResponse);
		JsonResponse<ElkArchiveResponse> elkResponseSuccess=elkController.archiveAction(request, elkArchiveRequest, response);
		assertNotNull(elkResponseSuccess);
		assertEquals(elkResponse.getResponseBody(), elkResponseSuccess.getResponseBody());
		elkArchiveResponse.setArchiveInfo(null);
		when(elkService.archiveAction(elkArchive)).thenReturn(elkArchiveResponse);
		JsonResponse<ElkArchiveResponse>  elkResponseFail=elkController.archiveAction(request, elkArchiveRequest, response);
		assertEquals(elkResponse.getResponseBody(), elkResponseFail.getResponseBody());
		assertNull(elkResponseFail.getResponseBody().getArchiveInfo());
		
	}
}
