package t;
import javax.annotation.Resource;

import org.htps.rpc.server.RServer;
import org.htps.rpc.test.service.TestService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;


public class RServerTest extends BaseJunit4Test {
	@Resource
	private TestService testService;
	@Value("${port}")
	private int port;

	@Test
	public void test() {
		try {
			RServer.start(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
