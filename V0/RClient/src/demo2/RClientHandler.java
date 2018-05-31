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
package demo2;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Handles a client-side channel.
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
		System.out.println(new StringBuffer("To:").append(ctx.channel().remoteAddress().toString()).append("===>").append(readyContent).toString());
        ctx.writeAndFlush(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(readyContent), Charset.forName("utf-8")));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    	UnpooledUnsafeDirectByteBuf uudbb = (UnpooledUnsafeDirectByteBuf) msg;
    	sb.append(uudbb.toString(Charset.forName("utf-8")));
    }

    @Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    	System.out.println(new StringBuffer("Return:").append(ctx.channel().remoteAddress().toString()).append(",costTime:").append(System.currentTimeMillis()-time).append("ms,===>").append(sb).toString());
		ctx.channel().close();
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

//    long counter;
//
//    private void generateTraffic() {
        // Flush the outbound buffer to the socket.
        // Once flushed, generate the same amount of traffic again.
//        ctx.writeAndFlush(content.duplicate().retain()).addListener(trafficGenerator);
//        ctx.writeAndFlush(content).addListener(trafficGenerator);
//    }
//
//	private final ChannelFutureListener trafficGenerator = new ChannelFutureListener() {
//        @Override
//        public void operationComplete(ChannelFuture future) {
//            if (future.isSuccess()) {
//            	System.out.println("future.isSuccess");
//            } else {
//                future.cause().printStackTrace();
//                future.channel().close();
//            }
//        }
//    };
}
