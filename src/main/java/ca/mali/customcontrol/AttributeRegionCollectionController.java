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

import ca.mali.fomparser.AttributeFDD;
import ca.mali.fomparser.FddObjectModel;
import ca.mali.fomparser.ObjectClassFDD;
import hla.rti1516e.RegionHandle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ca.mali.hlalistener.PublicVariables.regionHandles;

/**
 * Created by Mostafa Ali on 9/8/2015.
 */
public class AttributeRegionCollectionController extends VBox {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private Label classLabel;

    @FXML
    private ChoiceBox<ObjectClassFDD> ClassChoiceBox;

    @FXML
    private ChoiceBox<String> SetChoiceBox;

    @FXML
    private TableView<AttributeState> AttributeTableView;

    @FXML
    private TableColumn AttributeCheckColumn;

    @FXML
    private TableColumn AttributeNameColumn;

    @FXML
    private TableView<RegionState> RegionTableView;

    @FXML
    private TableColumn RegionCheckColumn;

    @FXML
    private TableColumn RegionColumn;

    private CheckBox AttributeCB = new CheckBox();

    private CheckBox RegionCB = new CheckBox();

    private Map<String, ObservableList<AttributeState>> attributeStateSetMap = new HashMap<>();
    private Map<String, ObservableList<RegionState>> regionStateSetMap = new HashMap<>();

