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

import project2.util.IdFactory;

import com.jme3.math.Vector3f;

public final class CubeFactory {
    private static CubeFactory instance;

    private CubeFactory() {
    }

    public static CubeFactory getInstance() {
        if (instance == null) {
            instance = new CubeFactory();
        }
        return instance;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public Cube createCube(final Vector3f location, final int size,
            final boolean subjectToSwitching) {
        return createCube(location, size, null, null, subjectToSwitching, null);
    }

    public Cube createCube(final Vector3f location, final int size,
            final SwitchCube switchCube, final Vector3f teleportDestination,
            final boolean subjectToSwitching, final String name) {
        return new Cube(IdFactory.generateID(), location, size, switchCube,
                teleportDestination, subjectToSwitching, name);

    }
}
