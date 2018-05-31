package org.htps.rpc.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.htps.rpc.bean.Page;
import org.htps.rpc.bean.PageDataList;
import org.htps.rpc.bean.User;
import org.htps.rpc.service.UserService;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService{


	public PageDataList<User> getPage(int pageIndex, int pageSize){
		long total = 10000;
		Page page = new Page(total, pageIndex, pageSize);
		List<User> list = new ArrayList<User>();
		for(int i=(int) page.getStart();i<page.getEnd();i++){
			User u = new User();
			u.setBirthDay(new Date());
			u.setCreateDate(new Date());
			u.setGender(i % 2 == 0);
			u.setModifyDate(new Date());
			u.setUserId(i+1L);
			u.setUserName("名字"+ i);
			list.add(u);
		}
		PageDataList<User> pdlist = new PageDataList<User>(page, list);
		return pdlist;
	}
}
