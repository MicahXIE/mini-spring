package com.micah.beandefinition;

/**
 * Register Bean definition
 * @author micah
 * @create 2020-09-26 15:20
 **/
public interface BeanDefinitionRegistry {

    /**
     * Register Bean definition
     * @param Bean definition
     * @param Bean name
     * @throws Exception
     */
    void registerBeanDefinition(BeanDefinition beanDefinition, String beanName) throws Exception;

    /**
     * Check whether Bean is registered
     * @param beanName
     * @return true or false
     */
    boolean containsBeanDefinition(String beanName);

    /**
     * Get Bean definition
     * @param beanName
     * @return BeanDefinition
     */
    BeanDefinition getBeanDefinition(String beanName);
}
