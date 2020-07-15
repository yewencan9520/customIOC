package com.abc.bean;


import com.abc.annotation.Autowired;

/**
 * 这是个老板
 */

public class Boss {

    @Autowired
    public Water water;

    private Coffee coffee;

    public void drink(){

        System.out.println("老板要喝"+water.getType());
    }

}
