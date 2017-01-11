package com.xzy.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * Created by RuzzZZ on 2017/1/11.
 */
@Slf4j
public class ZookeeperWatcherDemo {

    public static void main(String[] args){
        WatchedEvent event = new WatchedEvent(Watcher.Event.EventType.NodeDeleted, Watcher.Event.KeeperState.SyncConnected,"/zk");
    }
}
