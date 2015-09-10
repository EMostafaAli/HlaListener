/*
 * Copyright (c) 2015, Mostafa Ali
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met: Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer. Redistributions
 * in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 *  CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 *  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *   NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *   OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *   DAMAGE.
 */
package ca.mali.dialogs.chapter9;

import ca.mali.customcontrol.AttributeRegionCollectionController;
import ca.mali.hlalistener.LogEntry;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

import static ca.mali.hlalistener.PublicVariables.fddObjectModel;
import static ca.mali.hlalistener.PublicVariables.logEntries;

/**
 * FXML Controller class
 *
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class RegisterObjectInstanceWithRegionsServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private AttributeRegionCollectionController attributeRegionCollection;

    @FXML
    private TextField InstanceNameTextField;

    @FXML
    private Button OkButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        attributeRegionCollection.setFddObjectModel(fddObjectModel);
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
        LogEntry log = new LogEntry("9.3", "Commit Region Modifications service");
//        try {
//            RegionHandleSet regionHandleSet = rtiAmb.getRegionHandleSetFactory().create();
//            for (RegionHandle handle : regionListController.getRegions()) {
//                log.getSuppliedArguments().add(new ClassValuePair("Region handle", RegionHandle.class, handle.toString()));
//                regionHandleSet.add(handle);
//            }
//            rtiAmb.commitRegionModifications(regionHandleSet);
//            log.setDescription("Region modifications committed successfully");
//            log.setLogType(LogEntryType.REQUEST);
//        } catch (RestoreInProgress | SaveInProgress | FederateNotExecutionMember | RTIinternalError | InvalidRegion |
//                RegionNotCreatedByThisFederate | NotConnected ex) {
//            log.setException(ex);
//            log.setLogType(LogEntryType.ERROR);
//            logger.log(Level.ERROR, ex.getMessage(), ex);
//        } catch (Exception ex) {
//            log.setException(ex);
//            log.setLogType(LogEntryType.FATAL);
//            logger.log(Level.FATAL, ex.getMessage(), ex);
//        }
        logEntries.add(log);
        ((Stage) OkButton.getScene().getWindow()).close();
        logger.exit();
    }
}