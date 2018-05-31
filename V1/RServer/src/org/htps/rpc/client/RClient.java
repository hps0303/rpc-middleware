package org.htps.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * @description rpc client, start and ready to send msg to server
 * @path org.htps.rpc.client.RClient
 * @author heps
 * @date 2018年5月30日
 */
public final class RClient {


    public static String sendMsg(final String host,final int port, final String msg) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (System.getProperty("ssl") != null) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        final RClientHandler rch = new RClientHandler(msg);//client handler
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.option(ChannelOption.SO_SNDBUF, 1);
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     if (sslCtx != null) {
                         p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                     }
                     p.addLast(rch);
                 }
             });
           
            b.option(ChannelOption.TCP_NODELAY, true);
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
            return rch.getSb().toString();
        } finally {
            group.shutdownGracefully();
        }
    }
}
