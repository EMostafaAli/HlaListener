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
package ca.mali.dialogs.chapter4;

import ca.mali.customcontrol.*;
import ca.mali.hlalistener.*;
import static ca.mali.hlalistener.PublicVariables.*;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javafx.beans.binding.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;
import org.apache.logging.log4j.*;

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
            int i=1;
            for (File file : FomModuleDesignators.getFiles()) {
                foms.add(file.toURI().toURL());
                log.getSuppliedArguments().add(new ClassValuePair("FOM Module Deisgnator " + i++, URL.class, file.toURI().toURL().toString()));
            }
            if (FederateName.getText().isEmpty() && FomModuleDesignators.getFileNames().isEmpty()) {
                federateHandle=rtiAmb.joinFederationExecution(FederateType.getText(), FederationExecutionName.getText());
            } else if (FomModuleDesignators.getFileNames().isEmpty()) {
                federateHandle=rtiAmb.joinFederationExecution(FederateName.getText(), FederateType.getText(), FederationExecutionName.getText());
            } else if (FederateName.getText().isEmpty()) {
                federateHandle=rtiAmb.joinFederationExecution(FederateType.getText(), FederationExecutionName.getText(), foms.toArray(new URL[foms.size()]));
            } else {
                federateHandle=rtiAmb.joinFederationExecution(FederateName.getText(), FederateType.getText(), FederationExecutionName.getText(), foms.toArray(new URL[foms.size()]));
            }
            log.getReturnedArguments().add(new ClassValuePair("Federate Handle", FederateHandle.class, federateHandle.toString()));
            log.setDescription("Federate joined federation execution successfully");
            log.setLogType(LogEntryType.REQUEST);
            logicalTimeFactory = rtiAmb.getTimeFactory();
            currentLogicalTime = logicalTimeFactory.makeInitial();
            
            ObjectClassHandle FederationHandle = rtiAmb.getObjectClassHandle("HLAobjectRoot.HLAmanager.HLAfederation");
            System.out.println(FederationHandle.toString());
            RtiAmbInitializer.currentFDDHandle = rtiAmb.getAttributeHandle(FederationHandle, "HLAcurrentFDD");
            System.out.println(RtiAmbInitializer.currentFDDHandle.toString());
            AttributeHandleSet set = rtiAmb.getAttributeHandleSetFactory().create();
            set.add(RtiAmbInitializer.currentFDDHandle);
            rtiAmb.subscribeObjectClassAttributes(FederationHandle, set);
            rtiAmb.requestAttributeValueUpdate(FederationHandle, set, null);
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
