package com.djhu.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cyf
 * @description
 * @create 2020-04-24 15:12
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HIsInfoDto {

    private String his_id;
    private String his_visit_id;
    private String his_domain_id;
    private String his_visit_domain_id;

}
