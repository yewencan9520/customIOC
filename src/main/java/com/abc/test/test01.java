package com.abc.test;

import com.abc.bean.Boss;
import com.abc.bean.Dalao;
import com.abc.framework.customContext;

public class test01 {
    public static void main(String[] args) {
        Boss boss1 = customContext.getBean(Boss.class);
        boss1.drink();

        Dalao dalao = customContext.getBean(Dalao.class);
        dalao.drink();

    }
}

