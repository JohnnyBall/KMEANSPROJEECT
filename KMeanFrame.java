/* Name:   John Ball
 * Class:  Data Mining
 * Project 2
 *
 *READ ME: To use this simply click initialize to build the chart and points, then when you click the next button it will iterate through the different calculations
 *         and rebuild the chart accordingly. To change the number of means, and start over simply press the k spinner to the desired number of means (2-5) 
 *         and then click initialize again.
 *
 *
 *FURTHER NOTE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * If exception occurs, sorry. Im having different results compiling on different devices... I've never used JavaFX before, and im having issues.
 *
 *ALSO TRY USING THE NEWEST VERSION OF THE JAVA JDK (IM USING jdk1.8.0_92)
 *
 */
import java.io.*;
import java.awt.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;
import java.applet.*;
import javafx.scene.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Random;
import javax.swing.text.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.swing.event.*;
import javax.swing.JComponent;
import javafx.scene.chart.XYChart;
import javafx.embed.swing.JFXPanel;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Point2D.*;
import java.awt.geom.Point2D;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;

public class KMeanFrame extends JFrame implements ActionListener
{
    JButton   initializeButton;
    JButton   nextButton;
    JSpinner  klusterSpinner;
    JLabel    klusterLabel;
    JPanel    bottomPanel;
    JFXPanel  graphPanel;
    Container cp;
    int       numOfMeans;

    Vector<Vector<Point2D>> clusterVector;
    Vector<Point2D> meanVector;

