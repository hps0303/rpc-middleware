package demo2.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service
public class TestService {
	
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
		TestBean tb = new TestBean();
		tb.setCreateTime(new Date());
		tb.setId(12345L);
		tb.setName(arg0 + ":" + arg1);
		return tb;
	}

	public TestBean testBean(TestBean arg0, Integer arg1){
		System.out.println("testBean(TestBean arg0, Integer arg1)");
		TestBean tb = new TestBean();
		tb.setCreateTime(new Date());
		tb.setId(12345L);
		tb.setName(JSONObject.toJSONString(arg0));
		return tb;
	}
	
	
	public TestBean testBean(List<TestBean> arg0, Integer arg1){
		System.out.println("testBean(List<TestBean> arg0, Integer arg1)");
		TestBean tb = new TestBean();
		tb.setCreateTime(new Date());
		tb.setId(12345L);
		tb.setName(JSONObject.toJSONString(arg0));
		return tb;
	}

}
