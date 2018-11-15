package io.pivotal.user.domain.uaa;

import io.pivotal.user.domain.RegistrationRequest;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.Arrays;

/**
 *
 * Builder to instantiate UaaCreateUserRequest object
 *
 * @author Simon Rowe
 *
 */
@Data
public class UaaCreateUserRequestBuilder {

    private RegistrationRequest registrationRequest;

    public static UaaCreateUserRequestBuilder withRegistrationRequest(RegistrationRequest registrationRequest) {
        UaaCreateUserRequestBuilder builder = new UaaCreateUserRequestBuilder();
        builder.setRegistrationRequest(registrationRequest);
        return builder;
    }

    public UaaCreateUserRequest build() {
        Assert.notNull(registrationRequest, "Please populate registration request before attempting to build");
        UaaCreateUserRequest request = new UaaCreateUserRequest();
        request.setUserName(registrationRequest.getEmail());
        request.setPassword(registrationRequest.getPassword());
        request.setActive(true);
        request.setVerified(true);
        request.setOrigin("uaa");
        UaaName name = new UaaName();
        name.setFamilyName(registrationRequest.getSurname());
        name.setGivenName(registrationRequest.getGivenNames());
        request.setName(name);
        UaaEmail email = new UaaEmail();
        email.setValue(registrationRequest.getEmail());
        request.setEmails(Arrays.asList(email));
        request.setSchemas(Arrays.asList("urn:scim:schemas:core:1.0"));
        return request;
    }

}
