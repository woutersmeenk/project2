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

import project2.model.level.BoxMoveEvent;
import project2.model.level.SwitchBox;
import project2.triggers.Trigger;
import project2.triggers.TriggerManager;

import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

public class ViewManager implements EventListener<BoxMoveEvent> {
    private static final Log LOG = LogFactory.getLog(ViewManager.class);
    private AssetManager assetManager;
    private final Node rootNode;

    /* The geometry of the historical positions. */
    private final List<Geometry> historyGeometry;

    public ViewManager(final Node root) {
        super();
        rootNode = root;

        historyGeometry = new ArrayList<Geometry>();
    }

    public void initialize(final AssetManager assetManager) {
        this.assetManager = assetManager;
        /* Create a light. */
        final PointLight pl = new PointLight();
        pl.setPosition(new Vector3f(2, 2, 10));
        pl.setColor(ColorRGBA.White);
        pl.setRadius(30f);
        rootNode.addLight(pl);
    }

    public void createViewFromGameState(final GameStateManager gameStateManager) {
        // add boxes to scene graph
        for (final project2.model.level.Box box : gameStateManager.getLevel()
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
        final int size = gameStateManager.getPlayer().getSize();
        final Box box2 = new Box(new Vector3f(), 0.5f * size, 0.5f * size,
                0.5f * size);
        final Geometry geom = new Geometry("Box", box2);
        geom.setLocalTranslation(gameStateManager.getPlayer().getLocation());

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
        final project2.model.level.Box player = gameStateManager.getPlayer();
        final Trigger trigger = new Trigger(new PlayerMovedCondition(player),
                new ChangePositionResponse(geom, player));
        TriggerManager.getInstance().addTrigger(trigger);

    }

    public Geometry geometryFromId(final long id) {
        final Integer idObject = (int) id; // make a cast to int, to circumvent
                                           // an
        // error in jme3

        for (final Spatial spatial : rootNode.getChildren()) {
            if (idObject.equals(spatial.getUserData("id"))) {
                return (Geometry) spatial;
            }
        }

        return null;
    }

    public void showHistory(GameStateManager gameStateManager) {
        // remove all
        for (Geometry geom : historyGeometry) {
            rootNode.detachChild(geom);
        }

        final List<SwitchBox> switches = gameStateManager.getLevel()
                .getSwitches();

        for (int i = 0; i < gameStateManager.getHistory().size(); i++) {
            final List<Integer> switchStates = gameStateManager.getHistory()
                    .get(i).getSwitchStates();

            for (int j = 0; j < switchStates.size(); j++) {
                for (project2.model.level.Box box : switches.get(j).getStates()
                        .get(switchStates.get(j))) {
                    // create transparent box

                    final int size = box.getSize();
                    final Box box2 = new Box(new Vector3f(), 0.5f * size,
                            0.5f * size, 0.5f * size);
                    final Geometry geom = new Geometry("Box", box2);
                    geom.setLocalTranslation(box.getLocation());

                    Material mat2 = new Material(assetManager,
                            "Common/MatDefs/Misc/SolidColor.j3md");
                    mat2.setColor("m_Color", new ColorRGBA(0, 0, 1, (i + 1)
                            / (float) gameStateManager.getHistory().size()
                            * 0.40f));

                    mat2.getAdditionalRenderState().setBlendMode(
                            BlendMode.Alpha);

                    geom.setMaterial(mat2);
                    geom.setQueueBucket(Bucket.Transparent);

                    rootNode.attachChild(geom);
                    historyGeometry.add(geom);
                }
            }
        }
    }

    @Override
    public void onEvent(final BoxMoveEvent event) {
        final Geometry geom = geometryFromId(event.getId());

        if (geom != null) {
            geom.setLocalTranslation(event.getNewPos());
        } else {
            LOG.error("No matching geometry found for id " + event.getId());
        }
    }
}
