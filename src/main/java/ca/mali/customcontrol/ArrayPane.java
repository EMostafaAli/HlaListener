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
    private Button RemoveRow;

    public ArrayPane() {
        AddRow = new Button();
        AddRow.setTooltip(new Tooltip("Add row"));
        ImageView AddRowIV = new ImageView();
        AddRowIV.setImage(new Image(getClass().getResourceAsStream("/icons/AddRow.png")));
        AddRowIV.setFitWidth(16);
        AddRowIV.setPreserveRatio(true);
        AddRowIV.setSmooth(true);
        AddRowIV.setCache(true);
        AddRow.setGraphic(AddRowIV);

        RemoveRow = new Button();
        RemoveRow.setTooltip(new Tooltip("Delete row"));
        ImageView RemoveRowIV = new ImageView(new Image(getClass().getResourceAsStream("/icons/DeleteRow.png")));
        RemoveRowIV.setFitWidth(16);
        RemoveRowIV.setPreserveRatio(true);
        RemoveRowIV.setSmooth(true);
        RemoveRowIV.setCache(true);
        RemoveRow.setGraphic(RemoveRowIV);

        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.getChildren().addAll(AddRow, RemoveRow);
        setTop(hbox);
    }

    public void setAddRowDisable(boolean value) {
        AddRow.setDisable(value);
    }

    public void setRemoveRowDisable(boolean value) {
        RemoveRow.setDisable(value);
    }

    public Button getAddRow() {
        return AddRow;
    }

    public Button getRemoveRow() {
        return RemoveRow;
    }
}
