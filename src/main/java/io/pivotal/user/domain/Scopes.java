package io.pivotal.user.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties("scopes")
public class Scopes {

    private String trade;
    private String account;
    private String portfolio;
    private String bank;

    private Map<String,String> groupIds = new HashMap<>();

    public String getGroupId(String groupName) {
        return groupIds.get(groupName);
    }

    public void setGroupId(String groupName, String groupId) {
        groupIds.put(groupName, groupId);
    }

}
