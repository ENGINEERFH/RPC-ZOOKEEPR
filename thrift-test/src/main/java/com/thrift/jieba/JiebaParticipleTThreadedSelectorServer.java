package com.thrift.jieba;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

public class JiebaParticipleTThreadedSelectorServer {
    public static final int SERVER_PORT = 8070;

    public void startServer() {
        try{

            // 传输层(Transport), 设置监听端口为8070--非阻塞方式
            TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(SERVER_PORT);
            // 多线程半同步半异步
            TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
            // 处理层
            TProcessor tprocessor = new JiebaParticiple.Processor<JiebaParticiple.Iface>(new JiebaParticipleImpl());
            tArgs.processor(tprocessor);
            // 协议层 使用高密度二进制协议 使用非阻塞式IO，服务端和客户端需要指定TFramedTransport数据传输的方式
            tArgs.protocolFactory(new TCompactProtocol.Factory());
            tArgs.transportFactory(new TFramedTransport.Factory());
            // 服务层
            TThreadedSelectorServer server = new TThreadedSelectorServer(tArgs);
            // 启动监听服务
            System.out.println("JiebaParticiple TThreadedSelectorServer start ....");
            server.serve();


        } catch (Exception e) {
            System.out.println("TThreadedSelectorServer start error!!!");
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        JiebaParticipleTThreadedSelectorServer server = new JiebaParticipleTThreadedSelectorServer();
        server.startServer();
    }

}
