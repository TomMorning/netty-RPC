package client;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private AtomicInteger position = new AtomicInteger(0);

    @Override
    public String select(List<String> serviceUrls) {
        int pos = Math.abs(position.getAndIncrement() % serviceUrls.size());
        return serviceUrls.get(pos);
    }
}
