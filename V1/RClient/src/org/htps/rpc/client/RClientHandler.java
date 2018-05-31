package org.htps.rpc.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.htps.rpc.bean.RPCConstant;

/**
 * @description Handles a client-side channel
 * @path org.htps.rpc.client.RClientHandler
 * @author heps
 * @date 2018年5月30日
 */
public class RClientHandler extends SimpleChannelInboundHandler<Object> {

	private String readyContent;
    private StringBuffer sb = new StringBuffer();
    private long time = System.currentTimeMillis();

    public StringBuffer getSb() {
		return sb;
	}

	public RClientHandler(String readyContent) {
		super();
		this.readyContent = readyContent;
	}

	@Override
    public void channelActive(ChannelHandlerContext ctx) {
		RPCConstant.logger.info(new StringBuffer("To:").append(ctx.channel().remoteAddress().toString()).append("===>").append(readyContent).toString());
        ctx.writeAndFlush(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(readyContent), Charset.forName("utf-8")));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    	ByteBuf uudbb = (ByteBuf) msg;
    	sb.append(uudbb.toString(Charset.forName("utf-8")));
    }

    @Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    	RPCConstant.logger.info(new StringBuffer("Return:").append(ctx.channel().remoteAddress().toString()).append(",costTime:").append(System.currentTimeMillis()-time).append("ms,===>").append(sb).toString());
		ctx.channel().close();
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
