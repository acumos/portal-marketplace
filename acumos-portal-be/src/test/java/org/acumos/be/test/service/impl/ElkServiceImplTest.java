package org.acumos.be.test.service.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.acumos.portal.be.service.impl.ElkServiceImpl;
import org.acumos.portal.be.transport.ElasticStackIndiceResponse;
import org.acumos.portal.be.transport.ElasticStackIndices;
import org.acumos.portal.be.transport.ElkArchive;
import org.acumos.portal.be.transport.ElkArchiveResponse;
import org.acumos.portal.be.transport.ElkCreateSnapshotRequest;
import org.acumos.portal.be.transport.ElkDeleteSnapshotRequest;
import org.acumos.portal.be.transport.ElkGetRepositoriesResponse;
import org.acumos.portal.be.transport.ElkGetSnapshotsResponse;
import org.acumos.portal.be.transport.ElkRepositoriesRequest;
import org.acumos.portal.be.transport.ElkRepositoriesResponse;
import org.acumos.portal.be.transport.ElkRestoreSnapshotRequest;
import org.acumos.portal.be.transport.ElkSnapshotsResponse;
import org.acumos.portal.be.util.ElkClientConstants;
import org.acumos.portal.be.util.URIUtil;
import org.apache.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class ElkServiceImplTest {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	@InjectMocks
	ElkServiceImpl elkServiceImpl;
	
	@Mock
	Environment env;
	@Mock
	URIUtil uriUtil;
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));
	public static final String LOCAL_HOST="http://localhost:8000/";
   	public static final String GET_ARCHIVE = "/all/archive";
   	public static final String ARCHIVE_ACTION=	"/archive/action";
   	public static final String SNAPSHOT_CREATE_REPOSITORY = ElkClientConstants.SNAPSHOT_CREATE_REPOSITORY;
    public static final String GET_ALL_REPOSITORIES = ElkClientConstants.GET_ALL_REPOSITORIES;
    public static final String SNAPSHOT_DELETE_REPOSITORY_REQUEST = ElkClientConstants.SNAPSHOT_DELETE_REPOSITORY_REQUEST;
    public static final String CREATE_SNAPSHOT_REQUEST = ElkClientConstants.CREATE_SNAPSHOT_REQUEST;
    public static final String GET_ALL_SNAPSHOTS = ElkClientConstants.GET_ALL_SNAPSHOTS;
    public static final String DELETE_SNAPSHOT_REQUEST = ElkClientConstants.DELETE_SNAPSHOT_REQUEST;
    public static final String RESTORE_SNAPSHOT_REQUEST = ElkClientConstants.RESTORE_SNAPSHOT_REQUEST;
	public static final String GET_ALL_INDICES = ElkClientConstants.GET_ALL_INDICES;
	public static final String DELETE_INDICES = ElkClientConstants.DELETE_INDICES;

	
   	@Test
    public void createRepositorytest() {
           ElkRepositoriesRequest request = new ElkRepositoriesRequest();
           request.setNodeTimeout("1");
           request.setRepositoryName("repotest");
           URI uri = null;
           try {
                  uri=new URI(LOCAL_HOST+SNAPSHOT_CREATE_REPOSITORY);
           } catch (URISyntaxException e) {
                  logger.error("Error occured while creating URI: "+e.getMessage());
           }
           when(uriUtil.buildUri(Mockito.any(), Mockito.any())).thenReturn(uri);
           stubFor(post(urlEqualTo(SNAPSHOT_CREATE_REPOSITORY)).willReturn(
                        aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("true")));
           ElkRepositoriesResponse elkRepositoriesResponse = elkServiceImpl.createRepository(request);
           assertNotNull(elkRepositoriesResponse.getResponse());
    }

    @Test
    public void getAllRepositoriestest() {
           URI uri = null;
           try {
                  uri=new URI(LOCAL_HOST+GET_ALL_REPOSITORIES);
           } catch (URISyntaxException e) {
                  logger.error("Error occured while creating URI: "+e.getMessage());
           }
           when(uriUtil.buildUri(Mockito.any(), Mockito.any())).thenReturn(uri);
           stubFor(get(urlEqualTo(GET_ALL_REPOSITORIES)).willReturn(
                        aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"repositories\":" + "[{\"name\": \"Test\"," + "\"type\": \"Test type\"}"
                                      + "]}")));
           ElkGetRepositoriesResponse elkGetRepositoriesResponse = elkServiceImpl.getAllRepositories();
           assertNotNull(elkGetRepositoriesResponse);
           assertNotNull(elkGetRepositoriesResponse.getRepositories());
    }

    @Test
    public void getdeleteRepositorytest() {
           ElkRepositoriesRequest request = new ElkRepositoriesRequest();
           request.setNodeTimeout("1");
           request.setRepositoryName("repotest");
           URI uri = null;
           try {
                  uri=new URI(LOCAL_HOST+SNAPSHOT_DELETE_REPOSITORY_REQUEST);
           } catch (URISyntaxException e) {
                  logger.error("Error occured while creating URI: "+e.getMessage());
           }
           when(uriUtil.buildUri(Mockito.any(), Mockito.any())).thenReturn(uri);
           stubFor(post(urlEqualTo(SNAPSHOT_DELETE_REPOSITORY_REQUEST)).willReturn(
                        aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("true")));
           ElkRepositoriesResponse elkRepositoriesResponse = elkServiceImpl.deleteRepository(request);
           assertNotNull(elkRepositoriesResponse.getResponse());
    }


    @Test
    public void getElkSnapshotsResponsetest() {
           ElkCreateSnapshotRequest request= new ElkCreateSnapshotRequest();
           request.setNodeTimeout("1");
           URI uri = null;
           try {
                  uri=new URI(LOCAL_HOST+CREATE_SNAPSHOT_REQUEST);
           } catch (URISyntaxException e) {
                  logger.error("Error occured while creating URI: "+e.getMessage());
           }
           when(uriUtil.buildUri(Mockito.any(), Mockito.any())).thenReturn(uri);
           stubFor(post(urlEqualTo(CREATE_SNAPSHOT_REQUEST)).willReturn(
                        aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"elasticsearchSnapshots\":" + "[{\"repositoryName\": \"TestrepositoryName\"}"
                                      + "]}")));
           ElkSnapshotsResponse elkSnapshotsResponse = elkServiceImpl.createSnapshots(request);
           assertNotNull(elkSnapshotsResponse);
           assertNotNull(elkSnapshotsResponse.getElasticsearchSnapshots().get(0));
    }
    
    @Test
    public void getAllSnapshotstest() {
           URI uri = null;
           try {
                  uri=new URI(LOCAL_HOST+GET_ALL_SNAPSHOTS);
           } catch (URISyntaxException e) {
                  logger.error("Error occured while creating URI: "+e.getMessage());
           }
           when(uriUtil.buildUri(Mockito.any(), Mockito.any())).thenReturn(uri);
           stubFor(get(urlEqualTo(GET_ALL_SNAPSHOTS)).willReturn(
                        aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"elasticsearchSnapshots\":" + "[{\"repositoryName\": \"TestrepositoryName\"}"
                                      + "]}")));
           ElkGetSnapshotsResponse elkGetSnapshotsResponse = elkServiceImpl.getAllSnapshots();
           assertNotNull(elkGetSnapshotsResponse);
           assertNotNull(elkGetSnapshotsResponse.getElasticsearchSnapshots().get(0));
    }

    @Test
    public void deleteSnapshotstest() {
           ElkDeleteSnapshotRequest request= new ElkDeleteSnapshotRequest();
           request.setNodeTimeout("1");
           URI uri = null;
           try {
                  uri=new URI(LOCAL_HOST+DELETE_SNAPSHOT_REQUEST);
           } catch (URISyntaxException e) {
                  logger.error("Error occured while creating URI: "+e.getMessage());
           }
           when(uriUtil.buildUri(Mockito.any(), Mockito.any())).thenReturn(uri);
           stubFor(post(urlEqualTo(DELETE_SNAPSHOT_REQUEST)).willReturn(
                        aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"elasticsearchSnapshots\":" + "[{\"repositoryName\": \"TestrepositoryName\"}"
                                      + "]}")));
           ElkSnapshotsResponse elkSnapshotsResponse = elkServiceImpl.deleteSnapshots(request);
           assertNotNull(elkSnapshotsResponse);
           assertNotNull(elkSnapshotsResponse.getElasticsearchSnapshots().get(0));
    }

    @Test
    public void restoreSnapshotstest() {
           ElkRestoreSnapshotRequest request= new ElkRestoreSnapshotRequest();
           request.setNodeTimeout("1");
           request.setRepositoryName("Testrepo");
           URI uri = null;
           try {
                  uri=new URI(LOCAL_HOST+RESTORE_SNAPSHOT_REQUEST);
           } catch (URISyntaxException e) {
                  logger.error("Error occured while creating URI: "+e.getMessage());
           }
           when(uriUtil.buildUri(Mockito.any(), Mockito.any())).thenReturn(uri);
           stubFor(post(urlEqualTo(RESTORE_SNAPSHOT_REQUEST)).willReturn(
                        aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"message\": \"testMessage\"," + "\"status\": \"success\"}")));
           ElasticStackIndiceResponse elasticStackIndiceResponse = elkServiceImpl.restoreSnapshots(request);
           assertNotNull(elasticStackIndiceResponse);
           assertEquals(elasticStackIndiceResponse.getStatus(),"success");
    }      
    
    
    @Test
    public void getAllIndicestest() {
           URI uri = null;
           try {
                  uri=new URI(LOCAL_HOST+GET_ALL_INDICES);
           } catch (URISyntaxException e) {
                  logger.error("Error occured while creating URI: "+e.getMessage());
           }
           when(uriUtil.buildUri(Mockito.any(), Mockito.any())).thenReturn(uri);
           stubFor(get(urlEqualTo(GET_ALL_INDICES)).willReturn(
                        aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"indices\": [\"metricbeat-6.2.4-2019.06.24\",\"metricbeat-6.2.4-2019.08.29\"]}")));
           ElasticStackIndices elasticStackIndices = elkServiceImpl.getAllIndices();
           assertNotNull(elasticStackIndices);
    }      
    
    
    @Test
    public void deleteIndicestest() {
           ElasticStackIndices request = new ElasticStackIndices();
           List<String> indices = new ArrayList<String>();
           indices.add("test");
           indices.add("indice");
           request.setIndices(indices);
           URI uri = null;
           try {
                  uri=new URI(LOCAL_HOST+DELETE_INDICES);
           } catch (URISyntaxException e) {
                  logger.error("Error occured while creating URI: "+e.getMessage());
           }
           when(uriUtil.buildUri(Mockito.any(), Mockito.any())).thenReturn(uri);
           stubFor(post(urlEqualTo(DELETE_INDICES)).willReturn(
                        aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"message\": \"testMessage\"," + "\"status\": \"success\"}")));
           ElasticStackIndiceResponse elasticStackIndiceResponse = elkServiceImpl.deleteIndices(request);
           assertNotNull(elasticStackIndiceResponse);
           assertEquals(elasticStackIndiceResponse.getStatus(),"success");
    }

   	
	@Test
	public void getAllArchiveTest() {
		
		URI uri = null;
		try {
			uri=new URI(LOCAL_HOST+GET_ARCHIVE);
		} catch (URISyntaxException e) {
			logger.error("Error occured while creating URI: "+e.getMessage());
		}
		when(uriUtil.buildUri(Mockito.any(), Mockito.any())).thenReturn(uri);

		stubFor(get(urlEqualTo(GET_ARCHIVE)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"archiveInfo\":[" + "{\"date\": \"2019-08-20:14:24:28Z\","
				         	      + "\"repositoryName\": \"my_backup\" }],"
				          	      + "\"msg\": \"Action:INFO done\","
				          	      +  "\"status\": \"success\"}")));
		
		ElkArchiveResponse elkArchiveResponse=elkServiceImpl.getAllArchive();
		assertNotNull(elkArchiveResponse);
		assertEquals(elkArchiveResponse.getStatus(), "success");
		assertEquals(elkArchiveResponse.getMsg(), "Action:INFO done");
	}
	
	@Test
	public void archiveActionTest() {
		
		ElkArchive elkArchive=new ElkArchive();
		elkArchive.setAction("archive");
		List<String> repositoryName=new ArrayList<>();
		repositoryName.add("my_backup");
		elkArchive.setRepositoryName(repositoryName);
		URI uri = null;
		try {
			uri=new URI(LOCAL_HOST+ARCHIVE_ACTION);
		} catch (URISyntaxException e) {
			logger.error("Error occured while creating URI: "+e.getMessage());
		}
		when(uriUtil.buildUri(Mockito.any(), Mockito.any())).thenReturn(uri);

		stubFor(post(urlEqualTo(ARCHIVE_ACTION)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"archiveInfo\":[" + "{\"date\": \"2019-08-20:14:24:28Z\","
				         	      + "\"repositoryName\": \"my_backup\" }],"
				          	      + "\"msg\": \"Action:INFO done\","
				          	      +  "\"status\": \"success\"}")));
		
		ElkArchiveResponse elkArchiveResponse=elkServiceImpl.archiveAction(elkArchive);
		assertNotNull(elkArchiveResponse);
		assertEquals(elkArchiveResponse.getStatus(), "success");
		assertEquals(elkArchiveResponse.getMsg(), "Action:INFO done");
	}
	
}