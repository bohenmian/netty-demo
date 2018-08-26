package cn.edu.swpu.cins.nettydemo.chapter4.service.impl;

import cn.edu.swpu.cins.nettydemo.chapter4.annotation.RpcService;
import cn.edu.swpu.cins.nettydemo.chapter4.service.HelloService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello" + name;
    }
}
