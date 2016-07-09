package src;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.text.*;

/**
 * <h1>median_degree</h1>
 * This is the main function reads the input text file and output the PeriodGraph
 * results.
 *
 * @author Fuquan Wang
 */
public class median_degree {
	public static void main(String[] args){
		Path file = Paths.get("venmo_input/venmo-trans.txt");

		PrintWriter writer = null;
		try{
			writer = new PrintWriter(new OutputStreamWriter( new FileOutputStream("venmo_output/output.txt"), "utf-8"));
		} catch (IOException ex) {
			System.out.println(ex);
			return;
		}

		PeriodGraph graph = new PeriodGraph(60);
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				VemonTransParser vtp = new VemonTransParser(line);
				try{
					graph.addTransaction( vtp.getActor(), vtp.getTarget(), vtp.getTime() );
					writer.format( "%.2f", graph.getMedian() );
					writer.println();
				} catch (ParseException ex){
					System.out.println("The JSON "+line+" cannot be parsed correctly, no new output");
				}
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			return;
		} finally {
			writer.close();
		}
	}
}
