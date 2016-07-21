package src;
import java.util.*;
import java.io.*;

/**
 * <h1>MedianCalculator</h1>
 * Returns the median of a data stream, supporting adding and removing operations
 *
 * @author Fuquan Wang
 * @version 1.0
 * @param lo a max-heap to keep the numbers below or equals to the median
 * @param hi a min-heap to keep the numbers above or equals to the median
 */

public class MedianCalculator {
	private PriorityQueue<Integer> lo;
	private PriorityQueue<Integer> hi;

	/**
	 * The constructor initials the two heaps and make sure the max-heap is empty
	 */
	public MedianCalculator() {
		lo = new PriorityQueue<Integer>(1,Collections.reverseOrder());
		hi = new PriorityQueue<Integer>();
		while( !lo.isEmpty() ) lo.poll();
	}

	/**
	 * The method is called after adding or removing a number in this instance,
	 * which makes sure the two heaps are balanced: the max-heap has at most one 
	 * element more than the min-heap and the min-heap is not larger than the max-heap
	 */
	private void adjust(){
		if( lo.size()>hi.size()+1 )
			hi.add( lo.poll() );
		else if( lo.size()<hi.size() )
			lo.add( hi.poll() );
	}

	/**
	 * This method adds a number to this instance: adding to max-heap if only the 
	 * number is smaller than the heap's maximum value, other to the min-heap.
	 * The time complexity is O(logN) where N is the number of current elements in this instance
	 * @param num the number to be added
	 */
	public void add( int num ){
		if( lo.isEmpty() || num<lo.peek() )
			lo.add(num);
		else
			hi.add(num);
		adjust();
	}

	/**
	 * This method removes a number to this instance after checking which heap the
	 * number is in. If it is not in any of them, no action is taken.
	 * The time complexity is O(logN) where N is the current elements in this instance
	 * @param num the number to be removed
	 */
	public void remove( int num ){
		if( lo.isEmpty() && hi.isEmpty() ) return;
		else if( !lo.isEmpty() && num<=lo.peek() ) lo.remove( num );
		else if( !hi.isEmpty() && num>=hi.peek() ) hi.remove( num );
		adjust();
	}

	/**
	 * This method returns the median of the current data stream with time 
	 * complexity O(1). The max-heap has at most one more element. If it has one
	 * more element, its top is the median. Otherwise the average of the two heaps'
	 * tops is the median.
	 * @return The current median
	 */
	public double findMedian() {
		if( lo.isEmpty() ) return 0; // Protection for calling this method before adding anything
		else if( lo.size()>hi.size() ) return (double)lo.peek();
		else{
			double x = (double)lo.peek();
			double y = (double)hi.peek();
			return (x+y)/2.;
		}
	}
}
