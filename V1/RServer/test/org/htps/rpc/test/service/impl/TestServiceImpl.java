package org.htps.rpc.test.service.impl;

import java.util.Date;
import java.util.List;

import org.htps.rpc.test.bean.TestBean;
import org.htps.rpc.test.service.TestService;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service("testService")
public class TestServiceImpl implements TestService{
	
	public String test(){
		return "test service hello! 你好啊！";
	}
	
	public TestBean testBean(){
		System.out.println("testBean()");
		TestBean tb = new TestBean();
		tb.setCreateTime(new Date());
		tb.setId(12345L);
		tb.setName("testBean逗比");
		return tb;
	}
	
	
	public TestBean testBean(String arg0, Integer arg1){
		System.out.println("testBean(String arg0, Integer arg1)");
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<1000;i++){
			sb.append("工");
		}
		TestBean tb = new TestBean();
		tb.setCreateTime(new Date());
		tb.setId(12345L);
//		tb.setName(arg0 + ":" + arg1);
		tb.setName(sb.toString());
		return tb;
	}

	public TestBean testBean(TestBean arg0, Integer arg1){
		System.out.println("testBean(TestBean arg0, Integer arg1)");
		TestBean tb = new TestBean();
		tb.setCreateTime(new Date());
		tb.setId(12345L);
		tb.setName(JSONObject.toJSONString(arg0));
//		System.out.println(tb.getName());
//		System.out.println(JSONObject.toJSONString(tb));
		return tb;
	}
	
	
	public TestBean testBean(List<TestBean> arg0, Integer arg1){
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println("testBean(List<TestBean> arg0, Integer arg1)");
		TestBean tb = new TestBean();
		tb.setCreateTime(new Date());
		tb.setId(12345L);
//		tb.setName(JSONObject.toJSONString(arg0));
		tb.setName("ok好");
		return tb;
	}

}
