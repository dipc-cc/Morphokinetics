/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import basic.AbstractSimulation;
import basic.AgSimulation;
import basic.AgUcSimulation;
import basic.BasicGrowthSimulation;
import basic.CatalysisSimulation;
import basic.GrapheneSimulation;
import basic.Parser;
import eus.ehu.dipc.morphokinetics.R;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;

public class MainActivity extends AppCompatActivity {

  private int count = 0;
  private AbstractSimulation simulation = null;
  private Parser parser;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    try {
      parser = new Parser();
    } catch (Exception e) {
      System.out.println("ERROR!");
    }

    try {
      ImageView iv = (ImageView) findViewById(R.id.imageView);
      Bitmap bm = paint((AbstractGrowthLattice) simulation.getKmc().getLattice());
      iv.setImageBitmap(bm);
    } catch (NullPointerException e){

    }
  }

  public void runSimulation(View v) {
    Properties p = System.getProperties();
    Enumeration keys = p.keys();
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      String value = (String) p.get(key);
      System.out.println(key + " >>>> " + value);
    }

    Context context;
    Configurator configurator;
    if (System.getProperty("java.vm.name").equals("Dalvik")) {
      context = this;
      Configurator.getConfigurator().setContext(context);
    }
    TextView tv = (TextView) findViewById(R.id.outputText);
    tv.setText("Starting simulation " + count);
    count++;


    if (((RadioButton) findViewById(R.id.radioButtonBasic)).isChecked()) {
      parser.setCalculationMode("basic");
    }
    if (((RadioButton) findViewById(R.id.radioButtonAg)).isChecked()) {
      parser.setCalculationMode("Ag");
    }
    if (((RadioButton) findViewById(R.id.radioButtonGraphene)).isChecked()) {
      parser.setCalculationMode("graphene");
    }
    if (((RadioButton) findViewById(R.id.radioButtonCatalysis)).isChecked()) {
      parser.setCalculationMode("catalysis");
    }

    parser.setTemperature(Integer.parseInt(String.valueOf(((EditText) findViewById(R.id.editTextTemperature)).getText())));
    parser.setCartSizeX(Integer.parseInt(String.valueOf(((EditText) findViewById(R.id.editTextSize)).getText())));
    parser.setCartSizeY(Integer.parseInt(String.valueOf(((EditText) findViewById(R.id.editTextSize)).getText())));
    parser.setNumberOfSimulations(Integer.parseInt(String.valueOf(((EditText) findViewById(R.id.editTextRepetitions)).getText())));
    try {
      parser.print();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    switch (parser.getCalculationMode()) {
      case "Ag":
        simulation = new AgSimulation(parser);
        break;
      case "AgUc":
        simulation = new AgUcSimulation(parser);
        break;
      case "graphene":
        simulation = new GrapheneSimulation(parser);
        break;
      case "basic":
        simulation = new BasicGrowthSimulation(parser);
        break;
      case "catalysis":
        simulation = new CatalysisSimulation(parser);
        break;
      default:
        System.err.println("Error: Default case calculation mode. This simulation mode is not implemented!");
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }

    final PaintLoop paintLoop = new PaintLoop();
    paintLoop.start();

    new Thread(new Runnable() {
      @Override
      public void run() {
        simulation.initialiseKmc();
        simulation.createFrame();
        simulation.doSimulation();
        simulation.finishSimulation();

        final TextView tv0 = (TextView) findViewById(R.id.textViewResults);
        tv0.post(new Runnable() {
          @Override
          public void run() {
            tv0.setText(simulation.printFooter());
          }
        });
        try {
          paintLoop.finish();
          paintLoop.join();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();


  }

  private class PaintLoop extends Thread {

    private boolean running = true;

    @Override
    public void run() {
      while (running) {
        try {
          final ImageView iv = (ImageView) findViewById(R.id.imageView);
          final Bitmap bm = paint((AbstractGrowthLattice) simulation.getKmc().getLattice());
          iv.post(new Runnable() {
            @Override
            public void run() {
              iv.setImageBitmap(bm);
            }
          });
        } catch (NullPointerException e) {
        }
      }
      try {
        PaintLoop.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    public void finish() {
      running = false;
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public Bitmap paint(AbstractGrowthLattice lattice) { //real drawing method

    Paint p;

    int scale = 2;
    int baseX = 0;
    int baseY = 0;
    Bitmap g = Bitmap.createBitmap((int) (lattice.getCartSizeX() * scale), (int) (lattice.getCartSizeY() * scale), Bitmap.Config.RGB_565);

    for (int i = 0; i < lattice.size(); i++) {
      AbstractGrowthUc uc = lattice.getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthAtom atom = uc.getAtom(j);
        int Y = (int) Math.round((atom.getPos().getY() + uc.getPos().getY()) * scale) + baseY;
        int X = (int) Math.round((atom.getPos().getX() + uc.getPos().getX()) * scale) + baseX;
        String atomColor = "white";
        switch (atom.getType()) { // the cases are for graphene
          case AbstractAtom.TERRACE:
            atomColor = "gray";
            break;
          case AbstractAtom.CORNER:
            atomColor = "red";
            break;
          case AbstractAtom.EDGE:
            atomColor = "magenta";
            break;
          case AbstractAtom.ARMCHAIR_EDGE: // == Ag KINK
            atomColor = "white";
            break;
          case AbstractAtom.ZIGZAG_WITH_EXTRA: // == Ag ISLAND
            atomColor = "cyan";
            break;
          case AbstractAtom.SICK:
            atomColor = "blue";
            break;
          case AbstractAtom.KINK:
            atomColor = "yellow";
            break;
          case AbstractAtom.BULK:
            atomColor = "green";
            break;
        }
        if (atom.isOutside())
          atomColor = "white";

        int x = X;
        int y = Y;
        for (int iterX = 0; iterX < scale; iterX++) {
          x += iterX;
          for (int iterY = 0; iterY < scale; iterY++) {
            y += iterY;
            if (x < g.getWidth() && y < g.getHeight()) {
              g.setPixel(x, y, Color.parseColor(atomColor));
            }
          }
        }
      }
    }
    return g;
  }
}
