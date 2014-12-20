package american.options;

/**
 * Created by casa on 4/11/14.
 */
public class AmericanCallOption extends AmericanOption {

    private double strike;

    public AmericanCallOption(BinomialModel model, double strike){
        super(model);
        this.strike = strike;
    }

    public AmericanCallOption(double spot, double rate, double dividendRate, double volatility, int numberOfSteps,
                             double timeToMaturity, double strike) {
        super(spot, rate, dividendRate, volatility, numberOfSteps, timeToMaturity);
        this.strike = strike;
    }

    @Override
    public double payoff(double spot){
        return Math.max(spot - this.strike, 0.0);
    }

}
