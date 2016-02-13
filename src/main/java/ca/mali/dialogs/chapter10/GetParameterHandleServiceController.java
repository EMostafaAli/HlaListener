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
package ca.mali.dialogs.chapter10;

import ca.mali.customcontrol.ParameterListController;
import ca.mali.fomparser.ParameterFDD;
import ca.mali.hlalistener.ClassValuePair;
import ca.mali.hlalistener.LogEntry;
import ca.mali.hlalistener.LogEntryType;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.exceptions.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static ca.mali.hlalistener.PublicVariables.*;

/**
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class GetParameterHandleServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private ParameterListController parameterListController;

    @FXML
    private Button OkButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        parameterListController.setFddObjectModel(fddObjectModel);
        logger.exit();
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        logger.entry();
        ((Stage) OkButton.getScene().getWindow()).close();
        logger.exit();
    }

    @FXML
    private void OK_click(ActionEvent event) {
        logger.entry();
        Map<String, List<ParameterFDD>> list = parameterListController.getList();
        for (String key : list.keySet()) {
            List<ParameterFDD> parameters = list.get(key);
            if (!parameters.isEmpty()) {
                for (ParameterFDD parameter : parameters) {
                    LogEntry log = new LogEntry("10.17", "Get Parameter Handle service");
                    try {
                        log.getSuppliedArguments().add(new ClassValuePair("Interaction<handle>",
                                InteractionClassHandle.class, key + '<' + fddObjectModel.getInteractionClasses().get(key).getHandle().toString() + '>'));
                        log.getSuppliedArguments().add(new ClassValuePair("Parameter name", String.class, parameter.getName()));
                        ParameterHandle parameterHandle = rtiAmb.getParameterHandle(fddObjectModel.getInteractionClasses().get(key).getHandle(), parameter.getName());
                        log.getReturnedArguments().add(new ClassValuePair("Parameter handle", ParameterHandle.class, parameterHandle.toString()));
                        log.setDescription("Parameter handle retrieved successfully");
                        log.setLogType(LogEntryType.REQUEST);
                    } catch (NameNotFound | InvalidInteractionClassHandle | FederateNotExecutionMember |
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
                }
            }
        }
        ((Stage) OkButton.getScene().getWindow()).close();
        logger.exit();
    }
}
