package org.geotools.geotools_javafx.action;

import java.awt.Color;
import java.io.File;
import java.util.Random;

import org.geotools.data.CachingFeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geotools_javafx.MapPane;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * 导入shp文件的操作
 * 
 * @author sam
 *
 */
public class OpenShpLayerAction extends AbstractMapAction {

	/**
	 * 导入操作
	 * 
	 * @param mapPane
	 */
	public OpenShpLayerAction(MapPane mapPane) {
		super(mapPane);
	}

	/**
	 * 生成随机颜色的种子
	 */
	private static Random rd = new Random();

	/**
	 * 导入shp图层
	 */
	public OpenShpLayerAction() {

	}

	/**
	 * 导入操作的内容
	 */
	@Override
	public void handle(ActionEvent event) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Arcgis文件", "*.shp"));
		File file = fileChooser.showOpenDialog(null);
		if (file == null) {
			return;
		}

		try {
			FileDataStore store = FileDataStoreFinder.getDataStore(file);
			SimpleFeatureSource featureSource = store.getFeatureSource();
			CachingFeatureSource cache = new CachingFeatureSource(featureSource);
			Style style = SLD.createSimpleStyle(featureSource.getSchema(),
					new Color(rd.nextInt(255), rd.nextInt(255), rd.nextInt(255)));
			FeatureLayer layer = new FeatureLayer(cache, style , file.getName().substring(0 , file.getName().lastIndexOf(".")));

			this.getMapPane().getMapContent().addLayer(layer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
