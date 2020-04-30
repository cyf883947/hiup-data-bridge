package com.djhu.entity;

import java.util.Map;

/**
 * @author cyf
 * @description
 * @create 2020-04-30 11:55
 **/
public class HisInfoEntity {

    private Map<String, Object> map;
    private String hisId;
    private String hisDomainId;
    private String hisVisitId;
    private String hisVisitDomainId;

    public HisInfoEntity(Map<String, Object> map) {
        this.map = map;
    }

    public String getHisId() {
        return hisId;
    }

    public String getHisDomainId() {
        return hisDomainId;
    }

    public String getHisVisitId() {
        return hisVisitId;
    }

    public String getHisVisitDomainId() {
        return hisVisitDomainId;
    }

    public HisInfoEntity invoke() {
        if (map != null) {
            hisId = String.valueOf(map.getOrDefault("his_id", ""));
            hisDomainId = String.valueOf(map.getOrDefault("his_domain_id", ""));
            hisVisitId = String.valueOf(map.getOrDefault("his_visit_id", ""));
            hisVisitDomainId = String.valueOf(map.getOrDefault("his_visit_domain_id", ""));
        }
        return this;
    }
}
