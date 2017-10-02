package com.thrift;

import com.thrift.api.JiebaParticiple;
import com.thrift.proxy.ThriftClientProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 */
public class ClientTest {
    public static void main(String[] args) throws Exception{
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-applicationContext-client.xml");

        ThriftClientProxy thriftClientProxy = (ThriftClientProxy) context.getBean(ThriftClientProxy.class);
        JiebaParticiple.Iface client = (JiebaParticiple.Iface)thriftClientProxy.getClient(JiebaParticiple.class);

        String result = client.participle("我来到浙江大学与大家交流");
        System.out.println("participle result =: " + result);
    }
}
