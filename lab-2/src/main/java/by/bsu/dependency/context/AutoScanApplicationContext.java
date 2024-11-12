package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AutoScanApplicationContext extends AbstractApplicationContext {
    /**
     * Создает контекст, содержащий классы из пакета {@code packageName}, помеченные аннотацией {@code @Bean}.
     * <br/>
     * Если имя бина в анноации не указано ({@code name} пустой), оно берется из названия класса.
     * <br/>
     * Подразумевается, что у всех классов, переданных в списке, есть конструктор без аргументов.
     *
     * @param packageName имя сканируемого пакета
     * NOTE: suppress deprecation warning because of Reflections constructor
     */
    @SuppressWarnings("deprecation")
    public AutoScanApplicationContext(String packageName) {
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        initialize(reflections.getSubTypesOf(Object.class).stream());
    }

    @Override
    public void start() {
        beanDefinitions.forEach((beanName, beanClass) -> beans.put(beanName, instantiateBean(beanClass)));
        initializeStart();
        status = ContextStatus.STARTED;
    }
}
