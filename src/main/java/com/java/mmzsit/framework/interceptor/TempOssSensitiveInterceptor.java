package com.java.mmzsit.framework.interceptor;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Stream;

/**
 * @ClassName: OssMybatisInterceptor.java
 * @Author: jinchuanchuan@longfor.com
 * @Date: 2022/1/7 8:14 下午:00
 * @Description: OSS数据脱敏
 */
//@Intercepts({
//        @Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class}),
//        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class}),
//})
@Slf4j(topic="脱敏数据拦截器【OssSensitiveInterceptor】")
public class TempOssSensitiveInterceptor implements Interceptor {

//    @Resource
//    private AliyunOssClient aliyunOssClient;

    @Value("#{'${oss.column.names:url}'.split(',')}")
    private List<String> columnNames = Arrays.asList("id","name","age"); // 精确列名

    @Value("${oss.url.expiration:5}")
    private Long expiration; // oss有效期默认5分钟

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 对请求参数进行截取
        if (invocation.getTarget() instanceof StatementHandler) {
            System.out.println("class StatementHandler");
        }
        if (invocation.getTarget() instanceof ParameterHandler) {
            // 获取参数
            ParameterHandler parameterHandler = (ParameterHandler) invocation.getTarget();
            // 获取MetaObject
            MetaObject metaObject = SystemMetaObject.forObject(parameterHandler);
            // 通过MetaObject反射的内容获取MappedStatement
            MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("mappedStatement");
            if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT || mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE) {
                SqlSource sqlSource = mappedStatement.getSqlSource();
                BoundSql boundSql = sqlSource.getBoundSql(invocation.getArgs()[0]);
                String sql = boundSql.getSql();
                // TODO 截取表名
                if (sql.contains("")) {
                    // 获取参数字段
                    Field parameterField = parameterHandler.getClass().getDeclaredField("parameterObject");
                    parameterField.setAccessible(true);
                    // 获取实际入参对象
                    Object parameterObject = parameterField.get(parameterHandler);
                    if (Objects.nonNull(parameterObject)) {
                        Class<?> parameterObjectClass = parameterObject.getClass();
                        Field[] declaredFields = parameterObjectClass.getDeclaredFields();
                        for (Field field : declaredFields) {
                            // 实际入参对象字段是否需要截取（去掉URL中？后面的内容）
//                            OssSensitive fieldAnnotation = field.getAnnotation(OssSensitive.class);
//                            if (columnNames.contains(field)) {
                            if (true) {
                                field.setAccessible(true);
                                String name = field.getName();
                                Object paramValue = field.get(name);
//                        if (this.aliyunOssClient != null && Objects.nonNull(paramValue)) {
//                            field.set(parameterObject, this.aliyunOssClient.subOssUrl((String)paramValue));
//                        }
                                System.out.println("ParameterHandler进行参数截取了...............");
                            }
                        }
                    }

                }
            }


            return invocation.proceed();
        }

        // 对返回结果集进行OSS脱敏及时效处理
        if (invocation.getTarget() instanceof ResultSetHandler) {
            ResultSetHandler resultSetHandler = (ResultSetHandler) invocation.getTarget();
            MetaObject metaObject = SystemMetaObject.forObject(resultSetHandler);

            // 通过MetaObject反射的内容获取MappedStatement
            MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("mappedStatement");
            if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
                SqlSource sqlSource = mappedStatement.getSqlSource();
                BoundSql boundSql = sqlSource.getBoundSql(invocation.getArgs()[0]);
                String sql = boundSql.getSql();
                // TODO 截取表名
                if (sql.contains("")) {
                    List<Object> records = (List<Object>) invocation.proceed();
                    // 对结果集脱敏
                    records.forEach(this::sensitive);
                    return records;
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof ParameterHandler || target instanceof ResultSetHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

//    @Override
//    public Object plugin(Object target) {
//        return Interceptor.super.plugin(target);
//    }
//
//    @Override
//    public void setProperties(Properties properties) {
//        Interceptor.super.setProperties(properties);
//    }

    private void sensitive(Object source) {
        List<String> columnName = new ArrayList<>();
        columnName.add("id");
        columnName.add("name");
        columnName.add("age");
        // 拿到返回值类型
        Class<?> sourceClass = source.getClass();
        // 初始化返回值类型的 MetaObject
        MetaObject metaObject = SystemMetaObject.forObject(source);
        // 捕捉到属性上的标记注解 @OssSensitive 并进行对应处理
        Field[] declaredFields = sourceClass.getDeclaredFields();
        log.info("sensitive 对应字段：...............{}", JSON.toJSON(declaredFields));
        Stream.of(sourceClass.getDeclaredFields())
                .filter(field -> columnName.contains(field.getName()))
                .forEach(field -> doSensitive(metaObject, field));
    }

    private void doSensitive(MetaObject metaObject, Field field) {
        // 拿到属性名
        String name = field.getName();
        // 获取属性值
        Object value = metaObject.getValue(name);
        // 不为空才脱敏
        if (Objects.nonNull(value)) {
            // 把脱敏后的值塞回去
            // metaObject.setValue(name, this.aliyunOssClient.generatePreSignedUrl(value.toString(), expiration));
            log.info("ResultHandler对结果进行处理了...............");
        }
    }
}
