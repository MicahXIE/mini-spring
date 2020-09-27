package com.micah.beandefinition;

import com.micah.property.PropertyValue;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Bean definition interface to provide information to create Bean 
 * @author micah
 * @create 2020-09-26 13:20
 **/
public interface BeanDefinition {

    String SCOPE_SINGLETON = "singleton";

    String SCOPE_PROTOTYPE = "prototype";

    /**
     * Get Bean class 
     * @return Bean class
     */
    Class<?> getBeanClass();

    /**
     * Get Bean factory name
     * @return Bean factory name
     */
    String getFactoryBeanName();

    /**
     * Get the method name of creating Bean
     * @return Create Bean method name
     */
    String getCreateBeanMethodName();

    /**
     * Get the method name of static creating Bean
     * @return Static create Bean method name
     */
    String getStaticCreateBeanMethodName();

    /**
     * Get the init method name of Bean
     * @return Bean init method name
     */
    String getBeanInitMethodName();

    /**
     * Get the destroy method name of Bean
     * @return Bean destroy method name
     */
    String getBeanDestroyMethodName();

    /**
     * Get bean type
     * @return bean type
     */
    String getScope();

    /**
     * check Bean is Singleton or not
     * @return true or false
     */
    boolean isSingleton();

    /**
     * check Bean is Prototype or not
     * @return true or false
     */
    boolean isPrototype();

    /**
     * check the bean definition is correct or not
     * @return true or false
     */
    default boolean validate() {

        if (getBeanClass() == null) {
            if (StringUtils.isBlank(getFactoryBeanName()) && 
                StringUtils.isBlank(getCreateBeanMethodName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get Constructor parameters
     * @return list of the parameters
     */
    List<?> getConstructorArg();

    //below methods are used in DI
    Constructor<?> getConstructor();
    void setConstructor(Constructor<?> constructor);
    Method getFactoryMethod();
    void setFactoryMethod(Method factoryMethod);

    // used by attribute dependency
    Map<String,Object> getPropertyKeyValue();
    void setPropertyKeyValue(Map<String,Object> properties);
}
