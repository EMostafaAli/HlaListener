/*
 * Copyright (c) 2015, Mostafa Ali
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ca.mali.customcontrol;

import ca.mali.fomparser.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.util.*;
import org.apache.logging.log4j.*;

/**
 *
 * @author Mostafa
 */
public class ObjectInstanceAttributesController extends VBox {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TableColumn attributeName;

    @FXML
    private TableColumn checked;

    @FXML
    private TableView<AttributeState> AttributeTableView;

    CheckBox cb = new CheckBox();

    public ObjectInstanceAttributesController() {
        logger.entry();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/customcontrol/ObjectInstanceAttributes.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
            Label label = new Label("Select Object class instance to display its attributes");
            label.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
            AttributeTableView.setPlaceholder(label);
            attributeName.setCellValueFactory(new PropertyValueFactory<AttributeState, String>("attributeName"));
            checked.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AttributeState, Boolean>, ObservableValue<Boolean>>() {

                @Override
                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<AttributeState, Boolean> param) {
                    return param.getValue().onProperty();
                }

            });
            checked.setCellFactory(CheckBoxTableCell.forTableColumn(checked));
            cb.setOnAction(event -> {
                CheckBox cb1 = (CheckBox) event.getSource();
                AttributeTableView.getItems().stream().forEach((item) -> {
                    item.setOn(cb1.isSelected());
                });
            });
            checked.setGraphic(cb);

        } catch (IOException ex) {
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logger.exit();
    }

    public void setObjectInstance(ObjectInstanceFDD instance) {
        logger.entry();
        if (instance != null) {
            ObservableList<AttributeState> att = FXCollections.observableArrayList();
            for (AttributeFDD attribute : instance.getObjectClass().getAttributes()) {
                att.add(new AttributeState(attribute));
            }
            AttributeTableView.setItems(att);
            AttributeTableView.getItems().forEach((a) -> {
                    a.onProperty().addListener((observable1, oldValue1, newValue1) -> {
                        if (!newValue1) {
                            cb.setSelected(false);
                        } else if (AttributeTableView.getItems().stream().allMatch(b -> b.isOn())) {
                            cb.setSelected(true);
                        }
                    });
                });
        }
        logger.exit();
    }

    public List<AttributeFDD> getAttributes() {
        return AttributeTableView.getItems().stream().filter(a -> a.isOn()).map(a -> a.attribute).collect(Collectors.toList());
    }

    public static class AttributeState {

        private final ReadOnlyStringWrapper attributeName = new ReadOnlyStringWrapper();
        private final BooleanProperty on = new SimpleBooleanProperty();
        private final AttributeFDD attribute;

        public AttributeState(AttributeFDD attribute) {
            this.attribute = attribute;
            attributeName.set(attribute.getName());
        }

        public String getAttributeName() {
            return attributeName.get();
        }

        public ReadOnlyStringProperty attributeNameProperty() {
            return attributeName.getReadOnlyProperty();
        }

        public boolean isOn() {
            return on.get();
        }

        public void setOn(boolean value) {
            on.set(value);
        }

        public BooleanProperty onProperty() {
            return on;
        }

        public AttributeFDD getAttribute() {
            return attribute;
        }

        @Override
        public String toString() {
            return attribute.getName();
        }
    }
}
