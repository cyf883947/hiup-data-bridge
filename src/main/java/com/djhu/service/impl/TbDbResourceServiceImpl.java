package com.djhu.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.djhu.entity.scientper.TbDbResource;
import com.djhu.mapper.scientper.TbDbResourceMapper;
import com.djhu.service.ITbDbResourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author cyf
 * @since 2020-04-24
 */
@Service
public class TbDbResourceServiceImpl extends ServiceImpl<TbDbResourceMapper, TbDbResource> implements ITbDbResourceService {

    /**
     *  数据库创建已完成
     */
    private static final String CREATED = "2";


    @Override
    public String getEsSuffixByDbId(String dbId) {
        String esSuffix = null;
        if(StringUtils.isNotEmpty(dbId)){
            EntityWrapper<TbDbResource> wrapper = new EntityWrapper<>();
            wrapper.eq("db_id",dbId);
            wrapper.eq("status",CREATED);
            TbDbResource tbDbResource = this.selectOne(wrapper);
            if(tbDbResource != null){
                esSuffix = tbDbResource.getEsSuffix();
            }
        }
        return esSuffix;
    }

    @Override
    public String getDbIdByIndex(String index) {
        if(StringUtils.isNotEmpty(index)){
            String esSufffix = index.substring(index.indexOf(PREFIX) + PREFIX.length());
            EntityWrapper<TbDbResource> wrapper = new EntityWrapper<>();
            wrapper.eq("ES_SUFFIX", esSufffix);
            TbDbResource tbDbResource = selectOne(wrapper);
            if (tbDbResource != null) {
                String resourceDbId = tbDbResource.getDbId();
                return resourceDbId;
            }
        }
        return null;
    }

    @Override
    public List<String> selectDbIds(String dbId) {
        EntityWrapper<TbDbResource> wrapper = new EntityWrapper<>();
        // 创建成功的库
        wrapper.eq("status",CREATED);
        if(StringUtils.isNotEmpty(dbId)){
            wrapper.eq("DB_ID",dbId);
        }
        // 非全院库
        wrapper.isNull("HOSPITAL_FLAG");
        wrapper.isNotNull("ES_SUFFIX");
        wrapper.setSqlSelect("db_id");
        List<TbDbResource> tbDbResources = this.selectList(wrapper);

        List<String> dbIds = new ArrayList<>(tbDbResources.size());
        for (TbDbResource dbResource : tbDbResources) {
            dbIds.add(dbResource.getDbId());
        }
        return dbIds;
    }
}
