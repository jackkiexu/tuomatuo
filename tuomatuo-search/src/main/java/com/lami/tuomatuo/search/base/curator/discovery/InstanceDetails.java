package com.lami.tuomatuo.search.base.curator.discovery;

import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonRootName;

/**
 * Created by xjk on 2016/4/18.
 */
@JsonRootName("details")
@Data
public class InstanceDetails {

    private String description;

    public InstanceDetails() {
        this("");
    }

    public InstanceDetails(String description) {
        this.description = description;
    }
}
