package me.suazen.aframe.framework.core.mybatisflex;

import com.alibaba.druid.pool.DruidDataSource;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;

/**
 * @author sujizhen
 * @date 2023-04-23
 **/
public class Codegen {
    private static final String BASE_PACKAGE = "me.suazen.aframe.";
    private static final String MODULE_SYSTEM_CORE = "aframe-system/system-core";
    private static final String PACKAGE_SYSTEM_CORE = BASE_PACKAGE+"system.core";

    public static void main(String[] args) {

        //配置数据源
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://121.37.3.24:3306/aframe?useUnicode=true&characterEncoding=utf-8&useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("admin123");

        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setSourceDir(MODULE_SYSTEM_CORE+"/src/main/java");
        //设置只生成哪些表
        globalConfig.addGenerateTable("sys_user");

        //设置 entity 的包名
        globalConfig.setEntityPackage(PACKAGE_SYSTEM_CORE+".entity");

        //设置 mapper 类的包名
        globalConfig.setMapperPackage(PACKAGE_SYSTEM_CORE+".mapper");
        //设置表前缀
        //globalConfig.setTablePrefix("tb_");

        //设置 entity 是否使用 Lombok
        globalConfig.setEntityWithLombok(true);

        //是否生成 mapper 类，默认为 false
        globalConfig.setMapperGenerateEnable(true);


        //可以单独配置某个列
        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setColumnName("user_id");
        columnConfig.setKeyType(KeyType.Generator);
        columnConfig.setKeyValue("uuid");
        globalConfig.addColumnConfig("sys_user", columnConfig);

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();
    }
}
