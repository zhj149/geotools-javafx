package org.geotools.geotools_javafx.control.view;

import org.geotools.map.Layer;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * 图层名称
 * @author sam
 *
 */
public class LayerNameTableColumn extends TableColumn<Layer, String> {

	/**
	 * 图层名称
	 */
	public LayerNameTableColumn(){
		super("名称");
		this.setCellValueFactory(new PropertyValueFactory<Layer,String>("title"));
		this.setEditable(false);
	}
}
