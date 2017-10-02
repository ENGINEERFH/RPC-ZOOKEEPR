package com.thrift.jieba;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;

/**
 * blog http://www.micmiu.com
 *
 * @author Michael
 *
 */
public class JiebaParticipleServer {
    public static final int SERVER_PORT = 8070;

    public void startServer() {
        try {

            // 传输层(Transport), 设置监听端口为8070
            TServerSocket serverTransport = new TServerSocket(SERVER_PORT);
            TServer.Args tArgs = new TServer.Args(serverTransport);
            // 处理层
            TProcessor tprocessor = new JiebaParticiple.Processor(new JiebaParticipleImpl());
            tArgs.processor(tprocessor);
            // 协议层
            tArgs.protocolFactory(new TBinaryProtocol.Factory());
            // 服务层
            TServer server = new TSimpleServer(tArgs);
            // 启动监听服务
            System.out.println("JiebaParticiple server start ....");
            server.serve();


        } catch (Exception e) {
            System.out.println("Server start error!!!");
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        JiebaParticipleServer server = new JiebaParticipleServer();
        server.startServer();
    }

}
