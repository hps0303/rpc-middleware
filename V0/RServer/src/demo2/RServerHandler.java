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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * Handles a server-side channel.
 */
public class RServerHandler extends SimpleChannelInboundHandler<Object> {
	private StringBuffer sb = new StringBuffer();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf bb = (ByteBuf) msg;
    	sb.append(bb.toString(Charset.forName("utf-8")));
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println(new StringBuffer("From:").append(ctx.channel().remoteAddress().toString()).append("===>").append(sb).toString());
		String result = callMethod(sb.toString());
		ByteBuf msg = ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(result), Charset.forName("utf-8"));
		ctx.writeAndFlush(msg);
		ctx.channel().close();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String callMethod(String json){
		RPCBean bean = JSONObject.parseObject(json, RPCBean.class);
		String clazz = bean.getClazz();
		String method = bean.getMethod();
		String rsClazz = bean.getRsClazz();
		String[] argTypes = bean.getArgTypes();
		Object[] args = bean.getArgs();
		RPCResult rst = new RPCResult();
		rst.setClazz(rsClazz);
		try {
			Class cls = Class.forName(clazz);
			List<Class> parameterTypesList = new ArrayList<Class>();
			Class[] parameterTypes = new Class[]{};
			if(null != argTypes){
				for(int i=0;i<argTypes.length;i++){
					String argType = argTypes[i];
					Class argTypeCls = Class.forName(argType);
					args[i] = JSONObject.parseObject(JSONObject.toJSONString(args[i]), argTypeCls);
					parameterTypesList.add(argTypeCls);
				}
			}
			parameterTypes = parameterTypesList.toArray(parameterTypes);
			Object o = SpringContextExt.getBeanByClazz(cls);
			Method m = cls.getMethod(method, parameterTypes);
			Object r = m.invoke(o, args);
			rst.setO(r);
		} catch (Exception e) {
			rst.setE(e);
		}
		return JSONObject.toJSONString(rst);
	}
}
