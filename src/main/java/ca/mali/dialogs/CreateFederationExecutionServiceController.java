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
package ca.mali.dialogs;

import ca.mali.customcontrol.*;
import ca.mali.hlalistener.*;
import static ca.mali.hlalistener.PublicVariables.*;
import hla.rti1516e.exceptions.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javafx.beans.binding.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.*;
import org.apache.logging.log4j.*;

/**
 * FXML Controller class
 *
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class CreateFederationExecutionServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TextField FederationExecutionName;

    @FXML
    private TextField MimDesignator;

    @FXML
    private ChoiceBox<String> LogicalTimeImplementation;

    @FXML
    private FilesList FomModuleDesignators;

    @FXML
    private Button OkButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        OkButton.disableProperty().bind(
                Bindings.isEmpty(FederationExecutionName.textProperty())
                .or(Bindings.isEmpty(FomModuleDesignators.getFileNames())));

        MimDesignator.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select MIM file");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("XML file", "*.xml"));
                File file = fileChooser.showOpenDialog((Stage) FederationExecutionName.getScene().getWindow());
                if (file != null) {
                    MimDesignator.setText(file.getAbsolutePath());
                }
            }
        });
        logger.exit();
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        logger.entry();
        ((Stage) FederationExecutionName.getScene().getWindow()).close();
        logger.exit();

    }

    @FXML
    private void OK_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("4.5", "Create Federation Execution service");
        try {
            log.getSuppliedArguments().add(new ClassValuePair("Federation Execution Name", String.class, FederationExecutionName.getText()));
            List<URL> foms = new ArrayList<>();
            int i = 1;
            for (File file : FomModuleDesignators.getFiles()) {
                foms.add(file.toURI().toURL());
                log.getSuppliedArguments().add(new ClassValuePair("FOM Module Designator " + i++, URL.class, file.toURI().toURL().toString()));
            }
            if (MimDesignator.getText().isEmpty()) {
                log.getSuppliedArguments().add(new ClassValuePair("Logical Time Implementation", String.class, LogicalTimeImplementation.getValue()));
                rtiAmb.createFederationExecution(FederationExecutionName.getText(),
                        foms.toArray(new URL[foms.size()]),
                        LogicalTimeImplementation.getValue());
            } else {
                File mimFile = new File(MimDesignator.getText());
                log.getSuppliedArguments().add(new ClassValuePair("MIM Module Designator", URL.class, mimFile.toURI().toURL().toString()));
                log.getSuppliedArguments().add(new ClassValuePair("Logical Time Implementation", String.class, LogicalTimeImplementation.getValue()));
                rtiAmb.createFederationExecution(FederationExecutionName.getText(),
                        foms.toArray(new URL[foms.size()]),
                        mimFile.toURI().toURL(),
                        LogicalTimeImplementation.getValue());
            }
            log.setDescription("Federation execution created successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (CouldNotCreateLogicalTimeFactory | InconsistentFDD | ErrorReadingFDD | 
                CouldNotOpenFDD | ErrorReadingMIM | CouldNotOpenMIM | DesignatorIsHLAstandardMIM | 
                FederationExecutionAlreadyExists | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        ((Stage) FederationExecutionName.getScene().getWindow()).close();
        logger.exit();
    }
}
