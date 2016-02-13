/*
 * Copyright (c) 2015-2016, Mostafa Ali (engabdomostafa@gmail.com)
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met: Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer. Redistributions
 * in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package ca.mali.dialogs.chapter8;

import ca.mali.hlalistener.ClassValuePair;
import ca.mali.hlalistener.LogEntry;
import ca.mali.hlalistener.LogEntryType;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import hla.rti1516e.time.HLAinteger64Time;
import hla.rti1516e.time.HLAinteger64TimeFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

import static ca.mali.hlalistener.PublicVariables.*;

/**
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class NextMessageRequestServiceController implements Initializable {
    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private Spinner<Double> LogicalTimeSpin;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
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

        SpinnerValueFactory sVF = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0, step);
        sVF.setValue(currentValue);
        LogicalTimeSpin.setValueFactory(sVF);
        logger.exit();
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        logger.entry();
        ((Stage) LogicalTimeSpin.getScene().getWindow()).close();
        logger.exit();
    }

    @FXML
    private void Ok_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("8.10", "Next Message Request service");
        try {
            if (logicalTimeFactory == null) { //means not connected or federate is not execution member
                rtiAmb.getTimeFactory(); //this line will raise the appropriate exception
            }
            switch (logicalTimeFactory.getName()) {
                case "HLAfloat64Time": {
                    HLAfloat64Time logicalTime = ((HLAfloat64TimeFactory) logicalTimeFactory).makeTime(LogicalTimeSpin.getValue());
                    log.getSuppliedArguments().add(new ClassValuePair("Logical Time", HLAfloat64Time.class, logicalTime.toString()));
                    log.setSimulationTime(LogicalTimeSpin.getValue().toString());
                    rtiAmb.nextMessageRequest(logicalTime);
                    break;
                }
                case "HLAinteger64Time": {
                    if (!(LogicalTimeSpin.getValue() % 1 == 0)) {
                        throw new InvalidLogicalTime("The federate time is HLAinteger64Time, logical time cannot be double");
                    }
                    HLAinteger64Time logicalTime
                            = ((HLAinteger64TimeFactory) logicalTimeFactory).makeTime(LogicalTimeSpin.getValue().longValue());
                    log.getSuppliedArguments().add(new ClassValuePair("Logical Time", HLAinteger64Time.class, logicalTime.toString()));
                    log.setSimulationTime(String.valueOf(LogicalTimeSpin.getValue().longValue()));
                    rtiAmb.nextMessageRequest(logicalTime);
                    break;
                }
                default:
                    throw new Exception("Unknown Time Implementation");
            }
            log.setDescription("Next Message requested successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (LogicalTimeAlreadyPassed | InvalidLogicalTime |
                InTimeAdvancingState | RequestForTimeRegulationPending |
                RequestForTimeConstrainedPending | SaveInProgress |
                RestoreInProgress | FederateNotExecutionMember | NotConnected |
                RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        ((Stage) LogicalTimeSpin.getScene().getWindow()).close();
        logEntries.add(log);
        logger.exit();
    }
}