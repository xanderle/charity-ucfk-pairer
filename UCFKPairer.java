import java.util.*;
import java.io.*;
import java.lang.Math.*;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.alg.KuhnMunkresMinimalWeightBipartitePerfectMatching;
import org.jgrapht.alg.HopcroftKarpBipartiteMatching;

import com.opencsv.CSVReader;

public class UCFKPairer {

  private static int experience;
  private static SimpleWeightedGraph<Volunteer, DefaultWeightedEdge> graph;

  // Read in file given and initialise
  public static void init(String maleFile, String femaleFile)
  {

      graph = new SimpleWeightedGraph<Volunteer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
      ArrayList<Volunteer> males = new ArrayList<Volunteer>();
      ArrayList<Volunteer> females = new ArrayList<Volunteer>();

      // Females read and add to ArrayList
      parseFiles(maleFile, males);
      parseFiles(femaleFile, females);

      // Create graph from information
      fillGraph(males, females);
  }

  public static void parseFiles(String filename, ArrayList<Volunteer> array)
  {
    String temp[];
    Volunteer volunteer;
    boolean exp;
    boolean driver;
    try {
      CSVReader reader = new CSVReader(new FileReader(filename), ',' , '"' , 1);
      String[] nextLine;
      while ((temp = reader.readNext()) != null) {
         if (temp != null) {

          // Check if experienced, if picnics + camps is greater than 3
          if (Integer.parseInt(temp[4]) + Integer.parseInt(temp[5]) >= experience)
          {
            exp = true;
          }
          else
          {
            exp = false;
          }
          // Check if can be a driver
          if (temp[6].equals("Yes") && temp[7].equals("Yes"))
          {
            driver = true;
          }
          else
          {
            driver = false;
          }

          volunteer = new Volunteer(temp[1], temp[2], temp[3], exp, driver, temp[8], Double.parseDouble(temp[9]), Double.parseDouble(temp[10]));
          array.add(volunteer);
      }

      }
    } catch (Exception exception) {
        exception.printStackTrace();
    }
  }

  public static void fillGraph(ArrayList<Volunteer> m, ArrayList<Volunteer> f)
  {

    Haversine h = new Haversine();
    boolean compatible;

    for (Volunteer girl : f)
    {
      graph.addVertex(girl);
    }

    // Populate graph for first run to determine number of pairings
    for (Volunteer boy : m)
    {
      graph.addVertex(boy);
      for (Volunteer girl : f)
      {
        // Check if compatible...
        // TODO: Check against last list of pairings
        if ( !boy.driver && !girl.driver )
        {
            continue;
        }
        else
        {
          if ( boy.exp != girl.exp )
          {
            // How to get edge weights
            DefaultWeightedEdge e = graph.addEdge(boy, girl);
            graph.setEdgeWeight(e, h.haversine(boy.lat, boy.lng, girl.lat, girl.lng));
          }
        }
      }
    }

    Set<Volunteer> p1 = new HashSet<Volunteer>(m);
    Set<Volunteer> p2 = new HashSet<Volunteer>(f);

    HopcroftKarpBipartiteMatching<Volunteer, DefaultWeightedEdge> alg =
        new HopcroftKarpBipartiteMatching<Volunteer, DefaultWeightedEdge>(graph, p1, p2);

    Set<DefaultWeightedEdge> match = alg.getMatching();

    ArrayList<Volunteer> s = new ArrayList<Volunteer>();
    ArrayList<Volunteer> t = new ArrayList<Volunteer>();

    for (DefaultWeightedEdge pair : match)
    {
      s.add(graph.getEdgeSource(pair));
      t.add(graph.getEdgeTarget(pair));
      // System.out.println(graph.getEdgeSource(pair).name + " and " + graph.getEdgeTarget(pair).name + " are " + graph.getEdgeWeight(pair) + "kms away.");
    }

    // System.out.println(match);
    System.out.println(match.size() + " PAIRS.");

    graph = new SimpleWeightedGraph<Volunteer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

    for (Volunteer girl : f)
    {
      graph.addVertex(girl);
    }

    // Fill in empty edges with large weighting
    for (Volunteer boy : s)
    {
      graph.addVertex(boy);
      for (Volunteer girl : t)
      {
        // Check if compatible...
        DefaultWeightedEdge e = graph.addEdge(boy, girl);
        // TODO: Check against last list of pairings
        if ( !boy.driver && !girl.driver )
        {
            graph.setEdgeWeight(e, 100000);
        }
        else
        {
          if ( boy.exp != girl.exp )
          {
            // How to get edge weights
            graph.setEdgeWeight(e, h.haversine(boy.lat, boy.lng, girl.lat, girl.lng));
          }
          else
          {
            graph.setEdgeWeight(e, 100000);
          }
        }
      }
    }

    // Find minimal matching
    KuhnMunkresMinimalWeightBipartitePerfectMatching km =
      new KuhnMunkresMinimalWeightBipartitePerfectMatching(graph, s, t);


    match = km.getMatching();
    String formatted = String.format("\n%-25s |   %-25s |   %10s |  \n", "BOY", "GIRL", "DISTANCE");
    System.out.println(formatted);

    PairPrinter pp = new PairPrinter(match, graph);
    pp.print("pairs.csv");

    formatted = String.format("\n%5.2fkm total travel distance.\n", km.getMatchingWeight());
    System.out.println(formatted);

  }

  public int maximumPairs()
  {
    return 0;
  }

  public static void main(String[] args)
  {

    if (args.length < 3)
    {
      System.out.println("Please enter the filenames of the male and female data, and the experience threshold.");
      System.out.println("i.e.\njava UCFKPairer M.csv F.csv 3");
      return;
    }

    experience = Integer.parseInt(args[2]);

    // TODO: replace with variables
    init(args[0], args[1]);

  }

}
