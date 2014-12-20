package american.options;

/**
 * Created by casa on 4/11/14.
 */
public class AmericanPutOption extends AmericanOption {

    private double strike;

    public AmericanPutOption(BinomialModel model, double strike){
        super(model);
        this.strike = strike;
    }

    public AmericanPutOption(double spot, double rate, double dividendRate, double volatility, int numberOfSteps,
                             double timeToMaturity, double strike) {
        super(spot, rate, dividendRate, volatility, numberOfSteps, timeToMaturity);
        this.strike = strike;
    }

    @Override
    public double payoff(double spot){
        return Math.max(this.strike - spot, 0.0);
    }
}
