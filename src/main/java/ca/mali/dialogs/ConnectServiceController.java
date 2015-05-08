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
package ca.mali.dialogs;

import static ca.mali.hlalistener.PublicVariables.*;
import ca.mali.hlalistener.*;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;

import java.net.*;
import java.util.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;
import org.apache.logging.log4j.*;

/**
 * FXML Controller class
 *
 * @author Mostafa
 */
public class ConnectServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TextArea LocalSettingsDesignator;

    @FXML
    private ChoiceBox<CallbackModel> CallbackModel_choiceBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CallbackModel_choiceBox.getItems().addAll(CallbackModel.values());
        CallbackModel_choiceBox.setValue(CallbackModel.HLA_IMMEDIATE);
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        ((Stage) LocalSettingsDesignator.getScene().getWindow()).close();
    }

    @FXML
    private void OK_click(ActionEvent event) {
        logger.entry();
        try {
            if (LocalSettingsDesignator.getText().isEmpty()) {
                rtiAmb.connect(fedAmb, CallbackModel_choiceBox.getValue());
            } else {
                rtiAmb.connect(fedAmb, CallbackModel_choiceBox.getValue(), LocalSettingsDesignator.getText());
            }
            LogEntry log = new LogEntry("4.2", "Connected successfully to RTI");
            log.setLogType(LogEntryType.REQUEST);
            log.setSimulationTime("NA");
            logEntries.add(log);
        } catch (ConnectionFailed | UnsupportedCallbackModel | InvalidLocalSettingsDesignator | AlreadyConnected | RTIinternalError ex) {
            AddExceptionToLog("4.2", ex.getMessage(), ex, LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            AddExceptionToLog("4.2", ex.getMessage(), ex, LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        ((Stage) LocalSettingsDesignator.getScene().getWindow()).close();
        logger.exit();
    }
}
