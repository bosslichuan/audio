package com.mv.conf;

import com.gargoylesoftware.htmlunit.WebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author guanghan
 * @date 2018/11/2
 */
@Configuration
public class CommonConfig {
    @Bean
    public WebClient chrome(){
        return new WebClient();
    }
}
