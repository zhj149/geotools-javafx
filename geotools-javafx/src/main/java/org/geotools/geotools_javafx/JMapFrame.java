package org.geotools.geotools_javafx;

import org.geotools.geotools_javafx.action.OpenShpLayerAction;
import org.geotools.geotools_javafx.action.PanAction;
import org.geotools.geotools_javafx.action.RestAction;
import org.geotools.geotools_javafx.action.ZoomInAction;
import org.geotools.geotools_javafx.action.ZoomOutAction;
import org.geotools.geotools_javafx.control.MapLayerTableView;
import org.geotools.geotools_javafx.control.view.LayerNameTableColumn;
import org.geotools.geotools_javafx.control.view.LayerVisibleTableColumn;
import org.geotools.geotools_javafx.control.view.StyleSettingTableColumn;
import org.geotools.map.MapContent;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * javafx实现的地图窗口
 * 
 * @author sam
 *
 */
public class JMapFrame extends Application {

	/**
	 * 窗口的启动
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		MapContent mapContent = new MapContent();

		mapContent.setTitle("Quickstart");
		HBox toolBar = new HBox();
		VBox root = new VBox();

		JFXMapCanvas map = new JFXMapCanvas(mapContent);
		map.setWidth(1024);
		map.setHeight(768);

		// 执行的动作
		RestAction restAction = new RestAction(map);
		ZoomInAction zoomInAction = new ZoomInAction(map);
		ZoomOutAction zoomOutAction = new ZoomOutAction(map);
		PanAction paneAction = new PanAction(map);
		OpenShpLayerAction shpAction = new OpenShpLayerAction(map);

		// reset按钮
		Button btnRest = new Button("", new ImageView("/mActionZoomFullExtent.png"));
		btnRest.setMinSize(24, 24);
		btnRest.setTooltip(new Tooltip("重置"));
		btnRest.setOnAction(restAction);

		// 放大按钮
		Button btnZoomIn = new Button("", new ImageView("/mActionZoomIn.png"));
		btnZoomIn.setMinSize(24, 24);
		btnZoomIn.setTooltip(new Tooltip("放大"));
		btnZoomIn.setOnAction(zoomInAction);

		// 缩小按钮
		Button btnZoomOut = new Button("", new ImageView("/mActionZoomOut.png"));
		btnZoomOut.setMinSize(24, 24);
		btnZoomOut.setTooltip(new Tooltip("放大"));
		btnZoomOut.setOnAction(zoomOutAction);

		// 拖动按钮
		Button btnPane = new Button("", new ImageView("/mActionPan.png"));
		btnPane.setMinSize(24, 24);
		btnPane.setTooltip(new Tooltip("拖动"));
		btnPane.setOnAction(paneAction);

		// 拖动按钮
		Button shpPane = new Button("", new ImageView("/open.gif"));
		shpPane.setMinSize(24, 24);
		shpPane.setTooltip(new Tooltip("导入图层"));
		shpPane.setOnAction(shpAction);

		toolBar.getChildren().addAll(btnRest, btnZoomIn,btnZoomOut, btnPane, shpPane);

		// 图层表格
		MapLayerTableView layerTable = new MapLayerTableView();
		layerTable.setEditable(true);

		layerTable.getColumns().add(new LayerVisibleTableColumn());
		layerTable.getColumns().add(new LayerNameTableColumn());
		layerTable.getColumns().add(new StyleSettingTableColumn());

		mapContent.addMapLayerListListener(layerTable);

		root.getChildren().add(toolBar);
		root.getChildren().add(new SplitPane(map, layerTable));

		Scene scene = new Scene(root, 1366, 768);

		primaryStage.setTitle("geotools javafx");
		primaryStage.setScene(scene);
		primaryStage.show();

		map.repaint(true);

	}

	/**
	 * 程序的入口点
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
