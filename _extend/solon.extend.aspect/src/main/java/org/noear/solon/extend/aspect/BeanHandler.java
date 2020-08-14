package org.noear.solon.extend.aspect;

import org.noear.solon.core.ClassWrap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class BeanHandler implements InvocationHandler {
    private Object bean;
    private ClassWrap clazzWrap;
    private Object proxy;

    public BeanHandler(Object bean) {
        this(bean.getClass(), bean);
    }

    public BeanHandler(Class<?> clazz, Object bean) {
        try {
            Constructor constructor = clazz.getConstructor(new Class[]{});
            Object[] constructorParam = new Object[]{};

            this.bean = bean;
            this.clazzWrap = ClassWrap.get(clazz);
            this.proxy = Proxy.newProxyInstance(clazz.getClassLoader(), this, clazz, constructor, constructorParam);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    public Object getProxy() {
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        method.setAccessible(true);

        Object result = clazzWrap.getMethodWrap(method).invokeAndTran(bean, args);

        return result;
    }
}
