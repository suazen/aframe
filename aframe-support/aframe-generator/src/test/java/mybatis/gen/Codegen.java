package mybatis.gen;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author sujizhen
 * @date 2023-04-23
 **/
public class Codegen {
    private static final String DB_URL = "jdbc:mysql://43.136.57.159:7780/aframe?useUnicode=true";
    private static final String DB_USER = "aframe";
    private static final String DB_PASSWORD = "aframeSuper123";

    public static void main(String[] args) {


        FastAutoGenerator.create(DB_URL,DB_USER,DB_PASSWORD)
                // 全局配置
                .globalConfig((scanner, builder) -> builder.author("sujizhen").outputDir(".\\"+scanner.apply("请输入模块名？")+"\\src\\main\\java"))
                // 包配置
                .packageConfig((scanner, builder) -> builder.parent("me.suazen.aframe."+scanner.apply("请输入包名？me.suazen.aframe.")))
                // 策略配置
                .strategyConfig((scanner, builder) -> builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                        .entityBuilder()
                        .enableFileOverride()
                        .enableLombok()
                        .addTableFills(
                                new Column("create_time", FieldFill.INSERT),
                                new Column("update_time",FieldFill.INSERT_UPDATE)
                        )
                        .mapperBuilder()
                        .superClass(BaseMapper.class)
                        .build())
                .execute();
    }

    // 处理 all 情况
    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }

}
