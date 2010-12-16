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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project2.level.XMLLevelLoader;
import project2.model.level.Level;
import project2.util.JavaLoggingToCommonLoggingRedirector;
import triggers.Condition;
import triggers.ConditionAnd;
import triggers.TriggerManager;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.util.TangentBinormalGenerator;

public class Main extends SimpleApplication {
    private static final Log LOG = LogFactory.getLog(Main.class);

    TriggerManager triggerManager;

    public static void main(final String[] args) {
	JavaLoggingToCommonLoggingRedirector.activate();
	final Main app = new Main();
	app.start();
    }

    public Main() {
	triggerManager = new TriggerManager();
    }

    @Override
    public void update() {
	// update game logic
	triggerManager.update();

	super.update();
    }

    @Override
    public void simpleInitApp() {
	// create a custom condition
	Condition condition = new ConditionAnd(new Condition() {

	    @Override
	    public boolean isTrue() {
		return getCamera().getDirection().x > 0;
	    }
	}, new Condition() {

	    @Override
	    public boolean isTrue() {
		return getCamera().getDirection().y > 0;
	    }
	});

	// triggerManager.addTrigger(new Trigger(condition, new DebugResponse(
	// "You are looking the the positive x and y direction!")));

	XMLLevelLoader loader = new XMLLevelLoader();
	final Level level = loader.loadLevel(ClassLoader
		.getSystemResource("simple.xml"));

	// add boxes to scene graph
	for (project2.model.level.Box box : level.getBoxes()) {
	    int size = box.getSize();
	    final Box box2 = new Box(box.getLocation(), 0.5f * size,
		    0.5f * size, 0.5f * size);
	    final Geometry geom = new Geometry("Box", box2);
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

	    rootNode.attachChild(geom);
	}

	// add a light
	PointLight pl = new PointLight();
	pl.setPosition(new Vector3f(2, 2, 10));
	pl.setColor(ColorRGBA.White);
	pl.setRadius(30f);
	rootNode.addLight(pl);
    }
}
