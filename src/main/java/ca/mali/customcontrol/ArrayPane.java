package ca.mali.customcontrol;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Created by Mostafa on 1/30/2016.
 */
public class ArrayPane extends BorderPane {
    private Button AddRow;
    private Button AddColumn;
    private Button RemoveRow;
    private Button RemoveColumn;

    public ArrayPane(){
        AddRow = new Button();
        AddRow.setTooltip(new Tooltip("Add row"));
        ImageView AddRowIV = new ImageView();
        AddRowIV.setImage(new Image(getClass().getResourceAsStream("/icons/AddRow.png")));
        AddRowIV.setFitWidth(16);
        AddRowIV.setPreserveRatio(true);
        AddRowIV.setSmooth(true);
        AddRowIV.setCache(true);
        AddRow.setGraphic(AddRowIV);

        AddColumn = new Button();
        AddColumn.setTooltip(new Tooltip("Add column"));
        ImageView AddColumnIV = new ImageView(new Image(getClass().getResourceAsStream("/icons/AddColumn.png")));
        AddColumnIV.setFitWidth(16);
        AddColumnIV.setPreserveRatio(true);
        AddColumnIV.setSmooth(true);
        AddColumnIV.setCache(true);
        AddColumn.setGraphic(AddColumnIV);

        RemoveRow = new Button();
        RemoveRow.setTooltip(new Tooltip("Delete row"));
        ImageView RemoveRowIV = new ImageView(new Image(getClass().getResourceAsStream("/icons/DeleteRow.png")));
        RemoveRowIV.setFitWidth(16);
        RemoveRowIV.setPreserveRatio(true);
        RemoveRowIV.setSmooth(true);
        RemoveRowIV.setCache(true);
        RemoveRow.setGraphic(RemoveRowIV);

        RemoveColumn = new Button();
        RemoveColumn.setTooltip(new Tooltip("Delete column"));
        ImageView RemoveColumnIV = new ImageView(new Image(getClass().getResourceAsStream("/icons/DeleteColumn.png")));
        RemoveColumnIV.setFitWidth(16);
        RemoveColumnIV.setPreserveRatio(true);
        RemoveColumnIV.setSmooth(true);
        RemoveColumnIV.setCache(true);
        RemoveColumn.setGraphic(RemoveColumnIV);

        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.getChildren().addAll(AddRow, AddColumn, RemoveRow, RemoveColumn);
        setTop(hbox);
    }

    public void setAddRowDisable(boolean value){
        AddRow.setDisable(value);
    }

    public void setAddColumnDisable(boolean value){
        AddColumn.setDisable(value);
    }

    public void setRemoveRowDisable(boolean value){
        RemoveRow.setDisable(value);
    }

    public void setRemoveColumnDisable(boolean value){
        RemoveColumn.setDisable(value);
    }
}
