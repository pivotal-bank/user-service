package io.pivotal.user.domain.uaa;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddUserToGroupRequest {

    private String origin = "uaa";
    private String type = "USER";
    private String value;

    public AddUserToGroupRequest(String value) {
        this.value = value;
    }
}
