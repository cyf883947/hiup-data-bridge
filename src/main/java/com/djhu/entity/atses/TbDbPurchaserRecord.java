package com.djhu.entity.atses;


import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 数据接收厂商推送记录表
 * </p>
 *
 * @author cyf
 * @since 2020-04-27
 */
@TableName("TB_DB_PURCHASER_RECORD")
public class TbDbPurchaserRecord extends Model<TbDbPurchaserRecord> {

private static final long serialVersionUID=1L;

    @TableId(value = "ID",type = IdType.ID_WORKER)
    private String id;

    /**
     * 厂商id, 与表 tb_db_purchaser 的id关联
     */
    @TableField("PURCHASER_ID")
    private String purchaserId;

    @TableField("HIS_ID")
    private String hisId;

    @TableField("HIS_VISIT_ID")
    private String hisVisitId;

    @TableField("HIS_DOMAIN_ID")
    private String hisDomainId;

    @TableField("HIS_VISIT_DOMAIN_ID")
    private String hisVisitDomainId;

    /**
     * 专科库id
     */
    @TableField("DB_ID")
    private String dbId;

    /**
     * 专科库名称
     */
    @TableField("DB_NAME")
    private String dbName;

    /**
     * 耗时
     */
    @TableField("CONSUME")
    private String consume;

    @TableField("CREATEDATE")
    private LocalDateTime createdate;

    @TableField("UPDATEDATE")
    private LocalDateTime updatedate;

    /**
     * 推送状态 0-成功 1-失败
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

    public String getPurchaserId() {
        return purchaserId;
    }

    public void setPurchaserId(String purchaserId) {
        this.purchaserId = purchaserId;
    }

    public String getHisId() {
        return hisId;
    }

    public void setHisId(String hisId) {
        this.hisId = hisId;
    }

    public String getHisVisitId() {
        return hisVisitId;
    }

    public void setHisVisitId(String hisVisitId) {
        this.hisVisitId = hisVisitId;
    }

    public String getHisDomainId() {
        return hisDomainId;
    }

    public void setHisDomainId(String hisDomainId) {
        this.hisDomainId = hisDomainId;
    }

    public String getHisVisitDomainId() {
        return hisVisitDomainId;
    }

    public void setHisVisitDomainId(String hisVisitDomainId) {
        this.hisVisitDomainId = hisVisitDomainId;
    }

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

    public String getConsume() {
        return consume;
    }

    public void setConsume(String consume) {
        this.consume = consume;
    }

    public LocalDateTime getCreatedate() {
        return createdate;
    }

    public void setCreatedate(LocalDateTime createdate) {
        this.createdate = createdate;
    }

    public LocalDateTime getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(LocalDateTime updatedate) {
        this.updatedate = updatedate;
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
        return this.id;
    }

    @Override
    public String toString() {
        return "TbDbPurchaserRecord{" +
        "id=" + id +
        ", purchaserId=" + purchaserId +
        ", hisId=" + hisId +
        ", hisVisitId=" + hisVisitId +
        ", hisDomainId=" + hisDomainId +
        ", hisVisitDomainId=" + hisVisitDomainId +
        ", dbId=" + dbId +
        ", dbName=" + dbName +
        ", consume=" + consume +
        ", createdate=" + createdate +
        ", updatedate=" + updatedate +
        ", status=" + status +
        ", custom0=" + custom0 +
        ", custom1=" + custom1 +
        ", custom2=" + custom2 +
        "}";
    }
}
