package org.geotools.geotools_javafx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geotools_javafx.event.DefaultMapMouseEventDispatcher;
import org.geotools.geotools_javafx.event.MapMouseEventDispatcher;
import org.geotools.geotools_javafx.event.MapMouseListener;
import org.geotools.geotools_javafx.event.MapPaintListener;
import org.geotools.geotools_javafx.event.MapPaneEvent;
import org.geotools.geotools_javafx.event.MapPaneListener;
import org.geotools.geotools_javafx.tools.CursorTool;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.event.MapBoundsEvent;
import org.geotools.map.event.MapBoundsListener;
import org.geotools.map.event.MapLayerListEvent;
import org.geotools.map.event.MapLayerListListener;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.LabelCache;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.util.logging.Logging;
import org.jfree.fx.FXGraphics2D;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * javafx实现的geotools画布对象
 * 
 * @author sam
 *
 */
public class JFXMapPane extends Canvas implements MapPane, MapLayerListListener, MapBoundsListener {

	/**
	 * 重绘间隔
	 */
	public static final int DEFAULT_RESIZING_PAINT_DELAY = 500; // delay in
																// milliseconds

	/**
	 * 当前的日志对象
	 */
	protected static final Logger LOGGER = Logging.getLogger("org.geotools.JavaFXMapPane");

	/**
	 * 当前的视界范围
	 */
	protected ReferencedEnvelope fullExtent;

	/**
	 * 当前地图的上下文
	 */
	protected MapContent mapContent;

	/**
	 * 绘制上下文对象
	 */
	protected GTRenderer renderer;

	/*
	 * If the user sets the display area before the pane is shown on screen we
	 * store the requested envelope with this field and refer to it when the
	 * pane is shown.
	 */
	protected ReferencedEnvelope pendingDisplayArea;

	/**
	 * 注册的地图事件
	 */
	protected List<MapPaneListener> listeners = new LinkedList<>();

	/**
	 * 缓存(应该是标签)
	 */
	protected LabelCache labelCache;

	/**
	 * 重绘的时候是否清理标签缓存数据
	 */
	private volatile boolean clearLabelCache = true;

	/**
	 * 鼠标操作事件包装对象
	 */
	protected MapMouseEventDispatcher mapMouseEventDispatcher;

	/**
	 * 二维画布封装
	 */
	protected FXGraphics2D g2d;

