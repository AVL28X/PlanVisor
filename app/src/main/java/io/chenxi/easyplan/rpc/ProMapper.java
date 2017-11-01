package io.chenxi.easyplan.rpc;

import io.chenxi.easyplan.rpc.client.UserParams;

public class ProMapper {
    public static void Test(){
        System.out.println(UserParams.newBuilder().build().toString());
    }
}
