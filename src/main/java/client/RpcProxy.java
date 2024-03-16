package client;

import common.RpcRequest;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * 动态代理生成服务代理
 */
public class RpcProxy {

    private RpcClient client;

    public RpcProxy(RpcClient client) {
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class<?>[]{serviceClass},
                (proxy, method, args) -> {
                    RpcRequest request = new RpcRequest();
                    request.setRequestId(UUID.randomUUID().toString());
                    request.setClassName(method.getDeclaringClass().getName());
                    request.setMethodName(method.getName());
                    request.setParameterTypes(method.getParameterTypes());
                    request.setParameters(args);

                    return client.send(request);
                });
    }
}
