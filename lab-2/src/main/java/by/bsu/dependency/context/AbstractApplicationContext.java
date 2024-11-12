package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractApplicationContext implements ApplicationContext {
    protected Map<String, Class<?>> beanDefinitions;
    protected Map<String, Object> beans = new HashMap<>();
    protected ContextStatus status = ContextStatus.NOT_STARTED;
    protected Map<String, BeanScope> scopes = new HashMap<>();

    protected enum ContextStatus {
        NOT_STARTED,
        STARTED
    }

    protected void initialize(Stream<Class<?>> beanClasses) {
        this.beanDefinitions = beanClasses.collect(
                Collectors.toMap(
                        beanClass -> {
                            if(beanClass.isAnnotationPresent(Bean.class)) {
                                scopes.put(beanClass.getAnnotation(Bean.class).name(),
                                        beanClass.getAnnotation(Bean.class).scope());
                                return beanClass.getAnnotation(Bean.class).name();
                            }
                            String name = beanClass.getSimpleName();
                            if (Character.isUpperCase(name.charAt(0))) {
                                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                            } else {
                                name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                            }
                            scopes.put(name, BeanScope.SINGLETON);
                            return name;
                        },
                        Function.identity()
                )
        );
    }

    protected void initializeStart() {
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            if (!scopes.get(entry.getKey()).equals(BeanScope.SINGLETON)) {
                continue;
            }
            inject(entry.getKey(), entry.getValue());
            postConstruct(entry.getValue());
        }
    }

    protected void inject(String name, Object bean) {
        for(var field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                try {
                    field.set(bean, beans.get(field.getName()));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject dependency: ", e);
                }
            }
        }
    }

    protected void postConstruct(Object bean) {
        for(var method : bean.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                try {
                    method.setAccessible(true);
                    if (method.getParameterCount() == 0) {
                        method.invoke(bean);
                    } else {
                        System.err.println("Skipping method " + method.getName() + " as it has parameters.");
                    }
                } catch (Exception e) {
                    System.err.println("Failed to invoke method: " + method.getName() + " due to " + e);
                }
            }
        }
    }

    protected <T> T instantiateBean(Class<T> beanClass) {
        try {
            return beanClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        return status.equals(ContextStatus.STARTED);
    }

    public boolean containsBean(String name) {
        if (status.equals(ContextStatus.NOT_STARTED)) {
            throw new ApplicationContextNotStartedException("ApplicationContext is not started");
        }
        return beans.containsKey(name);
    }

    public Object getBean(String name) {
        if (status.equals(ContextStatus.NOT_STARTED)) {
            throw new ApplicationContextNotStartedException("ApplicationContext is not started");
        }
        if (!beans.containsKey(name)) {
            throw new NoSuchBeanDefinitionException(name);
        }
        if(scopes.get(name) == BeanScope.SINGLETON) {
            return beans.get(name);
        } else {
            var bean = instantiateBean(beanDefinitions.get(name));
            inject(name, bean);
            postConstruct(bean);
            return bean;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        if (status.equals(ContextStatus.NOT_STARTED)) {
            throw new ApplicationContextNotStartedException("ApplicationContext is not started");
        }
        if (!beanDefinitions.containsValue(clazz)) {
            throw new NoSuchBeanDefinitionException(clazz.getSimpleName());
        }
        String name = "";
        for (Map.Entry<String, Class<?>> entry : beanDefinitions.entrySet()) {
            if (Objects.equals(clazz, entry.getValue())) {
                name = entry.getKey();
                break;
            }
        }
        if (Objects.equals(name, "")) {
            throw new NoSuchBeanDefinitionException(clazz.getSimpleName());
        }
        return (T) getBean(name);
    }

    @Override
    public boolean isPrototype(String name) {
        if (!scopes.containsKey(name)) {
            throw new NoSuchBeanDefinitionException(name);
        }
        return scopes.get(name).equals(BeanScope.PROTOTYPE);
    }

    @Override
    public boolean isSingleton(String name) {
        if (!scopes.containsKey(name)) {
            throw new NoSuchBeanDefinitionException(name);
        }
        return scopes.get(name).equals(BeanScope.SINGLETON);
    }
}
