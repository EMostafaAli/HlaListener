package ca.mali.hlalistener;

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
import hla.rti1516e.LogicalTime;
import hla.rti1516e.LogicalTimeInterval;
import hla.rti1516e.ResignAction;
import hla.rti1516e.TimeQueryReturn;
import hla.rti1516e.exceptions.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static ca.mali.hlalistener.PublicVariables.*;

/**
 * FXML Controller class
 *
 * @author Mostafa
 */
public class MainWindowController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();
    @FXML
    TableView<ClassValuePair> SupplyTable;
    @FXML
    TableColumn<ClassValuePair, String> SupplyNameCol;
    @FXML
    TableColumn<ClassValuePair, String> SupplyValueCol;
    @FXML
    Label SuppliedArgsLbl;
    @FXML
    TableView<ClassValuePair> returnTable;
    @FXML
    TableColumn<ClassValuePair, String> ReturnNameCol;
    @FXML
    TableColumn<ClassValuePair, String> ReturnValueCol;
    @FXML
    Label ReturnedArgsLbl;
    @FXML
    TableView<LogEntry> logTable;
    @FXML
    TableColumn<LogEntry, Integer> idCol;
    @FXML
    TableColumn<LogEntry, String> sectionCol;
    @FXML
    TableColumn<LogEntry, String> titleCol;
    @FXML
    TableColumn<LogEntry, String> timeCol;
    @FXML
    TableColumn<LogEntry, Image> iconCol;
    @FXML
    Label titleLbl;
    @FXML
    Label sectionLbl;
    @FXML
    Label DescLbl;
    @FXML
    ImageView iconViewer;
    @FXML
    TextArea StackTraceTextArea;
    @FXML
    TitledPane SuppliedPane;
    @FXML
    TitledPane ReturnedPane;
    @FXML
    TitledPane StackTracePane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.entry();
        Label label = new Label("No content in the logger table");
        label.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        logTable.setPlaceholder(label);
        Label emptyLabel = new Label();
        SupplyTable.setPlaceholder(emptyLabel);
        returnTable.setPlaceholder(emptyLabel);
        idCol.setCellValueFactory(new PropertyValueFactory<>("logID"));
        sectionCol.setCellValueFactory(new PropertyValueFactory<>("sectionNo"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("simulationTime"));
        iconCol.setCellValueFactory(new PropertyValueFactory<>("icon"));
        iconCol.setCellFactory(param -> new TableCell<LogEntry, Image>() {
            ImageView imgView;

            {
                alignmentProperty().set(Pos.CENTER);
                imgView = new ImageView();
                imgView.setFitHeight(10);
                imgView.setFitWidth(10);
            }

            @Override
            protected void updateItem(Image item, boolean empty) {
                if (item != null) {
                    imgView.setImage(item);
                    setGraphic(imgView);
                } else {
                    setGraphic(null);
                }
            }
        });
        logTable.setItems(logEntries);
        titleLbl.textProperty().bind(Bindings.selectString(logTable.getSelectionModel().selectedItemProperty(), "title"));
        sectionLbl.textProperty().bind(Bindings.selectString(logTable.getSelectionModel().selectedItemProperty(), "sectionNo"));
        DescLbl.textProperty().bind(Bindings.selectString(logTable.getSelectionModel().selectedItemProperty(), "description"));
        iconViewer.imageProperty().bind(Bindings.select(logTable.getSelectionModel().selectedItemProperty(), "icon"));

        SuppliedPane.managedProperty().bind(SuppliedPane.visibleProperty());
        SuppliedPane.visibleProperty().bind(Bindings.selectBoolean(logTable.getSelectionModel().selectedItemProperty(), "SuppliedArgumentsIsNotEmpty"));
        SupplyNameCol.setCellValueFactory(new PropertyValueFactory<>("className"));
        SupplyValueCol.setCellValueFactory(new PropertyValueFactory<>("classValue"));
        SuppliedArgsLbl.textProperty().bind(Bindings.selectString(SupplyTable.getSelectionModel().selectedItemProperty(), "classType"));

        ReturnedPane.managedProperty().bind(ReturnedPane.visibleProperty());
        ReturnedPane.visibleProperty().bind(Bindings.selectBoolean(logTable.getSelectionModel().selectedItemProperty(), "ReturnedArguementsIsNotEmpty"));
        ReturnNameCol.setCellValueFactory(new PropertyValueFactory<>("className"));
        ReturnValueCol.setCellValueFactory(new PropertyValueFactory<>("classValue"));
        ReturnedArgsLbl.textProperty().bind(Bindings.selectString(returnTable.getSelectionModel().selectedItemProperty(), "classType"));

        StackTracePane.managedProperty().bind(StackTracePane.visibleProperty());
        StackTracePane.visibleProperty().bind(Bindings.selectString(logTable.getSelectionModel().selectedItemProperty(), "stackTrace").isNotEmpty());
        StackTraceTextArea.textProperty().bind(Bindings.selectString(logTable.getSelectionModel().selectedItemProperty(), "stackTrace"));

        logTable.itemsProperty().get().addListener((ListChangeListener.Change<? extends LogEntry> c) -> {
            c.next();
            if (c.wasAdded()) {
                //Use platform becasue trying to update UI from another thread (ListenerFederateAmb) raises an error
                Platform.runLater(() -> {
                    logTable.getSelectionModel().selectLast();
                    //work around to refresh the log table view
                    logTable.getColumns().get(0).setVisible(false);
                    logTable.getColumns().get(0).setVisible(true);
                });
            }
        });

        logTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                SupplyTable.setItems(newValue.getSuppliedArguments());
                returnTable.setItems(newValue.getReturnedArguments());
            }
        });
        logger.exit();
    }

    private void DisplayDialog(String title, String fxmlPath) throws IOException {
        logger.entry();
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setResizable(false);
        dialog.setTitle(title);
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene dialogScene = new Scene(root);
        dialog.setScene(dialogScene);
        dialog.show();
        logger.exit();
    }

    @FXML
    private void RtiInfo_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("", "RTI Info");
        try {
            log.setLogType(LogEntryType.REQUEST);
            log.getReturnedArguments().add(new ClassValuePair("RTI Name", String.class, rtiFactory.rtiName()));
            log.getReturnedArguments().add(new ClassValuePair("RTI Version", String.class, rtiFactory.rtiVersion()));
            log.getReturnedArguments().add(new ClassValuePair("RTI HLA Version", String.class, rtiAmb.getHLAversion()));
            logger.log(Level.INFO, rtiFactory.rtiName());
            logger.log(Level.INFO, rtiFactory.rtiVersion());
            logger.log(Level.INFO, rtiAmb.getHLAversion());
            log.setDescription("RTI Info retrieved successfully");
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    @FXML
    private void ClearLog_click(ActionEvent event) {
        try {
            logger.entry();
            logEntries.clear();
            LogEntry.id = 0;
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error clearing the log", ex);
        }
    }

    @FXML
    private void CloseHlaListener_click(ActionEvent event) {
        try {
            logger.entry();
//            primaryStage.close(); //Using this line will not fire the close on request
            primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error clearing the log", ex);
        }
    }

// <editor-fold desc="Chapter 4">
    //4.2
    @FXML
    private void Connect_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.2 Connect service", "/fxml/chapter4/ConnectService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Connect service dialog box", ex);
        }
    }

    //4.3
    @FXML
    private void Disconnect_click(ActionEvent event) {
        LogEntry log = new LogEntry("4.3", "Disconnect service");
        try {
            logger.entry();
            rtiAmb.disconnect();
            isConnected = false;
            log.setDescription("Disconnected successfully, you can terminate the program");
            log.setLogType(LogEntryType.REQUEST);
        } catch (FederateIsExecutionMember | CallNotAllowedFromWithinCallback | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //4.5
    @FXML
    private void CreateFederation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.5 Create Federation Execution service", "/fxml/chapter4/CreateFederationExecutionService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Create Federation service dialog box", ex);
        }
    }

    //4.6
    @FXML
    private void DestroyFederation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.6 Destroy Federation Execution service", "/fxml/chapter4/DestroyFederationExecutionService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Destroy Federation service dialog box", ex);
        }
    }

    //4.7
    @FXML
    private void ListFederationExecutions_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("4.7", "List Federation Execution service");
        try {
            rtiAmb.listFederationExecutions();
            log.setDescription("Federation execution list requested successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //4.9
    @FXML
    private void JoinFederation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.9 Join Federation Execution service", "/fxml/chapter4/JoinFederationExecutionService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Join Federation service dialog box", ex);
        }
    }

    //4.10
    @FXML
    private void ResignFederation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.10 Resign Federation Execution service", "/fxml/chapter4/ResignFederationExecutionService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Resign Federation service dialog box", ex);
        }
    }

    //4.11
    @FXML
    private void RegisterSyncPoint_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.11 Register Federation Synchronization Point service", "/fxml/chapter4/RegisterFederationSyncPointService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Register Sync Point service dialog box", ex);
        }
    }

    //4.14
    @FXML
    private void SyncPointAchieved_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.14 Synchronization Point Achieved service", "/fxml/chapter4/SyncPointAchievedService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Sync Point Achieved dialog box", ex);
        }
    }

    //4.16
    @FXML
    private void RequestFederationSave_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.16 Request Federation Save service", "/fxml/chapter4/RequestFederationSaveService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Request Save service dialog box", ex);
        }
    }

    //4.18
    @FXML
    private void FederateSaveBegun_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("4.18", "Federate Save Begun service");
        try {
            rtiAmb.federateSaveBegun();
            log.setDescription("Federate save can begin now");
            log.setLogType(LogEntryType.REQUEST);
        } catch (FederateNotExecutionMember | RestoreInProgress |
                SaveNotInitiated | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //4.19
    @FXML
    private void FederateSaveComplete_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.19 Federate Save Complete service", "/fxml/chapter4/FederateSaveCompleteService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Federate Save Complete service dialog box", ex);
        }
    }

    //4.21
    @FXML
    private void AbortFederationSave_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("4.21", "Abort Federation Save service");
        try {
            rtiAmb.abortFederationSave();
            log.setDescription("Federation save aborted");
            log.setLogType(LogEntryType.REQUEST);
        } catch (FederateNotExecutionMember | SaveNotInProgress |
                NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //4.22
    @FXML
    private void QueryFederationSave_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("4.22", " Query Federation Save Status service");
        try {
            rtiAmb.queryFederationSaveStatus();
            log.setDescription("Federation save status queried successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (FederateNotExecutionMember | RestoreInProgress |
                NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //4.24
    @FXML
    private void RequestFederationRestore_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.24 Request Federation Restore service", "/fxml/chapter4/RequestFederationRestoreService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Request Federation Store service dialog box", ex);
        }
    }

    //4.28
    @FXML
    private void FederateRestoreComplete_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("4.28 Federate Restore Complete service", "/fxml/chapter4/FederateRestoreCompleteService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Federate Restore Complete service dialog box", ex);
        }
    }

    //4.30
    @FXML
    private void AbortFederationRestore_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("4.30", "Abort Federation Restore service");
        try {
            rtiAmb.abortFederationRestore();
            log.setDescription("Federation restore aborted");
            log.setLogType(LogEntryType.REQUEST);
        } catch (FederateNotExecutionMember | RestoreNotInProgress |
                NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //4.31
    @FXML
    private void QueryFederationRestore_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("4.31", "Query Federation Restore Status service");
        try {
            rtiAmb.queryFederationRestoreStatus();
            log.setDescription("Federation restore status queried successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (FederateNotExecutionMember | SaveInProgress |
                NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }
// </editor-fold>
 
// <editor-fold desc="Chapter 5">
    //5.2
    @FXML
    private void PublishObjects_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("5.2 Publish Object Class Attributes service", "/fxml/chapter5/PublishObjectClassAttributesService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Publish Object Class Attributes service dialog box", ex);
        }
    }
    
    //5.3
    @FXML
    private void UnpublishObjects_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("5.3 Unpublish Object Class Attributes service", "/fxml/chapter5/UnpublishObjectClassAttributesService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Unpublish Object Class Attributes service dialog box", ex);
        }
    }
    
    //5.4
    @FXML
    private void PublishInteraction_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("5.4 Publish Interaction Class service", "/fxml/chapter5/PublishInteractionClassService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Publish Interaction Class service dialog box", ex);
        }
    }
    
    //5.5
    @FXML
    private void UnpublishInteraction_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("5.5 Unpublish Interaction Class service", "/fxml/chapter5/UnpublishInteractionClassService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Unpublish Interaction Class service dialog box", ex);
        }
    }
    
    //5.6
    @FXML
    private void SubscribeObjects_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("5.6 Subscribe Object Class Attributes service", "/fxml/chapter5/SubscribeObjectClassAttributesService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Subscribe Object Class Attributes service dialog box", ex);
        }
    }
    
    //5.7
    @FXML
    private void UnsubscribeObjects_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("5.7 Unsubscribe Object Class Attributes service", "/fxml/chapter5/UnsubscribeObjectClassAttributesService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Unsubscribe Object Class Attributes service dialog box", ex);
        }
    }
    
    //5.8
    @FXML
    private void SubscribeInteractions_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("5.8 Subscribe Interaction Class service", "/fxml/chapter5/SubscribeInteractionClassService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Subscribe Interaction Class service dialog box", ex);
        }
    }
    
    //5.9
    @FXML
    private void UnsubscribeInteractions_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("5.9 Unsubscribe Interaction Class service", "/fxml/chapter5/UnsubscribeInteractionClassService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Unsubscribe Interaction Class service dialog box", ex);
        }
    }
