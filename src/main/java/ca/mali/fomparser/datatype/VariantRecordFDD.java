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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mostafa
 */
public class VariantRecordFDD extends AbstractDataType {

    private String discriminantName;
    private EnumeratedFDDDataType discriminantType;
    private List<Field> alternatives;
    private String encoding;
    private String Semantics;

    public VariantRecordFDD(String name) {
        super(name, DataTypeEnum.VARIANTRECORD);
    }

    @Override
    public byte[] EncodeValue(Object value) {
        return null;
    }

    @Override
    public String DecodeValue(byte[] encodedValue) {
        return null;
    }

    @Override
    public DataElement getDataElement(Object value) {
        return null;
    }

    public String getDiscriminantName() {
        return discriminantName;
    }

    public void setDiscriminantName(String discriminantName) {
        this.discriminantName = discriminantName;
    }

    public EnumeratedFDDDataType getDiscriminantType() {
        return discriminantType;
    }

    public void setDiscriminantType(EnumeratedFDDDataType discriminantType) {
        this.discriminantType = discriminantType;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getSemantics() {
        return Semantics;
    }

    public void setSemantics(String semantics) {
        Semantics = semantics;
    }

    public List<Field> getAlternatives() {
        if (alternatives == null) {
            alternatives = new ArrayList<>();
        }
        return this.alternatives;
    }

    @Override
    public ControlValuePair getControlValue() {
        return null;
    }

    public static class Field {

        private String name;
        private String enumeratorSet;
        private AbstractDataType dataType;
        private String semantics;


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEnumeratorSet() {
            return enumeratorSet;
        }

        public void setEnumeratorSet(String enumeratorSet) {
            this.enumeratorSet = enumeratorSet;
        }

        public AbstractDataType getDataType() {
            return dataType;
        }

        public void setDataType(AbstractDataType dataType) {
            this.dataType = dataType;
        }

        public String getSemantics() {
            return semantics;
        }

        public void setSemantics(String semantics) {
            this.semantics = semantics;
        }
    }
}
