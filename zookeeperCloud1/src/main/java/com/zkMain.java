package com;


import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author ming.li
 * @date 2023/3/6 11:14
 */
@SpringBootApplication
public class zkMain {
    public static void main(String[] args) {
        SpringApplication.run(zkMain.class,args);
    }

    @Bean
    public Redisson redisson() {
        // 此为单机模式
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379").setDatabase(0);
        return (Redisson) Redisson.create(config);
    }

}
