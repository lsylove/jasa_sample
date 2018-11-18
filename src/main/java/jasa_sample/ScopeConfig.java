package jasa_sample;

import net.sourceforge.jabm.spring.SimulationScope;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScopeConfig {
    @Bean
    public BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return new BeanFactoryPostProcessor() {
            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
                factory.registerScope(SimulationScope.ATTRIBUTE_VALUE, SimulationScope.getSingletonInstance());
            }
        };
    }
}
