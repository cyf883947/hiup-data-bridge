package com.djhu.entity.scientper;


import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author cyf
 * @since 2020-04-24
 */
@TableName("TB_DB_RESOURCE")
public class TbDbResource extends Model<TbDbResource> {

private static final long serialVersionUID=1L;

    /**
     * 主键ID
     */
    @TableId("DB_ID")
    private String dbId;

    /**
     * 数据库名称
     */
    @TableField("DB_NAME")
    private String dbName;

    /**
     * 范围配置表ID
     */
    @TableField("ADVANCESEARCHREQUEST_ID")
    private String advancesearchrequestId;

    /**
     * 对应ES表INDEX
     */
    @TableField("ES_INDEX")
    private String esIndex;

    /**
     * 对应ES表TYPE
     */
    @TableField("ES_TYPE")
    private String esType;

    /**
     * 随访数据范围表表名
     */
    @TableField("FOLLOW_TABLE_NAME")
    private String followTableName;

    /**
     * 剩余时间
     */
    @TableField("PROCESS_REMAIN_TIME")
    private String processRemainTime;

    /**
     * 完成百分比
     */
    @TableField("PROCESS_PERCENT")
    private String processPercent;

    /**
     * 状态(0-关闭  1- 创建中 2- 已完成 3-修改中)
     */
    @TableField("STATUS")
    private String status;

    /**
     * 备注说明
     */
    @TableField("REMARK")
    private String remark;

    /**
     * 排序字段
     */
    @TableField("SORTED")
    private Long sorted;

    /**
     * ES后缀
     */
    @TableField("ES_SUFFIX")
    private String esSuffix;

    /**
     * 更新时间
     */
    @TableField("UPDATE_TIME")
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @TableField("CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 主数据表
     */
    @TableField("MASTER_TABLES")
    private String masterTables;

    @TableField("HOSPITAL_FLAG")
    private String hospitalFlag;

    /**
     * 是否合并(1表示合并, 0表示不合并)
     */
    @TableField("MERGE")
    private String merge;

    /**
     * 合并类型(1表示身份证,2表示身份证+病案号)
     */
    @TableField("MERGE_TYPE")
    private String mergeType;

    /**
     * 修改次数(0表示没有被修改,1表示已经修改过)
     */
    @TableField("MERGE_UPDATE_NUM")
    private String mergeUpdateNum;

    /**
     * 图标名称&颜色
     */
    @TableField("LOGO_NAME")
    private String logoName;


    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getAdvancesearchrequestId() {
        return advancesearchrequestId;
    }

    public void setAdvancesearchrequestId(String advancesearchrequestId) {
        this.advancesearchrequestId = advancesearchrequestId;
    }

    public String getEsIndex() {
        return esIndex;
    }

    public void setEsIndex(String esIndex) {
        this.esIndex = esIndex;
    }

    public String getEsType() {
        return esType;
    }

    public void setEsType(String esType) {
        this.esType = esType;
    }

    public String getFollowTableName() {
        return followTableName;
    }

    public void setFollowTableName(String followTableName) {
        this.followTableName = followTableName;
    }

    public String getProcessRemainTime() {
        return processRemainTime;
    }

    public void setProcessRemainTime(String processRemainTime) {
        this.processRemainTime = processRemainTime;
    }

    public String getProcessPercent() {
        return processPercent;
    }

    public void setProcessPercent(String processPercent) {
        this.processPercent = processPercent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getSorted() {
        return sorted;
    }

    public void setSorted(Long sorted) {
        this.sorted = sorted;
    }

    public String getEsSuffix() {
        return esSuffix;
    }

    public void setEsSuffix(String esSuffix) {
        this.esSuffix = esSuffix;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getMasterTables() {
        return masterTables;
    }

    public void setMasterTables(String masterTables) {
        this.masterTables = masterTables;
    }

    public String getHospitalFlag() {
        return hospitalFlag;
    }

    public void setHospitalFlag(String hospitalFlag) {
        this.hospitalFlag = hospitalFlag;
    }

    public String getMerge() {
        return merge;
    }

    public void setMerge(String merge) {
        this.merge = merge;
    }

    public String getMergeType() {
        return mergeType;
    }

    public void setMergeType(String mergeType) {
        this.mergeType = mergeType;
    }

    public String getMergeUpdateNum() {
        return mergeUpdateNum;
    }

    public void setMergeUpdateNum(String mergeUpdateNum) {
        this.mergeUpdateNum = mergeUpdateNum;
    }

    public String getLogoName() {
        return logoName;
    }

    public void setLogoName(String logoName) {
        this.logoName = logoName;
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }

    @Override
    public String toString() {
        return "TbDbResource{" +
        "db_id=" + dbId +
        ", dbName=" + dbName +
        ", advancesearchrequestId=" + advancesearchrequestId +
        ", esIndex=" + esIndex +
        ", esType=" + esType +
        ", followTableName=" + followTableName +
        ", processRemainTime=" + processRemainTime +
        ", processPercent=" + processPercent +
        ", status=" + status +
        ", remark=" + remark +
        ", sorted=" + sorted +
        ", esSuffix=" + esSuffix +
        ", updateTime=" + updateTime +
        ", createTime=" + createTime +
        ", masterTables=" + masterTables +
        ", hospitalFlag=" + hospitalFlag +
        ", merge=" + merge +
        ", mergeType=" + mergeType +
        ", mergeUpdateNum=" + mergeUpdateNum +
        ", logoName=" + logoName +
        "}";
    }
}
