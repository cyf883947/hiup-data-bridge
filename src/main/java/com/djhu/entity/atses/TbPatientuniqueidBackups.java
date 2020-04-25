package com.djhu.entity.atses;


import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 科研-患者编号备份表
 * </p>
 *
 * @author cyf
 * @since 2020-04-24
 */
@TableName("TB_PATIENTUNIQUEID_BACKUPS")
public class TbPatientuniqueidBackups extends Model<TbPatientuniqueidBackups> {

private static final long serialVersionUID=1L;

    @TableField("ID")
    private String id;

    /**
     * 科研-患者编号
     */
    @TableField("PATIENT_UNIQUE_ID")
    private String patientUniqueId;

    @TableField("DB_ID")
    private String dbId;

    /**
     * 科研-患者编号-合并类型(1-根据身份证号合并 2- 根据病案号合并 3- 根据住院号或就诊号(his_id)合并
     */
    @TableField("CREATE_TYPE")
    private String createType;

    @TableField("HIS_ID")
    private String hisId;

    @TableField("ID_NO")
    private String idNo;

    @TableField("REDIS_KEY")
    private String redisKey;

    @TableField("REDIS_VALUE")
    private String redisValue;

    @TableField("REDIS_INFO_KEY")
    private String redisInfoKey;

    @TableField("REDIS_INFO_VALUE")
    private String redisInfoValue;

    @TableField("CREATE_TIME")
    private LocalDateTime createTime;

    @TableField("UPDATE_TIME")
    private LocalDateTime updateTime;

    /**
     * 推送状态 0-已推 1-未推
     */
    @TableField("STATUS")
    private String status;

    @TableField("CUSTOM0")
    private String custom0;

    @TableField("CUSTOM1")
    private String custom1;

    @TableField("CUSTOM2")
    private String custom2;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientUniqueId() {
        return patientUniqueId;
    }

    public void setPatientUniqueId(String patientUniqueId) {
        this.patientUniqueId = patientUniqueId;
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getCreateType() {
        return createType;
    }

    public void setCreateType(String createType) {
        this.createType = createType;
    }

    public String getHisId() {
        return hisId;
    }

    public void setHisId(String hisId) {
        this.hisId = hisId;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getRedisKey() {
        return redisKey;
    }

    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }

    public String getRedisValue() {
        return redisValue;
    }

    public void setRedisValue(String redisValue) {
        this.redisValue = redisValue;
    }

    public String getRedisInfoKey() {
        return redisInfoKey;
    }

    public void setRedisInfoKey(String redisInfoKey) {
        this.redisInfoKey = redisInfoKey;
    }

    public String getRedisInfoValue() {
        return redisInfoValue;
    }

    public void setRedisInfoValue(String redisInfoValue) {
        this.redisInfoValue = redisInfoValue;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustom0() {
        return custom0;
    }

    public void setCustom0(String custom0) {
        this.custom0 = custom0;
    }

    public String getCustom1() {
        return custom1;
    }

    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    public String getCustom2() {
        return custom2;
    }

    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }

    @Override
    public String toString() {
        return "TbPatientuniqueidBackups{" +
        "id=" + id +
        ", patientUniqueId=" + patientUniqueId +
        ", db_id=" + dbId +
        ", createType=" + createType +
        ", hisId=" + hisId +
        ", idNo=" + idNo +
        ", redisKey=" + redisKey +
        ", redisValue=" + redisValue +
        ", redisInfoKey=" + redisInfoKey +
        ", redisInfoValue=" + redisInfoValue +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", status=" + status +
        ", custom0=" + custom0 +
        ", custom1=" + custom1 +
        ", custom2=" + custom2 +
        "}";
    }
}
