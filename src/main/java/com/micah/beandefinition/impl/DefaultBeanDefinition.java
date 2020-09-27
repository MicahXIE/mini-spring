package com.micah.beandefinition.impl;

import com.micah.property.PropertyValue;
import com.micah.beandefinition.BeanDefinition;
import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Bean definition implementation
 * @author micah
 * @create 2020-09-26 13:20
 **/
@Data
public class DefaultBeanDefinition implements BeanDefinition {

    private Class<?> clazz;

    private String factoryBeanName;

    private String createBeanMethodName;

    private String staticCreateBeanMethodName;

    private String beanInitMethodName;

    private String beanDestroyMethodName;

    private String scope = BeanDefinition.SCOPE_SINGLETON;

    private Constructor constructor;

    private Method method;

    private List<?> constructorArg;

    private Map<String,Object> values;

    @Override
    public Class<?> getBeanClass() {
        return this.clazz;
    }

    public void setBeanClass(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    @Override
    public String getCreateBeanMethodName() {
        return this.createBeanMethodName;
    }

    @Override
    public String getStaticCreateBeanMethodName() {
        return this.staticCreateBeanMethodName;
    }

    @Override
    public String getBeanInitMethodName() {
        return this.beanInitMethodName;
    }

    @Override
    public String getBeanDestroyMethodName() {
        return this.beanDestroyMethodName;
    }

    public void setScope(String scope) {
        if (StringUtils.isNotBlank(scope)) {
            this.scope = scope;
        }
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public boolean isSingleton() {
        return BeanDefinition.SCOPE_SINGLETON.equals(this.scope);
    }

    @Override
    public boolean isPrototype() {
        return BeanDefinition.SCOPE_PROTOTYPE.equals(this.scope);
    }

    @Override
    public List<?> getConstructorArg() {
        return this.constructorArg;
    }

    @Override
    public Constructor<?> getConstructor() {
        return this.constructor;
    }

    @Override
    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    @Override
    public Method getFactoryMethod() {
        return this.method;
    }

    @Override
    public void setFactoryMethod(Method factoryMethod) {
        this.method = method;
    }

    @Override
    public Map<String, Object> getPropertyKeyValue() {
        return this.values;
    }

    @Override
    public void setPropertyKeyValue(Map<String, Object> properties) {
        this.values = properties;
    }
}
