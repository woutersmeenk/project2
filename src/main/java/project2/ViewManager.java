/*  Copyright 2010 Ben Ruijl, Wouter Smeenk

This file is part of project2

project2 is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3, or (at your option)
any later version.

project2 is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with project2; see the file LICENSE.  If not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
02111-1307 USA.

 */
package project2;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project2.level.Level;
import project2.level.model.Checkpoint;
import project2.level.model.Cube;
import project2.level.model.SwitchCube;
import project2.level.model.Text;
import project2.util.IdFactory;
import project2.util.Utils;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.texture.TextureCubeMap;
import com.jme3.util.SkyFactory;

public class ViewManager implements EventListener<LocationEvent> {
    private static final Log LOG = LogFactory.getLog(ViewManager.class);
    private BitmapFont font;
    private AssetManager assetManager;
    private final Node rootNode;

    /* The geometry of the historical positions. */
    private final List<Geometry> historyGeometry;
    private Geometry playerGeometry;

    public ViewManager(final Node root) {
        super();
        rootNode = root;
        playerGeometry = null;
        historyGeometry = new ArrayList<Geometry>();
    }

    public void initialize(final AssetManager assetManager) {
        this.assetManager = assetManager;
        font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        /* Create a light. */
        final PointLight pl = new PointLight();
        pl.setPosition(new Vector3f(2, 2, 10));
        pl.setColor(ColorRGBA.White);
        pl.setRadius(30f);
        rootNode.addLight(pl);
        // TODO make our own sky map
        final Texture skyTexture = assetManager
                .loadTexture("blue-glow-1024.dds");
        final TextureCubeMap skyCubeMap = new TextureCubeMap(
                skyTexture.getImage());
        final Spatial sky = SkyFactory.createSky(assetManager, skyCubeMap,
                false);
        rootNode.attachChild(sky);
    }

    public Geometry addCube(final long id, final Vector3f pos,
            final float size, final ColorRGBA color) {
        return addCube(id, pos, new Vector3f(size, size, size), color);
    }

    public Geometry addTexturedCube(final long id, final Vector3f pos,
            final Vector3f outline, final String texture) {
        final Box box = new Box(outline.x * 0.5f, outline.y * 0.5f,
                outline.z * 0.5f);
        final Geometry geom = new Geometry("Cube_" + id, box);
        geom.setLocalTranslation(pos);
        final Material mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");

        Material mat_brick = new Material(assetManager,
                "Common/MatDefs/Misc/SimpleTextured.j3md");
        mat_brick.setTexture("m_ColorMap",
                assetManager.loadTexture("block.jpg"));
        geom.setMaterial(mat_brick);

        geom.setUserData("id", (int) id);

        rootNode.attachChild(geom);

        return geom;
    }

    public Geometry addCube(final long id, final Vector3f pos,
            final Vector3f outline, final ColorRGBA color) {
        final Box box = new Box(outline.x * 0.5f, outline.y * 0.5f,
                outline.z * 0.5f);
        final Geometry geom = new Geometry("Cube_" + id, box);
        geom.setLocalTranslation(pos);
        final Material mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");

        Material mat_brick = new Material(assetManager,
                "Common/MatDefs/Misc/SimpleTextured.j3md");
        mat_brick.setTexture("m_ColorMap",
                assetManager.loadTexture("block.jpg"));
        geom.setMaterial(mat_brick);

        mat.setFloat("m_Shininess", 12);
        mat.setBoolean("m_UseMaterialColors", true);
        mat.setColor("m_Specular", ColorRGBA.Gray);
        mat.setColor("m_Ambient", color);
        mat.setColor("m_Diffuse", color);

        geom.setMaterial(mat);

        geom.setUserData("id", (int) id);

        rootNode.attachChild(geom);

        return geom;
    }

    public Geometry addTransparentCube(final long id, final Vector3f pos,
            final int size, final ColorRGBA color) {
        final Box box = new Box(new Vector3f(), 0.5f * size, 0.5f * size,
                0.5f * size);
        final Geometry geom = new Geometry("Cube_" + id, box);
        geom.setLocalTranslation(pos);

        final Material mat2 = new Material(assetManager,
                "Common/MatDefs/Misc/SolidColor.j3md");
        mat2.setColor("m_Color", color);
        mat2.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

        geom.setMaterial(mat2);
        geom.setQueueBucket(Bucket.Transparent);
        geom.setUserData("id", (int) id);

        rootNode.attachChild(geom);

        return geom;
    }

    public void drawSwitchPath(List<List<Cube>> states) {
        for (int i = 0; i < states.size() - 1; i++) {
            for (int j = 0; j < states.get(i).size(); j++) {

                addCube(0, states.get(i).get(j).getLocation(), 0.06f,
                        ColorRGBA.Red);
                final Vector3f dir = states.get(i + 1).get(j).getLocation()
                        .subtract(states.get(i).get(j).getLocation());
                final Vector3f center = states.get(i).get(j).getLocation()
                        .add(dir.mult(0.5f));
                final Vector3f up = new Vector3f(0, 0, 1);
                // Cross product with up vector
                final Vector3f perp = dir.cross(up).normalize();

                addCube(0, center, dir.add(perp.mult(0.02f))
                        .add(up.mult(0.02f)), ColorRGBA.Red);

                if (i == states.size() - 2) {
                    addCube(0, states.get(i + 1).get(j).getLocation(), 0.06f,
                            ColorRGBA.Red);
                }
            }
        }
    }

