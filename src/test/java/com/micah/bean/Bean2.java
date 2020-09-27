package com.micah.bean;

public class Bean2 {

	private String name;

	private Bean3 b3;

	public Bean2(String name, Bean3 bean) {
		super();
		this.name = name;
		this.b3 = bean;
		System.out.println("Call Bean3 constructor with two arguments...");
	}

	public Bean2(Bean3 bean) {
        super();
        this.b3 = bean;
    }

    public void doSomething(){
        System.out.println(System.currentTimeMillis() + " " + this.name + " b3 name: " + this.b3.getName());
    }

    public void init(){
        System.out.println("bean2 init method executed...");
    }

    public void destroy(){
        System.out.println("bean2 destroy method executed...");
    }
}
