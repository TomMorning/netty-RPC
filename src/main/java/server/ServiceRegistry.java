package server;

import org.apache.zookeeper.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务注册
 */
public class ServiceRegistry {

    private Map<String, Object> serviceMap = new HashMap<>();

    public void register(String serviceName, Object serviceInstance) {
        serviceMap.put(serviceName, serviceInstance);
    }

    public Object getService(String serviceName) {
        return serviceMap.get(serviceName);
    }

//    private static final String REGISTRY_PATH = "/registry";
//    private final String zkServers;
//    private ZooKeeper zooKeeper;

//    public ServiceRegistry(String zkServers) {
//        this.zkServers = zkServers;
//        try {
//            connect();
//        } catch (Exception e) {
//            System.out.println("连接 ZooKeeper 服务器失败");
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void connect() throws Exception {
//        this.zooKeeper = new ZooKeeper(zkServers, 2000, event -> {});
//    }
//
//    public void register(String serviceName, String serviceAddress) throws Exception {
//        if (zooKeeper.exists(REGISTRY_PATH, false) == null) {
//            zooKeeper.create(REGISTRY_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//        }
//
//        String servicePath = REGISTRY_PATH + "/" + serviceName;
//        if (zooKeeper.exists(servicePath, false) == null) {
//            zooKeeper.create(servicePath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//        }
//
//        String addressPath = servicePath + "/address-";
//        zooKeeper.create(addressPath, serviceAddress.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
//    }
}
