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
import javafx.event.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.*;
import org.apache.logging.log4j.*;

/**
 *
 * @author Mostafa
 */
public class AttributeListController extends VBox {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private ListView<String> ObjectListView;
    
    @FXML
    private TableColumn attributeName;
    
    @FXML
    private TableColumn checked;

    @FXML
    private TableView<AttributeState> AttributeTableView;

    private Map<String, ObservableList<AttributeState>> list = new HashMap<>();

    public AttributeListController() {
        logger.entry();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/customcontrol/AttributesList.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();

        } catch (IOException ex) {
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logger.exit();
    }

    public void setFddObjectModel(FddObjectModel fddObjectModel) {
        logger.entry();
        Label label = new Label("Select Object class to display its attributes");
        label.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        AttributeTableView.setPlaceholder(label);
        if (fddObjectModel != null) {
            for (ObjectClassFDD value : fddObjectModel.getObjectClasses().values()) {
                ObservableList<AttributeState> att = FXCollections.observableArrayList();
                for (AttributeFDD attribute : value.getAttributes()) {
                    att.add(new AttributeState(attribute));
                }
                list.put(value.getFullName(), att);
            }
            ObjectListView.getItems().addAll(list.keySet());
            ObjectListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                AttributeTableView.setItems(list.get(newValue));
            });
            attributeName.setCellValueFactory(new PropertyValueFactory<AttributeState, String>("attributeName"));
            checked.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AttributeState, Boolean>, ObservableValue<Boolean>>() {

                @Override
                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<AttributeState, Boolean> param) {
                    return param.getValue().onProperty();
                }

            });
            checked.setCellFactory(CheckBoxTableCell.forTableColumn(checked));
            CheckBox cb = new CheckBox();
            cb.setUserData(checked);
            cb.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    CheckBox cb = (CheckBox) event.getSource();
                    TableColumn tc = (TableColumn) cb.getUserData();
                    for (AttributeState item : AttributeTableView.getItems()) {
                        item.setOn(cb.isSelected());
                    }
                }
            });
            checked.setGraphic(cb);
        }
        logger.exit();
    }

    public ListView<String> getObjectListView() {
        return ObjectListView;
    }

    public void setObjectListView(ListView<String> ObjectListView) {
        this.ObjectListView = ObjectListView;
    }

    public Map<String, List<AttributeFDD>> getList() {
        logger.entry();
        Map<String, List<AttributeFDD>> finalList = new HashMap<>();
        for (String key : list.keySet()) {
            List<AttributeFDD> collect = list.get(key).stream().filter(
                    a -> a.isOn()).map(a -> a.attribute).collect(Collectors.toList());
            finalList.put(key, collect);
        }
        logger.exit();
        return finalList;
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
