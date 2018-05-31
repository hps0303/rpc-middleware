package org.cloudy.dscm.subscriber;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.CodingErrorAction;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.cloudy.dscm.common.CConf;
import org.cloudy.dscm.common.CLogger;
import org.cloudy.dscm.common.CParameter;
import org.cloudy.dscm.publisher.CServer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import io.netty.util.internal.logging.InternalLoggerFactory;

public class CConnectionExecutorImpl implements CConnectionExecutor {
	private PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
	public static final CConnectionExecutor INST = new CConnectionExecutorImpl();
	private RequestConfig requestConfig = null;
	private CLogger logger;
	private boolean debug;
	private boolean waste;
	private String asyHost;
	private long checkTime;
	private long closeTime;
	private long asySize;

	private CConnectionExecutorImpl() {
		setConfigure();

		SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).setSoKeepAlive(false).setSoLinger(0).build();

		this.connectionManager.setDefaultSocketConfig(socketConfig);

		ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE).setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(CConf.CUTF)
				.setMessageConstraints(MessageConstraints.custom().build()).build();

		this.connectionManager.setDefaultConnectionConfig(connectionConfig);

		new IdleConnectionMonitor(this.connectionManager, this.checkTime, this.closeTime).start();
	}

	@SuppressWarnings("deprecation")
	private void setConfigure() {
		try {
			InputStream is = CConnectionExecutor.class.getClassLoader().getResourceAsStream("dscm.properties");

			Properties properties = new Properties();
			properties.load(is);
			is.close();

			this.waste = CConf.toBool(properties.getProperty("waste"));
			this.debug = CConf.toBool(properties.getProperty("debug"));
			this.asyHost = properties.getProperty("asyHost");
			this.asySize = CConf.toLong(properties.getProperty("asySize"));

			String logname = properties.getProperty("logname");

			int maxTimeout = CConf.toInt(properties.getProperty("maxTimeout"));
			int maxTotal = CConf.toInt(properties.getProperty("maxTotal"));
			int defaultMaxPerRoute = CConf.toInt(properties.getProperty("defaultMaxPerRoute"));

			this.checkTime = CConf.toLong(properties.getProperty("checkTime"));
			this.closeTime = CConf.toLong(properties.getProperty("closeTime"));

			this.connectionManager.setMaxTotal(maxTotal);
			this.connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);

			this.requestConfig = RequestConfig.custom().setSocketTimeout(maxTimeout).setConnectTimeout(maxTimeout).setConnectionRequestTimeout(maxTimeout).setStaleConnectionCheckEnabled(true).build();

			this.logger = new CLogger(InternalLoggerFactory.getInstance(logname));
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

	private CloseableHttpClient getHttpClient() {
		return HttpClients.custom().setConnectionManager(this.connectionManager).build();
	}

	private CParameter post(CloseableHttpClient httpClient, String url, Method method, String claz, String json, String sync, String safe, String uuid) throws Throwable {
		String content = null;
		CParameter result = new CParameter();
		CloseableHttpResponse response = null;
		String auth = new URL(url).getAuthority();

		url = url + "?" + "actn" + "=" + method.getName() + "&" + "__i__" + "=" + uuid;

		if (!"sync".equalsIgnoreCase(sync)) {
			if ((method.getReturnType() != Boolean.class) && (method.getReturnType() != Boolean.TYPE)) {
				throw new RuntimeException("async call function must return boolean or Boolean");
			}

			if (json.length() > this.asySize) {
				throw new RuntimeException("async message size greater asySize @see dscm-conf");
			}

			claz = "['java.lang.String','java.lang.String','java.lang.String','java.lang.String','java.lang.String','java.lang.String']";
			String[] array = { UUID.randomUUID().toString(), url, claz, json, safe, auth };
			json = JSON.toJSONString(array, CConf.FEATURE);

			url = this.asyHost + "/asycService?actn=create";
			sync = "sync";
		}

		HttpPost httpPost = new HttpPost(url);

		long currentTimestamp = System.currentTimeMillis();
		try {
			if (this.debug) {
				this.logger.log(new StringBuffer(httpPost.getURI().getQuery()).append(" ").append(sync).append(" ").append(claz).append(" ").append(json));
			}

			httpPost.setHeader("claz", claz);
			httpPost.setHeader("sync", sync);
			httpPost.setEntity(new StringEntity(json, "UTF-8"));

			httpPost.setConfig(this.requestConfig);
			response = httpClient.execute(httpPost);

			HttpEntity httpEntity = response.getEntity();
			content = EntityUtils.toString(httpEntity, "UTF-8");
		} catch (Exception exc) {
			throw new RuntimeException(url + exc.getMessage(), exc.getCause());
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException exc) {
					throw new RuntimeException(exc.getCause());
				}
			}

			INST.waste(new StringBuffer(url).append(" http-client-time=").append(System.currentTimeMillis() - currentTimestamp));
		}

		Header[] errs = response.getHeaders("errs");
		Header[] type = response.getHeaders("claz");

		if (this.debug) {
			this.logger.log(new StringBuffer(httpPost.getURI().getQuery()).append(" ").append(errs).append(" ").append(type).append(" ").append(content));
		}

		if (type.length < 1) {
			throw new RuntimeException("dscm-claz-err");
		}

		Object object = null;
		try {
			Class<?> ctarg = CParameter.parse(type[0].getValue());
			object = JSON.parseObject(content, ctarg);
		} catch (JSONException exc) {
			throw new RuntimeException(type[0].getValue() + " " + exc.getMessage() + content);
		} catch (Exception exc) {
			throw new RuntimeException(exc.getCause() != null ? exc.getCause() : exc);
		}

		if (errs.length > 0) {
			if ((object instanceof Throwable)) {
				throw ((Throwable) object);
			}
			throw new RuntimeException(object.toString());
		}

		result.object(object);

		return result;
	}

	public Object execute(Object proxy, Method method, Object[] args, String url, String sync, String safe, String uuid) throws Throwable {
		String clazz = null;
		String cjson = null;
		CloseableHttpClient httpClient = getHttpClient();
		try {
			clazz = JSON.toJSONString(method.getParameterTypes(), CConf.FEATURE);
			cjson = JSON.toJSONString(args, CConf.FEATURE);
		} catch (Throwable exc) {
			throw exc;
		}

		return post(httpClient, url, method, clazz, cjson, sync, safe, uuid).object();
	}

	public void waste(StringBuffer sb) {
		if (this.waste)
			this.logger.log(sb);
	}

	private static class IdleConnectionMonitor extends Thread {
		private final HttpClientConnectionManager httpClientConnectionManager;
		private long checkTime;
		private long closeTime;

		public IdleConnectionMonitor(HttpClientConnectionManager httpClientConnectionManager, long checkTime, long closeTime) {
			this.httpClientConnectionManager = httpClientConnectionManager;
			this.checkTime = checkTime;
			this.closeTime = closeTime;
		}

		public void run() {
			try {
				while (true)
					synchronized (this) {
						wait(this.checkTime);

						this.httpClientConnectionManager.closeExpiredConnections();

						this.httpClientConnectionManager.closeIdleConnections(this.closeTime, TimeUnit.SECONDS);
					}
			} catch (InterruptedException exc) {
				CServer.logger().log(new StringBuilder("CSubscriber pool error!!").append(exc.getMessage()));
			}
		}
	}
}
