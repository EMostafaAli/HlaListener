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
package ca.mali.customcontrol;

import ca.mali.fomparser.FddObjectModel;
import ca.mali.fomparser.InteractionClassFDD;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class InteractionsListController extends VBox {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TableView<InteractionState> InteractionTableView;
    @FXML
    private TableColumn<InteractionState, String> InteractionTableColumn;
    @FXML
    private TableColumn CheckTableColumn;

    CheckBox cb = new CheckBox();
    ObservableList<InteractionState> interactions = FXCollections.observableArrayList();

    public InteractionsListController() {
        logger.entry();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/customcontrol/InteractionsList.fxml"));
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
        if (fddObjectModel != null) {
            fddObjectModel.getInteractionClasses().values().stream().forEach((value) -> interactions.add(new InteractionState(value)));
            InteractionTableView.setItems(interactions);
            interactions.forEach((interaction) -> interaction.onProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    cb.setSelected(false);
                } else if (interactions.stream().allMatch(a -> a.isOn())) {
                    cb.setSelected(true);
                }
            }));
            InteractionTableColumn.setCellValueFactory(new PropertyValueFactory<>("interactionName"));
            InteractionTableColumn.setCellFactory(param -> {
                final TableCell<InteractionState, String> cell = new TableCell<InteractionState, String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        this.setText(item);
                        this.setTooltip((empty || item == null) ? null : new Tooltip(item));
                    }
                };
                return cell;
            });
            CheckTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<InteractionState, Boolean>, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<InteractionState, Boolean> param) {
                    return param.getValue().onProperty();
                }
            });

            CheckTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(CheckTableColumn));
            cb.setUserData(CheckTableColumn);
            cb.setOnAction((ActionEvent event) -> {
                CheckBox cb1 = (CheckBox) event.getSource();
                TableColumn tc = (TableColumn) cb1.getUserData();
                InteractionTableView.getItems().stream().forEach((item) -> item.setOn(cb1.isSelected()));
            });
            CheckTableColumn.setGraphic(cb);
        }
        logger.exit();
    }

    public List<InteractionClassFDD> getInteractions() {
        return interactions.stream().filter(InteractionState::isOn).map(a -> a.interaction).collect(Collectors.toList());
    }

    public static class InteractionState {

        private final ReadOnlyStringWrapper interactionName = new ReadOnlyStringWrapper();
        private final BooleanProperty on = new SimpleBooleanProperty();
        private final InteractionClassFDD interaction;

        public InteractionState(InteractionClassFDD interaction) {
            this.interaction = interaction;
            interactionName.set(interaction.getFullName());
        }

        public String getInteractionName() {
            return interactionName.get();
        }

        public ReadOnlyStringProperty interactionNameProperty() {
            return interactionName.getReadOnlyProperty();
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

        @Override
        public String toString() {
            return interaction.getFullName();
        }
    }
}
