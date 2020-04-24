package com.djhu.common.constant;

/**
 * @author cyf
 * @description
 * @create 2020-04-24 13:42
 **/
public class ProjectConstant {

    public static final String BASE_PACKAGE = "com.djhu";//生成代码所在的基础包名称，可根据自己公司的项目修改（注意：这个配置修改之后需要手工修改src目录项目默认的包路径，使其保持一致，不然会找不到类）

    public static final String CONFIG_LOCATION = "classpath:mybatis-config.xml";

    public static final String ATS_ES = "atses";
    public static final String SCIENT_PER = "scientper";

    // ATS_ES 数据库
    public static final String ATS_ES_MODEL_PACKAGE = BASE_PACKAGE + ".entity"+"."+ATS_ES;//生成的Model所在包
    public static final String ATS_ES_MAPPER_PACKAGE = BASE_PACKAGE + ".mapper"+"."+ATS_ES;//生成的Mapper所在包
    public static final String ATS_ES_MAPPER_LOCATION = "classpath:mapper/"+ATS_ES+"/*.xml";

    // SCIENT_PER 数据库
    public static final String SCIENT_PER_MODEL_PACKAGE = BASE_PACKAGE + ".entity"+"."+SCIENT_PER;//生成的Model所在包
    public static final String SCIENT_PER_MAPPER_PACKAGE = BASE_PACKAGE + ".mapper"+"."+SCIENT_PER;//生成的Mapper所在包
    public static final String SCIENT_PER_MAPPER_LOCATION = "classpath:mapper/"+SCIENT_PER+"/*.xml";
}