// </editor-fold>

// <editor-fold desc="Chapter 6">
    //6.2
    @FXML
    private void ReserveName_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("6.2 Reserve Object Instance Name service", "/fxml/chapter6/ReserveObjectInstanceNameService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Reserve Object Instance Name service dialog box", ex);
        }
    }
    
    //6.4
    @FXML
    private void ReleaseName_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("6.4 Release Object Instance Name service", "/fxml/chapter6/ReleaseObjectInstanceNameService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Release Object Instance Name service dialog box", ex);
        }
    }
    
    //6.5
    @FXML
    private void ReserveMultipleNames_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("6.5 Reserve Multiple Object Instance Names service", "/fxml/chapter6/ReserveMultipleObjectInstanceNameService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Reserve Multiple Object Instance Names service dialog box", ex);
        }
    }
    
    //6.7
    @FXML
    private void ReleaseMultipleNames_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("6.7 Release Multiple Object Instance Names service", "/fxml/chapter6/ReleaseMultipleObjectInstanceNameService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Release Multiple Object Instance Names service dialog box", ex);
        }
    }
    
    //6.8
    @FXML
    private void RegisterObject_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("6.8 Register Object Instance service", "/fxml/chapter6/RegisterObjectInstanceService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Register Object Instance service dialog box", ex);
        }
    }
    
    //6.14
    @FXML
    private void DeleteObject_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("6.14 Delete Object Instance service", "/fxml/chapter6/DeleteObjectInstanceService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Delete Object Instance service dialog box", ex);
        }
    }
    
    //6.16
    @FXML
    private void LocalDeleteObject_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("6.16 Local Delete Object Instance service", "/fxml/chapter6/LocalDeleteObjectInstanceService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Local Delete Object Instance service dialog box", ex);
        }
    }
    
    //6.23
    @FXML
    private void RequestAttributeTransporationChange_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("6.23 Request Attribute Transportation Type Change service", "/fxml/chapter6/RequestAttributeTransportationTypeChangeService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Request Attribute Transportation Type Change service dialog box", ex);
        }
    }
    
    //6.25
    @FXML
    private void QueryAttributeTransporation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("6.25 Query Attribute Transportation Type service", "/fxml/chapter6/QueryAttributeTransportationTypeService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Query Attribute Transportation Type service dialog box", ex);
        }
    }
    
    //6.27
    @FXML
    private void RequestTransportationType_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("6.27  Request Interaction Transportation Type Change service", "/fxml/chapter6/RequestInteractionTransportationTypeChangeService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Request Interaction Transportation Type Change service dialog box", ex);
        }
    }
    
    //6.29
    @FXML
    private void QueryInteractionTransportation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("6.29 Query Interaction Transportation Type service", "/fxml/chapter6/QueryInteractionTransportationTypeService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Query Interaction Transportation Type service dialog box", ex);
        }
    }
