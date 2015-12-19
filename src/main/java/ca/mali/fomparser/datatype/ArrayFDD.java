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

import ca.mali.fomparser.ControlValuePair;
import ca.mali.fomparser.DataTypeEnum;
import hla.rti1516e.encoding.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;

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

    public ArrayFDD(String name) {
        super(name, DataTypeEnum.ARRAY);
    }

    @Override
    public byte[] EncodeValue(Object value) {
        byte[] encodedValue;
        switch (getName()) {
            case "HLAASCIIstring": {
                HLAASCIIstring encoder = encoderFactory.createHLAASCIIstring();
                encoder.setValue((String) value);
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAunicodeString": {
                HLAunicodeString encoder = encoderFactory.createHLAunicodeString();
                encoder.setValue((String) value);
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAopaqueData": {
                HLAopaqueData encoder = encoderFactory.createHLAopaqueData();
                encoder.setValue((byte[]) value);
                encodedValue = encoder.toByteArray();
                break;
            }
            default: { // TODO: 12/16/2015 cast the value to array
                return getElementType().EncodeValue(value);
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
                default: { // TODO: 12/16/2015 cast the value to array
                    value = getElementType().DecodeValue(encodedValue);
                }
            }
        } catch (DecoderException ex) {
            logger.log(Level.ERROR, "Error in decoding value", ex);
        }
        return value;
    }

    @Override
    public DataElement getDataElement(Object value) {
        switch (getName()) {
            case "HLAASCIIstring": {
                HLAASCIIstring encoder = encoderFactory.createHLAASCIIstring();
                encoder.setValue((String) value);
                return encoder;
            }
            case "HLAunicodeString": {
                HLAunicodeString encoder = encoderFactory.createHLAunicodeString();
                encoder.setValue((String) value);
                return encoder;
            }
            case "HLAopaqueData": {
                HLAopaqueData encoder = encoderFactory.createHLAopaqueData();
                encoder.setValue((byte[]) value);
                return encoder;
            }
            default: { // TODO: 12/16/2015 cast the value to array
                return getElementType().getDataElement(value);
            }
        }
    }

    @Override
    public ControlValuePair getControlValue() {
        if ("HLAASCIIstring".equalsIgnoreCase(getName()) || "HLAunicodeString".equalsIgnoreCase(getName())) {
            TextField textField = new TextField();
            ObjectProperty value = new SimpleObjectProperty<>();
            textField.textProperty().addListener((observable, oldValue, newValue) -> value.setValue(newValue));
            return new ControlValuePair(textField, value);
        }
        return null; // TODO: 12/16/2015 GUI for array
    }

    @Override
    public boolean isValueExist(Object value) {
        if (value == null) return false;
        return value.getClass().isArray() || value instanceof String;

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
                if ("Dynamic".equalsIgnoreCase(getCardinality())){
                    return HLAvariableArray.class;
                }
                return HLAfixedArray.class;
            }
        }
    }

    @Override
    public String valueAsString(Object value) {
        if (value.getClass().isArray()){
            Object[] values = (Object[])value;
            if (values.length <= 5){
                String result = "[";
                for (Object o : values) {
                    result += getElementType().valueAsString(o) + ", ";
                }
                result = result.substring(0, result.length()-2) + "]";
                return result;
            }
            return "Array values <" + Arrays.toString(EncodeValue(value)) + ">";
        }
        return  value.toString() + "<" + Arrays.toString(EncodeValue(value)) + ">";
    }

    public AbstractDataType getElementType() {
        return elementType;
    }

    public void setElementType(AbstractDataType elementType) {
        this.elementType = elementType;
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
    }

    public String getSemantics() {
        return semantics;
    }

    public void setSemantics(String semantics) {
        this.semantics = semantics;
    }
}
