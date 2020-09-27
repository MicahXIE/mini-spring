import com.micah.bean.Bean1;
import com.micah.bean.Bean2;
import com.micah.bean.Bean3;
import com.micah.bean.Bean4;
import com.micah.bean.Bean5;
import com.micah.factory.Bean1Factory;
import com.micah.factory.Bean2Factory;
import com.micah.beandefinition.BeanDefinition;
import com.micah.beandefinition.impl.DefaultBeanDefinition;
import com.micah.factory.impl.DefaultBeanFactory;
import com.micah.beanreference.BeanReference;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Spring IOC/DI test class
 * @author micah
 * @create 2020-09-26 13:20
 **/
public class MiniSpringTest {

    static DefaultBeanFactory factory = new DefaultBeanFactory();

    @Test
    public void testRegisterBeanDefinition() throws Exception {
        DefaultBeanDefinition defaultBeanDefinition = new DefaultBeanDefinition();
        defaultBeanDefinition.setBeanClass(Bean1.class);
        defaultBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        defaultBeanDefinition.setBeanInitMethodName("init");
        defaultBeanDefinition.setBeanDestroyMethodName("destroy");
        factory.registerBeanDefinition(defaultBeanDefinition, "bean1");
    }

    @Test
    public void testRegisterStaticFactoryMethod() throws Exception {
        DefaultBeanDefinition defaultBeanDefinition = new DefaultBeanDefinition();
        defaultBeanDefinition.setBeanClass(Bean1Factory.class);
        defaultBeanDefinition.setStaticCreateBeanMethodName("getBean1");
        factory.registerBeanDefinition(defaultBeanDefinition, "staticBean1");
    }

    @Test
    public void testRegisterFactoryMethod() throws Exception {
        DefaultBeanDefinition defaultBeanDefinition = new DefaultBeanDefinition();
        defaultBeanDefinition.setBeanClass(Bean1Factory.class);
        String factoryBeanName = "factory";
        factory.registerBeanDefinition(defaultBeanDefinition,factoryBeanName);

        defaultBeanDefinition = new DefaultBeanDefinition();
        defaultBeanDefinition.setFactoryBeanName(factoryBeanName);
        defaultBeanDefinition.setCreateBeanMethodName("getOtherBean1");
        defaultBeanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        factory.registerBeanDefinition(defaultBeanDefinition, "factoryBean");
    }

    @Test
    public void testConstructorDI() throws Exception {
        DefaultBeanDefinition bd = new DefaultBeanDefinition();
        bd.setBeanClass(Bean2.class);
        List<Object> args = new ArrayList<>();
        args.add("bean2");
        args.add(new BeanReference("bean3"));
        bd.setConstructorArg(args);
        factory.registerBeanDefinition(bd, "bean2");

        bd = new DefaultBeanDefinition();
        bd.setBeanClass(Bean3.class);
        args = new ArrayList<>();
        args.add("bean3");
        bd.setConstructorArg(args);
        factory.registerBeanDefinition(bd, "bean3");

        Bean2 b2 = (Bean2)factory.getBean("bean2");

        b2.doSomething();   
    }

    @Test
    public void testStaticFactoryMethodDI() throws Exception {
        DefaultBeanDefinition bd = new DefaultBeanDefinition();
        bd.setBeanClass(Bean2Factory.class);
        bd.setStaticCreateBeanMethodName("getBean2");
        List<Object> args = new ArrayList<>();
        args.add("bean2a");
        args.add(new BeanReference("bean3a"));
        bd.setConstructorArg(args);
        factory.registerBeanDefinition(bd, "bean2a");

        bd = new DefaultBeanDefinition();
        bd.setBeanClass(Bean3.class);
        args = new ArrayList<>();
        args.add("bean3a");
        bd.setConstructorArg(args);
        factory.registerBeanDefinition(bd, "bean3a");

        Bean2 b2 = (Bean2)factory.getBean("bean2a");

        b2.doSomething();
        
    }

    @Test
    public void testFactoryMethodDI() throws Exception {
        DefaultBeanDefinition bd = new DefaultBeanDefinition();
        bd.setFactoryBeanName("Bean2Factory");
        bd.setCreateBeanMethodName("getOtherBean2");
        List<Object> args = new ArrayList<>();
        args.add("bean2b");
        args.add(new BeanReference("bean3a"));
        bd.setConstructorArg(args);
        factory.registerBeanDefinition(bd, "bean2b");

        bd = new DefaultBeanDefinition();
        bd.setBeanClass(Bean2Factory.class);
        factory.registerBeanDefinition(bd, "Bean2Factory");

        Bean2 b2 = (Bean2) factory.getBean("bean2b");

        b2.doSomething();
    }

    @Test(expected = Exception.class)
    public void testCirculationDI() throws Exception {
        DefaultBeanDefinition bd = new DefaultBeanDefinition();
        bd.setBeanClass(Bean4.class);
        List<Object> args = new ArrayList<>();
        args.add(new BeanReference("bean5"));
        bd.setConstructorArg(args);
        factory.registerBeanDefinition(bd, "bean4");

        bd = new DefaultBeanDefinition();
        bd.setBeanClass(Bean5.class);
        args = new ArrayList<>();
        args.add(new BeanReference("bean4"));
        bd.setConstructorArg(args);
        factory.registerBeanDefinition(bd, "bean5");

        Bean4 b4 = (Bean4)factory.getBean("bean4");

        b4.doSomething();      
    }

    @AfterClass
    public static void testGetBean() throws Exception{
        System.out.println("Constructor···");
        Bean1 bean1 = (Bean1)factory.getBean("bean1");
        bean1.doSomething();

        System.out.println("Static Factory Method···");
        bean1 = (Bean1)factory.getBean("staticBean1");
        bean1.doSomething();

        System.out.println("Factory Bean···");
        bean1 = (Bean1)factory.getBean("factoryBean");
        bean1.doSomething();

        factory.close();
    }

}
