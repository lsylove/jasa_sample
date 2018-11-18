package jasa_sample;

import cern.jet.random.engine.MersenneTwister64;
import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.SimulationController;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.agent.AgentList;
import net.sourceforge.jabm.distribution.UniformDistribution;
import net.sourceforge.jabm.evolution.*;
import net.sourceforge.jabm.init.AgentInitialiser;
import net.sourceforge.jabm.mixing.AgentMixer;
import net.sourceforge.jabm.mixing.RandomArrivalAgentMixer;
import net.sourceforge.jabm.util.MutableDoubleWrapper;
import net.sourceforge.jasa.agent.MarketAgentInitialiser;
import net.sourceforge.jasa.agent.SimpleTradingAgent;
import net.sourceforge.jasa.agent.strategy.ForecastTradeDirectionPolicy;
import net.sourceforge.jasa.agent.strategy.SimpleMarkupStrategy;
import net.sourceforge.jasa.agent.valuation.ChartistForecaster;
import net.sourceforge.jasa.agent.valuation.GeometricBrownianMotionPriceProcess;
import net.sourceforge.jasa.agent.valuation.ReturnForecastValuationPolicy;
import net.sourceforge.jasa.agent.valuation.evolution.ForecastErrorFitnessFunction;
import net.sourceforge.jasa.agent.valuation.evolution.ValuationPolicyImitationOperator;
import net.sourceforge.jasa.agent.valuation.evolution.WeightMutationOperator;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketSimulation;
import net.sourceforge.jasa.market.auctioneer.Auctioneer;
import net.sourceforge.jasa.market.auctioneer.ContinuousDoubleAuctioneer;
import net.sourceforge.jasa.market.rules.TimePriorityPricingPolicy;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import java.util.Arrays;
import java.util.Date;

@Configuration
public class Model {
    @Bean
    @Scope("simulation")
    public MarketSimulation marketSimulation(@Lazy SimulationController simulationController,
                                             @Lazy Population evolvingPopulation,
                                             AgentMixer randomArrivalAgentMixer,
                                             @Lazy AgentInitialiser agentInitialiserProxy,
                                             Auctioneer cda) {
        MarketSimulation ret = new MarketSimulation();
        ret.setSimulationController(simulationController);
        ret.setMaximumDays(1);
        ret.setLengthOfDay(200000);
        ret.setPopulation(evolvingPopulation);
        ret.setAgentMixer(randomArrivalAgentMixer);
        ret.setAgentInitialiser(agentInitialiserProxy);
        ret.setAuctioneer(cda);
        ret.setInitialPrice(100);
        return ret;
    }

    @Bean
    @Scope("simulation")
    public TimePriorityPricingPolicy timePriorityPricing() {
        return new TimePriorityPricingPolicy();
    }

    @Bean
    @Scope("simulation")
    public ContinuousDoubleAuctioneer cda(TimePriorityPricingPolicy pricingPolicy) {
        ContinuousDoubleAuctioneer ret = new ContinuousDoubleAuctioneer();
        ret.setPricingPolicy(pricingPolicy);
        return ret;
    }

    @Bean
    @Scope("simulation")
    public RandomArrivalAgentMixer randomArrivalAgentMixer(MersenneTwister64 prng) {
        RandomArrivalAgentMixer ret = new RandomArrivalAgentMixer(prng);
        ret.setArrivalProbability(0.5);
        return ret;
    }

    @Bean
    @Scope("simulation")
    public ProxyFactoryBean agentInitialiserProxy() {
        LazyInitTargetSource src = new LazyInitTargetSource();
        src.setTargetBeanName("agentInitialiser");
        ProxyFactoryBean ret = new ProxyFactoryBean();
        ret.setTargetSource(src);
        return ret;
    }

    @Bean
    @Scope("simulation")
    public MarketAgentInitialiser agentInitialiser(MarketSimulation marketSimulation) {
        MarketAgentInitialiser ret = new MarketAgentInitialiser();
        ret.setMarket(marketSimulation);
        return ret;
    }

    @Bean
    public MersenneTwister64 prng() {
        return new MersenneTwister64(new Date());
    }

    @Bean
    @Scope("simulation")
    public AgentList chartists(ObjectFactory<Agent> chartistFactory) {
        AgentList ret = new AgentList();
        ret.setSize(200);
        ret.setAgentFactory(chartistFactory);
        ret.populateFromFactory();
        return ret;
    }

    @Bean
    @Scope("simulation")
    public ObjectFactoryCreatingFactoryBean chartistFactory() {
        ObjectFactoryCreatingFactoryBean ret = new ObjectFactoryCreatingFactoryBean();
        ret.setTargetBeanName("chartistPrototype");
        return ret;
    }

