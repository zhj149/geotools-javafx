package org.geotools.geotools_javafx.control.view;

import org.geotools.data.FeatureSource;
import org.geotools.geotools_javafx.control.SimpleStyleDialog;
import org.geotools.map.Layer;
import org.opengis.feature.simple.SimpleFeatureType;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * 样式设置列
 * 
 * @author sam
 *
 */
public class StyleSettingTableColumn extends TableColumn<Layer, FeatureSource> {

	/**
	 * 样式设置列
	 */
	public StyleSettingTableColumn() {

		super("样式");
		this.setCellValueFactory(new PropertyValueFactory<Layer, FeatureSource>("featureSource"));
		this.setEditable(true);

		/**
		 * 图片的显示样式
		 */
		this.setCellFactory(new Callback<TableColumn<Layer, FeatureSource>, TableCell<Layer, FeatureSource>>() {

			@Override
			public TableCell<Layer, FeatureSource> call(TableColumn<Layer, FeatureSource> param) {
				final ImageView imageview = new ImageView("/style.gif");
				imageview.setFitHeight(16);
				imageview.setFitWidth(16);
				
				Button button = new Button("" , imageview);

				TableCell<Layer, FeatureSource> cell = new TableCell<Layer, FeatureSource>() {

					@Override
					protected void updateItem(FeatureSource item, boolean empty) {
						if (empty){
							this.setText(null);
							this.setGraphic(null);
						}else{
							this.setText("");
							button.setOnAction(event -> {
								SimpleFeatureType type = (SimpleFeatureType)item.getSchema();
								SimpleStyleDialog.showDialog(type);
							});
							this.setGraphic(button);
						}
					}
				};

				
				return cell;
			}

		});
	}

}
