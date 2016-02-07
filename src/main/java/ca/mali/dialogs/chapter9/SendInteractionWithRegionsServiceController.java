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
package ca.mali.dialogs.chapter9;

import ca.mali.customcontrol.RegionListController;
import ca.mali.fomparser.AbstractValuePair;
import ca.mali.fomparser.InteractionClassFDD;
import ca.mali.fomparser.ParameterValueCell;
import ca.mali.fomparser.ParameterValuePair;
import ca.mali.fomparser.datatype.AbstractDataType;
import ca.mali.hlalistener.ClassValuePair;
import ca.mali.hlalistener.LogEntry;
import ca.mali.hlalistener.LogEntryType;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import hla.rti1516e.time.HLAinteger64Time;
import hla.rti1516e.time.HLAinteger64TimeFactory;
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
public class SendInteractionWithRegionsServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private ComboBox<InteractionClassFDD> InteractionClassName;

    @FXML
    private TableView<ParameterValuePair> ParameterValueTableView;

    @FXML
    private TableColumn<ParameterValuePair, String> ParameterTableColumn;

    @FXML
    private TableColumn<ParameterValuePair, AbstractDataType> ValueTableColumn;

    @FXML
    private RegionListController regionListController;

    @FXML
    private TextField UserSuppliedTag;

    @FXML
    private CheckBox LogicalTimeCheck;

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
            regionListController.setFddObjectModel(fddObjectModel);
            ParameterValueTableView.setItems(valuePairs);
            ParameterValueTableView.setEditable(true);
            ParameterTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            ValueTableColumn.setCellValueFactory(new PropertyValueFactory<>("dataType"));
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
            double currentValue = 0;
            double step = .1;
            try {
                if (logicalTimeFactory != null) {
                    switch (logicalTimeFactory.getName()) {
                        case "HLAfloat64Time":
                            currentValue = ((HLAfloat64Time) currentLogicalTime).getValue();
                            break;
                        case "HLAinteger64Time":
                            step = 1;
                            currentValue = ((HLAinteger64Time) currentLogicalTime).getValue();
                            break;
                    }
                }
            } catch (Exception ex) {
                logger.log(Level.WARN, ex.getMessage(), ex);
            }
            SpinnerValueFactory<Double> sVF = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0, step);
            sVF.setValue(currentValue);
            LogicalTimeSpin.setValueFactory(sVF);
        }
        OkButton.disableProperty().bind(UserSuppliedTag.textProperty().isEmpty());
        LogicalTimeSpin.disableProperty().bind(LogicalTimeCheck.selectedProperty().not());
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
        LogEntry log = new LogEntry("9.12", "Send Interaction With Regions service");
        try {
            log.getSuppliedArguments().add(new ClassValuePair("Interaction<Handle>",
                    InteractionClassHandle.class, InteractionClassName.getValue().toString()
                    + '<' + InteractionClassName.getValue().getHandle().toString() + '>'));
            List<ParameterValuePair> valuePairList = valuePairs.stream().filter(AbstractValuePair::IsValueExist).collect(Collectors.toList());
            ParameterHandleValueMap parameterHandleValueMap = rtiAmb.getParameterHandleValueMapFactory().create(valuePairList.size());
            valuePairList.forEach(parameterValuePair -> {
                parameterHandleValueMap.put(parameterValuePair.getHandle(), parameterValuePair.EncodeValue());
                log.getSuppliedArguments().add(new ClassValuePair("Parameter <Handle>", ParameterHandle.class,
                        parameterValuePair.getName() + "<" + parameterValuePair.getHandle() + ">"));
                log.getSuppliedArguments().add(new ClassValuePair("Value <Encoded>", parameterValuePair.getObjectClass(),
                        parameterValuePair.ValueAsString()));
            });
            RegionHandleSet regionHandleSet = rtiAmb.getRegionHandleSetFactory().create();
            for (RegionHandle handle : regionListController.getRegions()) {
                log.getSuppliedArguments().add(new ClassValuePair("Region handle", RegionHandle.class, handle.toString()));
                regionHandleSet.add(handle);
            }
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", byte[].class, UserSuppliedTag.getText()));
            if (LogicalTimeCheck.isSelected()) {
                if (logicalTimeFactory == null) { //means not connected or federate is not execution member
                    rtiAmb.getTimeFactory(); //this line will raise the appropriate exception
                }
                LogicalTime logicalTime;
                switch (logicalTimeFactory.getName()) {
                    case "HLAfloat64Time": {
                        logicalTime = ((HLAfloat64TimeFactory) logicalTimeFactory).makeTime(LogicalTimeSpin.getValue());
                        log.getSuppliedArguments().add(new ClassValuePair("Logical Time", HLAfloat64Time.class, logicalTime.toString()));
                        log.setSimulationTime(LogicalTimeSpin.getValue().toString());
                        break;
                    }
                    case "HLAinteger64Time": {
                        if (!(LogicalTimeSpin.getValue() % 1 == 0)) {
                            throw new InvalidLogicalTime("The federate time is HLAinteger64Time, logical time cannot be double");
                        }
                        logicalTime
                                = ((HLAinteger64TimeFactory) logicalTimeFactory).makeTime(LogicalTimeSpin.getValue().longValue());
                        log.getSuppliedArguments().add(new ClassValuePair("Logical Time", HLAinteger64Time.class, logicalTime.toString()));
                        log.setSimulationTime(String.valueOf(LogicalTimeSpin.getValue().longValue()));
                        break;
                    }
                    default:
                        throw new Exception("Unknown Time Implementation");
                }
                rtiAmb.sendInteractionWithRegions(InteractionClassName.getValue().getHandle(), parameterHandleValueMap,
                        regionHandleSet, UserSuppliedTag.getText().getBytes(Charset.forName("UTF-8")), logicalTime);
            } else {
                rtiAmb.sendInteractionWithRegions(InteractionClassName.getValue().getHandle(), parameterHandleValueMap,
                        regionHandleSet, UserSuppliedTag.getText().getBytes(Charset.forName("UTF-8")));
            }
            log.setDescription("Interaction with regions sent successfully");
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
