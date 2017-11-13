package org.geotools.geotools_javafx;

import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geotools_javafx.tools.ZoomInTool;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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
	
	private double actualZoom = 0.3d; 

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
		if (files != null && !files.isEmpty()){
			for (File file : files) {
				FileDataStore store = FileDataStoreFinder.getDataStore(file);
				SimpleFeatureSource featureSource = store.getFeatureSource();

				Style style = SLD.createSimpleStyle(featureSource.getSchema(), Color.red);
				Layer layer = new FeatureLayer(featureSource, style);
				mapContent.addLayer(layer);
			}
		}else{
			return;
		}

		JFXMapPane map = new JFXMapPane(mapContent);
		map.setWidth(1024);
		map.setHeight(768);
		StackPane root = new StackPane();
		root.getChildren().add(map);

		Scene scene = new Scene(root, 1024, 768);

		primaryStage.setTitle("Hello World!");
		primaryStage.setScene(scene);
		primaryStage.show();

		Timer timer = new Timer("123");
		
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				map.repaint(true);
				map.setCursorTool(new ZoomInTool());
			}
		}, 1000);
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
