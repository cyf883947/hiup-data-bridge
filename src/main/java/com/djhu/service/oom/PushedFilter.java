package com.djhu.service.oom;

import com.djhu.service.Filter;
import com.djhu.service.ITbDbPurchaserRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author cyf
 * @description
 * @create 2020-04-29 19:26
 **/
@Service
public class PushedFilter implements Filter {

    @Autowired
    ITbDbPurchaserRecordService purchaserRecordService;

    @Override
    public boolean accept(String resourceDbId, String purchaserId,Map map) {
        return purchaserRecordService.exist(resourceDbId,purchaserId,map);
    }
}