// </editor-fold>

// <editor-fold desc="Chapter 7">
    //7.2
    @FXML
    private void UnconditionalDivistiture_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("7.2 Unconditional Attribute Ownership Divestiture service", "/fxml/chapter7/UnconditionalAttributeOwnershipDivestitureService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Unconditional Attribute Ownership Divestiture service dialog box", ex);
        }
    }
    
    //7.3
    @FXML
    private void NegotiateDivistiture_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("7.3 Negotiated Attribute Ownership Divestiture service", "/fxml/chapter7/NegotiatedAttributeOwnershipDivestitureService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Negotiated Attribute Ownership Divestiture service dialog box", ex);
        }
    }
    
    //7.6
    @FXML
    private void ConfirmDivistiture_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("7.6 Confirm Divestiture service", "/fxml/chapter7/ConfirmDivestitureService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Confirm Divestiture service dialog box", ex);
        }
    }
    
    //7.8
    @FXML
    private void AttributeAcquisition_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("7.8 Attribute Ownership Acquisition service", "/fxml/chapter7/AttributeOwnershipAcquisitionService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Attribute Ownership Acquisition service dialog box", ex);
        }
    }
    
    //7.9
    @FXML
    private void AttributeAcquisitionIfAvailable_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("7.9 Attribute Ownership Acquisition If Available service", "/fxml/chapter7/AttributeOwnershipAcquisitionIfAvailableService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Attribute Ownership Acquisition If Available service dialog box", ex);
        }
    }
    
    //7.12
    @FXML
    private void AttributeReleaseDenied_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("7.12 Attribute Ownership Release Denied service", "/fxml/chapter7/AttributeOwnershipReleaseDeniedService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Attribute Ownership Release Denied service dialog box", ex);
        }
    }
    
    //7.13
    @FXML
    private void AttributeDivestitureIfWanted_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("7.13 Attribute Ownership Divestiture If Wanted service", "/fxml/chapter7/AttributeOwnershipDivestitureIfWantedService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Attribute Ownership Divestiture If Wanted service dialog box", ex);
        }
    }
    
    //7.14
    @FXML
    private void CancelOwnershipDivestiture_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("7.14 Cancel Negotiated Attribute Ownership Divestiture service", "/fxml/chapter7/CancelNegotiatedAttributeOwnershipDivestitureService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Cancel Negotiated Attribute Ownership Divestiture service dialog box", ex);
        }
    }
    
    //7.15
    @FXML
    private void CancelOwnershipAcquistion_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("7.15 Cancel Attribute Ownership Acquisition service", "/fxml/chapter7/CancelAttributeOwnershipAcquisitionService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Cancel Attribute Ownership Acquisition service dialog box", ex);
        }
    }
    
    //7.17
    @FXML
    private void QueryAttributeOwnership_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("7.17 Query Attribute Ownership service", "/fxml/chapter7/QueryAttributeOwnershipService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Query Attribute Ownership service dialog box", ex);
        }
    }
    
    //7.19
    @FXML
    private void IsAttributeOwned_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("7.19 Is Attribute Owned by Federate service", "/fxml/chapter7/IsAttributeOwnedByFederateService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Is Attribute Owned by Federate service dialog box", ex);
        }
    }
 
