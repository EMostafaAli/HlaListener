package ca.mali.hlalistener;

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
import static ca.mali.hlalistener.PublicVariables.*;

import hla.rti1516e.exceptions.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

import org.apache.logging.log4j.*;

/**
 * FXML Controller class
 *
 * @author Mostafa
 */
public class MainWindowController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private void DisplayDialog(String title, String fxmlPath) throws IOException {
        logger.entry();
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setResizable(false);
        dialog.setTitle(title);
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene dialogScene = new Scene(root);
        dialog.setScene(dialogScene);
        dialog.show();
        logger.exit();
    }

    @FXML
    private void RtiInfo_click(ActionEvent event) {
        try {
            logger.entry();
            logger.log(Level.INFO, rtiFactory.rtiName());
            logger.log(Level.INFO, rtiFactory.rtiVersion());
            logger.log(Level.INFO, rtiAmb.getHLAversion());
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error getting RTI info", ex);
        }
    }

// <editor-fold desc="Chapter 4">
    //4.2
    @FXML
    private void Connect_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.2 Connect service", "/fxml/ConnectService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Connect service dialog box", ex);
        }
    }

    //4.3
    @FXML
    private void Disconnect_click(ActionEvent event) {
        try {
            logger.entry();
            rtiAmb.disconnect();
            logger.exit();
        } catch (FederateIsExecutionMember ex) {
            logger.log(Level.ERROR, "Error disconnecting, please resign first", ex);
        } catch (CallNotAllowedFromWithinCallback ex) {
            logger.log(Level.ERROR, "Error disconnecting, you are not connected", ex);
        } catch (RTIinternalError ex) {
            logger.log(Level.FATAL, "Internal error in RTI", ex);
        }
    }

    //4.5
    @FXML
    private void CreateFederation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.5 Create Federation Execution service", "/fxml/CreateFederationExecutionService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Create Federation service dialog box", ex);
        }
    }

    //4.6
    @FXML
    private void DestroyFederation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.6 Destroy Federation Execution service", "/fxml/DestroyFederationExecutionService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Destroy Federation service dialog box", ex);
        }
    }

    //4.7
    @FXML
    private void ListFederationExecutions_click(ActionEvent event) {
        try {
            logger.entry();
            rtiAmb.listFederationExecutions();
            logger.exit();
        } catch (NotConnected ex) {
            logger.log(Level.ERROR, "Not connected to RTI", ex);
        } catch (RTIinternalError ex) {
            logger.log(Level.ERROR, "Internal error in RTI", ex);
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error listing Federation Executions", ex);
        }
    }
    
    //4.9
    @FXML
    private void JoinFederation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.9 Join Federation Execution service", "/fxml/JoinFederationExecutionService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Join Federation service dialog box", ex);
        }
    }

    //4.10
    @FXML
    private void ResignFederation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.10 Resign Federation Execution service", "/fxml/ResignFederationExecutionService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Resign Federation service dialog box", ex);
        }
    }

    //4.11
    @FXML
    private void RegisterSyncPoint_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.11 Register Federation Synchronization Point service", "/fxml/RegisterFederationSyncPointService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Register Sync Point service dialog box", ex);
        }
    }

    //4.14
    @FXML
    private void SyncPointAchieved_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.14 Synchronization Point Achieved service", "/fxml/SyncPointAchievedService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Sync Point Achieved dialog box", ex);
        }
    }

    //4.16
    @FXML
    private void RequestFederationSave_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.16 Request Federation Save service", "/fxml/RequestFederationSaveService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Request Save service dialog box", ex);
        }
    }

    //4.18
    @FXML
    private void FederateSaveBegun_click(ActionEvent event) {
        try {
            logger.entry();
            rtiAmb.federateSaveBegun();
            logger.exit();
        } catch (FederateNotExecutionMember ex) {
            logger.log(Level.ERROR, "Federate is not Execution Member", ex);
        } catch (RestoreInProgress ex) {
            logger.log(Level.ERROR, "Restore in Progress", ex);
        } catch (SaveNotInitiated ex) {
            logger.log(Level.ERROR, "Save Not Initiated", ex);
        } catch (NotConnected ex) {
            logger.log(Level.ERROR, "Not connected to RTI", ex);
        } catch (RTIinternalError ex) {
            logger.log(Level.ERROR, "Internal error in RTI", ex);
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error in reporting Federate Save Begun", ex);
        }
    }

    //4.19
    @FXML
    private void FederateSaveComplete_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.19 Federate Save Complete service", "/fxml/FederateSaveCompleteService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Federate Save Complete service dialog box", ex);
        }
    }

    //4.21
    @FXML
    private void AbortFederationSave_click(ActionEvent event) {
        try {
            logger.entry();
            rtiAmb.abortFederationSave();
            logger.exit();
        } catch (FederateNotExecutionMember ex) {
            logger.log(Level.ERROR, "Federate is not Execution Member", ex);
        } catch (SaveNotInProgress ex) {
            logger.log(Level.ERROR, "Save not in Progress", ex);
        } catch (NotConnected ex) {
            logger.log(Level.ERROR, "Not connected to RTI", ex);
        } catch (RTIinternalError ex) {
            logger.log(Level.ERROR, "Internal error in RTI", ex);
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error in aborting federation save", ex);
        }
    }

    //4.22
    @FXML
    private void QueryFederationSave_click(ActionEvent event) {
        try {
            logger.entry();
            rtiAmb.queryFederationSaveStatus();
            logger.exit();
        } catch (FederateNotExecutionMember ex) {
            logger.log(Level.ERROR, "Federate is not Execution Member", ex);
        } catch (RestoreInProgress ex) {
            logger.log(Level.ERROR, "Restore in Progress", ex);
        } catch (NotConnected ex) {
            logger.log(Level.ERROR, "Not connected to RTI", ex);
        } catch (RTIinternalError ex) {
            logger.log(Level.ERROR, "Internal error in RTI", ex);
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error in reporting Federate Save Begun", ex);
        }
    }

    //4.24
    @FXML
    private void RequestFederationRestore_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.24 Request Federation Restore service", "/fxml/RequestFederationRestoreService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Request Federation Store service dialog box", ex);
        }
    }

    //4.28
    @FXML
    private void FederateRestoreComplete_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.28 Federate Restore Complete service", "/fxml/FederateRestoreCompleteService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Federate Restore Complete service dialog box", ex);
        }
    }

    //4.30
    @FXML
    private void AbortFederationRestore_click(ActionEvent event) {
        try {
            logger.entry();
            rtiAmb.abortFederationRestore();
            logger.exit();
        } catch (FederateNotExecutionMember ex) {
            logger.log(Level.ERROR, "Federate is not Execution Member", ex);
        } catch (RestoreNotInProgress ex) {
            logger.log(Level.ERROR, "Restore not in Progress", ex);
        } catch (NotConnected ex) {
            logger.log(Level.ERROR, "Not connected to RTI", ex);
        } catch (RTIinternalError ex) {
            logger.log(Level.ERROR, "Internal error in RTI", ex);
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error in aborting federation restore", ex);
        }
    }

    //4.31
    @FXML
    private void QueryFederationRestore_click(ActionEvent event) {
        try {
            logger.entry();
            rtiAmb.queryFederationRestoreStatus();
            logger.exit();
        } catch (FederateNotExecutionMember ex) {
            logger.log(Level.ERROR, "Federate is not Execution Member", ex);
        } catch (SaveInProgress ex) {
            logger.log(Level.ERROR, "Save in Progress", ex);
        } catch (NotConnected ex) {
            logger.log(Level.ERROR, "Not connected to RTI", ex);
        } catch (RTIinternalError ex) {
            logger.log(Level.ERROR, "Internal error in RTI", ex);
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error in querying federation restore status", ex);
        }
    }
