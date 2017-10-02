package com.thrift;

import com.thrift.rpc.client.ThriftServiceClientProxyFactory;
import com.thrift.rpc.api.JiebaParticiple;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;
import java.util.Map.Entry;

//客户端调用
@SuppressWarnings("resource")
public class Client {
	public static void main(String[] args) {
		//simple();
//		spring();
		for(int i=0 ; i<1000 ; i++){
			TThread thread = new TThread();
			thread.start();
		}
	}

	public static void participle() {
		try {
			final ApplicationContext context = new ClassPathXmlApplicationContext("spring-context-thrift-client.xml");
			JiebaParticiple.Iface jiebaparticipleserver = (JiebaParticiple.Iface) context.getBean("echoSerivce");
			System.out.println(jiebaparticipleserver.participle("我来到浙江大学与大家交流"));
			//关闭连接的钩子
			Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                	Map<String,ThriftServiceClientProxyFactory>
                	clientMap = context.getBeansOfType(ThriftServiceClientProxyFactory.class);
                	for(Entry<String, ThriftServiceClientProxyFactory> client : clientMap.entrySet()){
                		System.out.println("serviceName : "+client.getKey() + ",class obj: "+client.getValue());
                		client.getValue().close();
                	}
                }
            });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class TThread extends Thread {
		public void run() {
			try {
				participle();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
