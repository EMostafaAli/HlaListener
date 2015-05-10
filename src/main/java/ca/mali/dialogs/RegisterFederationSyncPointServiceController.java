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

import ca.mali.hlalistener.*;
import static ca.mali.hlalistener.PublicVariables.*;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;

import java.net.*;
import java.nio.*;
import java.nio.charset.*;
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
public class RegisterFederationSyncPointServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TextField SyncPointLabel;
    @FXML
    private TextField UserSuppliedTag;
    @FXML
    private TextField JoinedFederateDesignator;
    @FXML
    private Button OkButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        OkButton.disableProperty().bind(
                Bindings.isEmpty(SyncPointLabel.textProperty()));
        logger.exit();
    }

    @FXML
    private void CancelButton_Click(ActionEvent event) {
        logger.entry();
        ((Stage) SyncPointLabel.getScene().getWindow()).close();
        logger.exit();
    }

    @FXML
    private void OkButton_Click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("4.11", "Register Federation Synchronization Point service");
        try {
            log.getSuppliedArguments().add(new ClassValuePair("Synchronization point label", String.class, SyncPointLabel.getText()));
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", Byte.class, Arrays.toString(UserSuppliedTag.getText().getBytes(Charset.forName("UTF-8")))));
            if (JoinedFederateDesignator.getText().isEmpty()) {
                rtiAmb.registerFederationSynchronizationPoint(SyncPointLabel.getText(), UserSuppliedTag.getText().getBytes(Charset.forName("UTF-8")));
            } else {
                String[] handles = JoinedFederateDesignator.getText().split(",");
                FederateHandleSet fedHandleSet = rtiAmb.getFederateHandleSetFactory().create();
                for (String handle : handles) {
                    FederateHandle federateHandle = rtiAmb.getFederateHandleFactory()
                            .decode(ByteBuffer.allocate(4).putInt(Integer.parseInt(handle)).array(), 0);
                    fedHandleSet.add(federateHandle);
                }
                log.getSuppliedArguments().add(new ClassValuePair("set of joined federate designators", fedHandleSet.getClass(), fedHandleSet.toString()));
                rtiAmb.registerFederationSynchronizationPoint(SyncPointLabel.getText(),
                        UserSuppliedTag.getText().getBytes(Charset.forName("UTF-8")),
                        fedHandleSet);
            }
            log.setDescription("Federation synchronization point requested successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (CouldNotDecode | InvalidFederateHandle | FederateNotExecutionMember |
                SaveInProgress | RestoreInProgress | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        ((Stage) SyncPointLabel.getScene().getWindow()).close();
        logEntries.add(log);
        logger.exit();
    }
}
