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

import ca.mali.fomparser.DataTypeEnum;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAfixedRecord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ca.mali.hlalistener.PublicVariables.encoderFactory;

/**
 * @author Mostafa
 */
public class FixedRecordFDD extends AbstractDataType {

    //Logger
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    private String recordName;
    private String encoding;
    private String semantics;
    private List<Field> fields;

    public FixedRecordFDD(String name) {
        super(name, DataTypeEnum.FIXEDRECORD);
    }

    @Override
    public byte[] EncodeValue(Object value) {
        return getDataElement(value).toByteArray();
    }

    @Override
    public String DecodeValue(byte[] encodedValue) {
        try {
            HLAfixedRecord hlAfixedRecord = encoderFactory.createHLAfixedRecord();
            for (Field field : fields){
                hlAfixedRecord.add(field.getDataType().getDataElement(null)); // TODO: 12/16/2015 check if null has any side effects
            }
            hlAfixedRecord.decode(encodedValue);
            String[] values = new String[fields.size()];
            for (int i = 0; i < fields.size(); i++) {
                values[i] = "{" + fields.get(i).getName() + ": " +
                        fields.get(i).getDataType().DecodeValue(hlAfixedRecord.get(i).toByteArray()) + "}";
            }
            return Arrays.toString(values);
        } catch (DecoderException ex) {
            logger.log(Level.ERROR, "Error in decoding value", ex);
        }
        return null;
    }

    @Override
    public DataElement getDataElement(Object value) {
        HLAfixedRecord hlAfixedRecord = encoderFactory.createHLAfixedRecord();
        Object[] values = (Object[]) value;
        for (int i = 0; i < values.length; i++) {
            hlAfixedRecord.add(fields.get(i).getDataType().getDataElement(values[i]));
        }
        return hlAfixedRecord;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getSemantics() {
        return semantics;
    }

    public void setSemantics(String semantics) {
        this.semantics = semantics;
    }

    public List<Field> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return this.fields;
    }

    public static class Field {

        protected String name;
        protected AbstractDataType dataType;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public AbstractDataType getDataType() {
            return dataType;
        }

        public void setDataType(AbstractDataType dataType) {
            this.dataType = dataType;
        }
    }
}
