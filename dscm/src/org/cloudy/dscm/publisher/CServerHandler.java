package org.cloudy.dscm.publisher;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudy.dscm.common.CConf;
import org.cloudy.dscm.common.CParameter;

import com.alibaba.fastjson.JSON;

public class CServerHandler extends SimpleChannelInboundHandler<Object> {
	private CServerInitializer initializer;
	private AttributeKey<Map<String, Object>> paramKey = AttributeKey.valueOf("param");
	private AttributeKey<HttpRequest> requestKey = AttributeKey.valueOf("request");

	public CServerHandler(CServerInitializer httpserverInitializer) {
		this.initializer = httpserverInitializer;
	}

	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
	}

	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		this.initializer.remove(ctx.channel());
		super.channelInactive(ctx);
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);
	}

	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
	}

	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);
	}

	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		super.channelWritabilityChanged(ctx);
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		StringBuilder sb = new StringBuilder("clientreset addr=");
		sb.append(ctx.channel().remoteAddress().toString());
		ctx.channel().close();

		CServer.logger().log(sb);
	}

	protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
		if ((msg instanceof HttpRequest)) {
			HttpRequest request = (HttpRequest) msg;
			if (!request.getMethod().equals(HttpMethod.POST)) {
				writeResponse(request.getUri(), ctx.channel(), request, RuntimeException.class.getName(), "err", JSON.toJSONString(new RuntimeException("dscm-only-post")));

				clear(ctx);
				return;
			}

			Map<String,Object> param = (Map<String,Object>) ctx.channel().attr(this.paramKey).get();
			if (param == null) {
				param = new HashMap<String,Object>();
				ctx.channel().attr(this.paramKey).set(param);
			}

			QueryStringDecoder decoderQuery = new QueryStringDecoder(request.getUri());
			Map<String, List<String>> uriAttributes = decoderQuery.parameters();
			for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
				param.put(attr.getKey(), ((List<String>) attr.getValue()).size() > 0 ? (String) ((List<String>) attr.getValue()).get(0) : null);
			}
			param.put("___ctx___", decoderQuery.path().substring(1));
			Attribute<HttpRequest> requestVal = ctx.channel().attr(this.requestKey);
			requestVal.set(request);
		}

		if ((msg instanceof HttpContent)) {
			HttpContent chunk = (HttpContent) msg;

			if ((chunk instanceof LastHttpContent)) {
				byte[] buf = new byte[chunk.content().readableBytes()];
				chunk.content().readBytes(buf);
				String json = new String(buf, "UTF-8");

				CServer.getInstance().threadPool().execute(new BizImpl(ctx, json, chunk));
			}
		}
	}

	private void clear(ChannelHandlerContext ctx) {
		AttributeKey<Object> decoderKey = AttributeKey.valueOf("decoder");
		AttributeKey<Object> paramKey = AttributeKey.valueOf("param");
		AttributeKey<Object> requestKey = AttributeKey.valueOf("request");

		ctx.channel().attr(decoderKey).set(null);
		ctx.channel().attr(requestKey).set(null);
		ctx.channel().attr(paramKey).set(null);
	}

	private void writeResponse(String actn, Channel channel, HttpRequest request, String claz, String errs, String body) {
		ByteBuf buf = Unpooled.copiedBuffer(body, CConf.CUTF);

		boolean close = (request.headers().contains("Connection", "close", true))
				|| ((request.getProtocolVersion().equals(HttpVersion.HTTP_1_0)) && (!request.headers().contains("Connection", "keep-alive", true)));

		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);

		response.headers().set("Content-Type", "text/plain; charset=UTF-8");
		if (claz != null)
			response.headers().set("claz", claz);
		if (errs != null)
			response.headers().set("errs", errs);

		if (!close) {
			response.headers().set("Content-Length", Integer.valueOf(response.content().readableBytes()));
		}

		ChannelFuture future = channel.writeAndFlush(response);

		if (close) {
			future.addListener(ChannelFutureListener.CLOSE);
		}

		if (CServer.getInstance().debug())
			CServer.logger().log(new StringBuilder(body.length() + actn.length() + 3).append(actn).append("::").append(body));
	}

	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		messageReceived(ctx, msg);
	}

	class BizImpl implements Runnable {
		private ChannelHandlerContext ctx;
		private String json;

		public BizImpl(ChannelHandlerContext ctx, String json, HttpContent chunk) {
			this.ctx = ctx;
			this.json = json;
		}

		public void run() {
			Object bean = null;
			Method method = null;
			List<?> args = null;
			Class<?>[] types = null;

			long currentTimestamp = System.currentTimeMillis();

			HttpRequest request = (HttpRequest) this.ctx.channel().attr(CServerHandler.this.requestKey).get();
			Map<String, Object> param = (Map<String, Object>) this.ctx.channel().attr(CServerHandler.this.paramKey).get();

			String actn = (String) param.get("actn");
			String uuid = (String) param.get("__i__");
			String claz = request.headers().get("claz");
			String sync = request.headers().get("sync");
			try {
				if ((actn == null) || (claz == null)) {
					throw new RuntimeException("actn or claz is null");
				}

				sync = sync == null ? "sync" : sync;

				if (CServer.getInstance().debug()) {
					CServer.logger().log(
							new StringBuffer(this.ctx.channel().remoteAddress().toString()).append(" ").append(actn).append(" ").append(sync).append(actn).append(" ").append(claz).append(" ")
									.append(this.json));
				}

				bean = CServer.getInstance().getBean((String) param.get("___ctx___"));
				types = CParameter.parse(JSON.parseArray(claz, String.class));
				method = bean.getClass().getMethod(actn, types);

				args = JSON.parseArray(this.json, types);
			} catch (Exception exc) {
				CServerHandler.this.writeResponse(actn, this.ctx.channel(), request, RuntimeException.class.getName(), "err", JSON.toJSONString(exc.getMessage()));

				if (CServer.getInstance().waste()) {
					CServer.logger().log(
							new StringBuffer((String) param.get("___ctx___")).append("?actn=").append(actn).append(" uuid=").append(uuid).append(" server-time=")
									.append(System.currentTimeMillis() - currentTimestamp));
				}

				return;
			}

			String body = null;
			String err = null;
			try {
				Object object = method.invoke(bean, args == null ? null : args.toArray());
				body = JSON.toJSONString(object, CConf.FEATURE);
				claz = method.getReturnType().getName();
			} catch (Exception exc) {
				Throwable throwable = exc.getCause() == null ? exc : exc.getCause();
				CServer.logger().log(throwable.getMessage());
				claz = throwable.getClass().getName();
				body = JSON.toJSONString(throwable);
				err = "err";
			}

			CServerHandler.this.writeResponse(actn, this.ctx.channel(), request, claz, err, body);
			CServerHandler.this.clear(this.ctx);

			if (CServer.getInstance().waste())
				CServer.logger().log(
						new StringBuffer((String) param.get("___ctx___")).append("?actn=").append(actn).append(" uuid=").append(uuid).append(" server-time=")
								.append(System.currentTimeMillis() - currentTimestamp));
		}
	}
}
