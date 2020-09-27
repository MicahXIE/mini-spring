package com.micah.factory;

import com.micah.bean.Bean2;
import com.micah.bean.Bean3;

public class Bean2Factory {

    public static Bean2 getBean2(String name, Bean3 bean) {
    	System.out.println("Call Bean2Factory getBean2 method...");
        return new Bean2(name, bean);
    }

    public Bean2 getOtherBean2(String name, Bean3 bean){
    	System.out.println("Call Bean2Factory getOtherBean2 method...");
        return new Bean2(name, bean);
    }
}
