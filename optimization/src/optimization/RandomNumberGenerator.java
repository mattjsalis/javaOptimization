package optimization;

import java.util.Random;
/**
 * This enumeration provides a way to get a random number from a specified distribution.
 * The output is cast to the class provided in the method call. 
 * @author Matt
 *
 */
public enum RandomNumberGenerator {
	UNIFORM,GAUSSIAN{
		@Override
		public Number getRandomNumber(Number mean, Number stdev, Class<? extends Number> classToConvertTo){

			double randVal = mean.doubleValue() + stdev.doubleValue()*random.nextGaussian();
			if (classToConvertTo.equals(Integer.class)){
				return new Integer(Math.round((float)randVal));
			}
			return new Double(randVal);
		}
	};

	public Random random = new Random();
	
	public Number getRandomNumber(Number mean, Number stdev, Class<? extends Number> classToConvertTo){
		//Default is uniform distribution
		double randVal = mean.doubleValue() + 3.0/2.0*stdev.doubleValue()*random.nextDouble();
		if (classToConvertTo.equals(Integer.class)){
			return new Integer(Math.round((float)randVal));
		}
		return new Double(randVal);
	}
}
