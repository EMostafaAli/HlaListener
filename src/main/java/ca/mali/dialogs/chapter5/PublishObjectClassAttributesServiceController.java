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
package ca.mali.dialogs.chapter5;

import ca.mali.customcontrol.AttributeListController;
import static ca.mali.hlalistener.PublicVariables.*;
import ca.mali.fomparser.*;
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
public class PublishObjectClassAttributesServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private AttributeListController attributeListController;

    @FXML
    private Button OkButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        attributeListController.setFddObjectModel(fddObjectModel);
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
        Map<String, List<AttributeFDD>> list = attributeListController.getList();
        for (String key : list.keySet()) {
            List<AttributeFDD> attributes = list.get(key);
            if (!attributes.isEmpty()) {
                LogEntry log = new LogEntry("5.2", "Publish Object Class Attributes service");
                try {
                    log.getSuppliedArguments().add(new ClassValuePair("Class<handle>",
                            ObjectClassHandle.class, key + '<' + fddObjectModel.getObjectClasses().get(key).getHandle().toString() + '>'));
                    AttributeHandleSet set = rtiAmb.getAttributeHandleSetFactory().create();
                    attributes.forEach((item) -> {
                        set.add(item.getHandle());
                        log.getSuppliedArguments().add(new ClassValuePair("Attribute<handle>",
                                AttributeHandle.class, item.getName() + '<' + item.getHandle().toString() + '>'));
                    });
                    rtiAmb.publishObjectClassAttributes(fddObjectModel.getObjectClasses().get(key).getHandle(), set);
                    log.setDescription("Attributes published successfully");
                    log.setLogType(LogEntryType.REQUEST);
                } catch (FederateNotExecutionMember | NotConnected | AttributeNotDefined |
                        ObjectClassNotDefined | SaveInProgress | RestoreInProgress | RTIinternalError ex) {
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
        ((Stage) OkButton.getScene().getWindow()).close();
        logger.exit();
    }
}
