package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

//TODO: injection method, move similar logic to abstract class, tests
//TOTHINK map of scopes??? other bad decisions...
public class SimpleApplicationContext extends AbstractApplicationContext {
    private final Map<String, Class<?>> beanDefinitions;
    private final Map<String, Object> beans = new HashMap<>();
    private final Map<String, BeanScope> scopes = new HashMap<>();
    private ContextStatus status = ContextStatus.NOT_STARTED;

    /**
     * Создает контекст, содержащий классы, переданные в параметре.
     * <br/>
     * Если на классе нет аннотации {@code @Bean}, имя бина получается из названия класса, скоуп бина по дефолту
     * считается {@code Singleton}.
     * <br/>
     * Подразумевается, что у всех классов, переданных в списке, есть конструктор без аргументов.
     *
     * @param beanClasses классы, из которых требуется создать бины
     */
    public SimpleApplicationContext(Class<?>... beanClasses) {
        this.beanDefinitions = Arrays.stream(beanClasses).collect(
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

    /**
     * Помимо прочего, метод должен заниматься внедрением зависимостей в создаваемые объекты
     */
    @Override
    public void start() {
        beanDefinitions.forEach((beanName, beanClass) -> beans.put(beanName, instantiateBean(beanClass)));
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            if (!scopes.get(entry.getKey()).equals(BeanScope.SINGLETON)) {
                continue;
            }
            for(var field : entry.getValue().getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    field.setAccessible(true);
                    try {
                        field.set(entry.getValue(), beans.get(field.getName()));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to inject dependency: ", e);
                    }
                }
            }
            for(var method : entry.getValue().getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    try {
                        method.setAccessible(true);
                        if (method.getParameterCount() == 0) {
                            method.invoke(entry.getValue());
                        } else {
                            System.err.println("Skipping method " + method.getName() + " as it has parameters.");
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to invoke method: " + method.getName() + " due to " + e);
                    }
                }
            }
        }
        status = ContextStatus.STARTED;
    }



    @Override
    public boolean isRunning() {
        return status.equals(ContextStatus.STARTED);
    }

    @Override
    public boolean containsBean(String name) {
        if (status.equals(ContextStatus.NOT_STARTED)) {
            throw new ApplicationContextNotStartedException("ApplicationContext is not started");
        }
        return beans.containsKey(name);
    }

    @Override
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
            return bean;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
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
            }
        }
        if (Objects.equals(name, "")) {
            throw new NoSuchBeanDefinitionException(clazz.getSimpleName());
        }
        return (T) beans.get(name);
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

    private <T> T instantiateBean(Class<T> beanClass) {
        try {
            return beanClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
