package com.atypon.wayf.data.identity.external;

import com.atypon.wayf.data.InflationPolicy;

import java.util.Collection;

public class IdpExternalIdQuery {

    public static final String IDENTITY_PROVIDER = "identityProvider";

    private Long id;
    private String externalId;
    private InflationPolicy inflationPolicy;
    private String provider;
    private Long idpId;
    private Integer limit;
    private Integer offset;

    public IdpExternalIdQuery() {

    }

    public String getProvider() {
        return provider;
    }

    public IdpExternalIdQuery setProvider(String provider) {
        this.provider = provider;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public IdpExternalIdQuery setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public Long getId() {
        return id;
    }

    public IdpExternalIdQuery setId(Long id) {
        this.id = id;
        return this;
    }

    public InflationPolicy getInflationPolicy() {
        return inflationPolicy;
    }

    public IdpExternalIdQuery setInflationPolicy(InflationPolicy inflationPolicy) {
        this.inflationPolicy = inflationPolicy;
        return this;
    }

    public Long getIdpId() {
        return idpId;
    }

    public IdpExternalIdQuery setIdpId(Long idpId) {
        this.idpId = idpId;
        return this;
    }

    public boolean isNullIdpId() {
        return getIdpId() == null;
    }

    public Integer getOffset() {
        return offset;
    }

    public IdpExternalIdQuery setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public IdpExternalIdQuery setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

}
