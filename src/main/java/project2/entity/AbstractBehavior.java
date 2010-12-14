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
 * Base behavior class. Subclasses define specific behavior of its owner.
 * 
 * @author Wouter Smeenk
 * 
 */
public abstract class AbstractBehavior {
    private final Entity owner;

    /**
     * Default constructor
     * 
     * @param owner
     *            The entity that this behavior belongs to
     */
    public AbstractBehavior(final Entity owner) {
        this.owner = owner;
    }

    /**
     * Returns the current owner
     * 
     * @return The owner
     */
    public Entity getOwner() {
        return owner;
    }

    /**
     * Gets the object bound to the attribute. The attribute is owned by the
     * owner of this behavior.
     * 
     * @param attribute
     *            The attribute to get
     * @return Returns the object bound to this attribute
     */
    protected Object getAttribute(final Attribute attribute) {
        return getOwner().getAttribute(attribute);
    }

    /**
     * Binds the object to the attribute and returns the old object. The
     * attribute is owned by the owner of this behavior.
     * 
     * @param attribute
     *            The attribute to bind
     * @param newObject
     *            Object to bind
     * @param <T>
     *            The type of the newObject to set. Should be the same as the
     *            class defined in attribute
     * @return Returns the object bound to the attribute before
     */
    protected <T> T setAttribute(final Attribute attribute, final T newObject) {
        return getOwner().setAttribute(attribute, newObject);
    }

    /**
     * Sends a message to this behavior. Subclasses should react to certain
     * messages.
     * 
     * @param messageType
     *            Enum that specifies the message type
     * @param data
     *            Message specific data
     */
    public abstract void onMessage(MessageType messageType, Object data);

    /**
     * Called when the engine request an update in the status.
     * 
     * @param delta
     *            Amount of time elapsed since last update in seconds
     */
    public abstract void onUpdate(double delta);

    /**
     * Retrieves the current entity manager from this owner
     * 
     * @return the entity manager
     */
    protected EntityManager getEntityManager() {
        return owner.getEntityManager();
    }
}
