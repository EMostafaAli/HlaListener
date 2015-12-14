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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Created by Mostafa on 11/11/2015.
 */
public class AttribParamValuePair implements ValueListener {
    private final String name;
    private String dataTypeName = "";
    private ObjectProperty value = new SimpleObjectProperty<>();
    private final AbstractDataType dataType;
    private ParameterHandle parameterHandle;
    private AttributeHandle attributeHandle;

    public AttribParamValuePair(String name, AbstractDataType dataType, ParameterHandle parameterHandle) {
        this.name = name;
        this.dataType = dataType;
        if (dataType != null) {
            dataTypeName = dataType.getName();
        }
        this.parameterHandle = parameterHandle;
    }

    public AttribParamValuePair(String name, AbstractDataType dataType, AttributeHandle attributeHandle) {
        this.name = name;
        this.dataType = dataType;
        if (dataType != null) {
            dataTypeName = dataType.getName();
        }
        this.attributeHandle = attributeHandle;
    }

    public String getName() {
        return name;
    }

    public AbstractDataType getDataType() {
        return dataType;
    }

    public String getDataTypeName() {
        return dataTypeName;
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
        return getDataType().EncodeValue(getValue());
    }

//    public Region cellGUI(){
//        if (getDataType() == null) return null;
//        return getDataType().getGUI();
//    }

    @Override
    public void UpdateValue(Object value) {
        setValue(value);
    }

    //        byte[] encodedValue = null;
//
//        //Special cases
//        if (dataType == "HLAASCIIstring" || dataType == "HLAunicodeString" || dataType == "HLAASCIIchar" || dataType == "HLAunicodeChar") {
//            encodedValue = fddObjectModel.getBasicDataTypeMap().get("HLAinteger32BE").EncodeValue(getValue().toString(), dataType);
//        } else if (fddObjectModel.getSimpleDataTypeMap().keySet().contains(dataType)) { //Simple data type
//            SimpleFDDDataType simpleFDDDataType = fddObjectModel.getSimpleDataTypeMap().get(dataType);
//            encodedValue = fddObjectModel.getBasicDataTypeMap().get(simpleFDDDataType.getRepresentation()).EncodeValue(getValue().toString());
//        } else if (fddObjectModel.getEnumeratedDataTypeMap().keySet().contains(dataType)) { //Enumerated data type
//            EnumeratedFDDDataType enumerated = fddObjectModel.getEnumeratedDataTypeMap().get(dataType);
//            Optional<EnumeratedFDDDataType.Enumerator> first = enumerated.getEnumerator().stream().filter(enumerator -> enumerator.getName() == getValue().toString()).findFirst();
//            if (first.isPresent()) {
//                encodedValue = fddObjectModel.getBasicDataTypeMap().get(enumerated.getRepresentation()).EncodeValue(first.get().getValues().get(0));
//            }
//        } else if (fddObjectModel.getArrayDataTypeMap().keySet().contains(dataType)) { //Array data type
//            ArrayFDD arrayFDD = fddObjectModel.getArrayDataTypeMap().get(dataType);
//            String[] values = getValue().toString().split(";"); // TODO: 11/22/2015 Add multidimensional array
//            if (arrayFDD.getCardinality().equals("Dynamic")){ //Variable array
//                DataElementFactory factory = i -> stringToDataElement("0", getBasicDataType(arrayFDD.getDataType())); // TODO: 11/22/2015 Not sure if the trivial value "0" has any effects
//                HLAvariableArray hlAvariableArray = encoderFactory.createHLAvariableArray(factory);
//                for (String value : values){
//                    hlAvariableArray.addElement(stringToDataElement(value, getBasicDataType(arrayFDD.getDataType())));
//                }
//                encodedValue = hlAvariableArray.toByteArray();
//            } else { //Fixed array
//                int i = Integer.parseInt(arrayFDD.getCardinality());
//                DataElement[] elements = new DataElement[i];
//                for (int j=0; j<i; j++){
//                    if (values.length >= j){
//                        elements[j] = stringToDataElement(values[j], getBasicDataType(arrayFDD.getDataType()));
//                    }
//                }
//                HLAfixedArray<DataElement> hlAfixedArray = encoderFactory.createHLAfixedArray(elements);
//                encodedValue = hlAfixedArray.toByteArray();
//            }
//        }
//        return encodedValue;
//    }
//
//    private String getBasicDataType(String dataTypeName){
//        if (fddObjectModel.getBasicDataTypeMap().keySet().contains(dataTypeName)){
//            return dataTypeName;
//        } else if (fddObjectModel.getSimpleDataTypeMap().keySet().contains(dataTypeName)) { //Simple data type
//            return fddObjectModel.getSimpleDataTypeMap().get(dataTypeName).getRepresentation();
//        } else if (fddObjectModel.getEnumeratedDataTypeMap().keySet().contains(dataTypeName)) { //Enumerated data type
//            return fddObjectModel.getEnumeratedDataTypeMap().get(dataTypeName).getRepresentation();
//        } else if (fddObjectModel.getArrayDataTypeMap().keySet().contains(dataTypeName)) { //Array data type
//            return getBasicDataType(fddObjectModel.getArrayDataTypeMap().get(dataTypeName).getDataType());
//        } // TODO: 11/22/2015 Add fixed record or change the whole system to OOP
//        return null;
//    }
}

