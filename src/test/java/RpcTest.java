import client.RpcClient;
import client.RpcProxy;
import common.RpcRequest;
import org.junit.jupiter.api.Test;
import server.MyService;
import server.MyServiceImpl;
import server.RpcServer;
import server.ServiceRegistry;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RpcTest {

    @Test
    public void testRpcCommunication() throws Exception {
        // 启动 RPC 服务器
//        ServiceRegistry serviceRegistry = new ServiceRegistry("127.0.0.1:2181"); // 使用实际的 ZooKeeper 服务器地址
        ServiceRegistry serviceRegistry = new ServiceRegistry();
        RpcServer server = new RpcServer(8888, serviceRegistry);
        new Thread(() -> {
            try {
                server.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(1000); // 等待服务器启动

        // 使用 RPC 客户端调用服务
        RpcClient client = new RpcClient("localhost", 8888);
        RpcProxy proxy = new RpcProxy(client);
        MyService service = proxy.create(MyService.class);

        String result = service.sayHello("RPC");
        assertEquals("Hello RPC", result);

        server.stop();
        client.close();
    }


}
