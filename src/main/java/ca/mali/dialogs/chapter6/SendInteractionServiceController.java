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

import ca.mali.fomparser.InteractionClassFDD;
import ca.mali.fomparser.ParameterValueCell;
import ca.mali.fomparser.ParameterValuePair;
import ca.mali.hlalistener.ClassValuePair;
import ca.mali.hlalistener.LogEntry;
import ca.mali.hlalistener.LogEntryType;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
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
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ca.mali.hlalistener.PublicVariables.*;

/**
 * FXML Controller class
 *
 * @author Mostafa
 */
public class SendInteractionServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private ComboBox<InteractionClassFDD> InteractionClassName;

    @FXML
    private TableView<ParameterValuePair> ParameterValueTableView;

    @FXML
    private TableColumn<ParameterValuePair, String> ParameterTableColumn;

    @FXML
    private TableColumn<ParameterValuePair, Object> ValueTableColumn;

    @FXML
    private TextField UserSuppliedTag;

    @FXML
    private Spinner<Double> LogicalTimeSpin;

    @FXML
    private Button OkButton;

    private ObservableList<ParameterValuePair> valuePairs = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        if (fddObjectModel != null) {
            ParameterValueTableView.setItems(valuePairs);
            ParameterValueTableView.setEditable(true);
            ParameterTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//            ValueTableColumn.setCellValueFactory(new PropertyValueFactory<>("dataType"));
            ValueTableColumn.setCellFactory(param -> new ParameterValueCell());
            ValueTableColumn.setEditable(true);
            InteractionClassName.getItems().addAll(fddObjectModel.getInteractionClasses().values());
            InteractionClassName.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                valuePairs.clear();
                newValue.getParameters().forEach(parameterFDD -> valuePairs.add(ParameterValuePair.getInstance(parameterFDD)));
            });
            if (InteractionClassName.getItems().size() > 0) {
                InteractionClassName.setValue(InteractionClassName.getItems().get(0));
            }
            OkButton.disableProperty().bind(UserSuppliedTag.textProperty().isEmpty());
        }
        logger.exit();
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        logger.entry();
        ((Stage) InteractionClassName.getScene().getWindow()).close();
        logger.exit();
    }

    @FXML
    private void OK_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("6.12", "Send Interaction service");
        try {
            log.getSuppliedArguments().add(new ClassValuePair("Interaction<Handle>",
                    InteractionClassHandle.class, InteractionClassName.getValue().toString()
                    + '<' + InteractionClassName.getValue().getHandle().toString() + '>'));
            List<ParameterValuePair> valuePairList = valuePairs.stream().filter(parameterValuePair -> parameterValuePair.EncodeValue() != null).collect(Collectors.toList());
            ParameterHandleValueMap parameterHandleValueMap = rtiAmb.getParameterHandleValueMapFactory().create(valuePairList.size());
            valuePairList.forEach(parameterValuePair -> {
                parameterHandleValueMap.put(parameterValuePair.getHandle(), parameterValuePair.EncodeValue());
                log.getSuppliedArguments().add(new ClassValuePair("Parameter <Handle>", ParameterHandle.class,
                        parameterValuePair.getName() +"<" + parameterValuePair.getHandle()+">"));
                log.getSuppliedArguments().add(new ClassValuePair("Value <Encoded>", Object.class,
                        parameterValuePair.getValue().toString() + "<" + Arrays.toString(parameterValuePair.EncodeValue()) +">"));
            });
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", byte[].class, UserSuppliedTag.getText()));
            rtiAmb.sendInteraction(InteractionClassName.getValue().getHandle(), parameterHandleValueMap, UserSuppliedTag.getText().getBytes(Charset.forName("UTF-8")));
            //TODO: 11/15/2015 Using time stamp
            log.setDescription("Interaction sent successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (InteractionParameterNotDefined | InteractionClassNotPublished | InteractionClassNotDefined |
                SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        ((Stage) InteractionClassName.getScene().getWindow()).close();
        logger.exit();
    }
}
