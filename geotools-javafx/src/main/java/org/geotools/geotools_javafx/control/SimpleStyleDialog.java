package org.geotools.geotools_javafx.control;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.JOptionPane;

import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.geometry.jts.Geometries;
import org.geotools.geotools_javafx.styling.SLDs;
import org.geotools.map.RasterLayer;
import org.geotools.map.StyleLayer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Font;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;

import com.vividsolutions.jts.geom.Geometry;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.AnchorPane;

/**
 * 图层对象设置窗口
 * 
 * @author sam
 *
 */
public class SimpleStyleDialog extends Dialog<Style> {

	protected StyleBuilder build = new StyleBuilder();

	protected SimpleFeatureCollection featureCollection;

	public static StyleFactory sf = CommonFactoryFinder.getStyleFactory(GeoTools.getDefaultHints());

	/**
	 * 填充的样式
	 */
	private static final String WELL_KNOWN_SYMBOL_NAMES[] = { "Circle", "Square", "Cross", "X", "Triangle", "Star" };

	public static final Color DEFAULT_LINE_COLOR = Color.BLACK;
	public static final Color DEFAULT_FILL_COLOR = Color.WHITE;
	public static final float DEFAULT_LINE_WIDTH = 1.0f;
	public static final float DEFAULT_OPACITY = 1.0f;
	public static final float DEFAULT_POINT_SIZE = 3.0f;
	public static final String DEFAULT_POINT_SYMBOL_NAME = "Circle";
	private static int COLOR_ICON_SIZE = 16;
	private Geometries geomType;

	private Color lineColor;
	private Color fillColor;
	private float lineWidth;
	private float opacity;
	private float pointSize;
	private String pointSymbolName;
	private boolean labelFeatures;
	private String labelField;
	private Font labelFont;

	private static enum ControlCategory {
		LINE, FILL, POINT;
	}

	private Map<Component, ControlCategory> controls;

	private final SimpleFeatureType schema;
	private String[] fieldsForLabels;

	private boolean completed;

	/**
	 * 当前图层属性
	 */
	private Style style;

	/**
	 * 当前图层属性
	 * 
	 * @return
	 */
	public Style getStyle() {
		assert (featureCollection != null);
		Style style = this.style;
		if (style == null) {
			SimpleFeatureType schema = featureCollection.getSchema();
			if (SLDs.isLine(schema)) {
				style = SLD.createLineStyle(Color.red, 1);
			} else if (SLDs.isPoint(schema)) {
				style = SLD.createPointStyle("Circle", Color.red, Color.green, 1f, 3f);
			} else if (SLDs.isPolygon(schema)) {
				style = SLD.createPolygonStyle(Color.red, Color.green, 1f);
			}
		}
		this.style = style;
		return style;
	}

	/**
	 * Static convenience method: displays a {@code JSimpleStyleDialog} to
	 * prompt the user for style preferences to use with the given
	 * {@code StyleLayer}. The layer's existing style, if any, will be used to
	 * initialize the dialog.
	 *
	 * @param parent
	 *            parent component (may be null)
	 * @param layer
	 *            the map layer
	 *
	 * @return a new Style instance or null if the user cancels the dialog
	 */
	public static Style showDialog(StyleLayer layer) {
		/*
		 * Grid coverages and readers are not supported yet...
		 */
		if (layer instanceof RasterLayer) {
			JOptionPane.showMessageDialog(null, "Sorry, styling for for grid coverages is not working yet",
					"Style dialog", JOptionPane.WARNING_MESSAGE);

			return null;
		}

		SimpleFeatureType type = (SimpleFeatureType) layer.getFeatureSource().getSchema();
		return showDialog(type, layer.getStyle());
	}

	/**
	 * Static convenience method: displays a {@code JSimpleStyleDialog} to
	 * prompt the user for style preferences to use with the first feature type
	 * in the {@code dataStore}.
	 *
	 * @param parent
	 *            parent JFrame (may be null)
	 * @param dataStore
	 *            data store with the features to be rendered
	 *
	 * @return a new Style instance or null if the user cancels the dialog
	 */
	public static Style showDialog(DataStore dataStore) {
		return showDialog(dataStore, (Style) null);
	}

