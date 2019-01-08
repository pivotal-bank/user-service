package io.pivotal.user.domain.uaa;

import lombok.Data;


/**
 * POJO Representing a name in UAA
 *
 * @author Simon Rowe
 */
@Data
public class UaaName {
    private String familyName;
    private String givenName;
}
