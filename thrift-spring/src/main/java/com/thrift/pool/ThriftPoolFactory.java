package com.thrift.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 定义对象工厂
 *
 */
public class ThriftPoolFactory extends BasePooledObjectFactory<TTransport> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // 服务的IP
    private String serviceIP;
    // 服务的端口
    private int servicePort;
    // 超时设置
    private int timeOut;

    public ThriftPoolFactory(String serviceIP, int servicePort, int timeOut) {
        this.serviceIP = serviceIP;
        this.servicePort = servicePort;
        this.timeOut = timeOut;
    }

    @Override
    public TTransport create() throws Exception {
        try {
            TTransport transport = new TSocket(this.serviceIP, this.servicePort, this.timeOut);
            transport.open();
            return transport;
        } catch (Exception e) {
            logger.error("error ThriftPoolableObjectFactory()", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public PooledObject<TTransport> wrap(TTransport transport) {
        return new DefaultPooledObject<TTransport>(transport);
    }

    /**
     *  对象钝化(即：从激活状态转入非激活状态，returnObject时触发）
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<TTransport> pooledObject) throws TTransportException {
//        if (keepAlive){
//            pooledObject.getObject().flush();
//            pooledObject.getObject().close();
//        }
    }

    /**
     * 对象激活(borrowObject时触发）
     * @param pooledObject
     * @throws TTransportException
     */
    public void activateObject(PooledObject<TTransport> pooledObject) throws TTransportException {
        if (!pooledObject.getObject().isOpen()){
            pooledObject.getObject().open();
        }
    }

    /**
     * 对象销毁(clear时会触发）
     * @param pooledObject
     * @throws TTransportException
     */
    public void destroyObject(PooledObject<TTransport> pooledObject) throws TTransportException{
        pooledObject.getObject().close();
        pooledObject.markAbandoned();
    }

    /**
     * 验证对象有效性
     * @param pooledObject
     * @return
     */
    public boolean validateObject(PooledObject<TTransport> pooledObject){
        if (pooledObject.getObject() != null){
            if (pooledObject.getObject().isOpen()){
                return true;
            }
            try {
                pooledObject.getObject().open();
                return  true;
            } catch (TTransportException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
