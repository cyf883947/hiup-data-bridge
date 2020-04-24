package com.djhu.service.push;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.djhu.entity.MsgInfo;
import com.djhu.entity.atses.TbDbPurchaser;
import com.djhu.entity.atses.TbDbPurchaserRecord;
import com.djhu.service.ITbDbPurchaserRecordService;
import com.djhu.service.ITbDbPurchaserService;
import com.djhu.service.ITbDbResourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cyf
 * @description
 * @create 2020-04-24 18:16
 **/
public class IPushDataServiceImpl implements IPushDataService {

    @Autowired
    private ITbDbResourceService tbDbResourceService;

    @Autowired
    private ITbDbPurchaserService purchaserService;

    @Autowired
    private ITbDbPurchaserRecordService purchaserRecordService;

    @Override
    public void push(String dbId) {
        push(dbId,null);
    }

    @Override
    public void push() {
        push(null,null);
    }

    @Override
    public void push(String dbId,MsgInfo msgInfo) {

        //1. 查询所有可用的数据库（除了全院库）
        List<String> dbIds = new ArrayList<>();
        if(StringUtils.isEmpty(dbId)){
            dbIds  = tbDbResourceService.selectDbIds();
        }else {
            dbIds.add(dbId);
        }

        // 2. 查询所有厂商
        List<TbDbPurchaser> tbDbPurchasers = purchaserService.selectList(null);
        Map<String,String> purchasersMap = new HashMap<>();
        for (TbDbPurchaser tbDbPurchaser : tbDbPurchasers) {
            String id = tbDbPurchaser.getId();
            String url = tbDbPurchaser.getCustom0();
            if(!purchasersMap.containsKey(id)){
                purchasersMap.put(id,url);
            }
        }

        for (String resourceDbId : dbIds) {

            // 3. 判断当前库是否推送过对应的厂商数据
            EntityWrapper<TbDbPurchaserRecord> wrapper = new EntityWrapper<>();
            wrapper.eq("EXTERNAL_DB_ID",resourceDbId);
            List<TbDbPurchaserRecord> tbDbPurchaserRecords = purchaserRecordService.selectList(wrapper);

            for (TbDbPurchaserRecord record : tbDbPurchaserRecords) {
                String purchaserId = record.getPurchaserId();
                if(purchasersMap.containsKey(purchaserId)){
                   // 推送过，暂不处理
                }else{
                    // 首次推送

                    // 获取需要推送的数据

                    String url = purchasersMap.get(purchaserId);
                    // 调用接口 执行推送
                }
            }
        }

        if(StringUtils.isEmpty(dbId)){
            // 推送所有数据库（除了全院库）数据

            /*
            1. 查询所有可用的数据库（除了全院库）
            2. 查询所有厂商
            3. 判断是否推送过对应的厂商数据
                    是 - 暂不处理
                    否-重新推送。更新推送失败原因
             */
        }

        if(StringUtils.isNotEmpty(dbId) && msgInfo == null){
            // 推送指定数据库数据

            /*
            1. 查询所有厂商
            2. 判断是否推送过对应的厂商数据
                    - 是   需要执行更新
                            用状态为 2 的数据查询状态为1 的数据，查询结果进行处理
                                - 简单点，只做新增（用状态2集合remove状态1数据），得出的结果直接记录为新增

//                                - 查询到的为    为更新状态
//                                - 查询不到的为  为新增状态
//                                - 如果
                    - 否   新建库，直接推送
                            for 所有厂商请求地址
                                - 1. 调用需要推送的厂商地址，执行推送。
                                - 2. 更新推送记录 TB_DB_PURCHASER_RECORD
                                - 3. 先删除 当前库 TbPatientuniqueidBackups 表中状态为 1 的数据
                                - 4. 再改 TbPatientuniqueidBackups 中数据状态为 1 （1-已推送 2-未推送）
             */
        }

        if(StringUtils.isNotEmpty(dbId) && msgInfo != null){
            // 推送单条数据

            /*
            1.  根据msgInfo 查询oarcle记录表 p_id_bak 是否推送过。
                        是 - 更改为更新状态 直接推送
                        否 - 更改为新增状态 直接推送
             */
        }

    }


}
