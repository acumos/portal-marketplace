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

import org.acumos.portal.be.security.AuthenticationTokenFilter;
import org.acumos.portal.be.security.HttpUnauthorizedEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private HttpUnauthorizedEntryPoint authenticationEntryPoint;
    

    /**
	 * Open access to the documentation.
	 */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/users/register", "/auth/login", "oauth/login/**");
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
            .and()
            .csrf()
            .disable()
            .headers()
            .frameOptions()
            .disable()
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers("/auth/login").permitAll()
        .antMatchers("/oauth/login/register").permitAll()
        .antMatchers("/oauth/login").permitAll()
        .antMatchers("/auth/validateToken").permitAll()
        .antMatchers("/users/register").permitAll()
        .antMatchers("/users/delete").permitAll()
        .antMatchers("/solutions").permitAll()
        .antMatchers("/portal/solutions").permitAll()
        .antMatchers("/solutions/{solutionId}").permitAll()
        //.antMatchers("/artifacts").permitAll()
        //.antMatchers("/artifacts/{artifactId}").permitAll()
        //.antMatchers("/publish/{visibility}").permitAll()
        //.antMatchers("/models/{userId}").permitAll()
        //.antMatchers("/unpublish/{visibility}").permitAll()
        //.antMatchers("/webBasedOnBoarding/addToCatalog/{userId}").permitAll()
        .antMatchers("/webBasedOnBoarding/messagingStatus/{userId}/{trackingId}").permitAll()
        .antMatchers("/webBasedOnBoarding/stepResult/create").permitAll()
        .antMatchers("/auth/jwtToken").permitAll()
        .antMatchers("/filter/modeltype").permitAll()
        .antMatchers("/filter/toolkitType").permitAll()
        .antMatchers("/solution/updateViewCount/{solutionId}").permitAll()
        .antMatchers("/filter/accesstype").permitAll()
        .antMatchers("/swagger-ui.html").permitAll()
        .antMatchers("/model/upload/{userId}").permitAll()
        .antMatchers("/readArtifactSolutions/{artifactId}").permitAll()
        .antMatchers("/getRelatedMySolutions").permitAll()
        .antMatchers("/users/userAccountDetails").permitAll()
        .antMatchers("/validation/{taskId}").permitAll()
        .antMatchers("/solution/getRating/{solutionId}").permitAll()
        .antMatchers("/thread/{solutionId}/{revisionId}/comments").permitAll()
        .antMatchers("/solutions/{solutionId}/revisions").permitAll()
        .antMatchers("/solutions/{solutionId}/revisions/{revisionId}").permitAll()
        .antMatchers("/validation/{solutionId}/{revisionId}").permitAll()
        .antMatchers("/roleCounts").permitAll()
        .antMatchers("/oauth/login/register").permitAll()
        .antMatchers("/oauth/login").permitAll()
        .antMatchers("/admin/config/{configKey}").permitAll()
        .antMatchers("/user/deleteUser").permitAll()
        .antMatchers("/users/qAUrl").permitAll()
        .antMatchers("/users/docs").permitAll()
        .antMatchers("/users/userProfileImage/{userId}").permitAll()  
        .antMatchers("/admin/version").permitAll()
        .antMatchers("/users/forgetPassword").permitAll()
        .antMatchers("/webBasedOnBoarding/broker").permitAll()
        .antMatchers("/downloads/{solutionId}").permitAll()
        .antMatchers("/webBasedOnBoarding/convertToOnap/{solutionId}/{revisionId}/{userId}").permitAll()
        .anyRequest().authenticated();
    	
    	// Custom JWT based authentication
    	http
          .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    	
    	http.headers().cacheControl();

    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
      AuthenticationTokenFilter authenticationTokenFilter = new AuthenticationTokenFilter();
      authenticationTokenFilter.setAuthenticationManager(authenticationManagerBean());
      return authenticationTokenFilter;
    }
}
