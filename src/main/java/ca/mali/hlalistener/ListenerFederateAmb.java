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

import ca.mali.fomparser.*;
import static ca.mali.hlalistener.PublicVariables.*;

import hla.rti1516e.*;
import hla.rti1516e.encoding.*;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.*;
import java.util.*;
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
            if (theAttributes.containsKey(currentFDDHandle)) {
                HLAunicodeString stringEncoder = encoderFactory.createHLAunicodeString();
                stringEncoder.decode(theAttributes.get(currentFDDHandle));
                fddObjectModel = new FddObjectModel(stringEncoder.getValue());
            }
//            System.out.println(stringEncoder.getValue());
//            FomParser fomParser = new FomParser(stringEncoder.getValue());
//            fomParser.getObjectClass();
//            fomParser.getInteractionClass();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Exception in reflecting attribute values", ex);
        }
    }

// <editor-fold desc="Chapter 4">
    //4.4
    @Override
    public void connectionLost(String faultDescription) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.4", "Connection Lost † service");
        log.setDescription("Lost connection with RTI");
        log.setLogType(LogEntryType.CALLBACK);
        log.getSuppliedArguments().add(new ClassValuePair("Fault Description", String.class, faultDescription));
        logEntries.add(log);
        logger.log(Level.ERROR, "Lost connection with RTI: " + faultDescription);
        logger.exit();
    }

    //4.8
    @Override
    public void reportFederationExecutions(FederationExecutionInformationSet theFederationExecutionInformationSet) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.8", "Report Federation Execution † service");
        log.setDescription("Federation execution list retrieved successfully");
        log.setLogType(LogEntryType.CALLBACK);
        for (FederationExecutionInformation theFederationExecution : theFederationExecutionInformationSet) {
            log.getSuppliedArguments().add(new ClassValuePair("Federation Name <Time Implementation>",
                    FederationExecutionInformation.class,
                    String.format("%1$s <%2$s>", theFederationExecution.federationExecutionName, theFederationExecution.logicalTimeImplementationName)));
            logger.log(Level.INFO, "Federation Execution Name: {}, Time Implementation: {}",
                    theFederationExecution.federationExecutionName, theFederationExecution.logicalTimeImplementationName);
        }
        logEntries.add(log);
        logger.exit();
    }

    //4.12
    @Override
    public void synchronizationPointRegistrationSucceeded(String synchronizationPointLabel) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.12", "Confirm Synchronization Point Registration † service");
        log.setDescription("Confirm Synchronization Point Registration Succeeded");
        log.setLogType(LogEntryType.CALLBACK);
        log.getSuppliedArguments().add(new ClassValuePair("Synchronization point label", String.class, synchronizationPointLabel));
        logEntries.add(log);
        logger.log(Level.INFO, "Sync Point: {} has been successfully registered", synchronizationPointLabel);
        logger.exit();
    }

    //4.12
    @Override
    public void synchronizationPointRegistrationFailed(String synchronizationPointLabel, SynchronizationPointFailureReason reason) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.12", "Confirm Synchronization Point Registration † service");
        log.setDescription("Confirm Synchronization Point Registration Failed");
        log.setLogType(LogEntryType.CALLBACK);
        log.getSuppliedArguments().add(new ClassValuePair("Synchronization point label", String.class, synchronizationPointLabel));
        log.getSuppliedArguments().add(new ClassValuePair("Synchronization point failure reason", reason.getClass(), reason.toString()));
        logEntries.add(log);
        logger.log(Level.INFO, "Sync Point: {} registration failed because: {}", synchronizationPointLabel, reason);
        logger.exit();
    }

    //4.13
    @Override
    public void announceSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.13", "Announce Synchronization Point † service");
        log.setDescription("Synchronization point announced");
        log.setLogType(LogEntryType.CALLBACK);
        log.getSuppliedArguments().add(new ClassValuePair("Synchronization point label", String.class, synchronizationPointLabel));
        if (userSuppliedTag.length > 0) {
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", byte.class, Arrays.toString(userSuppliedTag)));
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", String.class, new String(userSuppliedTag)));
        }
        logEntries.add(log);
        logger.log(Level.INFO, "Sync Point: {} has been announced with the following Tag: {}", synchronizationPointLabel, new String(userSuppliedTag));
        logger.exit();
    }

    //4.15
    @Override
    public void federationSynchronized(String synchronizationPointLabel, FederateHandleSet failedToSyncSet) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.15", "Federation Synchronized † service");
        log.setDescription("Federation synchronized");
        log.setLogType(LogEntryType.CALLBACK);
        log.getSuppliedArguments().add(new ClassValuePair("Synchronization point label", String.class, synchronizationPointLabel));
        logger.log(Level.INFO, "Sync Point: {} has been achieved", synchronizationPointLabel);
        if (!failedToSyncSet.isEmpty()) {
            int i = 1;
            logger.log(Level.INFO, "The following federates failed to sync: ");
            for (FederateHandle federateHandle : failedToSyncSet) {
                log.getSuppliedArguments().add(new ClassValuePair("failed federate " + i++, FederateHandle.class, federateHandle.toString()));
                logger.log(Level.INFO, "\t *{}", federateHandle.toString());
            }
        }
        logEntries.add(log);
        logger.exit();
    }

    //4.17
    @Override
    public void initiateFederateSave(String label) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.17", "Initiate Federate Save † service");
        log.setDescription("Federate save initiated");
        log.setLogType(LogEntryType.CALLBACK);
        log.getSuppliedArguments().add(new ClassValuePair("Federation save label", String.class, label));
        logEntries.add(log);
        logger.log(Level.INFO, "Federate save initiated with label: '{}'", label);
        logger.exit();
    }

    //4.17
    @Override
    public void initiateFederateSave(String label, LogicalTime time) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.17", "Initiate Federate Save † service");
        log.setDescription("Federate save initiated");
        log.setLogType(LogEntryType.CALLBACK);
        log.getSuppliedArguments().add(new ClassValuePair("Federation save label", String.class, label));
        log.getSuppliedArguments().add(new ClassValuePair("Timestamp", LogicalTime.class, time.toString()));
        logEntries.add(log);
        logger.log(Level.INFO, "Federate save initiated with label: '{} at: '{}'", label, time);
        logger.exit();
    }

    //4.20
    @Override
    public void federationSaved() throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.20", "Federation Saved † service");
        log.setDescription("Federation saved successfully");
        log.setLogType(LogEntryType.CALLBACK);
        logEntries.add(log);
        logger.log(Level.INFO, "Federation saved successfully");
        logger.exit();
    }

    //4.20
    @Override
    public void federationNotSaved(SaveFailureReason reason) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.20", "Federation Saved † service");
        log.setDescription("Federation save failed");
        log.getSuppliedArguments().add(new ClassValuePair("Failure reason", SaveFailureReason.class, reason.toString()));
        log.setLogType(LogEntryType.CALLBACK);
        logEntries.add(log);
        logger.log(Level.INFO, "Federation save failed because: {}", reason);
        logger.exit();
    }

    //4.23
    @Override
    public void federationSaveStatusResponse(FederateHandleSaveStatusPair[] response) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.23", "Federation Save Status Response † service");
        log.setDescription("Save status for each federate");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Save status for each federate: ");
        for (FederateHandleSaveStatusPair statusPair : response) {
            log.getSuppliedArguments().add(new ClassValuePair("Federate <save status>",
                    FederateHandleSaveStatusPair.class, String.format("%1$s <%2$s>", statusPair.handle, statusPair.status)));
//            log.getSuppliedArguments().add(new ClassValuePair("Federate <save status>", FederateHandleSaveStatusPair.class, MessageFormat.format("{0} <{1}>", statusPair.handle, statusPair.status)));
            logger.log(Level.INFO, "Federate '{}' status is '{}'", statusPair.handle, statusPair.status);
        }
        logEntries.add(log);
        logger.exit();
    }

    //4.25
    @Override
    public void requestFederationRestoreSucceeded(String label) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.25", "Confirm Federation Restoration Request † service");
        log.getSuppliedArguments().add(new ClassValuePair("Federation save label", String.class, label));
        log.setDescription("Federation restoration requested successfully");
        log.setLogType(LogEntryType.CALLBACK);
        logEntries.add(log);
        logger.log(Level.INFO, "Restore request for the label: '{}' succeeded", label);
        logger.exit();
    }

    //4.25
    @Override
    public void requestFederationRestoreFailed(String label) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.25", "Confirm Federation Restoration Request † service");
        log.getSuppliedArguments().add(new ClassValuePair("Federation save label", String.class, label));
        log.setDescription("Federation restoration request failed");
        log.setLogType(LogEntryType.CALLBACK);
        logEntries.add(log);
        logger.log(Level.INFO, "Restore request for the label: '{}' failed", label);
        logger.exit();
    }

    //4.26
    @Override
    public void federationRestoreBegun() throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.26", "Federation Restore Begun † service");
        log.setDescription("Federation restoration can restart now");
        log.setLogType(LogEntryType.CALLBACK);
        logEntries.add(log);
        logger.log(Level.INFO, "Federation restore begun");
        logger.exit();
    }

    //4.27
    @Override
    public void initiateFederateRestore(String label, String federateName, FederateHandle federateHandle) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.27", "Initiate Federate Restore † service");
        log.getSuppliedArguments().add(new ClassValuePair("Federation save label", String.class, label));
        log.getSuppliedArguments().add(new ClassValuePair("Federate name", String.class, federateName));
        log.getSuppliedArguments().add(new ClassValuePair("Federate Handle", FederateHandle.class, federateHandle.toString()));
        log.setDescription("Federation restoration initiated");
        log.setLogType(LogEntryType.CALLBACK);
        logEntries.add(log);
        logger.log(Level.INFO, "Federation restore initiated for the label: '{}', "
                + "Federate Name: '{}', and Federate handle: '{}'", label, federateName, federateHandle);
        logger.exit();
    }

    //4.29
    @Override
    public void federationRestored() throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.29", "Federation Restored † service");
        log.setDescription("Federation restored successfully");
        log.setLogType(LogEntryType.CALLBACK);
        logEntries.add(log);
        logger.log(Level.INFO, "Federation restored successfully");
        logger.exit();
    }

    //4.29
    @Override
    public void federationNotRestored(RestoreFailureReason reason) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.29", "Federation Restored † service");
        log.getSuppliedArguments().add(new ClassValuePair("Failure reason", RestoreFailureReason.class, reason.toString()));
        log.setDescription("Federation restoration failed");
        log.setLogType(LogEntryType.CALLBACK);
        logEntries.add(log);
        logger.log(Level.INFO, "Federation restore failed because: {}", reason);
        logger.exit();
    }

    //4.32
    @Override
    public void federationRestoreStatusResponse(FederateRestoreStatus[] response) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("4.32", "Federation Restore Status Response † service");
        log.setDescription("Restore status for each federate");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Save restore for each federate: ");
        for (FederateRestoreStatus RestorePair : response) {
            log.getSuppliedArguments().add(new ClassValuePair("(Pre-restore handle, Post-restore handle) <restore status>",
                    FederateRestoreStatus.class, String.format("(%1$s,%2$s) <%3$s>",
                            RestorePair.preRestoreHandle, RestorePair.preRestoreHandle, RestorePair.status)));
            logger.log(Level.INFO, "Pre-restore handle '{}' (post-restore handle '{}') status is '{}'",
                    RestorePair.preRestoreHandle, RestorePair.preRestoreHandle, RestorePair.status);
        }
        logEntries.add(log);
        logger.exit();
    }
