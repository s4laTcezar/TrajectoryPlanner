package com.example;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.Color;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JFrame;


import org.math.plot.Plot2DPanel;

public class Main {

    static List<Double> xCenter = new LinkedList<>();
    static List<Double> yCenter = new LinkedList<>();
    static List<Double> xRight = new LinkedList<>();
    static List<Double> yRight = new LinkedList<>();
    static List<Double> xLeft = new LinkedList<>();
    static List<Double> yLeft = new LinkedList<>();

    public static void main(String[] args) {

        System.out.println("Choose a trajectory you want to render: ");
        System.out.println("1 - trajectory along the predetermined time vector and velocity vector");
        System.out.println("2 - trajectory along the square");
        System.out.println("3 - trajectory along the curve");

        Scanner enter = new Scanner(System.in);

        int k = enter.nextInt();

        switch(k){
            case 1:{
                Trajectory();
                PlotTrajectories();
                break;
            }
            case 2:{
                Stvorec();
                PlotTrajectories();
                break;
            }
            case 3:{
                Curve();
                PlotTrajectories();
                break;
            }
        }
    }

    public static void PlotTrajectories(){

        Plot2DPanel plot = new Plot2DPanel();

        double[] xcenter = new double[xCenter.size()];
        double[] ycenter = new double[yCenter.size()];
        double[] xright = new double[xRight.size()];
        double[] xleft = new double[xLeft.size()];
        double[] yright = new double[yRight.size()];
        double[] yleft = new double[yLeft.size()];

        xcenter = xCenter.stream().mapToDouble(Double::doubleValue).toArray();
        ycenter = yCenter.stream().mapToDouble(Double::doubleValue).toArray();

        xright = xRight.stream().mapToDouble(Double::doubleValue).toArray();
        yright = yRight.stream().mapToDouble(Double::doubleValue).toArray();

        xleft = xLeft.stream().mapToDouble(Double::doubleValue).toArray();
        yleft = yLeft.stream().mapToDouble(Double::doubleValue).toArray();

        plot.addScatterPlot(null, Color.BLACK, xcenter, ycenter);
        plot.addScatterPlot(null, Color.GREEN,xright, yright);
        plot.addScatterPlot(null, Color.RED, xleft, yleft);


        JFrame frame = new JFrame("Movement");
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
        frame.setSize(600, 600);
        frame.setContentPane(plot);
        frame.setVisible(true);
    }

    public static void ClearLists(){
        xCenter.clear();
        yCenter.clear();

        xRight.clear();
        xLeft.clear();

        yRight.clear();
        yLeft.clear();
    }

    public static void Stvorec(){

        ClearLists();
        
        Scanner scan = new Scanner(System.in);

        System.out.print("Enter a side: ");
        double side = scan.nextInt();
        double L = 0.2;
        double[] velocityL = VelocityL(side);
        double[] velocityR = VelocityR(side);
        double[] timeVector = timeVector(velocityR, velocityL, side);

        calculateTrajectories(velocityR, velocityL, timeVector, L, 0);
    }

    public static void Trajectory(){
        

        double L = 0.2;
        double[] t = {0, 5, 10, 15, 20};
        double[] rightWheel = {0, 2, -1, 1, 2};
        double[] leftWheel = {0, 2, 1, 1, -2};

        calculateTrajectories(rightWheel, leftWheel, t, L, 0);


    }

    public static void Curve(){
        
        ClearLists();

        double R1;
        double R2;
        double L1;
        Scanner scan = new Scanner(System.in);

        System.out.print("Enter a R1: ");
        R1 = scan.nextDouble();
        System.out.print("Enter a R2: ");
        R2 = scan.nextDouble();
        System.out.print("Enter a L1: ");
        L1 = scan.nextDouble();

        double L = 0.2;
        double[] VelocityR = VelocityR(R1, R2, L1/2);
        double[] VelocityL = VelocityL(R1, R2, L1/2);
        double[] t  = GenerateTimeVectorForCurve(VelocityR, VelocityL, R1, R2, L1);

        calculateTrajectories(VelocityR, VelocityL, t, L, Math.PI/2);

    }

    public static double[] VelocityR(double side){

        double[] velocityR = {side/4, side/4, side/4, side/4, side/4, side/4, side/4, 0};

        return velocityR;

    }

