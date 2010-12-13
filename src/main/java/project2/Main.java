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

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 * Sample 2 - How to use nodes as handles to manipulate objects in the scene
 * graph. You can rotate, translate, and scale objects by manipulating their
 * parent nodes. The Root Node is special: Only what is attached to the Root
 * Node appears in the scene.
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        // create a blue box at coordinates (1,-1,1)
        Box box1 = new Box(new Vector3f(1, -1, 1), 1, 1, 1);
        Geometry blue = new Geometry("Box", box1);
        Material mat1 = new Material(assetManager,
                "Common/MatDefs/Misc/SolidColor.j3md");
        mat1.setColor("m_Color", ColorRGBA.Blue);
        blue.setMaterial(mat1);

        // create a red box straight above the blue one at (1,3,1)
        Box box2 = new Box(new Vector3f(1, 3, 1), 1, 1, 1);
        Geometry red = new Geometry("Box", box2);
        Material mat2 = new Material(assetManager,
                "Common/MatDefs/Misc/SolidColor.j3md");
        mat2.setColor("m_Color", ColorRGBA.Red);
        red.setMaterial(mat2);

        // create a pivot node at (0,0,0) and attach it to root
        Node pivot = new Node("pivot");
        rootNode.attachChild(pivot);

        // attach the two boxes to the *pivot* node!
        pivot.attachChild(blue);
        pivot.attachChild(red);
        // rotate pivot node: Both boxes have rotated!
        pivot.rotate(0.4f, 0.4f, 0.0f);

    }
}
