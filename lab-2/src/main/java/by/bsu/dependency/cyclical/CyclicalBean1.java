package by.bsu.dependency.cyclical;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name = "cyclicalBean1")
public class CyclicalBean1 {

    @SuppressWarnings("unused")
    @Inject
    private CyclicalBean2 cyclicalBean2;

    @SuppressWarnings("unused")
    public CyclicalBean2 getCyclicalBean2() {
        return cyclicalBean2;
    }

    void doSomething() {
        System.out.println("Hi, I'm cyclicalBean1 bean");
    }

    @SuppressWarnings("unused")
    public void doSomethingWithSecond() {
        System.out.println("Trying to shake cyclical bean 2...");
        cyclicalBean2.doSomething();
    }

    @SuppressWarnings("unused")
    @PostConstruct
    void postConstruct() {
        System.out.println("PostConstruct bean running, dependency on cyclicalBean2");
        cyclicalBean2.doSomething();
    }
}
