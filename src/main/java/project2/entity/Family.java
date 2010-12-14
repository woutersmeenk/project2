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
package project2.entity;

/**
 * Family of the entities. Contains a tree-like structure to recursively build
 * an entity.
 * 
 * @author Ben Ruijl
 * 
 */
public enum Family {
    /**
     * Root of all the families
     */
    ROOT(null);

    private final Family parent;

    /**
     * Default constructor
     * 
     * @param parent
     *            The parent
     */
    private Family(final Family parent) {
        this.parent = parent;
    }

    /**
     * The parent
     * 
     * @return The parent
     */
    public Family getParent() {
        return parent;
    }
}
