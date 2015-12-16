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
import hla.rti1516e.encoding.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import static ca.mali.hlalistener.PublicVariables.encoderFactory;

/**
 * Created by Mostafa Ali on 10/22/2015.
 */
public class BasicDataType extends AbstractDataType {

    //Logger
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    private int size;
    private String interpretation;
    private boolean littleEndian;
    private String encoding;

    public BasicDataType(String name) {
        super(name, DataTypeEnum.BASIC);
    }

    public String getName() {
        return super.getName();
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

    @Override
    public byte[] EncodeValue(Object value) {
        byte[] encodedValue = null;
        switch (getName()) {
            case "HLAinteger16BE": {
                HLAinteger16BE encoder = encoderFactory.createHLAinteger16BE();
                encoder.setValue(Short.parseShort(value.toString()));
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAinteger32BE": {
                HLAinteger32BE encoder = encoderFactory.createHLAinteger32BE();
                encoder.setValue(Short.parseShort(value.toString()));
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAinteger64BE": {
                HLAinteger64BE encoder = encoderFactory.createHLAinteger64BE();
                encoder.setValue(Long.parseLong(value.toString()));
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAfloat32BE": {
                HLAfloat32BE encoder = encoderFactory.createHLAfloat32BE();
                encoder.setValue(Float.parseFloat(value.toString()));
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAfloat64BE": {
                HLAfloat64BE encoder = encoderFactory.createHLAfloat64BE();
                encoder.setValue(Double.parseDouble(value.toString()));
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAoctetPairBE": {
                HLAoctetPairBE encoder = encoderFactory.createHLAoctetPairBE();
                encoder.setValue(Short.parseShort(value.toString()));
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAinteger16LE": {
                HLAinteger16LE encoder = encoderFactory.createHLAinteger16LE();
                encoder.setValue(Short.parseShort(value.toString()));
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAinteger32LE": {
                HLAinteger32LE encoder = encoderFactory.createHLAinteger32LE();
                encoder.setValue(Integer.parseInt(value.toString()));
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAinteger64LE": {
                HLAinteger64LE encoder = encoderFactory.createHLAinteger64LE();
                encoder.setValue(Long.parseLong(value.toString()));
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAfloat32LE": {
                HLAfloat32LE encoder = encoderFactory.createHLAfloat32LE();
                encoder.setValue(Float.parseFloat(value.toString()));
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAfloat64LE": {
                HLAfloat64LE encoder = encoderFactory.createHLAfloat64LE();
                encoder.setValue(Double.parseDouble(value.toString()));
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAoctetPairLE": {
                HLAoctetPairLE encoder = encoderFactory.createHLAoctetPairLE();
                encoder.setValue(Short.parseShort(value.toString()));
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAoctet": {
                HLAoctet encoder = encoderFactory.createHLAoctet();
                encoder.setValue(Byte.parseByte(value.toString()));
                encodedValue = encoder.toByteArray();
                break;
            }
            default: {
                //TODO: 11/15/2015 encoder for custom types
                break;
            }
        }
        return encodedValue;
    }

    @Override
    public String DecodeValue(byte[] encodedValue) {
        String value = "";
        try {
            switch (getName()) {
                case "HLAinteger16BE": {
                    HLAinteger16BE encoder = encoderFactory.createHLAinteger16BE();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                    break;
                }
                case "HLAinteger32BE": {
                    HLAinteger32BE encoder = encoderFactory.createHLAinteger32BE();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                    break;
                }
                case "HLAinteger64BE": {
                    HLAinteger64BE encoder = encoderFactory.createHLAinteger64BE();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                    break;
                }
                case "HLAfloat32BE": {
                    HLAfloat32BE encoder = encoderFactory.createHLAfloat32BE();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                    break;
                }
                case "HLAfloat64BE": {
                    HLAfloat64BE encoder = encoderFactory.createHLAfloat64BE();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                    break;
                }
                case "HLAoctetPairBE": {
                    HLAoctetPairBE encoder = encoderFactory.createHLAoctetPairBE();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                    break;
                }
                case "HLAinteger16LE": {
                    HLAinteger16LE encoder = encoderFactory.createHLAinteger16LE();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                    break;
                }
                case "HLAinteger32LE": {
                    HLAinteger32LE encoder = encoderFactory.createHLAinteger32LE();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                    break;
                }
                case "HLAinteger64LE": {
                    HLAinteger64LE encoder = encoderFactory.createHLAinteger64LE();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                    break;
                }
                case "HLAfloat32LE": {
                    HLAfloat32LE encoder = encoderFactory.createHLAfloat32LE();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                    break;
                }
                case "HLAfloat64LE": {
                    HLAfloat64LE encoder = encoderFactory.createHLAfloat64LE();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                    break;
                }
                case "HLAoctetPairLE": {
                    HLAoctetPairLE encoder = encoderFactory.createHLAoctetPairLE();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                    break;
                }
                case "HLAoctet": {
                    HLAoctet encoder = encoderFactory.createHLAoctet();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                    break;
                }
                default: {
                    //TODO: 11/15/2015 encoder for custom types
                    break;
                }
            }
        } catch (DecoderException ex) {
            logger.log(Level.ERROR, "Error in decoding value", ex);
        }
        return value;
    }

    @Override
    public DataElement getDataElement(Object value) {
        switch (getName()) {
            case "HLAinteger16BE": {
                HLAinteger16BE encoder = encoderFactory.createHLAinteger16BE();
                encoder.setValue(Short.parseShort(value.toString()));
                return encoder;
            }
            case "HLAinteger32BE": {
                HLAinteger32BE encoder = encoderFactory.createHLAinteger32BE();
                encoder.setValue(Short.parseShort(value.toString()));
                return encoder;
            }
            case "HLAinteger64BE": {
                HLAinteger64BE encoder = encoderFactory.createHLAinteger64BE();
                encoder.setValue(Long.parseLong(value.toString()));
                return encoder;
            }
            case "HLAfloat32BE": {
                HLAfloat32BE encoder = encoderFactory.createHLAfloat32BE();
                encoder.setValue(Float.parseFloat(value.toString()));
                return encoder;
            }
            case "HLAfloat64BE": {
                HLAfloat64BE encoder = encoderFactory.createHLAfloat64BE();
                encoder.setValue(Double.parseDouble(value.toString()));
                return encoder;
            }
            case "HLAoctetPairBE": {
                HLAoctetPairBE encoder = encoderFactory.createHLAoctetPairBE();
                encoder.setValue(Short.parseShort(value.toString()));
                return encoder;
            }
            case "HLAinteger16LE": {
                HLAinteger16LE encoder = encoderFactory.createHLAinteger16LE();
                encoder.setValue(Short.parseShort(value.toString()));
                return encoder;
            }
            case "HLAinteger32LE": {
                HLAinteger32LE encoder = encoderFactory.createHLAinteger32LE();
                encoder.setValue(Integer.parseInt(value.toString()));
                return encoder;
            }
            case "HLAinteger64LE": {
                HLAinteger64LE encoder = encoderFactory.createHLAinteger64LE();
                encoder.setValue(Long.parseLong(value.toString()));
                return encoder;
            }
            case "HLAfloat32LE": {
                HLAfloat32LE encoder = encoderFactory.createHLAfloat32LE();
                encoder.setValue(Float.parseFloat(value.toString()));
                return encoder;
            }
            case "HLAfloat64LE": {
                HLAfloat64LE encoder = encoderFactory.createHLAfloat64LE();
                encoder.setValue(Double.parseDouble(value.toString()));
                return encoder;
            }
            case "HLAoctetPairLE": {
                HLAoctetPairLE encoder = encoderFactory.createHLAoctetPairLE();
                encoder.setValue(Short.parseShort(value.toString()));
                return encoder;
            }
            case "HLAoctet": {
                HLAoctet encoder = encoderFactory.createHLAoctet();
                encoder.setValue(Byte.parseByte(value.toString()));
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