// </editor-fold>
    
    //8.2
    @FXML
    private void EnableTimeRegulation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("8.2 Enable Time Regulation service", "/fxml/EnableTimeRegulationService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Enabling Time Regulation service dialog box", ex);
        }
    }

    //8.4
    @FXML
    private void DisableTimeRegulation_click(ActionEvent event) {
        try {
            logger.entry();
            rtiAmb.disableTimeRegulation();
            logger.exit();
        } catch (FederateNotExecutionMember ex) {
            logger.log(Level.ERROR, "Federate is not Execution Member", ex);
        } catch (TimeRegulationIsNotEnabled ex) {
            logger.log(Level.ERROR, "Time Regulation is not enabled", ex);
        } catch (SaveInProgress ex) {
            logger.log(Level.ERROR, "Save in Progress", ex);
        } catch (RestoreInProgress ex) {
            logger.log(Level.ERROR, "Restore in Progress", ex);
        } catch (NotConnected ex) {
            logger.log(Level.ERROR, "Not connected to RTI", ex);
        } catch (RTIinternalError ex) {
            logger.log(Level.ERROR, "Internal error in RTI", ex);
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error in disabling time regulation", ex);
        }
    }

    //8.5
    @FXML
    private void EnableTimeConstrained_click(ActionEvent event) {
        try {
            logger.entry();
            rtiAmb.enableTimeConstrained();
            logger.exit();
        } catch (FederateNotExecutionMember ex) {
            logger.log(Level.ERROR, "Federate is not Execution Member", ex);
        } catch (TimeConstrainedAlreadyEnabled ex) {
            logger.log(Level.ERROR, "Time Constrained is already enabled", ex);
        } catch (InTimeAdvancingState ex) {
            logger.log(Level.ERROR, "Federate is in time advance state", ex);
        } catch (RequestForTimeConstrainedPending ex) {
            logger.log(Level.ERROR, "Request for time constrained is pending", ex);
        } catch (SaveInProgress ex) {
            logger.log(Level.ERROR, "Save in Progress", ex);
        } catch (RestoreInProgress ex) {
            logger.log(Level.ERROR, "Restore in Progress", ex);
        } catch (NotConnected ex) {
            logger.log(Level.ERROR, "Not connected to RTI", ex);
        } catch (RTIinternalError ex) {
            logger.log(Level.ERROR, "Internal error in RTI", ex);
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error in enabling time constrained", ex);
        }
    }

    //8.7
    @FXML
    private void DisableTimeConstrained_click(ActionEvent event) {
        try {
            logger.entry();
            rtiAmb.disableTimeConstrained();
            logger.exit();
        } catch (FederateNotExecutionMember ex) {
            logger.log(Level.ERROR, "Federate is not Execution Member", ex);
        } catch (TimeConstrainedIsNotEnabled ex) {
            logger.log(Level.ERROR, "Time Constrained is not enabled", ex);
        } catch (SaveInProgress ex) {
            logger.log(Level.ERROR, "Save in Progress", ex);
        } catch (RestoreInProgress ex) {
            logger.log(Level.ERROR, "Restore in Progress", ex);
        } catch (NotConnected ex) {
            logger.log(Level.ERROR, "Not connected to RTI", ex);
        } catch (RTIinternalError ex) {
            logger.log(Level.ERROR, "Internal error in RTI", ex);
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error in disabling time constrained", ex);
        }
    }
}
