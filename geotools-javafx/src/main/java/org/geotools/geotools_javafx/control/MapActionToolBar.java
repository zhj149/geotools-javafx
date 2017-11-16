package org.geotools.geotools_javafx.control;

import org.geotools.geotools_javafx.MapListener;
import org.geotools.geotools_javafx.MapPane;
import org.geotools.geotools_javafx.action.RestAction;
import org.geotools.geotools_javafx.action.ZoomInAction;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

/**
 * 系统的工具条示例
 * 
 * @author sam
 *
 */
public class MapActionToolBar extends ToolBar implements MapListener {

	/**
	 * 画布
	 */
	private MapPane mapPane;
	
	
	private RestAction restAction;
	
	
	private ZoomInAction zoomInAction;

	/**
	 * 初始化菜单控件
	 */
	public MapActionToolBar() {
		initCompents();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MapPane getMapPane() {
		return this.mapPane;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMapPane(MapPane mapPane) {
		this.mapPane = mapPane;
		this.restAction.setMapPane(mapPane);
		this.zoomInAction.setMapPane(mapPane);
	}

	/**
	 * 初始化工具条
	 */
	protected void initCompents() {
		this.setPrefHeight(28);
		this.setPrefHeight(120);
		Button btnRest = new Button();
		btnRest.setMinSize(48, 48);
		btnRest.setTooltip(new Tooltip("重置"));
		btnRest.setBackground(new Background(new BackgroundImage(
				new Image(this.getClass().getResourceAsStream("/mActionPan.png")), BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
		
		restAction = new RestAction();
		btnRest.addEventHandler(MouseEvent.MOUSE_CLICKED, restAction);

		this.getChildren().add(btnRest);

		Button btnZoomIn = new Button();
		btnZoomIn.setMinSize(48, 48);
		btnZoomIn.setTooltip(new Tooltip("放大"));
		btnZoomIn.setBackground(new Background(new BackgroundImage(
				new Image(this.getClass().getResourceAsStream("/mActionZoomIn.png")), BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
		
		zoomInAction = new ZoomInAction();
		btnZoomIn.addEventHandler(MouseEvent.MOUSE_CLICKED, zoomInAction);

		this.getChildren().add(btnZoomIn);
	}
}
