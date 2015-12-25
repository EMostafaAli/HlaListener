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
import hla.rti1516e.encoding.HLAvariantRecord;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public void setAlternatives(List<Field> alternatives) {
        this.alternatives = alternatives;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        VariantRecordFDD cloned = (VariantRecordFDD) super.clone();
        List<Field> clonedAlternatives = new ArrayList<>(getAlternatives().size());
        for (Field item : getAlternatives()) clonedAlternatives.add((Field) item.clone());
        cloned.setAlternatives(clonedAlternatives);
        cloned.setDiscriminantType((EnumeratedFDDDataType) cloned.getDiscriminantType().clone());
        return cloned;
    }

    @Override
    public byte[] EncodeValue() {
        return null;
    }

    @Override
    public String DecodeValue(byte[] encodedValue) {
        return null;
    }

    @Override
    public DataElement getDataElement() {
        return null;
    }

    @Override
    public Region getControl() {
//        Object[] values = new Object[2];
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(0, 5, 0, 5));
        Label title = new Label("Discriminant: ");
        title.setMinWidth(Region.USE_PREF_SIZE);
        gridPane.add(title, 0, 0);
        ComboBox<String> discriminantControl = getDiscriminantType().getControl();
        HBox hBox = new HBox();
        discriminantControl.setOnAction(event -> {
            hBox.getChildren().clear();
            Optional<Field> first = getAlternatives().stream().filter(field ->
                    field.containsValue(discriminantControl.getSelectionModel().getSelectedItem(), getDiscriminantType().getEnumerator())).findFirst();
            if (first.isPresent() && !first.get().getName().equalsIgnoreCase("NA")) {
                Label l1 = new Label(first.get().getName() + ": ");
                l1.setMinWidth(Region.USE_PREF_SIZE);
                hBox.getChildren().add(l1);
                hBox.getChildren().add(first.get().getDataType().getControl());
            } else {
                Optional<Field> first1 = getAlternatives().stream().filter(Field::isHlaOther).findFirst();
                if (first1.isPresent() && !first1.get().getName().equalsIgnoreCase("NA")) {
                    Label l2 = new Label(first1.get().getName() + ": ");
                    l2.setMinWidth(Region.USE_PREF_SIZE);
                    hBox.getChildren().add(l2);
                    Region region = first1.get().getDataType().getControl();
                    HBox.setHgrow(region, Priority.ALWAYS);
                    region.setMaxWidth(Double.MAX_VALUE);
                    hBox.getChildren().add(region);
                }
            }
        });
        gridPane.add(discriminantControl, 1, 0);
        Separator separator = new Separator();
        gridPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            separator.setPrefWidth(newValue.doubleValue());
            discriminantControl.setPrefWidth(newValue.doubleValue());
            hBox.setPrefWidth(newValue.doubleValue());
        });
        gridPane.add(separator, 0, 1, 2, 1);
        gridPane.add(hBox, 0, 2, 2, 1);
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    @Override
    public boolean isValueExist() {// TODO: 12/18/2015 Write logic
        return false;
    }

    @Override
    public String valueAsString() {
        return null;
    }

    @Override
    public Class getObjectClass() {
        return HLAvariantRecord.class;
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
            String[] parts = enumeratorSet.split(",");
            for (String part : parts) {
                if (part.contains("..")) {
                    String s = part.replace("..", ",").replace("[", "").replace("]", "");
                    String[] firstEnd = s.split(",");
                    EnumeratedFDDDataType.Enumerator firstItem = enumerator.stream().filter(a -> a.getName().equalsIgnoreCase(firstEnd[0].trim())).findFirst().get();
                    EnumeratedFDDDataType.Enumerator lastItem = enumerator.stream().filter(a -> a.getName().equalsIgnoreCase(firstEnd[1].trim())).findFirst().get();
                    int i = enumerator.indexOf(firstItem);
                    int j = enumerator.indexOf(lastItem);
                    if (i == -1 || j == -1 || i > j) return false;
                    for (int k = i; k <= j; k++) {
                        if (enumerator.get(k).getName().equalsIgnoreCase(value))
                            return true;
                    }
                } else {
                    return part.equalsIgnoreCase(value);
                }
            }
            return false;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            Field cloned = (Field) super.clone();
            if (getDataType() == null){
                cloned.setDataType(null);
            } else {
                cloned.setDataType((AbstractDataType) getDataType().clone());
            }
            return cloned;
        }
    }
}
