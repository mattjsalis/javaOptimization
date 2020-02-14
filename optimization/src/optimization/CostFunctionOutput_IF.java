package optimization;

/**
 * This interface details required methods for a cost function output.
 * @author Matt
 *
 */
public interface CostFunctionOutput_IF {
	public boolean isNewCostFunctionOutputBetter(CostFunctionOutput_IF oldCFOutput, CostFunctionOutput_IF newCFOutput);
	public boolean isOptimizationCriterionSatisified();
	public boolean isSolutionWithinRestraints();
	public void printOutput();
	public String getOutputAsString();
}
