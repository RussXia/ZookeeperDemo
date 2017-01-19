package com.xzy.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by RuzzZZ on 2017/1/19.
 * <p>
 * 基于zookeeper实现的分布式锁，原理如下:
 * 在zookeeper的/lock节点下创建一个EPHEMERAL_SEQUENTIAL的自增节点序列。
 * 每个节点在前一个节点上设置watcher，当前一个节点被删除时，判断自己是否是当前最小节点，
 * 如果是，则获取锁。
 */
@Slf4j
public class DistributedLock implements Lock, Watcher {

    private ZooKeeper zooKeeper;

    private static final String ROOT_PATH = "/lock";//根节点

    private static final Long SESSION_TIMEOUT = 10000L;

    private static final String HOST_ADDRESS = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";

    private CountDownLatch flag = null;//判断其他线程是否释放了锁

    private String id;

    private String lockName;

    private String currentNodeName;//当前锁

    private String preNodeName;//前一个锁

    public DistributedLock(String lockName) {
        this.lockName = lockName;
        try {
            zooKeeper = new ZooKeeper(HOST_ADDRESS, SESSION_TIMEOUT.intValue(), this);
            Stat stat = zooKeeper.exists(ROOT_PATH, false);
            //如果不存在根节点，则创建一个
            if (stat == null) {
                zooKeeper.create(ROOT_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            throw new DistributedLockException(e);
        } catch (InterruptedException e) {
            throw new DistributedLockException(e);
        } catch (KeeperException e) {
            throw new DistributedLockException(e);
        }
        log.info("Init class DistributedLock over!");
    }

    /**
     * 实现:
     * 1.判断自己是不是当前最小节点，如果是，则认为是持有当前锁
     * 2.如果不是当前最小节点，则当前线程继续等待锁
     */
    public void lock() {
        if (this.tryLock()) {
            log.info("Success get the lock!");
            return;
        } else {
            waitForLock(preNodeName, SESSION_TIMEOUT);
        }
    }

    public void lockInterruptibly() throws InterruptedException {
        this.lock();
    }

    /**
     * 实现:
     * 1.创建一个临时节点
     * 2.取出所有根目录下的所有子节点
     * 3.如果当前临时节点是最小节点，则返回true
     * 4.如果当前节点不是最小节点，则找到它的前一个节点，返回false
     *
     * @return
     */
    public boolean tryLock() {
        try {
            //1.创建一个临时节点
            currentNodeName = zooKeeper.create(ROOT_PATH + "/" + lockName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            //2.获取当前根目录下的所有子节点
            List<String> allSubNodes = zooKeeper.getChildren(ROOT_PATH, false);
            Collections.sort(allSubNodes);
            //3.判断当前临时节点是否是最小节点，如果是返回true
            if (currentNodeName.equals(allSubNodes.get(0))) {
                return true;
            }
            //如果当前节点不是最小节点，找到自己的前一个节点，返回false
            preNodeName = allSubNodes.get(Collections.binarySearch(allSubNodes, currentNodeName) - 1);
        } catch (KeeperException e) {
            throw new DistributedLockException(e);
        } catch (InterruptedException e) {
            throw new DistributedLockException(e);
        }
        return false;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if (this.tryLock()) {
            return true;
        }
        waitForLock(preNodeName, time);
        return false;
    }

    public void unlock() {
        // TODO: 2017/1/19
    }

    public Condition newCondition() {
        return null;
    }

    public void process(WatchedEvent event) {
        // TODO: 2017/1/19  

    }

    private boolean waitForLock(String preNode, Long waitTime) {
        // TODO: 2017/1/19
        return false;
    }
}
