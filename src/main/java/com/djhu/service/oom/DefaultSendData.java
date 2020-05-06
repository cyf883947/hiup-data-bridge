package com.djhu.service.oom;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.djhu.common.constant.PatientStatusConstant;
import com.djhu.common.constant.PushStatusConstant;
import com.djhu.entity.HisInfoEntity;
import com.djhu.entity.ResultEntity;
import com.djhu.entity.atses.TbDbPurchaserRecord;
import com.djhu.service.ISendData;
import com.djhu.service.ITbDbPurchaserRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cyf
 * @description
 * @create 2020-04-29 19:17
 **/
@Slf4j
@Service
public class DefaultSendData implements ISendData {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ITbDbPurchaserRecordService purchaserRecordService;

    @Override
    public void send(List list, String url,String dbId,String purchasersId) {
        if(CollectionUtils.isEmpty(list) || StringUtils.isEmpty(url)){
            log.info(" 需要推送的数据 list 为空或者 调用地址 url 为空!!!");
            return;
        }
        long startTime = System.currentTimeMillis();

        JSONObject object = new JSONObject();
        object.put("db_id", dbId);
        List data = new ArrayList(list.size());
        for (Object obj : list) {
            Map<String,Object> map = (Map<String, Object>) obj;
            map.put("data_status", PatientStatusConstant.ADD);
            data.add(map);
        }
        ResponseEntity<ResultEntity> responseEntity = restTemplate.postForEntity(url, object.toJSONString(), ResultEntity.class);

        int code = responseEntity.getStatusCodeValue();

        for (Object obj : list) {
            Map<String, Object> map = (Map<String, Object>) obj;
            TbDbPurchaserRecord purchaserRecord = new TbDbPurchaserRecord();
            purchaserRecord.setId(IdWorker.getIdStr());
            purchaserRecord.setPurchaserId(purchasersId);
            purchaserRecord.setDbId(dbId);
            purchaserRecord.setConsume(String.valueOf(System.currentTimeMillis() - startTime));
            HisInfoEntity hisInfoEntity = new HisInfoEntity(map).invoke();
            purchaserRecord.setHisId(hisInfoEntity.getHisId());
            purchaserRecord.setHisVisitId(hisInfoEntity.getHisVisitId());
            purchaserRecord.setHisDomainId(hisInfoEntity.getHisDomainId());
            purchaserRecord.setHisVisitDomainId(hisInfoEntity.getHisVisitDomainId());
            if (code == 200) {
                purchaserRecord.setStatus(PushStatusConstant.SUCCESS);
            } else {
                purchaserRecord.setStatus(PushStatusConstant.ERROR);
            }
            try {
                purchaserRecordService.insert(purchaserRecord);
                log.info("保存记录到 TB_DB_PURCHASER_RECORD 表成功!!!");
            } catch (Exception e) {
                log.error("保存记录到 TB_DB_PURCHASER_RECORD 表失败!!!  {},{}", e.getMessage(), e);
                log.error("dbId:{},dbId:{}", dbId, purchasersId);
            }
        }
    }

}
