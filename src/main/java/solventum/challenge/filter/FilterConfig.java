package solventum.challenge.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    //Applying to all endpoints for now
    @Bean
    public FilterRegistrationBean<RateLimiterFilter> rateLimiter(RateLimiterFilter filter) {
        FilterRegistrationBean<RateLimiterFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        // Applying to all endpoints
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
