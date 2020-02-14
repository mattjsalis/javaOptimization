package optimization;

/**
 * This interface details required methods for an optimization algorithm.
 * @author Matt
 *
 */
public interface Optimizer_IF {
	public BestDiscoveredSolution optimize(CostFunction_IF costFunction);
}
