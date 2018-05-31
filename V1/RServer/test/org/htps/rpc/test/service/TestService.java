package org.htps.rpc.test.service;

import java.util.List;

import org.htps.rpc.test.bean.TestBean;

public interface TestService {
	
	public String test();
	
	public TestBean testBean();
	
	public TestBean testBean(String arg0, Integer arg1);

	public TestBean testBean(TestBean arg0, Integer arg1);
	
	public TestBean testBean(List<TestBean> arg0, Integer arg1);

}
