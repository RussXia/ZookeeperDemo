package com.xzy.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.util.List;

/**
 * Created by RuzzZZ on 2017/1/10.
 */
@Slf4j
public class ZookeeperTest {

    private static final int TIME_OUT = 3000;
    private static final String HOST = "127.0.0.1:2183";
    public static void main(String[] args) throws Exception {
        ZooKeeper zookeeper = new ZooKeeper(HOST, TIME_OUT,new Watcher() {
            //设置watcher，watcher是一个一次性的触发器!!!!!
            public void process(WatchedEvent event) {
                //WatchedEvent存在keeperState和eventType两种可供监听的触发点，此处只监听删除结点动作，详情看WatchedEvent源码
                if (event.getType().equals(Event.EventType.NodeDeleted)) {
                    log.info("hello zookeeper");
                    log.info(String.format("hello event! type=%s, stat=%s, path=%s", event.getType(), event.getState(), event.getPath()));
                }
            }
        });

        List<String> list1 = zookeeper.getChildren("/",false);
        log.info("=========当前根目录下已存在的结点数===========");
        log.info(list1.toString());

        if(zookeeper.exists("/test", false) == null)
        {
            log.info("=========创建节点===========");
            zookeeper.create("/test", "znode1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        List<String> list2 = zookeeper.getChildren("/",false);
        log.info("=========当前根目录下已存在的结点数===========");
        log.info(list2.toString());

        log.info("=========修改节点的数据==========");
        String data = "zNode2";
        zookeeper.setData("/test", data.getBytes(), -1);

        log.info("========查看修改的节点是否成功=========");
        //设置watcher
        log.info(new String(zookeeper.getData("/test", true, null)));

        //如果有watcher监测删除结点操作，触发watcher的process操作
        log.info("=======删除节点==========");
        zookeeper.delete("/test", -1);

        log.info("==========查看节点是否被删除============");
        log.info("节点状态：" + zookeeper.exists("/test", false));

        zookeeper.close();
    }

}