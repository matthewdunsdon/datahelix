package com.scottlogic.deg.schemas.common;

import static com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scottlogic.deg.schemas.v3.V3ProfileDTO;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "schemaVersion"
)
@JsonSubTypes({
    @Type(value = V3ProfileDTO.class, name = V3ProfileDTO.SchemaVersion)
})
public abstract class BaseProfile {
    @JsonIgnore // not sure why I have to do this - otherwise two instances are serialised. possibly because of the JsonTypeInfo above?
    public String schemaVersion;

    protected BaseProfile(String schemaVersion)
    {
        this.schemaVersion = schemaVersion;
    }
}
