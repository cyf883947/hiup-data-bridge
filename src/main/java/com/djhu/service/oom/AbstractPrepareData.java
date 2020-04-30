package com.djhu.service.oom;

import com.djhu.common.constant.GlobalConstant;
import com.djhu.elasticsearch.core.request.AutoGeneratorSearchRequest;
import com.djhu.elasticsearch.core.request.DefaultGetDelRequest;
import com.djhu.elasticsearch.core.request.SearchRequest;
import com.djhu.entity.HIsInfoRequest;
import com.djhu.service.Filter;
import com.djhu.service.IPrepareData;
import com.djhu.service.ITbDbResourceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author cyf
 * @description
 * @create 2020-04-29 18:29
 **/
public abstract class AbstractPrepareData implements IPrepareData {

    @Autowired
    ITbDbResourceService tbDbResourceService;

    @Override
    public List list(String dbId, String purchaserId, Filter filter) {
        return list(dbId,null,purchaserId,filter);
    }

    @Override
    public Object getBy(String dbId, HIsInfoRequest hIsInfo,String purchaserId,Filter filter) {
        AutoGeneratorSearchRequest autoSearchRequest = new AutoGeneratorSearchRequest(hIsInfo);
        return list(dbId,autoSearchRequest,purchaserId,filter).get(0);
    }


    @Override
    public Object getBy(String id, String index, String type,String purchaserId,Filter filter) {
        DefaultGetDelRequest getDelRequest = new DefaultGetDelRequest(id);
        return getBy(index,type,getDelRequest,purchaserId,filter);
    }

    protected abstract List list(String dbId, SearchRequest searchRequest,String purchaserId,Filter filter);
    protected abstract Map getBy(String index, String type, DefaultGetDelRequest searchRequest,String purchaserId,Filter filter);

    protected String getIndex(String dbId) {
        String index = GlobalConstant.HIUP_PERSON_INDEX+"_"+tbDbResourceService.getEsSuffixByDbId(dbId);
        return index;
    }
}
