package com.djhu.service.query;

import com.djhu.elasticsearch.core.request.SearchRequest;
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

    List<T> findAll(String dbId, SearchRequest searchRequest);

    T findBy(String dbId, HIsInfoDto hIsInfo);

    T findById(String dbId,String id);

    T findById(String index,String type,String id);

}
