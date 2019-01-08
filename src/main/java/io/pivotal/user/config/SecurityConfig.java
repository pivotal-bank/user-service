package io.pivotal.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2ResourceServerProperties resourceServerProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().
                headers().cacheControl().disable()
                .and().authorizeRequests()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                .anyRequest().fullyAuthenticated()
                .and().oauth2ResourceServer().jwt().decoder(jwtDecoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
       web.ignoring().antMatchers("/users/register");
    }

    @Bean
    @Qualifier("uaaClientCredentials")
    @ConfigurationProperties("security.oauth2.client.uaa")
    public ClientCredentialsResourceDetails clientCredentialsResourceDetails() {
        return new ClientCredentialsResourceDetails();
    }

    @Bean
    public OAuth2RestTemplate oauth2RestTemplate(@Qualifier("uaaClientCredentials") ClientCredentialsResourceDetails oAuth2ProtectedResourceDetails) {
        return new OAuth2RestTemplate(oAuth2ProtectedResourceDetails);
    }

    private JwtDecoder jwtDecoder() {
        String issuerUri = this.resourceServerProperties.getJwt().getIssuerUri();
        NimbusJwtDecoderJwkSupport jwtDecoder =
                (NimbusJwtDecoderJwkSupport) JwtDecoders.fromOidcIssuerLocation(issuerUri);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        jwtDecoder.setJwtValidator(withIssuer);
        return jwtDecoder;
    }

}
