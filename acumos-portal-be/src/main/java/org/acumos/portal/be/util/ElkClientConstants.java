/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

public final class ElkClientConstants {

	public static final String APPLICATION_JSON = "application/json";
	public static final String ELASTICSEARCH_GET_ALL_SNAPSHOT = "/all/snapshot";

	public static final String GET_ALL_INDICES = "/all/indices";
	public static final String DELETE_INDICES = "/delete/indices";
	public static final String GET_ALL_REPOSITORIES = "/all/repositories";
	public static final String SNAPSHOT_CREATE_REPOSITORY = "/create/repositories";
	public static final String SNAPSHOT_DELETE_REPOSITORY_REQUEST = "/delete/repositories";

	public static final String GET_ALL_SNAPSHOTS = "/all/snapshot";
	public static final String CREATE_SNAPSHOT_REQUEST = "/create/snapshot";
	public static final String DELETE_SNAPSHOT_REQUEST = "/delete/snapshot";
	public static final String RESTORE_SNAPSHOT_REQUEST = "/restore/snapshot";
	
	public static final String GET_ARCHIVE = "/all/archive";
	public static final String ARCHIVE_ACTION = "/archive/action";

	public static final String TRUE = "true";
	public static final String TIME_ONE_MINT_OUT = "1m";
	public static final String TIME_TWO_MINT_OUT = "2m";
	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
	
	public static final String  NODE_TIMEOUT="1";
	public static final String NODE_TIMEOUT_WITH_UNIT = "1m";
	
}
