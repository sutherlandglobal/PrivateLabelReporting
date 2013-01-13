package statistics;

import java.util.Vector;

public class LinearRegression
{
	private Vector<Double> xVals;
	private Vector<Double> yVals;

	private double xSum = 0;
	private double xSumSquares = 0;
	private double ySum = 0;

	private double xBar;
	private double yBar;

	private double xxbar = 0;
	private double yybar = 0;
	private double xybar = 0;

	private double beta1;
	private double beta0;

	private double ssr = 0;
	private double rss = 0;
	
	private double rSquared;


	public LinearRegression(Vector<Double> xVals, Vector<Double> yVals)
	{
		this.xVals = xVals;
		this.yVals = yVals;

		if(xVals.size() != yVals.size())
		{
			throw new ArrayIndexOutOfBoundsException("Invalid sizes");
		}
	}

	private void calcBars()
	{
		for(int i = 0; i< xVals.size(); i++)
		{
			xSum += xVals.get(i);
			xSumSquares += xVals.get(i) * xVals.get(i);
			ySum += yVals.get(i);
		}

		xBar = xSum / xVals.size();
		yBar = ySum / yVals.size();

		System.out.println("xBar: " + xBar);
		System.out.println("yBar: " + yBar);
	}

	private void calcSummaryStats()
	{
		for(int i = 0; i< xVals.size(); i++)
		{
			xxbar += (xVals.get(i) - xBar) * (xVals.get(i) - xBar);
			yybar += (yVals.get(i) - yBar) * (yVals.get(i)  - yBar);
			xybar += (xVals.get(i) - xBar) * (yVals.get(i) - yBar);
		}

		beta1 = xybar / xxbar;
		beta0 = yBar - beta1 * xBar;

		System.out.println("Beta0: " + beta0);
		System.out.println("Beta1: " + beta1);
	}
	
	public double getRSquared()
	{
		return rSquared;
	}

	public Vector<Double> runRegression(int steps)
	{
		//y vals
		Vector<Double> newData = new Vector<Double>();

		calcBars();
		calcSummaryStats();
		
		double fit;
		for(int i = 0; i< xVals.size(); i++)
		{
			//fit is new Y
			fit = beta1*xVals.get(i) + beta0;
			
			rss += (fit - yVals.get(i)) * (fit - yVals.get(i));
			ssr += (fit - yBar) * (fit - yBar);
			
			newData.add(fit);
		}

		rSquared = ssr/yBar;
		
		//System.out.println("rss: " + rss);
		//System.out.println("ssr: " + ssr);
		
		return newData;

	}

	public static void main(String[] args)
	{
		Vector<Double> xVals = new Vector<Double>();
		Vector<Double> yVals = new Vector<Double>();

		int steps = 1;

		for(int i =0; i< 9; i++)
		{
			xVals.add((double)i);
		}

		yVals.add((double)30486); //2011/1
		yVals.add((double)33370); //2011/2
		yVals.add((double)41088); //2011/3
		yVals.add((double)36646); //2011/4
		yVals.add((double)34503); //2011/5
		yVals.add((double)36356); //2011/6
		yVals.add((double)40894); //2011/7
		yVals.add((double)39591); //2011/8
		yVals.add((double)37018); //2011/9

		LinearRegression rg = new LinearRegression(xVals, yVals);

		Vector<Double> results = rg.runRegression(steps);

		for(int i = 0; i< xVals.size(); i++)
		{
			System.out.println(i + ": " + yVals.get(i).intValue());
		}
		
		for(int i = 0; i< results.size(); i++)
		{
			System.out.println(i + xVals.size() + ": " + results.get(i).intValue());
		}
		
		System.out.println("RsQ: " + rg.getRSquared());
	}
}