	/**
	 * Static convenience method: displays a {@code JSimpleStyleDialog} to
	 * prompt the user for style preferences to use with the first feature type
	 * in the {@code dataStore}.
	 *
	 * @param parent
	 *            parent JFrame (may be null)
	 * @param dataStore
	 *            data store with the features to be rendered
	 * @param initialStyle
	 *            an optional Style object to initialize the dialog (may be
	 *            {@code null})
	 *
	 * @return a new Style instance or null if the user cancels the dialog
	 */
	public static Style showDialog(DataStore dataStore, Style initialStyle) {
		SimpleFeatureType type = null;
		try {
			String typeName = dataStore.getTypeNames()[0];
			type = dataStore.getSchema(typeName);

		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}

		return showDialog(type, initialStyle);
	}

	/**
	 * Static convenience method: displays a {@code JSimpleStyleDialog} to
	 * prompt the user for style preferences to use with the given feature type.
	 *
	 * @param parent
	 *            parent component (may be null)
	 * @param featureType
	 *            the feature type that the Style will be used to display
	 *
	 * @return a new Style instance or null if the user cancels the dialog
	 */
	public static Style showDialog(SimpleFeatureType featureType) {
		return showDialog(featureType, (Style) null);
	}

	/**
	 * Static convenience method: displays a {@code JSimpleStyleDialog} to
	 * prompt the user for style preferences to use with the given feature type.
	 *
	 * @param parent
	 *            parent component (may be null)
	 * @param featureType
	 *            the feature type that the Style will be used to display
	 * @param initialStyle
	 *            an optional Style object to initialize the dialog (may be
	 *            {@code null})
	 *
	 * @return a new Style instance or null if the user cancels the dialog
	 */
	public static Style showDialog(SimpleFeatureType featureType, Style initialStyle) {

		Style style = null;
		SimpleStyleDialog dialog = null;

		dialog = new SimpleStyleDialog(featureType, initialStyle);

		dialog.show();

		if (dialog.completed()) {
			switch (dialog.getGeomType()) {
			case POLYGON:
			case MULTIPOLYGON:
				style = SLD.createPolygonStyle(dialog.getLineColor(), dialog.getFillColor(), dialog.getOpacity(),
						dialog.getLabelField(), dialog.getLabelFont());
				break;

			case LINESTRING:
			case MULTILINESTRING:
				style = SLD.createLineStyle(dialog.getLineColor(), dialog.getLineWidth(), dialog.getLabelField(),
						dialog.getLabelFont());
				break;

			case POINT:
			case MULTIPOINT:
				style = SLD.createPointStyle(dialog.getPointSymbolName(), dialog.getLineColor(), dialog.getFillColor(),
						dialog.getOpacity(), dialog.getPointSize(), dialog.getLabelField(), dialog.getLabelFont());
				break;
			default:
				break;
			}
		}

		return style;
	}

	/**
	 * Constructor.
	 *
	 * @param owner
	 *            the parent Frame (may be null)
	 * @param schema
	 *            the feature type for which the style is being created
	 * @param initialStyle
	 *            an optional Style object to initialize the dialog (may be
	 *            {@code null})
	 *
	 * @throws IllegalStateException
	 *             if the data store cannot be accessed
	 */
	public SimpleStyleDialog(SimpleFeatureType schema, Style initialStyle) {
		super();
		setResizable(false);
		this.schema = schema;
		init(initialStyle);
	}

