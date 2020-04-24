package com.djhu.common.aspect;

/**
 * @author cyf
 * @description
 * @create 2019-07-11 13:35
 **/
public enum LogTypeEnum {

    CREATE("创建"), UPDATE("更新"), COUNT("统计"), DELETE("删除"), SEARCH("查询"), SAVE("保存"),
    SETTING("设置"), CREATE_OR_UPDATE("创建/更新"), EXPORT("导出"), IMPORT("导入"), DEFAULT("操作");

    private String type;

    private LogTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
