package com.xzy.lock;

/**
 * 分布式锁的自定义异常类
 *
 * Created by RuzzZZ on 2017/1/19.
 */
public class DistributedLockException extends RuntimeException {

    private static final long serialVersionUID = 125324798392L;

    public DistributedLockException(String message) {
        super(message);
    }

    public DistributedLockException(Exception cause){
        super(cause);
    }
}
