package com.thrift;

import org.springframework.context.support.ClassPathXmlApplicationContext;

//服务端启动
public class Server {

	public static void main(String[] args) {
		try {
			new ClassPathXmlApplicationContext("classpath:spring-context-thrift-server.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("服务启动成功 ....");
	}
}
