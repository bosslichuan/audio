package com.mv.conf;

import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * spring的视图解析配置,采用beetl引擎
 *
 * @author guanghan
 * @date 2018/7/18
 */
@Configuration
@ConditionalOnClass(BeetlGroupUtilConfiguration.class)
@ConditionalOnProperty(name = {"beetl.enabled"}, havingValue = "true", matchIfMissing = true)
public class BeetlConfig {

    @Value("${beetl.resource.root}")
    String resourceRoot;
    @Value("${beetl.resource.autocheck}")
    String resourceAutoCheck;
    @Value("${beetl.delimiterStatemnetStart}")
    String delimiterStatemnetStart;
    @Value("${beetl.delimiterStatemnetEnd}")
    String delimiterStatemnetEnd;

    @Bean(initMethod = "init")
    public BeetlGroupUtilConfiguration beetlGroupUtilConfiguration() {
        BeetlGroupUtilConfiguration beetlGroupUtilConfiguration = new BeetlGroupUtilConfiguration();
        Properties properties = new Properties();
//        properties.setProperty("RESOURCE.root", resourceRoot);
        properties.setProperty("RESOURCE.autoCheck", resourceAutoCheck);
        properties.setProperty("DELIMITER_STATEMENT_START", delimiterStatemnetStart.substring(1));
        properties.setProperty("DELIMITER_STATEMENT_END", delimiterStatemnetEnd);
        beetlGroupUtilConfiguration.setConfigProperties(properties);
        ClasspathResourceLoader classPathLoader = new ClasspathResourceLoader(this.getClass().getClassLoader(),
                resourceRoot);
        beetlGroupUtilConfiguration.setResourceLoader(classPathLoader);
        return beetlGroupUtilConfiguration;
    }

    @Bean
    public BeetlSpringViewResolver beetlSpringViewResolver() {
        BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
        beetlSpringViewResolver.setConfig(beetlGroupUtilConfiguration());
        beetlSpringViewResolver.setContentType("text/html;charset=UTF-8");
        beetlSpringViewResolver.setOrder(0);
        return beetlSpringViewResolver;
    }

}
