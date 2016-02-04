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
package ca.mali.fomparser.datatype;

import ca.mali.customcontrol.ArrayPane;
import ca.mali.fomparser.DataTypeEnum;
import hla.rti1516e.encoding.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ca.mali.hlalistener.PublicVariables.encoderFactory;

/**
 * @author Mostafa
 */
public class ArrayFDD extends AbstractDataType {

    //Logger
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    private AbstractDataType elementType;
    private String encoding;
    private String cardinality;
    private String semantics;
    private Object value;
    TextField textField;
    private ArrayPane arrayPane = new ArrayPane();
    List<List<AbstractDataType>> arrayElements = new ArrayList<>();

    private int[] lowerLimit = new int[]{0, 0}; //we will just support 2 dimensional array
    private int[] upperLimit = new int[]{0, 0}; //we will just support 2 dimensional array

    public ArrayFDD(String name) {
        super(name, DataTypeEnum.ARRAY);
        getControl(true);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ArrayFDD cloned = (ArrayFDD) super.clone();
        cloned.setElementType((AbstractDataType) cloned.getElementType().clone());
        cloned.getControl(true);
        return cloned;
    }

    @Override
    public byte[] EncodeValue() {
        byte[] encodedValue;
        switch (getName()) {
            case "HLAASCIIstring": {
                HLAASCIIstring encoder = encoderFactory.createHLAASCIIstring();
                if (value != null)
                    encoder.setValue((String) value);
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAunicodeString": {
                HLAunicodeString encoder = encoderFactory.createHLAunicodeString();
                if (value != null)
                    encoder.setValue((String) value);
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAopaqueData": {
                HLAopaqueData encoder = encoderFactory.createHLAopaqueData();
                if (value != null)
                    encoder.setValue((byte[]) value);
                encodedValue = encoder.toByteArray();
                break;
            }
            default: {
                encodedValue = getDataElement().toByteArray();
                break;
            }
        }
        return encodedValue;
    }

    @Override
    public String DecodeValue(byte[] encodedValue) {
        String value = "";
        try {
            switch (getName()) {
                case "HLAASCIIstring": {
                    HLAASCIIstring encoder = encoderFactory.createHLAASCIIstring();
                    encoder.decode(encodedValue);
                    value = encoder.getValue();
                    break;
                }
                case "HLAunicodeString": {
                    HLAunicodeString encoder = encoderFactory.createHLAunicodeString();
                    encoder.decode(encodedValue);
                    value = encoder.getValue();
                    break;
                }
                case "HLAopaqueData": {
                    HLAopaqueData encoder = encoderFactory.createHLAopaqueData();
                    encoder.decode(encodedValue);
                    value = Arrays.toString(encoder.getValue());
                    break;
                }
                default: {
                    DataElementFactory<DataElement> dataElementFactory = i -> getElementType().getDataElement();
                    if ("Dynamic".equalsIgnoreCase(getCardinality())){
                        HLAvariableArray<DataElement> hlAvariableArray = encoderFactory.createHLAvariableArray(dataElementFactory);
                        hlAvariableArray.decode(encodedValue);
                        value = "[";
                        for (DataElement dataElement : hlAvariableArray) {
                            value += getElementType().DecodeValue(dataElement.toByteArray()) + ", ";
                        }
                        value = value.substring(0, value.length() - 2) + "]";
                    }
                    else {
                        // TODO: 12/16/2015 decode fixed arrays
                    }
                }
            }
        } catch (DecoderException ex) {
            logger.log(Level.ERROR, "Error in decoding value", ex);
        }
        return value;
    }

    @Override
    public DataElement getDataElement() {
        switch (getName()) {
            case "HLAASCIIstring": {
                HLAASCIIstring encoder = encoderFactory.createHLAASCIIstring();
                if (value != null)
                    encoder.setValue((String) value);
                return encoder;
            }
            case "HLAunicodeString": {
                HLAunicodeString encoder = encoderFactory.createHLAunicodeString();
                if (value != null)
                    encoder.setValue((String) value);
                return encoder;
            }
            case "HLAopaqueData": {
                HLAopaqueData encoder = encoderFactory.createHLAopaqueData();
                if (value != null)
                    encoder.setValue((byte[]) value);
                return encoder;
            }
            default: {

                DataElementFactory<DataElement> dataElementFactory = i -> getElementType().getDataElement();
                if ("Dynamic".equalsIgnoreCase(getCardinality())){
                    HLAvariableArray<DataElement> hlAvariableArray = encoderFactory.createHLAvariableArray(dataElementFactory);
                    for (List<AbstractDataType> arrayElement : arrayElements) {
                        hlAvariableArray.addElement(arrayElement.get(0).getDataElement()); // TODO: 2016-02-03 for now, I am considering first dimension only
                    }
                    return hlAvariableArray;
                }
                DataElement[] elements = new DataElement[arrayElements.size()];
                for (int i = 0; i < arrayElements.size(); i++) {
                    elements[i] = arrayElements.get(i).get(0).getDataElement(); // TODO: 2016-02-03 for now, I am considering first dimension only
                }
                return encoderFactory.createHLAfixedArray(elements);
            }
        }
    }

    @Override
    public Region getControl(boolean reset) {
        if ("HLAtoken".equalsIgnoreCase(getName()))
            return null; // TODO: 1/30/2016 I don't understand this attribute "HLAprivilegeToDeleteObject" very well
        if ("HLAASCIIstring".equalsIgnoreCase(getName()) || "HLAunicodeString".equalsIgnoreCase(getName())) {
            if (reset) {
                this.value = null;
                textField = new TextField();
                textField.textProperty().addListener((observable, oldValue, newValue) -> this.value = newValue);
            }
            return textField;
        } else {
            if (reset) {
                this.value = null;
                arrayPane = new ArrayPane();
                if (getCardinality() != null && !getCardinality().isEmpty()) {
                    addArrayElements();
                }
            }
            return arrayPane;
        }
    }

