package t;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.htps.rpc.test.bean.TestBean;
import org.htps.rpc.test.service.TestService;
import org.junit.Test;


public class RClientTest extends BaseJunit4Test {
	@Resource
	private TestService testService;

	@Test
	public void test() {
		List<TestBean> list = new ArrayList<TestBean>();
		for(int i=0;i<3;i++){
			TestBean tb = new TestBean();
			tb.setCreateTime(new Date());
			tb.setId((long)i);
			tb.setName("testBean"+i+"...");
			list.add(tb);
		}
		
		org.htps.rpc.test.bean.TestBean rst = null;
		try {
			rst = testService.testBean(list, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(rst);
	}
	
	
}