    public KMeanFrame()
    {
        System.out.println("initializing kmeanframe");
        klusterSpinner   = new JSpinner(new SpinnerNumberModel(2,2,5,1));
        klusterLabel     = new JLabel(" K=");
        nextButton       = new JButton("Next");
        initializeButton = new JButton("Initialize");
        bottomPanel      = new JPanel(new FlowLayout());
        graphPanel       = new JFXPanel();

        nextButton.setActionCommand ("NEXT");
        nextButton.addActionListener(this);

        initializeButton.setActionCommand ("INITIALIZE");
        initializeButton.addActionListener(this);

        bottomPanel.add(initializeButton);
        bottomPanel.add(nextButton);
        bottomPanel.add(klusterLabel);
        bottomPanel.add(klusterSpinner);

        cp = getContentPane();
        cp.add(graphPanel, BorderLayout.CENTER);
        cp.add(bottomPanel, BorderLayout.SOUTH);
        setupMainFrame();
    }//end of start

//=================================================================================================================
  public void setupMainFrame()
  {
    Toolkit   tk = Toolkit.getDefaultToolkit();
    Dimension d  = tk.getScreenSize();
    this.setSize(600, 600);
    this.setMinimumSize(new Dimension(500, 500));
    this.setLocation(d.width/4, d.height/4);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setTitle("K-Means Frame");
    this.pack();
    this.setVisible(true);
  }// end of setupMainFrame
//=================================================================================================================
  public void actionPerformed(ActionEvent e)
  {
     if(e.getActionCommand().equals("INITIALIZE"))
     {
        initializePoints();
        chartUpdate();
     }
     else if(e.getActionCommand().equals("NEXT"))
     {
        kmeansAlg();
        chartUpdate();
     }
  }//end of Action performed
//=================================================================================================================
  /*This function will setup the random points whenever initialize is pressed
   *
   */
  public void initializePoints()
  {
    Vector<Point2D> clusterValues = new Vector<Point2D>();
    Point2D  tmpPoint;
    Random rand = new Random();

    while((clusterValues.size() < 20))
    {
        //System.out.println("clusterValues: "+ clusterValues.size());
        tmpPoint = new Point2D.Double(rand.nextInt(20),rand.nextInt(20));
        if(!clusterValues.contains(tmpPoint))
        {
            clusterValues.add(tmpPoint);
            //System.out.println("Added "+tmpPoint +" to clusterYValues.");
        }
    }// end of while

    meanVector = new Vector<Point2D>();
    numOfMeans = (int)klusterSpinner.getValue();
    while(meanVector.size() < numOfMeans)
    {
        //System.out.println("meanVector: "+ meanVector.size());
        tmpPoint = clusterValues.get(rand.nextInt(20));
        if(!meanVector.contains(tmpPoint))
        {
          meanVector.add(tmpPoint);
          //System.out.println("Added "+tmpPoint +" to meanVector.");
        }
    }

    clusterVector = new Vector<Vector<Point2D>>();
    clusterVector.add(clusterValues);
    for(int i =1;i<numOfMeans;i++)
    {
        clusterVector.add(new Vector<Point2D>());
    }
    System.out.println("clusterVector.size(): "+ clusterVector.size());

    System.out.println("clusterValues: "+ clusterValues.toString());
  }// ennd of initialize points
//=================================================================================================================
    /*This function will do the Kmeans calculation 
   *
   */
  public void kmeansAlg()
  {
    int minIndex;
    double minValue;
    double kmeansCalc;
    double xMeanAvg;
    double yMeanAvg;
    Point2D  tmpPoint;
    Point2D  tmpMeanPoint;


///------------------------------------------------------------------------------------------------------------------------
    /*The area between these separations will move the points to the appropriate clusters depending on the means
     *
     */
    for(int i = 0; i < clusterVector.size(); i++)// iterates through the vectors of clusters
        {
            System.out.println("clusterVector.get(i).size(): "+clusterVector.get(i).toString());
            for(int j = (clusterVector.get(i).size()-1); j >= 0; j--)// iterates through the vector of vector points
            {
               tmpPoint = clusterVector.get(i).get(j);
               minValue = 100.0;
               minIndex = 0;
               for(int k = 0; k < meanVector.size();k++)
               {
                  tmpMeanPoint = meanVector.get(k);
                  System.out.print("Checking distance between tmpPoint: " + tmpPoint.toString()+ " and tmpMeanPoint: " + tmpMeanPoint.toString());
                  /*The line below this calculates the Euclidean distance of the points, between the mean and the point being checked*/
                  kmeansCalc = Math.sqrt(((tmpPoint.getX()-tmpMeanPoint.getX())*(tmpPoint.getX()-tmpMeanPoint.getX()))+
                                            ((tmpPoint.getY()-tmpMeanPoint.getY())*(tmpPoint.getY()-tmpMeanPoint.getY())));


                  System.out.println(" Calculated kmeansCalc: "+ kmeansCalc);
                  /** this if will check to see if the value calculated is a smaller distance, if it is then it will store its index of the mean and the value
                   *  so that it can be updated after all of the checks/calculations have been made.
                   */
                  if(minValue > kmeansCalc)
                  {
                    System.out.println("NEW K MEANS FOR CLUSTER "+ k + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    System.out.print(" replacing minIndex:"+minIndex+ " with: " + k);
                    minIndex = k;
                    System.out.println(" replacing minValue:"+minValue+ " with: "+kmeansCalc);
                    minValue = kmeansCalc;
                    System.out.println("");
                    System.out.println("");
                  }// end of if
               }// end of meanVector for loop

                if(minIndex != i)
                {

                    System.out.println("Placing tmpPoint in new vector, min index and i didnt match: minIndex: "+minIndex+" i:"+i);
                    clusterVector.get(i).remove(j);
                    clusterVector.get(minIndex).add(tmpPoint);// resets the temp point into its new position
                }
            }// end of for(int j
        }//end of for(int i
///------------------------------------------------------------------------------------------------------------------------
/* This section will calculate the new means based on the values of the numbers stored in the vectors
 */
    for(int k= 0; k < meanVector.size(); k++)// runs through each k
    {
        xMeanAvg = 0.0;
        yMeanAvg = 0.0;
        for(int j = 0; j < clusterVector.get(k).size(); j++)// iterates through the vector of vector points
        {
            tmpPoint = clusterVector.get(k).get(j);// will get the points for calculating the new mean.
            xMeanAvg = xMeanAvg + tmpPoint.getX();
            yMeanAvg = yMeanAvg + tmpPoint.getY();
        }// end of for(int j
        tmpMeanPoint = new Point2D.Double((xMeanAvg/clusterVector.get(k).size()),(yMeanAvg/clusterVector.get(k).size()));
        System.out.println("Old Mean: " +meanVector.get(k).toString()+" New Mean: "+ tmpMeanPoint +" *********************************************");
        meanVector.set(k,tmpMeanPoint);
    }//end of for(int k
  }//end of kmeansAlg()
//=================================================================================================================
    /*This function will setup the chart and draw it
   *
   */
  public void chartUpdate()
  {
        XYChart.Series tmpSeries;
        XYChart.Series meanSeries;

        System.out.println("Initialize axis");
        final NumberAxis xAxis = new NumberAxis(0, 20, 1);
        final NumberAxis yAxis = new NumberAxis(0, 20, 1);
        Point2D tmpPoint;
        Vector <XYChart.Series> seriesVector;
        //System.out.println("Setting up scatter chart");
        ScatterChart<Number,Number> scatterChart = new ScatterChart<Number,Number>(xAxis,yAxis);

        xAxis.setLabel("X-Values");
        yAxis.setLabel("Y-Values");
        scatterChart.setTitle("K-Means Chart");

		seriesVector = new Vector<XYChart.Series>();
//----------------------------------------------------------------------------------------
        // sets up the series data from the clusters vectors
		for(int i = 0; i < clusterVector.size(); i++)// iterates through the list of series points and vectors
        {
            tmpSeries = new XYChart.Series();
            tmpSeries.setName("cluster " + (i+1));
            //System.out.println("clusterVector.get(i).size(): "+clusterVector.get(i).size());
            for(int j = 0; j < clusterVector.get(i).size();j++)// iterates through the vector of vector points
            {
             tmpPoint = clusterVector.get(i).get(j);
            // System.out.println("tmpPoint: "+tmpPoint.toString());
             tmpSeries.getData().add(new XYChart.Data(tmpPoint.getX(), tmpPoint.getY()));//series1.getData().add(new XYChart.Data(4.2, 19.2));
            }
            seriesVector.add(i,tmpSeries);
        }
//----------------------------------------------------------------------------------------
        // sets up the mean areas for the chart
        meanSeries = new XYChart.Series();
        meanSeries.setName("Mean cluster");
        for(int k=0;k<meanVector.size();k++)
        {
            tmpPoint = meanVector.get(k);
            //System.out.println("tmpPoint: "+tmpPoint.toString());
            meanSeries.getData().add(new XYChart.Data(tmpPoint.getX(), tmpPoint.getY()));//series1.getData().add(new XYChart.Data(4.2, 19.2));
        }
//----------------------------------------------------------------------------------------

       // System.out.println("seriesVector.size(): "+seriesVector.size());
        for(int i =0; i < seriesVector.size(); i++)// adds the points to the graph
        {
            scatterChart.getData().add(seriesVector.get(i));
        }
        scatterChart.getData().add(meanSeries);
//----------------------------------------------------------------------------------------
        Scene scene  = new Scene(scatterChart, 500, 400);
        graphPanel.setScene(scene);
  }// end of chartUpdate
//=================================================================================================================

  public static void main (String args[])
  {
    SwingUtilities.invokeLater(new Runnable()
    {
        @Override public void run()
       {
            new KMeanFrame();
        }
    });
  }
//=================================================================================================================
}// class end
