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
import hla.rti1516e.encoding.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static ca.mali.hlalistener.PublicVariables.encoderFactory;

/**
 * @author Mostafa
 */
public class SimpleFDDDataType extends AbstractDataType {

    //Logger
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    private BasicDataType representation;
    private String units;
    private String resolution;
    private String accuracy;
    private String semantics;

    public SimpleFDDDataType(String name) {
        super(name, DataTypeEnum.SIMPLE);
    }

    public BasicDataType getRepresentation() {
        return representation;
    }

    public void setRepresentation(BasicDataType representation) {
        this.representation = representation;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getSemantics() {
        return semantics;
    }

    public void setSemantics(String semantics) {
        this.semantics = semantics;
    }

    @Override
    public byte[] EncodeValue(Object value) {
        byte[] encodedValue;
        switch (getName()) {
            case "HLAASCIIchar": {
                HLAASCIIchar encoder = encoderFactory.createHLAASCIIchar();
                encoder.setValue((byte) value.toString().charAt(0));
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAunicodeChar": {
                HLAunicodeChar encoder = encoderFactory.createHLAunicodeChar();
                encoder.setValue((short) value.toString().charAt(0));
                encodedValue = encoder.toByteArray();
                break;
            }
            case "HLAbyte": {
                HLAbyte encoder = encoderFactory.createHLAbyte();
                encoder.setValue((byte) value.toString().charAt(0));
                encodedValue = encoder.toByteArray();
                break;
            }
            default: {
                encodedValue = getRepresentation().EncodeValue(value);
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
                case "HLAASCIIchar": {
                    HLAASCIIchar encoder = encoderFactory.createHLAASCIIchar();
                    encoder.decode(encodedValue);
                    value = new String(new byte[]{encoder.getValue()}, "US-ASCII");
                    break;
                }
                case "HLAunicodeChar": {
                    HLAunicodeChar encoder = encoderFactory.createHLAunicodeChar();
                    encoder.decode(encodedValue);
                    value = String.valueOf((char) encoder.getValue());
                    break;
                }
                case "HLAbyte": {
                    HLAbyte encoder = encoderFactory.createHLAbyte();
                    encoder.decode(encodedValue);
                    value = String.valueOf(encoder.getValue());
                    break;
                }
                default: {
                    value = getRepresentation().DecodeValue(encodedValue);
                    break;
                }
            }
        } catch (DecoderException | UnsupportedEncodingException ex) {
            logger.log(Level.ERROR, "Error in decoding value", ex);
        }
        return value;
    }

    @Override
    public DataElement getDataElement(Object value) {
        switch (getName()) {
            case "HLAASCIIchar": {
                HLAASCIIchar encoder = encoderFactory.createHLAASCIIchar();
                encoder.setValue((byte) value.toString().charAt(0));
                return encoder;
            }
            case "HLAunicodeChar": {
                HLAunicodeChar encoder = encoderFactory.createHLAunicodeChar();
                encoder.setValue((short) value.toString().charAt(0));
                return encoder;
            }
            case "HLAbyte": {
                HLAbyte encoder = encoderFactory.createHLAbyte();
                encoder.setValue((byte) value.toString().charAt(0));
                return encoder;
            }
            default: {
                return getRepresentation().getDataElement(value);
            }
        }
    }

    @Override
    public ControlValuePair getControlValue() {
        TextField textField = new TextField();
        ObjectProperty<Object> value = new SimpleObjectProperty<>();
        textField.textProperty().addListener((observable, oldValue, newValue) -> value.setValue(newValue));
        return new ControlValuePair(textField, value);
    }

    @Override
    public boolean isValueExist(Object value) {
        return value != null;
    }

    @Override
    public Class getObjectClass() {
        switch (getName()) {
            case "HLAASCIIchar": {
                return HLAASCIIchar.class;
            }
            case "HLAunicodeChar": {
                return HLAunicodeChar.class;
            }
            case "HLAbyte": {
                return HLAbyte.class;
            }
            default: {
                return getRepresentation().getObjectClass();
            }
        }
    }

    @Override
    public String valueAsString(Object value) {
        return  value.toString() + "<" + Arrays.toString(EncodeValue(value)) + ">";
    }
}
