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
import static ca.mali.hlalistener.PublicVariables.*;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javafx.application.*;
import javafx.beans.binding.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import javafx.util.*;

import org.apache.logging.log4j.*;

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
        iconCol.setCellFactory(new Callback<TableColumn<LogEntry, Image>, TableCell<LogEntry, Image>>() {
            @Override
            public TableCell<LogEntry, Image> call(TableColumn<LogEntry, Image> param) {
                return new TableCell<LogEntry, Image>() {
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
                };
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
}