// </editor-fold>

// <editor-fold desc="Chapter 8">
    //8.2
    @FXML
    private void EnableTimeRegulation_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("8.2 Enable Time Regulation service", "/fxml/chapter8/EnableTimeRegulationService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Enabling Time Regulation service dialog box", ex);
        }
    }

    //8.4
    @FXML
    private void DisableTimeRegulation_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("8.4", "Disable Time Regulation service");
        try {
            rtiAmb.disableTimeRegulation();
            log.setDescription("Time Regulation disabled successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (FederateNotExecutionMember | TimeRegulationIsNotEnabled |
                SaveInProgress | RestoreInProgress | NotConnected |
                RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //8.5
    @FXML
    private void EnableTimeConstrained_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("8.5", "Enable Time Constrained service");
        try {
            rtiAmb.enableTimeConstrained();
            log.setDescription("Time Constrained requested successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (FederateNotExecutionMember | TimeConstrainedAlreadyEnabled |
                InTimeAdvancingState | RequestForTimeConstrainedPending |
                SaveInProgress | RestoreInProgress | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //8.7
    @FXML
    private void DisableTimeConstrained_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("8.7", "Disable Time Constrained service");
        try {
            rtiAmb.disableTimeConstrained();
            log.setDescription("Time Constrained disabled successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (FederateNotExecutionMember | TimeConstrainedIsNotEnabled |
                SaveInProgress | RestoreInProgress | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }
    
    //8.8
    @FXML
    private void TimeAdvanceRequest_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("8.8 Time Advance Request", "/fxml/chapter8/TimeAdvanceRequestService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Time Advance Request service dialog box", ex);
        }
    }
    
    //8.9
    @FXML
    private void TimeAdvanceRequestAvailable_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("8.9 Time Advance Request Available", "/fxml/chapter8/TimeAdvanceRequestAvailableService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Time Advance Request Available service dialog box", ex);
        }
    }
    
    //8.10
    @FXML
    private void NextMessageRequest_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("8.10 Next Message Request", "/fxml/chapter8/NextMessageRequestService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Next Message Request service dialog box", ex);
        }
    }
    
    //8.11
    @FXML
    private void NextMessageRequestAvailable_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("8.11 Next Message Request Available", "/fxml/chapter8/NextMessageRequestAvailableService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Next Message Request Available service dialog box", ex);
        }
    }
    
    //8.12
    @FXML
    private void FlushQueueRequest_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("8.12 Flush Queue Request service", "/fxml/chapter8/FlushQueueRequestService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Flush Queue Request service dialog box", ex);
        }
    }

    //8.14
    @FXML
    private void EnableAsynchronousDelivery_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("8.14", "Enable Asynchronous Delivery service");
        try {
            rtiAmb.enableAsynchronousDelivery();
            log.setDescription("Asynchronous Delivery enabled successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (AsynchronousDeliveryAlreadyEnabled | SaveInProgress |
                RestoreInProgress | FederateNotExecutionMember | NotConnected |
                RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //8.15
    @FXML
    private void DisableAsynchronousDelivery_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("8.15", "Disable Asynchronous Delivery service");
        try {
            rtiAmb.disableAsynchronousDelivery();
            log.setDescription("Asynchronous Delivery disabled successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (AsynchronousDeliveryAlreadyDisabled | SaveInProgress |
                RestoreInProgress | FederateNotExecutionMember | NotConnected |
                RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //8.16
    @FXML
    private void QueryGALT_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("8.16", "Query GALT service");
        try {
            TimeQueryReturn tQR = rtiAmb.queryGALT();
            if (tQR.timeIsValid) {
                log.getReturnedArguments().add((new ClassValuePair("Time is valid", boolean.class, "true")));
                log.getReturnedArguments().add((new ClassValuePair("Logical Time", LogicalTime.class, tQR.time.toString())));
            } else {
                log.getReturnedArguments().add((new ClassValuePair("Time is valid", boolean.class, "false")));
            }
            log.setDescription("Greatest Available Logical Time (GALT) queried successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (SaveInProgress | RestoreInProgress |
                FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //8.17
    @FXML
    private void QueryLogicalTime_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("8.17", "Query Logical Time service");
        try {
            LogicalTime logicalTime = rtiAmb.queryLogicalTime();
            log.getReturnedArguments().add((new ClassValuePair("Logical Time", LogicalTime.class, logicalTime.toString())));
            log.setDescription("Logical Time queried successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (SaveInProgress | RestoreInProgress |
                FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //8.18
    @FXML
    private void QueryLITS_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("8.18", "Query LITS service");
        try {
            TimeQueryReturn tQR = rtiAmb.queryLITS();
            if (tQR.timeIsValid) {
                log.getReturnedArguments().add((new ClassValuePair("Time is valid", boolean.class, "true")));
                log.getReturnedArguments().add((new ClassValuePair("Logical Time", LogicalTime.class, tQR.time.toString())));
            } else {
                log.getReturnedArguments().add((new ClassValuePair("Time is valid", boolean.class, "false")));
            }
            log.setDescription("Least Incoming Timestamp (LITS) queried successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (SaveInProgress | RestoreInProgress |
                FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //8.19
    @FXML
    private void ModifyLookahead_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("8.19 Modify Lookahead service", "/fxml/chapter8/ModifyLookaheadService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Modify Lookahed service dialog box", ex);
        }
    }
    
    //8.20
    @FXML
    private void QueryLookahead_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("8.20", "Query Lookahead service");
        try {
            LogicalTimeInterval logicalTimeInterval = rtiAmb.queryLookahead();
            log.getReturnedArguments().add((new ClassValuePair("Lookahead", LogicalTimeInterval.class, logicalTimeInterval.toString())));
            log.setDescription("Lookahead queried successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (TimeRegulationIsNotEnabled | SaveInProgress | RestoreInProgress |
                FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }
    
    //8.21
    @FXML
    private void RetractMessage_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("8.21 Retract service", "/fxml/chapter8/RetractService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Retract service dialog box", ex);
        }
    }
    
    //8.23
    @FXML
    private void ChangeAttributeOrderType_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("8.23 Change Attribute Order Type service", "/fxml/chapter8/ChangeAttributeOrderType.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Change Attribute Order Type service dialog box", ex);
        }
    }
    
    //8.24
    @FXML
    private void ChangeInteractionOrderType_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("8.24 Change Interaction Order Type service", "/fxml/chapter8/ChangeInteractionOrderType.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Change Interaction Order Type service dialog box", ex);
        }
    }
// </editor-fold>
    
    //10.2
    @FXML
    private void GetAutomaticResignDirective_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("10.2", "Get Automatic Resign Directive service");
        try {
            ResignAction resignAction = rtiAmb.getAutomaticResignDirective();
            log.getReturnedArguments().add((new ClassValuePair("Automatic resign directive", ResignAction.class, resignAction.toString())));
            log.setDescription("Automatic Resign Directive retrieved successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }
    
    //10.3
    @FXML
    private void SetAutomaticResignDirective_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.3 Set Automatic Resign Directive service", "/fxml/chapter10/SetAutomaticResignDirectiveService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Set Automatic Resign Directive service dialog box", ex);
        }
    }
    
    //10.4
    @FXML
    private void GetFederateHandle_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.4 Get Federate Handle service", "/fxml/chapter10/GetFederateHandleService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Federate Handle service dialog box", ex);
        }
    }
    
    //10.5
    @FXML
    private void GetFederateName_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.5 Get Federate Name service", "/fxml/chapter10/GetFederateNameService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Federate Name service dialog box", ex);
        }
    }
    
    //10.6
    @FXML
    private void GetObjectClassHandle_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.6 Get Object Class Handle service", "/fxml/chapter10/GetObjectClassHandleService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Object Class Handle service dialog box", ex);
        }
    }
    
    //10.7
    @FXML
    private void GetObjectClassName_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.7 Get Object Class Name service", "/fxml/chapter10/GetObjectClassNameService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Object Class Name service dialog box", ex);
        }
    }
    
    //10.8
    @FXML
    private void GetKnownObjectClassHandle_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.8 Get Known Object Class Handle service", "/fxml/chapter10/GetKnownObjectClassHandleService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Known Object Class Handle service dialog box", ex);
        }
    }
    
    //10.9
    @FXML
    private void GetObjectInstanceHandle_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.9 Get Object Instance Handle service", "/fxml/chapter10/GetObjectInstanceHandleService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Object Instance Handle service dialog box", ex);
        }
    }
    
    //10.10
    @FXML
    private void GetObjectInstanceName_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.10 Get Object Instance Name service", "/fxml/chapter10/GetObjectInstanceNameService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Object Instance Name service dialog box", ex);
        }
    }
    
    //10.11
    @FXML
    private void GetAttributeHandle_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.11 Get Attribute Handle service", "/fxml/chapter10/GetAttributeHandleService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Attribute Handle service dialog box", ex);
        }
    }
    
    //10.12
    @FXML
    private void GetAttributeName_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.11 Get Attribute Name service", "/fxml/chapter10/GetAttributeNameService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Attribute Name service dialog box", ex);
        }
    }
    
    //10.13
    @FXML
    private void GetUpdateRateValue_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.13 Get Update Rate Value service", "/fxml/chapter10/GetUpdateRateValueService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Update Rate Value service dialog box", ex);
        }
    }
    
    //10.14
    @FXML
    private void GetUpdateRateValueForAttribute_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.14 Get Update Rate Value For Attribute service", "/fxml/chapter10/GetUpdateRateValueForAttributeService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Update Rate Value For Attribute service dialog box", ex);
        }
    }
    
    //10.15
    @FXML
    private void GetInteractionHandle_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.15 Get Interaction Class Handle service", "/fxml/chapter10/GetInteractionClassHandleService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Interaction Class Handle service dialog box", ex);
        }
    }
    
    //10.16
    @FXML
    private void GetInteractionName_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.16 Get Interaction Class Name service", "/fxml/chapter10/GetInteractionClassNameService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Interaction Class Name service dialog box", ex);
        }
    }
    
    //10.18
    @FXML
    private void GetParameterName_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.18 Get Parameter Name service", "/fxml/chapter10/GetParameterNameService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Parameter Name service dialog box", ex);
        }
    }
    
    //10.19
    @FXML
    private void GetOrderType_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.19 Get Order Type service", "/fxml/chapter10/GetOrderTypeService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Order Type service dialog box", ex);
        }
    }
    
    //10.20
    @FXML
    private void GetOrderName_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.20 Get Order Name service", "/fxml/chapter10/GetOrderNameService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Order Name service dialog box", ex);
        }
    }
    
    //10.21
    @FXML
    private void GetTransportationTypeHandle_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.21 Get Transportation Type Handle service", "/fxml/chapter10/GetTransportationTypeHandleService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Transportation Type Handle service dialog box", ex);
        }
    }
    
    //10.22
    @FXML
    private void GetTransportationTypeName_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.21 Get Transportation Type Name service", "/fxml/chapter10/GetTransportationTypeNameService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Transportation Type Name service dialog box", ex);
        }
    }
    
    //10.23
    @FXML
    private void GetAvailDimAttribute_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.23 Get Available Dimensions For Class Attribute service", "/fxml/chapter10/GetAvailableDimensionsForClassAttributeService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Available Dimensions For Class Attribute service dialog box", ex);
        }
    }
    
    //10.24
    @FXML
    private void GetAvailDimInteraction_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.24 Get Available Dimensions For Interaction Class service", "/fxml/chapter10/GetAvailableDimensionsForInteractionClassService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Get Available Dimensions For Interaction Class service dialog box", ex);
        }
    }
    
    //10.33
    @FXML
    private void EnableObjectClassRelevanceAdvisorySwitch_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("10.33", "Enable Object Class Relevance Advisory Switch service");
        try {
            rtiAmb.enableObjectClassRelevanceAdvisorySwitch();
            log.setDescription("Object Class Relevance Advisory Switch enabled successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (ObjectClassRelevanceAdvisorySwitchIsOn | SaveInProgress | RestoreInProgress |
                FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }
    
    //10.34
    @FXML
    private void DisableObjectClassRelevanceAdvisorySwitch_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("10.33", "Disable Object Class Relevance Advisory Switch service");
        try {
            rtiAmb.disableObjectClassRelevanceAdvisorySwitch();
            log.setDescription("Object Class Relevance Advisory Switch disabled successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (ObjectClassRelevanceAdvisorySwitchIsOff | SaveInProgress | RestoreInProgress |
                FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //10.35
    @FXML
    private void EnableAttributeRelevanceSwitch_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("10.35", "Enable Attribute Relevance Advisory Switch service");
        try {
            rtiAmb.enableAttributeRelevanceAdvisorySwitch();
            log.setDescription("Attribute Relevance Advisory Switch enabled successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (AttributeRelevanceAdvisorySwitchIsOn | FederateNotExecutionMember | NotConnected | RTIinternalError |
                RestoreInProgress | SaveInProgress ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //10.36
    @FXML
    private void DisableAttributeRelevanceSwitch_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("10.36", "Disable Attribute Relevance Advisory Switch service");
        try {
            rtiAmb.disableAttributeRelevanceAdvisorySwitch();
            log.setDescription("Attribute Relevance Advisory Switch disabled successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (AttributeRelevanceAdvisorySwitchIsOff | FederateNotExecutionMember | NotConnected | RTIinternalError |
                RestoreInProgress | SaveInProgress ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //10.37
    @FXML
    private void EnableAttributeScopeAdvisorySwitch_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("10.37", "Enable Attribute Scope Advisory Switch service");
        try {
            rtiAmb.enableAttributeScopeAdvisorySwitch();
            log.setDescription("Attribute Relevance Advisory Switch enabled successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (AttributeScopeAdvisorySwitchIsOn | FederateNotExecutionMember | RTIinternalError | NotConnected |
                SaveInProgress | RestoreInProgress ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //10.38
    @FXML
    private void DisableAttributeScopeAdvisorySwitch_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("10.38", "Disable Attribute Scope Advisory Switch service");
        try {
            rtiAmb.disableAttributeScopeAdvisorySwitch();
            log.setDescription("Attribute Relevance Advisory Switch enabled successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (AttributeScopeAdvisorySwitchIsOff | FederateNotExecutionMember | RTIinternalError | NotConnected |
                SaveInProgress | RestoreInProgress ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }
    
    //10.39
    @FXML
    private void EnableInteractionRelevanceAdvisorySwitch_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("10.39", "Enable Interaction Relevance Advisory Switch service");
        try {
            rtiAmb.enableInteractionRelevanceAdvisorySwitch();
            log.setDescription("Interaction Relevance Advisory Switch enabled successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (InteractionRelevanceAdvisorySwitchIsOn | SaveInProgress | RestoreInProgress |
                FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }
    
    //10.40
    @FXML
    private void DisableInteractionRelevanceAdvisorySwitch_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("10.40", "Disable Interaction Relevance Advisory Switch service");
        try {
            rtiAmb.disableInteractionRelevanceAdvisorySwitch();
            log.setDescription("Interaction Relevance Advisory Switch disabled successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (InteractionRelevanceAdvisorySwitchIsOff | SaveInProgress | RestoreInProgress |
                FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //10.41
    @FXML
    private void EvokeCallback_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.41 Evoke Callback service", "/fxml/chapter10/EvokeCallbackService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Evoke Callback service dialog box", ex);
        }
    }

    //10.42
    @FXML
    private void EvokeMultipleCallbacks_click(ActionEvent event) {
        try {
            logger.entry();
            DisplayDialog("10.42 Evoke Multiple Callbacks service", "/fxml/chapter10/EvokeMultipleCallbacksService.fxml");
            logger.exit();
        } catch (Exception ex) {
            logger.log(Level.FATAL, "Error Displaying Evoke Multiple Callbacks service dialog box", ex);
        }
    }

    //10.43
    @FXML
    private void EnableCallbacksservice_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("10.43", "Enable Callbacks service");
        try {
            rtiAmb.enableCallbacks();
            log.setDescription("Callbacks enabled successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (RTIinternalError | RestoreInProgress | SaveInProgress ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    //10.44
    @FXML
    private void DisableCallbacksservice_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("10.44", "Disable Callbacks service");
        try {
            rtiAmb.disableCallbacks();
            log.setDescription("Callbacks disabled successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (RTIinternalError | RestoreInProgress | SaveInProgress ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        logger.exit();
    }

    @FXML
    private void AboutWindow_click(ActionEvent event) {
        Alert aboutWindow = new Alert(Alert.AlertType.INFORMATION);
        aboutWindow.setTitle("HLA Listener");
        aboutWindow.setHeaderText("HLA Listener v1.0.0");
        aboutWindow.setContentText("Developed by Mostafa Ali (engabdomostafa@gmail.com)\nAll rights reserved");
        aboutWindow.initOwner(primaryStage);
        aboutWindow.showAndWait();
    }
}
