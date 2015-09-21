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
package ca.mali.dialogs.chapter4;

import ca.mali.customcontrol.FilesList;
import ca.mali.hlalistener.ClassValuePair;
import ca.mali.hlalistener.LogEntry;
import ca.mali.hlalistener.LogEntryType;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.exceptions.*;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static ca.mali.hlalistener.PublicVariables.*;

/**
 * FXML Controller class
 *
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class JoinFederationExecutionServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TextField FederateName;
    @FXML
    private TextField FederateType;
    @FXML
    private TextField FederationExecutionName;
    @FXML
    private FilesList FomModuleDesignators;
    @FXML
    private Button OkButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        OkButton.disableProperty().bind(
                Bindings.isEmpty(FederationExecutionName.textProperty())
                .or(Bindings.isEmpty(FederateType.textProperty())));
        logger.exit();
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        logger.entry();
        ((Stage) FederationExecutionName.getScene().getWindow()).close();
        logger.exit();
    }

    @FXML
    private void OK_click(ActionEvent event) {
        logger.entry();
        FederateHandle federateHandle;
        LogEntry log = new LogEntry("4.9", "Join Federation Execution service");
        try {
            if (!FederateName.getText().isEmpty()) {
                log.getSuppliedArguments().add(new ClassValuePair("Federate Name", String.class, FederateName.getText()));
            }
            log.getSuppliedArguments().add(new ClassValuePair("Federate Type", String.class, FederateType.getText()));
            log.getSuppliedArguments().add(new ClassValuePair("Federation Execution Name", String.class, FederationExecutionName.getText()));
            List<URL> foms = new ArrayList<>();
            int i = 1;
            for (File file : FomModuleDesignators.getFiles()) {
                foms.add(file.toURI().toURL());
                log.getSuppliedArguments().add(new ClassValuePair("FOM Module Deisgnator " + i++, URL.class, file.toURI().toURL().toString()));
            }
            if (FederateName.getText().isEmpty() && FomModuleDesignators.getFileNames().isEmpty()) {
                federateHandle = rtiAmb.joinFederationExecution(FederateType.getText(), FederationExecutionName.getText());
            } else if (FomModuleDesignators.getFileNames().isEmpty()) {
                federateHandle = rtiAmb.joinFederationExecution(FederateName.getText(), FederateType.getText(), FederationExecutionName.getText());
            } else if (FederateName.getText().isEmpty()) {
                federateHandle = rtiAmb.joinFederationExecution(FederateType.getText(), FederationExecutionName.getText(), foms.toArray(new URL[foms.size()]));
            } else {
                federateHandle = rtiAmb.joinFederationExecution(FederateName.getText(), FederateType.getText(), FederationExecutionName.getText(), foms.toArray(new URL[foms.size()]));
            }
            log.getReturnedArguments().add(new ClassValuePair("Federate Handle", FederateHandle.class, federateHandle.toString()));
            log.setDescription("Federate joined federation execution successfully");
            log.setLogType(LogEntryType.REQUEST);
            logicalTimeFactory = rtiAmb.getTimeFactory();
            currentLogicalTime = logicalTimeFactory.makeInitial();

            //subscribe to HLAcurrentFDD to retrieve FDD
            ObjectClassHandle FederationHandle = rtiAmb.getObjectClassHandle("HLAobjectRoot.HLAmanager.HLAfederation");
            currentFDDHandle = rtiAmb.getAttributeHandle(FederationHandle, "HLAcurrentFDD");
            AttributeHandleSet set = rtiAmb.getAttributeHandleSetFactory().create();
            set.add(currentFDDHandle);
            rtiAmb.subscribeObjectClassAttributes(FederationHandle, set);
            rtiAmb.requestAttributeValueUpdate(FederationHandle, set, null);
            //In case of HLA_EVOKED we require this line to receive the FDD
            rtiAmb.evokeMultipleCallbacks(.05, 1); //evoke one callback will not be enough because the reflect attribute is the second one
        } catch (CouldNotCreateLogicalTimeFactory | CallNotAllowedFromWithinCallback |
                CouldNotOpenFDD | ErrorReadingFDD | InconsistentFDD |
                FederateNameAlreadyInUse | FederateAlreadyExecutionMember |
                FederationExecutionDoesNotExist | SaveInProgress | RestoreInProgress |
                NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        ((Stage) FederationExecutionName.getScene().getWindow()).close();
        logger.exit();
    }
}
