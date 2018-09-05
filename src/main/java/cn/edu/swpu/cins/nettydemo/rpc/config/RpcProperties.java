package cn.edu.swpu.cins.nettydemo.rpc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Rpc配置文件
@ConfigurationProperties(prefix = "rpc")
public class RpcProperties {

    private String registryAddress;

    private String serverAddress;

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
}
