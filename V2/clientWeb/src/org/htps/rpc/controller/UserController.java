package org.htps.rpc.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.htps.rpc.bean.PageDataList;
import org.htps.rpc.bean.User;
import org.htps.rpc.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/test")
public class UserController{

	@Resource
	private UserService userService;
	
	@RequestMapping(value = "/userlist", method = RequestMethod.GET)
	public void userlist(Integer pageIndex, Integer pageSize, HttpServletResponse response) {
		PageDataList<User> pdlist = userService.getPage(pageIndex, pageSize);
		String json = JSONObject.toJSONString(pdlist);
		System.out.println(json);
		PrintWriter writer;
		try {
			response.setContentType("application/json;charset=UTF-8");
			response.setHeader("Cache-Control", "no-store, no-cache");
			writer = response.getWriter();
			writer.print(json);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
