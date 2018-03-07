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

package org.acumos.portal.be.config;

public class HttpClientConfigurationBuilder
//<A extends HttpClientConfigurationBuilder<A,T>,
//T extends HttpClientConfiguration>
{

	private /* T */HttpClientConfiguration config = newConfig();

	protected /* A */HttpClientConfigurationBuilder builder() {
		return this;
	}

	protected /* T */HttpClientConfiguration newConfig() {
		return new HttpClientConfiguration();
	}

	public /* T */HttpClientConfiguration buildConfig() {
		return this.config;
	}

	public HttpClientConfigurationBuilder/* A */ withSSL(HttpClientConfiguration.SSL theSSL) {
		this.config.setSSL(theSSL);
		return builder();
	}

	/** */
	public static class SSLBuilder {

		private HttpClientConfiguration.SSL ssl = new HttpClientConfiguration.SSL();

		public SSLBuilder withKeyStore(String theKeyStore) {
			this.ssl.setKeyStore(theKeyStore);
			return this;
		}

		public SSLBuilder withKeyStoreType(String theKeyStoreType) {
			this.ssl.setKeyStoreType(theKeyStoreType);
			return this;
		}

		public SSLBuilder withKeyStorePassword(String theKeyStorePassword) {
			this.ssl.setKeyStorePassword(theKeyStorePassword);
			return this;
		}

		public SSLBuilder withKeyAlias(String theKeyAlias) {
			this.ssl.setKeyAlias(theKeyAlias);
			return this;
		}

		public SSLBuilder withTrustStore(String theTrustStore) {
			this.ssl.setTrustStore(theTrustStore);
			return this;
		}

		public SSLBuilder withTrustStoreType(String theTrustStoreType) {
			this.ssl.setTrustStoreType(theTrustStoreType);
			return this;
		}

		public SSLBuilder withTrustStorePassword(String theTrustStorePassword) {
			this.ssl.setTrustStorePassword(theTrustStorePassword);
			return this;
		}

		public HttpClientConfiguration.SSL build() {
			return this.ssl;
		}
	}
}
