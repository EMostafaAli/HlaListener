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

import ca.mali.hlalistener.ClassValuePair;
import ca.mali.hlalistener.LogEntry;
import ca.mali.hlalistener.LogEntryType;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.InteractionClassHandle;
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

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;

import static ca.mali.hlalistener.PublicVariables.logEntries;
import static ca.mali.hlalistener.PublicVariables.rtiAmb;

/**
 * FXML Controller class
 *
 * @author Mostafa
 */
public class QueryInteractionTransportationTypeServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TextField FederateDeignator;
    @FXML
    private TextField InteractionClassDesignator;
    @FXML
    private Button OkButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        OkButton.disableProperty().bind(
                Bindings.or(FederateDeignator.textProperty().isEmpty(),
                        InteractionClassDesignator.textProperty().isEmpty()));
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
        LogEntry log = new LogEntry("6.29", "Query Interaction Transportation Type service");
        try {
            FederateHandle federateHandle = rtiAmb.getFederateHandleFactory()
                    .decode(ByteBuffer.allocate(4).putInt(Integer.parseInt(FederateDeignator.getText())).array(), 0);
            InteractionClassHandle interactionHandle = rtiAmb.getInteractionClassHandleFactory()
                    .decode(ByteBuffer.allocate(4).putInt(Integer.parseInt(InteractionClassDesignator.getText())).array(), 0);
            log.getSuppliedArguments().add(new ClassValuePair("Federate designator", FederateHandle.class, federateHandle.toString()));
            log.getSuppliedArguments().add(new ClassValuePair("Interaction class designator", InteractionClassHandle.class, interactionHandle.toString()));
            rtiAmb.queryInteractionTransportationType(federateHandle, interactionHandle);
            log.setDescription("Interaction Transportation Type queried successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (FederateNotExecutionMember | NotConnected | CouldNotDecode |
                RTIinternalError | InteractionClassNotDefined | SaveInProgress |
                RestoreInProgress ex) {
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
}
