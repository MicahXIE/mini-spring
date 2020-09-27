package com.micah.factory;

/**
 * Bean factory interface
 * @author micah
 * @create 2020-09-26 13:20
 **/
public interface BeanFactory {

    /**
     * Get reference Bean name
     * @param beanName
     * @return Bean
     */
    Object getBean(String beanName) throws Exception;;

}
