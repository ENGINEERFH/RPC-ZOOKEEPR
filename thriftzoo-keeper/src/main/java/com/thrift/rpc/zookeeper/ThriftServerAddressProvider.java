package com.thrift.rpc.zookeeper;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 获取服务地址接口
 */
public interface ThriftServerAddressProvider {
    //获取服务名称
    String getService();

    /**
     * 获取所有服务端地址
     * @return
     */
    List<InetSocketAddress> findServerAddressList();

    /**
     * 选取一个合适的address,可以随机获取等'
     * 内部可以使用合适的算法.
     * @return
     */
    InetSocketAddress selector();

    void close();
}
