package com.djhu.entity.atses;


import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 数据接收厂商表
 * </p>
 *
 * @author cyf
 * @since 2020-04-24
 */
@TableName("TB_DB_PURCHASER")
public class TbDbPurchaser extends Model<TbDbPurchaser> {

private static final long serialVersionUID=1L;

    @TableId("ID")
    private String id;

    /**
     * 厂商名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 厂商类型
     */
    @TableField("TYPE")
    private String type;

    @TableField("CREATETIME")
    private LocalDateTime createtime;

    @TableField("UPDATETIME")
    private LocalDateTime updatetime;

    /**
     * 厂商状态 - 是否启用  0-启用 1-禁用
     */
    @TableField("STATUS")
    private String status;

    /**
     * 厂商地址 请求url
     */
    @TableField("CUSTOM0")
    private String custom0;

    /**
     * 备用字段2
     */
    @TableField("CUSTOM1")
    private String custom1;

    /**
     * 备用字段3
     */
    @TableField("CUSTOM2")
    private String custom2;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public LocalDateTime getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(LocalDateTime updatetime) {
        this.updatetime = updatetime;
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
        return "TbDbPurchaser{" +
        "id=" + id +
        ", name=" + name +
        ", type=" + type +
        ", createtime=" + createtime +
        ", updatetime=" + updatetime +
        ", status=" + status +
        ", custom0=" + custom0 +
        ", custom1=" + custom1 +
        ", custom2=" + custom2 +
        "}";
    }
}
