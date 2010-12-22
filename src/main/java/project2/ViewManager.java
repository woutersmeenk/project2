package project2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project2.model.level.BoxMoveEvent;
import project2.triggers.Trigger;
import project2.triggers.TriggerManager;

import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

public class ViewManager implements EventListener<BoxMoveEvent> {
    private static final Log LOG = LogFactory.getLog(ViewManager.class);
    private AssetManager assetManager;
    private final Node rootNode;

    public ViewManager(final Node root) {
        super();
        rootNode = root;
    }

    public void initialize(AssetManager assetManager) {
        this.assetManager = assetManager;
        /* Create a light. */
        final PointLight pl = new PointLight();
        pl.setPosition(new Vector3f(2, 2, 10));
        pl.setColor(ColorRGBA.White);
        pl.setRadius(30f);
        rootNode.addLight(pl);
    }

    public void createViewFromGameState(final GameState gameState) {
        // add boxes to scene graph
        for (final project2.model.level.Box box : gameState.getLevel()
                .getBoxes().values()) {
            final int size = box.getSize();
            final Box box2 = new Box(new Vector3f(), 0.5f * size, 0.5f * size,
                    0.5f * size);
            final Geometry geom = new Geometry("Box", box2);
            geom.setLocalTranslation(box.getLocation());
            final Material mat2 = new Material(assetManager,
                    "Common/MatDefs/Light/Lighting.j3md");

            mat2.setFloat("m_Shininess", 12);
            mat2.setBoolean("m_UseMaterialColors", true);

            mat2.setColor("m_Specular", ColorRGBA.Gray);

            // give switch a different color
            if (box.getSwitchBox() == null) {
                mat2.setColor("m_Ambient", ColorRGBA.Blue);
                mat2.setColor("m_Diffuse", ColorRGBA.Blue);

            } else {
                mat2.setColor("m_Ambient", ColorRGBA.Red);
                mat2.setColor("m_Diffuse", ColorRGBA.Red);
            }

            geom.setMaterial(mat2);
            geom.setUserData("id", (int) box.getId());

            rootNode.attachChild(geom);
        }

        /* Add player. */
        final int size = gameState.getPlayer().getSize();
        final Box box2 = new Box(new Vector3f(), 0.5f * size, 0.5f * size,
                0.5f * size);
        final Geometry geom = new Geometry("Box", box2);
        geom.setLocalTranslation(gameState.getPlayer().getLocation());

        final Material mat2 = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");

        mat2.setFloat("m_Shininess", 12);
        mat2.setBoolean("m_UseMaterialColors", true);

        mat2.setColor("m_Specular", ColorRGBA.Gray);
        mat2.setColor("m_Ambient", ColorRGBA.Yellow);
        mat2.setColor("m_Diffuse", ColorRGBA.Yellow);
        geom.setMaterial(mat2);
        rootNode.attachChild(geom);

        /* Register a move trigger */
        final project2.model.level.Box player = gameState.getPlayer();
        Trigger trigger = new Trigger(new PlayerMovedCondition(player),
                new ChangePositionResponse(geom, player));
        TriggerManager.getInstance().addTrigger(trigger);

    }

    public Geometry geometryFromId(long id) {
        Integer idObject = (int) id; // make a cast to int, to circumvent an
                                     // error in jme3

        for (Spatial spatial : rootNode.getChildren()) {
            if (idObject.equals(spatial.getUserData("id"))) {
                return (Geometry) spatial;
            }
        }

        return null;
    }

    @Override
    public void onEvent(BoxMoveEvent event) {
        Geometry geom = geometryFromId(event.getId());

        if (geom != null) {
            geom.setLocalTranslation(event.getNewPos());
        } else {
            LOG.error("No matching geometry found for id " + event.getId());
        }
    }
}
