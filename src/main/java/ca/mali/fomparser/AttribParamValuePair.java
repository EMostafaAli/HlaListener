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

import java.util.Optional;

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
        //Simple data type
        if (fddObjectModel.getSimpleDataTypeMap().keySet().contains(dataType)){
            SimpleFDDDataType simpleFDDDataType = fddObjectModel.getSimpleDataTypeMap().get(dataType);
            encodedValue = fddObjectModel.getBasicDataTypeMap().get(simpleFDDDataType.getRepresentation()).EncodeValue(getValue().toString());
        } else if (fddObjectModel.getEnumeratedDataTypeMap().keySet().contains(dataType)){ //Enumerated data type
            EnumeratedFDDDataType enumerated = fddObjectModel.getEnumeratedDataTypeMap().get(dataType);
            Optional<EnumeratedFDDDataType.Enumerator> first = enumerated.getEnumerator().stream().filter(enumerator -> enumerator.getName() == getValue().toString()).findFirst();
            if (first.isPresent()){
                encodedValue = fddObjectModel.getBasicDataTypeMap().get(enumerated.getRepresentation()).EncodeValue(first.get().getValues().get(0));
            }
        }
        return encodedValue;
    }
}

