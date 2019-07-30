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
import java.util.List;

import org.acumos.portal.be.transport.DesignStudioBlock;
import org.acumos.portal.be.transport.DesignStudioMenu;
import org.junit.Assert;
import org.junit.Test;

public class DesignStudioMenuTest  {

	@Test	
	public void testDesignStudioBlockParameters() {
		boolean active = true;
		String title = "New block";
		String description = "Description";
		String url = "https://test.url.org";
		String imagePath = "/path/to/image.png";
		
		DesignStudioBlock block = new DesignStudioBlock();
		block.setActive(active);
		block.setTitle(title);
		block.setDescription(description);
		block.setUrl(url);
		block.setImagePath(imagePath);
		
		Assert.assertEquals(active, block.isActive());
		Assert.assertEquals(title, block.getTitle());
		Assert.assertEquals(description, block.getDescription());
		Assert.assertEquals(url, block.getUrl());
		Assert.assertEquals(imagePath, block.getImagePath());
	}
	
	@Test	
	public void testDesignStudioMenuParameters() {
		boolean active = true;
		String title = "New block";
		String description = "Description";
		String url = "https://test.url.org";
		String imagePath = "/path/to/image.png";
		
		DesignStudioBlock block = new DesignStudioBlock();
		block.setActive(active);
		block.setTitle(title);
		block.setDescription(description);
		block.setUrl(url);
		block.setImagePath(imagePath);
		
		List<DesignStudioBlock> blocks = new ArrayList<>();
		blocks.add(block);
		
		boolean isWorkbenchActive = false;
		boolean isAcucomposeActive = true;
		
		DesignStudioMenu menu = new DesignStudioMenu();
		menu.setWorkbenchActive(isWorkbenchActive);
		menu.setAcucomposeActive(isAcucomposeActive);
		menu.setBlocks(blocks);
		
		Assert.assertEquals(isWorkbenchActive, menu.isWorkbenchActive());
		Assert.assertEquals(isAcucomposeActive, menu.isAcucomposeActive());
		Assert.assertEquals(blocks, menu.getBlocks());
	}
}
