package org.geotools.geotools_javafx.action;

import org.geotools.geotools_javafx.MapPane;
import org.geotools.geotools_javafx.tools.ZoomOutTool;

import javafx.event.ActionEvent;

/**
 * 缩小操作
 * 
 * @author sam
 *
 */
public class ZoomOutAction extends AbstractMapAction {

	/**
	 * 缩小操作
	 */
	public ZoomOutAction() {

	}

	/**
	 * 缩小操作
	 * 
	 * @param mapPane
	 */
	public ZoomOutAction(MapPane mapPane) {
		this.setMapPane(mapPane);
	}

	/**
	 * 缩小操作
	 */
	@Override
	public void handle(ActionEvent event) {
		if (this.getMapPane() != null) {
			this.getMapPane().setCursorTool(new ZoomOutTool());
		}
	}

}
