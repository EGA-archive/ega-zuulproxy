package zuulserver;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import zuulserver.filters.MyZuulFilter;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableCaching
@EnableZuulProxy
public class ZuulServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ZuulServerApplication.class).web(true).run(args);
    }

    @Bean
    public MyZuulFilter myZuulFilter() {
        return new MyZuulFilter();
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("permissions");
    }

}
