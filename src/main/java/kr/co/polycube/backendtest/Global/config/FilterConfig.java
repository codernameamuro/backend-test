package kr.co.polycube.backendtest.Global.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<checkSpecialCharFilter> specialCharacterFilter() {
        FilterRegistrationBean<checkSpecialCharFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new checkSpecialCharFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
