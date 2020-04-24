package com.djhu.entity;

import com.djhu.elasticsearch.core.annotation.QueryAnnotation;
import lombok.Data;

/**
 * @author cyf
 * @description
 * @create 2020-04-24 16:41
 **/
@Data
public class HIsInfoRequest {
    @QueryAnnotation
    private String his_id;
    @QueryAnnotation
    private String his_visit_id;
    @QueryAnnotation
    private String his_domain_id;
    @QueryAnnotation
    private String his_visit_domain_id;

//    public HIsInfoRequest(){
//
//    }

//    public HIsInfoRequest(HIsInfoDto hIsInfoDto){
//        this.his_id = hIsInfoDto.getHis_id();
//        this.his_domain_id = hIsInfoDto.getHis_domain_id();
//        this.his_visit_id = hIsInfoDto.getHis_visit_id();
//        this.his_visit_domain_id =hIsInfoDto.getHis_visit_domain_id() ;
//    }

}
