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

import ca.mali.fomparser.datatype.AbstractDataType;
import ca.mali.fomparser.datatype.EnumeratedFDDDataType;
import ca.mali.fomparser.datatype.FixedRecordFDD;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Mostafa on 12/13/2015.
 */
public abstract class AbstractValuePair {
    private final String name;
    private ObjectProperty value = new SimpleObjectProperty<>();
    private final AbstractDataType dataType;

    protected AbstractValuePair(AttributeFDD attributeFDD) {
        this.name = attributeFDD.getName();
        this.dataType = attributeFDD.getDataType();
    }

    protected AbstractValuePair(ParameterFDD parameterFDD) {
        this.name = parameterFDD.getName();
        this.dataType = parameterFDD.getDataType();
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value.get();
    }

    public ObjectProperty valueProperty() {
        return value;
    }

    public void setValue(Object value) {
        this.value.set(value);
    }

    public AbstractDataType getDataType() {
        return dataType;
    }

    public Region cellGUI() {
        if (getDataType() == null) return null;
        switch (getDataType().getDataType()) {
            case SIMPLE: {
                TextField textField = new TextField();
                textField.textProperty().addListener((observable, oldValue, newValue) -> setValue(newValue));
                return textField;
            }
            case ENUMERATED: {
                ComboBox<String> values = new ComboBox<>();
                values.getItems().addAll(((EnumeratedFDDDataType) getDataType()).getEnumerator().stream().map(EnumeratedFDDDataType.Enumerator::getName).collect(Collectors.toList()));
                values.setOnAction(event -> setValue(values.getSelectionModel().getSelectedItem()));
                return values;
            }
            case ARRAY: {
                if (Objects.equals(getDataType().getName(), "HLAASCIIstring") || Objects.equals(getDataType().getName(), "HLAunicodeString")) {
                    TextField textField = new TextField();
                    textField.textProperty().addListener((observable, oldValue, newValue) -> setValue(newValue));
                    return textField;
                }
                return null; // TODO: 12/16/2015 GUI for array
            }
            case FIXEDRECORD: {// TODO: 12/16/2015 GUI for fixed record
                FixedRecordFDD fixedRecordFDD = (FixedRecordFDD) getDataType();
                Object[] values = new Object[fixedRecordFDD.getFields().size()];
                GridPane gridPane = new GridPane();
                gridPane.setHgap(5);
                gridPane.setVgap(5);
                gridPane.setPadding(new Insets(0, 5, 0, 5));
                for (int i = 0; i < values.length; i++) {
                    Text title = new Text(fixedRecordFDD.getFields().get(i).getName());
                    gridPane.add(title, i, 0);
                }
                ScrollPane scrollPane = new ScrollPane(gridPane);
                scrollPane.setFitToWidth(true);
                return scrollPane;
            }
            default:
                return null;
        }
    }

//    private Control getControl(AbstractDataType dataType, Object  relatedValue){
//        if (dataType == null) return null;
//        switch (dataType.getDataType()){
//            case SIMPLE:{
//                TextField textField = new TextField();
//                textField.textProperty().addListener((observable, oldValue, newValue) -> relatedValue = newValue);
//                return textField;
//            }
//
//        }
//        return null;
//    }

    public byte[] EncodeValue() {
        if (getValue() == null) return null;
        return getDataType().EncodeValue(getValue());
    }
}
