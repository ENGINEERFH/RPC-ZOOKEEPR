package com.thrift.rpc.client.pool;

import org.apache.thrift.TServiceClient;
import com.thrift.rpc.zookeeper.ThriftException;
import com.thrift.rpc.zookeeper.ThriftServerAddressProvider;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 定义对象工厂
 *
 */
public class ThriftPoolFactory extends BasePooledObjectFactory<TServiceClient> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final ThriftServerAddressProvider serverAddressProvider;
    private final TServiceClientFactory<TServiceClient> clientFactory;
    private ThriftPoolFactory.PoolOperationCallBack callback;

    public ThriftPoolFactory(ThriftServerAddressProvider addressProvider, TServiceClientFactory<TServiceClient> clientFactory) throws Exception {
        this.serverAddressProvider = addressProvider;
        this.clientFactory = clientFactory;
    }

    public ThriftPoolFactory(ThriftServerAddressProvider addressProvider, TServiceClientFactory<TServiceClient> clientFactory,
                                ThriftPoolFactory.PoolOperationCallBack callback) throws Exception {
        this.serverAddressProvider = addressProvider;
        this.clientFactory = clientFactory;
        this.callback = callback;
    }

    @Override
    public TServiceClient create() throws Exception {
        InetSocketAddress address = serverAddressProvider.selector();
        if(address==null){
            new ThriftException("No provider available for remote service");
        }
        TSocket tsocket = new TSocket(address.getHostName(), address.getPort());
        TTransport transport = new TFramedTransport(tsocket);
        TProtocol protocol = new TBinaryProtocol(transport);
        TServiceClient client = this.clientFactory.getClient(protocol);
        transport.open();
        if (callback != null) {
            try {
                callback.make(client);
            } catch (Exception e) {
                logger.warn("makeObject:{}", e);
            }
        }
        return client;
    }

    @Override
    public PooledObject<TServiceClient> wrap(TServiceClient serviceclient) {
        return new DefaultPooledObject<TServiceClient>(serviceclient);
    }

    /**
     *  对象钝化(即：从激活状态转入非激活状态，returnObject时触发）
     * @param pooledObject
     * @throws Exception
     */
    @Override
    public void passivateObject(PooledObject<TServiceClient> pooledObject) throws Exception {
    }

    /**
     * 对象激活(borrowObject时触发）
     * @param pooledObject
     * @throws TTransportException
     */
    public void activateObject(PooledObject<TServiceClient> pooledObject) throws Exception {
    }

    /**
     * 对象销毁(clear时会触发）
     * @param pooledObject
     * @throws TTransportException
     */
    public void destroyObject(PooledObject<TServiceClient> pooledObject) throws TTransportException{
        if (callback != null) {
            try {
                callback.destroy(pooledObject.getObject());
            } catch (Exception e) {
                logger.warn("destroyObject:{}", e);
            }
        }
        logger.info("destroyObject:{}", pooledObject.getObject());
        TTransport pin = pooledObject.getObject().getInputProtocol().getTransport();
        pin.close();
        TTransport pout = pooledObject.getObject().getOutputProtocol().getTransport();
        pout.close();
    }

    /**
     * 验证对象有效性
     * @param pooledObject
     * @return
     */
    public boolean validateObject(PooledObject<TServiceClient> pooledObject){
        TTransport pin = pooledObject.getObject().getInputProtocol().getTransport();
        logger.info("validateObject input:{}", pin.isOpen());
        TTransport pout = pooledObject.getObject().getOutputProtocol().getTransport();
        logger.info("validateObject output:{}", pout.isOpen());
        return pin.isOpen() && pout.isOpen();
    }

    public static interface PoolOperationCallBack {
        // 销毁client之前执行
        void destroy(TServiceClient client);

        // 创建成功是执行
        void make(TServiceClient client);
    }
}
