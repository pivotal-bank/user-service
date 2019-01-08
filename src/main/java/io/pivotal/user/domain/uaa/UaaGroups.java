package io.pivotal.user.domain.uaa;

import lombok.Data;

import java.util.List;

@Data
public class UaaGroups {

    private List<UaaGroup> resources;
}
