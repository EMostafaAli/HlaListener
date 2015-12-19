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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;

/**
 * Created by Mostafa on 12/13/2015.
 */
public abstract class AbstractValuePair {
    private final String name;
    private ObjectProperty<Object> value = new SimpleObjectProperty<>();
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
        ControlValuePair controlValue = getDataType().getControlValue();
        if (controlValue == null) return null;
        this.value = controlValue.valueProperty();
        return controlValue.getRegion();
    }

    public byte[] EncodeValue() {
        if (getValue() == null) return null;
        return getDataType().EncodeValue(getValue());
    }

    public boolean IsValueExist(){
        return getDataType().isValueExist(getValue());
    }

    public Class getObjectClass(){
        return getDataType().getObjectClass();
    }

    public String ValueAsString(){
        return getDataType().valueAsString(getValue());
    }
}
