package com.wiz.models;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

public interface Json {
    JsonObject toJson();

    default Buffer toBuffer() {
        return this.toJson().toBuffer();
    }
}
