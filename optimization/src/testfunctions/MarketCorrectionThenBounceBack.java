package testfunctions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import optimization.BestDiscoveredSolution;
import optimization.CostFunctionOutput_IF;
import optimization.CostFunction_IF;
import optimization.Parameter;
import optimization.pso.ParticleParameter;
import optimization.pso.ParticleSwarm;

public class MarketCorrectionThenBounceBack implements CostFunction_IF{
	double salePrice = 400000.0;
	double startRent = 1200.0;
	double hoaFee = 300.0;
	int mortgageTerm = 360; //months
	double downPayment = 15000.0;
	double percClosingCost = 3.0; //percentage of home value paid in closing cost
	double	rateOfAppreciation = 2.5;
	double	interestRate = 3.5;
	//Total percentage and length of correction
	double correctionPercPerYear = 10;
	double monthsCorrection = 12;
	double roommateRent = 800;
	int numberOfRoommates = 1;
	double monthlyOutOfPocket = 0.0;
	
	public int getNumberOfRoommates() {
		return numberOfRoommates;
	}

	public void setNumberOfRoommates(int numberOfRoommates) {
		this.numberOfRoommates = numberOfRoommates;
	}

	public double getHoaFee() {
		return hoaFee;
	}

	public void setHoaFee(double hoaFee) {
		this.hoaFee = hoaFee;
	}

	public static void main(String[] args) throws IOException{
		String report = "MonthsOfCorrection,TotalCorrectionPerc,Rent,NumRoommates,RoommateRent,HOAFee,SalePrice,AppreciationAfterCorrection,NumMonthsToEqualize,AverageMonthlyOutOfPocket\n";
		Path reportFilePath = Paths.get("C:\\Users\\Matt\\Desktop\\home_risk_report\\", "bounce_back_report.csv");
		File reportFile = new File(reportFilePath.toString());
		double[] salePrice = {350000,375000,400000,425000,450000,475000,500000};
		double[] appreciation = {2.0,2.5,3.0,3.5,4.0,4.5,5.0};
		int[] roommates = {0,1,2};
		
		for (int i_sp = 0;i_sp < salePrice.length;i_sp++){
			for (int i_app = 0;i_app < appreciation.length;i_app++){
				for (int i_rm = 0;i_rm < roommates.length;i_rm++){
					MarketCorrectionThenBounceBack homeValueFunc = new MarketCorrectionThenBounceBack(salePrice[i_sp],appreciation[i_app]);
					boolean calculate = true;
					if (salePrice[i_sp] > 300000 && salePrice[i_sp] < 400000){
						//2 bedroom condo
						if (roommates[i_rm] > 1){
							//unrealistic scenario
							calculate = false;
						}
					} else if (salePrice[i_sp] >= 400000 && salePrice[i_sp] < 475000){
						//3 bedroom condo or townhouse
						if (roommates[i_rm] > 2){
							//unrealistic scenario
							calculate = false;
						}
					} else if (salePrice[i_sp] >= 475000 && salePrice[i_sp] < 550000){
						//Seems to be homes with 3 bedrooms and less hoa fee at this price point
						if (roommates[i_rm] > 2){
							//unrealistic scenario
							calculate = false;
						}
						homeValueFunc.setHoaFee(150.0);
					} else {
						//about where HOA fees disappear
						if (roommates[i_rm] > 3){
							//unrealistic scenario
							calculate = false;
						}
						homeValueFunc.setHoaFee(0.0);
					}
					if (calculate){
						homeValueFunc.setNumberOfRoommates(roommates[i_rm]);
						ParticleSwarm pso = new ParticleSwarm(30,1000,0.01,new ParticleParameter(0,360)).setOptimizationToRunForPeriodOfTime(5).setConvergenceLimit(20);
						BestDiscoveredSolution monthAtEqualLoss = pso.optimize(homeValueFunc);
						monthAtEqualLoss.printSolution();
						HomeValueOutput output = (HomeValueOutput)monthAtEqualLoss.getCostFunctionOutput();
						Number monthsToEqualize = monthAtEqualLoss.getParameterValues().get(0);
						Double[] vals = {homeValueFunc.monthsCorrection,homeValueFunc.correctionPercPerYear,homeValueFunc.startRent, Double.valueOf(homeValueFunc.getNumberOfRoommates()),
								homeValueFunc.roommateRent, homeValueFunc.getHoaFee(),
								salePrice[i_sp],appreciation[i_app],monthsToEqualize.doubleValue(),homeValueFunc.monthlyOutOfPocket};
						report += Arrays.asList(vals).stream().map((val) -> String.valueOf(val)).collect(Collectors.joining(",")) +"\n";
					}
				}
			}
		}
		FileWriter writer = new FileWriter(reportFile);
		writer.write(report);
		writer.close();
	}
	
	public MarketCorrectionThenBounceBack(double salePrice, double appreciation){
		this.salePrice = salePrice;
		this.rateOfAppreciation = appreciation;
	}
	
