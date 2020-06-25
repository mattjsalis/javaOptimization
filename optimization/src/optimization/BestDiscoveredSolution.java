package optimization;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * This class stores a cost function output as well as the set of parameters that produced the output.
 * @author Matt
 *
 */
public class BestDiscoveredSolution implements Cloneable{
	private CostFunctionOutput_IF costFunctionReturnValue;
	private Parameter[] parameters;
	
	public BestDiscoveredSolution(Parameter[] parameters, CostFunctionOutput_IF costFunctionReturnValue){
		this.parameters = OptUtils.cloneParameterArray(parameters);
		this.costFunctionReturnValue = costFunctionReturnValue;
	}
	public List<Number> getParameterValues(){
		return Stream.of(parameters).map((param) -> param.getCurrentValue()).collect(Collectors.toList());
	}
	public List<Parameter> getParameters(){
		return Arrays.asList(parameters);
	}
	public CostFunctionOutput_IF getCostFunctionOutput(){
		return this.costFunctionReturnValue;
	}
	public void printSolution(){
		String solutionString = "Parameters: ";
		solutionString += Stream.of(parameters).map((param) -> {
			switch(param.paramType){
			case CATEGORICAL:
				return param.decode(param.getCurrentValue().intValue());
			default:
				return String.valueOf(param.getCurrentValue());
			}
		}).collect(Collectors.joining(","));
		solutionString += " " + this.costFunctionReturnValue.getOutputAsString();
		System.out.println(solutionString);
	}
	
	@Override
	public BestDiscoveredSolution clone() throws CloneNotSupportedException{
		BestDiscoveredSolution clonedSolution = (BestDiscoveredSolution)super.clone();
		return clonedSolution;
	}
}
