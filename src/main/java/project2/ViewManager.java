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

import project2.level.model.Checkpoint;
import project2.level.model.Cube;
import project2.level.model.SwitchCube;
import project2.util.IdFactory;

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

public class ViewManager implements EventListener<LocationEvent> {
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

    public Geometry addCube(final long id, final Vector3f pos, final int size,
            final ColorRGBA color) {
        final Box box = new Box(new Vector3f(), 0.5f * size, 0.5f * size,
                0.5f * size);
        final Geometry geom = new Geometry("Cube_" + id, box);
        geom.setLocalTranslation(pos);
        final Material mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");

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

    public void createViewFromGameState(final GameStateManager gameStateManager) {
        // add cubes to scene graph
        for (final Cube cube : gameStateManager.getLevel().getCubes().values()) {
            final ColorRGBA color = cube.getSwitchCube() == null ? ColorRGBA.Blue
                    : ColorRGBA.Red;
            addCube(cube.getId(), cube.getLocation(), cube.getSize(), color);
        }

        /* Add player. */
        final Cube player = gameStateManager.getPlayer();
        addCube(player.getId(), player.getLocation(), player.getSize(),
                ColorRGBA.Yellow);

        /* Add checkpoints */
        for (final Checkpoint cp : gameStateManager.getLevel().getCheckpoints()
                .values()) {
            addTransparentCube(cp.getId(), cp.getLocation(), 1, new ColorRGBA(
                    0, 1, 0, 0.15f));
        }

        /* Add end indicator */
        addTransparentCube(IdFactory.generateID(), gameStateManager.getLevel()
                .getStart(), 1, new ColorRGBA(1, 0, 1, 0.15f));
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

        final List<SwitchCube> switches = gameStateManager.getLevel()
                .getSwitches();

        for (int i = 0; i < gameStateManager.getHistory().size(); i++) {
            final List<Integer> switchStates = gameStateManager.getHistory()
                    .get(i).getSwitchStates();

            for (int j = 0; j < switchStates.size(); j++) {
                for (final Cube cube : switches.get(j).getStates()
                        .get(switchStates.get(j))) {
                    final ColorRGBA color = new ColorRGBA(0, 0, 1, (i + 1)
                            / (float) gameStateManager.getHistory().size()
                            * 0.40f);
                    final Geometry geom = addTransparentCube(cube.getId(),
                            cube.getLocation(), 1, color);

                    historyGeometry.add(geom);
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
