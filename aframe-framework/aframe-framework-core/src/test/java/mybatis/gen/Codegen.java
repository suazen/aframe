package mybatis.gen;

import com.alibaba.druid.pool.DruidDataSource;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.UpdateListener;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.TableConfig;
import me.suazen.aframe.framework.core.mybatisflex.listener.DefaultInsertListener;
import me.suazen.aframe.framework.core.mybatisflex.listener.DefaultUpdateListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sujizhen
 * @date 2023-04-23
 **/
public class Codegen {
    private static final String DB_URL = "jdbc:mysql://121.37.3.24:3306/aframe?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin123";

    private static final String BASE_PACKAGE = "me.suazen.aframe.";
    private static final String MODULE_SYSTEM_CORE = "aframe-system/aframe-system-core";
    private static final String PACKAGE_SYSTEM_CORE = BASE_PACKAGE+"system.core";

    private static final String[] tables = new String[]{"sys_user"};

    public static void main(String[] args) {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        Map<String,String> pkMap = new HashMap<>();
        pkMap.put("sys_user","user_id");
        setKeyType(globalConfig,pkMap);

        gen(globalConfig);
    }


    //-----------------DO NOT MODIFY--------------------//
    private static void setKeyType(GlobalConfig globalConfig,
                                   Map<String, String> pkMap,
                                   KeyType keyType,
                                   String keyValue,
                                   Class<? extends InsertListener> insertClass,
                                   Class<? extends UpdateListener> updateClass){
        pkMap.forEach((table,pk)->{
            //可以单独配置某个列
            ColumnConfig columnConfig = new ColumnConfig();
            columnConfig.setColumnName(pk);
            columnConfig.setKeyType(keyType);
            columnConfig.setKeyValue(keyValue);

            TableConfig tableConfig = new TableConfig();
            tableConfig.setTableName(table);
            tableConfig.setInsertListenerClass(DefaultInsertListener.class);
            tableConfig.setUpdateListenerClass(DefaultUpdateListener.class);
            tableConfig.addColumnConfig(columnConfig);
            globalConfig.addTableConfig(tableConfig);
        });
    }

    private static void setKeyType(GlobalConfig globalConfig,
                                   Map<String, String> pkMap,
                                   KeyType keyType,
                                   String keyValue){
        setKeyType(globalConfig,pkMap,keyType,keyValue, DefaultInsertListener.class, DefaultUpdateListener.class);
    }

    private static void setKeyType(GlobalConfig globalConfig,
                                   Map<String, String> pkMap){
        setKeyType(globalConfig,pkMap,KeyType.Generator,"uuid");
    }

    private static void gen(GlobalConfig globalConfig){
        //配置数据源
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(DB_URL);
        dataSource.setUsername(DB_USER);
        dataSource.setPassword(DB_PASSWORD);

        globalConfig.setSourceDir(MODULE_SYSTEM_CORE+"/src/main/java");
        //设置只生成哪些表
        globalConfig.addGenerateTable(tables);
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

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();
    }

}
