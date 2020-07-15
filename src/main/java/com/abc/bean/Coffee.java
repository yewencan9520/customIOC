package com.abc.bean;


import com.abc.annotation.Component;

@Component
public class Coffee implements IWater{

    public String getType(){
        return "卡布奇诺";
    }

}
