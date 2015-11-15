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

import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAinteger16BE;
import hla.rti1516e.encoding.HLAinteger32BE;

import static ca.mali.hlalistener.PublicVariables.encoderFactory;

/**
 * Created by Mostafa Ali on 10/22/2015.
 */
public class BasicDataType {

    private final String name;
    private int size;
    private String interpretation;
    private boolean littleEndian;
    private String encoding;

    public BasicDataType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getInterpretation() {
        return interpretation;
    }

    public void setInterpretation(String interpretation) {
        this.interpretation = interpretation;
    }

    public boolean isLittleEndian() {
        return littleEndian;
    }

    public void setLittleEndian(boolean littleEndian) {
        this.littleEndian = littleEndian;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public byte[] EncodeValue(String value) {
        byte[] encodedValue = null;
        switch (name) {
            case "HLAinteger16BE": {
                HLAinteger16BE encoder = encoderFactory.createHLAinteger16BE();
                encoder.setValue(Short.parseShort(value));
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAinteger32BE": {
                HLAinteger32BE encoder = encoderFactory.createHLAinteger32BE();
                encoder.setValue(Short.parseShort(value));
                encodedValue = encoder.toByteArray();
                break;
            }
        }
        return encodedValue;
    }

    public String DecodeValue(byte[] encodedValue) {
        String value = "";
        try {
            switch (name) {
                case "HLAinteger16BE": {
                    HLAinteger16BE encoder = encoderFactory.createHLAinteger16BE();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                }
                break;
                case "HLAinteger32BE": {
                    HLAinteger32BE encoder = encoderFactory.createHLAinteger32BE();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                    break;
                }
            }
        } catch (DecoderException e) {
            e.printStackTrace(); //// TODO: 11/14/2015 Add to logger
        }
        return value;
    }
}
