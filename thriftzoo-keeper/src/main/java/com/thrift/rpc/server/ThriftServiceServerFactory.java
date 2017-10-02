package com.thrift.rpc.server;

import com.thrift.rpc.zookeeper.ThriftServerAddressRegister;
import com.thrift.rpc.zookeeper.ThriftServerIpLocalNetworkResolve;
import com.thrift.rpc.zookeeper.ThriftServerIpResolve;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.springframework.beans.factory.InitializingBean;
import com.thrift.rpc.zookeeper.ThriftException;

import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Constructor;

/**
 * 服务端注册服务工厂
 */
public class ThriftServiceServerFactory implements InitializingBean {

    // 服务注册本机端口
    private Integer port = 8299;
    // 优先级
    private Integer weight = 1;// default
    // 服务实现类
    private Object service;// serice实现类
    //服务版本号
    private String version;
    //服务注册
    private ThriftServerAddressRegister thriftServerAddressRegister;

    // 解析本机IP
    private ThriftServerIpResolve thriftServerIpResolve;

    private ServerThread serverThread;



    @Override
    public void afterPropertiesSet() throws Exception {
        if (thriftServerIpResolve == null){
            thriftServerIpResolve = new ThriftServerIpLocalNetworkResolve();
        }
        String serverIP = thriftServerIpResolve.getServerIp();
        if (serverIP == null || serverIP.equals("")){
            throw new ThriftException("cant find server ip...");
        }
        String hostname = serverIP + ":" + port + ":" + weight;
        Class<?>  serviceClass = service.getClass();
        // 获取实现类接口
        Class<?>[] interfaces = serviceClass.getInterfaces();
        if (interfaces.length == 0) {
            throw new IllegalClassFormatException("service-class should implements Iface");
        }
        // reflect,load "Processor";
        TProcessor processor = null;
        String serviceName = null;
        for (Class<?> clazz : interfaces) {
            String cname = clazz.getSimpleName();
            if (!cname.equals("Iface")) {
                continue;
            }
            serviceName = clazz.getEnclosingClass().getName();
            String pname = serviceName + "$Processor";
            try {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                Class<?> pclass = classLoader.loadClass(pname);
                if (!TProcessor.class.isAssignableFrom(pclass)) {
                    continue;
                }
                Constructor<?> constructor = pclass.getConstructor(clazz);
                processor = (TProcessor) constructor.newInstance(service);
                break;
            } catch (Exception e) {
                //
            }
        }
        if (processor == null) {
            throw new IllegalClassFormatException("service-class should implements Iface");
        }
        //需要单独的线程,因为serve方法是阻塞的.
        serverThread = new ServerThread(processor, port);
        serverThread.start();
        // 注册服务
        if (thriftServerAddressRegister != null) {
            thriftServerAddressRegister.register(serviceName, version, hostname);
        }
    }

    class ServerThread extends Thread {
        private TServer server;
        ServerThread(TProcessor processor, int port) throws Exception {
            // 传输层(Transport), 设置监听端口为port--非阻塞方式
            TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(port);
            // 多线程半同步半异步
            TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
            // 处理层
            TProcessorFactory processorFactory = new TProcessorFactory(processor);
            tArgs.processorFactory(processorFactory);
            // 协议层 使用二进制协议 使用非阻塞式IO，服务端和客户端需要指定TFramedTransport数据传输的方式
            tArgs.transportFactory(new TFramedTransport.Factory());
            tArgs.protocolFactory( new TBinaryProtocol.Factory(true, true));
            // 服务层
            server = new TThreadedSelectorServer(tArgs);
        }

        @Override
        public void run(){
            try{
                //启动服务
                server.serve();
            }catch(Exception e){
                //
            }
        }

        public void stopServer(){
            server.stop();
        }
    }

    public void close() {
        serverThread.stopServer();
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setService(Object service) {
        this.service = service;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setThriftServerAddressRegister(ThriftServerAddressRegister thriftServerAddressRegister) {
        this.thriftServerAddressRegister = thriftServerAddressRegister;
    }
}
