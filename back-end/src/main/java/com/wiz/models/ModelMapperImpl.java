package com.wiz.models;

import java.util.Map;
import com.wiz.utils.JsonUtils;
import io.vertx.core.json.JsonObject;

public abstract class ModelMapperImpl extends JsonImpl implements ModelMapper {
    private final Map<String, String> map;

    protected ModelMapperImpl(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public JsonObject toJson() {
        return JsonUtils.toJsonObject(this);
    }

    @Override
    public Map<String, String> getMap() {
        return map;
    }
}
