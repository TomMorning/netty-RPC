package server;

import common.RpcRequest;
import common.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * RPC 服务器
 */
public class RpcServer {

    private final int port;
    private final ServiceRegistry serviceRegistry;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public RpcServer(int port, ServiceRegistry serviceRegistry) {
        this.port = port;
        this.serviceRegistry = serviceRegistry;
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
    }

    public void start() throws Exception {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new SimpleChannelInboundHandler<RpcRequest>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
                                    RpcResponse response = handleRequest(request);
                                    ctx.writeAndFlush(response);
                                }
                            });
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();
            registerAllService(); // 注册服务

            future.channel().closeFuture().sync(); // 阻塞，直到服务器关闭
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stop(); // 确保最终关闭服务器
        }
    }

    private void registerAllService() throws Exception {
        // TODO: 从配置文件中读取服务信息
        Map<Class<?>, Object> services = new HashMap<>();
        services.put(MyService.class, new MyServiceImpl());
        registerServices(services);
    }

    private RpcResponse handleRequest(RpcRequest request) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        try {
            Object service = serviceRegistry.getService(request.getClassName());
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
            Object result = method.invoke(service, request.getParameters());
            response.setResult(result);
        } catch (Exception e) {
            response.setError(e);
            e.printStackTrace();
        }
        return response;
    }


    public void registerServices(Map<Class<?>, Object> services) throws Exception {
        for (Map.Entry<Class<?>, Object> entry : services.entrySet()) {
            Class<?> serviceClass = entry.getKey();
            Object serviceInstance = entry.getValue();
            // 注册服务实例到本地注册表
            serviceRegistry.register(serviceClass.getName(), serviceInstance);
        }
    }

    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
