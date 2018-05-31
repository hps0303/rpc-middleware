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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handles a server-side channel.
 */
public class RServerHandler extends SimpleChannelInboundHandler<Object> {
	private StringBuffer sb = new StringBuffer();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		UnpooledUnsafeDirectByteBuf uudbb = (UnpooledUnsafeDirectByteBuf) msg;
    	sb.append(uudbb.toString(Charset.forName("utf-8")));
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("read complete! total info===>"+ sb.toString());
		
		ByteBuf msg = ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap("收到了！" + sb.toString()), Charset.forName("utf-8"));
		ctx.writeAndFlush(msg);
		super.channelReadComplete(ctx);
	}
	
}
