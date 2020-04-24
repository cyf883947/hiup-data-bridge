package com.djhu.service.query;

import com.djhu.entity.dto.HIsInfoDto;

import java.util.List;

/**
 * @author cyf
 * @description
 * @create 2020-04-24 15:07
 **/
public interface IQueryDataService<T> {

    List<T> findAll(String dbId);

    List<T> findAll(String dbId, List<HIsInfoDto> hIsInfo);

    T findBy(String dbId, HIsInfoDto hIsInfo);

}
