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
package ca.mali.dialogs.chapter6;

import ca.mali.hlalistener.*;
import static ca.mali.hlalistener.PublicVariables.*;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.*;
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
 * @author Mostafa
 */
public class DeleteObjectInstanceServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TextField ObjectInstanceDesignator;
    @FXML
    private TextField UserSuppliedTag;
    @FXML
    private TextField TimeStamp;
    @FXML
    private Button OkButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        OkButton.disableProperty().bind(
                Bindings.or(ObjectInstanceDesignator.textProperty().isEmpty(),
                        UserSuppliedTag.textProperty().isEmpty()));
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
        LogEntry log = new LogEntry("6.14", "Delete Object Instance service");
        try {
            ObjectInstanceHandle instanceHandle = rtiAmb.getObjectInstanceHandleFactory()
                    .decode(ByteBuffer.allocate(4).putInt(Integer.parseInt(ObjectInstanceDesignator.getText())).array(), 0);
            log.getSuppliedArguments().add(new ClassValuePair("Object instance designator", ObjectInstanceHandle.class, instanceHandle.toString()));
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", byte[].class, UserSuppliedTag.getText()));
            if (TimeStamp.getText().isEmpty()) {
                rtiAmb.deleteObjectInstance(instanceHandle, UserSuppliedTag.getText().getBytes(Charset.forName("UTF-8")));
            } else {
                if (logicalTimeFactory == null) { //means not connected or federate is not execution member
                    rtiAmb.getTimeFactory(); //this line will raise the appropriate exception
                }
                MessageRetractionReturn messageRetraction;
                switch (logicalTimeFactory.getName()) {
                    case "HLAfloat64Time": {
                        HLAfloat64Time logicalTime
                                = ((HLAfloat64TimeFactory) logicalTimeFactory).makeTime(Double.parseDouble(TimeStamp.getText()));
                        log.getSuppliedArguments().add(new ClassValuePair("Timestamp", HLAfloat64Time.class, logicalTime.toString()));
                        log.setSimulationTime(TimeStamp.getText());
                        messageRetraction = rtiAmb.deleteObjectInstance(instanceHandle, UserSuppliedTag.getText().getBytes(Charset.forName("UTF-8")), logicalTime);
                        break;
                    }
                    case "HLAinteger64Time": {
                        HLAinteger64Time logicalTime
                                = ((HLAinteger64TimeFactory) logicalTimeFactory).makeTime(Long.parseLong(TimeStamp.getText()));
                        log.getSuppliedArguments().add(new ClassValuePair("Timestamp", HLAinteger64Time.class, logicalTime.toString()));
                        log.setSimulationTime(TimeStamp.getText());
                        messageRetraction = rtiAmb.deleteObjectInstance(instanceHandle, UserSuppliedTag.getText().getBytes(Charset.forName("UTF-8")), logicalTime);
                        break;
                    }
                    default:
                        throw new Exception("Unknown Time Implementation");
                }
                if (messageRetraction != null) {
                    log.getReturnedArguments().add(new ClassValuePair("Message retraction designator<Is Valid>",
                            MessageRetractionReturn.class, messageRetraction.handle.toString() +
                                    '<' + String.valueOf(messageRetraction.retractionHandleIsValid) + '>'));
                }
            }
            log.setDescription("Object instance deleted successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (DeletePrivilegeNotHeld | ObjectInstanceNotKnown | SaveInProgress |
                RestoreInProgress | FederateNotExecutionMember | NotConnected |
                RTIinternalError | InvalidLogicalTime ex) {
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
