package optimization;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This class defines a given optimization parameter. It infers the type of the parameter from
 * the constructor used to instantiate it. The class supports Integer and Double values
 * parameters. Categorical parameters are accepted and encoded into Integers.
 * @author Matt
 *
 */
public class Parameter implements Cloneable{
	
	/**
	 * Enumeration of supported parameter types with their
	 * corresponding numeric class return type.
	 * @author Matt
	 *
	 */
	public enum ParameterType{
		INTEGER(Integer.class),
		DOUBLE(Double.class),
		CATEGORICAL(Integer.class);
		
		Class<? extends Number> returnType = null;
		ParameterType(Class<? extends Number> returnType){
			this.returnType = returnType;
		}
	}
	public ParameterType paramType = null;
	public HashMap<String,Integer> categoricalMap = null;
	public Number lowerBound = null;
	public Number upperBound = null;
	public Double mean = null;
	public Double range = null;
	public Number currentValue = null;
	protected RandomNumberGenerator randNumberGen = RandomNumberGenerator.UNIFORM;
	public boolean reinitializeUponOutOfBounds = false;
	
	/**
	 * Constructor to define a parameter of numeric type Double with the lower and upper bounds of the parameter.
	 * @param lowerBound
	 * @param upperBound
	 */
	public Parameter(Double lowerBound, Double upperBound){
		this.paramType = ParameterType.DOUBLE;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		setStatsForNumber(lowerBound,upperBound);
		this.currentValue = RandomNumberGenerator.UNIFORM.getRandomNumber(mean, range,paramType.returnType);
	}
	/**
	 * Constructor to define a parameter of numeric type Integer with the lower and upper bounds of the parameter.
	 * @param lowerBound
	 * @param upperBound
	 */
	public Parameter(Integer lowerBound, Integer upperBound){
		this.paramType = ParameterType.INTEGER;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		setStatsForNumber(lowerBound,upperBound);
		this.currentValue = RandomNumberGenerator.UNIFORM.getRandomNumber(mean, range,paramType.returnType);

	}
	/**
	 * Constructor to define a parameter of numeric type Integer with categorical String inputs
	 * @param lowerBound
	 * @param upperBound
	 */
	public Parameter(String... categoricalStrings){
		this.paramType = ParameterType.CATEGORICAL;
		this.categoricalMap = OptUtils.encodeCategorical(categoricalStrings);
		setStatsForCategorical();
	}
	/**
	 * Constructor to define a parameter of numeric type Integer with an array of Enum
	 * @param lowerBound
	 * @param upperBound
	 */
	public Parameter(Enum<? extends Object>[] categoricalEnum){
		this.paramType = ParameterType.CATEGORICAL;
		this.categoricalMap = OptUtils.encodeCategorical(categoricalEnum);
		setStatsForCategorical();
	}
	/**
	 * Sets hyperparameters required for parameter value initialization
	 * @param lowerBound
	 * @param upperBound
	 */
	private void setStatsForNumber(Number lowerBound,Number upperBound){
		this.range = upperBound.doubleValue()  - lowerBound.doubleValue() ;
		this.mean = lowerBound.doubleValue() + range/2.0;
	}
	/**
	 * Sets hyperparameters required for parameter value initialization
	 * @param lowerBound
	 * @param upperBound
	 */
	private void setStatsForCategorical(){
		this.lowerBound = Integer.valueOf(categoricalMap.values().stream().min(Integer::compare).get());
		this.upperBound = Integer.valueOf(categoricalMap.values().stream().max(Integer::compare).get());
		setStatsForNumber(lowerBound,upperBound);
		this.currentValue = RandomNumberGenerator.UNIFORM.getRandomNumber(mean, range,paramType.returnType);
	}
	/**
	 * Reinitializes the parameter based on a defined distribution type and the current hyperparameters
	 * @param randomNumberGenerator
	 * @return
	 */
	public Parameter renitializeValue(RandomNumberGenerator randomNumberGenerator){
		this.randNumberGen = randomNumberGenerator;
		this.currentValue = randomNumberGenerator.getRandomNumber(mean, range,paramType.returnType);
		return this;
	}
	/**
	 * Reinitializes the parameter based on a current distribution type and the current hyperparameters
	 * @param randomNumberGenerator
	 * @return
	 */
	public Parameter renitializeValue(){
		this.currentValue = randNumberGen.getRandomNumber(mean, range,paramType.returnType);
		return this;
	}
	/**
	 * Sets the parameter to reinitialize itself based on the current distribution type and hyperparameters
	 * its value gets outside of the defined bounds
	 * Note: Default behavior is to set the parameter to the value of whichever bound it has exceeded
	 * @return
	 */
	public Parameter isReinitializeUponOutOfBounds(){
		this.reinitializeUponOutOfBounds = true;
		return this;
	}
	/**
	 * This method is the primary way that the parameter should be updated. It enforces parameter boundaries
	 * and casts numeric values appropriately.
	 * @param valueToBound new proposed value for the parameter
	 */
	public void updateAndBoundCurrentValue(Number valueToBound){
		if (valueToBound.doubleValue() < this.lowerBound.doubleValue()){
			if (reinitializeUponOutOfBounds){
				this.currentValue = randNumberGen.getRandomNumber(mean, range, paramType.returnType);
			} else {
				this.currentValue = randNumberGen.getRandomNumber(lowerBound, 0, paramType.returnType);
			}
		} else if (valueToBound.doubleValue() > this.upperBound.doubleValue()){
			if (reinitializeUponOutOfBounds){
				this.currentValue = randNumberGen.getRandomNumber(mean, range, paramType.returnType);
			} else {
				this.currentValue = randNumberGen.getRandomNumber(upperBound, 0, paramType.returnType);
			}
		} else {
			this.currentValue = randNumberGen.getRandomNumber(valueToBound, 0, paramType.returnType);
		}
	}
	/**
	 * Method to decode an Integer into the categorical parameter it represents
	 * @param encodedInteger
	 * @return corresponding categorical parameter
	 */
	public String decode(Integer encodedInteger){
		for (Entry<String, Integer> encodeEntry : this.categoricalMap.entrySet()){
			if (encodeEntry.getValue().equals(encodedInteger)){
				return encodeEntry.getKey();
			}
		}
		
		try {
			throw new Exception("Unsupported Categorical Entry");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ParameterType getParamType() {
		return paramType;
	}

	public Number getCurrentValue() {
		return currentValue;
	}
	public Double getCurrentValueAsDouble(){
		return this.currentValue.doubleValue();
	}
	public Double getRangeOfParameterValue(){
		return range;
	}
	@Override
	public Parameter clone() throws CloneNotSupportedException{
		return (Parameter)super.clone();
	}
}
