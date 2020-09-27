package com.micah.bean;

public class Bean1 {

    public void doSomething(){
        System.out.println(System.currentTimeMillis()+" "+this);
    }

    public void init(){
        System.out.println("bean1 init method executed...");
    }

    public void destroy(){
        System.out.println("bean1 destroy method executed...");
    }
}