    public static double[] VelocityL(double side){

        double[] velocityL = {side/4, -side/4, side/4, -side/4, side/4, -side/4, side/4, 0};

        return velocityL;

    }

    public static double[] VelocityR(double R1, double R2, double L){


        double velocityR[] = {findVelocity(R1)[0], L/4, findVelocity(R2)[1], 0};

        return velocityR;
    }

    public static double[] VelocityL(double R1, double R2, double L){

        double velocityL[] = {findVelocity(R1)[1], L/4, findVelocity(R2)[0], 0};

        return velocityL;
    }

    public static double[] findVelocity(double R){

        double firstLimit = Math.pow(R, 4.0);
        double Velocity[] = new double[2];
        boolean flag = false;
        double r= 0;
        for(double leftWheel =1; leftWheel<=firstLimit; leftWheel+=0.5)
        {
            if(flag){
                break;
            }
            for (double rightWheel = 1; rightWheel <=firstLimit; rightWheel+=0.5)
            {
                if (rightWheel - leftWheel == 0){
                    continue;
                }
                r = (0.1 * ((rightWheel + leftWheel)/(rightWheel - leftWheel)));
                if (r == R){
                    double Vl = leftWheel;
                    double Vr = rightWheel;
                    Velocity[0] = Vl;
                    Velocity[1] = Vr;
                    flag = true;
                    break;
                } 
            }
        }
        return Velocity;
    }

    public static double[] GenerateTimeVectorForCurve(double[] VelocityR, double[] VelocityL, double R1, double R2, double L1){
        
        double omega1 = Math.abs((VelocityR[0] - VelocityL[0])/0.2); 
        double omega2 = Math.abs((VelocityR[2] - VelocityL[2])/0.2);
        double[] t = {0, ((Math.PI/2)/omega1), L1/VelocityR[1]+((Math.PI/2)/omega1), 
            L1/VelocityR[1]+((Math.PI/2)/omega1)+((Math.PI/2)/omega2)};

        return t;
    }


    public static double[] timeVector(double[] velocityR, double[] velocityL, double side){
        double[] timeVector = new double[8];
        timeVector[0] = 0;
        double omega = velocityR[1]/0.1;
        double timeTmp = side/velocityR[1];

        for(int i = 1; i < 8; i++)
        {
            if ((i % 2) == 0){
                timeVector[i] = timeVector[i-1] + (Math.PI/2)/omega;
            }
            else{ 
                timeVector[i] = timeVector[i-1] + timeTmp;
            }
        }


        return timeVector; 
    }

    public static double calculateCenterSpeed(double rightWheel, double leftWheel) {
        return (rightWheel + leftWheel) / 2.0;
    }

    public static double calculateOmegaCenter(double rightWheel, double leftWheel, double L) {
        return (rightWheel - leftWheel) / L;
    }

    public static void calculateTrajectories(double[] rightWheel, double[] leftWheel, double[] t, double L, double phi) {
        
        double Centerx = 0, Centery = 0;

        for (int i = 0; i < t.length; i++) {
            double omegaCenter = calculateOmegaCenter(rightWheel[i], leftWheel[i], L);
            double Vcenter = calculateCenterSpeed(rightWheel[i], leftWheel[i]);
            double timeLimit = (i + 1 >= t.length) ? t[i] + 5 : t[i + 1];

            double dt = (timeLimit - t[i])/10000;
            for (double j = t[i]; j < timeLimit; j += dt) {
                phi += omegaCenter * dt;
                double Vx = Vcenter * Math.cos(phi);
                double Vy = Vcenter * Math.sin(phi);
                Centerx += Vx * dt;
                Centery += Vy * dt;

                xCenter.add(Centerx);
                yCenter.add(Centery);

                xRight.add(Centerx - (L / 2) * Math.cos(phi + (Math.PI/2)));
                yRight.add(Centery - (L / 2) * Math.sin(phi + (Math.PI/2)));

                xLeft.add(Centerx + (L / 2) * Math.cos(phi + (Math.PI/2)));
                yLeft.add(Centery + (L / 2) * Math.sin(phi + (Math.PI/2)));

            }
        }

    }

}