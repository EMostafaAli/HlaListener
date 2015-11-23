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

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.encoding.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Optional;

import static ca.mali.hlalistener.PublicVariables.encoderFactory;
import static ca.mali.hlalistener.PublicVariables.fddObjectModel;

/**
 * Created by Mostafa on 11/11/2015.
 */
public class AttribParamValuePair {
    private String name;
    private ObjectProperty value = new SimpleObjectProperty<>();
    private String dataType;
    private ParameterHandle parameterHandle;
    private AttributeHandle attributeHandle;

    public AttribParamValuePair(String name, String dataType, ParameterHandle parameterHandle) {
        this.name = name;
        this.dataType = dataType;
        this.parameterHandle = parameterHandle;
    }

    public AttribParamValuePair(String name, String dataType, AttributeHandle attributeHandle) {
        this.name = name;
        this.dataType = dataType;
        this.attributeHandle = attributeHandle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
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

    public ParameterHandle getParameterHandle() {
        return parameterHandle;
    }

    public AttributeHandle getAttributeHandle() {
        return attributeHandle;
    }

    public byte[] EncodeValue() {
        if (getValue() == null) return null;
        byte[] encodedValue = null;

        //Special cases
        if (dataType == "HLAASCIIstring" || dataType == "HLAunicodeString" || dataType == "HLAASCIIchar" || dataType == "HLAunicodeChar") {
            // TODO: 11/18/2015 move encode function here and make it always accept data type
            encodedValue = fddObjectModel.getBasicDataTypeMap().get("HLAinteger32BE").EncodeValue(getValue().toString(), dataType);
        } else if (fddObjectModel.getSimpleDataTypeMap().keySet().contains(dataType)) { //Simple data type
            SimpleFDDDataType simpleFDDDataType = fddObjectModel.getSimpleDataTypeMap().get(dataType);
            encodedValue = fddObjectModel.getBasicDataTypeMap().get(simpleFDDDataType.getRepresentation()).EncodeValue(getValue().toString());
        } else if (fddObjectModel.getEnumeratedDataTypeMap().keySet().contains(dataType)) { //Enumerated data type
            EnumeratedFDDDataType enumerated = fddObjectModel.getEnumeratedDataTypeMap().get(dataType);
            Optional<EnumeratedFDDDataType.Enumerator> first = enumerated.getEnumerator().stream().filter(enumerator -> enumerator.getName() == getValue().toString()).findFirst();
            if (first.isPresent()) {
                encodedValue = fddObjectModel.getBasicDataTypeMap().get(enumerated.getRepresentation()).EncodeValue(first.get().getValues().get(0));
            }
        } else if (fddObjectModel.getArrayDataTypeMap().keySet().contains(dataType)) { //Array data type
            ArrayFDD arrayFDD = fddObjectModel.getArrayDataTypeMap().get(dataType);
            String[] values = getValue().toString().split(";"); // TODO: 11/22/2015 Add multidimensional array
            if (arrayFDD.getCardinality().equals("Dynamic")){ //Variable array
                DataElementFactory factory = i -> stringToDataElement("0", getBasicDataType(arrayFDD.getDataType())); // TODO: 11/22/2015 Not sure if the trivial value "0" has any effects
                HLAvariableArray hlAvariableArray = encoderFactory.createHLAvariableArray(factory);
                for (String value : values){
                    hlAvariableArray.addElement(stringToDataElement(value, getBasicDataType(arrayFDD.getDataType())));
                }
                encodedValue = hlAvariableArray.toByteArray();
            } else { //Fixed array
                int i = Integer.parseInt(arrayFDD.getCardinality());
                DataElement[] elements = new DataElement[i];
                for (int j=0; j<i; j++){
                    if (values.length >= j){
                        elements[j] = stringToDataElement(values[j], getBasicDataType(arrayFDD.getDataType()));
                    }
                }
                HLAfixedArray<DataElement> hlAfixedArray = encoderFactory.createHLAfixedArray(elements);
                encodedValue = hlAfixedArray.toByteArray();
            }
        }
        return encodedValue;
    }

    private String getBasicDataType(String dataTypeName){
        if (fddObjectModel.getBasicDataTypeMap().keySet().contains(dataTypeName)){
            return dataTypeName;
        } else if (fddObjectModel.getSimpleDataTypeMap().keySet().contains(dataTypeName)) { //Simple data type
            return fddObjectModel.getSimpleDataTypeMap().get(dataTypeName).getRepresentation();
        } else if (fddObjectModel.getEnumeratedDataTypeMap().keySet().contains(dataTypeName)) { //Enumerated data type
            return fddObjectModel.getEnumeratedDataTypeMap().get(dataTypeName).getRepresentation();
        } else if (fddObjectModel.getArrayDataTypeMap().keySet().contains(dataTypeName)) { //Array data type
            return getBasicDataType(fddObjectModel.getArrayDataTypeMap().get(dataTypeName).getDataType());
        } // TODO: 11/22/2015 Add fixed record or change the whole system to OOP
        return null;
    }
    private DataElement stringToDataElement(String value, String dataTypeName){
        switch (dataTypeName) {
            case "HLAinteger16BE": {
                HLAinteger16BE encoder = encoderFactory.createHLAinteger16BE();
                encoder.setValue(Short.parseShort(value));
                return encoder;
            }
            case "HLAinteger32BE": {
                HLAinteger32BE encoder = encoderFactory.createHLAinteger32BE();
                encoder.setValue(Short.parseShort(value));
                return encoder;
            }
            case "HLAinteger64BE": {
                HLAinteger64BE encoder = encoderFactory.createHLAinteger64BE();
                encoder.setValue(Long.parseLong(value));
                return encoder;
            }
            case "HLAfloat32BE": {
                HLAfloat32BE encoder = encoderFactory.createHLAfloat32BE();
                encoder.setValue(Float.parseFloat(value));
                return encoder;
            }
            case "HLAfloat64BE": {
                HLAfloat64BE encoder = encoderFactory.createHLAfloat64BE();
                encoder.setValue(Double.parseDouble(value));
                return encoder;
            }
            case "HLAoctetPairBE": {
                HLAoctetPairBE encoder = encoderFactory.createHLAoctetPairBE();
                encoder.setValue(Short.parseShort(value));
                return encoder;
            }
            case "HLAinteger16LE": {
                HLAinteger16LE encoder = encoderFactory.createHLAinteger16LE();
                encoder.setValue(Short.parseShort(value));
                return encoder;
            }
            case "HLAinteger32LE": {
                HLAinteger32LE encoder = encoderFactory.createHLAinteger32LE();
                encoder.setValue(Integer.parseInt(value));
                return encoder;
            }
            case "HLAinteger64LE": {
                HLAinteger64LE encoder = encoderFactory.createHLAinteger64LE();
                encoder.setValue(Long.parseLong(value));
                return encoder;
            }
            case "HLAfloat32LE": {
                HLAfloat32LE encoder = encoderFactory.createHLAfloat32LE();
                encoder.setValue(Float.parseFloat(value));
                return encoder;
            }
            case "HLAfloat64LE": {
                HLAfloat64LE encoder = encoderFactory.createHLAfloat64LE();
                encoder.setValue(Double.parseDouble(value));
                return encoder;
            }
            case "HLAoctetPairLE": {
                HLAoctetPairLE encoder = encoderFactory.createHLAoctetPairLE();
                encoder.setValue(Short.parseShort(value));
                return encoder;
            }
            case "HLAoctet": {
                HLAoctet encoder = encoderFactory.createHLAoctet();
                encoder.setValue(Byte.parseByte(value));
                return encoder;
            }
            case "HLAASCIIstring": {
                HLAASCIIstring encoder = encoderFactory.createHLAASCIIstring();
                encoder.setValue(value);
                return encoder;
            }
            case "HLAunicodeString": {
                HLAunicodeString encoder = encoderFactory.createHLAunicodeString();
                encoder.setValue(value);
                return encoder;
            }
            case "HLAASCIIchar": {
                HLAASCIIchar encoder = encoderFactory.createHLAASCIIchar();
                encoder.setValue((byte) value.charAt(0));
                return encoder;
            }
            case "HLAunicodeChar": {
                HLAunicodeChar encoder = encoderFactory.createHLAunicodeChar();
                encoder.setValue((short) value.charAt(0));
                return encoder;
            }
            default: {
                //TODO: 11/15/2015 encoder for custom types
                break;
            }
        }
        return null;
    }
}

