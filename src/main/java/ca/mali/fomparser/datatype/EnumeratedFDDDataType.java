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
package ca.mali.fomparser.datatype;

import ca.mali.fomparser.DataTypeEnum;
import hla.rti1516e.encoding.DataElement;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Region;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class EnumeratedFDDDataType extends AbstractDataType {

    private BasicDataType representation;
    private List<Enumerator> enumerator;
    private String semantics;
    private String value="";
    private ComboBox<String> values;

    public EnumeratedFDDDataType(String name) {
        super(name, DataTypeEnum.ENUMERATED);
        getControl(true);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        EnumeratedFDDDataType cloned = (EnumeratedFDDDataType)super.clone();
        cloned.setRepresentation((BasicDataType) cloned.getRepresentation().clone());
        cloned.getControl(true);
        return cloned;
    }

    @Override
    public byte[] EncodeValue() {
        byte[] encodedValue = null;
        Optional<Enumerator> first = getEnumerator().stream().filter(enumerator ->
                Objects.equals(enumerator.getName(), value)).findFirst();
        if (first.isPresent()) {
            getRepresentation().setValue(first.get().getValues().get(0));
            encodedValue = getRepresentation().EncodeValue();
        }
        return encodedValue;
    }

    @Override
    public String DecodeValue(byte[] encodedValue) {
        String s = getRepresentation().DecodeValue(encodedValue);
        Optional<Enumerator> first = getEnumerator().stream().filter(enumerator -> enumerator.getValues().contains(s)).findFirst();
        if (first.isPresent())
            return first.get().getName() + "(" + s + ")";
        return s;
    }

    @Override
    public DataElement getDataElement() {
        Optional<Enumerator> first = getEnumerator().stream().filter(enumerator ->
                Objects.equals(enumerator.getName(), value)).findFirst();
        if (first.isPresent()) {
            getRepresentation().setValue(first.get().getValues().get(0));
        }
        return getRepresentation().getDataElement();
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public ComboBox<String> getControl(boolean reset) {
        if (reset){
            value = "";
            values = new ComboBox<>();
            values.getItems().addAll(getEnumerator().stream().map(EnumeratedFDDDataType.Enumerator::getName).collect(Collectors.toList()));
            values.setOnAction(event ->this.value = values.getSelectionModel().getSelectedItem());
        }
        return values;
    }

    @Override
    public boolean isValueExist() {
        return !value.isEmpty();
    }

    @Override
    public Class getObjectClass() {
        return getRepresentation().getObjectClass();
    }

    @Override
    public String valueAsString() {
        return value + "<" + Arrays.toString(EncodeValue()) + ">";
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
}