// </editor-fold>

// <editor-fold desc="Chapter 5">
    //5.10
    @Override
    public void startRegistrationForObjectClass(ObjectClassHandle theClass) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("5.10", "Start Registration For Object Class † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Class Handle", ObjectClassHandle.class, theClass.toString()));
        log.setDescription("Start registeration for object class received");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Start registration for object class: {}", theClass.toString());
        logEntries.add(log);
        logger.exit();
    }

    //5.11
    @Override
    public void stopRegistrationForObjectClass(ObjectClassHandle theClass) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("5.11", "Stop Registration For Object Class † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Class Handle", ObjectClassHandle.class, theClass.toString()));
        log.setDescription("Stop registeration for object class received");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Stop registration for object class: {}", theClass.toString());
        logEntries.add(log);
        logger.exit();
    }

    //5.12
    @Override
    public void turnInteractionsOn(InteractionClassHandle theHandle) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("5.12", "Turn Interactions On † service");
        log.getSuppliedArguments().add(new ClassValuePair("Interaction Class Handle", InteractionClassHandle.class, theHandle.toString()));
        log.setDescription("Turn interaction on received");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Turn interaction on for interaction class: {}", theHandle.toString());
        logEntries.add(log);
        logger.exit();
    }

    //5.13
    @Override
    public void turnInteractionsOff(InteractionClassHandle theHandle) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("5.13", "Turn Interactions Off † service");
        log.getSuppliedArguments().add(new ClassValuePair("Interaction Class Handle", InteractionClassHandle.class, theHandle.toString()));
        log.setDescription("Turn interaction off received");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Turn interaction off for interaction class: {}", theHandle.toString());
        logEntries.add(log);
        logger.exit();
    }
