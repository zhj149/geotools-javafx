package org.geotools.geotools_javafx.action;

import org.geotools.geotools_javafx.MapPane;
import org.geotools.geotools_javafx.action.MapAction;

/**
 * 所有的工具控件的基类
 * @author sam
 *
 */
public abstract class AbstractMapAction implements MapAction {
	
	/**
	 * 画布对象
	 */
	protected MapPane mapPane;
	
	/**
	 * 所有的工具控件的基类
	 */
	public AbstractMapAction(MapPane mapPane){
		this.setMapPane(mapPane);
	}
	
	/**
	 * 所有的工具控件的基类
	 */
	public AbstractMapAction(){
	}

	/**
	 * 画布对象
	 */
	@Override
	public MapPane getMapPane() {
		return this.mapPane;
	}

	/**
	 * 设置画布对象
	 */
	@Override
	public void setMapPane(MapPane mapPane) {
		this.mapPane = mapPane;
	}

}
