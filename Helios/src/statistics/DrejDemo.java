package statistics;


import javax.vecmath.GMatrix;
import javax.vecmath.GVector;

import com.gregdennis.drej.Kernel;
import com.gregdennis.drej.LinearKernel;
import com.gregdennis.drej.Matrices;
import com.gregdennis.drej.Regression;
import com.gregdennis.drej.Representer;

public class DrejDemo
{
	public static void main(String[] args)
	{
		double[] n = new double[]{30486, 33370, 41088, 36646, 34503, 36356, 40894, 39591, 37018};
		
		 GMatrix data = new GMatrix(n.length, n.length);
		 GVector values = new GVector(n);
		
		 // here you would put all your data points into the data matrix
		 // each data point goes into a column of the matrix
		 // put the actual values for those data points in the values vector
		 // the data point in the ith column of the data matrix should have
		 // the value in the ith entry in the values vector.
		 // I believe some kernels only work when your range of possible values has
		 // zero as a midpoint. for instance, if you're classifying data points into "yes"
		 // and "no", best to choose their values as 1 and -1, as opposed to 1 and 0.
		
		 // construct the kernel you want to use:
		 Kernel kernel = LinearKernel.KERNEL;
		
		 // choose a penalty factor on the complexity of the solution
		 // this helps to prevent overfitting the data
		 // I was told me this number should be between
		 // 10^-3 and 1, I often choose 0.5, but you can play with it
		 double lambda = 0.5;
		
		 // do the regression, which returns a function fit to the data
		 Representer representer = Regression.solve(data, values, kernel, lambda);
		
		 GVector coef = representer.coeffs();
		 
		 for(int i =0; i< coef.getSize(); i++)
		 {
			 System.out.println("coef: " + coef.getElement(i));
		 }
		 
		//That's basically it. What happens next depends on what you want to use it for. If you'd like to use the regression to predict the value of a data point y, just feed y into the representer function:
		
		 //double predictedValue = representer.eval(n.length + 1);
		
		//If you'd like to calculate how well the function fits the data, you can first calculate the vector of values the representer would predict for your data points, subtract from that the vector of actual values, and take the norm squared of that difference. Let's call this the "cost". The lower the cost, the better the function fits the data. You can try out different kernels, and see which one yields the best-fit curve (the lowest cost):
		
		 GVector predictedValues = Matrices.mapCols(representer, data);
		 predictedValues.sub(values);
		 double cost = predictedValues.normSquared();
		 
		 for(int i =0; i< predictedValues.getSize(); i++)
		 {
			 System.out.println(i + ": " + predictedValues.getElement(i));
		 }
		 System.out.println("Cost: " + cost);
		
	}
}
