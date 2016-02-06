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
package ca.mali.customcontrol;

import ca.mali.fomparser.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Mostafa
 */
public class ParameterListController extends VBox {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private ListView<String> InteractionListView;

    @FXML
    private TableColumn parameterName;

    @FXML
    private TableColumn checked;

    @FXML
    private TableView<ParameterState> ParameterTableView;

    CheckBox cb = new CheckBox();
    private Map<String, ObservableList<ParameterState>> list = new HashMap<>();

    public ParameterListController() {
        logger.entry();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/customcontrol/ParametersList.fxml"));
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
        Label label = new Label("Select Interaction class to display its parameters");
        label.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        ParameterTableView.setPlaceholder(label);
        if (fddObjectModel != null) {
            for (InteractionClassFDD value : fddObjectModel.getInteractionClasses().values()) {
                ObservableList<ParameterState> att = FXCollections.observableArrayList();
                att.addAll(value.getParameters().stream().map(ParameterState::new).collect(Collectors.toList()));
                list.put(value.getFullName(), att);
            }
            InteractionListView.getItems().addAll(list.keySet());
            InteractionListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                ParameterTableView.setItems(list.get(newValue));
                cb.setSelected(ParameterTableView.getItems().stream().allMatch(ParameterState::isOn));
                list.get(newValue).forEach((a) -> a.onProperty().addListener((observable1, oldValue1, newValue1) -> {
                    if (!newValue1) {
                        cb.setSelected(false);
                    } else if (list.get(newValue).stream().allMatch(ParameterState::isOn)) {
                        cb.setSelected(true);
                    }
                }));
            });
            InteractionListView.setCellFactory(param -> {
                final ListCell<String> cell = new ListCell<String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        this.setText(item);
                        this.setTooltip((empty || item == null) ? null : new Tooltip(item));
                    }
                };
                return cell;
            });
            parameterName.setCellValueFactory(new PropertyValueFactory<ParameterState, String>("parameterName"));
            checked.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ParameterState, Boolean>, ObservableValue<Boolean>>() {

                @Override
                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<ParameterState, Boolean> param) {
                    return param.getValue().onProperty();
                }

            });
            checked.setCellFactory(CheckBoxTableCell.forTableColumn(checked));
            cb.setUserData(checked);
            cb.setOnAction(event -> {
                CheckBox cb1 = (CheckBox) event.getSource();
                TableColumn tc = (TableColumn) cb1.getUserData();
                ParameterTableView.getItems().stream().forEach((item) -> item.setOn(cb1.isSelected()));
            });
            checked.setGraphic(cb);
        }
        logger.exit();
    }

    public ListView<String> getInteractionListView() {
        return InteractionListView;
    }

    public void setInteractionListView(ListView<String> ObjectListView) {
        this.InteractionListView = ObjectListView;
    }

    public Map<String, List<ParameterFDD>> getList() {
        logger.entry();
        Map<String, List<ParameterFDD>> finalList = new HashMap<>();
        for (String key : list.keySet()) {
            List<ParameterFDD> collect = list.get(key).stream().filter(
                    ParameterState::isOn).map(a -> a.attribute).collect(Collectors.toList());
            finalList.put(key, collect);
        }
        logger.exit();
        return finalList;
    }

    public static class ParameterState {

        private final ReadOnlyStringWrapper parameterName = new ReadOnlyStringWrapper();
        private final BooleanProperty on = new SimpleBooleanProperty();
        private final ParameterFDD attribute;

        public ParameterState(ParameterFDD attribute) {
            this.attribute = attribute;
            parameterName.set(attribute.getName());
        }

        public String getParameterName() {
            return parameterName.get();
        }

        public ReadOnlyStringProperty parameterNameProperty() {
            return parameterName.getReadOnlyProperty();
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

        public ParameterFDD getAttribute() {
            return attribute;
        }

        @Override
        public String toString() {
            return attribute.getName();
        }
    }

    private static class AttributeStateTooltip extends TableCell<ParameterState, String>{
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            this.setText(item);
            this.setTooltip((empty || item == null) ? null : new Tooltip(item));
        }
    }
}