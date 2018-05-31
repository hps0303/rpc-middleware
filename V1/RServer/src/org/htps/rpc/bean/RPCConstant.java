package org.htps.rpc.bean;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import org.htps.rpc.client.RClient;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @description constant and static method
 * @path org.htps.rpc.bean.RPCConstant
 * @author heps
 * @date 2018年5月30日
 */
public class RPCConstant {
	
	public static final String DEFAULT_CHARSET = "UTF-8";
	
	public static final SerializerFeature[] FEATURE = { SerializerFeature.WriteClassName,SerializerFeature.WriteDateUseDateFormat, SerializerFeature.IgnoreNonFieldGetter };
	
	public static final InternalLogger logger = InternalLoggerFactory.getInstance("rpc");
	
	/**
	 * @description send msg from client to server
	 * @param addr
	 * @param bean
	 * @return
	 * @author heps
	 * @date 2018年5月30日
	 */
	private static RPCResult sendMsg(RPCAddr addr, RPCBean bean){
		String content = JSONObject.toJSONString(bean,RPCConstant.FEATURE);
		RPCConstant.logger.info("rPCBean===>"+content);
		RPCResult rst = null;
		try {
			String res = RClient.sendMsg(addr.getHost(), addr.getPort(), content);
			RPCConstant.logger.info("rPCResult===>"+res);
			rst = JSONObject.parseObject(res, RPCResult.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rst;
	}
	
	/**
	 * @description rpc requst
	 * @param host server host
	 * @param port server port
	 * @param method target method
	 * @param clazz target clazz
	 * @param resClazz target method return type
	 * @param argTypes target method args type
	 * @param args args
	 * @return
	 * @throws Exception
	 * @author heps
	 * @date 2018年5月30日
	 */
	public static Object rpcRequst(String host, int port, String method, String clazz, String resClazz, String[] argTypes, Object[] args) throws Exception{
		Object ob = null;
		
		RPCAddr addr = new RPCAddr();
		addr.setHost(host);
		addr.setPort(port);
		
		RPCBean bean = new RPCBean();
		bean.setArgs(args);
		bean.setArgTypes(argTypes);
		bean.setMethod(method);
		bean.setClazz(clazz);

		RPCResult rst = RPCConstant.sendMsg(addr, bean);
		if(null != rst){
			if(null != rst.getE()){
				throw rst.getE();
			}
			ob = JSONObject.parseObject(JSONObject.toJSONString(rst.getO(),RPCConstant.FEATURE), Class.forName(resClazz));
		}
		return ob;
	}
}
