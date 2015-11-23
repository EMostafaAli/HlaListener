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
package ca.mali.fomparser;

import ca.mali.fdd.*;
import ca.mali.hlalistener.PublicVariables;
import hla.rti1516e.DimensionHandle;
import hla.rti1516e.TransportationTypeHandle;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.lang.String;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
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
    private Map<String, DimensionFDD> Dimensions = new TreeMap<>();
    private Map<String, BasicDataType> basicDataTypeMap = new TreeMap<>();
    private Map<String, SimpleFDDDataType> simpleDataTypeMap = new TreeMap<>();
    private Map<String, EnumeratedFDDDataType> enumeratedDataTypeMap = new TreeMap<>();
    private Map<String, ArrayFDD> arrayDataTypeMapDataTypeMap = new TreeMap<>();

    public FddObjectModel(String fddText) {
        logger.entry();
        try {
            this.fdd = fddText;
            jaxbContext = JAXBContext.newInstance(ca.mali.fdd.ObjectFactory.class);
            unmarshaller = jaxbContext.createUnmarshaller();
            javax.xml.bind.JAXBElement unmarshaller = (javax.xml.bind.JAXBElement) this.unmarshaller.unmarshal(new ByteArrayInputStream(fdd.getBytes(StandardCharsets.UTF_8)));
            fddModel = (ca.mali.fdd.ObjectModelType) unmarshaller.getValue();
            readBasicDataType();
            readSimpleDataType();
            readEnumeratedDataType();
            readArrayDataType();
            readUpdateRate();
            readTransportationType();
            readDimension();
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

    public Map<String, BasicDataType> getBasicDataTypeMap() {
        return basicDataTypeMap;
    }

    public Map<String, SimpleFDDDataType> getSimpleDataTypeMap() {
        return simpleDataTypeMap;
    }

    public Map<String, EnumeratedFDDDataType> getEnumeratedDataTypeMap() {
        return enumeratedDataTypeMap;
    }

    public Map<String, UpdateRateFDD> getUpdateRates() {
        return updateRates;
    }

    public Map<String, TransportationFDD> getTransportation() {
        return Transportation;
    }

    public Map<String, DimensionFDD> getDimensions() {
        return Dimensions;
    }

    public Map<String, ArrayFDD> getArrayDataTypeMap() {
        return arrayDataTypeMapDataTypeMap;
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

            rootClass.getObjectClass().stream().forEach((_item) -> readObjectClasses(_item, objectClassFDD));
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
            rootInteraction.getInteractionClass().stream().forEach(item -> readInteractionClasses(item, interactionClassFDD));
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
            try {
                TransportationTypeHandle transportationTypeHandle = PublicVariables.rtiAmb.getTransportationTypeHandle(trans.getName().getValue());
                TransportationFDD transportation = new TransportationFDD(trans.getName().getValue(), transportationTypeHandle);
                if (trans.getReliable().getValue() == ReliableEnumerations.YES) {
                    transportation.setIsReliable(true);
                }
                Transportation.put(transportation.getName(), transportation);
            } catch (Exception ex) {
                logger.log(Level.FATAL, ex.getMessage(), ex);
            }
        });
    }

    private void readDimension() {
        fddModel.getDimensions().getDimension().forEach(dim -> {
            try {
                DimensionHandle dimensionHandle = PublicVariables.rtiAmb.getDimensionHandle(dim.getName().getValue());
                DimensionFDD dimensionFDD = new DimensionFDD(dim.getName().getValue(), dimensionHandle);
                Dimensions.put(dimensionFDD.getName(), dimensionFDD);
            } catch (Exception ex) {
                logger.log(Level.FATAL, ex.getMessage(), ex);
            }
        });
    }

    private void readBasicDataType() {
        fddModel.getDataTypes().getBasicDataRepresentations().getBasicData().forEach(basicData -> {
            try {
                BasicDataType basicDataType = new BasicDataType(basicData.getName().getValue());
                basicDataType.setInterpretation(basicData.getInterpretation().getValue());
                basicDataType.setSize(basicData.getSize().getValue().intValue());
                basicDataType.setEncoding(basicData.getEncoding().getValue());
                basicDataType.setLittleEndian(basicData.getEndian().getValue() == EndianEnumerations.LITTLE ? true : false);
                getBasicDataTypeMap().put(basicDataType.getName(), basicDataType);
            } catch (Exception ex) {
                logger.log(Level.FATAL, ex.getMessage(), ex);
            }
        });
    }

    private void readSimpleDataType() {
        fddModel.getDataTypes().getSimpleDataTypes().getSimpleData().forEach(simpleData -> {
            try {
                SimpleFDDDataType simpleDataType = new SimpleFDDDataType(simpleData.getName().getValue());
                simpleDataType.setRepresentation(simpleData.getRepresentation().getValue());
                simpleDataType.setUnits(simpleData.getUnits().getValue());
                simpleDataType.setResolution(simpleData.getResolution().getValue());
                simpleDataType.setAccuracy(simpleData.getAccuracy().getValue());
                getSimpleDataTypeMap().put(simpleDataType.getName(), simpleDataType);
            } catch (Exception ex) {
                logger.log(Level.FATAL, ex.getMessage(), ex);
            }
        });
    }

    private void readEnumeratedDataType() {
        fddModel.getDataTypes().getEnumeratedDataTypes().getEnumeratedData().forEach(enumeratedData -> {
            try {
                EnumeratedFDDDataType enumerated = new EnumeratedFDDDataType(enumeratedData.getName().getValue());
                enumerated.setRepresentation(enumeratedData.getRepresentation().getValue());
                enumeratedData.getEnumerator().forEach(enumerator -> {
                    EnumeratedFDDDataType.Enumerator en = new EnumeratedFDDDataType.Enumerator();
                    en.setName(enumerator.getName().getValue());
                    en.getValues().addAll(enumerator.getValue().stream()
                            .map(ca.mali.fdd.String::getValue).collect(Collectors.toList()));
                    enumerated.getEnumerator().add(en);
                });
                getEnumeratedDataTypeMap().put(enumerated.getName(), enumerated);
            } catch (Exception ex) {
                logger.log(Level.FATAL, ex.getMessage(), ex);
            }
        });
    }

    private void readArrayDataType(){
        fddModel.getDataTypes().getArrayDataTypes().getArrayData().forEach(arrayData -> {
            ArrayFDD arrayFDD = new ArrayFDD(arrayData.getName().getValue());
            arrayFDD.setDataType(arrayData.getDataType().getValue());
            arrayFDD.setCardinality(arrayData.getCardinality().getValue());
            arrayFDD.setEncoding(arrayData.getEncoding().getValue());
            getArrayDataTypeMap().put(arrayFDD.getName(), arrayFDD);
        });
    }
}
