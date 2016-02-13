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

package ca.mali.dialogs.chapter9;

import ca.mali.customcontrol.AttributeRegionCollectionController;
import ca.mali.fomparser.AttributeFDD;
import ca.mali.hlalistener.ClassValuePair;
import ca.mali.hlalistener.LogEntry;
import ca.mali.hlalistener.LogEntryType;
import hla.rti1516e.*;
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
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ca.mali.hlalistener.PublicVariables.*;

/**
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class UnsubscribeObjectClassAttributesWithRegionsServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private AttributeRegionCollectionController regionListController;

    @FXML
    private Button OkButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.entry();
        if (fddObjectModel != null) {
            regionListController.setFddObjectModel(fddObjectModel);
            OkButton.setDisable(false);
        }
        logger.exit();
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        logger.entry();
        ((Stage) regionListController.getScene().getWindow()).close();
        logger.exit();
    }

    @FXML
    private void Ok_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("9.9", "Unsubscribe Object Class Attributes With Regions service");
        try {
            AttributeRegionCollectionController.AttributeRegionResult attributeRegionResult = regionListController.getAttributeRegionResult();
            log.getSuppliedArguments().add(new ClassValuePair("Object Class<handle>",
                    InteractionClassHandle.class, attributeRegionResult.getObjectClassFDD().getFullName() +
                    '<' + attributeRegionResult.getObjectClassFDD().getHandle().toString() + '>'));
            int count = attributeRegionResult.getAttributeFddList().size();
            AttributeSetRegionSetPairList attributeRegionAssociations = rtiAmb.getAttributeSetRegionSetPairListFactory().create(count);
            for (int i = 0; i < count; i++) {
                AttributeHandleSet attributeHandles = rtiAmb.getAttributeHandleSetFactory().create();
                attributeHandles.addAll(attributeRegionResult.getAttributeFddList().get(i).stream().map(AttributeFDD::getHandle).collect(Collectors.toList()));
                RegionHandleSet regionHandleSet = rtiAmb.getRegionHandleSetFactory().create();
                regionHandleSet.addAll(attributeRegionResult.getRegionList().get(i).stream().collect(Collectors.toList()));
                AttributeRegionAssociation regionAssociation = new AttributeRegionAssociation(attributeHandles, regionHandleSet);
                attributeRegionAssociations.add(regionAssociation);
                log.getSuppliedArguments().add(new ClassValuePair(
                        "Attribute region association " + (i+1), AttributeRegionAssociation.class, attributeRegionAssociations.toString()));
            }
            rtiAmb.unsubscribeObjectClassAttributesWithRegions(attributeRegionResult.getObjectClassFDD().getHandle(), attributeRegionAssociations);
            log.setDescription("Object class with regions unsubscribed successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (NotConnected | FederateNotExecutionMember | RestoreInProgress | RegionNotCreatedByThisFederate |
                InvalidRegion | SaveInProgress | RTIinternalError | AttributeNotDefined | ObjectClassNotDefined ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        ((Stage) regionListController.getScene().getWindow()).close();
        logger.exit();
    }
}
