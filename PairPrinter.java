import java.util.*;
import java.io.*;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import com.opencsv.CSVWriter;

/*

Prints out the results of the UCFK Pairer program to a CSV file, ready to email to volunteers.

*/

public class PairPrinter
{

  private Set<DefaultWeightedEdge> pairs;
  private SimpleWeightedGraph<Volunteer, DefaultWeightedEdge> graph;

  public PairPrinter(Set<DefaultWeightedEdge> pairs, SimpleWeightedGraph<Volunteer, DefaultWeightedEdge> graph)
  {

    this.pairs = pairs;
    this.graph = graph;

  }

  public void print(String filename)
  {
    try {
      CSVWriter writer = new CSVWriter(new FileWriter(filename, true));
      String [] record;
      String formatted;

      int count = 1;
      for (DefaultWeightedEdge pair : pairs)
      {
        formatted = String.format("%-25s |   %-25s |   %8.2fkm |  ", graph.getEdgeSource(pair).name, graph.getEdgeTarget(pair).name, graph.getEdgeWeight(pair));
        System.out.println(formatted);

        record = new String[1];
        record[0] = "Family #"+count;
        count++;
        writer.writeNext(record);
        //Create record for the name and phone number of the male volunteer
        record = new String[2];
        record[0] = "Name: " + graph.getEdgeSource(pair).name;
        record[1] = "Phone: " + graph.getEdgeSource(pair).phone;
        //Create record for the address and email of the male volunteer
        writer.writeNext(record);
        //Create record
        record = new String[2];
        record[0] = "Address: " + graph.getEdgeSource(pair).address;
        record[1] = "Email: " + graph.getEdgeSource(pair).email;
        //Write the record to file
        writer.writeNext(record);
        //Create record for the name and phone number of the female volunteer
        record = new String[2];
        record[0] = "Name: " + graph.getEdgeTarget(pair).name;
        record[1] = "Phone: " + graph.getEdgeTarget(pair).phone;
        //Write the record to file
        writer.writeNext(record);
        //Create record for the address and email of the female volunteer
        record = new String[2];
        record[0] = "Address: " + graph.getEdgeTarget(pair).address;
        record[1] = "Email: " + graph.getEdgeTarget(pair).email;
        //Write the record to file
        writer.writeNext(record);

      }
      //close the writer
      writer.close();
    }
    catch (Exception exception) {
        exception.printStackTrace();
    }
  }

}
