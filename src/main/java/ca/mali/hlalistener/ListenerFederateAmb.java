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

import ca.mali.fomParser.FomParser;
import static ca.mali.hlalistener.HlaPublicVariables.*;


import hla.rti1516e.*;
import hla.rti1516e.encoding.*;
import hla.rti1516e.exceptions.*;
import org.apache.logging.log4j.*;

/**
 *
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class ListenerFederateAmb extends NullFederateAmbassador{
    
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
}