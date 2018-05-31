package org.htps.rpc.service;

import org.htps.rpc.bean.PageDataList;
import org.htps.rpc.bean.User;

public interface UserService {

	public PageDataList<User> getPage(int pageIndex, int pageSize);
}
