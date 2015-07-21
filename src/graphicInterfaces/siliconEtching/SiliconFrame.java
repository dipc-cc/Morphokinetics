/*
 * Panel1.java
 *
 * Created on 5 de octubre de 2008, 14:22
 */
package graphicInterfaces.siliconEtching;

import com.sun.j3d.utils.universe.SimpleUniverse;
import graphicInterfaces.KmcGraphics;
import graphicInterfaces.siliconEtching.mouseBehaviors.MouseZoom;
import graphicInterfaces.siliconEtching.mouseBehaviors.MouseTranslate;
import graphicInterfaces.siliconEtching.mouseBehaviors.MouseRotate;
import graphicInterfaces.siliconEtching.mouseBehaviors.MouseRocking;
import utils.list.AbstractList;
import kineticMonteCarlo.atom.SiAtom;
import kineticMonteCarlo.kmcCore.AbstractKmc;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Material;
import javax.media.j3d.PointAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;


/**
 *
 * @author Administrador
 */
public class SiliconFrame extends javax.swing.JFrame implements KmcGraphics {

  private SiliconPointArray sil;

  public SiliconFrame() {
    super();
    this.setVisible(true);
  }

  @Override
  public void drawKmc(AbstractKmc kmc) {

    if (sil == null) {
      sil = new SiliconPointArray(new float[]{});
      float side = this.update(kmc);
      createSiliconPanel(side);
    } else {
      this.update(kmc);
    }
  }

  private BranchGroup createSceneGraph(float sideWidth) {

    // Create the root of the branch graph
    BranchGroup objRoot = new BranchGroup();
    objRoot.setCapability(objRoot.ALLOW_DETACH);

    TransformGroup objRotate = null;
    MouseRotate myMouseRotate = null;
    MouseRocking myMouseRocking = null;
    MouseTranslate myMouseTranslate = null;
    MouseZoom wheelZoom = new MouseZoom();

    Transform3D transform = new Transform3D();
    transform.rotX(Math.PI);
    // create ColorCube and MouseRotate behvaior objects
    objRotate = new TransformGroup(transform);
    objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

    objRoot.addChild(objRotate);

    Shape3D shape = new Shape3D(sil, createMaterialAppearance());

    Transform3D scale = new Transform3D();

    scale.setScale(1 / sideWidth);

    TransformGroup SC = new TransformGroup(scale);
    SC.addChild(shape);
    objRotate.addChild(SC);

    Background background = new Background();
    background.setColor(new Color3f(0.2f, 0.2f, 0.2f));
    background.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));

    objRoot.addChild(background);

    myMouseRotate = new MouseRotate();
    myMouseRotate.setTransformGroup(objRotate);
    myMouseRotate.setSchedulingBounds(new BoundingSphere());
    objRoot.addChild(myMouseRotate);

    myMouseRocking = new MouseRocking();
    myMouseRocking.setTransformGroup(objRotate);
    myMouseRocking.setSchedulingBounds(new BoundingSphere());
    objRoot.addChild(myMouseRocking);

    myMouseTranslate = new MouseTranslate(100);
    myMouseTranslate.setTransformGroup(objRotate);
    myMouseTranslate.setSchedulingBounds(new BoundingSphere());
    objRoot.addChild(myMouseTranslate);

    wheelZoom.setTransformGroup(objRotate);
    wheelZoom.setSchedulingBounds(new BoundingSphere());
    objRoot.addChild(wheelZoom);
    wheelZoom.setFactor(10 / sideWidth);

    DirectionalLight lightD = new DirectionalLight();
    lightD.setDirection(new Vector3f(0.0f, -0.0f, -7f));

    lightD.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 10000000));
    objRoot.addChild(lightD);

    Color3f ambientColour = new Color3f(0.0f, 0.0f, 0.0f);
    AmbientLight ambientLight = new AmbientLight(ambientColour);
    ambientLight.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 10000000));
    objRoot.addChild(ambientLight);
    SC.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
    objRoot.compile();
    return objRoot;

  }

  private void createSiliconPanel(float side) {

    initComponents();

    GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

    Canvas3DOverlay canvas3D = new Canvas3DOverlay(config);
    add("Center", canvas3D);

    // SimpleUniverse is a Convenience Utility class
    SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

        // This will move the ViewPlatform back a bit so the
    // objects in the scene can be viewed.
    simpleU.getViewingPlatform().setNominalViewingTransform();
    javax.media.j3d.View view = simpleU.getViewer().getView();
    view.setBackClipDistance(50000);
    view.setFrontClipDistance(0.0001);
    jScrollPane1.setViewportView(canvas3D);

    simpleU.addBranchGraph(createSceneGraph(side));

  }

  private Appearance createMaterialAppearance() {

    Appearance materialAppear = new Appearance();
    Material material = new Material();
    material.setAmbientColor(new Color3f(1.0f, 1.0f, 1.0f));
    materialAppear.setPointAttributes(new PointAttributes(1, true));
    return materialAppear;
  }

  private float update(AbstractKmc kmc) {

    AbstractList surface = kmc.getSurfaceList();
    float sizeX = 0;
    float sizeY = 0;

    float[] surfacePoints = new float[surface.getSize() * 3];

    float max_Z = 0;
    for (int i = 0; i < surface.getSize(); i++) {
      SiAtom atom = (SiAtom) surface.getAtomAt(i);
      surfacePoints[i * 3] = atom.getX();
      sizeX = Math.max(atom.getX(), sizeX);
      surfacePoints[i * 3 + 1] = atom.getY();
      sizeY = Math.max(atom.getY(), sizeY);
      max_Z = Math.max(max_Z, atom.getZ());
      surfacePoints[i * 3 + 2] = -atom.getZ();
    }

    for (int i = 0; i < surface.getSize(); i++) {
      surfacePoints[i * 3] -= sizeX * 0.5f;
      surfacePoints[i * 3 + 1] -= sizeY * 0.5f;
      surfacePoints[i * 3 + 2] += max_Z;
    }

    sil.actualizadata(surfacePoints);

    return Math.max(sizeX, sizeY);

  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Silicon KMC surface viewer");
    setResizable(false);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 834, Short.MAX_VALUE)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
    /**
   * @param args the command line arguments
   */
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane jScrollPane1;
  // End of variables declaration//GEN-END:variables
}
