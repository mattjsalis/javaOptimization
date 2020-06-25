package optimization.pso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import optimization.CostFunction_IF;
import optimization.BestDiscoveredSolution;
import optimization.CostFunctionOutput_IF;
import optimization.Optimizer_IF;

/**
 * <pre>
 * This class performs Particle Swarm Optimization (PSO) given a user defined 
 * cost function and set of constrained parameters.
 * </pre>
 *  <pre>
 * This class initializes and stores the <b>Particle</b>s required for the 
 * optimization. It steps the particles through the optimization until the 
 * user defined cost function class (<b>must implement CostFunction_IF</b>) 
 * returns an acceptable solution as detailed by the user defined cost function 
 * output class (<b>must implement CostFunctionOutput_IF</b>) or until the number
 * of optimization steps equals the maximum number specified by the user.
 * </pre>
 * <pre>
 * <b>Note</b>: To combat premature convergence, the algorithm is set to 
 * reinitialize itself after a specified number of generations over which
 * the best swarm value has not been updated. The object will keep the
 * best overall value (best value found over all reinitializations) and
 * return this instance of <b>BestDiscoveredSolution</b> at the end of 
 * the optimization.
 * </pre>
 * @author Matt
 *
 */
public class ParticleSwarm implements Optimizer_IF{

	private List<Particle> particles = new ArrayList<Particle>();
	private BestDiscoveredSolution swarmOverallBest = null;
	private BestDiscoveredSolution swarmBest = null;
	private int maxGenerations;
	private double socialCoefficient = 2.0;
	private double cognitiveCoefficient = 2.0;
	private double inertialCoefficient = 1.0;
	private int convergenceLimit = 5;
	//Set optimization to run indefinitely until a time limit is specified
	private boolean runForSpecifiedTime = false;
	//Max runtime is seconds
	private double maxRunTime = 300;
	private Double startTime = null;
	/**
	 * <pre>
	 * Constructor that defines the number of particles, maximum optimization generations, and maximum percentage of
	 * it's range that a given parameter can travel in a given generation as well as the parameters used in the cost
	 * function.
	 * <pre>
	 * <pre>
	 * <b>Note:</b> The velocity limit for parameters with numeric type Integer is set to 1.
	 * <pre>
	 * @param numberOfParticles
	 * @param maxGenerations
	 * @param maxDecPercRangePerOptStep
	 * @param parameters
	 */
	public ParticleSwarm(int numberOfParticles, int maxGenerations,Double maxDecPercRangePerOptStep,ParticleParameter... parameters){
		this.particles.clear();
		Stream.of(parameters).forEach(
				(parameter) -> {
					switch(parameter.getParamType()){
					case INTEGER:
						parameter.setVelocityLimit(1.0);
						break;
					case CATEGORICAL:
						parameter.setVelocityLimit(1.0);
						break;
					case DOUBLE:
						parameter.setVelocityLimit(parameter.getRangeOfParameterValue()*maxDecPercRangePerOptStep);
						break;
					}	
				}
				);
		for (int i_part=0; i_part<numberOfParticles;i_part++){
			particles.add(new Particle(parameters));
		}
		this.maxGenerations = maxGenerations;
	}
	
	/**
	 * After setting the <b>swarmOverallBest</b>  to the current value of
	 * <b>swarmBest</b> providing it is a better solution, this method reinitializes
	 *  all of the parameter values within the particles and sets the best value in 
	 *  each of the particles as well as in the swarm to <b>null</b>. Doing this will
	 *  effectively restart the algorithm while maintaining the current generation
	 *  count.
	 */
	private void restartSwarm(){
		if (swarmOverallBest == null || 
				swarmOverallBest.getCostFunctionOutput()
				.isNewCostFunctionOutputBetter(swarmBest.getCostFunctionOutput())){

			this.swarmOverallBest = swarmBest;

		}
		this.swarmBest = null;
		this.particles.stream().forEach(
				(particle) ->
				Stream.of(particle.getParameters()).forEach((param) -> param.renitializeValue())
				);
		this.particles.stream().forEach(
				(particle) -> 
				particle.setBestValueToNull()
				
				);
	}
	
	@Override
	public BestDiscoveredSolution optimize(CostFunction_IF costFunction) {
		if (startTime == null){
			startTime = Double.valueOf(System.nanoTime())/1E9;
		}
		int convCount = 0;
		for (int i_gen = 0; i_gen < maxGenerations;i_gen++){
			boolean betterValueFound = false;
			for (int i_part = 0; i_part < particles.size();i_part++){
				CostFunctionOutput_IF output = costFunction.evaluateCostFunction(particles.get(i_part).getParameters());
				if (output.isOptimizationCriterionSatisified()){
					return new BestDiscoveredSolution(particles.get(i_part).getParameters(),output);
				} else if (swarmBest == null || swarmBest.getCostFunctionOutput().isNewCostFunctionOutputBetter(output) 
						){
					if (output.isSolutionWithinRestraints()){
						betterValueFound = true;
						this.swarmBest = new BestDiscoveredSolution(particles.get(i_part).getParameters(),output);
					}
				}
				particles.get(i_part).updateParameters(inertialCoefficient, cognitiveCoefficient, socialCoefficient, swarmBest, output);
			}
			if (swarmBest != null){
				System.out.println("Generation: " + String.valueOf(i_gen) + ",Best Value: " + swarmBest.getCostFunctionOutput().getOutputAsString());
			} else {
				System.out.println("Generation: " + String.valueOf(i_gen) + ", No value meeting constraints found.");
			}
			if (!betterValueFound && swarmBest != null){
				convCount++;
				if (convCount >= convergenceLimit){
					this.restartSwarm();
					convCount = 0;
				}
			} else {
				convCount = 0;
			}
			double elapsedTime = Double.valueOf(System.nanoTime())/1E9;
			if (runForSpecifiedTime && elapsedTime - startTime > Double.valueOf(maxRunTime)){
				return swarmOverallBest;
			}
		}
		if (swarmOverallBest == null){
			swarmOverallBest = swarmBest;
		}
		if (runForSpecifiedTime && !swarmOverallBest.getCostFunctionOutput().isOptimizationCriterionSatisified()){
			double elapsedTime = Double.valueOf(System.nanoTime())/1E9;
			if (elapsedTime - startTime < maxRunTime){
				optimize(costFunction);
			}
				
		}
		if (swarmOverallBest != null){
			return swarmOverallBest;
		}
		return swarmBest;
	}
	
	/**
	 * <pre>
	 * This method will set the algorithm to run until the optimization criterion are satisified
	 * over a period of time defined in seconds.
	 * </pre>
	 * WARNING: This algorithm will continue running over the specified time period
				until the optimization function is satisfied.
				(CostFunctionOutput_IF.isOptimizationCriterionSatisified() returns true).
	 * @param secToRunFor
	 * @return
	 */
	public ParticleSwarm setOptimizationToRunForPeriodOfTime(double secToRunFor){
		this.maxRunTime = secToRunFor;
		this.runForSpecifiedTime = true;
		return this;
	}
	/**
	 * Sets the number of generations after which the swarm will be reinitialized if there is
	 * no update the the best swarm value
	 * 
	 * <pre>
	 * <b>Tip:</b> If no reinitialization is desired then set the convergence limit to an impossibly
	 * high number (Ex: 2^32-1).
	 * </pre>
	 * @param convergenceLimit
	 * @return
	 */
	public ParticleSwarm setConvergenceLimit(int convergenceLimit){
		this.convergenceLimit = convergenceLimit;
		return this;
	}
}