    @Override
    public boolean isValueExist() {
//        return "Employees".equalsIgnoreCase(getName());
        //        return value != null && (value.getClass().isArray() || value instanceof String);
        if (value != null && value instanceof String)
            return true;

        if (arrayElements.size() > 0){
            for (List<AbstractDataType> arrayElement : arrayElements) {
                if ( arrayElement.stream().anyMatch(AbstractDataType::isValueExist)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Class getObjectClass() {
        switch (getName()) {
            case "HLAASCIIstring": {
                return HLAASCIIstring.class;
            }
            case "HLAunicodeString": {
                return HLAunicodeString.class;
            }
            case "HLAopaqueData": {
                return HLAopaqueData.class;
            }
            default: {
                if ("Dynamic".equalsIgnoreCase(getCardinality())) {
                    return HLAvariableArray.class;
                }
                return HLAfixedArray.class;
            }
        }
    }

    @Override
    public String valueAsString() {
        if (value != null)
            return value.toString() + "<" + Arrays.toString(EncodeValue()) + ">";
        String result = "[";
        for (List<AbstractDataType> arrayElement : arrayElements) {
            result += "{";
            for (AbstractDataType element : arrayElement) {
                result += element.valueAsString() + ", ";
            }
            result = result.substring(0, result.length() - 2) + "}, ";
        }
        result = result.substring(0, result.length() - 2) + "]";
        return result;
    }

    public AbstractDataType getElementType() {
        return elementType;
    }

    public void setElementType(AbstractDataType elementType) {
        this.elementType = elementType;
        if (this.elementType != null && !"HLAASCIIstring".equalsIgnoreCase(getName()) && !"HLAunicodeString".equalsIgnoreCase(getName())) {
            addArrayElements();
        }
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getCardinality() {
        return cardinality;
    }

    public void setCardinality(String cardinality) {
        this.cardinality = cardinality;
        if (!"HLAASCIIstring".equalsIgnoreCase(getName()) && !"HLAunicodeString".equalsIgnoreCase(getName())) {
            if (this.cardinality.contains("Dynamic")) {
                for (int i = 0; i < upperLimit.length; i++) {
                    upperLimit[i] = Integer.MAX_VALUE;
                    lowerLimit[i] = 1;
                }
            } else {
                String[] parts = this.cardinality.split(",");
                if (parts.length > 2) {
                    logger.log(Level.WARN, "Array: {} has {} dimensions, only two dimensions will be displayed",
                            getName(), parts.length);
                }
                for (int i = 0; i < Math.min(2, parts.length); i++) {
                    if (parts[i].contains("..")) {
                        String s = parts[i].replace("..", ",").replace("[", "").replace("]", "").replace(" ", "");
                        String[] upperLowerLimit = s.split(",");
                        upperLimit[i] = Integer.parseInt(upperLowerLimit[0]);
                        lowerLimit[i] = Integer.parseInt(upperLowerLimit[1]);
                    } else {
                        int v = Integer.parseInt(parts[i]);
                        upperLimit[i] = v;
                        lowerLimit[i] = v;
                    }
                }
            }
        }
    }

    public String getSemantics() {
        return semantics;
    }

    public void setSemantics(String semantics) {
        this.semantics = semantics;
    }

    private void addArrayElements() {
        try {
            arrayElements.clear();
            GridPane gridPane = new GridPane();
            gridPane.setHgap(4);
            gridPane.setVgap(4);
            gridPane.setPadding(new Insets(4));
            ScrollPane scrollPane = new ScrollPane(gridPane);
            arrayPane.setCenter(scrollPane);

            if (upperLimit[0] == lowerLimit[0]) {
                arrayPane.setAddRowDisable(true);
                arrayPane.setRemoveRowDisable(true);
            }
            if (upperLimit[1] == lowerLimit[1]) {
                arrayPane.setAddColumnDisable(true);
                arrayPane.setRemoveColumnDisable(true);
            }
            if (cardinality.contains("Dynamic")) { //Start with 3x1 array
                for (int i = 0; i < 3; i++) {
                    List<AbstractDataType> row = new ArrayList<>();
                    row.add((AbstractDataType) getElementType().clone());
                    arrayElements.add(row);
                }
            } else {
                for (int i = 0; i < upperLimit[0]; i++) {
                    List<AbstractDataType> row = new ArrayList<>();
                    for (int j = 0; j < upperLimit[1] + 1; j++) {
                        row.add((AbstractDataType) getElementType().clone());
                    }
                    arrayElements.add(row);
                }
            }
            if (!arrayElements.isEmpty()) {
                for (int i = 0; i < arrayElements.get(0).size(); i++) {
                    Label colNum = new Label(String.valueOf(i + 1));
                    gridPane.add(colNum, i + 1, 0);
                    GridPane.setHalignment(colNum, HPos.CENTER);
                }
                for (int i = 0; i < arrayElements.size(); i++) {
                    Label rowNum = new Label(String.valueOf(i + 1));
                    gridPane.add(rowNum, 0, i + 1);
                    for (int j = 0; j < arrayElements.get(0).size(); j++) {
                        gridPane.add(arrayElements.get(i).get(j).getControl(true), j + 1, i + 1);
                    }
                }
            }

        } catch (CloneNotSupportedException ex) {
            logger.log(Level.ERROR, "Error in adding array elements", ex);
        }
    }
}
