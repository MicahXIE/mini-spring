# mini-spring
This repo is used to implement some features of Spring framework. It is an intereasting journey when you start to realize the feature
you are using every day. It will help you understand the framework better which will definitely improve the performance of your work.

## Current status
Currently I already implemented the basic IOC and DI functions. It supports singleton and prototype scope. It supports init and destroy method of the 
Bean. It can get bean through constructor, static factory method and factory bean. It also can work with constructor with different arguments and factory
method with different arguments. It also supports properties dependecy. It can detect the Circular dependency.


## Develop Environment
- JDK8
- Maven 3.3.9


## Code Structure

source tree
```
├── beandefinition 
│   ├── BeanDefinition.java (bean definition interface)
│   ├── BeanDefinitionRegistry.java (bean definition registry interface)
│   └── impl
│       └── DefaultBeanDefinition.java (bean definition interface implementation)
├── beanreference
│   └── BeanReference.java (bean reference used in arguments case)
├── factory
│   ├── BeanFactory.java (bean factory interface)
│   └── impl
│       └── DefaultBeanFactory.java (core)
└── property
    └── PropertyValue.java (properties dependency)

```

test tree
```
.
├── MiniSpringTest.java (main test function)
├── bean (test bean class)
│   ├── Bean1.java
│   ├── Bean2.java
│   ├── Bean3.java
│   ├── Bean4.java
│   └── Bean5.java
└── factory (test factory class)
    ├── Bean1Factory.java
    └── Bean2Factory.java
```


Class Diagram


![class](https://user-gold-cdn.xitu.io/2018/12/17/167b9a72da472398?imageslim)


## Limitations & Future Work
Currently this framework only has very limited functions. Also it is very inconvient to use beacuse you need to 
manually set the bean definition to get bean. In the following phase, I want to add annotations, AOP and mvc part
to make it more user friendly to use.


## Usages
You can start to try by running 
- `mvn clean test` to run the testing cases.
- `mvn clean package` to generate the jar.



