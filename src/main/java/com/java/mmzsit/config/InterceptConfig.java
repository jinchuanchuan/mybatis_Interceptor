package com.java.mmzsit.config;

import com.java.mmzsit.framework.interceptor.OssSensitiveInterceptor;
import com.java.mmzsit.framework.interceptor.TableSplitInterceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @author ：mmzsit
 * @description：
 * @date ：2019/6/14 10:08
 */
@Configuration
public class InterceptConfig {

    /**
     * 脱敏数据拦截器
     * @return
     */
    @Bean
    public OssSensitiveInterceptor initOssSensitiveInterceptor() {
        return new OssSensitiveInterceptor();
    }

    /**
     * mybatis分表拦截器
     * @return
     */
//    @Bean
    public TableSplitInterceptor initTableSplitInterceptor() {
        return new TableSplitInterceptor();
    }
}
