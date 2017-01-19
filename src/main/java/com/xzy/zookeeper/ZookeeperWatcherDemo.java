package com.xzy.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by RuzzZZ on 2017/1/11.
 */
@Slf4j
public class ZookeeperWatcherDemo {

    private static final int TIME_OUT = 3000;
    private static final String HOST = "localhost:2181";
    public static void main(String[] args) throws  Exception {
        ZooKeeper zookeeper = new ZooKeeper(HOST, TIME_OUT, new Watcher() {
            //设置watcher，watcher是一个一次性的触发器!!!!!
            public void process(WatchedEvent event) {
                //WatchedEvent存在keeperState和eventType两种可供监听的触发点，此处只监听删除结点动作，详情看WatchedEvent源码
                if(event.getType().equals(Event.EventType.NodeDeleted)){
                    log.info("hello zookeeper");
                    log.info(String.format("hello event! type=%s, stat=%s, path=%s", event.getType(), event.getState(), event.getPath()));
                }
            }
        });
    }
}
