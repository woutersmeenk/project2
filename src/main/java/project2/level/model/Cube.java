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
package project2.level.model;

import com.jme3.math.Vector3f;

public class Cube {
    private Vector3f location;
    private final int size;
    private final SwitchCube switchCube;
    private final long id;

    Cube(final long id, final Vector3f location2, final int size,
            final SwitchCube switchCube) {
        location = location2;
        this.id = id;
        this.size = size;
        this.switchCube = switchCube;
    }

    public Vector3f getLocation() {
        return location;
    }

    public int getSize() {
        return size;
    }

    public SwitchCube getSwitchCube() {
        return switchCube;
    }

    public void setLocation(final Vector3f location) {
        this.location = location;
    }

    public long getId() {
        return id;
    }
}
