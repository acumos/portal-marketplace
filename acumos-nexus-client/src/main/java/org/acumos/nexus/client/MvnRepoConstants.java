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

package org.acumos.nexus.client;

/**
 * The Class MvnRepoConstants.
 */
public final class MvnRepoConstants {
	public static final String CONFIG_GROUP_NAME = "mvnrepo";
	public static final String CONFIG_URL = "url";
	public static final String CONFIG_USERNAME = "username";
	public static final String CONFIG_PASSWORD = "password";
	public static final String CONFIG_PROXY = "proxy";
	public static final String CONFIG_SYNCHRONIZATION_INTERVAL = "synchronizationInterval";
	public static final String CONFIG_ENABLE_AUDIT = "enableAudit";
	public static final String CONFIG_CONTEXT_DATA_FILE_PATTERN = "contextDataFilePattern";
	public static final String CONFIG_CONTEXT_DATA_XML_FILE_PATTERN = "contextDataXmlFilePattern";
	public static final String CONFIG_CONTEXT_DATA_TEMPLATE = "contextDataFile";
	public static final String CONFIG_XFORM_TEMPLATES_PATH = "xformTemplatesPath";
	public static final String PROXY_LOOP_COUNT = "proxyLoopCount";
	public static final String PROXY_WAIT_TIME = "proxyWaitTimeSeconds";
	
	public static final String CONFIG_CONTEXT_DATA_EVOLVE_FILE_PATTERN = "contextDataEvolveFilePattern";

	/**
	 * Should not be instantiated.
	 */
	private MvnRepoConstants() {
	}
}
