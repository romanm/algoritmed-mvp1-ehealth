package org.algoritmed.mvp1;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Права доступу
 * @author roman
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable() /* enable POST */
		.authorizeRequests()
		.antMatchers("/" /* index.html from static */
			, "/v/**" /* view HTML sites*/
			, "/f/**" /* files for sites*/
			, "/r/**" /* read JSON from server*/
			, "/webjars/**" /* CSS&JS from gradle */
		).permitAll()
		.anyRequest().authenticated()
		.and()
		.formLogin()
		.loginPage("/login")
		.permitAll()
		.and()
		.logout()
		.permitAll();
	}

	@Autowired DataSource dataSourceDb1;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
		.inMemoryAuthentication()
		.withUser("user").password("password").roles("USER");

		auth.jdbcAuthentication().dataSource(dataSourceDb1)
		.usersByUsernameQuery(
				"select username,password, enabled from users where username=?")
		.authoritiesByUsernameQuery(
				"select username, role from user_roles where username=?");
	}

}