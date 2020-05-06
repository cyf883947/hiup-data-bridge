package com.djhu.service;


import com.baomidou.mybatisplus.service.IService;
import com.djhu.entity.scientper.TbDbResource;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author cyf
 * @since 2020-04-24
 */
public interface ITbDbResourceService extends IService<TbDbResource> {
    String PREFIX = "person_";

    String getEsSuffixByDbId(String dbId);

    String getDbIdByIndex(String index);

    List<String> selectDbIds(String dbId);

}
