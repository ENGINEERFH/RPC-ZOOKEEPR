package com.thrift.jieba;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * blog http://www.micmiu.com
 *
 * @author Michael
 *
 */
public class JiebaParticipleClient {

    public static final String SERVER_IP = "localhost";
    public static final int SERVER_PORT = 8070;
    public static final int TIMEOUT = 30000;

    /**
     *
     * @param bestring
     */
    public void startClient(String bestring) {
        TTransport transport = null;
        try {
            // 传输层

            transport = new TSocket(SERVER_IP, SERVER_PORT, TIMEOUT);
            transport.open();
            // 协议层 协议要和服务端一致
            TProtocol protocol = new TBinaryProtocol(transport);
            // 创建RPC客户端
            JiebaParticiple.Client client = new JiebaParticiple.Client(protocol);
            // 调用服务
            String result = client.participle(bestring);
            System.out.println("participle result =: " + result);

        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            if (null != transport) {
                // 关闭句柄
                transport.close();
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        JiebaParticipleClient client = new JiebaParticipleClient();
        client.startClient("我来到浙江大学与大家交流");
    }

}
