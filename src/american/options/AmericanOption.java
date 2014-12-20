package american.options;

import java.util.HashMap;
import mathematical.matrix.*;

/**
 * Created by casa on 3/11/14.
 */
public abstract class AmericanOption {
    private BinomialModel model;
    private UpTriangularMatrix values;
    private HashMap<String, Function> greeks;
    private final double increasingFactor = 1.1;

    private interface Function {
        public double increases();
    }

    private class Delta implements Function {
        @Override
        public double increases(){
            AmericanOption.this.model.setSpot(AmericanOption.this.model.getSpot()*increasingFactor);
            return (increasingFactor - 1) * AmericanOption.this.model.getSpot();
        }
    }

    private class Rho implements Function {
        @Override
        public double increases(){
            AmericanOption.this.model.setRate(AmericanOption.this.model.getRate() * increasingFactor);
            return (increasingFactor - 1) * AmericanOption.this.model.getRate();
        }
    }

    private class Vega implements Function {
        @Override
        public double increases(){
            AmericanOption.this.model.setVolatility(AmericanOption.this.model.getVolatility() * increasingFactor);
            return (increasingFactor - 1) * AmericanOption.this.model.getVolatility();
        }
    }

    private class Dividend implements Function {
        @Override
        public double increases() {
            AmericanOption.this.model.setDividendRate(AmericanOption.this.model.getDividendRate() * increasingFactor);
            return(increasingFactor - 1) * AmericanOption.this.model.getDividendRate();
        }
    }

    private class Theta implements Function {
        @Override
        public double increases() {
            AmericanOption.this.model.setTimeToMaturity(AmericanOption.this.model.getTimeToMaturity() * increasingFactor);
            return - (increasingFactor - 1) * AmericanOption.this.model.getTimeToMaturity();
        }
    }


    public AmericanOption(BinomialModel model){
        this.model = model;
        values = new UpTriangularMatrix();
        greeks = new HashMap<String, Function>();
        greeks.clear();
        greeks.put("DELTA", new Delta());
        greeks.put("RHO", new Rho());
        greeks.put("VEGA", new Vega());
        greeks.put("DIVIDEND", new Dividend());
        greeks.put("THETA", new Theta());
    }

    public AmericanOption(double spot, double rate, double dividendRate, double volatility,
                          int numberOfSteps, double timeToMaturity){
        this.model = new BinomialModel(spot, rate, dividendRate, volatility, numberOfSteps,
                                        timeToMaturity);
        values = new UpTriangularMatrix();
    }

    public BinomialModel getModel(){
        return this.model;
    }

    public void setModel(BinomialModel model){
        this.model = model;
    }

    public abstract double payoff(double spot);

    public double evaluate(){
        this.model.makeUnderlyingEvolution();
        for (int i=this.model.getNumberOfSteps(); i>=0; i--){
            for (int j=0; j<=i; j++){
                double previousUp = 0.0;
                double previousDown = 0.0;
                if (i<this.model.getNumberOfSteps()){
                    previousUp = this.values.getElement(j,i+1);
                    previousDown = this.values.getElement(j+1,i+1);
                }
                double value_ji = Math.exp(-this.model.getRate()*this.model.getTimeToMaturity()/(double)this.model.getNumberOfSteps())*
                        (this.model.getP()*previousUp +
                        (1 - this.model.getP()) * previousDown);
                double payoff = payoff(this.model.getSpotAt(j,i));
                if (payoff >= value_ji) this.values.setElement(j,i, payoff);
                else this.values.setElement(j,i, value_ji);
            }
        }
        return this.values.getElement(0,0);
    }

    private double increment(String greek){
        return this.greeks.get(greek).increases();
    }

    public double calculateGreeks(String greek){

        double actual_value = this.evaluate();

        BinomialModel oldModel = new BinomialModel(this.model);
        double delta_x = this.increment(greek);

        double new_value = this.evaluate();

        this.model = oldModel;

        return (new_value-actual_value)/delta_x;
    }
}
