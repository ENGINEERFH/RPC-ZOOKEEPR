package com.thrift.pool;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 连接池实现
 *
 */
public class ThriftProviderImpl implements ThriftProvider,InitializingBean, DisposableBean {

    // 服务的IP地址
    private String serviceIP;
    // 服务的端口
    private int servicePort;
    // 连接超时配置
    private int conTimeOut;
    // 缓存池中最大空闲对象数量
    private int maxIdle = 8;
    // 缓存池中最小空闲对象数量
    private int minIdle = 0;
    // 阻塞的最大数量
    private long maxWait = -1L;

    // 从缓存池中分配对象，是否执行PoolableObjectFactory.validateObject方法
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;
    private boolean testWhileIdle =false;

    // 对象缓存池
    private ObjectPool<TTransport> objectPool = null;

    @Override
    public TSocket getConnection() {
        try {
            // 从对象池取对象
            TSocket socket = (TSocket) objectPool.borrowObject();
            return socket;
        } catch (Exception e) {
            throw new RuntimeException("error getConnection()", e);
        }
    }

    @Override
    public void returnCon(TSocket socket) {
        try {
            // 将对象放回对象池
            objectPool.returnObject(socket);
        } catch (Exception e) {
            throw new RuntimeException("error returnCon()", e);
        }

    }

    @Override
    public void destroy() throws Exception {
        try {
            objectPool.close();
        } catch (Exception e) {
            throw new RuntimeException("erorr destroy()", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWait);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setTestWhileIdle(testWhileIdle);
        config.setBlockWhenExhausted(true);

        // 设置factory
        ThriftPoolFactory thriftPoolFactory = new ThriftPoolFactory(serviceIP, servicePort, conTimeOut);
        // 对象池
        objectPool = new GenericObjectPool<TTransport>(thriftPoolFactory,config);
    }

    public String getServiceIP() {
        return serviceIP;
    }

    public void setServiceIP(String serviceIP) {
        this.serviceIP = serviceIP;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public int getConTimeOut() {
        return conTimeOut;
    }

    public void setConTimeOut(int conTimeOut) {
        this.conTimeOut = conTimeOut;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }
}
