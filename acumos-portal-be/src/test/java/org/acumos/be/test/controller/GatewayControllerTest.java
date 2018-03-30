package org.acumos.be.test.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.portal.be.common.Clients;
import org.acumos.portal.be.common.GatewayClient;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.config.GatewayClientConfiguration;
import org.acumos.portal.be.controller.GatewayController;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class GatewayControllerTest {

	@InjectMocks
    GatewayController gatewayController;
	
	@Mock
	Clients clients;
	@Mock
	RestTemplate restTemplate;
	
	@Mock
	GatewayClient gateway;
	
	@Mock
	Environment env;
	
	@Mock
	GatewayClientConfiguration gatewayClientConfiguration;
	
	@Mock
	GatewayClient client;
	
	 private MockMvc mockMvc;
		
		@Before
		public void setUp() throws Exception {
			mockMvc = standaloneSetup(gatewayController).build();

		}
	
	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	@Test
	public void pingGateway() {
		
		JsonResponse<MLPPeer> peer = new JsonResponse<>();
		MLPPeer mlpPeer=new MLPPeer();
		mlpPeer.setPeerId("peerId");
		mlpPeer.setName("peerName");
		peer.setContent(mlpPeer);
		
		HttpClientBuilder clientBuilder = HttpClients.custom();
		when(env.getProperty("gateway.url")).thenReturn("http://abc.com");
		when(gatewayClientConfiguration.buildClient()).thenReturn(clientBuilder.build());
		GatewayClient client = new GatewayClient(env.getProperty("gateway.url"), gatewayClientConfiguration.buildClient());
		when(clients.getGatewayClient()).thenReturn(client);
		
		when(gateway.ping("peer123")).thenReturn(peer);
		gatewayController.pingGateway(request, "ggre34gsd", response);
	}
	
	@Test
	public void getSolutions() {
		
		JsonRequest<MLPPeerSubscription> peerSubscription = new JsonRequest<>();
		MLPPeerSubscription sub=new MLPPeerSubscription();
		sub.setSubId(1234L);
		sub.setPeerId("1ce7-41e8-a364-93f5b57deb14");
		sub.setOwnerId("1a8e8b73-1ce7-41e8-a364-93f5b57deb14");
		String selector="{modelTypeCode:CL,toolkitTypeCode:DS}";
		sub.setSelector(selector);
		peerSubscription.setBody(sub);
		
		List<MLPSolution> solList = new ArrayList<>();
		MLPSolution sol = new MLPSolution();
		sol.setSolutionId("4e1c2a84-c597-499b-a9be-3b5e563ec100");
		sol.setOwnerId("1a8e8b73-1ce7-41e8-a364-93f5b57deb14");
		sol.setName("Robot");
		solList.add(sol);
		JsonResponse<List<MLPSolution>> solutions = new JsonResponse<>();
		solutions.setResponseBody(solList);
		HttpClientBuilder clientBuilder = HttpClients.custom();
		when(env.getProperty("gateway.url")).thenReturn("http://abc.com");
		when(gatewayClientConfiguration.buildClient()).thenReturn(clientBuilder.build());
		GatewayClient client = new GatewayClient(env.getProperty("gateway.url"), gatewayClientConfiguration.buildClient());
		when(clients.getGatewayClient()).thenReturn(client);
	//	when(client.getSolutions("peerId", "jsonString")).thenReturn(solutions);
		gatewayController.getSolutions(request, peerSubscription, response);
		
	}
	
	@Test
	public void getSolution() {
		HttpClientBuilder clientBuilder = HttpClients.custom();
		when(env.getProperty("gateway.url")).thenReturn("http://abc.com");
		when(gatewayClientConfiguration.buildClient()).thenReturn(clientBuilder.build());
		GatewayClient client = new GatewayClient(env.getProperty("gateway.url"), gatewayClientConfiguration.buildClient());
		when(clients.getGatewayClient()).thenReturn(client);
		
		gatewayController.getSolution(request, "4e1c2a84-c597-499b-a9be-3b5e563ec100", "1ce7-41e8-a364-93f5b57deb14", response);
	}
}
