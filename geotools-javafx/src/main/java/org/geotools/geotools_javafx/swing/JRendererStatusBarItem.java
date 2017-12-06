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

package org.geotools.geotools_javafx.swing;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.geotools.geotools_javafx.MapPane;
import org.geotools.geotools_javafx.event.MapPaneAdapter;
import org.geotools.geotools_javafx.event.MapPaneEvent;

/**
 * A status bar item that displays an animated icon to indicate renderer activity.
 *
 * @see JMapStatusBar
 *
 * @author Michael Bedward
 * @since 8.0
 *
 * @source $URL$
 * @version $Id$
 */
public class JRendererStatusBarItem extends StatusBarItem {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TOOL_TIP = "RendererTooltip";
    private static final String BUSY_IMAGE = "busy.gif";
    private static final String IDLE_IMAGE = "idle.gif";

    private final ImageIcon busyIcon;
    private final ImageIcon idleIcon;

    /*
     * Creates a new item associated with teh given map.
     */
    public JRendererStatusBarItem(MapPane mapPane) {
        super("Busy", false);

        busyIcon = new ImageIcon(JRendererStatusBarItem.class.getResource(BUSY_IMAGE));
        idleIcon = new ImageIcon(JRendererStatusBarItem.class.getResource(IDLE_IMAGE));

        final JLabel renderLabel = new JLabel();
        renderLabel.setIcon(idleIcon);
        renderLabel.setToolTipText(TOOL_TIP);

        Insets insets = getInsets();
        renderLabel.setMinimumSize(new Dimension(
                busyIcon.getIconWidth() + insets.left + insets.right,
                busyIcon.getIconHeight() + insets.top + insets.bottom));

        add(renderLabel);

        mapPane.addMapPaneListener(new MapPaneAdapter() {
            @Override
            public void onRenderingStarted(MapPaneEvent ev) {
                renderLabel.setIcon(busyIcon);
            }

            @Override
            public void onRenderingStopped(MapPaneEvent ev) {
                renderLabel.setIcon(idleIcon);
            }
        });
    }

}
