/*
 * Copyright (c) 2015-2016, Mostafa Ali (engabdomostafa@gmail.com)
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met: Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer. Redistributions
 * in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package ca.mali.customcontrol;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * @author Mostafa Ali <engabdomostafa@gmail.com>
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
