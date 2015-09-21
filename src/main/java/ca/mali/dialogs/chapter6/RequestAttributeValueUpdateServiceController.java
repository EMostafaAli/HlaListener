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
package ca.mali.dialogs.chapter6;

import ca.mali.fomparser.AttributeFDD;
import ca.mali.fomparser.FddObjectModel;
import ca.mali.fomparser.ObjectClassFDD;
import ca.mali.fomparser.ObjectInstanceFDD;
import ca.mali.hlalistener.ClassValuePair;
import ca.mali.hlalistener.LogEntry;
import ca.mali.hlalistener.LogEntryType;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.exceptions.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ca.mali.hlalistener.PublicVariables.*;

/**
 * FXML Controller class
 *
 * @author Mostafa
 */
public class RequestAttributeValueUpdateServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private RadioButton ObjectInstanceRadioButton;
    @FXML
    private RadioButton ObjectClassRadioButton;
    @FXML
    private ChoiceBox<ObjectInstanceFDD> ObjectInstanceChoiceBox;
    @FXML
    private ChoiceBox<ObjectClassFDD> ObjectClassChoiceBox;
    @FXML
    private TableView<AttributeState> AttributeTableView;
    @FXML
    private TableColumn AtttributeCheckColumn;
    @FXML
    private TableColumn AttributeNameColumn;
    @FXML
    private TextField UserSuppliedTag;
    @FXML
    private Button OkButton;

    CheckBox cb = new CheckBox();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        ObjectInstanceChoiceBox.disableProperty().bind(Bindings.not(ObjectInstanceRadioButton.selectedProperty()));
        ObjectClassChoiceBox.disableProperty().bind(Bindings.not(ObjectClassRadioButton.selectedProperty()));
        OkButton.disableProperty().bind(UserSuppliedTag.textProperty().isEmpty());
        setFddObjectModel(fddObjectModel);
        logger.exit();
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        logger.entry();
        ((Stage) OkButton.getScene().getWindow()).close();
        logger.exit();
    }

    @FXML
    private void Ok_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("6.19", "Request Attribute Value Update service");
        try {
            AttributeHandleSet attributeHandleSet = rtiAmb.getAttributeHandleSetFactory().create();
            attributeHandleSet.addAll(AttributeTableView.getItems().stream().filter(AttributeState::isOn)
                    .map(a -> a.getAttribute().getHandle()).collect(Collectors.toList()));
            if (ObjectInstanceRadioButton.isSelected()) {
                log.getSuppliedArguments().add(new ClassValuePair("Object instance <handle>", ObjectInstanceHandle.class,
                        ObjectInstanceChoiceBox.getValue().getName() + '<' + ObjectInstanceChoiceBox.getValue().getHandle().toString() + '>'));
                rtiAmb.requestAttributeValueUpdate(ObjectInstanceChoiceBox.getValue().getHandle(), attributeHandleSet,
                        UserSuppliedTag.getText().getBytes(Charset.forName("UTF-8")));
            } else {
                log.getSuppliedArguments().add(new ClassValuePair("Object class <handle>", ObjectClassHandle.class,
                        ObjectClassChoiceBox.getValue().getName() + '<' + ObjectClassChoiceBox.getValue().getHandle().toString() + '>'));
                rtiAmb.requestAttributeValueUpdate(ObjectClassChoiceBox.getValue().getHandle(), attributeHandleSet,
                        UserSuppliedTag.getText().getBytes(Charset.forName("UTF-8")));
            }
            AttributeTableView.getItems().stream().filter(AttributeState::isOn).forEach((item) ->
                    log.getSuppliedArguments().add(new ClassValuePair("Attribute<handle>", AttributeHandle.class,
                            item.getAttribute().getName() + '<' + item.getAttribute().getHandle().toString() + '>')));
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", byte[].class, UserSuppliedTag.getText()));
            log.setDescription("Attribute value updates requested successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (FederateNotExecutionMember | NotConnected | ObjectInstanceNotKnown | RestoreInProgress |
                RTIinternalError | AttributeNotDefined | SaveInProgress | ObjectClassNotDefined ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        ((Stage) OkButton.getScene().getWindow()).close();
        logger.exit();
    }

    private void setFddObjectModel(FddObjectModel fddObjectModel) {
        logger.entry();
        if (fddObjectModel != null) {
            ObjectClassChoiceBox.getItems().addAll(fddObjectModel.getObjectClasses().values());
            ObjectInstanceChoiceBox.getItems().addAll(objectInstances.values());
            ObjectClassChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                SetAttributeList(newValue);
            });
            ObjectInstanceChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                SetAttributeList(newValue.getObjectClass());
            });

            ObjectClassRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    if (ObjectClassChoiceBox.getValue() != null) {
                        SetAttributeList(ObjectClassChoiceBox.getValue());
                    }
                } else {
                    if (ObjectInstanceChoiceBox.getValue() != null) {
                        SetAttributeList(ObjectInstanceChoiceBox.getValue().getObjectClass());
                    }
                }
            });

            AttributeNameColumn.setCellValueFactory(new PropertyValueFactory<AttributeState, String>("attributeName"));
            AtttributeCheckColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AttributeState, Boolean>, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<AttributeState, Boolean> param) {
                    return param.getValue().onProperty();
                }
            });
            AtttributeCheckColumn.setCellFactory(CheckBoxTableCell.forTableColumn(AtttributeCheckColumn));
            cb.setUserData(AtttributeCheckColumn);
            cb.setOnAction(event -> {
                CheckBox cb1 = (CheckBox) event.getSource();
                AttributeTableView.getItems().stream().forEach((item) -> item.setOn(cb1.isSelected()));
            });
            AtttributeCheckColumn.setGraphic(cb);
        }
        logger.exit();
    }

    private void SetAttributeList(ObjectClassFDD newValue) {
        ObservableList<AttributeState> att = FXCollections.observableArrayList();
        att.addAll(newValue.getAttributes().stream().map(AttributeState::new).collect(Collectors.toList()));
        AttributeTableView.setItems(att);
        cb.setSelected(AttributeTableView.getItems().stream().allMatch(AttributeState::isOn));
        att.forEach((a) -> a.onProperty().addListener((observable1, oldValue1, newValue1) -> {
            if (!newValue1) {
                cb.setSelected(false);
            } else if (att.stream().allMatch(b -> b.isOn())) {
                cb.setSelected(true);
            }
        }));
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
