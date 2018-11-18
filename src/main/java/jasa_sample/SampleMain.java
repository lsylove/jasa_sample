package jasa_sample;

import net.sourceforge.jabm.SpringSimulationController;
import net.sourceforge.jabm.report.Report;
import net.sourceforge.jabm.util.MutableStringWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan({"jasa_sample"})
public class SampleMain {
    @Bean
    public SpringSimulationController simulationController(Report priceTimeSeriesChart,
                                                           Report currentPriceTimeSeriesChart,
                                                           Report equilibriumPriceTimeSeriesChart,
                                                           Report currentPriceCSVReport) {
        SpringSimulationController ret = new SpringSimulationController();
        ret.setSimulationBeanName("marketSimulation");
        ret.setNumSimulations(10);
        ret.setReports(new ArrayList<>(Arrays.asList(priceTimeSeriesChart, currentPriceTimeSeriesChart, equilibriumPriceTimeSeriesChart, currentPriceCSVReport)));
        ret.setModelDescription("Iori and Chiarella - A Simulation analysis of a the microstructure of double auction markets");
        return ret;
    }

    @Bean
    public MutableStringWrapper fileNamePrefix() {
        return new MutableStringWrapper("data/");
    }
}
