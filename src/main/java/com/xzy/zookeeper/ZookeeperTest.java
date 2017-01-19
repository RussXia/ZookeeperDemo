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
    private static final String HOST = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    public static void main(String[] args) throws Exception {
        ZooKeeper zookeeper = new ZooKeeper(HOST, TIME_OUT,new Watcher() {
            //设置watcher，watcher是一个一次性的触发器!!!!!
            public void process(WatchedEvent event) {
                //WatchedEvent存在keeperState和eventType两种可供监听的触发点，此处只监听删除结点动作，详情看WatchedEvent源码
                if (event.getType().equals(Event.EventType.NodeDeleted)) {
                    log.info("Node deleted!");
                    log.info(String.format("hello event! type=%s, stat=%s, path=%s", event.getType(), event.getState(), event.getPath()));
                }
                if(event.getState().equals(Event.KeeperState.Disconnected)){
                    log.info("Disconnected from zookeeper!");
                    log.info(String.format("hello event! type=%s, stat=%s, path=%s", event.getType(), event.getState(), event.getPath()));
                }
            }
        });

        List<String> list1 = zookeeper.getChildren("/",false);
        log.info("=========当前根目录下已存在的结点数===========");
        log.info(list1.toString());

        /**
         * SEQUENTIAL:
         * 创建sequence节点时, ZooKeeper server会在指定的节点名称后加上一个数字序列, 该数字序列是递增的.
         */

        /**
         * ephemeralOwner = 0x0
         * 持久化状态节点(PERSISTENT_SEQUENTIAL/PERSISTENT):
         * 节点不与特定的session相绑定，节点不随session的结束而删除。除非显示删除，否则节点一直存在
         * 持久化节点的ephemeralOwner值恒为0！！！
         */
        log.info("=========创建PERSISTENT_SEQUENTIAL节点===========");
        zookeeper.create("/test", "znode1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);

        if(zookeeper.exists("/test", false) == null)
        {
            log.info("=========创建PERSISTENT节点===========");
            zookeeper.create("/test", "znode1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        /**
         * ephemeralOwner = 0x159a655f6cc000c
         * 持久化状态节点(EPHEMERAL_SEQUENTIAL/EPHEMERAL):
         * 节点随session的结束而自动删除。ephemeral节点不能拥有子节点
         * 在ephemeral节点被删除之前，其他session也都可以访问该节点
         * ephemeralOwner的值表示与该节点绑定的sessionId
         */
        log.info("=========创建EPHEMERAL_SEQUENTIAL节点===========");
        String str = zookeeper.create("/ephemeral", "znode1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        log.info("========="+str+"========");

        if(zookeeper.exists("/ephemeral", false) == null)
        {
            log.info("=========创建EPHEMERAL节点===========");
            zookeeper.create("/ephemeral", "znode1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
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
//        log.info("=======删除节点==========");
//        zookeeper.delete("/test", -1);

        log.info("==========查看节点是否被删除============");
        log.info("节点状态：" + zookeeper.exists("/test", false));

        zookeeper.close();
    }

}