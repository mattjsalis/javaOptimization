package optimization.pso;

import optimization.Parameter;
/**
 * This class extends the class <b>Parameter</b> to also store the 
 * velocity value/limits for a given parameter required for the PSO
 * algorithm.
 * @author Matt
 *
 */
public class ParticleParameter extends Parameter{
	public Double particleVelocity = 0.0;
	private Double velocityLimit;
	public void setParticleVelocity(Double velocity){
		if (Math.abs(velocity) < velocityLimit){
			this.particleVelocity = velocity;
		} else {
			this.particleVelocity = Math.signum(velocity)*velocityLimit;
		}
	}
	public ParticleParameter(Double lowerBound, Double upperBound){
		super(lowerBound,upperBound);
	}
	public ParticleParameter(Integer lowerBound, Integer upperBound){
		super(lowerBound,upperBound);
	}
	public ParticleParameter(String... categoricalStrings){
		super(categoricalStrings);
	}
	public ParticleParameter(Enum<? extends Object>[] categoricalEnum){
		super(categoricalEnum);
	}
	public void setVelocityLimit(Double velocityLimit){
		this.velocityLimit = velocityLimit;
	}
	@Override
	public ParticleParameter clone() throws CloneNotSupportedException{
		return (ParticleParameter)super.clone();
	}
	public Double getParticleVelocity() {
		return particleVelocity;
	}
	public Double getVelocityLimit() {
		return velocityLimit;
	}
}