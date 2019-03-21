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


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
//import org.apache.http.ssl.SSLContexts;
//import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
//@Configuration
//@ConfigurationProperties(prefix = "client")
public class HttpClientConfiguration {

	@Autowired
	private ResourceLoader resourceLoader;
	
	protected static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());		

	private SSL ssl;
	
	public SSL getSSL() {
		return this.ssl;
	}

	public void setSSL(SSL theSSL) {
		this.ssl = theSSL;
	}

	public static class SSL {

		private String keyStore;
		private String keyStoreType = "JKS";
		private String keyStorePasswd;
		private String keyAlias;
		private String trustStore;
		private String trustStoreType = "JKS";
		private String trustStorePasswd;

		public String getKeyStore() {
			return this.keyStore;
		}

		public void setKeyStore(String theKeyStore) {
			this.keyStore = theKeyStore;
		}

		public String getKeyStoreType() {
			return this.keyStoreType;
		}

		public void setKeyStoreType(String theKeyStoreType) {
			this.keyStoreType = theKeyStoreType;
		}

		public String getKeyStorePassword() {
			return this.keyStorePasswd;
		}

		public void setKeyStorePassword(String theKeyStorePassword) {
			this.keyStorePasswd = theKeyStorePassword;
		}

		public String getKeyAlias() {
			return this.keyAlias;
		}

		public void setKeyAlias(String theKeyAlias) {
			this.keyAlias = theKeyAlias;
		}

		public String getTrustStore() {
			return this.trustStore;
		}

		public void setTrustStore(String theTrustStore) {
			this.trustStore = theTrustStore;
		}

		public String getTrustStoreType() {
			return this.trustStoreType;
		}

		public void setTrustStoreType(String theTrustStoreType) {
			this.trustStoreType = theTrustStoreType;
		}

		public String getTrustStorePassword() {
			return this.trustStorePasswd;
		}

		public void setTrustStorePassword(String theTrustStorePassword) {
			this.trustStorePasswd = theTrustStorePassword;
		}

		protected boolean hasKeyStoreInfo() {
			return this.keyStore != null && this.keyStoreType != null && this.keyStorePasswd != null;
		}

		protected boolean hasTrustStoreInfo() {
			return this.trustStore != null
					&& this.trustStoreType != null /*
													 * && this.trustStorePasswd
													 * != null
													 */;
		}

		public String toString() {
			return new StringBuilder("").append("SSL(").append(this.keyStore).append(",").append(this.keyStoreType)
					.append(",").append(this.keyAlias).append("/").append(this.trustStore).append(",")
					.append(this.trustStoreType).append(")").toString();
		}
	}

	public String toString() {
		return new StringBuilder("").append("ClientConfiguration(").append(this.ssl).append(")").toString();
	}
	
	public HttpClient buildClient() {

		SSLContext sslContext = null;
		log.info( "Build HttpClient with " + this);

		if (this.resourceLoader == null)
			this.resourceLoader = new DefaultResourceLoader();

		if (this.ssl == null) {
			log.info( "No ssl config was provided");
		} else {
			KeyStore keyStore = null;
			if (this.ssl.hasKeyStoreInfo()) {
				InputStream keyStoreSource = null;
				try {
					keyStoreSource = this.resourceLoader.getResource(this.ssl.keyStore).getURL().openStream();
				}
				catch (FileNotFoundException rnfx) {
					try {
						keyStoreSource = new FileInputStream(this.ssl.keyStore);
					}
					catch (FileNotFoundException fnfx) {
						throw new IllegalStateException("Failed to find key store " + this.ssl.keyStore);
					}
				}
				catch (IOException iox) {
					throw new IllegalStateException("Error loading key material: " + iox, iox);
				}

				try {
					keyStore = KeyStore.getInstance(this.ssl.keyStoreType);
					keyStore.load(keyStoreSource,	this.ssl.keyStorePasswd.toCharArray());
					log.info( "Loaded key store: " + this.ssl.keyStore);
				}
				catch (Exception x) {
					throw new IllegalStateException("Error loading key material: " + x, x);
				}
			}

			KeyStore trustStore = null;
			if (this.ssl.hasTrustStoreInfo()) {
				InputStream trustStoreSource = null;
				try {
					trustStoreSource = this.resourceLoader.getResource(this.ssl.trustStore).getURL().openStream();
				}
				catch (FileNotFoundException rnfx) {
					try {
						trustStoreSource = new FileInputStream(this.ssl.trustStore);
					}
					catch (FileNotFoundException fnfx) {
						throw new IllegalStateException("Failed to find trust store " + this.ssl.trustStore);
					}
				}
				catch (IOException iox) {
					throw new IllegalStateException("Error loading trust material: " + iox, iox);
				}

				try {
					trustStore = KeyStore.getInstance(this.ssl.trustStoreType);
					trustStore.load(trustStoreSource,	this.ssl.trustStorePasswd.toCharArray());
					log.info( "Loaded trust store: " + this.ssl.trustStore);
				}
				catch (Exception x) {
					throw new IllegalStateException("Error loading trust material: " + x, x);
				}
			}

			SSLContextBuilder contextBuilder = SSLContexts.custom();
			try {
				if (keyStore != null) {
					contextBuilder.loadKeyMaterial(keyStore,
							this.ssl.keyStorePasswd.toCharArray()/*
																	 * , (aliases, socket) -> {
																	 * 
																	 * return this.ssl.keyAlias; }
																	 */);
				}

				if (trustStore != null) {
					contextBuilder.loadTrustMaterial(trustStore, (x509Certificates, s) -> false);
				}

				sslContext = contextBuilder.build();
			} catch (Exception x) {
				throw new IllegalStateException("Error building ssl context", x);
			}
		}
		// !!TODO: teh default hostname verifier needs to be changed!!

		SSLConnectionSocketFactory sslSocketFactory = null;
		if (sslContext != null) {
			sslSocketFactory = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1.2" }, null,
					SSLConnectionSocketFactory.getDefaultHostnameVerifier());
			log.info( "SSL connection factory configured");
		}

		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
		registryBuilder.register("http", PlainConnectionSocketFactory.getSocketFactory());
		if (sslSocketFactory != null) {
			registryBuilder.register("https", sslSocketFactory);
		}
		Registry<ConnectionSocketFactory> registry = registryBuilder.build();

		/*
		 * PoolingHttpClientConnectionManager connectionManager = new
		 * PoolingHttpClientConnectionManager(registry);
		 * connectionManager.setMaxTotal(this.poolSize);
		 * connectionManager.setDefaultMaxPerRoute(this.poolSize);
		 */

		/*CredentialsProvider credsProvider = null;
		if (hasClient()) {
			credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(this.client.getUsername(), this.client.getPassword()));
			log.info( "Credentials configured");
		} else {
			log.info( "No credentials were provided");
		}*/

		HttpClientBuilder clientBuilder = HttpClients.custom();

		// clientBuilder.setConnectionManager(connectionManager);
		clientBuilder.setConnectionManager(new BasicHttpClientConnectionManager(registry));

		if (sslSocketFactory != null)
			clientBuilder.setSSLSocketFactory(sslSocketFactory);

		/*if (credsProvider != null)
			clientBuilder.setDefaultCredentialsProvider(credsProvider);*/

		/*if (hasAddress()) {
			clientBuilder.setRoutePlanner(
				new HttpRoutePlanner() {
					public HttpRoute determineRoute(HttpHost theTarget, HttpRequest theRequest, HttpContext theContext) {
						return new HttpRoute(theTarget, InterfaceConfiguration.this.inetAddress, hasSSL());
					}
				});
		}*/

		return clientBuilder.build();
	}

	/*public HttpClient buildClient() {

		SSLContext sslContext = null;
		log.info( "Build HttpClient with " + this);

		if (this.resourceLoader == null)
			this.resourceLoader = new DefaultResourceLoader();

		if (this.ssl == null) {
			log.info( "No ssl config was provided");
		} else {
			KeyStore keyStore = null;
			if (this.ssl.hasKeyStoreInfo()) {
				InputStream keyStoreSource = null;
				try {
					keyStoreSource = this.resourceLoader.getResource(this.ssl.keyStore).getURL().openStream();
				}
				catch (FileNotFoundException rnfx) {
					try {
						keyStoreSource = new FileInputStream(this.ssl.keyStore);
					}
					catch (FileNotFoundException fnfx) {
						throw new IllegalStateException("Failed to find key store " + this.ssl.keyStore);
					}
				}
				catch (IOException iox) {
					throw new IllegalStateException("Error loading key material: " + iox, iox);
				}

				try {
					keyStore = KeyStore.getInstance(this.ssl.keyStoreType);
					keyStore.load(keyStoreSource,	this.ssl.keyStorePasswd.toCharArray());
					log.info( "Loaded key store: " + this.ssl.keyStore);
				}
				catch (Exception x) {
					throw new IllegalStateException("Error loading key material: " + x, x);
				}
			}

			KeyStore trustStore = null;
			if (this.ssl.hasTrustStoreInfo()) {
				InputStream trustStoreSource = null;
				try {
					trustStoreSource = this.resourceLoader.getResource(this.ssl.trustStore).getURL().openStream();
				}
				catch (FileNotFoundException rnfx) {
					try {
						trustStoreSource = new FileInputStream(this.ssl.trustStore);
					}
					catch (FileNotFoundException fnfx) {
						throw new IllegalStateException("Failed to find trust store " + this.ssl.keyStore);
					}
				}
				catch (IOException iox) {
					throw new IllegalStateException("Error loading trust material: " + iox, iox);
				}

				try {
					trustStore = KeyStore.getInstance(this.ssl.trustStoreType);
					trustStore.load(trustStoreSource,	this.ssl.trustStorePasswd.toCharArray());
					log.info( "Loaded trust store: " + this.ssl.trustStore);
				}
				catch (Exception x) {
					throw new IllegalStateException("Error loading trust material: " + x, x);
				}
			}

			SSLContextBuilder contextBuilder = SSLContexts.custom();
			try {
				if (keyStore != null) {
					contextBuilder.loadKeyMaterial(keyStore,
							this.ssl.keyStorePasswd.toCharArray());
				}

				if (trustStore != null) {
					contextBuilder.loadTrustMaterial(trustStore, (x509Certificates, s) -> false);
				}

				sslContext = contextBuilder.build();
			} catch (Exception x) {
				throw new IllegalStateException("Error building ssl context", x);
			}
		}
		SSLConnectionSocketFactory sslSocketFactory = null;
		if (sslContext != null) {
			sslSocketFactory = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1.2" }, null,
					SSLConnectionSocketFactory.getDefaultHostnameVerifier());
			log.info( "SSL connection factory configured");
		}

		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
		registryBuilder.register("http", PlainConnectionSocketFactory.getSocketFactory());
		if (sslSocketFactory != null) {
			registryBuilder.register("https", sslSocketFactory);
		}
		Registry<ConnectionSocketFactory> registry = registryBuilder.build();

		HttpClientBuilder clientBuilder = HttpClients.custom();

		// clientBuilder.setConnectionManager(connectionManager);
		clientBuilder.setConnectionManager(new BasicHttpClientConnectionManager(registry));

		if (sslSocketFactory != null)
			clientBuilder.setSSLSocketFactory(sslSocketFactory);

		return clientBuilder.build();
	} */
}

