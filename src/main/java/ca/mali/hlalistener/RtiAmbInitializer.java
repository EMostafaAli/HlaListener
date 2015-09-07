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

import hla.rti1516e.RtiFactory;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ResourceBundle;

import static ca.mali.hlalistener.PublicVariables.*;

/**
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class RtiAmbInitializer implements Initializable {

    @FXML
    private TextField JarFileLocation;

    @FXML
    private Button OkButton;

    /**
     *
     */

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private void handleButtonAction(ActionEvent event) {
        try {
            logger.entry();
            File file = new File(JarFileLocation.getText());

            //Check if the path represents an actual file
            if (!file.exists()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Path Error");
                alert.setHeaderText(null);
                alert.setContentText("Path cannot be resolved to jar file");
                alert.showAndWait();
                return;
            }

            AddtoBuildPath(file);
            URL[] urls = new URL[]{file.toURI().toURL()};
            URLClassLoader child = new URLClassLoader(urls, this.getClass().getClassLoader());
            Class RtiFactoryFactory = Class.forName("hla.rti1516e.RtiFactoryFactory", true, child);
            Method getRtiFactory = RtiFactoryFactory.getMethod("getRtiFactory");
            rtiFactory = (RtiFactory) getRtiFactory.invoke(null);
            rtiAmb = rtiFactory.getRtiAmbassador();
            fedAmb = new ListenerFederateAmb();
            encoderFactory = rtiFactory.getEncoderFactory();

//            Stage stage = (Stage) JarFileLocation.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
//            primaryStage.setMinHeight(0);
//            primaryStage.setMaxHeight(Double.MAX_VALUE);
            primaryStage.setResizable(true);
            primaryStage.setMaximized(true);
            primaryStage.show();

//            rtiAmb.connect(fedAmb, CallbackModel.HLA_IMMEDIATE);
//            FOMModules = new URL[]{coreFile.toURI().toURL()};
//            rtiAmb.createFederationExecution("TestFederation", FOMModules);
//            rtiAmb.joinFederationExecution("Listener Test fed", "TestFederation");
//            rtiAmb.enableAsynchronousDelivery();
//            ObjectClassHandle FederationHandle = rtiAmb.getObjectClassHandle("HLAobjectRoot.HLAmanager.HLAfederation");
//            System.out.println(FederationHandle.toString());
//            currentFDDHandle = rtiAmb.getAttributeHandle(FederationHandle, "HLAcurrentFDD");
//            System.out.println(currentFDDHandle.toString());
//            AttributeHandleSet set = rtiAmb.getAttributeHandleSetFactory().create();
//            set.add(currentFDDHandle);
//            rtiAmb.subscribeObjectClassAttributes(FederationHandle, set);
//            System.out.println(rriFactory.rtiName());
//            System.out.println(rriFactory.rtiVersion());
//
//            rtiAmb.requestAttributeValueUpdate(FederationHandle, set, null);
        } catch (Exception ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error loading library");
            alert.setHeaderText("Error processing the provided file");
            alert.setContentText("Please make sure the selected file is a valid HLA1516e jar library");
            alert.showAndWait();
            logger.log(Level.FATAL, "Exception", ex);
        }
    }

    private static int AddtoBuildPath(File f) {
        logger.entry();
        try {
            URI u = f.toURI();
            URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class<URLClassLoader> urlClass = URLClassLoader.class;
            Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(urlClassLoader, new Object[]{u.toURL()});
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException |
                InvocationTargetException | MalformedURLException | IllegalAccessException ex) {
            logger.log(Level.FATAL, "Error adding the jar file to the class path", ex);
            return 1;
        }
        logger.exit();
        return 0;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fedAmb = new ListenerFederateAmb();
        Tooltip tooltip = new Tooltip("Double click to Select HLA jar file");
        JarFileLocation.setTooltip(tooltip);
        OkButton.disableProperty().bind(Bindings.isEmpty(JarFileLocation.textProperty()));
        JarFileLocation.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select jar file");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Jar file", "*.jar"));
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    JarFileLocation.setText(file.getAbsolutePath());
                }
            }
        });
    }
}
