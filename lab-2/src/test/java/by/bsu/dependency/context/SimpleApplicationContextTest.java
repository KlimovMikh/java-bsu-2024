package by.bsu.dependency.context;

import by.bsu.dependency.example.FirstBean;
import by.bsu.dependency.example.OtherBean;
import by.bsu.dependency.example.PrototypeBean;
import by.bsu.dependency.example.TestBean;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleApplicationContextTest {

    private ApplicationContext applicationContext;

    @BeforeEach
    void init() {
        applicationContext = new SimpleApplicationContext(FirstBean.class, OtherBean.class,
                PrototypeBean.class, TestBean.class);
    }

    @Test
    void testIsRunning() {
        assertThat(applicationContext.isRunning()).isFalse();
        applicationContext.start();
        assertThat(applicationContext.isRunning()).isTrue();
    }

    @Test
    void testContextContainsNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.containsBean("firstBean")
        );
    }

    @Test
    void testContextContainsBeans() {
        applicationContext.start();

        assertThat(applicationContext.containsBean("firstBean")).isTrue();
        assertThat(applicationContext.containsBean("otherBean")).isTrue();
        assertThat(applicationContext.containsBean("randomName")).isFalse();
    }

    @Test
    void testContextGetBeanNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.getBean("firstBean")
        );
    }

    @Test
    void testGetBeanReturns() {
        applicationContext.start();

        assertThat(applicationContext.getBean("firstBean")).isNotNull().isInstanceOf(FirstBean.class);
        assertThat(applicationContext.getBean("otherBean")).isNotNull().isInstanceOf(OtherBean.class);
    }

    @Test
    void testGetBeanThrows() {
        applicationContext.start();

        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.getBean("randomName")
        );
    }

    @Test
    void testIsSingletonReturns() {
        assertThat(applicationContext.isSingleton("firstBean")).isTrue();
        assertThat(applicationContext.isSingleton("otherBean")).isTrue();
    }

    @Test
    void testIsSingletonThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isSingleton("randomName")
        );
    }

    @Test
    void testIsPrototypeReturns() {
        assertThat(applicationContext.isPrototype("firstBean")).isFalse();
        assertThat(applicationContext.isPrototype("otherBean")).isFalse();
    }

    @Test
    void testIsPrototypeThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isPrototype("randomName")
        );
    }

    @Test
    void testInjection() {
        applicationContext.start();
        assertDoesNotThrow(() -> ((OtherBean) applicationContext.getBean("otherBean")).doSomethingWithFirst());
        assertThat(applicationContext.getBean("firstBean")).isNotNull().isInstanceOf(FirstBean.class);
        assertThat(applicationContext.getBean("otherBean")).isNotNull().isInstanceOf(OtherBean.class);
    }
}

//further test development

//class SimpleApplicationContextTest {
//
//    private ApplicationContext applicationContext;
//
//    @BeforeEach
//    void init() {
//        applicationContext = new SimpleApplicationContext(FirstBean.class, OtherBean.class, PrototypeBean.class);
//    }
//
//    @Test
//    void testIsRunning() {
//        assertThat(applicationContext.isRunning()).isFalse();
//        applicationContext.start();
//        assertThat(applicationContext.isRunning()).isTrue();
//    }
//
//    @Test
//    void testContextContainsNotStarted() {
//        assertThrows(
//                ApplicationContextNotStartedException.class,
//                () -> applicationContext.containsBean("firstBean")
//        );
//    }
//
//    @Test
//    void testContextContainsBeans() {
//        applicationContext.start();
//
//        assertThat(applicationContext.containsBean("firstBean")).isTrue();
//        assertThat(applicationContext.containsBean("otherBean")).isTrue();
//        assertThat(applicationContext.containsBean("prototypeBean")).isTrue();
//        assertThat(applicationContext.containsBean("randomName")).isFalse();
//    }
//
//    @Test
//    void testContextGetBeanNotStarted() {
//        assertThrows(
//                ApplicationContextNotStartedException.class,
//                () -> applicationContext.getBean("firstBean")
//        );
//    }
//
//    @Test
//    void testGetBeanReturns() {
//        applicationContext.start();
//
//        assertThat(applicationContext.getBean("firstBean")).isNotNull().isInstanceOf(FirstBean.class);
//        assertThat(applicationContext.getBean("otherBean")).isNotNull().isInstanceOf(OtherBean.class);
//        assertThat(applicationContext.getBean("prototypeBean")).isNotNull().isInstanceOf(PrototypeBean.class);
//    }
//
//    @Test
//    void testGetBeanThrows() {
//        applicationContext.start();
//
//        assertThrows(
//                NoSuchBeanDefinitionException.class,
//                () -> applicationContext.getBean("randomName")
//        );
//    }
//
//    @Test
//    void testIsSingletonReturns() {
//        applicationContext.start();
//
//        assertThat(applicationContext.isSingleton("firstBean")).isTrue();
//        assertThat(applicationContext.isSingleton("otherBean")).isTrue();
//        assertThat(applicationContext.isSingleton("prototypeBean")).isFalse();
//    }
//
//    @Test
//    void testIsSingletonThrows() {
//        assertThrows(
//                NoSuchBeanDefinitionException.class,
//                () -> applicationContext.isSingleton("randomName")
//        );
//    }
//
//    @Test
//    void testIsPrototypeReturns() {
//        applicationContext.start();
//
//        assertThat(applicationContext.isPrototype("firstBean")).isFalse();
//        assertThat(applicationContext.isPrototype("otherBean")).isFalse();
//        assertThat(applicationContext.isPrototype("prototypeBean")).isTrue();
//    }
//
//    @Test
//    void testIsPrototypeThrows() {
//        assertThrows(
//                NoSuchBeanDefinitionException.class,
//                () -> applicationContext.isPrototype("randomName")
//        );
//    }
//}

