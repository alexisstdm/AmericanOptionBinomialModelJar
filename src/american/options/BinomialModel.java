package american.options;

import mathematical.matrix.*;

/**
 * Created by casa on 2/11/14.
 */
public class BinomialModel {
    private double spot;
    private double rate;
    private double dividendRate;
    private double volatility;
    private int numberOfSteps;
    private double timeToMaturity;

    private UpTriangularMatrix underlying;
    private double d;
    private double u;
    private double p;

    private void calibrateModel(){
        double deltaT = this.timeToMaturity/(double)this.numberOfSteps;
        double A = 0.5*(Math.exp(-(this.rate-this.dividendRate)*deltaT)+Math.exp((this.rate-this.dividendRate+Math.pow(this.volatility,2.0))*deltaT));
        this.d = A - Math.sqrt(Math.pow(A,2.0)-1);
        this.u = A + Math.sqrt(Math.pow(A,2.0)-1);
        this.p = (Math.exp((this.rate-this.dividendRate)*deltaT)-this.d)/(this.u-this.d);
    }

    public BinomialModel(double spot, double rate, double dividendRate, double volatility, int numberOfSteps, double timeToMaturity) {
        this.spot = spot;
        this.rate = rate;
        this.dividendRate = dividendRate;
        this.volatility = volatility;
        this.numberOfSteps = numberOfSteps;
        this.timeToMaturity = timeToMaturity;
        this.underlying = new UpTriangularMatrix();
        this.underlying.setElement(0, 0, spot);
        //
        // Model Explicit Calibration
        calibrateModel();

    }

    public BinomialModel(BinomialModel model){
        this.spot = model.spot;
        this.rate = model.rate;
        this.dividendRate = model.dividendRate;
        this.volatility= model.volatility;
        this.numberOfSteps = model.numberOfSteps;
        this.timeToMaturity = model.timeToMaturity;
        this.underlying = new UpTriangularMatrix();
        this.underlying.setElement(0, 0, spot);
        this.d = model.d;
        this.u = model.u;
        this.p = model.p;
    }

    public double getSpot() {
        return spot;
    }

    public double getRate() {
        return rate;
    }

    public double getDividendRate() {
        return dividendRate;
    }

    public double getVolatility() {
        return volatility;
    }

    public int getNumberOfSteps() {
        return numberOfSteps;
    }

    public double getTimeToMaturity() {
        return timeToMaturity;
    }

    public double getD(){
        return this.d;
    }

    public double getU(){
        return this.u;
    }

    public double getP(){
        return this.p;
    }

    public void setSpot(double spot) {
        this.spot = spot;
    }

    public void setRate(double rate) {
        this.rate = rate;
        calibrateModel();
    }

    public void setDividendRate(double dividendRate) {
        this.dividendRate = dividendRate;
        calibrateModel();
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
        calibrateModel();
    }

    public void setNumberOfSteps(int numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
    }

    public void setTimeToMaturity(double timeToMaturity) {
        this.timeToMaturity = timeToMaturity;
    }

    @Override
    public String toString() {
        return "BinomialModel{" +
                "spot=" + spot +
                ", rate=" + rate +
                ", dividendRate=" + dividendRate +
                ", volatility=" + volatility +
                ", numberOfSteps=" + numberOfSteps +
                ", timeToMaturity=" + timeToMaturity +
                '}';
    }

    public void makeUnderlyingEvolution(){

        double current = this.spot;

        for (int i=0; i<=this.numberOfSteps; i++){
            this.underlying.setElement(0,i,current);
            double current_down = current;
            for (int j=i+1; j<=this.numberOfSteps; j++){
                current_down = this.d * current_down;
                this.underlying.setElement(j-i,j,current_down);
            }
            current = this.u * current;
        }
    }

    public Double getSpotAt(int i, int j){
        return this.underlying.getElement(i,j);
    }
}
