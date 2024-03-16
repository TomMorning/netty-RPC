package client;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机负载均衡
 */
public class RandomLoadBalancer implements LoadBalancer {

    @Override
    public String select(List<String> serviceUrls) {
        int index = ThreadLocalRandom.current().nextInt(serviceUrls.size());
        return serviceUrls.get(index);
    }
}
