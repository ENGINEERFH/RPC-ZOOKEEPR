package com.thrift.rpc.zookeeper;

/**
 * 解析thrift-server端IP地址，用于注册服务
 *
 */
public interface ThriftServerIpResolve {
    String getServerIp() throws Exception;

    void reset();

    //当IP变更时,将会调用reset方法
    static interface IpRestCalllBack{
        public void rest(String newIp);
    }
}
