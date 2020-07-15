package com.abc.bean;


import com.abc.annotation.Component;

@Component
public class Water implements IWater{

    private String type;

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return "今麦郎凉白开";
    }
}
