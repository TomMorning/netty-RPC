package client;

import org.apache.zookeeper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 服务发现
 */
public class ServiceDiscovery {

    private static final String REGISTRY_PATH = "/registry";
    private String zkServers;
    private ZooKeeper zooKeeper;
    private List<String> urlList = new ArrayList<>();
    private LoadBalancer loadBalancer;


    public ServiceDiscovery(String zkServers, LoadBalancer loadBalancer) {
        this.zkServers = zkServers;
        this.loadBalancer = loadBalancer;
        connect();
    }

    private void connect() {
        try {
            this.zooKeeper = new ZooKeeper(zkServers, 2000, this::watcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void watcher(WatchedEvent event) {
        if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
            fetchServiceUrl();
        }
    }

    public void fetchServiceUrl() {
        try {
            List<String> nodeList = zooKeeper.getChildren(REGISTRY_PATH, this::watcher);
            List<String> urls = new ArrayList<>();
            for (String node : nodeList) {
                byte[] bytes = zooKeeper.getData(REGISTRY_PATH + "/" + node, false, null);
                urls.add(new String(bytes));
            }
            this.urlList = urls;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String discover() {
        fetchServiceUrl();
        return loadBalancer.select(urlList);
    }
}
