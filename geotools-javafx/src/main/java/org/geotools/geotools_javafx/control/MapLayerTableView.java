package org.geotools.geotools_javafx.control;

import org.geotools.map.Layer;
import org.geotools.map.event.MapLayerListEvent;
import org.geotools.map.event.MapLayerListListener;

import javafx.scene.control.TableView;

/**
 * 图层列表控件
 * @author sam
 *
 */
public class MapLayerTableView extends TableView<Layer> implements MapLayerListListener {

	//begin MapLayerListListener
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layerAdded(MapLayerListEvent event) {
		this.getItems().add(event.getElement());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layerRemoved(MapLayerListEvent event) {
		this.getItems().remove(event.getElement());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layerChanged(MapLayerListEvent event) {
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layerMoved(MapLayerListEvent event) {
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layerPreDispose(MapLayerListEvent event) {
		
	}

	//end
}
