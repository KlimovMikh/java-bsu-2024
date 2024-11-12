package by.bsu.dependency.context;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

public class HardCodedSingletonApplicationContext extends AbstractApplicationContext {

    /**
     * ! Класс существует только для базового примера !
     * <br/>
     * Создает контекст, содержащий классы, переданные в параметре. Полагается на отсутсвие зависимостей в бинах,
     * а также на наличие аннотации {@code @Bean} на переданных классах.
     * <br/>
     * ! Контекст данного типа не занимается внедрением зависимостей !
     * <br/>
     * ! Создает только бины со скоупом {@code SINGLETON} !
     *
     * @param beanClasses классы, из которых требуется создать бины
     */
    public HardCodedSingletonApplicationContext(Class<?>... beanClasses) {
        this.beanDefinitions = Arrays.stream(beanClasses).collect(
                Collectors.toMap(
                        beanClass -> beanClass.getAnnotation(Bean.class).name(),
                        Function.identity()
                )
        );
    }

    @Override
    public void start() {
        beanDefinitions.forEach((beanName, beanClass) -> beans.put(beanName, instantiateBean(beanClass)));
        status = ContextStatus.STARTED;
    }

//    same methods as in AbstractApplicationContext

//    @Override
//    public boolean isRunning() {
//        return status.equals(ContextStatus.STARTED);
//    }

//    @Override
//    public boolean containsBean(String name) {
//        if (status.equals(ContextStatus.NOT_STARTED)) {
//            throw new ApplicationContextNotStartedException("ApplicationContext is not started");
//        }
//        return beans.containsKey(name);
//    }

    @Override
    public Object getBean(String name) {
        if (status.equals(ContextStatus.NOT_STARTED)) {
            throw new ApplicationContextNotStartedException("ApplicationContext is not started");
        }
        if (!beans.containsKey(name)) {
            throw new NoSuchBeanDefinitionException("Bean of name " + name + " not found");
        }
        return beans.get(name);
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        if (status.equals(ContextStatus.NOT_STARTED)) {
            throw new ApplicationContextNotStartedException("ApplicationContext is not started");
        }
        return beans.values().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst()
                .orElseThrow(() -> new NoSuchBeanDefinitionException("Bean of class " + clazz.getName() + " not found"));
    }

    @Override
    public boolean isPrototype(String name) {
        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException(name);
        }
        return false;
    }

    @Override
    public boolean isSingleton(String name) {
        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException(name);
        }
        return true;
    }
}