    public AttributeRegionCollectionController() {
        logger.entry();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/customcontrol/AttributeRegionCollection.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
            SetChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (newValue.contains("Add new set")) {
                        SetChoiceBox.getItems().add(0, "Set " + String.valueOf(SetChoiceBox.getItems().size()));
                        SetChoiceBox.setValue(SetChoiceBox.getItems().get(0));
                        return;
                    }
                    if (!attributeStateSetMap.containsKey(SetChoiceBox.getValue())) {
                        ObservableList<AttributeState> aList = FXCollections.observableArrayList();
                        aList.addAll(ClassChoiceBox.getValue().getAttributes().stream().map(AttributeState::new).collect(Collectors.toList()));
                        attributeStateSetMap.put(SetChoiceBox.getValue(), aList);

                        ObservableList<RegionState> rList = FXCollections.observableArrayList();
                        rList.addAll(regionHandles.stream().map(RegionState::new).collect(Collectors.toList()));
                        regionStateSetMap.put(SetChoiceBox.getValue(), rList);
                    }

                    setAttributeSet(attributeStateSetMap.get(SetChoiceBox.getValue()));
                    setRegionSet(regionStateSetMap.get(SetChoiceBox.getValue()));
                }
            });
        } catch (IOException ex) {
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logger.exit();
    }

    public void setFddObjectModel(FddObjectModel fddObjectModel) {
        logger.entry();
        if (fddObjectModel != null) {
            SetChoiceBox.setDisable(false);
            ClassChoiceBox.getItems().addAll(fddObjectModel.getObjectClasses().values());
            ClassChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                resetSet();
            });
            AttributeNameColumn.setCellValueFactory(new PropertyValueFactory<AttributeState, String>("attributeName"));
            AttributeCheckColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AttributeState, Boolean>, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue call(TableColumn.CellDataFeatures<AttributeState, Boolean> param) {
                    return param.getValue().onProperty();
                }
            });
            AttributeCheckColumn.setCellFactory(CheckBoxTableCell.forTableColumn(AttributeCheckColumn));
            AttributeCB.setOnAction(event -> AttributeTableView.getItems().stream().forEach(item -> item.setOn(AttributeCB.isSelected())));
            AttributeCheckColumn.setGraphic(AttributeCB);
            RegionColumn.setCellValueFactory(new PropertyValueFactory<RegionState, String>("regionName"));
            RegionCheckColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RegionState, Boolean>, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue call(TableColumn.CellDataFeatures<RegionState, Boolean> param) {
                    return param.getValue().onProperty();
                }
            });
            RegionCheckColumn.setCellFactory(CheckBoxTableCell.forTableColumn(RegionCheckColumn));
            RegionCB.setOnAction(event -> RegionTableView.getItems().stream().forEach(item -> item.setOn(RegionCB.isSelected())));
            RegionCheckColumn.setGraphic(RegionCB);
            if (ClassChoiceBox.getItems().size() > 0) {
                ClassChoiceBox.setValue(ClassChoiceBox.getItems().get(0));
            }
        }
    }

    private void resetSet(){
        SetChoiceBox.getItems().clear();
        SetChoiceBox.getItems().addAll("Set 1", "<Add new set>");
        attributeStateSetMap.clear();
        regionStateSetMap.clear();
        SetChoiceBox.setValue(SetChoiceBox.getItems().get(0));
    }

    private void setAttributeSet(ObservableList<AttributeState> attributeStateList){
        AttributeTableView.setItems(attributeStateList);
        AttributeCB.setSelected(AttributeTableView.getItems().stream().allMatch(AttributeState::isOn));
        attributeStateList.forEach(attributeState -> attributeState.onProperty().addListener((observable1, oldValue1, newValue1) -> {
            if (!newValue1) {
                AttributeCB.setSelected(false);
            } else if (attributeStateList.stream().allMatch(AttributeState::isOn)) {
                AttributeCB.setSelected(true);
            }
        }));
    }

    private void setRegionSet(ObservableList<RegionState> regionStateList){
        regionStateList.forEach(regionState -> regionState.onProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                RegionCB.setSelected(false);
            } else if (regionStateList.stream().allMatch(RegionState::isOn)) {
                RegionCB.setSelected(true);
            }
        }));
        RegionTableView.setItems(regionStateList);
    }

    public AttributeRegionResult getAttributeRegionResult(){
        return new AttributeRegionResult(ClassChoiceBox.getValue(), attributeStateSetMap, regionStateSetMap);
    }

    public static class AttributeRegionResult{
        private final ObjectClassFDD objectClassFDD;
        private final Map<String, ObservableList<AttributeState>> attributeStatesMap;
        private final Map<String, ObservableList<RegionState>> regionStateMap;

        public AttributeRegionResult(ObjectClassFDD objectClassFDD,
                                     Map<String, ObservableList<AttributeState>> attributeStatesMap,
                                     Map<String, ObservableList<RegionState>> regionStateMap) {
            this.objectClassFDD = objectClassFDD;
            this.attributeStatesMap = attributeStatesMap;
            this.regionStateMap = regionStateMap;
        }

        public ObjectClassFDD getObjectClassFDD() {
            return objectClassFDD;
        }

        public List<List<AttributeFDD>> getAttributeFddList(){
            List<List<AttributeFDD>> attributeList = new ArrayList<>();
            for (ObservableList<AttributeState> list :attributeStatesMap.values()){
                attributeList.add(list.stream().filter(AttributeState::isOn)
                        .map(attributeState -> attributeState.attribute).collect(Collectors.toList()));
            }
            return attributeList;
        }

        public List<List<RegionHandle>> getRegionList() {
            List<List<RegionHandle>> regionList = new ArrayList<>();
            for (ObservableList<RegionState> list : regionStateMap.values()) {
                regionList.add(list.stream().filter(RegionState::isOn)
                        .map(regionState -> regionState.regionHandle).collect(Collectors.toList()));
            }
            return regionList;
        }
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

    public static class RegionState {

        private final ReadOnlyStringWrapper regionName = new ReadOnlyStringWrapper();
        private final RegionHandle regionHandle;
        private final BooleanProperty on = new SimpleBooleanProperty();


        public RegionState(RegionHandle regionHandle) {
            this.regionHandle = regionHandle;
            regionName.set(regionHandle.toString());
        }

        public String getRegionName() {
            return regionName.get();
        }

        public ReadOnlyStringProperty regionNameProperty() {
            return regionName.getReadOnlyProperty();
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
            return getRegionHandle().toString();
        }

        public RegionHandle getRegionHandle() {
            return regionHandle;
        }
    }
}
