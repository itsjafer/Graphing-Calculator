/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphing.calculator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author Jafer Haider
 */
public class GraphingCalculator extends JComponent implements KeyListener {

    // Height and Width of our game
    static final int WIDTH = 500;
    static final int HEIGHT = 500;

    // the precision of the points calculated
    int precision = 15;

    // the number of 'ticks' on each axis
    int numberOfTicks = 10;

    // the calculation for determining the distance between each tick
    int distanceBetweenTicks = Math.max(WIDTH, HEIGHT) / numberOfTicks / 2;

    String enteredFunction = "";

    // We're going to store and x,y values in these arraylists. TODO: Use a 2D arraylist instead.
    ArrayList<Double> xValues = new ArrayList();
    ArrayList<Double> yValues = new ArrayList();

    long desiredFPS = 15;
    long desiredTime = (1000) / desiredFPS;

    
    public void variableExpression(String expr) {
        distanceBetweenTicks = Math.max(WIDTH, HEIGHT) / numberOfTicks / 2;
        xValues.clear();
        yValues.clear();
        // special case for the evaluation of negative coefficients
        if (expr.charAt(0) == '-') {
            expr = "0" + expr;
        }
        if (!enteredFunction.trim().isEmpty()) {
            for (int x = -WIDTH / 2 * precision; x <= WIDTH / 2 * precision; x++) {
                // we sub the value of x into the equation and evaluate it
                String subbedExpr = expr.replaceAll("x", "(" + x + "/" + (distanceBetweenTicks * precision) + ")");
                // we also sub in a very close approximation of Euler's number as needed
                subbedExpr = subbedExpr.replace("e", "(" + Math.E + ")");

                // we initializae our evaluaiton class and use it to find the value of Expr in terms of x
                EvaluateExpression eval = new EvaluateExpression(subbedExpr);
                double calculatedValue = eval.parse();

                // checking if the value exists, we proceed to add the point (x,y)
                if (!Double.isNaN(calculatedValue)) {
                    xValues.add(WIDTH / 2 + (double) x / precision);
                    yValues.add(WIDTH / 2 - calculatedValue * distanceBetweenTicks);
                }
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // creates a windows to show my game
        JFrame frame = new JFrame("Graphing Calculator");

        // creates an instance of my game
        GraphingCalculator graph = new GraphingCalculator();
        // sets the size of my game
        graph.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // adds the game to the window
        frame.add(graph);

        // sets some options and size of the window automatically
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // shows the window to the user
        frame.setVisible(true);
        frame.addKeyListener(graph);
        graph.run();
        // starts my game loop
    }

    public void run() {
        // Used to keep track of time used to draw and update the game
        // This is used to limit the framerate later on
        long startTime;
        long deltaTime;

        // the main game loop section
        // game will end if you set done = false;
        boolean done = false;
        while (!done) {
            // determines when we started so we can keep a framerate
            startTime = System.currentTimeMillis();

            // all your game rules and move is done in here
            // GAME LOGIC STARTS HERE 
            repaint();
            // GAME LOGIC ENDS HERE 
            // update the drawing (calls paintComponent)
            // SLOWS DOWN THE GAME BASED ON THE FRAMERATE ABOVE
            // USING SOME SIMPLE MATH
            deltaTime = System.currentTimeMillis() - startTime;
            if (deltaTime > desiredTime) {
                //took too much time, don't wait
            } else {
                try {
                    Thread.sleep(desiredTime - deltaTime);
                } catch (Exception e) {
                };
            }
        }
    }

    // drawing of the game happens in here
    // we use the Graphics object, g, to perform the drawing
    // NOTE: This is already double buffered!(helps with framerate/speed
    @Override
    public void paintComponent(Graphics g) {
        // always clear the screen first!
        g.clearRect(0, 0, WIDTH, HEIGHT);

        // defining the color grey
        Color Grey = new Color(166, 166, 166);
        // GAME DRAWING GOES HERE 

        // draw the graph layout
        g.setColor(Grey);
        g.fillRect(0, 0, WIDTH, HEIGHT); //the size of the graphing background

        int xaxisPos = (int) WIDTH / 2; // find the middle of the screen width-wise
        int yaxisPos = (int) HEIGHT / 2; // find the middle of the screen length-wise

        distanceBetweenTicks = Math.max(WIDTH, HEIGHT) / numberOfTicks / 2;

        // we want black axes
        g.setColor(Color.BLACK);
        g.fillRect(0, xaxisPos, WIDTH, 2); //drawing the x-axis
        g.fillRect(yaxisPos, 0, 2, HEIGHT); //drawing the y-axis

        // we draw the 'ticks' on the axis by going left and right from the middle
        //     from the middle of the screen
        for (int x = 0; x <= xaxisPos; x += distanceBetweenTicks) {
            g.fillRect(xaxisPos + x, xaxisPos - 1, 2, 4);
            g.fillRect(xaxisPos - x, xaxisPos - 1, 2, 4);
        }
        // we draw the 'ticks' on the axis by going up and down from the middle
        //     from the middle of the screen
        for (int y = 0; y <= yaxisPos; y += distanceBetweenTicks) {
            g.fillRect(yaxisPos - 1, yaxisPos + y, 4, 2);
            g.fillRect(yaxisPos - 1, yaxisPos - y, 4, 2);
        }

        // we draw each x,y point for the given expression
        for (int z = 0; z != xValues.size(); z++) {
            g.fillOval(xValues.get(z).intValue(), yValues.get(z).intValue(), 4, 4);
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g.drawString("y=" + enteredFunction, 15, 30);
        // GAME DRAWING ENDS HERE
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if ("()0123456789x^./*+-|sincostaelg".contains("" + e.getKeyChar())) {
            enteredFunction += e.getKeyChar();
        } else if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            variableExpression(enteredFunction);
        }
        if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
            if (!(enteredFunction).equals("")) {
                enteredFunction = enteredFunction.substring(0, enteredFunction.length() - 1);
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            numberOfTicks++;
            variableExpression(enteredFunction);
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            if (numberOfTicks != 1) {
                numberOfTicks--;
            }
            variableExpression(enteredFunction);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
