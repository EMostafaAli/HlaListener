/*
 * Copyright (c) 2015, Mostafa
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met: Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer. Redistributions
 * in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 *  CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 *  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *   NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *   OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *   DAMAGE.
 */

package ca.mali.fomparser;

import javafx.scene.control.TableCell;
import javafx.scene.layout.Region;

public class AttributeValueCell extends TableCell<AttributeValuePair, Object> {

    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        AbstractValuePair valuePair = (AbstractValuePair) this.getTableRow().getItem();
        if (empty || valuePair == null) return;
        Region r = valuePair.cellGUI();
        if (r != null) {
            this.widthProperty().addListener((observable, oldValue, newValue) -> r.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2));
            setGraphic(r);
        }
        //Enumerated data type
//        if (fddObjectModel.getEnumeratedDataTypeMap().keySet().contains(valuePair.getDataType().getName())) {
//            ComboBox<String> values = new ComboBox<>();
//            this.widthProperty().addListener((observable, oldValue, newValue) -> values.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2));
//            values.getItems().addAll(fddObjectModel.getEnumeratedDataTypeMap().get(valuePair.getDataType())
//                    .getEnumerator().stream().map(a -> a.getName()).collect(Collectors.toList()));
//            values.setOnAction(event -> commitEdit(values.getSelectionModel().getSelectedItem()));
//            setGraphic(values);
//        } else if(fddObjectModel.getArrayDataTypeMap().keySet().contains(valuePair.getDataType())){ //Array data type
//            TextField textField = new TextField();
//            textField.setPromptText("Separate values with semicolon (;)");
//            this.widthProperty().addListener((observable, oldValue, newValue) -> textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2));
//            textField.textProperty().addListener((observable, oldValue, newValue) -> commitEdit(newValue));
//            setGraphic(textField);
//        } else { //Simple data type
//            TextField textField = new TextField();
//            this.widthProperty().addListener((observable, oldValue, newValue) -> textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2));
//            textField.textProperty().addListener((observable, oldValue, newValue) -> commitEdit(newValue));
//            setGraphic(textField);
//        }
    }
//
//    @Override
//    public void commitEdit(Object newValue) {
//        super.commitEdit(newValue);
//        ((AttribParamValuePair) this.getTableRow().getItem()).setValue(newValue);
//    }

}
