package demo2;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import demo2.RClient;
import demo2.RPCAddr;
import demo2.RPCBean;
import demo2.RPCResult;

@Service
public class ClientService {
	
	private RPCResult sendMsg(RPCAddr addr, RPCBean bean){
		String content = JSONObject.toJSONString(bean);
//		System.out.println("rPCBean===>"+content);
		RPCResult rst = null;
		try {
			String res = RClient.sendMsg(addr.getHost(), addr.getPort(), content);
//			System.out.println("rPCResult===>"+res);
			rst = JSONObject.parseObject(res, RPCResult.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rst;
	}
	
	
	public Object rpcRequst(String host, int port, String method, String clazz, String resClazz, String[] argTypes, Object[] args) throws Exception{
		Object ob = null;
		
		RPCAddr addr = new RPCAddr();
		addr.setHost(host);
		addr.setPort(port);
		
		RPCBean bean = new RPCBean();
		bean.setArgs(args);
		bean.setArgTypes(argTypes);
		bean.setMethod(method);
		bean.setClazz(clazz);

		RPCResult rst = this.sendMsg(addr, bean);
		if(null != rst){
			if(null != rst.getE()){
				throw rst.getE();
			}
			ob = JSONObject.parseObject(JSONObject.toJSONString(rst.getO()), Class.forName(resClazz));
		}
		return ob;
	}
	
}
