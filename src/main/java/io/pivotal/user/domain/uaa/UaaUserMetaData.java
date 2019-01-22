package io.pivotal.user.domain.uaa;


import lombok.Data;
import org.joda.time.DateTime;

/**
 *
 * POJO Representing user meta data in UAA
 *
 * @author Simon Rowe
 */

@Data
public class UaaUserMetaData {

    private Long version;
    private DateTime created;
    private DateTime lastModified;

}
