package client;

import java.util.List;

/**
 * 负载均衡策略接口
 */
public interface LoadBalancer {
    String select(List<String> serviceUrls);
}
