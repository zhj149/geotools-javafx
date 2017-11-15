package org.geotools.geotools_javafx.action;

import org.geotools.geotools_javafx.MapPane;

import javafx.scene.input.MouseEvent;

/**
 * 地图初始化的动作
 * 
 * @author sam
 *
 */
public class RestAction extends AbstractMapAction {

	/**
	 * 地图初始化的动作
	 * 
	 * @param mapPane
	 */
	public RestAction(MapPane mapPane) {
		super(mapPane);
	}

	/**
	 * 地图初始化的动作
	 */
	@Override
	public void handle(MouseEvent event) {
		if (this.getMapPane() != null)
			this.getMapPane().reset();
	}

}
