package com.thrift.rpc.client;

import com.thrift.rpc.client.pool.ThriftPoolFactory;
import com.thrift.rpc.zookeeper.ThriftServerAddressProvider;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.Closeable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * 客户端代理工厂
 */
public class ThriftServiceClientProxyFactory implements FactoryBean, InitializingBean,Closeable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // 缓存池中最大空闲对象数量
    private int maxIdle = 32;
    // 缓存池中最小空闲对象数量
    private int minIdle = 0;

    private Integer idleTime = 180000;// ms,default 3 min,链接空闲时间, -1,关闭空闲检测
    private ThriftServerAddressProvider serverAddressProvider;

    private Object proxyClient;
    private Class<?> objectClass;

    private GenericObjectPool<TServiceClient> pool;

    private ThriftPoolFactory.PoolOperationCallBack callback = new ThriftPoolFactory.PoolOperationCallBack() {
        @Override
        public void make(TServiceClient client) {
            logger.info("create");
        }

        @Override
        public void destroy(TServiceClient client) {
            logger.info("destroy");
        }
    };




    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        // 加载Iface接口
        objectClass = classLoader.loadClass(serverAddressProvider.getService() + "$Iface");
        // 加载Client.Factory类
        Class<TServiceClientFactory<TServiceClient>> fi = (Class<TServiceClientFactory<TServiceClient>>) classLoader.loadClass(serverAddressProvider.getService() + "$Client$Factory");
        TServiceClientFactory<TServiceClient> clientFactory = fi.newInstance();

        ThriftPoolFactory clientPool = new ThriftPoolFactory(serverAddressProvider, clientFactory, callback);
        pool = new GenericObjectPool<TServiceClient>(clientPool, makePoolConfig());

        ThriftServiceClientProxy handler = new ThriftServiceClientProxy(pool);

        proxyClient = Proxy.newProxyInstance(classLoader, new Class[] { objectClass }, handler);
    }



    private GenericObjectPoolConfig  makePoolConfig() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMinEvictableIdleTimeMillis(idleTime);
        config.setTimeBetweenEvictionRunsMillis(idleTime * 2L);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(false);
        config.setTestWhileIdle(false);
        config.setBlockWhenExhausted(true);
        return config;
    }

    @Override
    public Object getObject() throws Exception {
        return proxyClient;
    }

    @Override
    public Class<?> getObjectType() {
        return objectClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void close() {
        if(pool!=null){
            try {
                pool.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (serverAddressProvider != null) {
            serverAddressProvider.close();
        }
    }

    public void setIdleTime(Integer idleTime) {
        this.idleTime = idleTime;
    }

    public void setServerAddressProvider(ThriftServerAddressProvider serverAddressProvider) {
        this.serverAddressProvider = serverAddressProvider;
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
}
