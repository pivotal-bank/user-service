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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        scopes.setGroupId(scopes.getAdmin(), groupIdFor(scopes.getAdmin()));
        scopes.setGroupId(scopes.getAccountOpen(), groupIdFor(scopes.getAccountOpen()));
        scopes.setGroupId(scopes.getBuy(), groupIdFor(scopes.getBuy()));
        scopes.setGroupId(scopes.getPortfolioRead(), groupIdFor(scopes.getPortfolioRead()));
        scopes.setGroupId(scopes.getSell(), groupIdFor(scopes.getSell()));
        scopes.setGroupId(scopes.getBank(), groupIdFor(scopes.getBank()));
    }

    public User register(RegistrationRequest registrationRequest) {
        UaaCreateUserRequest uaaCreateUserRequest = UaaCreateUserRequestBuilder.withRegistrationRequest(registrationRequest).build();
        ResponseEntity<UaaUser> uaaUserResponseEntity = restTemplate.postForEntity(uaaTarget + "/Users", uaaCreateUserRequest, UaaUser.class);
        User user = UserBuilder.withUaaUser(uaaUserResponseEntity.getBody()).build();
        assignUserToGroups(user, scopes.getAccountOpen(), scopes.getAdmin(), scopes.getBuy(), scopes.getSell(), scopes.getPortfolioRead(), scopes.getBank());
        return user;
    }

    public User get(String email) {
        ResponseEntity<UaaUsers> uaaUsersResponseEntity = restTemplate.getForEntity(uaaTarget + "/Users?filter=emails.value eq \"{email}\"", UaaUsers.class, email);
        UaaUsers uaaUsers = uaaUsersResponseEntity.getBody();
        if (CollectionUtils.isEmpty(uaaUsers.getResources())) {
            throw new NoRecordsFoundException();
        }
        return UserBuilder.withUaaUser(uaaUsers.getResources().get(0)).build();
    }


    public String groupIdFor(String groupName) {
        ResponseEntity<UaaGroups> uaaGroupsResponseEntity = restTemplate.getForEntity(uaaTarget + "/Groups?filter=displayName eq \"{groupName}\"", UaaGroups.class, groupName);
        return uaaGroupsResponseEntity.getBody().getResources().get(0).getId();
    }


    public void assignUserToGroups(User user, String... groups) {
        for (String groupName : groups) {
            ResponseEntity<Map> mapResponseEntity = restTemplate.postForEntity(uaaTarget + "/Groups/{groupId}/members",new AddUserToGroupRequest(user.getId()), Map.class, scopes.getGroupId(groupName));
        }
    }
}
