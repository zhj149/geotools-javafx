package org.geotools.geotools_javafx.action;

import org.geotools.geotools_javafx.MapPane;
import org.geotools.geotools_javafx.tools.PanTool;

import javafx.event.ActionEvent;

/**
 * 移动操作
 * 
 * @author sam
 *
 */
public class PanAction extends AbstractMapAction {

	/**
	 * 移动操作
	 */
	public PanAction() {

	}

	/**
	 * 移动操作
	 * 
	 * @param mapPane
	 */
	public PanAction(MapPane mapPane) {
		this.setMapPane(mapPane);
	}

	/**
	 * 放大操作
	 */
	@Override
	public void handle(ActionEvent event) {
		if (this.getMapPane() != null) {
			this.getMapPane().setCursorTool(new PanTool());
		}
	}

}
