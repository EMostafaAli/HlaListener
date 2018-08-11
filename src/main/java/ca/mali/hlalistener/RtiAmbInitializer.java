/*
 * Copyright (c) 2015-2016, Mostafa Ali (engabdomostafa@gmail.com)
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met: Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer. Redistributions
 * in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

import static ca.mali.hlalistener.PublicVariables.*;

/**
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class RtiAmbInitializer implements Initializable {

    @FXML
    private TextField JarFileLocation;

    @FXML
    private Button OkButton;

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

            URL[] urls = new URL[]{file.toURI().toURL()};
            URLClassLoader ucl = new URLClassLoader(urls);
            ServiceLoader<RtiFactory> sl = ServiceLoader.load(RtiFactory.class, ucl);

            Iterator<RtiFactory> i = sl.iterator();
            if (!i.hasNext()) {
                throw new Exception();
            }

            rtiFactory = i.next();
            rtiAmb = rtiFactory.getRtiAmbassador();
            fedAmb = new ListenerFederateAmb();
            encoderFactory = rtiFactory.getEncoderFactory();

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error loading library");
            alert.setHeaderText("Error processing the provided file");
            alert.setContentText("Please make sure the selected file is a valid HLA1516e jar library");
            alert.showAndWait();
            logger.log(Level.FATAL, "Exception", ex);
        }
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
