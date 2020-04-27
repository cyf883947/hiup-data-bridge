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
 * @since 2020-04-24
 */
@TableName("TB_DB_PURCHASER_RECORD")
public class TbDbPurchaserRecord extends Model<TbDbPurchaserRecord> {

private static final long serialVersionUID=1L;

    @TableId(value = "ID",type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 当前数据库db_id 与scient_per.tb_db_resource 的db_id 关联
     */
    @TableField("BAK_ID")
    private String bakId;

    /**
     * 对外提供查询db_id, 暂时和当前数据库db_id 保持一致
     */
    @TableField("EXTERNAL_DB_ID")
    private String externalDbId;

    /**
     * 厂商id, 与表 tb_db_purchaser 的id关联
     */
    @TableField("PURCHASER_ID")
    private String purchaserId;

    /**
     * 推送数据总数
     */
    @TableField("TOTAL")
    private String total;

    /**
     * 推送数据库index
     */
    @TableField("ES_INDEX")
    private String esIndex;

    /**
     * 推送数据库type
     */
    @TableField("ES_TYPE")
    private String esType;

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

    /**
     * 重试次数
     */
    @TableField("RETRY_COUNT")
    private String retryCount;

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

    public String getBakId() {
        return bakId;
    }

    public void setBakId(String bakId) {
        this.bakId = bakId;
    }

    public String getExternalDbId() {
        return externalDbId;
    }

    public void setExternalDbId(String externalDbId) {
        this.externalDbId = externalDbId;
    }

    public String getPurchaserId() {
        return purchaserId;
    }

    public void setPurchaserId(String purchaserId) {
        this.purchaserId = purchaserId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
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

    public String getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(String retryCount) {
        this.retryCount = retryCount;
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
        ", bakId=" + bakId +
        ", externalDbId=" + externalDbId +
        ", purchaserId=" + purchaserId +
        ", total=" + total +
        ", esIndex=" + esIndex +
        ", esType=" + esType +
        ", consume=" + consume +
        ", createdate=" + createdate +
        ", updatedate=" + updatedate +
        ", status=" + status +
        ", retryCount=" + retryCount +
        ", custom0=" + custom0 +
        ", custom1=" + custom1 +
        ", custom2=" + custom2 +
        "}";
    }
}
