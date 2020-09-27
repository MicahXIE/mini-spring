package com.micah.factory;

import com.micah.bean.Bean1;

public class Bean1Factory {

    public static Bean1 getBean1(){
        return new Bean1();
    }

    public Bean1 getOtherBean1(){
        return new Bean1();
    }
}
