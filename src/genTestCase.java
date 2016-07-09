package src;
import java.util.*;
import java.io.*;

/**
 * <h1>genTestCase</h1>
 * This class implements a quick generator for the coding-challenge test cases.
 * The following parameters can be adjusted:
 * number of the different people names (nVtx)
 * number of output lines (nLines)
 * time increase in seconds expectation (inc)
 * the start time in seconds (startDate)
 * twice of the maximum deviation from previous time plus time increase (variaton)
 * @param args use the first argument as the random number seed
 */
public class genTestCase {
	public static void main( String[] args ){
		if( args.length<=0 ) {
			System.out.println("Please input a number as random seed number");
			return;
		}
		Random rdn = new Random( Long.valueOf(args[0]) );

		int nVtx = 5;
		int nLines = 15;
		long startDate = 1220227200L;
		int inc = 15;
		int variation = 180;

		PrintWriter writer = null;
		try{ 
			writer = new PrintWriter(new OutputStreamWriter( new FileOutputStream("venmo_input/venmo-trans.txt"), "utf-8"));
		} catch (IOException ex) {
			System.out.println(ex);
		}

		String[] vertice = new String[nVtx];
		for( int i=0; i<nVtx; i++ )
			vertice[i] = excelStyleString(i);

		Calendar cal = Calendar.getInstance();
		for( int i=0; i<nLines; i++ ){
			startDate += rdn.nextInt(variation)-variation/2+inc;
			Date date = new Date( startDate*1000 );
			cal.setTime(date);
			int idxActor = rdn.nextInt(nVtx);
			int idxTarget = rdn.nextInt(nVtx);
			while( idxTarget==idxActor )
				idxTarget = rdn.nextInt(nVtx);
			StringBuilder sb = new StringBuilder();
			sb.append("{\"actor\": \"").append(vertice[idxActor]).append("\",");
			sb.append("\"target\": \"").append(vertice[idxTarget]).append("\",");
			sb.append("\"created_time\": \"");

			sb.append(cal.get(Calendar.YEAR)).append('-');
			if( cal.get(Calendar.MONTH)>=10 ) sb.append(cal.get(Calendar.MONTH)).append('-');
			else sb.append('0').append(cal.get(Calendar.MONTH)).append('-');
			if( cal.get(Calendar.DAY_OF_MONTH)>=10 ) sb.append(cal.get(Calendar.DAY_OF_MONTH)).append('T');
			else sb.append('0').append(cal.get(Calendar.DAY_OF_MONTH)).append('T');

			if( cal.get(Calendar.HOUR)>=10 ) sb.append(cal.get(Calendar.HOUR)).append(':');
			else sb.append('0').append(cal.get(Calendar.HOUR)).append(':');
			if( cal.get(Calendar.MINUTE)>=10 ) sb.append(cal.get(Calendar.MINUTE)).append(':');
			else sb.append('0').append(cal.get(Calendar.MINUTE)).append(':');
			if( cal.get(Calendar.SECOND)>=10 ) sb.append(cal.get(Calendar.SECOND)).append("Z\"}");
			else sb.append('0'). append(cal.get(Calendar.SECOND)).append("Z\"}");
			writer.println( sb.toString() );
		}

		writer.close();
	}

	private static String excelStyleString( int n ){
		if( n<26 ) return String.valueOf( (char)('A'+n) );
		else {
			StringBuilder sb = new StringBuilder();
			sb.append( excelStyleString(n/26-1) );
			sb.append( excelStyleString(n%26) );
			return sb.toString();
		}
	}
}

