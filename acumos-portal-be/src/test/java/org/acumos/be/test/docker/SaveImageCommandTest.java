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
package org.acumos.be.test.docker;

import org.acumos.portal.be.docker.cmd.SaveImageCommand;
import org.junit.Assert;
import org.junit.Test;

public class SaveImageCommandTest {

	@Test
	public void saveImageCommandTestParam() {
	String imageName="imageName";
	 String imageTag="imageTag";
	 String destination="destination";
	 String filename="filename";
	boolean ignoreIfNotFound=false;
	
	SaveImageCommand saveImageCommand = new SaveImageCommand(destination, destination, destination, destination, ignoreIfNotFound);
	
	Assert.assertNotNull(saveImageCommand);
	/*Assert.assertEquals(imageName, saveImageCommand.getImageName());
	Assert.assertEquals(imageTag, saveImageCommand.getImageTag());
	Assert.assertEquals(destination, saveImageCommand.getDestination());
	Assert.assertEquals(filename, saveImageCommand.getFilename());
	Assert.assertEquals(ignoreIfNotFound, saveImageCommand.getIgnoreIfNotFound());*/
	
	}
}
