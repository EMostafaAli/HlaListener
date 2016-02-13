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
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAvariantRecord;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ca.mali.hlalistener.PublicVariables.encoderFactory;

/**
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class VariantRecordFDD extends AbstractDataType {

    //Logger
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    private String discriminantName;
    private EnumeratedFDDDataType discriminantType;
    private List<Field> alternatives;
    private String encoding;
    private String Semantics;
    private ComboBox<String> discriminantValue;
    private ScrollPane scrollPane;

    public VariantRecordFDD(String name) {
        super(name, DataTypeEnum.VARIANTRECORD);
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
        discriminantValue = discriminantType.getControl(true);
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

    public void setAlternatives(List<Field> alternatives) {
        this.alternatives = alternatives;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        VariantRecordFDD cloned = (VariantRecordFDD) super.clone();
        cloned.setDiscriminantType((EnumeratedFDDDataType) getDiscriminantType().clone());
        cloned.getControl(true);
        List<Field> clonedAlternatives = new ArrayList<>(getAlternatives().size());
        for (Field item : getAlternatives()) clonedAlternatives.add((Field) item.clone());
        cloned.setAlternatives(clonedAlternatives);
        return cloned;
    }

    @Override
    public byte[] EncodeValue() {
        if (getDataElement() == null)
            return null;
        return getDataElement().toByteArray();
    }

    @Override
    public String DecodeValue(byte[] encodedValue) {
//        IMPORTANT NOTE: The following code is a little bit complicated because it is generic for normal cases use the following sample
//        HLAvariantRecord<HLAinteger32BE> record = encoderFactory.createHLAvariantRecord(encoderFactory.createHLAinteger32BE());
//        record.setVariant(encoderFactory.createHLAinteger32BE(0), encoderFactory.createHLAboolean());
//        record.setVariant(encoderFactory.createHLAinteger32BE(1), encoderFactory.createHLAinteger32BE());
//        record.setVariant(encoderFactory.createHLAinteger32BE(2), encoderFactory.createHLAinteger32BE());
//        record.setVariant(encoderFactory.createHLAinteger32BE(3), encoderFactory.createHLAinteger32BE());
//        record.setVariant(encoderFactory.createHLAinteger32BE(5), encoderFactory.createHLAinteger32BE());
        try {
            EnumeratedFDDDataType disClone = (EnumeratedFDDDataType) getDiscriminantType().clone();
            HLAvariantRecord<DataElement> hlAvariantRecord = encoderFactory.createHLAvariantRecord(disClone.getDataElement());
            alternatives.forEach(field -> {
                try {

                    if (field.getDataType() != null) {
                        for (String s : field.getEnumeratorValues(disClone.getEnumerator())) {
                            EnumeratedFDDDataType disAlt = (EnumeratedFDDDataType) getDiscriminantType().clone();
                            disAlt.setValue(s);
                            hlAvariantRecord.setVariant(disAlt.getDataElement(), field.getDataType().getDataElement());
                        }
                    }
                } catch (CloneNotSupportedException ex) {
                    logger.log(Level.ERROR, "Error in creating variant record", ex);
                }
            });
            hlAvariantRecord.decode(encodedValue);
            String result = "";
            result += "[{Discriminant: " + getDiscriminantType().DecodeValue(hlAvariantRecord.getDiscriminant().toByteArray()) + "},";
            int enumeratedIndex = Integer.parseInt(getDiscriminantType().getRepresentation().DecodeValue(hlAvariantRecord.getDiscriminant().toByteArray()));
            Field valueField = getValueField(getDiscriminantType().getEnumerator().get(enumeratedIndex).getName());
            if (valueField != null) {
                AbstractDataType valueDataType = valueField.getDataType();
                if (valueDataType != null)
                    result += "{Value: " + valueDataType.DecodeValue(hlAvariantRecord.getValue().toByteArray()) + "}]";
            }
            return result;
        } catch (Exception ex) {
            logger.log(Level.ERROR, "Error in decoding value", ex);
        }
        return null;
    }

    @Override
    public HLAvariantRecord<DataElement> getDataElement() {
        if (discriminantValue == null || discriminantValue.getSelectionModel().isEmpty())
            return null;
        getDiscriminantType().setValue(discriminantValue.getValue());
        DataElement discriminantDataElement = getDiscriminantType().getDataElement();
        HLAvariantRecord<DataElement> hlAvariantRecord = encoderFactory.createHLAvariantRecord(discriminantDataElement);
        Field valueField = getValueField(discriminantValue.getValue());
        if (valueField != null) {
            AbstractDataType valueDataType = valueField.getDataType();
            if (valueDataType != null)
                hlAvariantRecord.setVariant(discriminantDataElement, valueDataType.getDataElement());
        }
        return hlAvariantRecord;
    }

    @Override
    public ScrollPane getControl(boolean reset) {
        if (reset) {
            GridPane gridPane = new GridPane();
            gridPane.setHgap(5);
            gridPane.setVgap(5);
            gridPane.setPadding(new Insets(0, 5, 0, 5));
            Label title = new Label("Discriminant: ");
            title.setMinWidth(Region.USE_PREF_SIZE);
            gridPane.add(title, 0, 0);
            HBox hBox = new HBox();
            discriminantValue = discriminantType.getControl(true);
            discriminantValue.setOnAction(event -> {
                hBox.getChildren().clear();
                Field valueField = getValueField(discriminantValue.getValue());
                if (valueField != null) {
                    Label l1 = new Label(valueField.getName() + ": ");
                    l1.setMinWidth(Region.USE_PREF_SIZE);
                    hBox.getChildren().add(l1);
                    Region region = valueField.getDataType().getControl(true);
                    HBox.setHgrow(region, Priority.ALWAYS);
                    region.setMaxWidth(Double.MAX_VALUE);
                    hBox.getChildren().add(region);
                }
            });
            gridPane.add(discriminantValue, 1, 0);
            Separator separator = new Separator();
            gridPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                separator.setPrefWidth(newValue.doubleValue());
                discriminantValue.setPrefWidth(newValue.doubleValue());
                hBox.setPrefWidth(newValue.doubleValue());
            });
            gridPane.add(separator, 0, 1, 2, 1);
            gridPane.add(hBox, 0, 2, 2, 1);
            scrollPane = new ScrollPane(gridPane);
            scrollPane.setFitToWidth(true);
        }
        return scrollPane;
    }

    @Override
    public boolean isValueExist() {
        return !(discriminantValue == null || discriminantValue.getSelectionModel().isEmpty()) &&
                getValueField(discriminantValue.getSelectionModel().getSelectedItem()) != null;
    }

    @Override
    public String valueAsString() {
        String result = "";
        if (discriminantValue == null || discriminantValue.getSelectionModel().isEmpty())
            return result;
        result += "[{Discriminant: " + discriminantValue.getValue() + "}, {Value: ";
        Optional<Field> first = getAlternatives().stream().filter(field ->
                field.containsValue(discriminantValue.getSelectionModel().getSelectedItem(), getDiscriminantType().getEnumerator())).findFirst();
        if (first.isPresent() && !first.get().getName().equalsIgnoreCase("NA")) {
            result += first.get().getDataType().valueAsString();
        } else {
            Optional<Field> first1 = getAlternatives().stream().filter(Field::isHlaOther).findFirst();
            if (first1.isPresent() && !first1.get().getName().equalsIgnoreCase("NA")) {
                result += first1.get().getDataType().valueAsString();
            } else {
                result += "NA";
            }
        }
        result += "}]";
        return result;
    }

    @Override
    public Class getObjectClass() {
        return HLAvariantRecord.class;
    }

    private Field getValueField(String value) {
//        if (discriminantValue == null || discriminantValue.getSelectionModel().isEmpty())
//            return null;
        Optional<Field> first = getAlternatives().stream().filter(field ->
                field.containsValue(value, getDiscriminantType().getEnumerator())).findFirst();
        if (first.isPresent() && !first.get().getName().equalsIgnoreCase("NA")) {
            return first.get();
        } else {
            Optional<Field> first1 = getAlternatives().stream().filter(Field::isHlaOther).findFirst();
            if (first1.isPresent() && !first1.get().getName().equalsIgnoreCase("NA")) {
                return first1.get();
            }
        }
        return null;
    }

    public static class Field implements Cloneable {

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

        public boolean isHlaOther() {
            return "HLAother".equalsIgnoreCase(enumeratorSet);
        }

        public boolean containsValue(String value, List<EnumeratedFDDDataType.Enumerator> enumerator) {
            List<String> enumeratorValues = getEnumeratorValues(enumerator);
            return enumeratorValues.contains(value);
//            String[] parts = enumeratorSet.split(",");
//            for (String part : parts) {
//                if (part.contains("..")) {
//                    String s = part.replace("..", ",").replace("[", "").replace("]", "");
//                    String[] firstEnd = s.split(",");
//                    EnumeratedFDDDataType.Enumerator firstItem = enumerator.stream().filter(a -> a.getName().equalsIgnoreCase(firstEnd[0].trim())).findFirst().get();
//                    EnumeratedFDDDataType.Enumerator lastItem = enumerator.stream().filter(a -> a.getName().equalsIgnoreCase(firstEnd[1].trim())).findFirst().get();
//                    int i = enumerator.indexOf(firstItem);
//                    int j = enumerator.indexOf(lastItem);
//                    if (i == -1 || j == -1 || i > j) return false;
//                    for (int k = i; k <= j; k++) {
//                        if (enumerator.get(k).getName().equalsIgnoreCase(value))
//                            return true;
//                    }
//                } else {
//                    return part.equalsIgnoreCase(value);
//                }
//            }
//            return false;
        }

        public List<String> getEnumeratorValues(List<EnumeratedFDDDataType.Enumerator> enumerator) {
            List<String> values = new ArrayList<>();
            String[] parts = enumeratorSet.split(",");
            for (String part : parts) {
                if (part.contains("..")) {
                    String s = part.replace("..", ",").replace("[", "").replace("]", "");
                    String[] firstEnd = s.split(",");
                    EnumeratedFDDDataType.Enumerator firstItem = enumerator.stream().filter(a -> a.getName().equalsIgnoreCase(firstEnd[0].trim())).findFirst().get();
                    EnumeratedFDDDataType.Enumerator lastItem = enumerator.stream().filter(a -> a.getName().equalsIgnoreCase(firstEnd[1].trim())).findFirst().get();
                    int i = enumerator.indexOf(firstItem);
                    int j = enumerator.indexOf(lastItem);
                    for (int k = i; k <= j; k++) {
                        values.add(enumerator.get(k).getName());
                    }
                } else {
                    values.add(part.trim());
                }
            }
            return values;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            Field cloned = (Field) super.clone();
            if (getDataType() == null) {
                cloned.setDataType(null);
            } else {
                cloned.setDataType((AbstractDataType) getDataType().clone());
            }
            return cloned;
        }
    }
}
