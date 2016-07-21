package src;
import java.util.*;
import java.io.*;
import java.text.*;

/**
 * <h1>PeriodGraph</h1>
 * Builds and maintains a graph of connections within a given length of time, 
 * supports to get the median of the vertice's degrees using the class 
 * <code>MedianCalculator</code>
 *
 * @author Fuquan Wang
 * @version 1.0
 * @param lastTime The time stamp of the latest transaction
 * @param timeMap The map of Time in seconds to a graph edge
 * @param checkMap The map of graph edge to time in seconds, used to make sure 
 * no double-connection exist and time stamp properly updated for the timeMap
 * @param transMap The actual map of the persons with recorded transactions as
 * edges
 * @param mc <code>MedianCalculator</code> to calculate the median in data stream
 * @param df <code>DateFormat</code> in the format of the code challenge request
 * @param period The length of the time to keep transaction records
 */

public class PeriodGraph {
	private long lastTime;
	private HashMap<Long, HashSet<String>> timeMap;
	private HashMap<String, Long> checkMap;
	private HashMap<String, HashSet<String>> transMap;
	private MedianCalculator mc;
	private static final DateFormat df = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss'Z'");
	private final int period;

	/**
	 * This construction initializes all maps and the <code>MedianCalculator</code>.
	 * @param period The length of the time in seconds to keep transaction records
	 */
	public PeriodGraph( int period ){
		lastTime=0;
		timeMap = new HashMap<Long, HashSet<String>>();
		checkMap = new HashMap<String, Long>();
		transMap = new HashMap<String, HashSet<String>>();
		mc = new MedianCalculator();
		this.period = period;
	}

	/**
	 * This method is the main method to update the map: including checking the input
	 * validty, checking if the time stamp is out of order, updating time stamp of
	 * the existing edges, removing the edges/vertice more period seconds ago and
	 * (of course) adding the new vertice/edges
	 * @param actor The actor value field of the JSON string
	 * @param target The target value field of the JSON string
	 * @param time The time value field of the JSON string
	 * @return The add operation is successful or not
	 */
	public boolean addTransaction( String actor, String target, String time ) {
		// Check the validity of the inputs:
		// Empty fields will or same person transfers will not be counted
		if( actor==null || actor.length()==0 || target==null || target.length()==0 || time==null || time.length()==0 ) {
			System.out.println("At least one field is empty, no new output generated");
			return false;
		}
		if( actor.equals(target) ) {
			System.out.println("Are you sure to send the money from "+actor+" to "+target+" (the same person)?");
			return false;
		}

		long timeInSeconds = toSeconds(time);
		if( timeInSeconds == (long)(-period) ){
			System.out.println("The create_time field is not in the format of yyyy-mm-ddTHH:MM:SSZ, no new output generated");
			return false;
		}
		if ( lastTime-timeInSeconds>=period ) return true; // Do nothing if the new item is more than period seconds ago

		// The graph is undirected, so a connection is presented as a concatenated string
		// in alphabetical order, which is separated with a special character
		StringBuilder sb = new StringBuilder();
		if( actor.compareTo(target)<0 ) sb.append(actor).append('\0').append(target);
		else sb.append(target).append('\0').append(actor);
		String str = new String(sb.toString());

		// If the edge exists, only update the time stamp and do not further update  the graph,
		// otherwise continue to update all stuffs
		if( checkMap.containsKey( str ) ){
			long oldTime = checkMap.get(str);
			if( oldTime<timeInSeconds ){
				if( timeMap.containsKey(oldTime) ) timeMap.get( oldTime ).remove(str);
				checkMap.put( str, timeInSeconds );
				if ( !timeMap.containsKey( timeInSeconds ) )
					timeMap.put( timeInSeconds, new HashSet<String>() );
				timeMap.get(timeInSeconds).add(str);
			}
			return true;
		}
		checkMap.put( str, timeInSeconds );

		// Add the new edge into the timeMap with the current tiem stamp
		// Compatible with Java 8. Using the old way for backward compatibility
		// timeMap.getOrDefault( timeInSeconds, new HashSet<String>() ).add( str );
		if ( !timeMap.containsKey( timeInSeconds ) )
			timeMap.put( timeInSeconds, new HashSet<String>() );
		timeMap.get(timeInSeconds).add(str);

		// Check and remove the old entries only if the new time stamp is more recent
		if ( timeInSeconds>lastTime ){
			lastTime = timeInSeconds; // Update to the latest time stamp
			removeOldEntries();
		}

		// Store the current degree of the two vertice to be updated
		int prevActor = transMap.containsKey(actor) ? transMap.get(actor).size() : 0;
		int prevTarget = transMap.containsKey(target) ? transMap.get(target).size() : 0;

		// Add the transaction
		// Compatible with Java 8. Using the old way for backward compatibility
		// transMap.getOrDefault( actor, new HashSet<String>() ).add( target );
		// transMap.getOrDefault( target, new HashSet<String>() ).add( actor );
		if( !transMap.containsKey( actor ) )
			transMap.put( actor, new HashSet<String>() );
		transMap.get(actor).add(target);
		if( !transMap.containsKey( target ) )
			transMap.put( target, new HashSet<String>() );
		transMap.get(target).add(actor);

		// Update the vertice median in the <code>MedianCalculator</code>
		if( prevActor>0 ) mc.remove( prevActor );
		if( prevTarget>0 ) mc.remove( prevTarget );
		mc.add( transMap.get(actor).size() );
		mc.add( transMap.get(target).size() );

		return true;
	}

