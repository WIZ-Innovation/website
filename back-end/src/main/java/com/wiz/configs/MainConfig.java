package com.wiz.configs;

import java.util.HashMap;
import com.wiz.models.ModelMapperImpl;

public class MainConfig extends ModelMapperImpl {
    private Integer host;

    public MainConfig() {
        super(new HashMap<String, String>() {
            {
                put("host", "host");
            }
        });
    }

    public Integer getHost() {
        return host;
    }

    public void setHost(Integer host) {
        this.host = host;
    }

}
