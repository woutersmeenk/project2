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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A entity in the game
 * 
 * @author Wouter Smeenk
 * 
 */
public final class Entity {
    private static final Log LOG = LogFactory.getLog(Entity.class);
    private final Map<Class<? extends AbstractBehavior>, AbstractBehavior> behaviors;
    private final Map<Attribute, Object> attributes;
    private final long id;
    private final Family family;
    private boolean markedRemoved;
    private final EntityManager entityManager;

    /**
     * Creates a new entity.
     * 
     * @param entityManager
     *            Manager that created this entity
     * @param family
     *            Family of this entity
     * @param id
     *            Id of this entity
     */
    public Entity(final EntityManager entityManager, final Family family,
            final long id) {
        behaviors = new HashMap<Class<? extends AbstractBehavior>, AbstractBehavior>();
        attributes = new HashMap<Attribute, Object>();
        this.id = id;
        this.family = family;
        markedRemoved = false;
        this.entityManager = entityManager;
    }

    /**
     * Get the id of the entity.
     * 
     * @return Id of the entity
     */
    public long getId() {
        return id;
    }

    /**
     * Get the family.
     * 
     * @return Family
     */
    public Family getFamily() {
        return family;
    }

    /**
     * Adds a behavior to this class.
     * 
     * @param behavior
     *            The behavior instance to be added
     */
    public void addBehavior(final AbstractBehavior behavior) {
        final Class<? extends AbstractBehavior> clazz = behavior.getClass();
        if (behaviors.containsKey(clazz)) {
            throw new IllegalArgumentException("Entity [" + toString()
                    + "] already contains Component of class: "
                    + behavior.getClass().getName());
        }
        behaviors.put(clazz, behavior);
    }

    /**
     * Retrieves a behavior of this entity based on the class of the behavior.
     * 
     * @param <T>
     *            The type of the Behavior that has to be fetched
     * @param clazz
     *            Class of the Behavior that has to be fetched
     * @return The requested behavior or null if this entity doesn't have a
     *         behavior of this class
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractBehavior> T getBehavior(final Class<T> clazz) {
        if (!behaviors.containsKey(clazz)) {
            throw new IllegalArgumentException("Object " + id + "@"
                    + hashCode() + " does not have behaviour of class "
                    + clazz.getName());
        }

        final T beh = (T) behaviors.get(clazz);
        return beh;
    }

    /**
     * Checks if entity has a certain attribute. Useful in situations where you
     * want to filter some entities.
     * 
     * @param attribute
     *            The attribute to check
     * @return True if entity has a non-null attribute, else false
     */
    public boolean hasAttribute(final Attribute attribute) {
        return attributes.containsKey(attribute)
                && attributes.get(attribute) != null;
    }

    /**
     * Gets the object bound to the attribute.
     * 
     * @param attribute
     *            The attribute to get
     * @return Returns the object bound to this attribute
     */
    public Object getAttribute(final Attribute attribute) {
        if (!attributes.containsKey(attribute)) {
            LOG.warn("Object " + id + "@" + hashCode()
                    + " does not have attribute " + attribute.name());
        }

        return attributes.get(attribute);
    }

    /**
     * Checks if this entity has a behavior of a given class.
     * 
     * @param behaviorClass
     *            The class of the behavior.
     * @return True if entity has behavior, else false
     */
    public boolean hasBehavior(
            final Class<? extends AbstractBehavior> behaviorClass) {
        return behaviors.containsKey(behaviorClass)
                && behaviors.get(behaviorClass) != null;
    }

    /**
     * Binds the object to the attribute and returns the old object.
     * 
     * @param attribute
     *            The attribute to bind
     * @param newObject
     *            Object to bind
     * @param <T>
     *            The class of the attribute
     * @return Returns the object bound to the attribute before
     */
    @SuppressWarnings("unchecked")
    public <T> T setAttribute(final Attribute attribute, final T newObject) {
        if (attribute.getClazz().isInstance(newObject) || newObject == null) {
            final T result = (T) attributes.put(attribute, newObject);

            sendMessage(MessageType.ATTRIBUTE_SET, attribute);
            return result;
        } else {
            throw new IllegalArgumentException("Object should be of class: "
                    + attribute.getClazz().getName());
        }
    }

    /**
     * Retrieves the values of all the requested attributes
     * 
     * @param requestedAttributes
     *            the requested attributes
     * @return the values of all the requested attributes
     */
    public Map<Attribute, Object> getAttributes(
            final Set<Attribute> requestedAttributes) {

        final Map<Attribute, Object> temp = new HashMap<Attribute, Object>();
        for (final Attribute attribute : requestedAttributes) {
            temp.put(attribute, attributes.get(attribute));
        }
        return temp;
    }

    /**
     * Removes a behavior from this entity.
     * 
     * @param clazz
     *            Class of the behavior to be removed
     * @return The behavior that was removed
     */
    public AbstractBehavior removeBehavior(
            final Class<? extends AbstractBehavior> clazz) {
        final AbstractBehavior behavior = behaviors.remove(clazz);
        return behavior;
    }

    /**
     * Calls onMessage on all the behaviors of this entity.
     * 
     * @param messageType
     *            The type of message
     * @param data
     *            the data to be send
     */
    public void sendMessage(final MessageType messageType, final Object data) {
        final List<AbstractBehavior> behaviorList = new ArrayList<AbstractBehavior>(
                behaviors.values());

        for (int i = 0; i < behaviorList.size(); i++) {
            behaviorList.get(i).onMessage(messageType, data);
        }
    }

    /**
     * Calls onUpdate on all the behaviors of this entity.
     * 
     * @param delta
     *            time that has passed in seconds
     */
    public void sendUpdate(final double delta) {
        final List<AbstractBehavior> behaviorList = new ArrayList<AbstractBehavior>(
                behaviors.values());

        for (int i = 0; i < behaviorList.size(); i++) {
            behaviorList.get(i).onUpdate(delta);
        }
    }

    /**
     * Mark this entity for removal.
     */
    public void remove() {
        markedRemoved = true;
    }

    /**
     * Set the removed flag to false
     */
    public void resetMarkedRemoved() {
        markedRemoved = false;
    }

    /**
     * Returns if the removed flag is set
     * 
     * @return true if the removed flag is set
     */
    public boolean isMarkedRemoved() {
        return markedRemoved;
    }

    /**
     * The entity manager that manages this entity
     * 
     * @return The entity manager that manages this entity
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
