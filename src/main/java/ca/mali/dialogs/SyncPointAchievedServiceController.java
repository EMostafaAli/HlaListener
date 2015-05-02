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
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;
import hla.rti1516e.exceptions.SynchronizationPointLabelNotAnnounced;
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
 * @author Mostafa
 */
public class SyncPointAchievedServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TextField SyncPointLabel;
    @FXML
    private ChoiceBox<String> SuccessIndicator;
    @FXML
    private Button OkButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        OkButton.disableProperty().bind(
                Bindings.isEmpty(SyncPointLabel.textProperty()));
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        ((Stage) OkButton.getScene().getWindow()).close();
    }

    @FXML
    private void Ok_click(ActionEvent event) {
        try {
            switch (SuccessIndicator.getValue()) {
                case "":
                    rtiAmb.synchronizationPointAchieved(SyncPointLabel.getText());
                    break;
                case "true":
                    rtiAmb.synchronizationPointAchieved(SyncPointLabel.getText(), true);
                    break;
                case "false":
                    rtiAmb.synchronizationPointAchieved(SyncPointLabel.getText(), false);
                    break;
            }
        } catch (SynchronizationPointLabelNotAnnounced ex) {
            logger.log(Level.ERROR, "Syncronization Point Label not Announced", ex);
        } catch (FederateNotExecutionMember ex) {
            logger.log(Level.ERROR, "Federate is not Execution Member", ex);
        } catch (SaveInProgress ex) {
            logger.log(Level.ERROR, "Save In Progress", ex);
        } catch (RestoreInProgress ex) {
            logger.log(Level.ERROR, "Restore In Progress", ex);
        } catch (NotConnected ex) {
            logger.log(Level.ERROR, "Not connected to RTI", ex);
        } catch (RTIinternalError ex) {
            logger.log(Level.ERROR, "Internal error in RTI", ex);
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error in achieving Synchronization Point", ex);
        }
        ((Stage) OkButton.getScene().getWindow()).close();
    }
}
