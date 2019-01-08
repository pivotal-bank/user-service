package io.pivotal.user.domain.uaa;

import lombok.Data;
import org.joda.time.DateTime;

import java.util.List;

/**
 * POJO representing a user in UAA
 *
 * @author Simon Rowe
 */
@Data
public class UaaUser {

    private String id;
    private UaaUserMetaData meta;
    private String userName;
    private UaaName name;
    private List<UaaEmail> emails;
    private DateTime passwordLastModified;

}
