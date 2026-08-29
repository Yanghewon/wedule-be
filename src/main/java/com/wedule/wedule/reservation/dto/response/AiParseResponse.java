package com.wedule.wedule.reservation.dto.response;

import java.util.Map;

public class AiParseResponse {
    private String groomName;
    private String brideName;
    private String phone;
    private String weddingDate;
    private String weddingTime;
    private String venueName;
    private Map<String, String> customFields;

    public String getGroomName() { return groomName; }
    public void setGroomName(String groomName) { this.groomName = groomName; }

    public String getBrideName() { return brideName; }
    public void setBrideName(String brideName) { this.brideName = brideName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWeddingDate() { return weddingDate; }
    public void setWeddingDate(String weddingDate) { this.weddingDate = weddingDate; }

    public String getWeddingTime() { return weddingTime; }
    public void setWeddingTime(String weddingTime) { this.weddingTime = weddingTime; }

    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }

    public Map<String, String> getCustomFields() { return customFields; }
    public void setCustomFields(Map<String, String> customFields) { this.customFields = customFields; }
}
