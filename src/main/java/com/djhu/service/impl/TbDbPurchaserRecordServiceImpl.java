package com.djhu.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.djhu.common.constant.PushStatusConstant;
import com.djhu.entity.HisInfoEntity;
import com.djhu.entity.atses.TbDbPurchaserRecord;
import com.djhu.mapper.atses.TbDbPurchaserRecordMapper;
import com.djhu.service.ITbDbPurchaserRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 数据接收厂商推送记录表 服务实现类
 * </p>
 *
 * @author cyf
 * @since 2020-04-24
 */
@Slf4j
@Service
public class TbDbPurchaserRecordServiceImpl extends ServiceImpl<TbDbPurchaserRecordMapper, TbDbPurchaserRecord> implements ITbDbPurchaserRecordService {

    @Override
    public boolean exist(String resourceDbId, String purchasersId, Map map) {
        HisInfoEntity hisInfoEntity = new HisInfoEntity(map).invoke();
        String hisId = hisInfoEntity.getHisId();
        String hisDomainId = hisInfoEntity.getHisDomainId();
        String hisVisitId = hisInfoEntity.getHisVisitId();
        String hisVisitDomainId = hisInfoEntity.getHisVisitDomainId();

        EntityWrapper<TbDbPurchaserRecord> wrapper = new EntityWrapper<>();
        wrapper.eq("DB_ID",resourceDbId);
        wrapper.eq("PURCHASER_ID",purchasersId);
        wrapper.eq("STATUS", PushStatusConstant.SUCCESS);
        wrapper.eq("his_id",hisId);
        wrapper.eq("his_domain_id",hisDomainId);
        wrapper.eq("his_visit_id",hisVisitId);
        wrapper.eq("his_visit_domain_id",hisVisitDomainId);
        int count = 0;
        try {
            count = selectCount(wrapper);
        } catch (Exception e) {
            log.error("查询出现异常!!! {}",e.getMessage(),e);
        }
        return count>0;
    }
}
