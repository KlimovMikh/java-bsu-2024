package by.bsu.dependency.context;

import by.bsu.dependency.example.FirstBean;
import by.bsu.dependency.example.OtherBean;
import by.bsu.dependency.example.TestBean;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AutoScanApplicationContextTest {

    private ApplicationContext applicationContext;

    @BeforeEach
    void init() {
        applicationContext = new AutoScanApplicationContext("by.bsu.dependency.example");
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

    @Test
    void testSingleton() {
        applicationContext.start();
        assertThat(applicationContext.getBean("firstBean").equals(applicationContext.getBean("firstBean"))).isTrue();
    }

    @Test
    void testPrototype() {
        applicationContext.start();
        assertThat(applicationContext.getBean("prototypeBean").equals(applicationContext.getBean("prototypeBean"))).isFalse();
    }

    @Test
    void testChainInjection() {
        applicationContext.start();
        assertDoesNotThrow(() -> ((TestBean) applicationContext.getBean("testBean")).doSomethingWithFirst());
    }

    @Test
    void testPostConstruct() {
        applicationContext.start();
        assertThat(((TestBean) applicationContext.getBean("testBean")).getX() != 0).isTrue();
    }

    @Test
    void testPrototypeInjection() {
        applicationContext.start();
        assertDoesNotThrow(() -> ((TestBean) applicationContext.getBean("testBean")).doSomethingWithPrototype());
    }

    @Test
    void testCyclicalDependencies() {
        // behaviour in cyclical dependencies does not raise errors, but may become unexpected
        ApplicationContext cyclicalContext = new AutoScanApplicationContext("by.bsu.dependency.cyclical");
        cyclicalContext.start();
        System.out.println(cyclicalContext.getBean("cyclicalBean1"));
        System.out.println(cyclicalContext.getBean("cyclicalBean2"));
    }
}



