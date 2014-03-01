/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphic_interfaces.GA_convergence;

import java.awt.Color;
import java.awt.*;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author nesferjo
 */
public class Error_panel extends JPanel {

    private ArrayList<Float> Xs;
    private ArrayList<Float> Ys;
    private float maxX = 0;
    private float maxY = -9999999999f;
    private float minY = 9999999999f;
    private Color c;

    public Error_panel(Color c) {

        maxX = (float) Math.log10(maxX);
        Xs = new ArrayList();
        Ys = new ArrayList();
        this.c = c;
    }

    public void addPoint(float Xs, float Ys) {
        
        float Xslog = Xs;//(float) Math.log10(Xs);
        float Yslog = (float) Math.log10(Ys);

        if (Yslog > maxY) {
            maxY = Yslog;
        }
        if (Yslog < minY) {
            minY = Yslog;
        }
        if (Xslog > maxX) {
            maxX = Xslog;
        }
        this.Xs.add(Xslog);
        this.Ys.add(Yslog);
        this.repaint();
    }

    public float getLogYSize() {
        return Ys.size();
    }

    public float getLogY(int pos) {
        return Ys.get(pos);
    }

    public void clear() {
        Xs.clear();
        Ys.clear();
        maxX = 0;
        maxY = -9999999999f;
        minY = 9999999999f;
    }

    @Override
    public void paint(Graphics g) {


        Graphics2D G2d = (Graphics2D) g;
        G2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
                RenderingHints.VALUE_ANTIALIAS_ON);
        G2d.setColor(Color.white);
        G2d.setPaint(new GradientPaint(0, 0, Color.white, 0, this.getHeight(), new Color(240, 240, 240)));
        G2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        if (Xs.size() < 2) {
            return;
        }
        G2d.setColor(c);
        float width = this.getWidth() - 40;
        float height = this.getHeight() - 40;
        float scaleX = width / maxX;
        float scaleY = height / (maxY - minY);

        for (int i = 1; i < Xs.size(); i++) {
            G2d.drawLine(10 + (int) (Xs.get(i - 1) * scaleX),
                    this.getHeight() - (20 + (int) ((Ys.get(i - 1) - minY) * scaleY)),
                    10 + (int) (Xs.get(i) * scaleX),
                    this.getHeight() - (20 + (int) ((Ys.get(i) - minY) * scaleY)));

        }

        G2d.fillOval(8 + (int) (Xs.get(Xs.size() - 1) * scaleX),
                this.getHeight() - (20 + (int) ((Ys.get(Xs.size() - 1) - minY) * scaleY)) - 2, 4, 4);

        if (Xs.size() > 0) {
            g.setColor(Color.black);
            g.drawString(Float.toString((float)Math.pow(10,Ys.get(Xs.size() - 1))),
                    -29 + (int) (Xs.get(Xs.size() - 1) * scaleX), -4
                    + this.getHeight() - (20 + (int) (+(Ys.get(Xs.size() - 1) - minY) * scaleY)));
            G2d.setColor(c);
            g.drawString(Float.toString((float)Math.pow(10,Ys.get(Xs.size() - 1))),
                    -30 + (int) (Xs.get(Xs.size() - 1) * scaleX), -5
                    + this.getHeight() - (20 + (int) (+(Ys.get(Xs.size() - 1) - minY) * scaleY)));
        }

        G2d.setColor(Color.black);
        G2d.drawString(Float.toString((float)Math.pow(10, maxY)), 5, 15);
    }
}
