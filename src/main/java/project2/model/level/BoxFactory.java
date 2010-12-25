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
package project2.model.level;

import com.jme3.math.Vector3f;

public class BoxFactory {
    private static BoxFactory instance;

    public long currentID = 0;

    private BoxFactory() {
    }

    public static BoxFactory getInstance() {
        if (instance == null) {
            instance = new BoxFactory();
        }
        return instance;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public Box createBox(final Vector3f location, final int size) {
        return createBox(location, size, null);
    }

    public Box createBox(final Vector3f location, final int size,
            final SwitchBox switchBox) {
        final long id = currentID;
        currentID++;
        return new Box(id, location, size, switchBox);
    }
}
