package org.cloudy.dscm.publisher;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class CServerInitializer extends ChannelInitializer<SocketChannel> {
	private CServer server;

	public CServerInitializer(CServer server) {
		this.server = server;
	}

	public boolean remove(Channel channel) {
		return this.server.remove(channel);
	}

	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("aggregator", new HttpObjectAggregator(this.server.aggregator()));
		pipeline.addLast("encoder", new HttpResponseEncoder());

		pipeline.addLast("handler", new CServerHandler(this));
	}
}
