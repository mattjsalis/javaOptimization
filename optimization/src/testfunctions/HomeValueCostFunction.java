package testfunctions;

import java.util.HashMap;

import optimization.CostFunctionOutput_IF;
import optimization.CostFunction_IF;
import optimization.Parameter;

public class HomeValueCostFunction implements CostFunction_IF{
	
	double homeValue = 300000.0;
	double rent = 1200.0;
	int mortgageTerm = 360; //months
	double downPayment = 15000.0;
	double percClosingCost = 3.0; //percentage of home value paid in closing cost
	double hoaFee = 300.0;
	double	rateOfAppreciation = 3.0;
	double	interestRate = 3.5;
	
	HashMap<String,Double> dictLossPercentages = getLossPercentages();
	public static HashMap<String,Double> getLossPercentages(){
		HashMap<String,Double> dictLossPercentages = new HashMap<String,Double>();
		dictLossPercentages.put("taxes", 1.0);
		dictLossPercentages.put("maintenance", 1.0);
		dictLossPercentages.put("pmi", 1.0);
		dictLossPercentages.put("insurance", 1.0);
		return dictLossPercentages;}
	
	@Override
	public CostFunctionOutput_IF evaluateCostFunction(Parameter... parameters) {
		
		double months = parameters[0].getCurrentValueAsDouble();
		
		double owedValue = homeValue - downPayment;
		double r = interestRate/12.0/100.0;
		double mortgagePayment = owedValue*r*Math.pow(1.0 + r,mortgageTerm)/(Math.pow(1.0 + r,mortgageTerm) - 1.0);
		
		double totalSunkCostHome = -this.downPayment - percClosingCost/100.0*homeValue;
		double totaSunkCostHomeMonth = 0.0;
		double appreciationMultiplier = 0.0;
		
		
		for (int i_month = 0;i_month < months;i_month++){
			totaSunkCostHomeMonth = 0.0;
			appreciationMultiplier = Math.pow(1.0 + rateOfAppreciation/100.0,i_month/12.0);
			//Assume hoa fee scales with appreciation
			totaSunkCostHomeMonth = hoaFee*appreciationMultiplier;
			
		}
		
		return null;
	}
	
	public static class HomeValueOutput implements CostFunctionOutput_IF{

		@Override
		public boolean isNewCostFunctionOutputBetter(CostFunctionOutput_IF oldCFOutput,
				CostFunctionOutput_IF newCFOutput) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isOptimizationCriterionSatisified() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isSolutionWithinRestraints() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void printOutput() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getOutputAsString() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
