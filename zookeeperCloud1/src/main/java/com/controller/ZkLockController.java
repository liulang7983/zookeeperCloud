package com.controller;

import com.alibaba.fastjson.JSONObject;
import com.clientUnit.CuratorClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author ming.li
 * @date 2023/3/6 10:59
 */
@RestController
@Slf4j
public class ZkLockController {

    /**
     * 卖10张票，要求全部卖出，不能超卖
     */
    private int tickets = 100;

    @Autowired
    private CuratorClientUtil curatorClientUtil;

    @Autowired
    private StringRedisTemplate template;

    @GetMapping("/zkLock")
    public Object testLock() throws Exception {

        String threadName = Thread.currentThread().getName();

        InterProcessMutex mutex = new InterProcessMutex(curatorClientUtil.getClient(), "/lock");
        try {
            //尝试获取锁，最长等待3s，超时放弃获取
            boolean lockFlag = mutex.acquire(3000, TimeUnit.SECONDS);

            //获取锁成功
            if (lockFlag) {
                log.info("当前的票数为: {}", tickets);

                Thread.sleep(1000);
                tickets--;
                log.info("售完后当前的票数为: {}", tickets);

            } else {
                log.info("{}---获取锁fail", threadName);
            }
        } catch (Exception e) {
            log.info("{}---获取锁异常", threadName);
        } finally {
            //释放锁
            mutex.release();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", "线程: " + threadName + "执行完成");
        return jsonObject.toString();
    }

    @GetMapping("/noLock")
    public Object noLock() throws Exception {

        String threadName = Thread.currentThread().getName();
        log.info("当前的票数为: {}", tickets);

        Thread.sleep(1000);
        tickets--;
        log.info("售完后当前的票数为: {}", tickets);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", "线程: " + threadName + "执行完成");
        return jsonObject.toString();
    }

    @RequestMapping("/noZkRedisLock")
    public Object noZkRedisLock(){
        int stock = Integer.parseInt(template.opsForValue().get("st"));
        if (stock > 0) {
            int realStock = stock - 1;
            template.opsForValue().set("st", realStock + ""); // jedis.set(key,value)
            System.out.println("扣减成功，剩余库存:" + realStock);
        } else {
            System.out.println("扣减失败，库存不足");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", "执行完成");
        return jsonObject.toString();
    }

    @RequestMapping("/zkRedisLock")
    public Object zkRedisLock() throws  Exception{
        String threadName = Thread.currentThread().getName();
        InterProcessMutex mutex = new InterProcessMutex(curatorClientUtil.getClient(), "/lock");
        try {
            //尝试获取锁，最长等待3s，超时放弃获取
            boolean lockFlag = mutex.acquire(3000, TimeUnit.SECONDS);
            //获取锁成功
            if (lockFlag) {
                int stock = Integer.parseInt(template.opsForValue().get("st"));
                if (stock > 0) {
                    int realStock = stock - 1;
                    template.opsForValue().set("st", realStock + ""); // jedis.set(key,value)
                    System.out.println("扣减成功，剩余库存:" + realStock);
                } else {
                    System.out.println("扣减失败，库存不足");
                }

            } else {
                log.info("{}---获取锁fail", threadName);
            }
        } catch (Exception e) {
            log.info("{}---获取锁异常", threadName);
        } finally {
            //释放锁
            mutex.release();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", "线程: " + threadName + "执行完成");
        return jsonObject.toString();
    }


}

