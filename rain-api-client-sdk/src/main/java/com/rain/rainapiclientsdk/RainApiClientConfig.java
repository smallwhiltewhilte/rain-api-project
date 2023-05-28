package com.rain.rainapiclientsdk;

import com.rain.rainapiclientsdk.client.RainApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties("rainapi.client")
@Data
@ComponentScan
public class RainApiClientConfig {
    private String accessKey;
    private String secretKey;
    @Bean
    public RainApiClient rainApiClient(){
        return new RainApiClient(accessKey, secretKey);
    }
}
