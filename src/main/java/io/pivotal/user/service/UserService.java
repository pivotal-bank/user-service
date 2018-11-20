package io.pivotal.user.service;

import io.pivotal.user.domain.RegistrationRequest;
import io.pivotal.user.domain.Scopes;
import io.pivotal.user.domain.UserBuilder;
import io.pivotal.user.domain.uaa.*;
import io.pivotal.user.domain.User;

import io.pivotal.user.exception.NoRecordsFoundException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.Map;

/**
 * The service in the user microservice.
 *
 * @author David Ferreira Pinto
 * @author Simon Rowe
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory
            .getLogger(UserService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Scopes scopes;

    @Value("${targets.uaa}")
    private String uaaTarget;

    @Value("${uaa.identity-zone-id}")
    private String identityZoneId;

    @PostConstruct
    public void init() {
        scopes.setGroupId(scopes.getAccount(), groupIdFor(scopes.getAccount()));
        scopes.setGroupId(scopes.getBank(), groupIdFor(scopes.getBank()));
        scopes.setGroupId(scopes.getPortfolio(), groupIdFor(scopes.getPortfolio()));
        scopes.setGroupId(scopes.getTrade(), groupIdFor(scopes.getTrade()));
    }

    public User register(RegistrationRequest registrationRequest) {
        UaaCreateUserRequest uaaCreateUserRequest = UaaCreateUserRequestBuilder.withRegistrationRequest(registrationRequest).build();
        ResponseEntity<UaaUser> uaaUserResponseEntity = restTemplate.exchange(uaaTarget + "/Users", HttpMethod.POST, getEntityWithHeaders(uaaCreateUserRequest), UaaUser.class);
        User user = UserBuilder.withUaaUser(uaaUserResponseEntity.getBody()).build();
        assignUserToGroups(user, scopes.getAccount(), scopes.getTrade(), scopes.getPortfolio(), scopes.getBank());
        return user;
    }

    public User get(String id) {

        ResponseEntity<UaaUser> uaaUsersResponseEntity = restTemplate.exchange(uaaTarget + "/Users/{id}", HttpMethod.GET, getEntityWithHeaders(null), UaaUser.class, id);
        return UserBuilder.withUaaUser(uaaUsersResponseEntity.getBody()).build();
    }


    public String groupIdFor(String groupName) {
        ResponseEntity<UaaGroups> uaaGroupsResponseEntity = restTemplate.exchange(uaaTarget + "/Groups?filter=displayName eq \"{groupName}\"", HttpMethod.GET, getEntityWithHeaders(null), UaaGroups.class, groupName);
        return uaaGroupsResponseEntity.getBody().getResources().get(0).getId();
    }


    public void assignUserToGroups(User user, String... groups) {
        for (String groupName : groups) {
            ResponseEntity<Map> mapResponseEntity = restTemplate.exchange(uaaTarget + "/Groups/{groupId}/members", HttpMethod.POST, getEntityWithHeaders(new AddUserToGroupRequest(user.getId())), Map.class, scopes.getGroupId(groupName));
        }
    }

    private HttpEntity getEntityWithHeaders(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Identity-Zone-Id", identityZoneId);
        HttpEntity entity = new HttpEntity(body,headers);
        return entity;
    }
}
