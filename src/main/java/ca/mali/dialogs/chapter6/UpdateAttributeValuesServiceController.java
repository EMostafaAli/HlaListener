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
package ca.mali.dialogs.chapter6;

import ca.mali.fomparser.AbstractValuePair;
import ca.mali.fomparser.AttributeValueCell;
import ca.mali.fomparser.AttributeValuePair;
import ca.mali.fomparser.ObjectInstanceFDD;
import ca.mali.hlalistener.ClassValuePair;
import ca.mali.hlalistener.LogEntry;
import ca.mali.hlalistener.LogEntryType;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.exceptions.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ca.mali.hlalistener.PublicVariables.*;

/**
 * FXML Controller class
 *
 * @author Mostafa
 */
public class UpdateAttributeValuesServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private ComboBox<ObjectInstanceFDD> InstanceName;

    @FXML
    private TableView<AttributeValuePair> AttributeValueTableView;

    @FXML
    private TableColumn<AttributeValuePair, String> AttributeTableColumn;

    @FXML
    private TableColumn<AttributeValuePair, Object> ValueTableColumn;

    @FXML
    private TextField UserSuppliedTag;

    @FXML
    private Spinner<Double> LogicalTimeSpin;

    @FXML
    private Button OkButton;

    private ObservableList<AttributeValuePair> valuePairs = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        if (fddObjectModel != null) {
            AttributeValueTableView.setItems(valuePairs);
            AttributeValueTableView.setEditable(true);
            AttributeTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//            ValueTableColumn.setCellValueFactory(new PropertyValueFactory<>("dataTypeName"));
            ValueTableColumn.setCellFactory(param -> new AttributeValueCell());
            ValueTableColumn.setEditable(true);
            InstanceName.getItems().addAll(objectInstances.values());
            InstanceName.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                valuePairs.clear();
                newValue.getObjectClass().getAttributes().forEach(attributeFDD ->
                        valuePairs.add(AttributeValuePair.getInstance(attributeFDD)));
            });
            if (InstanceName.getItems().size() > 0) {
                InstanceName.setValue(InstanceName.getItems().get(0));
            }
            OkButton.disableProperty().bind(UserSuppliedTag.textProperty().isEmpty());
        }
        logger.exit();
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        logger.entry();
        ((Stage) InstanceName.getScene().getWindow()).close();
        logger.exit();
    }

    @FXML
    private void OK_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("6.10", "Update Attribute Values service");
        try {
            log.getSuppliedArguments().add(new ClassValuePair("Object Instance<Handle>",
                    ObjectInstanceHandle.class, InstanceName.getValue().toString()
                    + '<' + InstanceName.getValue().getHandle().toString() + '>'));
            List<AttributeValuePair> valuePairList = valuePairs.stream().filter(AbstractValuePair::IsValueExist).collect(Collectors.toList());
            AttributeHandleValueMap attributes = rtiAmb.getAttributeHandleValueMapFactory().create(valuePairList.size());
            valuePairList.forEach(attributeValuePair -> {
                attributes.put(attributeValuePair.getHandle(), attributeValuePair.EncodeValue());
                log.getSuppliedArguments().add(new ClassValuePair("Attribute <Handle>", AttributeHandle.class,
                        attributeValuePair.getName() +"<" + attributeValuePair.getHandle()+">"));
                log.getSuppliedArguments().add(new ClassValuePair("Value <Encoded>", attributeValuePair.getObjectClass(),
                        attributeValuePair.ValueAsString()));
            });
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", byte[].class, UserSuppliedTag.getText()));
            rtiAmb.updateAttributeValues(InstanceName.getValue().getHandle(), attributes, UserSuppliedTag.getText().getBytes(Charset.forName("UTF-8")));
            //TODO: 11/15/2015 Using time stamp
            log.setDescription("Attribute Values updated successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (ObjectInstanceNotKnown | AttributeNotDefined | AttributeNotOwned | SaveInProgress | RestoreInProgress |
                FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        ((Stage) InstanceName.getScene().getWindow()).close();
        logger.exit();
    }
}
