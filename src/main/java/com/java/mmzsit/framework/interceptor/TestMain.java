package com.java.mmzsit.framework.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.java.mmzsit.dto.TableColumnDTO;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @ClassName: TestMain.java
 * @Author: jinchuanchuan@longfor.com
 * @Date: 2022/1/11 11:15 下午:00
 * @Description: TODO
 */
public class TestMain {
    @Value("${oss.table.column.name:null}")
    private String tableNamesFieldJson;

    public static void main(String[] args) {
        TestMain testMain = new TestMain();
        System.out.println(testMain.test01());
        String tableNamesFieldJson = "[{\n" +
                "\t\t\"tableName\": \"user\",\n" +
                "\t\t\"columns\": [\"url\", \"name\", \"age\"]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"tableName\": \"log\",\n" +
                "\t\t\"columns\": [\"id\", \"logUrl\", \"level\"]\n" +
                "\t}\n" +
                "]";
        List<TableColumnDTO> tableColumnDTOList = JSONObject.parseObject(testMain.test01(), new TypeReference<List<TableColumnDTO>>() {
        });
        if (!CollectionUtils.isEmpty(tableColumnDTOList)) {
            tableColumnDTOList.get(0);
        }
        System.out.println("----------" + JSON.toJSONString(tableColumnDTOList));

        System.out.println(getTableName("select   *   from   user where", SqlCommandType.SELECT));
        System.out.println(getTableName("update   user   set where", SqlCommandType.UPDATE));
        System.out.println(getTableName("insert   into   user   ", SqlCommandType.INSERT));
    }

    private static String getTableName(String sql, SqlCommandType sqlCommandType) {
        try {
            if (sqlCommandType == SqlCommandType.INSERT) {
                String[] intoArray = sql.split("into");
                String[] intoTarget = intoArray[1].trim().split(" ");
                return intoTarget[0].trim();
            } else if(sqlCommandType == SqlCommandType.UPDATE) {
                String[] updateArray = sql.split("set");
                String[] updateTarget = updateArray[0].trim().split(" ");
                return updateTarget[updateTarget.length - 1].trim();
            } else if(sqlCommandType == SqlCommandType.SELECT) {
                String[] selectArray = sql.split("from");
                String[] selectTarget = selectArray[1].trim().split(" ");
                return selectTarget[0].trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String test01() {
        return tableNamesFieldJson;
    }
}
