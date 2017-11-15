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

import java.util.ArrayList;
import java.util.List;

import org.geotools.geotools_javafx.MapListener;
import org.geotools.geotools_javafx.MapPane;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Receives mouse events from a MapPane instance, converts them to
 * MapPaneMouseEvents, and sends these to the active map pane tools.
 * 
 * @author Michael Bedward
 * @since 8.0
 *
 * @source $URL$
 * @version $Id$
 */
public class DefaultMapMouseEventDispatcher implements MapMouseEventDispatcher, MapListener {

	/**
	 * 当前的地图操作上下文
	 */
	private MapPane mapPane;

	/**
	 * 注册进来的事件列表
	 */
	private List<MapMouseListener> listeners;

	/**
	 * Creates a new manager instance to work with the specified map pane.
	 * 
	 * @param mapPane
	 *            the map pane
	 */
	public DefaultMapMouseEventDispatcher(MapPane mapPane) {
		this.mapPane = mapPane;
		this.listeners = new ArrayList<MapMouseListener>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addMouseListener(MapMouseListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("listener must not be null");
		}

		if (!listeners.contains(listener)) {
			return listeners.add(listener);
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeMouseListener(MapMouseListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("listener must not be null");
		}

		return listeners.remove(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAllListeners() {
		listeners.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MapMouseListener> getAllListeners() {
		return new ArrayList<>(this.listeners);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventHandler<MouseEvent> getMouseClicked() {

		return (MouseEvent event) -> {
			MapMouseEvent mapEv = convertEvent(event);
			if (mapEv != null) {
				for (MapMouseListener listener : listeners) {
					listener.onMouseClicked(mapEv);
				}
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventHandler<MouseEvent> getMousePressed() {

		return (MouseEvent event) -> {
			MapMouseEvent mapEv = convertEvent(event);
			if (mapEv != null) {
				for (MapMouseListener listener : listeners) {
					listener.onMousePressed(mapEv);
				}
			}
		};
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param ev
	 */
	@Override
	public EventHandler<MouseEvent> getMouseReleased() {

		return (MouseEvent event) -> {
			MapMouseEvent mapEv = convertEvent(event);
			if (mapEv != null) {
				for (MapMouseListener listener : listeners) {
					listener.onMouseReleased(mapEv);
				}
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventHandler<MouseEvent> getMouseEntered() {

		return (MouseEvent event) -> {
			MapMouseEvent mapEv = convertEvent(event);
			if (mapEv != null) {
				for (MapMouseListener listener : listeners) {
					listener.onMouseEntered(mapEv);
				}
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventHandler<MouseEvent> getMouseExited() {

		return (MouseEvent event) -> {
			MapMouseEvent mapEv = convertEvent(event);
			if (mapEv != null) {
				for (MapMouseListener listener : listeners) {
					listener.onMouseExited(mapEv);
				}
			}
		};
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param ev
	 * @return
	 */
	@Override
	public EventHandler<MouseEvent> getMouseDragged() {

		return (MouseEvent event) -> {
			MapMouseEvent mapEv = convertEvent(event);
			if (mapEv != null) {
				for (MapMouseListener listener : listeners) {
					listener.onMouseDragged(mapEv);
				}
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventHandler<MouseEvent> getMouseMoved() {

		return (MouseEvent event) -> {
			MapMouseEvent mapEv = convertEvent(event);
			if (mapEv != null) {
				for (MapMouseListener listener : listeners) {
					listener.onMouseMoved(mapEv);
				}
			}
		};

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventHandler<ScrollEvent> getMouseWheelMoved() {

		return (ScrollEvent event) -> {
			MapMouseEvent mapEv = convertEvent(event);
			if (mapEv != null) {
				for (MapMouseListener listener : listeners) {
					listener.onMouseWheelMoved(mapEv);
				}
			}
		};

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MapMouseEvent convertEvent(MouseEvent ev) {
		MapMouseEvent mapEv = null;
		if (mapPane.getScreenToWorldTransform() != null) {
			mapEv = new MapMouseEvent(mapPane, ev);
		}

		return mapEv;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MapMouseEvent convertEvent(ScrollEvent ev) {
		MapMouseEvent mapEv = null;
		if (mapPane.getScreenToWorldTransform() != null) {
			mapEv = new MapMouseEvent(mapPane, ev);
		}

		return mapEv;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MapPane getMapPane() {
		// TODO Auto-generated method stub
		return this.mapPane;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMapPane(MapPane mapPane) {
		this.mapPane = mapPane;
	}

}
