package org.geotools.geotools_javafx.control.view;

import org.geotools.map.Layer;

import javafx.beans.property.adapter.JavaBeanBooleanProperty;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;

/**
 * 图层展示和隐藏的工具
 * 
 * @author sam
 *
 */
public class LayerVisibleTableColumn extends TableColumn<Layer, Boolean> {

	/**
	 * 图层展示和隐藏的工具
	 */
	public LayerVisibleTableColumn() {
		super("显示");
		this.setCellValueFactory(data -> {
			JavaBeanBooleanProperty build = null;
			try {
				build = JavaBeanBooleanPropertyBuilder.create().name("visible").bean(data.getValue()).build();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			return build;
		});
		this.setCellFactory(CheckBoxTableCell.<Layer>forTableColumn(this));
		this.setEditable(true);
	}
}
