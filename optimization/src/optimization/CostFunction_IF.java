package optimization;

/**
 * This interface details required methods for a cost function.
 * @author Matt
 *
 */
public interface CostFunction_IF {
	public CostFunctionOutput_IF evaluateCostFunction(Parameter... parameters);
}
