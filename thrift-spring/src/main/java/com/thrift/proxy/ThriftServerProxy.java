package com.thrift.proxy;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.*;

import java.lang.reflect.Constructor;

public class ThriftServerProxy {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private int port;// 端口
    private String serviceInterface;// 实现类接口
    private Object serviceImplObject;// 实现类

    public void start() {
        new Thread() {
            @Override
            public void run() {
                try {
                    TServerSocket serverTransport = new TServerSocket(getPort());
                    // 实现类处理类class
                    Class Processor = Class.forName(serviceInterface + "$Processor");
                    // 接口
                    Class Iface = Class.forName(serviceInterface + "$Iface");
                    // 接口构造方法类
                    Constructor con = Processor.getConstructor(Iface);
                    // 实现类处理类
                    TProcessor processor = (TProcessor) con.newInstance(serviceImplObject);
                    TBinaryProtocol.Factory protFactory = new TBinaryProtocol.Factory(true, true);
                    TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
                    args.protocolFactory(protFactory);
                    args.processor(processor);
                    TServer server = new TThreadPoolServer(args);
                    logger.info("Starting server on port " + getPort() + " ...");
                    System.out.println("Starting server on port " + getPort() + " ...");
                    server.serve();
                } catch (TTransportException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public Object getServiceImplObject() {
        return serviceImplObject;
    }

    public void setServiceImplObject(Object serviceImplObject) {
        this.serviceImplObject = serviceImplObject;
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-applicationContext-server.xml");

        ThriftServerProxy thriftServerProxy = (ThriftServerProxy) context.getBean(ThriftServerProxy.class);
        thriftServerProxy.start();
    }
}
