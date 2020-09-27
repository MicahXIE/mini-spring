package com.micah.bean;

public class Bean4 {

	private Bean5 b5;

    public Bean4(Bean5 bean) {
    	super();
        this.b5 = bean;
    }

    public void doSomething(){
        System.out.println(System.currentTimeMillis() + " Bean4 do something...");
    }
}
