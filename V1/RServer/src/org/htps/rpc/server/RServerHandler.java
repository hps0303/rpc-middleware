package org.htps.rpc.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.htps.rpc.bean.RPCBean;
import org.htps.rpc.bean.RPCConstant;
import org.htps.rpc.bean.RPCResult;
import org.htps.rpc.util.SpringContextExt;

import com.alibaba.fastjson.JSONObject;

/**
 * @description Handles a server-side channel
 * @path org.htps.rpc.server.RServerHandler
 * @author heps
 * @date 2018年5月30日
 */
public class RServerHandler extends SimpleChannelInboundHandler<Object> {

	private ByteBuf tmpBuf = Unpooled.buffer(0);
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		tmpBuf = Unpooled.copiedBuffer(tmpBuf,(ByteBuf) msg);
	}

	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        RPCConstant.logger.error("exceptionCaught:", cause);
        ctx.close();
    }

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		String content = tmpBuf.toString(Charset.forName(RPCConstant.DEFAULT_CHARSET));
		RPCConstant.logger.info(new StringBuffer("From:").append(ctx.channel().remoteAddress().toString()).append("===>").append(content).toString());
		String result = callMethod(content);
		RPCConstant.logger.info("end callMethod===>res:"+result);
		ByteBuf msg = ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(result), Charset.forName(RPCConstant.DEFAULT_CHARSET));
		ctx.writeAndFlush(msg);
		ctx.channel().close();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String callMethod(String json){
		RPCBean bean = JSONObject.parseObject(json, RPCBean.class);
		String clazz = bean.getClazz();
		String method = bean.getMethod();
		String[] argTypes = bean.getArgTypes();
		Object[] args = bean.getArgs();
		RPCResult rst = new RPCResult();
		try {
			Class cls = Class.forName(clazz);
			List<Class> parameterTypesList = new ArrayList<Class>();
			Class[] parameterTypes = new Class[]{};
			if(null != argTypes){
				for(int i=0;i<argTypes.length;i++){
					String argType = argTypes[i];
					Class argTypeCls = Class.forName(argType);
					args[i] = JSONObject.parseObject(JSONObject.toJSONString(args[i],RPCConstant.FEATURE), argTypeCls);
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
		return JSONObject.toJSONString(rst,RPCConstant.FEATURE);
	}
}
