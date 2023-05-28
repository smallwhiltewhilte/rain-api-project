package com.rain.rainapigateway;

//import com.rain.rainapigateway.feignclient.UserInterfaceInfoClient;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@EnableDubbo
//@EnableFeignClients
public class RainApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(RainApiGatewayApplication.class, args);
    }

    //    @Bean
//    @LoadBalanced
//    public RestTemplate restTemplate(){
//        return new RestTemplate();
//    }
//    @Bean
//    @ConditionalOnMissingBean
//    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
//        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
//    }

}
