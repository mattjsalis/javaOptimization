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
		public Number getRandomNumber(Number mean, Number range, Class<? extends Number> classToConvertTo){
			double posOrNeg = -1.0;
			if (random.nextBoolean()){
				posOrNeg = 1.0;
			}
			double stdev = range.doubleValue()/6.0;
			double randVal = mean.doubleValue() + posOrNeg*stdev*random.nextGaussian();
			if (classToConvertTo.equals(Integer.class)){
				return new Integer(Math.round((float)randVal));
			}
			return new Double(randVal);
		}
	};

	public Random random = new Random();
	
	public Number getRandomNumber(Number mean, Number range, Class<? extends Number> classToConvertTo){
		//Default is uniform distribution
		double posOrNeg = -1.0;
		if (random.nextBoolean()){
			posOrNeg = 1.0;
		}
		double randVal = mean.doubleValue() + posOrNeg*0.5*range.doubleValue()*random.nextDouble();
		if (classToConvertTo.equals(Integer.class)){
			return new Integer(Math.round((float)randVal));
		}
		return new Double(randVal);
	}
}
