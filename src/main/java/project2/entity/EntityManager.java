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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Manages the entities. Sends messages to all the listeners when a entity is
 * created or removed. Manages the removed flag in entities.
 * 
 * @author Wouter Smeenk
 * 
 */
public class EntityManager {
    private static final Log LOG = LogFactory.getLog(EntityManager.class);
    private final Map<Long, Entity> entities;
    private final EntityFactory factory;
    private final List<EntityListener> listeners;

    /**
     * Default constructor
     * 
     * @param factory
     *            The factory that will create the entities
     */
    public EntityManager(final EntityFactory factory) {
        entities = new ConcurrentHashMap<Long, Entity>();
        this.factory = factory;
        listeners = new ArrayList<EntityListener>();
    }

    /**
     * Add a listener
     * 
     * @param listener
     *            The listener
     */
    public void addListener(final EntityListener listener) {
        listeners.add(listener);
    }

    /**
     * Fires the removed event on all the listeners
     * 
     * @param entity
     *            The entity that was removed
     */
    private void fireOnEntityRemoved(final Entity entity) {
        for (final EntityListener listener : listeners) {
            listener.onEntityRemoved(entity);
        }
    }

    /**
     * Fires the created event on all the listeners
     * 
     * @param entity
     *            The entity that was created
     */
    private void fireOnEntityCreated(final Entity entity) {
        for (final EntityListener listener : listeners) {
            listener.onEntityCreated(entity);
        }
    }

    /**
     * Creates a new Entity and adds it to the entities list.
     * 
     * @param family
     *            Family of the entity
     * @return The created entity or null on failure
     */
    public Entity create(final Family family) {
        final Entity entity = factory.create(this, family);
        add(entity);
        fireOnEntityCreated(entity);
        return entity;
    }

    /**
     * Adds an entity to the list.
     * 
     * @param entity
     *            Entity to add
     */
    public void add(final Entity entity) {
        /*
         * If entity already exists, check if the removed flag is set. If so,
         * unset that.
         */
        if (entities.containsKey(entity.getId())) {
            if (entity.isMarkedRemoved()) {
                entity.resetMarkedRemoved();
            } else {
                LOG.warn("Trying to add entity " + entity.getId()
                        + " , but entity already exists.");
            }

            return;
        }

        entities.put(entity.getId(), entity);
    }

    /**
     * Add all the entities
     * 
     * @param entitiesList
     *            The entities to add
     */
    public void add(final Collection<Entity> entitiesList) {
        for (final Entity en : entitiesList) {
            add(en);
        }
    }

    /**
     * Removes the entity from the list, resets the markedRemoved flag and
     * resets the entity changed attributes list. This is required, because the
     * entity still exists and can be added later.
     * 
     * @param id
     *            Entity id
     * @return Removed entity
     */
    public Entity remove(final long id) {
        final Entity entity = entities.get(id);

        if (entity == null) {
            LOG.warn("Entity with id " + id + " is already removed.");
            return null;
        }

        entities.remove(id);

        entity.resetMarkedRemoved();
        fireOnEntityRemoved(entity);
        return entity;
    }

    /**
     * Retrieves a entity
     * 
     * @param id
     *            The entity id
     * @return The entity or null if it does not exist
     */
    public Entity get(final long id) {
        return entities.get(id);

    }

    /**
     * Returns the entity list. Do not edit this list directly, but use the add
     * and remove functions.
     * 
     * @return Entity list
     */
    public Map<Long, Entity> getEntities() {
        return Collections.unmodifiableMap(entities);
    }

    /**
     * Updates all the entities
     * 
     * @param delta
     *            The time since last update in seconds
     */
    public void update(final double delta) {
        /* Clean up entities which are flagged for removal */
        final List<Entity> removeList = new ArrayList<Entity>();
        for (final Entity entity : entities.values()) {
            if (entity.isMarkedRemoved()) {
                removeList.add(entity);
            }
        }

        for (int i = 0; i < removeList.size(); i++) {
            remove(removeList.get(i).getId());
        }

        for (final Entity entity : entities.values()) {
            entity.sendUpdate(delta);
        }
    }

}
