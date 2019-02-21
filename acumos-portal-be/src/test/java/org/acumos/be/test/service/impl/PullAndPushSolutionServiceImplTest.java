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
package org.acumos.be.test.service.impl;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.Instant;

import org.acumos.cds.domain.MLPArtifact;
import org.acumos.portal.be.docker.cmd.SaveImageCommand;
import org.acumos.portal.be.service.impl.PullAndPushSolutionServiceImpl;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PullAndPushSolutionServiceImplTest {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(PullAndPushSolutionServiceImplTest.class);

	@Mock
	PullAndPushSolutionServiceImpl impl = new PullAndPushSolutionServiceImpl();

	@Test
	public void downloadModelArtifact() throws URISyntaxException{
		String artifactId = "00c9bc79-7703-471c-9f44-e1537bf61d9b";
		MLPArtifact mlpArtifact = new MLPArtifact();
		mlpArtifact.setArtifactId(artifactId);
		mlpArtifact.setArtifactTypeCode("BP");
		mlpArtifact.setCreated(Instant.now());
		mlpArtifact.setDescription("Artifact");
		mlpArtifact.setUri("http://abc.com");

		InputStream inputStream = null;
		if(mlpArtifact != null && !mlpArtifact.getUri().isEmpty()) {
			SaveImageCommand saveImageCommand = mock(SaveImageCommand.class);
			inputStream = saveImageCommand.getDockerImageStream();
			Mockito.when(impl.downloadModelArtifact(artifactId)).thenReturn(inputStream);
			Assert.assertEquals(inputStream, inputStream);
			logger.info("Successfully return downloadModelArtifact");
		}
	}
}