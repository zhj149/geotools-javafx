package org.geotools.geotools_javafx.action;

import org.geotools.geotools_javafx.MapPane;
import org.geotools.geotools_javafx.tools.ZoomInTool;

import javafx.scene.input.MouseEvent;

/**
 * 放大操作
 * 
 * @author sam
 *
 */
public class ZoomInAction extends AbstractMapAction {

	/**
	 * 放大操作
	 */
	public ZoomInAction() {

	}

	/**
	 * 放大操作
	 * 
	 * @param mapPane
	 */
	public ZoomInAction(MapPane mapPane) {
		this.setMapPane(mapPane);
	}

	/**
	 * 放大操作
	 */
	@Override
	public void handle(MouseEvent event) {
		if (this.getMapPane() != null) {
			this.getMapPane().setCursorTool(new ZoomInTool());
		}
	}

}
