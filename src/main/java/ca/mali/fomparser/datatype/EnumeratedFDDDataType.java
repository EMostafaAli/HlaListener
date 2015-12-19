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
import hla.rti1516e.encoding.DataElement;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ComboBox;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Mostafa
 */
public class EnumeratedFDDDataType extends AbstractDataType {

    private BasicDataType representation;
    private List<Enumerator> enumerator;
    private String semantics;

    public EnumeratedFDDDataType(String name) {
        super(name, DataTypeEnum.ENUMERATED);
    }

    @Override
    public byte[] EncodeValue(Object value) {
        byte[] encodedValue = null;
        Optional<Enumerator> first = getEnumerator().stream().filter(enumerator ->
                Objects.equals(enumerator.getName(), value.toString())).findFirst();
        if (first.isPresent()) {
            encodedValue = getRepresentation().EncodeValue(first.get().getValues().get(0));
        }
        return encodedValue;
    }

    @Override
    public String DecodeValue(byte[] encodedValue) {
        return null;
    }

    @Override
    public DataElement getDataElement(Object value) {
        Optional<Enumerator> first = getEnumerator().stream().filter(enumerator ->
                Objects.equals(enumerator.getName(), value.toString())).findFirst();
        if (first.isPresent()) {
            return getRepresentation().getDataElement(first.get().getValues().get(0));
        }
        return null;
    }

    public BasicDataType getRepresentation() {
        return representation;
    }

    public void setRepresentation(BasicDataType representation) {
        this.representation = representation;
    }

    public List<Enumerator> getEnumerator() {
        if (enumerator == null) {
            enumerator = new ArrayList<>();
        }
        return this.enumerator;
    }

    public String getSemantics() {
        return semantics;
    }

    public void setSemantics(String semantics) {
        this.semantics = semantics;
    }

    public static class Enumerator {
        protected String name;
        protected List<String> values;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getValues() {
            if (values == null) {
                values = new ArrayList<>();
            }
            return this.values;
        }
    }

    @Override
    public ControlValuePair getControlValue() {
        ComboBox<String> values = new ComboBox<>();
        values.getItems().addAll(getEnumerator().stream().map(EnumeratedFDDDataType.Enumerator::getName).collect(Collectors.toList()));
        ObjectProperty<Object> value = new SimpleObjectProperty<>();
        values.setOnAction(event -> value.setValue(values.getSelectionModel().getSelectedItem()));
        return new ControlValuePair(values, value);
    }

    @Override
    public boolean isValueExist(Object value) {
        return value != null;
    }

    @Override
    public Class getObjectClass() {
        return getRepresentation().getObjectClass();
    }

    @Override
    public String valueAsString(Object value) {
        return  value.toString() + "<" + Arrays.toString(EncodeValue(value)) + ">";
    }
}
