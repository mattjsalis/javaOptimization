package testfunctions;

import optimization.BestDiscoveredSolution;
import optimization.CostFunctionOutput_IF;
import optimization.CostFunction_IF;
import optimization.Parameter;
import optimization.pso.ParticleParameter;
import optimization.pso.ParticleSwarm;

public class TestCategoricalOptimization implements CostFunction_IF{
	//Here the expected outputs 
	@Override
	public CostFunctionOutput_IF evaluateCostFunction(Parameter... parameters) {
		String categorical = parameters[0].decode(parameters[0].getCurrentValue().intValue());
		
		
		double x = parameters[1].getCurrentValueAsDouble();
		double y = parameters[2].getCurrentValueAsDouble();
		double funcValue = 0.0;
		
		switch(categorical){
		case "mccormick":
			funcValue = Math.sin(x+y) + Math.pow(x-y,2.0) -1.5*x + 2.5*y + 1.0;
			break;
		case "beale":
			funcValue = Math.pow(1.5 - x + x*y,2.0) 
			+ Math.pow(2.25 - x + x*Math.pow(y, 2.0),2.0) 
			+ Math.pow(2.625 - x + x* Math.pow(y,3.0),2.0);
			break;
		case "himmelblau":
			funcValue = Math.pow(Math.pow(x, 2.0) + y - 11.0, 2) + Math.pow(Math.pow(y, 2.0) + x - 7.0, 2);
			break;
		case "eggholder":
			funcValue = -(y + 47.0)*Math.sin(Math.sqrt(Math.abs(x/2.0 + y + 47.0))) - x*Math.sin(Math.sqrt(Math.abs(x - y - 47.0)));
			break;
		case "crossintray":
			funcValue = -0.0001*Math.pow(Math.abs(Math.sin(x)*Math.sin(y)*Math.exp(Math.abs(100 - Math.hypot(x, y)/Math.PI))) + 1, 0.1);
			break;
		case "holdertable":
			funcValue = -Math.abs(Math.sin(x)*Math.cos(y)*Math.exp(Math.abs(1 - Math.hypot(x,y)/Math.PI)));
		}
		
		return new CategoricalOutput(funcValue);
	}
	
	public static class CategoricalOutput implements CostFunctionOutput_IF{
		double value = 0.0;
		public CategoricalOutput(double value){
			this.value = value;
		}
		
		public double getValue() {
			return value;
		}

		@Override
		public boolean isNewCostFunctionOutputBetter(
				CostFunctionOutput_IF newCFOutput) {
			if (((CategoricalOutput)newCFOutput).getValue() < this.getValue() &&
					isSolutionWithinRestraints()){
				return true;
			}
			return false;
		}

		@Override
		public boolean isOptimizationCriterionSatisified() {
			if (value < -19.208){
				return true;
			}
			return false;
		}

		@Override
		public void printOutput() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getOutputAsString() {
			
			return String.valueOf(value);
		}

		@Override
		public boolean isSolutionWithinRestraints() {
			return true;
		}
		
	}
	
	
	
	public static void main(String[] args){
		ParticleSwarm swarm = new ParticleSwarm(30,5000,0.01,
				new ParticleParameter("mccormick","beale","himmelblau","crossintray","holdertable"), 
				new ParticleParameter(-10.0,10.0),
				new ParticleParameter(-10.0,10.0)).setOptimizationToRunForPeriodOfTime(5);
		BestDiscoveredSolution solution = swarm.optimize(new TestCategoricalOptimization());
		solution.printSolution();
		
	}
}