	/**
	 * This method directly call <code>MedianCalculator.findMedian()</code> method
	 * to get the median from the data stream.
	 */
	public double getMedian(){
		return mc.findMedian();
	}

	/**
	 * This method parses the time String to a long in seconds.
	 * @param time The input time string
	 * @return Long in seconds
	 */
	private long toSeconds(String time){
		Date date = null;
		try {
			date = df.parse(time);
		} catch (ParseException pe){
			System.out.println(pe);
		}
		// In case of ParseException of the time string, set the time to -period (discard this transaction)
		if( date!=null ) return date.getTime()/1000;
		else return -period;
	}

	/**
	 * The main method to check and remove the entries more than period
	 * seconds ago. It loops over the time stamps (number of period at most),
	 * and removes the edges in <code>checkMap</code> and updates the vertic
	 * information of the <code>transMap</code>
	 */
	private void removeOldEntries(){
		// List of outdated time stamps to be removed
		List<Long> toRemoveList = new ArrayList<Long>();
		// Maps of vertex to degree, before and after the removal
		HashMap<String,Integer> initialCount = new HashMap<>();
		HashMap<String,Integer> finalCount = new HashMap<>();
		for( long t: timeMap.keySet() ){
			if( lastTime-t>=period ){
				for( String pair: timeMap.get(t) ){
					// Remove the outdated edge
					checkMap.remove( pair );
					int separator = pair.indexOf('\0');
					// Using an array for concise coding of the vertex pair
					String[] vertice = { pair.substring(0,separator), pair.substring(separator+1) };
					for( int i=0; i<2; i++ ){
						String vertex = vertice[i];
						String neighbor = vertice[i==0?1:0];
						// Remove the edge and update the degrees in the MedianCalculator
						if( transMap.containsKey(vertex) ){
							HashSet<String> set = transMap.get(vertex);
							if( initialCount.containsKey(vertex) )
								finalCount.put(vertex, finalCount.get(vertex)-1);
							else {
								initialCount.put( vertex, set.size() );
								finalCount.put( vertex, set.size()-1 );
							}
							set.remove(neighbor);
							// Vertex with no edges will be removed
							if( set.isEmpty() )
								transMap.remove(vertex);
						}
					}
				}
				// Add the outdated time stamp to the list
				toRemoveList.add(t);
			}
		}
		for( long t: toRemoveList ) timeMap.remove(t);
		removeCounts( initialCount, finalCount );
	}

	/**
	 * This method updates the <code>MedianCalculator</code> in an efficient way.
	 * The vertex to degree map is converted to number-of-edges to count map.
	 * Then the maps before and after removal are compared so that only the degree
	 * changes will be propagated to <code>MedianCalculator</code> to minimize the 
	 * add and remove function calls.
	 */
	private void removeCounts( HashMap<String,Integer> initialCount, HashMap<String,Integer> finalCount ){
		// Convert the map format
		HashMap<Integer,Integer> oldDegreeMap = new HashMap<Integer,Integer>();
		for( String s: initialCount.keySet() ){
			int degree = initialCount.get( s );
			if( !oldDegreeMap.containsKey(degree) )
				oldDegreeMap.put( degree, 1 );
			else oldDegreeMap.put( degree, oldDegreeMap.get(degree)+1 );
		}
		HashMap<Integer,Integer> newDegreeMap = new HashMap<Integer,Integer>();
		for( String s: finalCount.keySet() ){
			int degree = finalCount.get( s );
			if( !newDegreeMap.containsKey(degree) )
				newDegreeMap.put( degree, 1 );
			else newDegreeMap.put( degree, newDegreeMap.get(degree)+1 );
		}
		// For the common degrees in two maps before and after removal, keep only the 
		// positive changes in each map
		for( int degree: oldDegreeMap.keySet() ){
			if( newDegreeMap.containsKey(degree) ){
				int oldCount = oldDegreeMap.get(degree);
				int newCount = newDegreeMap.get(degree);
				oldDegreeMap.put(degree, oldCount-newCount);
				newDegreeMap.put(degree, newCount-oldCount);
			}
		}
		// Remove the degrees from <code>MedianCalculator</code>
		for( int degree: oldDegreeMap.keySet() ){
			int count = oldDegreeMap.get(degree);
			if( count>0 )
				for( int i=0; i<count; i++ )
					mc.remove( degree );
		}
		// Add the degrees to <code>MedianCalculator</code>
		for( int degree: newDegreeMap.keySet() ){
			// Vertex with 0 degrees will not be considered any more
			if( degree<=0 ) continue;
			int count = newDegreeMap.get(degree);
			if( count>0 )
				for( int i=0; i<count; i++ )
					mc.add( degree );
		}
	}
}
