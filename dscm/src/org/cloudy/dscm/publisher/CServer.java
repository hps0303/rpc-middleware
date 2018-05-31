package org.cloudy.dscm.publisher;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.cloudy.dscm.common.CConf;
import org.cloudy.dscm.common.CLogger;
import org.cloudy.dscm.context.CContextLoaderListener;

public class CServer {
	private static final int DEFAULT_THREADS = Math.max(1, SystemPropertyUtil.getInt("io.netty..eventLoopThreads", Runtime.getRuntime().availableProcessors() * 4));
	private CLogger logger;
	private int port = 5520;
	private int backlog = 10240;
	private int rcvbuf = 262144;
	private int sndbuf = 262144;
	private int aggregator = 262144;
	private boolean keepalive = true;
	private boolean debug = true;
	private boolean waste = true;
	private int threadSize = 0;
	private static CServer httpserver;
	private CContextLoaderListener loader;
	private ExecutorService threadPool = null;
	private ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private ServerBootstrap bootstrap;
	private NioEventLoopGroup bossGroup;
	private NioEventLoopGroup workGroup;

	public boolean remove(Channel channel) {
		return this.channels.remove(channel);
	}

	public static CServer getInstance(CContextLoaderListener loader) {
		if ((httpserver == null) && (loader != null)) {
			httpserver = new CServer(loader);
		}
		return httpserver;
	}

	public static CLogger logger() {
		return getInstance().logger;
	}

	public static CServer getInstance() {
		return httpserver;
	}

	public CServer(CContextLoaderListener loader) {
		this.loader = loader;
	}

	public Object getBean(String name) throws Exception {
		if ((name == null) || (name.length() <= 0)) {
			throw new Exception("bean name is null");
		}
		return this.loader.classByName(name);
	}

	public void stopHttpserver() {
		this.channels.close().awaitUninterruptibly();
	}

	public boolean debug() {
		return this.debug;
	}

	public boolean waste() {
		return this.waste;
	}

	public int aggregator() {
		return this.aggregator;
	}

	public ExecutorService threadPool() {
		return this.threadPool;
	}

	public void startHttpserver(Properties properties) throws Exception {
		String logname = properties.getProperty("logname");

		this.port = CConf.toInt(properties.getProperty("port"));
		this.backlog = CConf.toInt(properties.getProperty("backlog"));
		this.rcvbuf = CConf.toInt(properties.getProperty("rcvbuf"));
		this.sndbuf = CConf.toInt(properties.getProperty("sndbuf"));
		this.keepalive = CConf.toBool(properties.getProperty("keepalive"));
		this.debug = CConf.toBool(properties.getProperty("debug"));
		this.waste = CConf.toBool(properties.getProperty("waste"));
		this.threadSize = CConf.toInt(properties.getProperty("threadSize"), DEFAULT_THREADS);

		this.aggregator = CConf.toInt(properties.getProperty("aggregator"));

		this.threadPool = Executors.newCachedThreadPool();
		this.logger = new CLogger(InternalLoggerFactory.getInstance(logname));

		this.logger.log(new StringBuffer("threadPool=").append(this.threadSize));

		this.bootstrap = new ServerBootstrap();
		this.bossGroup = new NioEventLoopGroup();
		this.workGroup = new NioEventLoopGroup();

		this.bootstrap.group(this.bossGroup, this.workGroup);
		this.bootstrap.option(ChannelOption.SO_BACKLOG, Integer.valueOf(this.backlog));
		this.bootstrap.option(ChannelOption.SO_RCVBUF, Integer.valueOf(this.rcvbuf));
		this.bootstrap.option(ChannelOption.SO_SNDBUF, Integer.valueOf(this.sndbuf));
		this.bootstrap.option(ChannelOption.SO_LINGER, Integer.valueOf(0));
		this.bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		this.bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		this.bootstrap.childOption(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(this.keepalive));
		this.bootstrap.childOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
		this.bootstrap.channel(NioServerSocketChannel.class);
		this.bootstrap.childHandler(new CServerInitializer(this));

		this.logger.log("|backlog:" + this.backlog + "|rcvbuf:" + this.rcvbuf + "|sndbuf:" + this.sndbuf + "|keepalive:" + this.keepalive + "|port:" + this.port);
		final Channel channel;
		try {
			channel = this.bootstrap.bind(this.port).sync().channel();
		} catch (InterruptedException exc) {
			this.logger.log(exc.getMessage());
			throw exc;
		}
		new Thread(new Runnable() {
			public void run() {
				try {
					channel.closeFuture().sync();
				} catch (InterruptedException exc) {
					exc.printStackTrace();
				} finally {
					CServer.this.workGroup.shutdownGracefully();
					CServer.this.workGroup.shutdownGracefully();
				}
			}
		}).start();
	}
}