    public void addText(Vector3f pos, Vector3f rotation, String text) {
        BitmapText textGeom = new BitmapText(font, false);
        //textGeom.setBox(new Rectangle(0, 0, 6, 3));
        float[] rotations = rotation.toArray(null);
        for (int i = 0; i < 3; i++) {
            rotations[i] = (float) Math.toRadians(rotations[i]);
        }
        final Quaternion dir = new Quaternion(rotations);
        textGeom.setLocalRotation(dir);
        textGeom.setLocalTranslation(pos);
        textGeom.setCullHint(CullHint.Never);
        textGeom.setQueueBucket(Bucket.Transparent);
        textGeom.setSize(0.5f);
        textGeom.setText(text);
        rootNode.attachChild(textGeom);
    }

    public void addCheckpoint(Checkpoint checkpoint) {
        addTransparentCube(checkpoint.getId(), checkpoint.getLocation(), 1,
                new ColorRGBA(0, 1, 0, 0.15f));
    }

    public void createViewFromGameState(final GameStateManager gameStateManager) {
        /* Add player and register chase camera. */
        final Player player = gameStateManager.getPlayer();
        playerGeometry = addCube(player.getModel().getId(),
                player.getWorldLocation(), player.getModel().getSize(),
                ColorRGBA.Yellow);

        for (final Level level : gameStateManager.getLevelSet()) {
            // add cubes to scene graph
            for (final Cube cube : level.getCubes().values()) {
                ColorRGBA color = ColorRGBA.Blue;

                if (cube.getSwitchCube() != null) {
                    color = ColorRGBA.Red;
                } else {
                    if (cube.isSubjectToSwitching()) {
                        color = new ColorRGBA(0.2f, 0, 0.8f, 1);
                    } else {
                        if (cube.isTeleporter()) {
                            color = new ColorRGBA(0.2f, 0.5f, 0.2f, 1);
                        }
                    }
                }

                addCube(cube.getId(), cube.getLocation(), cube.getSize(), color);
            }

            /* Add checkpoints */
            for (final Checkpoint cp : level.getCheckpoints().values()) {
                addCheckpoint(cp);
            }

            /* Add end indicator */
            addTransparentCube(IdFactory.generateID(), level.getEnd(), 1,
                    new ColorRGBA(1, 0, 1, 0.15f));

            /* Draw switch paths */
            for (SwitchCube switchCube : level.getSwitches()) {
                drawSwitchPath(switchCube.getStates());
            }

            /* add text */
            for (Text text : level.getTexts()) {
                addText(text.getPosition(), text.getRotation(), text.getText());
            }
        }
    }

    public Geometry getPlayerGeometry() {
        return playerGeometry;
    }

    public Geometry geometryFromId(final long id) {
        // make a cast to int, to circumvent an error in jme3
        final Integer idObject = (int) id;

        for (final Spatial spatial : rootNode.getChildren()) {
            if (idObject.equals(spatial.getUserData("id"))) {
                return (Geometry) spatial;
            }
        }

        LOG.info("Could not find ID " + id);
        return null;
    }

    public void deleteById(final long id) {
        final Geometry geom = geometryFromId(id);
        if (geom != null) {
            geom.removeFromParent();
        }
    }

    public void showHistory(final GameStateManager gameStateManager) {
        // remove all
        for (final Geometry geom : historyGeometry) {
            rootNode.detachChild(geom);
        }

        historyGeometry.clear();

        if (gameStateManager.getHistory().size() == 0) {
            return;
        }

        final List<SwitchCube> switches = gameStateManager.getLevel()
                .getSwitches();

        /* For each box, make a path */
        for (int j = 0; j < switches.size(); j++) {
            final List<Integer> states = new ArrayList<Integer>();

            for (int i = 0; i < gameStateManager.getHistory().size(); i++) {
                states.add(gameStateManager.getHistory().get(i)
                        .getSwitchStates().get(j));
            }

            final List<Integer> cleanPath = Utils
                    .removeDuplicatesWithOrder(states);

            for (int i = 0; i < cleanPath.size(); i++) {
                // Add a transparent cube if the state is not the current one
                if (switches.get(j).getCurrentStateID() != cleanPath.get(i)) {
                    for (final Cube cube : switches.get(j).getStates()
                            .get(cleanPath.get(i))) {

                        final ColorRGBA color = new ColorRGBA(0.2f, 0, 0.8f,
                                (i + 1) / (float) cleanPath.size() * 0.40f);
                        final Geometry geom = addTransparentCube(cube.getId(),
                                cube.getLocation(), 1, color);

                        historyGeometry.add(geom);
                    }
                }
            }

        }
    }

    @Override
    public void onEvent(final LocationEvent event) {
        final Geometry geom = geometryFromId(event.getId());

        if (geom != null) {
            geom.setLocalTranslation(event.getNewPos());
        } else {
            LOG.error("No matching geometry found for id " + event.getId());
        }
    }
}
