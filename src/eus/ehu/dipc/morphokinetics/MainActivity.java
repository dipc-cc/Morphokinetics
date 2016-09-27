package eus.ehu.dipc.morphokinetics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONException;

import basic.AbstractSimulation;
import basic.AgSimulation;
import basic.AgUcSimulation;
import basic.BasicGrowthSimulation;
import basic.GrapheneSimulation;
import basic.Parser;
import basic.SiSimulation;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.BasicAtom;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import kineticMonteCarlo.unitCell.IUc;

import static android.R.color.darker_gray;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ebookfrenzy.StateChange";

    private int count = 0;
    private AbstractSimulation simulation = null;
    private Parser parser;
    Paint mTextPaint;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            parser = new Parser();
        } catch (Exception e){
            System.out.println("ERROR!");
        }
    }

    public void runSimulation(View v) {

        TextView tv = (TextView) findViewById(R.id.outputText);
        tv.setText("Starting simulation " + count);
        System.out.println("hello " + count + " times ");
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
        //EditText editText = (EditText) findViewById(R.id.editTextTemperature);
        //int kk = Integer.parseInt(editText.getText());
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
            case "Si":
                simulation = new SiSimulation(parser);
                break;
            case "basic":
                simulation = new BasicGrowthSimulation(parser);
                break;
            default:
                System.err.println("Error: Default case calculation mode. This simulation mode is not implemented!");
                throw new IllegalArgumentException("This simulation mode is not implemented");
        }


        System.out.println("Init");
        simulation.initialiseKmc();
        System.out.println("Frame");
        simulation.createFrame();
        System.out.println("Simulation");
        simulation.doSimulation();
        System.out.println("Finish");
        simulation.finishSimulation();
        TextView tv0 = (TextView) findViewById(R.id.textViewResults);
        if (tv0 != null) {
            tv0.setText(simulation.printFooter());
        }
        //SurfaceView sv = (SurfaceView) findViewById(R.id.surfaceView);
        //sv.set.
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        Canvas canvas = new Canvas();
        Paint paint = new Paint();
        Bitmap bm = paint((AbstractGrowthLattice) simulation.getKmc().getLattice());
        iv.setImageBitmap(bm);
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
        //Canvas g;

        //super.paint(g);
        int scale = 2;
        int baseX = 0;
        int baseY = 0;
        //g.setColor(GRAY);
        Bitmap g = Bitmap.createBitmap((int) (lattice.getCartSizeX() * scale), (int) (lattice.getCartSizeY() * scale), Bitmap.Config.RGB_565);
        //g.fillRect(baseX, baseY, (int) (lattice.getCartSizeX() * scale), (int) (lattice.getCartSizeY() * scale));

        System.out.println("Created a bitmap of "+(int) (lattice.getCartSizeX() * scale) +"x"+ (int) (lattice.getCartSizeY() * scale));
        for (int i = 0; i < lattice.size(); i++) {
            IUc uc = lattice.getUc(i);
            for (int j = 0; j < uc.size(); j++) {
                AbstractGrowthAtom atom = uc.getAtom(j);
                int Y = (int) Math.round((atom.getPos().getY() + uc.getPos().getY()) * scale) + baseY;
                int X = (int) Math.round((atom.getPos().getX() + uc.getPos().getX()) * scale) + baseX;
                String atomColor = "white";
                switch (atom.getType()) { // the cases are for graphene
                    case AbstractAtom.TERRACE:
                        //g.setColor(WHITE_GRAY);
                        atomColor = "gray";
                        break;
                    case AbstractAtom.CORNER:
                        atomColor = "red";
                        //g.setColor(RED);
                        break;
                    case AbstractAtom.EDGE:
                        atomColor = "magenta";
                        //g.setColor(LILAC);
                        break;
                    case AbstractAtom.ARMCHAIR_EDGE: // == Ag KINK
                        atomColor = "white";
                        //g.setColor(Color.WHITE);
                        break;
                    case AbstractAtom.ZIGZAG_WITH_EXTRA: // == Ag ISLAND
                        atomColor = "cyan";
                        //g.setColor(Color.CYAN);
                        break;
                    case AbstractAtom.SICK:
                        atomColor = "blue";
                        //g.setColor(Color.BLUE);
                        break;
                    case AbstractAtom.KINK:
                        atomColor = "yellow";
                        //g.setColor(BANANA);
                        break;
                    case AbstractAtom.BULK:
                        atomColor = "green";
                        //g.setColor(GREEN);
                        break;
                }

                System.out.println("Trying to print pixel "+X+" "+Y+" color "+atomColor+" type "+atom.getType());
                int x = X;
                int y = Y;
                for (int iterX = 0; iterX < scale; iterX++) {
                    x += iterX;
                    for (int iterY = 0; iterY < scale; iterY++) {
                        y += iterY;
                        System.out.println("printing pixel "+x+" "+y+" color "+atomColor+" type "+atom.getType());
                        if (x < g.getWidth() && y < g.getHeight()) {
                            g.setPixel(x, y, Color.parseColor(atomColor));

                        }
                    }
                }
                /*if (X < g.getWidth() && Y < g.getHeight()) {
                    p = new Paint();
                    p.setColor(Color.parseColor(atomColor));
                    //g.setPixel(X, Y, Color.parseColor(atomColor));
                    g.drawCircle(X, Y, scale, p);

                }*/

                /*if (scale < 3) {
                    if (atom.isOccupied()) {
                        g.fillRect(X, Y, scale, scale);
                    } else if (!atom.isOutside()) {
                        g.drawRect(X, Y, scale, scale);
                    }

                } else if (atom.isOccupied()) {
                    g.fillOval(X, Y, scale, scale);
                    if (scale > 8) {
                        g.drawString(Integer.toString(atom.getId()), X, Y);
                    }
                } else if (!atom.isOutside()) {
                    g.drawOval(X, Y, scale, scale);
                }*/
            }
        }
        //g.dispose();
        //g.
        return g;
    }
}