    @Bean
    @Scope("prototype")
    public SimpleTradingAgent chartistPrototype(SimpleMarkupStrategy returnForecastStrategy,
                                                ReturnForecastValuationPolicy chartistValuationPolicy) {
        SimpleTradingAgent ret = new SimpleTradingAgent();
        ret.setStrategy(returnForecastStrategy);
        ret.setValuationPolicy(chartistValuationPolicy);
        return ret;
    }

    @Bean
    @Scope("prototype")
    public SimpleMarkupStrategy returnForecastStrategy(SimulationController simulationController,
                                                       UniformDistribution markupDistribution,
                                                       ForecastTradeDirectionPolicy tradeDirectionPolicy,
                                                       MersenneTwister64 prng) {
        SimpleMarkupStrategy ret = new SimpleMarkupStrategy();
        ret.setScheduler(simulationController);
        ret.setMarkupDistribution(markupDistribution);
        ret.setPrng(prng);
        ret.setTradeDirectionPolicy(tradeDirectionPolicy);
        return ret;
    }

    @Bean
    public ForecastTradeDirectionPolicy tradeDirectionPolicy(MersenneTwister64 prng) {
        ForecastTradeDirectionPolicy ret = new ForecastTradeDirectionPolicy();
        ret.setPrng(prng);
        return ret;
    }

    @Bean
    public UniformDistribution markupDistribution(MersenneTwister64 prng) {
        UniformDistribution ret = new UniformDistribution();
        ret.setMax(0.5);
        ret.setMin(0.);
        ret.setPrng(prng);
        return ret;
    }

    @Bean
    public MutableDoubleWrapper gbmFundamentalPrice() {
        MutableDoubleWrapper ret = new MutableDoubleWrapper();
        ret.setValue(500.);
        return ret;
    }

    @Bean
    @Scope("simulation")
    public GeometricBrownianMotionPriceProcess gbmPriceProcess(MutableDoubleWrapper gbmFundamentalPrice,
                                                               MersenneTwister64 prng) {
        GeometricBrownianMotionPriceProcess ret = new GeometricBrownianMotionPriceProcess();
        ret.setPriceWrapper(gbmFundamentalPrice);
        ret.setDrift(0.2);
        ret.setVolatility(0.4);
        ret.setDt(0.00005);
        ret.setPrng(prng);
        return ret;
    }

    @Bean
    @Scope("prototype")
    public ReturnForecastValuationPolicy chartistValuationPolicy(ChartistForecaster chartistForecaster) {
        ReturnForecastValuationPolicy ret = new ReturnForecastValuationPolicy();
        ret.setForecaster(chartistForecaster);
        return ret;
    }

    @Bean
    @Scope("prototype")
    public ChartistForecaster chartistForecaster(final MarketSimulation mark,
                                                 UniformDistribution chartistWindowSizeDistribution) {
        class _internal extends  ChartistForecaster {
            private _internal() {
                this.market = mark;
            }
        }
        ChartistForecaster ret = new _internal();
        ret.setWindowSizeDistribution(chartistWindowSizeDistribution);
        ret.setTimeHorizon(200.);
        return ret;
    }

    @Bean
    @Scope("prototype")
    public UniformDistribution chartistWindowSizeDistribution(MersenneTwister64 prng) {
        UniformDistribution ret = new UniformDistribution();
        ret.setMin(200.);
        ret.setMax(2000.);
        ret.setPrng(prng);
        return ret;
    }

    @Bean
    @Scope("simulation")
    public EvolvingPopulation evolvingPopulation(AgentList agentList,
                                                 MersenneTwister64 prng,
                                                 CombiBreeder uniformBreeder) {
        EvolvingPopulation ret = new EvolvingPopulation();
        ret.setAgentList(agentList);
        ret.setPrng(prng);
        ret.setBreeder(uniformBreeder);
        ret.setBreedingInterval(1);
        return ret;
    }

    @Bean
    public CombiBreeder uniformBreeder(//Breeder mutationBreeder,
                                       Breeder fitnessProportionateBreeder) {
        CombiBreeder ret = new CombiBreeder();
        ret.setBreedingPipeline(Arrays.asList(//mutationBreeder,
                fitnessProportionateBreeder));
        return ret;
    }

    @Bean
    public MutationBreeder mutationBreeder(MersenneTwister64 prng) {
        MutationBreeder ret = new MutationBreeder();
        ret.setPrng(prng);
        ret.setMutationOperator(new WeightMutationOperator());
        ret.setMutationProbability(0.005);
        return ret;
    }

    @Bean
    @Scope("simulation")
    public FitnessProportionateBreeder fitnessProportionateBreeder(MersenneTwister64 prng) {
        FitnessProportionateBreeder ret = new FitnessProportionateBreeder();
        ret.setPrng(prng);
        ret.setFitnessFunction(new ForecastErrorFitnessFunction());
        ret.setImitationOperator(new ValuationPolicyImitationOperator());
        ret.setImitationProbability(0.2);
        return ret;
    }
}
