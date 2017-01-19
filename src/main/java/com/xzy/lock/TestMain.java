package com.xzy.lock;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by RuzzZZ on 2017/1/19.
 */
@Slf4j
public class TestMain {

    public static void main(String[] args){
        Thread thread1 = new Thread(new LockThread());
        Thread thread2 = new Thread(new LockThread());
        Thread thread3 = new Thread(new LockThread());
        thread1.start();
        thread2.start();
        thread3.start();
    }

    static class LockThread implements Runnable{
        public void run() {
            for(int i = 0;i<10;i++){
                DistributedLock lock = new DistributedLock("myLock");
                lock.lock();
                log.warn("I'm get the Lock now! The current thread is :"+Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

}