	/**
	 * javafx实现的geotools画布对象
	 * 
	 * @param content
	 */
	public JFXMapPane(MapContent content) {

		// 实现画布
		g2d = new FXGraphics2D(this.getGraphicsContext2D());
		this.renderer = new StreamingRenderer();
		this.setMapContent(content);

		mapMouseEventDispatcher = new DefaultMapMouseEventDispatcher(this);
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, mapMouseEventDispatcher.getMousePressed());
		this.addEventHandler(MouseEvent.MOUSE_RELEASED, mapMouseEventDispatcher.getMouseReleased());
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, mapMouseEventDispatcher.getMouseClicked());
		this.addEventHandler(MouseEvent.MOUSE_DRAGGED, mapMouseEventDispatcher.getMouseDragged());
		this.addEventHandler(MouseEvent.MOUSE_ENTERED, mapMouseEventDispatcher.getMouseEntered());
		this.addEventHandler(MouseEvent.MOUSE_EXITED, mapMouseEventDispatcher.getMouseExited());
		this.addEventHandler(MouseEvent.MOUSE_MOVED, mapMouseEventDispatcher.getMouseMoved());
		this.addEventHandler(ScrollEvent.SCROLL, mapMouseEventDispatcher.getMouseWheelMoved());
	}

	// begin functions

	/**
	 * Gets the full extent of map context's layers. The only reason this method
	 * is defined is to avoid having try-catch blocks all through other methods.
	 */
	private void setFullExtent() {
		if (mapContent != null && mapContent.layers().size() > 0) {
			try {

				fullExtent = mapContent.getMaxBounds();

				/*
				 * Guard agains degenerate envelopes (e.g. empty map layer or
				 * single point feature)
				 */
				if (fullExtent == null) {
					// set arbitrary bounds centred on 0,0
					fullExtent = worldEnvelope();

				}

			} catch (Exception ex) {
				throw new IllegalStateException(ex);
			}
		} else {
			fullExtent = null;
		}
	}

	/**
	 * 视界地图边界
	 * 
	 * @return
	 */
	private ReferencedEnvelope worldEnvelope() {
		return new ReferencedEnvelope(-180, 180, -90, 90, DefaultGeographicCRS.WGS84);
	}

	/**
	 * Helper method for {@linkplain #setDisplayArea} which is also called by
	 * other methods that want to set the display area without provoking
	 * repainting of the display
	 *
	 * @param envelope
	 *            requested display area
	 */
	protected void doSetDisplayArea(Envelope envelope) {
		if (mapContent != null) {
			CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();
			if (crs == null) {
				// assume that it is the current CRS
				crs = mapContent.getCoordinateReferenceSystem();
			}

			ReferencedEnvelope refEnv = new ReferencedEnvelope(envelope.getMinimum(0), envelope.getMaximum(0),
					envelope.getMinimum(1), envelope.getMaximum(1), crs);

			mapContent.getViewport().setBounds(refEnv);

		} else {
			pendingDisplayArea = new ReferencedEnvelope(envelope);
		}

		// Publish the resulting display area with the event
		publishEvent(new MapPaneEvent(this, MapPaneEvent.Type.DISPLAY_AREA_CHANGED, getDisplayArea()));
	}

	// end

	// begin MapPane implements

	/**
	 * 当前地图的上下文
	 */
	public MapContent getMapContent() {
		return mapContent;
	}

	/**
	 * 重新设置地图上下文
	 */
	public void setMapContent(MapContent content) {

		if (this.mapContent != content) {

			if (this.mapContent != null) {
				this.mapContent.removeMapLayerListListener(this);
			}

			this.mapContent = content;

			if (content != null) {
				this.mapContent.addMapLayerListListener(this);
				this.mapContent.addMapBoundsListener(this);

				// set all layers as selected by default for the info tool
				for (Layer layer : content.layers()) {
					layer.setSelected(true);
				}

				setFullExtent();
			}

			if (renderer != null) {
				renderer.setMapContent(this.mapContent);
			}

			MapPaneEvent ev = new MapPaneEvent(this, MapPaneEvent.Type.NEW_CONTEXT);
			publishEvent(ev);
		}
	}

	/**
	 * 发布map事件订阅
	 * 
	 * @param ev
	 */
	private void publishEvent(MapPaneEvent ev) {
		for (MapPaneListener listener : listeners) {
			switch (ev.getType()) {
			case NEW_CONTEXT:
				listener.onNewContext(ev);
				break;

			case NEW_RENDERER:
				listener.onNewRenderer(ev);
				break;

			case PANE_RESIZED:
				listener.onResized(ev);
				break;

			case DISPLAY_AREA_CHANGED:
				listener.onDisplayAreaChanged(ev);
				break;

			case RENDERING_STARTED:
				listener.onRenderingStarted(ev);
				break;

			case RENDERING_STOPPED:
				listener.onRenderingStopped(ev);
				break;

			case RENDERING_PROGRESS:
				listener.onRenderingProgress(ev);
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MapMouseEventDispatcher getMouseEventDispatcher() {
		return mapMouseEventDispatcher;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMouseEventDispatcher(MapMouseEventDispatcher dispatcher) {
		if (mapMouseEventDispatcher != dispatcher) {
			if (this.mapMouseEventDispatcher != null && dispatcher != null) {
				List<MapMouseListener> listeners = mapMouseEventDispatcher.getAllListeners();
				for (MapMouseListener l : listeners) {
					dispatcher.addMouseListener(l);
				}
				mapMouseEventDispatcher.removeAllListeners();
			}
			this.mapMouseEventDispatcher = dispatcher;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReferencedEnvelope getDisplayArea() {
		if (mapContent != null) {
			return mapContent.getViewport().getBounds();
		} else if (pendingDisplayArea != null) {
			return new ReferencedEnvelope(pendingDisplayArea);
		} else {
			return new ReferencedEnvelope();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDisplayArea(Envelope envelope) {
		if (envelope == null) {
			throw new IllegalArgumentException("envelope must not be null");
		}

		doSetDisplayArea(envelope);
		if (mapContent != null) {
			clearLabelCache = true;
			this.repaint();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		if (fullExtent != null) {
			setDisplayArea(fullExtent);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AffineTransform getScreenToWorldTransform() {
		if (mapContent != null) {
			return mapContent.getViewport().getScreenToWorld();
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AffineTransform getWorldToScreenTransform() {
		if (mapContent != null) {
			return mapContent.getViewport().getWorldToScreen();
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMapPaneListener(MapPaneListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeMapPaneListener(MapPaneListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMouseListener(MapMouseListener listener) {
		if (this.mapMouseEventDispatcher != null)
			this.mapMouseEventDispatcher.addMouseListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeMouseListener(MapMouseListener listener) {
		if (this.mapMouseEventDispatcher != null)
			this.mapMouseEventDispatcher.removeMouseListener(listener);
	}

	public CursorTool getCursorTool() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCursorTool(CursorTool tool) {
		// TODO Auto-generated method stub

	}

	public void moveImage(int dx, int dy) {
		// TODO Auto-generated method stub

	}
	
	private BufferedImage baseImage;

	/**
	 * 重绘
	 */
	public void repaint() {
		
		Rectangle r = new Rectangle(0, 0, (int) this.getWidth(), (int) this.getHeight());
		if (g2d == null) {
			g2d = new FXGraphics2D(this.getGraphicsContext2D());
			clearLabelCache = true;

		} else {
			g2d.setBackground(Color.WHITE);
			g2d.clearRect(0, 0, (int) this.getWidth(), (int) this.getHeight());
		}
		
		baseImage = new BufferedImage(r.width + 1, r.height + 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D memory2D = baseImage.createGraphics();
		
		this.renderer.paint(memory2D, r, this.mapContent.getMaxBounds(), getWorldToScreenTransform());
		g2d.drawImage(baseImage, 0, 0, null);
	}

	public void addPaintListener(MapPaintListener listener) {
		// TODO Auto-generated method stub

	}

	public void removePaintListener(MapPaintListener listener) {
		// TODO Auto-generated method stub

	}

	// end

	// begin layer implements

	public void layerAdded(MapLayerListEvent event) {
		// TODO Auto-generated method stub

	}

	public void layerRemoved(MapLayerListEvent event) {
		// TODO Auto-generated method stub

	}

	public void layerChanged(MapLayerListEvent event) {
		// TODO Auto-generated method stub

	}

	public void layerMoved(MapLayerListEvent event) {
		// TODO Auto-generated method stub

	}

	public void layerPreDispose(MapLayerListEvent event) {
		// TODO Auto-generated method stub

	}

	// end

	// begin mapbound implements

	public void mapBoundsChanged(MapBoundsEvent event) {

	}

	// end
}
