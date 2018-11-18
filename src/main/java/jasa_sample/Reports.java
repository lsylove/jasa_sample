package jasa_sample;

import net.sourceforge.jabm.report.*;
import net.sourceforge.jabm.util.MutableStringWrapper;
import net.sourceforge.jabm.view.TimeSeriesChart;
import net.sourceforge.jasa.agent.valuation.GeometricBrownianMotionPriceProcess;
import net.sourceforge.jasa.report.CurrentPriceReportVariables;
import net.sourceforge.jasa.report.EquilibriumReportVariables;
import org.jfree.data.io.CSV;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class Reports {
    @Bean
    public CurrentPriceReportVariables currentPriceReportVariables() {
        return new CurrentPriceReportVariables();
    }

    @Bean
    public SeriesReportVariables currentPriceTimeSeries(CurrentPriceReportVariables currentPriceReportVariables) {
        SeriesReportVariables ret = new SeriesReportVariables();
        ret.setReportVariables(currentPriceReportVariables);
        return ret;
    }
    @Bean
    public EquilibriumReportVariables equilibriumPriceReportVariables() {
        return new EquilibriumReportVariables();
    }

    @Bean
    public SeriesReportVariables equilibriumPriceTimeSeries(EquilibriumReportVariables equilibriumPriceReportVariables) {
        SeriesReportVariables ret = new SeriesReportVariables();
        ret.setReportVariables(equilibriumPriceReportVariables);
        return ret;
    }

    @Bean
    public SeriesReportVariables gbmPriceTimeSeries(GeometricBrownianMotionPriceProcess gbmPriceProcess) {
        SeriesReportVariables ret = new SeriesReportVariables();
        ret.setReportVariables(gbmPriceProcess);
        return ret;
    }

    @Bean
    public CombiSeriesReportVariables priceTimeSeries(Timeseries gbmPriceTimeSeries,
                                                      Timeseries currentPriceTimeSeries,
                                                      Timeseries equilibriumPriceTimeSeries) {
        CombiSeriesReportVariables ret = new CombiSeriesReportVariables();
        ret.setSeriesList(Arrays.asList(gbmPriceTimeSeries, currentPriceTimeSeries, equilibriumPriceTimeSeries));
        return ret;
    }

    @Bean
    public TimeSeriesChart priceTimeSeriesChart(CombiSeriesReportVariables priceTimeSeries) {
        TimeSeriesChart ret = new TimeSeriesChart();
        ret.setSeries(priceTimeSeries);
        ret.setChartTitle("Price Time Series");
        ret.setRangeAxisLabel("Price ($)");
        return ret;
    }

    @Bean
    public TimeSeriesChart currentPriceTimeSeriesChart(SeriesReportVariables currentPriceTimeSeries) {
        TimeSeriesChart ret = new TimeSeriesChart();
        ret.setSeries(currentPriceTimeSeries);
        ret.setChartTitle("Current Price");
        return ret;
    }

    @Bean
    public TimeSeriesChart equilibriumPriceTimeSeriesChart(SeriesReportVariables equilibriumPriceTimeSeries) {
        TimeSeriesChart ret = new TimeSeriesChart();
        ret.setSeries(equilibriumPriceTimeSeries);
        ret.setChartTitle("Equilibrium Price");
        return ret;
    }

    @Bean
    public CSVReportVariables currentPriceCSVReportVariables(ReportVariables currentPriceReportVariables,
                                                             MutableStringWrapper fileNamePrefix) {
        CSVReportVariables ret = new CSVReportVariables();
        ret.setReportVariables(currentPriceReportVariables);
        ret.setFileNamePrefix(fileNamePrefix);
        ret.setFileNameSuffix("current_price");
        ret.setFileNameExtension(".csv");
        ret.setPassThrough(true);
        return ret;
    }

    @Bean
    public InteractionIntervalReport currentPriceCSVReport(CSVReportVariables currentPriceCSVReportVariables) {
        InteractionIntervalReport ret = new InteractionIntervalReport();
        ret.setReportVariables(currentPriceCSVReportVariables);
        ret.setSampleInterval(1);
        return ret;
    }
}
