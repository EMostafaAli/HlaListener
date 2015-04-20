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
package ca.mali.fomParser;

import java.io.*;
import java.nio.charset.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.apache.logging.log4j.*;

/**
 *
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class FomParser {

    private Document doc;

    //Logger
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    public FomParser(String xmlFOM) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setIgnoringComments(true);
            builderFactory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
            doc = docBuilder.parse(new ByteArrayInputStream(xmlFOM.getBytes(StandardCharsets.UTF_8)));
        } catch (ParserConfigurationException ex) {
            logger.log(Level.FATAL, "Exception in parsing configuration", ex);
        } catch (SAXException ex) {
            logger.log(Level.FATAL, "Exception in SAX", ex);
        } catch (IOException ex) {
            logger.log(Level.FATAL, "Exception in IO", ex);
        }
    }

    public void getObjectClass() {
        try {
            //Get the hlaObjectRoot
            Node node = doc.getElementsByTagName("objectClass").item(0);
            recursiveCall(node, "objectClass", "");
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Exception in parsing object classes", ex);
        }
    }
    
    public void getInteractionClass() {
        try {
            //Get the hlaObjectRoot
            Node node = doc.getElementsByTagName("interactionClass").item(0);
            recursiveCall(node, "interactionClass", "");
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Exception in parsing interaction classes", ex);
        }
    }

    private void recursiveCall(Node node, String elementName, String parentName) {
        Element objectClass = (Element) node;
        String temp = parentName + objectClass.getElementsByTagName("name").item(0).getTextContent();
        System.out.println(temp);
        NodeList nodes = objectClass.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (elementName.equals(nodes.item(i).getNodeName())) {
                recursiveCall(nodes.item(i), elementName, temp + ".");
            }
        }
    }
}
