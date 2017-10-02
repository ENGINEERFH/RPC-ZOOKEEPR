package com.thrift.pool;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.*;
import com.thrift.jieba.*;

public class TserverFactory extends BasePooledObjectFactory<TServer> {
    private  int port;
    private boolean keepAlive;

    public TserverFactory(int port, boolean keepAlive) {
        this.port = port;
        this.keepAlive = keepAlive;
    }

    @Override
    public TServer create() throws Exception{
        try {

            // 传输层(Transport), 设置监听端口为8070
            TServerSocket serverTransport = new TServerSocket(port);
            TServer.Args tArgs = new TServer.Args(serverTransport);
            // 处理层
            TProcessor tprocessor = new JiebaParticiple.Processor(new JiebaParticipleImpl());
            tArgs.processor(tprocessor);
            // 协议层
            tArgs.protocolFactory(new TBinaryProtocol.Factory());
            // 服务层
            TServer server = new TSimpleServer(tArgs);
            // 启动监听服务
            System.out.println("create: server creat ....");
            return server;

        } catch (Exception e) {
            System.out.println("create: Server creat error !!!!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PooledObject<TServer> wrap(TServer server) {
        return new DefaultPooledObject<TServer>(server);
    }

    /**
     *  对象钝化(即：从激活状态转入非激活状态，returnObject时触发）
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<TServer> pooledObject) throws Exception{
        if (keepAlive){
            try {
                pooledObject.getObject().stop();
                System.out.println("passivateObject: server stop ....");
            } catch (Exception e){
                System.out.println("passivateObject: server stop error !!!!");
                e.printStackTrace();
            }
        }
    }

    /**
     * 对象激活(borrowObject时触发）
     * @param pooledObject
     * @throws Exception
     */
    public void activateObject(PooledObject<TServer> pooledObject) throws Exception{
        if (!pooledObject.getObject().isServing()){
            try {
                System.out.println("activateObject: server start ....");
                pooledObject.getObject().serve();
            } catch (Exception e){
                System.out.println("activateObject: server start error !!!!");
                e.printStackTrace();
            }
        }
    }

    /**
     * 对象销毁(clear时会触发）
     * @param pooledObject
     * @throws Exception
     */
    public void destroyObject(PooledObject<TServer> pooledObject) throws Exception{
        passivateObject(pooledObject);
        pooledObject.markAbandoned();
    }

    /**
     * 验证对象有效性
     * @param pooledObject
     * @return
     */
    public boolean validateObject(PooledObject<TServer> pooledObject){
        if (pooledObject.getObject() != null){
            if (pooledObject.getObject().isServing()){
                return true;
            }
            try {
                System.out.println("validateObject: server start ....");
                pooledObject.getObject().serve();
                return  true;
            } catch (Exception e) {
                System.out.println("validateObject: server start error !!!!");
                e.printStackTrace();
            }
        }
        return false;
    }
}
