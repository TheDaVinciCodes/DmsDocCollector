package com.tdvc.doccollector;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tdvc.dms.rest.client.DmsRestClient;

/**
 * @author TheDaVinciCodes
 *
 */
@SpringBootApplication
@EnableWebSecurity
@ComponentScan(basePackages = "com.tdvc.doccollector")
public class App extends WebSecurityConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Value(value = "${spring.security.user.name}")
	String username;

	@Value(value = "${spring.security.user.password}")
	String password;

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
    	PasswordEncoder encoder =
          PasswordEncoderFactories.createDelegatingPasswordEncoder();
    	auth
          .inMemoryAuthentication()
          .withUser(username)
          .password(encoder.encode(password))
          .roles("USER", "ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
          .authorizeRequests()
          .anyRequest()
          .authenticated()
          .and()
          .csrf(cust -> cust.disable())
          .httpBasic()
          .and()
          .formLogin();
    }

	@Bean
	@ConfigurationProperties(prefix="spring.datasource")
	public DataSource dataSource() {
	    return DataSourceBuilder.create().build();
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource ds) {
	    return new JdbcTemplate(ds);
	}

	@Bean
    DmsRestClient dmsRestClient(@Value("${dms.base.url}") String baseUrl) {
        return DmsRestClient.of(baseUrl);
    }

}
