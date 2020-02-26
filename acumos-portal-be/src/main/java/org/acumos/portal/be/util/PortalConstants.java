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

package org.acumos.portal.be.util;

public class PortalConstants {

	public static final String CAROUSEL_CONFIG_KEY = "carousel_config";
	public static final String TAG_NAME = "tagName";
	public static final String PUBLISH_SELF_REQ_ENABLED_PROPERTY = "portal.feature.publishSelfRequestEnabled";
	public static final int DEFAULT_CATALOG_PAGE_SIZE = 1000;
    public static final String PUBLIC_CATALOG = "PB";
    public static final String LICENSE_FILENAME = "license.json"; 
    public static final String LICENSE_ARTIFACT_TYPE = "LI";
    public static final String LICENSE_FILENAME_PREFIX = "license";
    public static final String LICENSE_EXT = ".json"; 
    public static final String K8CLUSTER_CONFIG_KEY = "k8sCluster";
    public static final String DEPLOY_TO_K8="deploy/";
    public static final String ENV_LUM_URL="lum.url";
	public static final String ENV_NEXUS_URL="nexus.url";
	public static final String LOGIN_EXPIRE_PROPERTY_KEY = "portal.feature.loginExpire.duration";
	public static final long ONE_DAY_EXPIRY = 24;
	public static final String ADMIN_USER = "Admin";
	public static final String PUBLISHER_USER = "Publisher";
}
