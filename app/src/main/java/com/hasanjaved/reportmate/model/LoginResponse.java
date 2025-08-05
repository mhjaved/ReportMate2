
package com.hasanjaved.reportmate.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("token_type")
    @Expose
    private String tokenType;
    @SerializedName("expires_in")
    @Expose
    private Long expiresIn;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
//    @SerializedName("companies")
//    @Expose
//    private Companies companies;
//    @SerializedName("factories")
//    @Expose
//    private List<Object> factories;
    @SerializedName("role")
    @Expose
    private String role;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
//
//    public Companies getCompanies() {
//        return companies;
//    }
//
//    public void setCompanies(Companies companies) {
//        this.companies = companies;
//    }
//
//    public List<Object> getFactories() {
//        return factories;
//    }
//
//    public void setFactories(List<Object> factories) {
//        this.factories = factories;
//    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", status=" + status +
                ", accessToken='" + accessToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", deviceId='" + deviceId + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
