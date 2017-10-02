package com.thrift.pool;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;


public class PooledServerTest {
    public static void main(String[] args) throws Exception{
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(10);
        config.setMinIdle(1);
        config.setTestOnBorrow(true);

        ObjectPool<TServer> pool = new AutoClearGenericObjectPool<TServer>(
                new TserverFactory(8070, true), config);

        TServer server = pool.borrowObject();


    }
}
