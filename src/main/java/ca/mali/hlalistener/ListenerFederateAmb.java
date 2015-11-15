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
package ca.mali.hlalistener;

import ca.mali.fomparser.*;
import hla.rti1516e.*;
import hla.rti1516e.encoding.HLAunicodeString;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAinteger64Time;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static ca.mali.hlalistener.PublicVariables.*;

/**
 *
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class ListenerFederateAmb extends NullFederateAmbassador {

    //Logger
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    @Override
    public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes, byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, SupplementalReflectInfo reflectInfo) throws FederateInternalError {
        try {
            if (theAttributes.containsKey(currentFDDHandle)) {
                HLAunicodeString stringEncoder = encoderFactory.createHLAunicodeString();
                stringEncoder.decode(theAttributes.get(currentFDDHandle));
                fddObjectModel = new FddObjectModel(stringEncoder.getValue());
            }
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

// <editor-fold desc="Chapter 6">
    //6.3
    @Override
    public void objectInstanceNameReservationSucceeded(String objectName) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.3", "Object Instance Name Reserved † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Name", String.class, objectName));
        log.setDescription("Object Instance name reserved successfully");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Object Instance name {} reserved successfully", objectName);
        logEntries.add(log);
        logger.exit();
    }

    //6.3
    @Override
    public void objectInstanceNameReservationFailed(String objectName) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.3", "Object Instance Name Reserved † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Name", String.class, objectName));
        log.setDescription("Object Instance name reservation failed");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Object Instance name {} reservation failed", objectName);
        logEntries.add(log);
        logger.exit();
    }

    //6.6
    @Override
    public void multipleObjectInstanceNameReservationSucceeded(Set<String> objectNames) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.6", "Multiple Object Instance Names Reserved † service");
        log.getSuppliedArguments().add(new ClassValuePair("Name Set", Set.class, objectNames.toString()));
        log.setDescription("Object Instance names reserved successfully");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Name Set {} reserved successfully", objectNames);
        logEntries.add(log);
        logger.exit();
    }

    //6.6
    @Override
    public void multipleObjectInstanceNameReservationFailed(Set<String> objectNames) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.6", "Multiple Object Instance Names Reserved † service");
        log.getSuppliedArguments().add(new ClassValuePair("Name Set", Set.class, objectNames.toString()));
        log.setDescription("Object Instance names reservation failed");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Name Set {} reservation failed", objectNames);
        logEntries.add(log);
        logger.exit();
    }

    //6.9
    @Override
    public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass, String objectName) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.9", "Discover Object Instance † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Object Class Handle", ObjectClassHandle.class, theObjectClass.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Object Name", String.class, objectName));
        log.setDescription("Discover object instance");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Discover object instance: {}, object class handle: {}, name: {}", theObject, theObjectClass, objectName);
        Optional<ObjectClassFDD> findObjectClass = fddObjectModel.getObjectClasses().values().stream().filter(a -> a.getHandle() == theObjectClass).findFirst();
        if (findObjectClass.isPresent()) {
            try {
                objectInstances.put(theObject, new ObjectInstanceFDD(theObject, findObjectClass.get()));
            } catch (Exception ex) {
                log.setException(ex);
                log.setLogType(LogEntryType.ERROR);
                logger.log(Level.ERROR, ex.getMessage(), ex);
            }
        }
        logEntries.add(log);
        logger.exit();
    }

    //6.9
    @Override
    public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass, String objectName, FederateHandle producingFederate) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.9", "Discover Object Instance † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Object Class Handle", ObjectClassHandle.class, theObjectClass.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Object Name", String.class, objectName));
        log.getSuppliedArguments().add(new ClassValuePair("Federate Handle", FederateHandle.class, producingFederate.toString()));
        log.setDescription("Discover object instance");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Discover object instance: {}, object class handle: {}, name: {}, Federate {}", theObject, theObjectClass, objectName, producingFederate);
        Optional<ObjectClassFDD> findObjectClass = fddObjectModel.getObjectClasses().values().stream().filter(a -> a.getHandle() == theObjectClass).findFirst();
        if (findObjectClass.isPresent()) {
            try {
                objectInstances.put(theObject, new ObjectInstanceFDD(theObject, findObjectClass.get()));
            } catch (Exception ex) {
                log.setException(ex);
                log.setLogType(LogEntryType.ERROR);
                logger.log(Level.ERROR, ex.getMessage(), ex);
            }
        }
        logEntries.add(log);
        logger.exit();
    }

    //6.13
    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters, byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        logger.entry();
        logger.log(Level.INFO, "Interaction class: {}, User Supplied Tag: {}, Send Order: {}, Transportation: {}," +
                        " Producing Federate ({}) <{}>, Sent region ({}) <{}>",
                interactionClass, userSuppliedTag, sentOrdering, theTransport, receiveInfo.hasProducingFederate(),
                receiveInfo.getProducingFederate(), receiveInfo.hasSentRegions(), receiveInfo.getSentRegions());
        LogEntry log = new LogEntry("6.13", "Receive Interaction † service");
        Optional<InteractionClassFDD> interactionClassFDD = fddObjectModel.getInteractionClasses().values().stream().filter(a -> a.getHandle().equals(interactionClass)).findFirst();
        if (!interactionClassFDD.isPresent()){
            logger.log(Level.ERROR,"The received interaction cannot be found in the FddObjectModel");
            return;
        }
        log.getSuppliedArguments().add(new ClassValuePair("Interaction Class Name <Handle>", InteractionClassHandle.class,
                String.format("%1$s <%2$s>", interactionClassFDD.get().getName(), interactionClass.toString())));
        interactionClassFDD.get().getParameters().forEach(parameterFDD -> {
            if (theParameters.containsKey(parameterFDD.getHandle())){
                logger.log(Level.INFO, "Parameter handle: {}, value: {}", parameterFDD.getHandle(), Arrays.toString(theParameters.get(parameterFDD.getHandle())));
                log.getSuppliedArguments().add(new ClassValuePair("Parameter <Handle>", ParameterHandle.class,
                        String.format("%1$s <%2$s>",parameterFDD.getName(), parameterFDD.getHandle().toString())));
                log.getSuppliedArguments().add(new ClassValuePair("Parameter Value (encoded)", byte[].class,
                        Arrays.toString(theParameters.get(parameterFDD.getHandle()))));
                if (fddObjectModel.getSimpleDataTypeMap().keySet().contains(parameterFDD.getDataType())){
                    SimpleFDDDataType simpleFDDDataType = fddObjectModel.getSimpleDataTypeMap().get(parameterFDD.getDataType());
                    String value = fddObjectModel.getBasicDataTypeMap().get(simpleFDDDataType.getRepresentation()).DecodeValue(theParameters.get(parameterFDD.getHandle()));
                    log.getSuppliedArguments().add(new ClassValuePair("Parameter Value", Object.class, value));
                } else if (fddObjectModel.getEnumeratedDataTypeMap().keySet().contains(parameterFDD.getDataType())){ //Enumerated data type
                    EnumeratedFDDDataType enumerated = fddObjectModel.getEnumeratedDataTypeMap().get(parameterFDD.getDataType());
                    String value = fddObjectModel.getBasicDataTypeMap().get(enumerated.getRepresentation()).DecodeValue(theParameters.get(parameterFDD.getHandle()));
                    Optional<EnumeratedFDDDataType.Enumerator> first = enumerated.getEnumerator().stream().filter(e -> e.getValues().contains(value)).findFirst();
                    if (first.isPresent()){
                        log.getSuppliedArguments().add(new ClassValuePair("Parameter Value", Object.class,
                                String.format("%1$s <%2$s>", first.get().getName(), value)));
                    }
                }
            }
        });
        if (userSuppliedTag.length > 0) {
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", byte.class, Arrays.toString(userSuppliedTag)));
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", String.class, new String(userSuppliedTag)));
        }
        log.getSuppliedArguments().add(new ClassValuePair("Send Order", OrderType.class, sentOrdering.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Transportation type", TransportationTypeHandle.class, theTransport.toString()));
        if (receiveInfo.hasProducingFederate()) {
            log.getSuppliedArguments().add(new ClassValuePair("Producing Federate", SupplementalReceiveInfo.class, String.valueOf(receiveInfo.getProducingFederate())));
        }
        if (receiveInfo.hasSentRegions()) {
            log.getSuppliedArguments().add(new ClassValuePair("Sent Region", SupplementalReceiveInfo.class, String.valueOf(receiveInfo.getSentRegions())));
        }
        log.setDescription("Receive Interaction");
        log.setLogType(LogEntryType.CALLBACK);
        logEntries.add(log);
        logger.exit();
    }

    //6.13
    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters, byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime, OrderType receivedOrdering, SupplementalReceiveInfo receiveInfo) throws FederateInternalError {

    }

    //6.13
    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters, byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime, OrderType receivedOrdering, MessageRetractionHandle retractionHandle, SupplementalReceiveInfo receiveInfo) throws FederateInternalError {

    }

    //6.15
    @Override
    public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering, SupplementalRemoveInfo removeInfo) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.15", "Remove Object Instance † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        if (userSuppliedTag.length > 0) {
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", byte.class, Arrays.toString(userSuppliedTag)));
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", String.class, new String(userSuppliedTag)));
        }
        log.getSuppliedArguments().add(new ClassValuePair("Send Order", OrderType.class, sentOrdering.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Supplemental Remove Info (has federate)", SupplementalRemoveInfo.class, String.valueOf(removeInfo.hasProducingFederate())));
        if (removeInfo.hasProducingFederate()) {
            log.getSuppliedArguments().add(new ClassValuePair("Supplemental Remove Info (federate)", SupplementalRemoveInfo.class, removeInfo.getProducingFederate().toString()));
        }
        log.setDescription("Remove object instance");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Remove object instance: {}, User Supplied Tag: {}, Send Order: {}, Supplemental Remove Info ({}) <{}>",
                theObject, userSuppliedTag, sentOrdering, removeInfo.hasProducingFederate(), removeInfo.getProducingFederate());
        logEntries.add(log);
        logger.exit();
    }

    //6.15
    @Override
    public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering, LogicalTime theTime, OrderType receivedOrdering, SupplementalRemoveInfo removeInfo) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.15", "Remove Object Instance † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        if (userSuppliedTag.length > 0) {
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", byte.class, Arrays.toString(userSuppliedTag)));
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", String.class, new String(userSuppliedTag)));
        }
        log.getSuppliedArguments().add(new ClassValuePair("Send Order", OrderType.class, sentOrdering.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Logical Time", LogicalTime.class, theTime.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Receive Order", OrderType.class, receivedOrdering.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Supplemental Remove Info (has federate)", SupplementalRemoveInfo.class, String.valueOf(removeInfo.hasProducingFederate())));
        if (removeInfo.hasProducingFederate()) {
            log.getSuppliedArguments().add(new ClassValuePair("Supplemental Remove Info (federate)", SupplementalRemoveInfo.class, removeInfo.getProducingFederate().toString()));
        }
        log.setDescription("Remove object instance");
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
        logger.log(Level.INFO, "Remove object instance: {}, User Supplied Tag: {}, Send Order: {}, Logical Time: {}, Receive Order: {}, Supplemental Remove Info ({}) <{}>",
                theObject, userSuppliedTag, sentOrdering, theTime, receivedOrdering, removeInfo.hasProducingFederate(), removeInfo.getProducingFederate());
        logEntries.add(log);
        logger.exit();
    }

    //6.15
    @Override
    public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering, LogicalTime theTime, OrderType receivedOrdering, MessageRetractionHandle retractionHandle, SupplementalRemoveInfo removeInfo) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.15", "Remove Object Instance † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        if (userSuppliedTag.length > 0) {
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", byte.class, Arrays.toString(userSuppliedTag)));
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", String.class, new String(userSuppliedTag)));
        }
        log.getSuppliedArguments().add(new ClassValuePair("Send Order", OrderType.class, sentOrdering.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Logical Time", LogicalTime.class, theTime.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Receive Order", OrderType.class, receivedOrdering.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Message Retraction Handle", MessageRetractionHandle.class, retractionHandle.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Supplemental Remove Info (has federate)", SupplementalRemoveInfo.class, String.valueOf(removeInfo.hasProducingFederate())));
        if (removeInfo.hasProducingFederate()) {
            log.getSuppliedArguments().add(new ClassValuePair("Supplemental Remove Info (federate)", SupplementalRemoveInfo.class, removeInfo.getProducingFederate().toString()));
        }
        log.setDescription("Remove object instance");
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
        logger.log(Level.INFO, "Remove object instance: {}, User Supplied Tag: {}, Send Order: {}, Logical Time: {}, Receive Order: {}, Message Retraction Handle: {}, Supplemental Remove Info ({}) <{}>",
                theObject, userSuppliedTag, sentOrdering, theTime, receivedOrdering, retractionHandle, removeInfo.hasProducingFederate(), removeInfo.getProducingFederate());
        logEntries.add(log);
        logger.exit();
    }

    //6.17
    @Override
    public void attributesInScope(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.17", "Attributes In Scope † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        int i = 1;
        for (AttributeHandle theAttribute : theAttributes) {
            log.getSuppliedArguments().add(new ClassValuePair("Attribute " + i++, AttributeHandle.class, theAttribute.toString()));
        }
        log.setDescription("Attributes In Scope");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Attributes In Scope, Object Instance Handle: {}, attributes: {}", theObject, theAttributes);
        logEntries.add(log);
        logger.exit();
    }

    //6.18
    @Override
    public void attributesOutOfScope(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.18", "Attributes Out Of Scope † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        int i = 1;
        for (AttributeHandle theAttribute : theAttributes) {
            log.getSuppliedArguments().add(new ClassValuePair("Attribute " + i++, AttributeHandle.class, theAttribute.toString()));
        }
        log.setDescription("Attributes Out Of Scope");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Attributes Out Of Scope, Object Instance Handle: {}, attributes: {}", theObject, theAttributes);
        logEntries.add(log);
        logger.exit();
    }

    //6.20
    @Override
    public void provideAttributeValueUpdate(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes, byte[] userSuppliedTag) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.20", "Provide Attribute Value Update † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        int i = 1;
        for (AttributeHandle theAttribute : theAttributes) {
            log.getSuppliedArguments().add(new ClassValuePair("Attribute " + i++, AttributeHandle.class, theAttribute.toString()));
        }
        if (userSuppliedTag.length > 0) {
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", byte.class, Arrays.toString(userSuppliedTag)));
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", String.class, new String(userSuppliedTag)));
        }
        log.setDescription("Provide Attribute Value Update");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Provide Attribute Value Update, Object Instance Handle: {}, attributes: {}, user supplied tag:{}",
                theObject, theAttributes, new String(userSuppliedTag));
        logEntries.add(log);
        logger.exit();
    }

    //6.21
    @Override
    public void turnUpdatesOnForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.21", "Turn Updates On For Object Instance † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        int i = 1;
        for (AttributeHandle theAttribute : theAttributes) {
            log.getSuppliedArguments().add(new ClassValuePair("Attribute " + i++, AttributeHandle.class, theAttribute.toString()));
        }
        log.setDescription("Turn Updates On For Object Instance");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Turn Updates On For Object Instance, Object Instance Handle: {}, attributes: {}",
                theObject, theAttributes);
        logEntries.add(log);
        logger.exit();
    }

    //6.21
    @Override
    public void turnUpdatesOnForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes, String updateRateDesignator) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.21", "Turn Updates On For Object Instance † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        int i = 1;
        for (AttributeHandle theAttribute : theAttributes) {
            log.getSuppliedArguments().add(new ClassValuePair("Attribute " + i++, AttributeHandle.class, theAttribute.toString()));
        }
        log.getSuppliedArguments().add(new ClassValuePair("Maximum update rate designator", String.class, updateRateDesignator));
        log.setDescription("Turn Updates On For Object Instance");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Turn Updates On For Object Instance, Object Instance Handle: {}, attributes: {}, Update Rate: {}",
                theObject, theAttributes, updateRateDesignator);
        logEntries.add(log);
        logger.exit();
    }

    //6.22
    @Override
    public void turnUpdatesOffForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.22", "Turn Updates Off For Object Instance † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        int i = 1;
        for (AttributeHandle theAttribute : theAttributes) {
            log.getSuppliedArguments().add(new ClassValuePair("Attribute " + i++, AttributeHandle.class, theAttribute.toString()));
        }
        log.setDescription("Turn Updates Off For Object Instance");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Turn Updates Off For Object Instance, Object Instance Handle: {}, attributes: {}",
                theObject, theAttributes);
        logEntries.add(log);
        logger.exit();
    }

    //6.24
    @Override
    public void confirmAttributeTransportationTypeChange(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes, TransportationTypeHandle theTransportation) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.24", "Confirm Attribute Transportation Type Change † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        int i = 1;
        for (AttributeHandle theAttribute : theAttributes) {
            log.getSuppliedArguments().add(new ClassValuePair("Attribute " + i++, AttributeHandle.class, theAttribute.toString()));
        }
        log.getSuppliedArguments().add(new ClassValuePair("Transportation type", TransportationTypeHandle.class, theTransportation.toString()));
        log.setDescription("Confirm Attribute Transportation Type Change");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Confirm Attribute Transportation Type Change, Object Instance Handle: {}, attributes: {}, Transportation: {}",
                theObject, theAttributes, theTransportation.toString());
        logEntries.add(log);
        logger.exit();
    }

    //6.26
    @Override
    public void reportAttributeTransportationType(ObjectInstanceHandle theObject, AttributeHandle theAttribute, TransportationTypeHandle theTransportation) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.26", "Report Attribute Transportation Type † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Attribute", AttributeHandle.class, theAttribute.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Transportation type", TransportationTypeHandle.class, theTransportation.toString()));
        log.setDescription("Confirm Attribute Transportation Type Change");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Confirm Attribute Transportation Type Change, Object Instance Handle: {}, attribute: {}, Transportation: {}",
                theObject, theAttribute, theTransportation.toString());
        logEntries.add(log);
        logger.exit();
    }

    //6.28
    @Override
    public void confirmInteractionTransportationTypeChange(InteractionClassHandle theInteraction, TransportationTypeHandle theTransportation) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.28", "Confirm Interaction Transportation Type Change † service");
        log.getSuppliedArguments().add(new ClassValuePair("Interaction Class Handle", InteractionClassHandle.class, theInteraction.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Transportation type", TransportationTypeHandle.class, theTransportation.toString()));
        log.setDescription("Confirm Interaction Transportation Type Change");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Confirm Interaction Transportation Type Change, Interaction Class Handle: {}, Transportation: {}",
                theInteraction, theTransportation.toString());
        logEntries.add(log);
        logger.exit();
    }

    //6.30
    @Override
    public void reportInteractionTransportationType(FederateHandle theFederate, InteractionClassHandle theInteraction, TransportationTypeHandle theTransportation) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("6.30", "Report Interaction Transportation Type † service");
        log.getSuppliedArguments().add(new ClassValuePair("Federate Handle", FederateHandle.class, theFederate.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Interaction Class Handle", InteractionClassHandle.class, theInteraction.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Transportation type", TransportationTypeHandle.class, theTransportation.toString()));
        log.setDescription("Report Interaction Transportation Type");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Report Interaction Transportation Type, Federate Handle: {}, Interaction Class Handle: {}, Transportation: {}",
                theFederate, theInteraction, theTransportation.toString());
        logEntries.add(log);
        logger.exit();
    }
// </editor-fold>

// <editor-fold desc="Chapter 7">
    //7.4
    @Override
    public void requestAttributeOwnershipAssumption(ObjectInstanceHandle theObject, AttributeHandleSet offeredAttributes, byte[] userSuppliedTag) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("7.4", "Request Attribute Ownership Assumption † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        int i = 1;
        for (AttributeHandle theAttribute : offeredAttributes) {
            log.getSuppliedArguments().add(new ClassValuePair("Attribute " + i++, AttributeHandle.class, theAttribute.toString()));
        }
        if (userSuppliedTag.length > 0) {
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", byte.class, Arrays.toString(userSuppliedTag)));
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", String.class, new String(userSuppliedTag)));
        }
        log.setDescription("Request Attribute Ownership Assumption");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Request Attribute Ownership Assumption, Object Instance Handle: {}, attributes: {}, user supplied tag:{}",
                theObject, offeredAttributes, new String(userSuppliedTag));
        logEntries.add(log);
        logger.exit();
    }

    //7.5
    @Override
    public void requestDivestitureConfirmation(ObjectInstanceHandle theObject, AttributeHandleSet offeredAttributes) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("7.5", "Request Divestiture Confirmation † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        int i = 1;
        for (AttributeHandle theAttribute : offeredAttributes) {
            log.getSuppliedArguments().add(new ClassValuePair("Attribute " + i++, AttributeHandle.class, theAttribute.toString()));
        }
        log.setDescription("Request Divestiture Confirmation");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Request Divestiture Confirmation, Object Instance Handle: {}, attributes: {}",
                theObject, offeredAttributes);
        logEntries.add(log);
        logger.exit();
    }

    //7.7
    @Override
    public void attributeOwnershipAcquisitionNotification(ObjectInstanceHandle theObject, AttributeHandleSet securedAttributes, byte[] userSuppliedTag) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("7.7", "Attribute Ownership Acquisition Notification † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        int i = 1;
        for (AttributeHandle theAttribute : securedAttributes) {
            log.getSuppliedArguments().add(new ClassValuePair("Attribute " + i++, AttributeHandle.class, theAttribute.toString()));
        }
        if (userSuppliedTag.length > 0) {
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", byte.class, Arrays.toString(userSuppliedTag)));
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", String.class, new String(userSuppliedTag)));
        }
        log.setDescription("Request Attribute Ownership Assumption");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Attribute Ownership Acquisition Notification, Object Instance Handle: {}, attributes: {}, user supplied tag:{}",
                theObject, securedAttributes, new String(userSuppliedTag));
        logEntries.add(log);
        logger.exit();
    }

    //7.10
    @Override
    public void attributeOwnershipUnavailable(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("7.10", "Attribute Ownership Unavailable † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        int i = 1;
        for (AttributeHandle theAttribute : theAttributes) {
            log.getSuppliedArguments().add(new ClassValuePair("Attribute " + i++, AttributeHandle.class, theAttribute.toString()));
        }
        log.setDescription("Attribute Ownership Unavailable");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Attribute Ownership Unavailable, Object Instance Handle: {}, attributes: {}",
                theObject, theAttributes);
        logEntries.add(log);
        logger.exit();
    }

    //7.11
    @Override
    public void requestAttributeOwnershipRelease(ObjectInstanceHandle theObject, AttributeHandleSet candidateAttributes, byte[] userSuppliedTag) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("7.11", "Request Attribute Ownership Release † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        int i = 1;
        for (AttributeHandle theAttribute : candidateAttributes) {
            log.getSuppliedArguments().add(new ClassValuePair("Attribute " + i++, AttributeHandle.class, theAttribute.toString()));
        }
        if (userSuppliedTag.length > 0) {
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", byte.class, Arrays.toString(userSuppliedTag)));
            log.getSuppliedArguments().add(new ClassValuePair("User-supplied tag", String.class, new String(userSuppliedTag)));
        }
        log.setDescription("Request Attribute Ownership Release");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Request Attribute Ownership Release, Object Instance Handle: {}, attributes: {}, user supplied tag:{}",
                theObject, candidateAttributes, new String(userSuppliedTag));
        logEntries.add(log);
        logger.exit();
    }

    //7.16
    @Override
    public void confirmAttributeOwnershipAcquisitionCancellation(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("7.16", "Confirm Attribute Ownership Acquisition Cancellation † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        int i = 1;
        for (AttributeHandle theAttribute : theAttributes) {
            log.getSuppliedArguments().add(new ClassValuePair("Attribute " + i++, AttributeHandle.class, theAttribute.toString()));
        }
        log.setDescription("Confirm Attribute Ownership Acquisition Cancellation");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Confirm Attribute Ownership Acquisition Cancellation, Object Instance Handle: {}, attributes: {}",
                theObject, theAttributes);
        logEntries.add(log);
        logger.exit();
    }

    //7.18
    @Override
    public void informAttributeOwnership(ObjectInstanceHandle theObject, AttributeHandle theAttribute, FederateHandle theOwner) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("7.18", "Inform Attribute Ownership † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Attribute", AttributeHandle.class, theAttribute.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Owner", FederateHandle.class, theOwner.toString()));
        log.setDescription("Attribute is owned by federate");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Attribute is owned by federate, Object Instance Handle: {}, attribute: {}, Owner: {}",
                theObject, theAttribute, theOwner);
        logEntries.add(log);
        logger.exit();
    }

    //7.18
    @Override
    public void attributeIsNotOwned(ObjectInstanceHandle theObject, AttributeHandle theAttribute) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("7.18", "Inform Attribute Ownership † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Attribute", AttributeHandle.class, theAttribute.toString()));
        log.setDescription("Attribute is not owned");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Attribute is not owned, Object Instance Handle: {}, attribute: {}",
                theObject, theAttribute);
        logEntries.add(log);
        logger.exit();
    }

    //7.18
    @Override
    public void attributeIsOwnedByRTI(ObjectInstanceHandle theObject, AttributeHandle theAttribute) throws FederateInternalError {
        logger.entry();
        LogEntry log = new LogEntry("7.18", "Inform Attribute Ownership † service");
        log.getSuppliedArguments().add(new ClassValuePair("Object Instance Handle", ObjectInstanceHandle.class, theObject.toString()));
        log.getSuppliedArguments().add(new ClassValuePair("Attribute", AttributeHandle.class, theAttribute.toString()));
        log.setDescription("Attribute is owned by RTI");
        log.setLogType(LogEntryType.CALLBACK);
        logger.log(Level.INFO, "Attribute is owned by RTI, Object Instance Handle: {}, attribute: {}",
                theObject, theAttribute);
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
