package org.geotools.geotools_javafx;

import java.awt.Color;
import java.io.File;
import java.util.List;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geotools_javafx.action.PanAction;
import org.geotools.geotools_javafx.action.RestAction;
import org.geotools.geotools_javafx.action.ZoomInAction;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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

		final FileChooser fileChooser = new FileChooser();
		// Create a map content and add our shapefile to it
		MapContent mapContent = new MapContent();
		fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Arcgis文件", "*.shp"));
		mapContent.setTitle("Quickstart");

		List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
		if (files != null && !files.isEmpty()) {
			for (File file : files) {
				FileDataStore store = FileDataStoreFinder.getDataStore(file);
				SimpleFeatureSource featureSource = store.getFeatureSource();

				Style style = SLD.createSimpleStyle(featureSource.getSchema(), Color.red);
				Layer layer = new FeatureLayer(featureSource, style);
				mapContent.addLayer(layer);
			}
		} else {
			return;
		}
		HBox toolBar = new HBox(5);
		VBox root = new VBox();

		JFXMapCanvas map = new JFXMapCanvas(mapContent);
		map.setWidth(1024);
		map.setHeight(768);

		// 执行的动作
		RestAction restAction = new RestAction(map);
		ZoomInAction zoomInAction = new ZoomInAction(map);
		PanAction paneAction = new PanAction(map);

		// reset按钮
		Button btnRest = new Button("", new ImageView("/mActionZoomFullExtent.png"));
		btnRest.setMinSize(24, 24);
		btnRest.setTooltip(new Tooltip("重置"));
		btnRest.addEventHandler(MouseEvent.MOUSE_CLICKED, restAction);

		// 放大按钮
		Button btnZoomIn = new Button("", new ImageView("/mActionZoomIn.png"));
		btnZoomIn.setMinSize(24, 24);
		btnZoomIn.setTooltip(new Tooltip("放大"));
		btnZoomIn.addEventHandler(MouseEvent.MOUSE_CLICKED, zoomInAction);

		// 拖动按钮
		Button btnPane = new Button("", new ImageView("/mActionPan.png"));
		btnPane.setMinSize(24, 24);
		btnPane.setTooltip(new Tooltip("拖动"));
		btnPane.addEventHandler(MouseEvent.MOUSE_CLICKED, paneAction);

		toolBar.getChildren().addAll(btnRest, btnZoomIn, btnPane);

		root.getChildren().add(toolBar);
		root.getChildren().add(map);

		Scene scene = new Scene(root, 1024, 768);

		primaryStage.setTitle("Hello World!");
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
