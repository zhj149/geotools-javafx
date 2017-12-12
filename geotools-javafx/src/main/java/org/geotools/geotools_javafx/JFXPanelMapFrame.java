package org.geotools.geotools_javafx;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

import org.geotools.geotools_javafx.action.OpenShpLayerAction;
import org.geotools.geotools_javafx.action.PanAction;
import org.geotools.geotools_javafx.action.RestAction;
import org.geotools.geotools_javafx.action.ZoomInAction;
import org.geotools.geotools_javafx.action.ZoomOutAction;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * 使用jfxpanel的demo
 * @author sam
 *
 */
public class JFXPanelMapFrame extends JFrame { 
	
	private static final long serialVersionUID = -343978783556856172L;

	/**
	 * 解决放大缩小窗口后，画布消失的问题
	 */
	static {
		Platform.setImplicitExit(false);
	}

	/**
	 * 画布对象
	 */
	private JFXMapCanvas canvas;

	/**
	 * 画布对象
	 * 
	 * @return
	 */
	public JFXMapCanvas getCanvas() {
		return canvas;
	}

	/**
	 * 画布对象
	 * 
	 * @param canvas
	 */
	public void setCanvas(JFXMapCanvas canvas) {
		this.canvas = canvas;
	}

	/**
	 * 构造函数
	 * 
	 * @param parent
	 * @param style
	 */
	public JFXPanelMapFrame() {
		this(new StreamingRenderer(), new MapContent());
	}

	/**
	 * 构造函数
	 * 
	 * @param parent
	 * @param style
	 * @param renderer
	 * @param content
	 */
	public JFXPanelMapFrame(GTRenderer renderer, MapContent content) {
		super();
		initGraphics(renderer, content);
		// 重置画布大小
		panel.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				Platform.runLater(() -> {
					Double widthDouble = new Integer(panel.getWidth()).doubleValue();
					Double heightDouble = new Integer(panel.getHeight()).doubleValue();
					canvas.setWidth(widthDouble - 2);
					canvas.setHeight(heightDouble - toolBar.getHeight() - 2);
					canvas.repaint(true);
				});
			}
		});

	}

	/**
	 * 是否显示比例尺
	 */
	private boolean showScaleRuler = true;

	/**
	 * 是否显示比例尺
	 * 
	 * @return
	 */
	public boolean isShowScaleRuler() {
		return showScaleRuler;
	}

	/**
	 * 是否显示比例尺
	 * 
	 * @param showScaleRuler
	 */
	public void setShowScaleRuler(boolean showScaleRuler) {
		this.showScaleRuler = showScaleRuler;
	}

	/**
	 * 画布所在的容器
	 */
	private JFXPanel panel;
	
	/**
	 *  工具条
	 */
	private HBox toolBar;

	/**
	 * 初始化画布的方法
	 * 
	 * @param renderer
	 * @param content
	 */
	protected void initGraphics(GTRenderer renderer, MapContent content) {
		this.setLayout(new BorderLayout());

		panel = new JFXPanel();

		try {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					Group root = new Group();
					Scene scene = new Scene(root);
					panel.setScene(scene);
					Double widthDouble = new Integer(panel.getWidth()).doubleValue();
					Double heightDouble = new Integer(panel.getHeight()).doubleValue();

					VBox box = new VBox();
					toolBar = new HBox(2);
					toolBar.setMaxHeight(28);

					canvas = new JFXMapCanvas(content);
					canvas.setWidth(widthDouble);
					canvas.setHeight(heightDouble);
					box.getChildren().add(toolBar);
					box.getChildren().add(canvas);
					root.getChildren().add(box);

					createToolBar(toolBar, canvas);

				}
			});

			this.add(panel, BorderLayout.CENTER);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 这里是创建toolbar的地方
	 * 
	 * @param toolBar
	 * @param map
	 */
	private void createToolBar(HBox toolBar, JFXMapCanvas map) {
		RestAction restAction = new RestAction(map);
		ZoomInAction zoomInAction = new ZoomInAction(map);
		ZoomOutAction zoomOutAction = new ZoomOutAction(map);
		PanAction paneAction = new PanAction(map);
		OpenShpLayerAction shpAction = new OpenShpLayerAction(map);

		// reset按钮
		Button btnRest = new Button("", new ImageView("/mActionZoomFullExtent.png"));
		btnRest.setMaxSize(24, 24);
		btnRest.setTooltip(new Tooltip("重置"));
		btnRest.setOnAction(restAction);

		// 放大按钮
		Button btnZoomIn = new Button("", new ImageView("/mActionZoomIn.png"));
		btnZoomIn.setMaxSize(24, 24);
		btnZoomIn.setTooltip(new Tooltip("放大"));
		btnZoomIn.setOnAction(zoomInAction);

		// 放大按钮
		Button btnZoomOut = new Button("", new ImageView("/mActionZoomOut.png"));
		btnZoomOut.setMaxSize(24, 24);
		btnZoomOut.setTooltip(new Tooltip("缩小"));
		btnZoomOut.setOnAction(zoomOutAction);

		// 拖动按钮
		Button btnPane = new Button("", new ImageView("/mActionPan.png"));
		btnPane.setMaxSize(24, 24);
		btnPane.setTooltip(new Tooltip("拖动"));
		btnPane.setOnAction(paneAction);

		// 拖动按钮
		Button shpPane = new Button("", new ImageView("/mOpenLayer.png"));
		shpPane.setMaxSize(24, 24);
		shpPane.setTooltip(new Tooltip("导入图层"));
		shpPane.setOnAction(shpAction);


		toolBar.getChildren().addAll(btnRest, btnZoomIn, btnZoomOut, btnPane, shpPane);
	}

	public static void main(String[] args) {
		JFXPanelMapFrame frm = new JFXPanelMapFrame();
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setSize(1024, 768);
		frm.setVisible(true);
	}

}
