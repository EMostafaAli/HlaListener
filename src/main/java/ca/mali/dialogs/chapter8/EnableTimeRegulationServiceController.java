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
package ca.mali.dialogs.chapter8;

import ca.mali.hlalistener.*;
import static ca.mali.hlalistener.PublicVariables.*;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.*;
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
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class EnableTimeRegulationServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private Spinner<Double> Lookahead;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        SpinnerValueFactory sVF = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0, .1);
        Lookahead.setValueFactory(sVF);
        logger.exit();
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        logger.entry();
        ((Stage) Lookahead.getScene().getWindow()).close();
        logger.exit();
    }

    @FXML
    private void Ok_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("8.2", "Enable Time Regulation service");
        try {
            if (logicalTimeFactory == null) { //means not connected or federate is not execution member
                rtiAmb.getTimeFactory(); //this line will raise the appropriate exception
            }
            switch (logicalTimeFactory.getName()) {
                case "HLAfloat64Time": {
                    HLAfloat64Interval lookaheadInterval = ((HLAfloat64TimeFactory) logicalTimeFactory).makeInterval(Lookahead.getValue());
                    log.getSuppliedArguments().add(new ClassValuePair("lookahead", HLAfloat64Interval.class, Lookahead.getValue().toString()));
                    rtiAmb.enableTimeRegulation(lookaheadInterval);
                    break;
                }
                case "HLAinteger64Time": {
                    HLAinteger64Interval lookaheadInterval
                            = ((HLAinteger64TimeFactory) logicalTimeFactory).makeInterval(Lookahead.getValue().longValue());
                    log.getSuppliedArguments().add(new ClassValuePair("lookahead", HLAfloat64Interval.class, Lookahead.getValue().toString()));
                    rtiAmb.enableTimeRegulation(lookaheadInterval);
                    break;
                }
                default:
                    throw new Exception("Unknown Time Implementation");
            }
            log.setDescription("Time Regulation requested successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (TimeRegulationAlreadyEnabled | InvalidLookahead |
                InTimeAdvancingState | RequestForTimeRegulationPending |
                FederateNotExecutionMember | SaveInProgress | RestoreInProgress |
                NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        ((Stage) Lookahead.getScene().getWindow()).close();
        logEntries.add(log);
        logger.exit();
    }
}
