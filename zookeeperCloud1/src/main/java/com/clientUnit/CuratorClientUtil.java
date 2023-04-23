package com.clientUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Component
@Slf4j
public class CuratorClientUtil implements DisposableBean {

    @Value("${zookeeper.connect}")
    private String zookeeperConnect;

    /**
     * 销毁需要用到get方法
     */
    @Getter
    private CuratorFramework client;

    @PostConstruct
    public void init() {
        this.client = CuratorFrameworkFactory.builder().
                connectString(zookeeperConnect).
                sessionTimeoutMs(60 * 1000).
                connectionTimeoutMs(15 * 1000).
                retryPolicy(new ExponentialBackoffRetry(3000, 10))
                .build();
        this.client.start();
    }

    @Override
    public void destroy() throws Exception {
        try {
            if (Objects.nonNull(getClient())) {
                getClient().close();
            }
        } catch (Exception e) {
            log.info("CuratorFramework close error=>{}", e.getMessage());
        }
    }

}
