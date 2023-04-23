package com.controller;

import com.bean.User;
import com.clientUnit.CuratorClientUtil;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ming.li
 * @date 2023/4/21 20:33
 */
@RestController
@RequestMapping("/readAndWrite")
public class ReadAndWriteController {

    @Autowired
    private CuratorClientUtil curatorClientUtil;

    @RequestMapping("/read")
    public User read() throws Exception {
        InterProcessReadWriteLock readAndWrite = new InterProcessReadWriteLock(curatorClientUtil.getClient(), "/readAndWrite");
        InterProcessMutex mutex = readAndWrite.readLock();
        long start = System.currentTimeMillis();
        System.out.println("读锁");
        mutex.acquire();
        System.out.println("读锁加锁成功");
        Thread.sleep(5000);
        mutex.release();
        long end = System.currentTimeMillis();
        System.out.println("读锁释放锁成功,耗时："+(end-start));
        User user = new User();
        user.setName("read");
        return user;
    }

    @RequestMapping("/write")
    public User write() throws Exception {
        InterProcessReadWriteLock readAndWrite = new InterProcessReadWriteLock(curatorClientUtil.getClient(), "/readAndWrite");
        InterProcessMutex mutex = readAndWrite.writeLock();
        long start = System.currentTimeMillis();
        System.out.println("写锁");
        mutex.acquire();
        System.out.println("写锁加锁成功");
        Thread.sleep(5000);
        mutex.release();
        long end = System.currentTimeMillis();
        System.out.println("写锁释放锁成功,耗时："+(end-start));
        User user = new User();
        user.setName("write");
        return user;
    }
}
