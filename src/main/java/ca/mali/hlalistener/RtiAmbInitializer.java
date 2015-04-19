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

import static ca.mali.hlalistener.HlaPublicVariables.*;

import hla.rti1516e.*;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;

import org.apache.logging.log4j.*;

/**
 * @author Mostafa Ali <mostafaali@ualberta.ca>
 */
public class RtiAmbInitializer implements Initializable {

    @FXML
    private TextField JarFileLocation;

    
    private ListenerFederateAmb fedAmb;
    private static final File coreFile = new File("FOMs/RestaurantFOMmodule.xml");
    private static URL[] FOMModules;

    /**
     *
     */
    public static AttributeHandle currentFDDHandle;

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private void handleButtonAction(ActionEvent event) {
        try {
            logger.entry();
            File file = new File(JarFileLocation.getText());
            AddtoBuildPath(file);
            URL[] urls = new URL[]{file.toURI().toURL()};
            URLClassLoader child = new URLClassLoader(urls, this.getClass().getClassLoader());
            Class RtiFactoryFactory = Class.forName("hla.rti1516e.RtiFactoryFactory", true, child);
            Method getRtiFactory = RtiFactoryFactory.getMethod("getRtiFactory");
            RtiFactory rriFactory = (RtiFactory) getRtiFactory.invoke(null);
            rtiAmb = rriFactory.getRtiAmbassador();
            fedAmb = new ListenerFederateAmb();
            encoderFactory = rriFactory.getEncoderFactory();
            rtiAmb.connect(fedAmb, CallbackModel.HLA_IMMEDIATE);
            FOMModules = new URL[]{coreFile.toURI().toURL()};
            rtiAmb.createFederationExecution("TestFederation", FOMModules);
            rtiAmb.joinFederationExecution("Listener Test fed", "TestFederation");
            rtiAmb.enableAsynchronousDelivery();
            ObjectClassHandle FederationHandle = rtiAmb.getObjectClassHandle("HLAobjectRoot.HLAmanager.HLAfederation");
            System.out.println(FederationHandle.toString());
            currentFDDHandle = rtiAmb.getAttributeHandle(FederationHandle, "HLAcurrentFDD");
            System.out.println(currentFDDHandle.toString());
            AttributeHandleSet set = rtiAmb.getAttributeHandleSetFactory().create();
            set.add(currentFDDHandle);
            rtiAmb.subscribeObjectClassAttributes(FederationHandle, set);
            System.out.println(rriFactory.rtiName());
            System.out.println(rriFactory.rtiVersion());

            rtiAmb.requestAttributeValueUpdate(FederationHandle, set, null);
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Exception", ex);
        }
    }

    /**
     *
     * @param f Jar file that will be loaded in the build path
     * @throws Exception
     */
    private static int AddtoBuildPath(File f) {
        logger.entry();
        try {
            URI u = f.toURI();
            URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class<URLClassLoader> urlClass = URLClassLoader.class;
            Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(urlClassLoader, new Object[]{u.toURL()});
        }  catch (NoSuchMethodException | SecurityException | IllegalArgumentException | 
                InvocationTargetException | MalformedURLException | IllegalAccessException ex) {
                        logger.log(Level.FATAL, "Error adding the jar file to the class path", ex);
                        return 1;
        }
        logger.exit();
        return 0;
    }

    @FXML
    private void JarBrowser_click(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select jar file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Jar file", "*.jar"));
        File file = fileChooser.showOpenDialog(JarFileLocation.getScene().getWindow());
        if (file != null) {
            JarFileLocation.setText(file.getAbsolutePath());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        fedAmb = new ListenerFederateAmb();
    }
}
