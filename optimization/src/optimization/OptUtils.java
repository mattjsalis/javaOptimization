package optimization;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import optimization.pso.ParticleParameter;

/**
 * This class provides static methods useful for optimization functions.
 * @author Matt
 *
 */
public class OptUtils {
	
	/**
	 * Encodes an array of Enums (such as one obtained by MyExampeEnum.values())
	 * @param categoricalEnum
	 * @return
	 */
	public static <T> HashMap<String,Integer> encodeCategorical(Enum<? extends Object>[] categoricalEnum){
		
		return encodeCategorical(
				Stream.of(categoricalEnum)
					.map((anEnum) -> anEnum.toString())
					.collect(Collectors.toList())
					);
				
	}
	/**
	 * Encodes an array of strings in the order in which they are given
	 * @param categoricalStrings
	 * @return
	 */
	public static HashMap<String,Integer> encodeCategorical(String... categoricalStrings){

		return OptUtils.encodeCategorical(
				Stream.of(categoricalStrings)
					.collect(Collectors.toList())
				);
	}
	/**
	 * Encode a list of strings into integers and store in a hashmap
	 * @param categoricalStrings
	 * @return hashmap that maps string to integer
	 */
	public static HashMap<String,Integer> encodeCategorical(List<String> categoricalStrings){
		HashMap<String,Integer> encodeMap = new HashMap<String,Integer>();
		int encodeInt = -1;		
		for (String str : categoricalStrings){
			encodeMap.put(str, ++encodeInt);
		}
		return encodeMap;
	}
	/**
	 * Returns a cloned version of the input array
	 * @param parameters
	 * @return
	 */
	public static Parameter[] cloneParameterArray(Parameter[] parameters){
		return Stream.of(parameters).map((parameter) -> {
			try {
				return parameter.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			return parameter;
		}).collect(Collectors.toList()).toArray(new ParticleParameter[parameters.length]);
	}
	

}
