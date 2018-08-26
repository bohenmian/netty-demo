package cn.edu.swpu.cins.nettydemo;

import cn.edu.swpu.cins.nettydemo.chapter4.config.RpcProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RpcProperties.class)

public class NettyDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyDemoApplication.class, args);
    }
}
