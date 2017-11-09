/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotools.geotools_javafx.event;

import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Takes Java AWT mouse events received by a map pane and converts them to 
 * {@code MapMouseEvents} which add world location data. The resulting events are then
 * dispatched to {@code MapMouseListeners} by the methods overriden from the 
 * AWT listener interfaces.
 * 
 * @author Michael Bedward
 * @since 8.0
 *
 * @source $URL$
 * @version $Id$
 */
public interface MapMouseEventDispatcher {

    /**
     * Adds a listener for map pane mouse events.
     *
     * @param listener the new listener
     * @return true if successful; false otherwise
     * @throws IllegalArgumentException if the {@code listener} is {@code null}
     */
	public boolean addMouseListener(MapMouseListener listener);

    /**
     * Removes the given listener.
     *
     * @param listener the listener to remove
     * @return true if successful; false otherwise
     * @throws IllegalArgumentException if the {@code listener} is {@code null}
     */
	public boolean removeMouseListener(MapMouseListener listener);

    /**
     * Removes all listeners.
     */
	public void removeAllListeners();
	
	/**
	 * 获取全部的事件资源
	 * @return
	 */
	public List<MapMouseListener> getAllListeners();

    /**
     * Converts an incoming Java AWT mouse event to a {@linkplain MapMouseEvent}.
     * 
     */
	public MapMouseEvent convertEvent(MouseEvent ev);
    
    /**
     * Converts an incoming Java AWT mouse wheel event to a {@linkplain MapMouseEvent}.
     * 
     */
	public MapMouseEvent convertEvent(ScrollEvent ev);
	
	/**
	 * Receives a mouse clicked event and sends a derived
	 * {@linkplain MapMouseEvent} to listeners.
	 * 
	 */
	public EventHandler<MouseEvent> getMouseClicked();

	/**
	 * Receives a mouse pressed event and sends a derived
	 * {@linkplain MapMouseEvent} to listeners.
	 * 
	 */
	public EventHandler<MouseEvent> getMousePressed();

	/**
	 * Receives a mouse released event and sends a derived
	 * {@linkplain MapMouseEvent} to listeners.
	 * 
	 */
	public EventHandler<MouseEvent> getMouseReleased();

	/**
	 * Receives a mouse entered event and sends a derived
	 * {@linkplain MapMouseEvent} to listeners.
	 * 
	 */
	public EventHandler<MouseEvent> getMouseEntered();

	/**
	 * Receives a mouse exited event and sends a derived
	 * {@linkplain MapMouseEvent} to listeners.
	 * 
	 */
	public EventHandler<MouseEvent> getMouseExited();

	/**
	 * Receives a mouse dragged event and sends a derived
	 * {@linkplain MapMouseEvent} to listeners.
	 * 
	 */
	public EventHandler<MouseEvent> getMouseDragged();

	/**
	 * Receives a mouse moved event and sends a derived
	 * {@linkplain MapMouseEvent} to listeners.
	 * 
	 */
	public EventHandler<MouseEvent> getMouseMoved();

	/**
	 * Receives a mouse wheel event and sends a derived
	 * {@linkplain MapMouseEvent} to listeners.
	 * 
	 */
	public EventHandler<ScrollEvent> getMouseWheelMoved();

}
