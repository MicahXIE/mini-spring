package com.micah.beanreference;

/**
 * Reference type in attribute dependence 
 * @author micah
 * @create 2020-09-26 13:20
 **/
public class BeanReference {

    private String beanName;


    public BeanReference(String beanName){
        super();
        this.beanName = beanName;
    }
    /**
     * Get reference Bean name
     * @return beanName
     */
    public String getBeanName() {
        return beanName;
    }
    /**
     * Set reference Bean name
     * @param beanName
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
