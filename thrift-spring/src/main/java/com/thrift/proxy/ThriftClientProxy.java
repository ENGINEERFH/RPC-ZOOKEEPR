package com.thrift.proxy;
import com.thrift.pool.ThriftManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import java.lang.reflect.Constructor;

/**
 * Created by chenshunyang on 2016/11/1.
 */
public class ThriftClientProxy {

    private ThriftManager thriftManager;

    public ThriftManager getThriftManager() {
        return thriftManager;
    }
    public void setThriftManager(ThriftManager thriftManager) {
        this.thriftManager = thriftManager;
    }
    public Object getClient(Class clazz) {
        Object result = null;
        try {
            TTransport transport = thriftManager.getSocket();
            TProtocol protocol = new TBinaryProtocol(transport);
            Class client = Class.forName(clazz.getName() + "$Client");
            Constructor con = client.getConstructor(TProtocol.class);
            result = con.newInstance(protocol);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}