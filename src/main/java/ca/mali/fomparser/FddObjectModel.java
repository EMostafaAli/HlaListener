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
package ca.mali.fomparser;

import ca.mali.fdd.ObjectClass;
import ca.mali.fdd.InteractionClass;
import ca.mali.fdd.ObjectModelType;
import ca.mali.fdd.ReliableEnumerations;
import ca.mali.hlalistener.PublicVariables;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import javax.xml.bind.*;
import org.apache.logging.log4j.*;

/**
 *
 * @author Mostafa
 */
public class FddObjectModel {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    JAXBContext jaxbContext;
    Unmarshaller unmarshaller;
    ObjectModelType fddModel;
    private String fdd;

    private Map<String, ObjectClassFDD> objectClasses = new TreeMap<>();
    private Map<String, InteractionClassFDD> interactionClasses = new TreeMap<>();
    private Map<String, UpdateRateFDD> updateRates = new TreeMap<>();
    private Map<String, TransportationFDD> Transportation = new TreeMap<>();

    public FddObjectModel(String fddText) {
        logger.entry();
        try {
            this.fdd = fddText;
            jaxbContext = JAXBContext.newInstance(ca.mali.fdd.ObjectFactory.class);
            unmarshaller = jaxbContext.createUnmarshaller();
            javax.xml.bind.JAXBElement unmarshal = (javax.xml.bind.JAXBElement) unmarshaller.unmarshal(new ByteArrayInputStream(fdd.getBytes(StandardCharsets.UTF_8)));
            fddModel = (ca.mali.fdd.ObjectModelType) unmarshal.getValue();
            readUpdateRate();
            readTransportationType();
            readObjectClasses(fddModel.getObjects().getObjectClass(), null);
            readInteractionClasses(fddModel.getInteractions().getInteractionClass(), null);
        } catch (Exception ex) {
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logger.exit();
    }

    public String getFdd() {
        return this.fdd;
    }

    public Map<String, ObjectClassFDD> getObjectClasses() {
        return objectClasses;
    }

    public Map<String, InteractionClassFDD> getInteractionClasses() {
        return interactionClasses;
    }

    public Map<String, UpdateRateFDD> getUpdateRates() {
        return updateRates;
    }

    public Map<String, TransportationFDD> getTransportation() {
        return Transportation;
    }

    public void setTransportation(Map<String, TransportationFDD> Transportation) {
        this.Transportation = Transportation;
    }

    private void readObjectClasses(ObjectClass rootClass, ObjectClassFDD parent) {
        try {
            ObjectClassFDD objectClassFDD = new ObjectClassFDD(rootClass.getName().getValue(), parent);
            objectClassFDD.setHandle(PublicVariables.rtiAmb.getObjectClassHandle(objectClassFDD.getFullName()));
            rootClass.getAttribute().stream().map((attribute) -> new AttributeFDD(
                    attribute.getName().getValue(), attribute.getDataType().getValue())).forEach((attributeFDD) -> {
                        try {
                            attributeFDD.setHandle(PublicVariables.rtiAmb.getAttributeHandle(
                                            objectClassFDD.getHandle(), attributeFDD.getName()));
                        } catch (Exception ex) {
                            logger.log(Level.FATAL, ex.getMessage(), ex);
                        }
                        objectClassFDD.getAttributes().add(attributeFDD);
                    });
            objectClasses.put(objectClassFDD.getFullName(), objectClassFDD);

            rootClass.getObjectClass().stream().forEach((_item) -> {
                readObjectClasses(_item, objectClassFDD);
            });
        } catch (Exception ex) {
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
    }

    private void readInteractionClasses(InteractionClass rootInteraction, InteractionClassFDD parent) {
        try {
            InteractionClassFDD interactionClassFDD = new InteractionClassFDD(rootInteraction.getName().getValue(), parent);
            interactionClassFDD.setHandle(PublicVariables.rtiAmb.getInteractionClassHandle(interactionClassFDD.getFullName()));
            rootInteraction.getParameter().stream().forEach((parameter) -> {
                try {
                    ParameterFDD paramFDD = new ParameterFDD(parameter.getName().getValue(), parameter.getDataType().getValue());
                    paramFDD.setHandle(PublicVariables.rtiAmb.getParameterHandle(interactionClassFDD.getHandle(), paramFDD.getName()));
                    interactionClassFDD.getParameters().add(paramFDD);
                } catch (Exception ex) {
                    logger.log(Level.FATAL, ex.getMessage(), ex);
                }
            });
            interactionClasses.put(interactionClassFDD.getFullName(), interactionClassFDD);
            rootInteraction.getInteractionClass().stream().forEach(item -> {
                readInteractionClasses(item, interactionClassFDD);
            });
        } catch (Exception ex) {
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
    }

    private void readUpdateRate() {
        fddModel.getUpdateRates().getUpdateRate().stream().forEach(updateRate -> {
            UpdateRateFDD rate = new UpdateRateFDD();
            rate.setName(updateRate.getName().getValue());
            rate.setValue(updateRate.getRate().getValue().doubleValue());
            updateRates.put(rate.getName(), rate);
        });
    }

    private void readTransportationType() {
        fddModel.getTransportations().getTransportation().forEach(trans -> {
            TransportationFDD transportation = new TransportationFDD();
            transportation.setName(trans.getName().getValue());
            if (trans.getReliable().getValue() == ReliableEnumerations.YES) {
                transportation.setIsReliable(true);
            }
            Transportation.put(transportation.getName(), transportation);
        });
    }
}
