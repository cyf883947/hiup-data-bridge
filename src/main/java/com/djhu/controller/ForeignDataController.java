package com.djhu.controller;


import com.djhu.common.Result;
import com.djhu.common.ResultGenerator;
import com.djhu.common.aspect.ApiLog;
import com.djhu.common.aspect.LogTypeEnum;
import com.djhu.common.constant.DataConst;
import com.djhu.entity.dto.HIsInfoDto;
import com.djhu.entity.dto.UnifiedDto;
import com.djhu.service.ProcessingData;
import com.djhu.service.query.IQueryDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 对外提供数据接口controller
 * </p>
 *
 * @author cyf
 * @since 2020-04-24
 */
@Slf4j
@RestController
public class ForeignDataController {

    @Autowired
    private IQueryDataService queryDataService;
    @Autowired
    private ProcessingData processingData;

    @RequestMapping(value = "/start" ,method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Result start(){
        log.info("ForeignDataController.start......");
        return ResultGenerator.genSuccessResult();
    }

    @ApiLog(value = "科研推送专科库",logType = LogTypeEnum.SEARCH)
    @RequestMapping(value = "/data/push" ,method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public Result query(String dbId){
        if(StringUtils.isEmpty(dbId)){
            return ResultGenerator.genFailResult("传入参数不能为空!!!");
        }
        Result result;
        try {
            processingData.handle(dbId, DataConst.CREATE_OR_UPDATE);
            result = ResultGenerator.genSuccessResult();
        } catch (Exception e) {
            result = ResultGenerator.genFailResult(e.getMessage());
        }
        return result;
    }



    @ApiLog(value = "科研对外查询",logType = LogTypeEnum.SEARCH)
    @RequestMapping(value = "/data/query" ,method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Result query(@RequestBody UnifiedDto unifiedDto){
        String dbId = unifiedDto.getDb_id();
        if(StringUtils.isEmpty(dbId) ||  emptyParam(unifiedDto)){
            return ResultGenerator.genFailResult("传入参数不能为空!!!");
        }
        HIsInfoDto hIsInfoDto = new HIsInfoDto();
        BeanUtils.copyProperties(unifiedDto,hIsInfoDto);

        Object data = queryDataService.findBy(dbId,hIsInfoDto);
        return ResultGenerator.genSuccessResult(data);
    }


    private boolean emptyParam(UnifiedDto unifiedDto) {
        return StringUtils.isAnyEmpty(unifiedDto.getHis_id(),unifiedDto.getHis_domain_id(),unifiedDto.getHis_visit_id(),unifiedDto.getHis_visit_domain_id());
    }

}