// </editor-fold>

// <editor-fold desc="Chapter 8">
    //8.3
    @Override
    public void timeRegulationEnabled(LogicalTime time) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("8.3", "Time Regulation Enabled † service");
        log.getSuppliedArguments().add(new ClassValuePair("Current logical time", LogicalTime.class, time.toString()));
        log.setDescription("Time Regulation enabled successfully");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Time regulation enabled successfully, current logical time: {}", time.toString());
        logEntries.add(log);
        logger.exit();
    }

    //8.6
    @Override
    public void timeConstrainedEnabled(LogicalTime time) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("8.6", "Time Constrained Enabled † service");
        log.getSuppliedArguments().add(new ClassValuePair("Current logical time", LogicalTime.class, time.toString()));
        log.setDescription("Time Constrained enabled successfully");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Time constrained enabled successfully, current logical time: {}", time.toString());
        logEntries.add(log);
        logger.exit();
    }

    //8.13
    @Override
    public void timeAdvanceGrant(LogicalTime theTime) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("8.13", "Time Advance Grant † service");
        log.getSuppliedArguments().add(new ClassValuePair("Current logical time", LogicalTime.class, theTime.toString()));
        log.setDescription("Time Advance Granted");
//        log.setSimulationTime(theTime.);
        switch (logicalTimeFactory.getName()) {
            case "HLAfloat64Time": {
                log.setSimulationTime(String.valueOf(((HLAfloat64Time) theTime).getValue()));
                break;
            }
            case "HLAinteger64Time": {
                log.setSimulationTime(String.valueOf(((HLAinteger64Time) theTime).getValue()));
                break;
            }
        }
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Time Advance Granted, current logical time: {}", theTime.toString());
        logEntries.add(log);
        currentLogicalTime = theTime;
        logger.exit();
    }

// </editor-fold>
}