	//Map containing 'sunk cost' parameters as a percentage of home value lost per year
	HashMap<String,Double> sunkLossPercentages = getLossPercentages();
	public static HashMap<String,Double> getLossPercentages(){
		HashMap<String,Double> dictLossPercentages = new HashMap<String,Double>();
		dictLossPercentages.put("taxes", 0.8);
		dictLossPercentages.put("maintenance", 1.0);
		dictLossPercentages.put("pmi", 0.7);
		dictLossPercentages.put("insurance", 0.5);
		return dictLossPercentages;}
	
	
	
	@Override
	public CostFunctionOutput_IF evaluateCostFunction(Parameter... parameters) {
		
		double months = parameters[0].getCurrentValueAsDouble();
		double homeNet = 0.0;
		double rentNet = 0.0;
		//Calculate the net value lost or gained when selling a home after a certain number of months
		//	where Net = value_at_sale(months) - sunk_costs
		double owedValue = salePrice - downPayment;
		double r = interestRate/12.0/100.0;
		double mortgagePayment = owedValue*r*Math.pow(1.0 + r,mortgageTerm)/(Math.pow(1.0 + r,mortgageTerm) - 1.0);
		double totalSunkCostHome = this.downPayment + percClosingCost/100.0*salePrice;
		double appreciationMultiplier = 0.0;
		double appreciatedHomeValue = salePrice;
		double interestPaid = 0.0;
		double totalMonthlyOutOfPocket = 0.0;
		double aSunkCost = 0.0;
		
		for (int i_month = 0;i_month < months;i_month++){
			if (monthsCorrection <= 0){
				appreciationMultiplier = Math.pow(1.0 + rateOfAppreciation/100.0,i_month/12.0);
			}else if (i_month <= monthsCorrection){
				appreciationMultiplier = Math.pow(1.0 - correctionPercPerYear/100.0,i_month/12.0);
			} else {
				double totalCorrrection = Math.pow(1.0 - correctionPercPerYear/100.0,monthsCorrection/12.0);
				appreciationMultiplier = Math.pow(1.0 + rateOfAppreciation/100.0,i_month/12.0)*totalCorrrection;
			}
			
			appreciatedHomeValue = salePrice*appreciationMultiplier;
			//Assume hoa fee scales with appreciation
			totalSunkCostHome += (hoaFee - roommateRent*numberOfRoommates)*appreciationMultiplier;
			totalMonthlyOutOfPocket += (hoaFee - roommateRent*numberOfRoommates)*appreciationMultiplier + mortgagePayment;
			//If 20% of slae prce is paid, stop paying pmi
			if (salePrice - owedValue < 0.2*salePrice && sunkLossPercentages.containsKey("pmi")){
				sunkLossPercentages.remove("pmi");
			}
			//calculate the sunk costs for the month
			for(String sunkLossKey : sunkLossPercentages.keySet()){
				aSunkCost = appreciatedHomeValue*sunkLossPercentages.get(sunkLossKey)/100.0/12.0;;
				totalSunkCostHome += aSunkCost;
				totalMonthlyOutOfPocket += aSunkCost;
			}
			interestPaid = (1 + r)/mortgageTerm*owedValue;
			//update the owed value on the mortgage
			owedValue -= mortgagePayment - interestPaid;
			//update the total sunk cost to include interest
			totalSunkCostHome += interestPaid;
			
			//Calculate total lost on rent
			rentNet -= startRent*appreciationMultiplier;
		}
		
		totalMonthlyOutOfPocket = totalMonthlyOutOfPocket/months;
		homeNet = appreciatedHomeValue - totalSunkCostHome - owedValue;
		this.monthlyOutOfPocket = totalMonthlyOutOfPocket;
		return new HomeValueOutput(Math.abs(homeNet - rentNet),totalMonthlyOutOfPocket);
	}
	
	public static class HomeValueOutput implements CostFunctionOutput_IF{
		
		double differenceBetweenHomeAndRentNet = 0.0;
		double monthlyCost = 0.0;
		public HomeValueOutput(double differenceBetweenHomeAndRentNet,double monthlyCost){
			this.differenceBetweenHomeAndRentNet = differenceBetweenHomeAndRentNet;
			this.monthlyCost = monthlyCost;
		}
		
		public double getDifference(){
			return differenceBetweenHomeAndRentNet;
		}
		@Override
		public boolean isNewCostFunctionOutputBetter(
				CostFunctionOutput_IF newCFOutput) {
			if (((HomeValueOutput)newCFOutput).getDifference() < this.getDifference()){
				return true;
			}
			return false;
		}

		@Override
		public boolean isOptimizationCriterionSatisified() {
			if (this.differenceBetweenHomeAndRentNet < 1200.0){
				return true;
			}
			return false;
		}

		@Override
		public boolean isSolutionWithinRestraints() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void printOutput() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getOutputAsString() {
			// TODO Auto-generated method stub
			return String.valueOf(differenceBetweenHomeAndRentNet) + " ; Avg Monthly Cost: " + String.valueOf(monthlyCost);
		}
		
	}

}