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
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
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
        .antMatchers("/cas/serviceValidate").permitAll()
        .antMatchers("/oauth/login/register").permitAll()
        .antMatchers("/oauth/login").permitAll()
        .antMatchers("/auth/validateToken").permitAll()
        .antMatchers("/users/register").permitAll()
        .antMatchers("/solutions").permitAll()
        .antMatchers("/portal/solutions").permitAll()
        .antMatchers("/solutions/{solutionId}").permitAll()
        .antMatchers("/webBasedOnBoarding/messagingStatus/{userId}/{trackingId}").permitAll()
        .antMatchers("/webBasedOnBoarding/stepResult/create").permitAll()
        .antMatchers("/auth/jwtToken").permitAll()
        .antMatchers("/filter/modeltype").permitAll()
        .antMatchers("/filter/toolkitType").permitAll()
        .antMatchers("/solution/updateViewCount/{solutionId}").permitAll()
        .antMatchers("/filter/accesstype").permitAll()
        .antMatchers("/swagger-ui.html").permitAll()
        .antMatchers("/readArtifactSolutions/{artifactId}").permitAll()
        .antMatchers("/getRelatedMySolutions").permitAll()
        .antMatchers("/validation/{taskId}").permitAll()
        .antMatchers("/solution/getRating/{solutionId}").permitAll()
        .antMatchers("/thread/{solutionId}/{revisionId}/comments").permitAll()
        .antMatchers("/solutions/{solutionId}/revisions").permitAll()
        .antMatchers("/solutions/{solutionId}/revisions/{revisionId}").permitAll()
        .antMatchers("/validation/{solutionId}/{revisionId}").permitAll()
        .antMatchers("/solution/avgRating/{solutionId}").permitAll()
        .antMatchers(HttpMethod.POST, "/users/userAccountDetails").permitAll()
        .antMatchers("/roleCounts").permitAll()
        .antMatchers(HttpMethod.GET, "/admin/config/{configKey}").permitAll()
        .antMatchers(HttpMethod.GET, "/users/qAUrl").permitAll()
        .antMatchers(HttpMethod.GET, "/users/docs").permitAll()
        .antMatchers("/users/userProfileImage/{userId}").permitAll()  
        .antMatchers(HttpMethod.GET, "/admin/version").permitAll()
        .antMatchers("/users/forgetPassword").permitAll()
        .antMatchers(HttpMethod.GET, "/downloads/{solutionId}").permitAll()
        .antMatchers(HttpMethod.GET, "/cas/enabled").permitAll()
        .antMatchers("/notifications/pref/create").permitAll()
        .antMatchers("/notifications/pref/byUserId/{userId}").permitAll()
        .antMatchers("/notifications/pref/update").permitAll()       
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
