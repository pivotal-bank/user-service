package io.pivotal.user.domain;

import io.pivotal.user.domain.uaa.UaaUser;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * Builder object to instantiating User object given a UaaUser
 *
 * @author Simon Rowe
 */

@Data
public class UserBuilder {

    private UaaUser uaaUser;

    public static UserBuilder withUaaUser(UaaUser uaaUser) {
        UserBuilder builder = new UserBuilder();
        builder.setUaaUser(uaaUser);
        return builder;
    }

    public User build() {
        Assert.notNull(uaaUser, "Please set uaaUser before attempting to build");
        User user = new User();
        user.setId(uaaUser.getId());
        user.setEmail(uaaUser.getEmails().get(0).getValue());
        user.setGivenNames(uaaUser.getName().getGivenName());
        user.setSurname(uaaUser.getName().getFamilyName());
        user.setCreatedDate(uaaUser.getMeta().getCreated());
        return user;
    }
}
