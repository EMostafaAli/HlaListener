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
package ca.mali.hlalistener;

import ca.mali.fomparser.FomParser;
import static ca.mali.hlalistener.PublicVariables.*;

import hla.rti1516e.*;
import hla.rti1516e.encoding.*;
import hla.rti1516e.exceptions.*;
import org.apache.logging.log4j.*;

/**
 *
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class ListenerFederateAmb extends NullFederateAmbassador {

    //Logger
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    /**
     *
     * @param theObject
     * @param theAttributes
     * @param userSuppliedTag
     * @param sentOrdering
     * @param theTransport
     * @param reflectInfo
     * @throws FederateInternalError
     */
    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes, byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, SupplementalReflectInfo reflectInfo) throws FederateInternalError {
        try {
            stringEncoder = encoderFactory.createHLAunicodeString();
            stringEncoder.decode(theAttributes.get(RtiAmbInitializer.currentFDDHandle));
            System.out.println(stringEncoder.getValue());
            FomParser fomParser = new FomParser(stringEncoder.getValue());
            fomParser.getObjectClass();
            fomParser.getInteractionClass();
        } catch (DecoderException ex) {
            logger.log(Level.FATAL, "Exception in reflecting attribute values", ex);
        }
    }

    // <editor-fold desc="Chapter 4">
    //4.4
    @Override
    public void connectionLost(String faultDescription) throws FederateInternalError {
        logger.log(Level.ERROR, "Lost connection with RTI: " + faultDescription);
    }

    //4.8
    @Override
    public void reportFederationExecutions(FederationExecutionInformationSet theFederationExecutionInformationSet) throws FederateInternalError {
        for (FederationExecutionInformation theFederationExecution : theFederationExecutionInformationSet) {
            logger.log(Level.INFO, "Federation Execution Name: {}", theFederationExecution.federationExecutionName);
            logger.log(Level.INFO, "Federation Time Implementation: {}", theFederationExecution.logicalTimeImplementationName);
        }
    }

    //4.12
    @Override
    public void synchronizationPointRegistrationSucceeded(String synchronizationPointLabel) throws FederateInternalError {
        logger.log(Level.INFO, "Sync Point: {} has been successfully registered", synchronizationPointLabel);
    }

    //4.12
    @Override
    public void synchronizationPointRegistrationFailed(String synchronizationPointLabel, SynchronizationPointFailureReason reason) throws FederateInternalError {
        logger.log(Level.INFO, "Sync Point: {} registeration failed because: {}", synchronizationPointLabel, reason);
    }

    //4.13
    @Override
    public void announceSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag) throws FederateInternalError {
        logger.log(Level.INFO, "Sync Point: {} has been announced with the following Tag: {}", synchronizationPointLabel, new String(userSuppliedTag));
    }

    //4.15
    @Override
    public void federationSynchronized(String synchronizationPointLabel, FederateHandleSet failedToSyncSet) throws FederateInternalError {
        logger.log(Level.INFO, "Sync Point: {} has been achieved", synchronizationPointLabel);
        if (!failedToSyncSet.isEmpty()) {
            logger.log(Level.INFO, "The following federates failed to sync: ");
            for (FederateHandle federateHandle : failedToSyncSet) {
                logger.log(Level.INFO, "\t -{}", federateHandle.toString());
            }
        }
    }

    //4.17
    @Override
    public void initiateFederateSave(String label) throws FederateInternalError {
        logger.log(Level.INFO, "Federate save initiated with label: '{}'", label);
    }

    //4.17
    @Override
    public void initiateFederateSave(String label, LogicalTime time) throws FederateInternalError {
        logger.log(Level.INFO, "Federate save initiated with label: '{} at: '{}'", label, time);
    }

    //4.20
    @Override
    public void federationSaved() throws FederateInternalError {
        logger.log(Level.INFO, "Federation saved successfully");
    }

    //4.20
    @Override
    public void federationNotSaved(SaveFailureReason reason) throws FederateInternalError {
        logger.log(Level.INFO, "Federation save failed because: {}", reason);
    }

    //4.23
    @Override
    public void federationSaveStatusResponse(FederateHandleSaveStatusPair[] response) throws FederateInternalError {
        logger.log(Level.INFO, "Save status for each federate: ");
        for (FederateHandleSaveStatusPair statusPair : response) {
            logger.log(Level.INFO, "Federate '{}' status is '{}'", statusPair.handle, statusPair.status);
        }
    }

    //4.25
    @Override
    public void requestFederationRestoreSucceeded(String label) throws FederateInternalError {
        logger.log(Level.INFO, "Restore request for the label: '{}' successed", label);
    }

    //4.25
    @Override
    public void requestFederationRestoreFailed(String label) throws FederateInternalError {
        logger.log(Level.INFO, "Restore request for the label: '{}' failed", label);
    }

    //4.26
    @Override
    public void federationRestoreBegun() throws FederateInternalError {
        logger.log(Level.INFO, "Federation restore begun");
    }

    //4.27
    @Override
    public void initiateFederateRestore(String label, String federateName, FederateHandle federateHandle) throws FederateInternalError {
        logger.log(Level.INFO, "Federation restore initiated for the label: '{}', "
                + "Federate Name: '{}', and Federate handle: '{}'", label, federateName, federateHandle);
    }

    //4.29
    @Override
    public void federationRestored() throws FederateInternalError {
        logger.log(Level.INFO, "Federation restored successfully");
    }

    //4.29
    @Override
    public void federationNotRestored(RestoreFailureReason reason) throws FederateInternalError {
        logger.log(Level.INFO, "Federation restore failed because: {}", reason);
    }

    //4.32
    @Override
    public void federationRestoreStatusResponse(FederateRestoreStatus[] response) throws FederateInternalError {
        logger.log(Level.INFO, "Save restore for each federate: ");
        for (FederateRestoreStatus RestorePair : response) {
            logger.log(Level.INFO, "Prestore handle '{}' (postrestore handle '{}') status is '{}'",
                    RestorePair.preRestoreHandle, RestorePair.preRestoreHandle, RestorePair.status);
        }
    }
// </editor-fold>

// <editor-fold desc="Chapter 8">
    //8.3
    @Override
    public void timeRegulationEnabled(LogicalTime time) throws FederateInternalError {
        logger.log(Level.INFO, "Time regulation enabled successfully, current logical time: {}", time.toString());
    }

    //8.6
    @Override
    public void timeConstrainedEnabled(LogicalTime time) throws FederateInternalError {
        logger.log(Level.INFO, "Time constrained enabled successfully, current logical time: {}", time.toString());
    }

// </editor-fold>
}
