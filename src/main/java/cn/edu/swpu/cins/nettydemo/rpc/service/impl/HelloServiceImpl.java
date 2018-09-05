package cn.edu.swpu.cins.nettydemo.rpc.service.impl;

import cn.edu.swpu.cins.nettydemo.rpc.annotation.RpcService;
import cn.edu.swpu.cins.nettydemo.rpc.service.HelloService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello" + name;
    }
}
