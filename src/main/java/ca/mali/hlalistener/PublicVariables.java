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

import ca.mali.fomparser.FddObjectModel;
import ca.mali.fomparser.ObjectInstanceFDD;
import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.stage.Stage;

/**
 *
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class PublicVariables {

    public static RtiFactory rtiFactory;

    /**
     * RTI ambassador
     */
    public static RTIambassador rtiAmb;

    /**
     * Federate ambassador
     */
    public static ListenerFederateAmb fedAmb;

    /**
     * Logical Time Factory
     */
    public static LogicalTimeFactory logicalTimeFactory;

    public static LogicalTime currentLogicalTime;

    public static LogicalTimeInterval LookaheadValue;

    public static AttributeHandle currentFDDHandle;
    
    public static FddObjectModel fddObjectModel;

    public static boolean isConnected = false;

    public static boolean subscribeFdd = false;

    /**
     * Encoder Factory
     */
    public static EncoderFactory encoderFactory;


    public static Stage primaryStage;

    public final static ObservableList<LogEntry> logEntries = FXCollections.observableArrayList();
    
    public final static ObservableList<MessageRetractionHandle> messageRetractionHandles = FXCollections.observableArrayList();

    //TODO: add to list from create region (this part has been done) and chapter 6 (sent regions)
    public final static ObservableList<RegionHandle> regionHandles = FXCollections.observableArrayList();
    
    public final static ObservableMap<ObjectInstanceHandle, ObjectInstanceFDD> objectInstances = FXCollections.observableHashMap();

}
