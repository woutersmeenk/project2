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

import project2.model.level.Box;
import project2.triggers.Response;

import com.jme3.scene.Geometry;

public class ChangePositionResponse implements Response {
    private static final Log LOG = LogFactory
            .getLog(ChangePositionResponse.class);
    private final Geometry geometry;
    private final Box player;

    public ChangePositionResponse(final Geometry geometry, final Box player) {
        super();
        this.geometry = geometry;
        this.player = player;
    }

    @Override
    public void execute() {
        geometry.setLocalTranslation(player.getLocation());
        player.setChanged(false);
    }

}
