package com.abc.bean;

import com.abc.annotation.Autowired;

public class Dalao {
    @Autowired
    public Water water;
    @Autowired
    private Coffee coffee;

    public void drink(){
        System.out.println("大佬在喝"+coffee.getType());
    }


}
