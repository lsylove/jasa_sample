package jasa_sample;

import net.sourceforge.jabm.DesktopSimulationManager;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.spring.PropertyOverrideWithReferencesConfigurer;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;
import java.net.URL;

public class Launcher {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ScopeConfig.class);
        context.register(SampleMain.class);
        context.refresh();
        final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory(context);
        final DesktopSimulationManager manager = new DesktopSimulationManager() {
            @Override
            public SimulationController getSimulationController() {
                return (SimulationController) beanFactory.getBean("simulationController");
            }

            @Override
            protected ImageIcon createImageIcon(String path, String description) {
                URL imgURL = ClassLoader.getSystemResource("net/sourceforge/jabm/" + path);
                if (imgURL != null) {
                    return new ImageIcon(imgURL, description);
                } else {
                    throw new RuntimeException("Couldn't find file: " + path);
                }
            }

            @Override
            public void runSingleExperiment() {
                if (this.simulationProperties != null) {
                    PropertyOverrideConfigurer configurer = new PropertyOverrideWithReferencesConfigurer();
                    configurer.setProperties(simulationProperties);
                    configurer.postProcessBeanFactory(beanFactory);
                }
                launch((SimulationController) beanFactory.getBean("simulationController"));
            }
        };
        manager.initialise();
    }
}
