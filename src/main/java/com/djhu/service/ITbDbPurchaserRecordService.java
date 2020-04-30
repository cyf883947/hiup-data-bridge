package com.djhu.service;

import com.baomidou.mybatisplus.service.IService;
import com.djhu.entity.atses.TbDbPurchaserRecord;

import java.util.Map;

/**
 * <p>
 * 数据接收厂商推送记录表 服务类
 * </p>
 *
 * @author cyf
 * @since 2020-04-24
 */
public interface ITbDbPurchaserRecordService extends IService<TbDbPurchaserRecord> {

    boolean exist(String resourceDbId, String purchasersId, Map map);

}
