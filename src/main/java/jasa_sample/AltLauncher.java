package jasa_sample;

import net.sourceforge.jabm.DesktopSimulationManager;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.spring.PropertyOverrideWithReferencesConfigurer;
import net.sourceforge.jabm.spring.SimulationScope;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import javax.swing.*;
import java.net.URL;

public class AltLauncher {
    //public static void main(String[] args) {
        // new DesktopSimulationManager().initialise();
    //}

    public static void main(String[] args) {
        final DefaultListableBeanFactory context = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions("config/main.xml");
        context.registerScope(SimulationScope.ATTRIBUTE_VALUE, SimulationScope.getSingletonInstance());
        final DesktopSimulationManager manager = new DesktopSimulationManager() {
            @Override
            public SimulationController getSimulationController() {
                return (SimulationController) context.getBean("simulationController");
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
                    configurer.postProcessBeanFactory(context);
                }
                launch((SimulationController) context.getBean("simulationController"));
            }
        };
        manager.initialise();
    }
}
