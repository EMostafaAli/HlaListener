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

    @FXML
    private void Connect_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.2 Connect Service", "/fxml/ConnectService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Connect dialog box", ex);
        }
    }

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

    @FXML
    private void ListFederationExecutions_click(ActionEvent event) {
        try {
            logger.entry();
            rtiAmb.listFederationExecutions();
            logger.exit();
        } catch (NotConnected ex) {
            logger.log(Level.ERROR, "Error, please connect first", ex);
        } catch (RTIinternalError ex) {
            logger.log(Level.FATAL, "Internal error in RTI", ex);
        }
    }

    @FXML
    private void CreateFederation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.5 Create Federation Execution Service", "/fxml/CreateFederationExecutionService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Create Federation dialog box", ex);
        }
    }

    @FXML
    private void DestroyFederation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.6 Destroy Federation Execution Service", "/fxml/DestroyFederationExecutionService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Destroy Federation dialog box", ex);
        }
    }

    @FXML
    private void JoinFederation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.9 Join Federation Execution Service", "/fxml/JoinFederationExecutionService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Join Federation dialog box", ex);
        }
    }

    @FXML
    private void ResignFederation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.10 Resign Federation Execution Service", "/fxml/ResignFederationExecutionService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Resign Federation dialog box", ex);
        }
    }
    
    @FXML
    private void RegisterSyncPoint_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.11 Register Federation Synchronization Point service", "/fxml/RegisterFederationSyncPointService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Register Sync Point dialog box", ex);
        }
    }
}
