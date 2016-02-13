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

import ca.mali.fomparser.DimensionFDD;
import ca.mali.fomparser.FddObjectModel;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
public class DimensionsListController extends VBox {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TableView<DimensionState> DimensionTableView;
    @FXML
    private TableColumn<DimensionState, String> DimensionTableColumn;
    @FXML
    private TableColumn CheckTableColumn;

    CheckBox cb = new CheckBox();
    ObservableList<DimensionState> dimensions = FXCollections.observableArrayList();

    public DimensionsListController() {
        logger.entry();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/customcontrol/DimensionsList.fxml"));
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
            fddObjectModel.getDimensions().values().stream().forEach((value) -> dimensions.add(new DimensionState(value)));
            DimensionTableView.setItems(dimensions);
            dimensions.forEach((interaction) -> interaction.onProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    cb.setSelected(false);
                } else if (dimensions.stream().allMatch(a -> a.isOn())) {
                    cb.setSelected(true);
                }
            }));
            DimensionTableColumn.setCellValueFactory(new PropertyValueFactory<>("DimensionName"));
            CheckTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DimensionState, Boolean>, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<DimensionState, Boolean> param) {
                    return param.getValue().onProperty();
                }
            });

            CheckTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(CheckTableColumn));
            cb.setUserData(CheckTableColumn);
            cb.setOnAction((ActionEvent event) -> {
                CheckBox cb1 = (CheckBox) event.getSource();
                TableColumn tc = (TableColumn) cb1.getUserData();
                DimensionTableView.getItems().stream().forEach((item) -> item.setOn(cb1.isSelected()));
            });
            CheckTableColumn.setGraphic(cb);
        }
        logger.exit();
    }

    public List<DimensionFDD> getDimensions() {
        return dimensions.stream().filter(DimensionState::isOn).map(a -> a.dimension).collect(Collectors.toList());
    }

    public static class DimensionState {

        private final ReadOnlyStringWrapper DimensionName = new ReadOnlyStringWrapper();
        private final BooleanProperty on = new SimpleBooleanProperty();
        private final DimensionFDD dimension;

        public DimensionState(DimensionFDD dimension) {
            this.dimension = dimension;
            DimensionName.set(dimension.getName());
        }

        public String getDimensionName() {
            return DimensionName.get();
        }

        public ReadOnlyStringProperty dimensionNameProperty() {
            return DimensionName.getReadOnlyProperty();
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
            return dimension.getName();
        }
    }
}