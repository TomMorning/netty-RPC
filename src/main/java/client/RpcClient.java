package client;

import common.RpcRequest;
import common.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * RPC 客户端
 */
public class RpcClient {

    private final String host;
    private final int port;
    private Channel channel;
    private final EventLoopGroup group;
    private final BlockingQueue<RpcResponse> responseQueue = new ArrayBlockingQueue<>(1);

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.group = new NioEventLoopGroup();
        try {
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast(new SimpleChannelInboundHandler<RpcResponse>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) {
                                responseQueue.offer(response); // 存储响应以便于 send 方法可以检索
                            }
                        });
                    }
                });

        ChannelFuture future = bootstrap.connect(host, port).sync();
        this.channel = future.channel();
    }

    public Object send(RpcRequest request) throws Exception {
        channel.writeAndFlush(request).sync();
        // 等待并获取响应
        RpcResponse response = responseQueue.take(); // 此处可能需要设置超时
        return response.getResult();
    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
        group.shutdownGracefully();
    }
}
