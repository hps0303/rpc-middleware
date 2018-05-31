/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package demo1;

import java.nio.CharBuffer;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handles a client-side channel.
 */
public class RClientHandler extends SimpleChannelInboundHandler<Object> {

//    private ByteBuf content;
    private ByteBuf content;
    private ChannelHandlerContext ctx;
    private StringBuffer sb = new StringBuffer();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.ctx = ctx;

        // Initialize the message.
//        content = ctx.alloc().directBuffer(DiscardClient.SIZE).writeZero(DiscardClient.SIZE);
        content = ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap("你好啊！"), Charset.forName("utf-8"));
        // Send the initial messages.
        System.out.println("channelActive");
        generateTraffic();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        content.release();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    	UnpooledUnsafeDirectByteBuf uudbb = (UnpooledUnsafeDirectByteBuf) msg;
    	sb.append(uudbb.toString(Charset.forName("utf-8")));
    }

    @Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelReadComplete===>"+sb.toString());
		super.channelReadComplete(ctx);
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    long counter;

    private void generateTraffic() {
        // Flush the outbound buffer to the socket.
        // Once flushed, generate the same amount of traffic again.
//        ctx.writeAndFlush(content.duplicate().retain()).addListener(trafficGenerator);
        ctx.writeAndFlush(content).addListener(trafficGenerator);
    }

    private final ChannelFutureListener trafficGenerator = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) {
            if (future.isSuccess()) {
            	System.out.println("future.isSuccess");
//                generateTraffic();
            } else {
                future.cause().printStackTrace();
                future.channel().close();
            }
        }
    };
}
