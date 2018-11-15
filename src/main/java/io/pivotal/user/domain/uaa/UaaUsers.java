package io.pivotal.user.domain.uaa;

import lombok.Data;

import java.util.List;


/**
 * POJO representing a list of users in UAA
 *
 * @author Simon Rowe
 */
@Data
public class UaaUsers {

    private List<UaaUser> resources;
}
