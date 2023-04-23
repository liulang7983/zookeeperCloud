package com.clientUnit;

/**
 * @author ming.li
 * @date 2023/4/21 13:44
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConfigCenter1 {

    private final static  String CONNECT_STR="127.0.0.1:2181";

    private final static Integer  SESSION_TIMEOUT=30*1000;

    private static ZooKeeper zooKeeper=null;


    private static CountDownLatch countDownLatch=new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {



        zooKeeper=new ZooKeeper(CONNECT_STR, SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getType()== Event.EventType.None
                        && event.getState() == Event.KeeperState.SyncConnected){
                    log.info("连接已建立");
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();


        MyConfig myConfig = new MyConfig();
        myConfig.setKey("anykey");
        myConfig.setName("anyName");

        ObjectMapper objectMapper=new ObjectMapper();

        byte[] bytes = objectMapper.writeValueAsBytes(myConfig);

        Stat stat = zooKeeper.setData("/myconfig", bytes, -1);
        System.out.println(stat);


    }




}