	/**
	 * Helper for constructors
	 *
	 * @param initialStyle
	 *            an optional Style object to initialize the dialog (may be
	 *            {@code null})
	 */
	private void init(Style initialStyle) {

		lineColor = DEFAULT_LINE_COLOR;
		fillColor = DEFAULT_FILL_COLOR;
		lineWidth = DEFAULT_LINE_WIDTH;
		opacity = DEFAULT_OPACITY;
		pointSize = DEFAULT_POINT_SIZE;
		pointSymbolName = DEFAULT_POINT_SYMBOL_NAME;
		labelFeatures = false;
		labelField = null;
		labelFont = sf.getDefaultFont();

		geomType = null;
		completed = false;

		try {
			initComponents();
			setType();
			setStyle(initialStyle);

		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Query if the dialog was completed (user clicked the Apply button)
	 * 
	 * @return true if completed; false otherwise
	 */
	public boolean completed() {
		return completed;
	}

	/**
	 * Gets the geometry type of the selected feature type. Returns {@code null}
	 * if the user cancelled the dialog.
	 *
	 * @return the geometry type
	 */
	public Geometries getGeomType() {
		return geomType;
	}

	/**
	 * Get the selected line color
	 *
	 * @return line color
	 */
	public Color getLineColor() {
		return lineColor;
	}

	/**
	 * Get the selected fill color
	 *
	 * @return fill color
	 */
	public Color getFillColor() {
		return fillColor;
	}

	/**
	 * Get the fill opacity
	 *
	 * @return fill opacity between 0 and 1
	 */
	public float getOpacity() {
		return opacity;
	}

	/**
	 * Get the selected line width
	 *
	 * @return line width
	 */
	public float getLineWidth() {
		return lineWidth;
	}

	/**
	 * Get the selected point size
	 *
	 * @return point size
	 */
	public float getPointSize() {
		return pointSize;
	}

	/**
	 * Get the selected point symbol name
	 *
	 * @return symbol name
	 */
	public String getPointSymbolName() {
		return pointSymbolName;
	}

	/**
	 * Get the feature field (attribute) to use for labels
	 *
	 * @return field name
	 */
	public String getLabelField() {
		if (labelFeatures) {
			return labelField;
		}

		return null;
	}

	/**
	 * Get the font to use for labels
	 *
	 * @return a GeoTools Font object
	 */
	public Font getLabelFont() {
		return labelFont;
	}

	private StylePanel stylePanel;

	/**
	 * 初始化控件的操作
	 */
	public void initComponents() {
		this.setTitle("图层属性设计");

		final DialogPane dialogPane = getDialogPane();

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		stylePanel = new StylePanel();
		dialogPane.setContent(stylePanel);
	}

	/**
	 * Set up the dialog to work with a given feature type
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setType() {

		GeometryDescriptor desc = schema.getGeometryDescriptor();
		Class<? extends Geometry> clazz = (Class<? extends Geometry>) desc.getType().getBinding();
		geomType = Geometries.getForBinding(clazz);

		String labelText = schema.getTypeName();

		switch (geomType) {
		case POLYGON:
		case MULTIPOLYGON:
			labelText = labelText + " (polygon)";
			break;

		case LINESTRING:
		case MULTILINESTRING:
			labelText = labelText + " (line)";
			break;

		case POINT:
		case MULTIPOINT:
			labelText = labelText + " (point)";
			break;

		default:
			throw new UnsupportedOperationException("No style method for " + clazz.getName());
		}

		stylePanel.labelFeature.setText(labelText);

		// enable relevant components
		for (Component c : controls.keySet()) {
			switch (controls.get(c)) {
			case LINE:
				// nothing to do at present
				break;

			case FILL:
				c.setEnabled(geomType != Geometries.LINESTRING && geomType != Geometries.MULTILINESTRING);
				break;

			case POINT:
				c.setEnabled(geomType == Geometries.POINT || geomType == Geometries.MULTIPOINT);
				break;
			}
		}

		// set the fields available for labels
		fieldsForLabels = new String[schema.getAttributeCount() - 1];

		int k = 0;
		for (AttributeDescriptor attr : schema.getAttributeDescriptors()) {
			if (Geometry.class.isAssignableFrom(attr.getType().getBinding())) {
				continue;
			}

			fieldsForLabels[k++] = attr.getLocalName();
		}
		stylePanel.ddlbIcon.getItems().addAll(fieldsForLabels);
	}

	/**
	 * Set dialog items to show the contents of the given style
	 *
	 * @param style
	 *            style to display
	 */
	private void setStyle(Style style) {
		FeatureTypeStyle featureTypeStyle = null;
		Rule rule = null;
		Symbolizer symbolizer = null;

		if (style != null) {
			featureTypeStyle = SLD.featureTypeStyle(style, schema);

			if (featureTypeStyle != null) {
				/*
				 * At present this dialog just examines the very first rule and
				 * symbolizer
				 */
				if (featureTypeStyle.rules() == null || featureTypeStyle.rules().isEmpty()) {
					return;
				}
				rule = featureTypeStyle.rules().get(0);

				if (rule.symbolizers() == null) {
					return;
				}
				for (Symbolizer sym : rule.symbolizers()) {
					if (isValidSymbolizer(sym, geomType)) {
						symbolizer = sym;
						break;
					}
				}
				if (symbolizer == null) {
					return;
				}

			} else {
				/*
				 * Just grap the first feature type style that contains the
				 * right sort of symbolizer
				 */
				for (int ifts = 0; featureTypeStyle == null && ifts < style.featureTypeStyles().size(); ifts++) {
					FeatureTypeStyle fts = style.featureTypeStyles().get(ifts);
					for (int irule = 0; featureTypeStyle == null && irule < fts.rules().size(); irule++) {
						Rule r = fts.rules().get(irule);
						for (Symbolizer sym : r.symbolizers()) {
							if (isValidSymbolizer(sym, geomType)) {
								featureTypeStyle = fts;
								rule = r;
								symbolizer = sym;
								break;
							}
						}
					}
				}
			}

			if (featureTypeStyle != null && rule != null && symbolizer != null) {
				initControls(featureTypeStyle, rule, symbolizer);
			}
		}
	}

	/**
	 * Initialize the control states based on the given style objects
	 *
	 * @param fts
	 *            a {@code FeatureTypeStyle}
	 * @param rule
	 *            a {@code Rule}
	 * @param sym
	 *            a {@code Symbolizer}
	 */
	private void initControls(FeatureTypeStyle fts, Rule rule, Symbolizer sym) {
		// Expression exp = null;

		switch (geomType) {
		case POLYGON:
		case MULTIPOLYGON:
			PolygonSymbolizer polySym = (PolygonSymbolizer) sym;
			setLineColorItems(SLD.color(polySym.getStroke()));
			setFillColorItems(SLD.color(polySym.getFill()));
			setFillOpacityItems(SLD.opacity(polySym.getFill()));
			break;

		case LINESTRING:
		case MULTILINESTRING:
			LineSymbolizer lineSym = (LineSymbolizer) sym;
			setLineColorItems(SLD.color(lineSym));
			break;

		case POINT:
		case MULTIPOINT:
			PointSymbolizer pointSym = (PointSymbolizer) sym;
			setLineColorItems(SLD.pointColor(pointSym));
			setFillColorItems(SLD.pointFill(pointSym));
			setFillOpacityItems(SLD.pointOpacity(pointSym));
			setPointSizeItems(SLD.pointSize(pointSym));
			setPointSymbolItems(SLD.pointWellKnownName(pointSym));
		default:
			break;
		}
	}

	private boolean isValidSymbolizer(Symbolizer sym, Geometries type) {
		if (sym != null) {
			if (sym instanceof PolygonSymbolizer) {
				return type == Geometries.POLYGON || type == Geometries.MULTIPOLYGON;
			} else if (sym instanceof LineSymbolizer) {
				return type == Geometries.LINESTRING || type == Geometries.MULTILINESTRING;
			} else if (sym instanceof PointSymbolizer) {
				return type == Geometries.POINT || type == Geometries.MULTIPOINT;
			}
		}

		return false;
	}

	/**
	 * Display a color chooser dialog to set the line color
	 */
	private void chooseLineColor() {
//		Color color = JColorChooser.showDialog(this, "Choose line color", lineColor);
//		setLineColorItems(color);
	}

	/**
	 * Set the line color items to show the given color choice
	 * 
	 * @param color
	 *            current color
	 */
	private void setLineColorItems(Color color) {
		if (color != null) {
			lineColor = color;
//			lineColorIcon.setColor(color);
//			lineColorLabel.repaint();
		}
	}

	/**
	 * Display a color chooser dialog to set the fill color
	 */
	private void chooseFillColor() {
//		Color color = JColorChooser.showDialog(this, "Choose fill color", fillColor);
//		setFillColorItems(color);
	}

	/**
	 * Set the fill color items to show the given color choice
	 * 
	 * @param color
	 *            current color
	 */
	private void setFillColorItems(Color color) {
		if (color != null) {
			fillColor = color;
//			fillColorIcon.setColor(color);
//			fillColorLabel.repaint();
		}
	}

	/**
	 * Set the fill opacity items to the given value
	 * 
	 * @param value
	 *            opacity value between 0 and 1
	 */
	private void setFillOpacityItems(double value) {
		opacity = (float) Math.min(1.0, Math.max(0.0, value));
//		fillOpacitySlider.setValue((int) (opacity * 100));
	}

	/**
	 * Set items for the given point size
	 *
	 * @param value
	 *            point size
	 */
	@SuppressWarnings("unchecked")
	private void setPointSizeItems(double value) {
//		pointSize = (float) Math.max(0.0, value);
//		int newValue = (int) pointSize;
//
//		@SuppressWarnings("rawtypes")
//		MutableComboBoxModel model = (MutableComboBoxModel) pointSizeCBox.getModel();
//		int insert = -1;
//		for (int i = 0; i < model.getSize(); i++) {
//			int elValue = ((Number) model.getElementAt(i)).intValue();
//			if (elValue == newValue) {
////				pointSizeCBox.setSelectedIndex(i);
//				return;
//
//			} else if (elValue > newValue) {
//				insert = i;
//				break;
//			}
//		}
//
//		if (insert < 0) {
//			insert = model.getSize();
//			model.addElement(Integer.valueOf(newValue));
//		} else {
//			model.insertElementAt(Integer.valueOf(newValue), insert);
//		}
//		pointSizeCBox.setSelectedIndex(insert);
	}

	/**
	 * Set items for the given point symbol, identified by its 'well known name'
	 *
	 * @param wellKnownName
	 *            name of the symbol
	 */
	private void setPointSymbolItems(String wellKnownName) {
		if (wellKnownName != null) {
			for (int i = 0; i < WELL_KNOWN_SYMBOL_NAMES.length; i++) {
				if (WELL_KNOWN_SYMBOL_NAMES[i].equalsIgnoreCase(wellKnownName)) {
					pointSymbolName = WELL_KNOWN_SYMBOL_NAMES[i];
//					pointSymbolCBox.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	private void chooseLabelFont() {
//		Font font = JFontChooser.showDialog(this, "Choose label font", labelFont);
//		if (font != null) {
//			labelFont = font;
//		}
	}

	/**
	 * 样式设置panel
	 * 
	 * @author sam
	 *
	 */
	private static class StylePanel extends AnchorPane {

		/**
		 * 样式设置panel
		 */
		public StylePanel() {
			this.initCompents();
		}

		/**
		 * 图层类型
		 */
		private Label labelFeature;

		/**
		 * 是否显示边框
		 */
		private CheckBox cbkLine;

		/**
		 * 边框线颜色
		 */
		private ColorPicker colorLine;

		/**
		 * 线条宽度
		 */
		private Spinner<Integer> spinnerLine;

		/**
		 * 是否填充
		 */
		private CheckBox cbkFill;

		/**
		 * 填充颜色
		 */
		private ColorPicker colorFill;

		/**
		 * 填充透明度
		 */
		private Slider sliderFill;

		/**
		 * 是否填充
		 */
		private CheckBox cbkMarket;

		/**
		 * 样式
		 */
		private ComboBox<String> ddlbIcon;

		/**
		 * 标记的大小
		 */
		private Spinner<Integer> spinnerMarkSize;

		/**
		 * 标签字体按钮
		 */
		private Button fontButton;

		/**
		 * 标签复选框
		 */
		private CheckBox cbxFont;

		/**
		 * 初始化显示控件
		 */
		private void initCompents() {

			this.setPrefSize(480, 320); // 最小大小

			// 坐标的基准点
			final double BASE_X = 7.0;
			final double BASE_Y = 14.0;
			final double X_2 = 39.0;
			final double X_3 = 93.0;
			final double X_4 = 175.0;

			// 和线条有关的操作
			Label lableStyle = new Label("图元类型");
			lableStyle.setLayoutX(BASE_X);
			lableStyle.setLayoutY(BASE_Y);
			this.getChildren().add(lableStyle);

			// 图层图元对象的类型
			labelFeature = new Label();
			labelFeature.setLayoutX(X_2);
			labelFeature.setLayoutY(BASE_Y);
			this.getChildren().add(labelFeature);

			Label lableLine = new Label("边框");
			lableLine.setLayoutX(lableStyle.getLayoutX());
			lableLine.setLayoutY(45.0);
			this.getChildren().add(lableLine);

			cbkLine = new CheckBox("显示");
			cbkLine.setMnemonicParsing(false);
			cbkLine.setLayoutX(X_2);
			cbkLine.setLayoutY(lableLine.getLayoutY() - 5);
			cbkLine.setPrefSize(76.0, 23.0);
			this.getChildren().add(cbkLine);

			colorLine = new ColorPicker();
			colorLine.setLayoutX(X_3);
			colorLine.setLayoutY(lableLine.getLayoutY() - 5);
			colorLine.setPrefSize(120.0, 23.0);
			this.getChildren().add(colorLine);

			Label lableLineWidth = new Label("线宽");
			lableLineWidth.setLayoutX(215.0);
			lableLineWidth.setLayoutY(lableLine.getLayoutY());
			this.getChildren().add(lableLineWidth);

			spinnerLine = new Spinner<>(1, 10, 1);
			spinnerLine.setLayoutX(250.0);
			spinnerLine.setLayoutY(lableLine.getLayoutY() - 5);
			spinnerLine.setPrefSize(76.0, 23.0);
			this.getChildren().add(spinnerLine);

			// 和填充有关的操作
			Label lableFill = new Label("填充");
			lableFill.setLayoutX(lableStyle.getLayoutX());
			lableFill.setLayoutY(75.0);
			this.getChildren().add(lableFill);

			cbkFill = new CheckBox("显示");
			cbkFill.setMnemonicParsing(false);
			cbkFill.setLayoutX(X_2);
			cbkFill.setLayoutY(lableFill.getLayoutY() - 5);
			cbkFill.setPrefSize(76.0, 23.0);
			this.getChildren().add(cbkFill);

			colorFill = new ColorPicker();
			colorFill.setLayoutX(X_3);
			colorFill.setLayoutY(lableFill.getLayoutY() - 5);
			colorFill.setPrefSize(120.0, 23.0);
			this.getChildren().add(colorFill);

			Label lableFillTrans = new Label("透明度");
			lableFillTrans.setLayoutX(215.0);
			lableFillTrans.setLayoutY(lableFill.getLayoutY());
			this.getChildren().add(lableFillTrans);

			sliderFill = new Slider(0, 100, 50);
			sliderFill.setLayoutX(250.0);
			sliderFill.setLayoutY(lableFill.getLayoutY());
			sliderFill.setPrefWidth(220.0);
			sliderFill.setShowTickLabels(true);
			sliderFill.setShowTickMarks(true);
			this.getChildren().add(sliderFill);

			// 一下是图标的地方
			Label lableMark = new Label("标记");
			lableMark.setLayoutX(lableStyle.getLayoutX());
			lableMark.setLayoutY(105.0);
			this.getChildren().add(lableMark);

			cbkMarket = new CheckBox("显示");
			cbkMarket.setMnemonicParsing(false);
			cbkMarket.setLayoutX(X_2);
			cbkMarket.setLayoutY(lableMark.getLayoutY() - 5);
			cbkMarket.setPrefSize(76.0, 23.0);
			this.getChildren().add(cbkMarket);

			spinnerMarkSize = new Spinner<>(1, 20, 1);
			spinnerMarkSize.setLayoutX(X_3);
			spinnerMarkSize.setLayoutY(lableMark.getLayoutY() - 5);
			spinnerMarkSize.setPrefSize(76.0, 23.0);
			this.getChildren().add(spinnerMarkSize);

			ddlbIcon = new ComboBox<>();
			ddlbIcon.setLayoutX(X_4);
			ddlbIcon.setLayoutY(lableMark.getLayoutY() - 5);
			ddlbIcon.setPrefSize(76.0, 23.0);
			this.getChildren().add(ddlbIcon);

			// 一下是字体设计
			Label lableFont = new Label("标签");
			lableFont.setLayoutX(BASE_X);
			lableFont.setLayoutY(135.0);
			this.getChildren().add(lableFont);

			cbxFont = new CheckBox("显示");
			cbxFont.setMnemonicParsing(false);
			cbxFont.setLayoutX(X_2);
			cbxFont.setLayoutY(lableFont.getLayoutY() - 5);
			cbxFont.setPrefSize(76.0, 23.0);
			this.getChildren().add(cbxFont);

			fontButton = new Button();
			fontButton.setLayoutX(X_3);
			fontButton.setLayoutY(lableFont.getLayoutY() - 5);
			fontButton.setPrefSize(120.0, 23.0);
			this.getChildren().add(fontButton);
		}
	}
}
