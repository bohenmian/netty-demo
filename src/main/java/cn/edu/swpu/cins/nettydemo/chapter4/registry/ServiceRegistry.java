package cn.edu.swpu.cins.nettydemo.chapter4.registry;

import cn.edu.swpu.cins.nettydemo.chapter4.config.RpcProperties;
import cn.edu.swpu.cins.nettydemo.chapter4.constant.RpcConstant;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

// 实现服务注册
public class ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private RpcProperties properties;

    @Autowired
    public ServiceRegistry(RpcProperties properties) {
        this.properties = properties;
    }

    public void registry(String data) {
        if (data != null) {
            ZooKeeper zk = connectServer();
            if (zk != null) {
                AddRootNode(zk);
                createNode(zk, data);
            }
        }
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(properties.getRegistryAddress(), RpcConstant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("", e);
        }
        return zk;
    }

    private void AddRootNode(ZooKeeper zk) {
        try {
            Stat s = zk.exists(RpcConstant.ZK_DATA_PATH, false);
            if (s == null) {
                zk.create(RpcConstant.ZK_REGISTRY_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (InterruptedException | KeeperException e) {
            LOGGER.error(e.toString());
        }
    }

    private void createNode(ZooKeeper zk, String data) {
        try {
            byte[] bytes = data.getBytes();
            String path = zk.create(RpcConstant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            LOGGER.debug("create zookeeper node ({} => {})", path, data);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }
    }

}
