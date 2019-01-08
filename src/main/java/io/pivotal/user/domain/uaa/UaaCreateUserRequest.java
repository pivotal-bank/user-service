package io.pivotal.user.domain.uaa;

import lombok.Data;

import java.util.List;

/**
 *
 * This class represents the Request Body for a UAA create user POST.
 *
 * @author Simon Rowe
 */

@Data
public class UaaCreateUserRequest {

    private String externalId;
    private String userName;
    private String password;
    private UaaName name;
    private List<UaaEmail> emails;
    private Boolean active;
    private Boolean verified;
    private String origin;
    private List<String> schemas;


}


