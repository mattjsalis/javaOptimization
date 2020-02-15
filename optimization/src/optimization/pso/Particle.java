package optimization.pso;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import optimization.BestDiscoveredSolution;
import optimization.CostFunctionOutput_IF;
/**
 * <pre>
 * This class represents the Particle portion of the Particle Swarm Optimization
 * algorithm. It stores all of the <b>ParticleParameter</b>(s) as well as the best 
 * solution that the Particle has found (<b>BestDiscoveredSolution</b>)
 * </pre>
 * <pre>
 * The class currently uses the basic PSO update algorithm:
 * v<sub>i+1</sub> = w*v<sub>i</sub> 
 * 		+ r<sub>1</sub>c<sub>cog</sub>*(b<sub>part</sub> - p<sub>i</sub>) 
 * 		+ r<sub>2</sub>c<sub>soc</sub>*(b<sub>swarm</sub> - p<sub>i</sub>)
 * </pre>
 * @author Matt
 *
 */

public class Particle {
	
	private ParticleParameter[] parameters = null;
	private BestDiscoveredSolution bestValue = null;
	
	public Particle(ParticleParameter... parameters){

		this.parameters = Stream.of(parameters).map((parameter) -> {
			try {
				return parameter.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			return parameter;
		}).collect(Collectors.toList()).toArray(new ParticleParameter[parameters.length]);
		

	}
	
	
	public ParticleParameter[] getParameters() {
		return parameters;
	}


	public void setParameters(ParticleParameter[] parameters) {
		this.parameters = parameters;
	}
	
	public void setBestValueToNull(){
		this.bestValue = null;
	}

	public List<Number> getParameterValues(){
		return Stream.of(this.parameters).map((param) -> param.getCurrentValue()).collect(Collectors.toList());
	}
	public void updateParameters(double inertialCoeff, double cognitveCoeff, double socialCoeff, BestDiscoveredSolution swarmBest, CostFunctionOutput_IF costFunctionOutput){
		//if this is the first particle call then initialize best value
		if (bestValue == null){
			if (costFunctionOutput.isSolutionWithinRestraints()){
				this.bestValue = new BestDiscoveredSolution(parameters,costFunctionOutput);
			}
			for (ParticleParameter param : parameters){
				double negOrPos = -1;
				if ((new Random()).nextBoolean()){
					negOrPos = 1.0;
				}
				param.setParticleVelocity(negOrPos*Math.random()*param.getVelocityLimit());
				//Update parameter value
				param.updateAndBoundCurrentValue(param.getCurrentValueAsDouble() + param.getParticleVelocity());
			}
		} else {
			if (bestValue.getCostFunctionOutput().isNewCostFunctionOutputBetter(costFunctionOutput)){
				this.bestValue = new BestDiscoveredSolution(parameters,costFunctionOutput);
			}

			for (int i_param = 0;i_param < parameters.length;i_param++){
				
				double velSelfComponent = inertialCoeff*parameters[i_param].getParticleVelocity() + 
						Math.random()*cognitveCoeff*(bestValue.getParameterValues().get(i_param).doubleValue() - parameters[i_param].getCurrentValueAsDouble());
				double velSwarmComponent = Math.random()*socialCoeff*(swarmBest.getParameterValues().get(i_param).doubleValue() - parameters[i_param].getCurrentValueAsDouble());
				parameters[i_param].setParticleVelocity(velSelfComponent + velSwarmComponent);
				//Update parameter value
				parameters[i_param].updateAndBoundCurrentValue(parameters[i_param].getCurrentValueAsDouble() + parameters[i_param].getParticleVelocity());
			}
		}
	}
	
	
}
