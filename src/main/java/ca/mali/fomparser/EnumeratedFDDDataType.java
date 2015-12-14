/*
 * Copyright (c) 2015, Mostafa Ali
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

import hla.rti1516e.encoding.DataElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Mostafa
 */
public class EnumeratedFDDDataType extends AbstractDataType {

    private BasicDataType representation;
    private List<Enumerator> enumerator;

    public EnumeratedFDDDataType(String name) {
        super(name, DataTypeEnum.ENUMERATED);
    }

    @Override
    byte[] EncodeValue(Object value) {
        byte[] encodedValue = null;
        Optional<Enumerator> first = getEnumerator().stream().filter(enumerator ->
                enumerator.getName() == value.toString()).findFirst();
        if (first.isPresent()) {
            encodedValue = getRepresentation().EncodeValue(first.get().getValues().get(0));
        }
        return encodedValue;
    }

    @Override
    String DecodeValue(byte[] encodedValue) {
        return null;
    }

    @Override
    DataElement getDataElement(Object value) {
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
