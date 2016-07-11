package src;
import java.util.*;
import java.io.*;
import java.text.*;

/**
 * <h1>VemonTransParser</h1>
 * Parses the JSON string as code challenge requests. A general JSON parser
 * is more suitable if the "exotic" library can be easily applied
 *
 * @author Fuquan Wang
 * @version 1.0
 * @param createTime the processed string of the time
 * @param actor the processed string of the actor
 * @param target the processed string of the target
 */

public class VemonTransParser {
	String createTime;
	String actor;
	String target;

	/**
	 * This constructor takes the json string and use <code>getMatch</code> to parse
	 * the needed elements.
	 * @param json the input JSON string
	 */
	public VemonTransParser( String json ){
		try{
			createTime = getMatch( json, "\"created_time\"" );
		} catch( ParseException ex ){
			createTime = "";
			System.out.println("Cannot find the created_time key+value in the JSON string, setting it to an empty string.");
			System.out.println( ex );
		}
		try{
			actor = getMatch( json, "\"actor\"" );
		} catch( ParseException ex ){
			actor = "";
			System.out.println("Cannot find the actor key+value in the JSON string, setting it to an empty string.");
			System.out.println( ex );
		}
		try{
			target = getMatch( json, "\"target\"" );
		} catch( ParseException ex ){
			target = "";
			System.out.println("Cannot find the target key+value in the JSON string, setting it to an empty string.");
			System.out.println( ex );
		}
	}

	/**
	 * @return The processed string of the time
	 */
	public String getTime() {
		return createTime;
	}

	/**
	 * @return The processed string of the actor
	 */
	public String getActor() {
		return actor;
	}

	/**
	 * @return The processed string of the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * This method matches the pattern of the keys from the JSON string and
	 * returns the value field. The time complexity is O(m+n), where m and n
	 * are the lengths of the JSON and pattern strings.
	 * @return The matched the string in the value field
	 * @throws ParseException in case of failed parsing
	 */
	private String getMatch( String json, String pattern ) throws ParseException{
		int idxbrace = json.indexOf( '{' );
		if( idxbrace<0 )
			throw new ParseException("The JSON string does not has a starting {",0);
		int idx = json.indexOf( pattern, idxbrace );
		if( idx<0 )
			throw new ParseException("Cannot find the pattern "+pattern+" in JSON string "+json, idxbrace);

		int idxlo = json.indexOf( '"', pattern.length()+idx );
		while( idxlo>0 && json.charAt(idxlo-1)=='\\' ) idxlo = json.indexOf( '"', idxlo+1 );
		if( idxlo<0 ) 
			throw new ParseException("Cannot find a \" following "+pattern+" in JSON string "+json, idx);
		int idxhi = json.indexOf( '"', idxlo+1 );
		while( idxhi>0 && json.charAt(idxhi-1)=='\\' ) idxhi = json.indexOf( '"', idxhi+1 );
		if( idxhi<0 ) 
			throw new ParseException("Cannot find the second \" following "+pattern+" in JSON string "+json, idxlo);

		int idxend = json.indexOf( ',', idxhi+1 );
		if( idxend<0 )
			idxend = json.indexOf( '}', idxhi+1 );
		if( idxend<0 )
			throw new ParseException("Cannot find , or } after pattern "+pattern+" in JSON string "+json, idxlo);
		String str = json.substring( idx, idxend );
		if( str.indexOf(':')<0 )
			throw new ParseException("No valid semicolon separator in JSON string "+json, idx);

		return json.substring( idxlo+1, idxhi );
	}
}
