package com.micah.factory.impl;

import com.micah.beandefinition.BeanDefinition;
import com.micah.beandefinition.BeanDefinitionRegistry;
import com.micah.beanreference.BeanReference;
import com.micah.factory.BeanFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean factory implementation
 * @author micah
 * @create 2020-09-26 13:20
 **/
public class DefaultBeanFactory implements BeanFactory, BeanDefinitionRegistry, Closeable {

    private Log log = LogFactory.getLog(this.getClass());

    //ConcurrentHashMap to store <beanName, BeanDefinition>
    private Map<String, BeanDefinition> bdMap = new ConcurrentHashMap<>();

    //ConcurrentHashMap to store <beanName, Instance>
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();

    //used to avoid Circular dependency
    private ThreadLocal<Set<String>> initialedBeans = new ThreadLocal<>();

    @Override
    public void registerBeanDefinition(BeanDefinition beanDefinition, String beanName) {

        if (StringUtils.isBlank(beanName)) {
            log.error("beanName can't be blank beanName:" + beanName);
        }
        if (beanDefinition == null) {
            log.error("BeanDefinition can't be null beanDefinition:" + beanName);
            return;
        }

        if (bdMap.containsKey(beanName)) {
            log.info("[" + beanName + "] already exists");
        }

        if (!beanDefinition.validate()) {
            log.error("BeanDefinition is illegal");
            return;
        }

        bdMap.put(beanName, beanDefinition);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return bdMap.containsKey(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        if (!bdMap.containsKey(beanName)) {
            log.info("[" + beanName + "] doesn't exists");
            return null;
        }
        return bdMap.get(beanName);
    }

    /**
     * Core function for IOC
     * @param beanName
     * @return get the bean by using constructor, static factory method or factory bean
     */
    public Object doGetBean(String beanName) throws Exception {
        if (!beanMap.containsKey(beanName)) {
            log.info("[" + beanName + "] doesn't exists");
        }

        // record the Bean being created
        Set<String> beans = this.initialedBeans.get();
        if (beans == null) {
            beans = new HashSet<>();
            this.initialedBeans.set(beans);
        }

        // detect circular dependency
        if (beans.contains(beanName)) {
            throw new Exception("Detect " + beanName + " has Circular dependency : " + beans);
        }

        beans.add(beanName);

        Object instance = beanMap.get(beanName);

        if (instance != null) {
            return instance;
        }

        //check bean definition exists or not
        if (!this.bdMap.containsKey(beanName)) {
            log.info("doesn't exist ：[" + beanName + "] beandefinition, will create");
        }

        BeanDefinition bd = this.bdMap.get(beanName);

        Class<?> beanClass = bd.getBeanClass();

        //choose the specific method to create bean
        if (beanClass != null) {
            if (StringUtils.isBlank(bd.getStaticCreateBeanMethodName())) {
                instance = this.createBeanByConstructor(bd);
            } else {
                instance = this.createBeanByStaticFactoryMethod(bd);
            }
        } else {
            instance = createBeanByFactoryMethod(bd);
        }


        this.doInit(bd, instance);

        //add properties dependency
        this.parsePropertyValues(bd, instance);

        //Bean creation done and remove beanName from set
        beans.remove(beanName);

        if (instance != null && bd.isSingleton()) {
            beanMap.put(beanName, instance);
        }

        return instance;
    }

    /**
     * parse constructor arguments
     * @param constructorArgs
     * @return Object Array to store structor args
     */
    private Object[] parseConstructorArgs(List constructorArgs) throws Exception {

        if (constructorArgs==null || constructorArgs.size()==0) {
            return null;
        }

        Object[] args = new Object[constructorArgs.size()];
        for (int i = 0; i < constructorArgs.size(); i++) {
            Object arg = constructorArgs.get(i);
            Object value = null;
            if (arg instanceof BeanReference) {
                String beanName = ((BeanReference) arg).getBeanName();
                value = this.doGetBean(beanName);
            } else if (arg instanceof List) {
                List<BeanReference> references = (List<BeanReference>) arg;
                List param = new LinkedList();
                for (BeanReference reference:references) {
                    Object o = this.doGetBean(reference.getBeanName());
                    param.add(o);
                }
                value = param;
            } else if (arg instanceof Map) {
                //todo handle map
            } else if(arg instanceof Properties) {
                //todo handl properties
            } else {
                value = arg;
            }
            args[i] = value;
        }
        return args;
    }

    /**
     * match the correct constructor
     * @param BeanDefinition
     * @param constructor args
     * @return the correct constructor
     */
    private Constructor<?> matchConstructor(BeanDefinition bd, Object[] args) throws Exception {
        if (args == null) {
            return bd.getBeanClass().getConstructor(null);
        }
        //If already store in Beandefinition, will return
        if (bd.getConstructor() != null)
            return bd.getConstructor();

        int len = args.length;
        Class[] param = new Class[len];
        //Get constructor parameters 
        for (int i = 0; i < len; i++) {
            param[i] = args[i].getClass();
        }
        //match the constructor
        Constructor constructor = null;
        try {
            constructor = bd.getBeanClass().getConstructor(param);
        } catch (Exception e) {
            //in order to continue the program, here only catch the exception
        }
        if (constructor != null) {
            return constructor;
        }

        //continue to match constructor
        List<Constructor> firstFilterAfter = new LinkedList<>();
        Constructor[] constructors = bd.getBeanClass().getConstructors();
        //match by parameter numbers
        for (Constructor cons:constructors) {
            if (cons.getParameterCount() == len) {
                firstFilterAfter.add(cons);
            }
        }

        if (firstFilterAfter.size() == 1) {
            return firstFilterAfter.get(0);
        }
        if (firstFilterAfter.size() == 0) {
            log.error("doesn't exist corresponding constructor: " + args);
            throw new Exception("doesn't exist corresponding constructor: " + args);
        }
        //match by parameter type
        boolean isMatch = true;
        for (int i = 0; i < firstFilterAfter.size(); i++){
            Class[] types = firstFilterAfter.get(i).getParameterTypes();
            for (int j = 0; j < types.length; j++){
                if (types[j].isAssignableFrom(args[j].getClass())) {
                    isMatch = false;
                    break;
                }
            }
            if (isMatch) {
                //prototype bean definition set constructor
                if (bd.isPrototype()) {
                    bd.setConstructor(firstFilterAfter.get(i));
                }
                return firstFilterAfter.get(i);
            }
        }
        //can't match
        throw new Exception("doesn't exist corresponding constructor: " + args);
    }

    /**
     * parse properties value
     * @param BeanDefinition
     * @param specific instance
     * @return
     */
    private void parsePropertyValues(BeanDefinition bd, Object instance) throws Exception {
        Map<String, Object> propertyKeyValue = bd.getPropertyKeyValue();
        if (propertyKeyValue == null || propertyKeyValue.size() == 0) {
            return ;
        }
        Class<?> aClass = instance.getClass();
        Set<Map.Entry<String, Object>> entries = propertyKeyValue.entrySet();
        for (Map.Entry<String, Object> entry:entries) {
            //Get the specific field info
            Field field = aClass.getDeclaredField(entry.getKey());
            //Set access permission to true
            field.setAccessible(true);
            Object arg = entry.getValue();
            Object value = null;
            if (arg instanceof BeanReference) {
                String beanName = ((BeanReference) arg).getBeanName();
                value = this.doGetBean(beanName);
            } else if (arg instanceof List) {
                List param = parseListArg((List) arg);
                value = param;
            } else if (arg instanceof Map) {
                //todo handle map
            } else if (arg instanceof Properties) {
                //todo handle properties
            } else {
                value = arg;
            }
            field.set(instance, value);
        }
    }

    /**
     * used in parsePropertyValues for field type is List
     * @param original arg list
     * @return parsed arg list
     */
    private List parseListArg(List arg) throws Exception {
        List param = new LinkedList();
        for (Object value:arg) {
            Object res = new Object();
            if (arg instanceof BeanReference) {
                String beanName = ((BeanReference) value).getBeanName();
                res = this.doGetBean(beanName);
            } else if (arg instanceof List) {
                res = parseListArg(arg);
            } else if (arg instanceof Map) {
                //todo handle map
            } else if (arg instanceof Properties) {
                //todo handle properties
            } else {
                res = arg;
            }
            param.add(res);
        }
        return param;
    }

    /**
     * run init method for the instance
     * @param BeanDefinition
     * @param instance
     * @return
     */
    private void doInit(BeanDefinition bd, Object instance) {
        Class<?> beanClass = instance.getClass();
        if (StringUtils.isNotBlank(bd.getBeanInitMethodName())) {
            try {
                Method method = beanClass.getMethod(bd.getBeanInitMethodName(), null);
                method.invoke(instance, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * creat Bean by using constructor
     * @param Bean definition
     * @return instance
     */
    private Object createBeanByConstructor(BeanDefinition bd) {
        Object instance = null;
        try {
            //parse constructor parameters
            List<?> constructorArg = bd.getConstructorArg();
            Object[] objects = parseConstructorArgs(constructorArg);
            //match constructor parameters
            Constructor<?> constructor = matchConstructor(bd, objects);
            if (constructor != null) {
                instance = constructor.newInstance(objects);
            } else {
                instance = bd.getBeanClass().newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * create Bean by using factory method
     * @param Bean defintion
     * @return instance
     */
    private Object createBeanByFactoryMethod(BeanDefinition bd) {
        Object instance = null;
        try {
            //Get factory object
            Object factory = doGetBean(bd.getFactoryBeanName());
            List<?> constructorArg = bd.getConstructorArg();
            if (constructorArg != null && constructorArg.size() > 0) {
                Object[] args = parseConstructorArgs(constructorArg);
                Class[] paramTypes = new Class[args.length];
                int j = 0;
                for (Object p : args) {
                    paramTypes[j++] = p.getClass();
                }
                //Get create bean method with args
                Method method = factory.getClass().getMethod(bd.getCreateBeanMethodName(), paramTypes);
                instance = method.invoke(factory, args);
            } else {
                //Get create bean method without args
                Method method = factory.getClass().getMethod(bd.getCreateBeanMethodName());
                instance = method.invoke(factory, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * create Bean by using static factory method
     * @param Bean definition
     * @return instance
     */
    private Object createBeanByStaticFactoryMethod(BeanDefinition bd) {
        Object instance = null;
        try {
            Class<?> beanClass = bd.getBeanClass();
            List<?> constructorArg = bd.getConstructorArg();
            if (constructorArg != null && constructorArg.size() > 0) {
                Object[] args = parseConstructorArgs(constructorArg);
                Class[] paramTypes = new Class[args.length];
                int j = 0;
                for (Object p : args) {
                    paramTypes[j++] = p.getClass();
                }
                //Get create bean method with args
                Method method = beanClass.getMethod(bd.getStaticCreateBeanMethodName(), paramTypes);
                instance = method.invoke(beanClass, args);
            } else {
                //Get create bean method without args
                Method method = beanClass.getMethod(bd.getStaticCreateBeanMethodName());
                instance = method.invoke(beanClass, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        return doGetBean(beanName);
    }

    @Override
    public void close() throws IOException {
        Set<Map.Entry<String, BeanDefinition>> entries = bdMap.entrySet();
        for (Map.Entry<String, BeanDefinition>  entry: entries) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (beanDefinition.isSingleton() && StringUtils.isNotBlank(beanDefinition.getBeanDestroyMethodName())) {
                Object instance = this.beanMap.get(beanName);
                try {
                    Method method = instance.getClass().getMethod(beanDefinition.getBeanDestroyMethodName(), null);
                    method.invoke(instance, null);
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    log.error("execute bean [" + beanName + "] " + beanDefinition + "destroy method exception", e);
                }
            }
        }
    }
}
