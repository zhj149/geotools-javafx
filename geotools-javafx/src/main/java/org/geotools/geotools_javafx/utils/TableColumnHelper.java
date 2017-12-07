package org.geotools.geotools_javafx.utils;

import javafx.beans.property.Property; 
import javafx.beans.value.ChangeListener; 
import javafx.beans.value.ObservableValue; 
import javafx.event.EventHandler; 
import javafx.scene.control.OverrunStyle; 
import javafx.scene.control.TableCell; 
import javafx.scene.control.TableColumn; 
import javafx.scene.control.TableColumn.CellDataFeatures; 
import javafx.scene.control.TableColumn.CellEditEvent; 
import javafx.scene.control.TextField; 
import javafx.scene.control.cell.CheckBoxTableCell; 
import javafx.scene.control.cell.TextFieldTableCell; 
import javafx.util.Callback; 
import javafx.util.StringConverter; 
 
/**
 * javafx table工具类
 * 抄袭的
 * @author sam
 *
 */
public class TableColumnHelper {
 
    public static interface ColumnStringAccessor<T> { 
        public Property<String> getProperty(T row); 
    } 
 
    public static interface ColumnBooleanAccessor<T> { 
        public Property<Boolean> getProperty(T row); 
    } 
 
    public static <T, U> void setConverterCellFactory(TableColumn<T, U> column, final StringConverter<U> converter) { 
        column.setCellFactory(new Callback<TableColumn<T, U>, TableCell<T, U>>() { 
            @Override 
            public TableCell<T, U> call(TableColumn<T, U> param) { 
                return new TextFieldTableCell<T, U>(converter); 
            } 
        }); 
    } 
 
    public static <T, U> void setTextOverrunCellFactory(TableColumn<T, U> column, final OverrunStyle overrunStyle) { 
        column.setCellFactory(new Callback<TableColumn<T, U>, TableCell<T, U>>() { 
            @Override 
            public TableCell<T, U> call(TableColumn<T, U> param) { 
                TextFieldTableCell<T, U> cell = new TextFieldTableCell<T, U>(); 
                cell.setTextOverrun(overrunStyle); 
                return cell; 
            } 
        }); 
    } 
 
    /**
     * T Tableobject 
     * Column Display Type 
     */ 
    public static <T> void setupEditableStringColumn(TableColumn<T, String> column, final ColumnStringAccessor<T> propertyAccessor) { 
        column.getTableView().setEditable(true); 
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>>() { 
            @Override 
            public ObservableValue<String> call(CellDataFeatures<T, String> param) { 
                return propertyAccessor.getProperty(param.getValue()); 
            } 
        }); 
        column.setEditable(true); 
        column.setOnEditCommit(new EventHandler<CellEditEvent<T, String>>() { 
            @Override 
            public void handle(CellEditEvent<T, String> t) { 
                propertyAccessor.getProperty(t.getRowValue()).setValue(t.getNewValue()); 
            } 
        }); 
        column.setCellFactory(new Callback<TableColumn<T, String>, TableCell<T, String>>() { 
            @Override 
            public TableCell<T, String> call(TableColumn<T, String> param) { 
                return new EditingCell<T>(); 
            } 
        }); 
    } 
 
    public static <T> void setupEditableBooleanColumn(TableColumn<T, Boolean> column, final ColumnBooleanAccessor<T> propertyAccessor) { 
        column.getTableView().setEditable(true); 
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<T, Boolean>, ObservableValue<Boolean>>() { 
            @Override 
            public ObservableValue<Boolean> call(CellDataFeatures<T, Boolean> param) { 
                return propertyAccessor.getProperty(param.getValue()); 
            } 
        }); 
        column.setOnEditCommit(new EventHandler<CellEditEvent<T, Boolean>>() { 
            @Override 
            public void handle(CellEditEvent<T, Boolean> t) { 
                propertyAccessor.getProperty(t.getRowValue()).setValue(t.getNewValue()); 
            } 
        }); 
        column.setCellFactory(CheckBoxTableCell.forTableColumn(column)); 
        column.setEditable(true); 
    } 
 
    public static class EditingCell<T> extends TableCell<T, String> { 
 
        private TextField textField; 
 
        public EditingCell() { 
        } 
 
        @Override 
        public void startEdit() { 
            if (!isEmpty()) { 
                super.startEdit(); 
                createTextField(); 
                setText(null); 
                setGraphic(textField); 
                textField.selectAll(); 
            } 
        } 
 
        @Override 
        public void cancelEdit() { 
            super.cancelEdit(); 
 
            setText(getItem()); 
            setGraphic(null); 
        } 
 
        @Override 
        public void updateItem(String item, boolean empty) { 
            super.updateItem(item, empty); 
 
            if (empty) { 
                setText(null); 
                setGraphic(null); 
            } else { 
                if (isEditing()) { 
                    if (textField != null) { 
                        textField.setText(getString()); 
                    } 
                    setText(null); 
                    setGraphic(textField); 
                } else { 
                    setText(getString()); 
                    setGraphic(null); 
                } 
            } 
        } 
 
        private void createTextField() { 
            textField = new TextField(getString()); 
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2); 
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() { 
                @Override 
                public void changed(ObservableValue<? extends Boolean> arg0, 
                        Boolean arg1, Boolean arg2) { 
                    if (!arg2) { 
                        commitEdit(textField.getText()); 
                    } 
                } 
            }); 
        } 
 
        private String getString() { 
            return getItem() == null ? "" : getItem(); 
        } 
    } 
 
}
