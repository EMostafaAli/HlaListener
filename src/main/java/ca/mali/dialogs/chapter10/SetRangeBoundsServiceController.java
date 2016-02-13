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
package ca.mali.dialogs.chapter10;

import ca.mali.hlalistener.ClassValuePair;
import ca.mali.hlalistener.LogEntry;
import ca.mali.hlalistener.LogEntryType;
import hla.rti1516e.DimensionHandle;
import hla.rti1516e.RangeBounds;
import hla.rti1516e.RegionHandle;
import hla.rti1516e.exceptions.*;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;

import static ca.mali.hlalistener.PublicVariables.*;

/**
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class SetRangeBoundsServiceController implements Initializable {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private ChoiceBox<RegionHandle> RegionHandleChoiceBox;

    @FXML
    private TextField DimensionHandleTextField;

    @FXML
    private Spinner<Integer> LowerSpinner;

    @FXML
    private Spinner<Integer> UpperSpinner;

    @FXML
    private Button OkButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        RegionHandleChoiceBox.setItems(regionHandles);
        logger.entry();
        if (RegionHandleChoiceBox.getItems().size() > 0) {
            OkButton.disableProperty().bind(
                    Bindings.isEmpty(DimensionHandleTextField.textProperty()));
        }
        SpinnerValueFactory lowerSVF = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0, 1);
        LowerSpinner.setValueFactory(lowerSVF);
        SpinnerValueFactory upperSVF = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1, 1);
        UpperSpinner.setValueFactory(upperSVF);
        logger.exit();
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        logger.entry();
        ((Stage) RegionHandleChoiceBox.getScene().getWindow()).close();
        logger.exit();
    }

    @FXML
    private void Ok_click(ActionEvent event) {
        logger.entry();
        LogEntry log = new LogEntry("10.30", "Set Range Bounds service");
        try {
            log.getSuppliedArguments().add(new ClassValuePair("Region Handle", RegionHandle.class, RegionHandleChoiceBox.getValue().toString()));
            DimensionHandle dimHandle = rtiAmb.getDimensionHandleFactory()
                    .decode(ByteBuffer.allocate(4).putInt(Integer.parseInt(DimensionHandleTextField.getText())).array(), 0);
            log.getSuppliedArguments().add(new ClassValuePair("Dimension Handle", DimensionHandle.class, dimHandle.toString()));
            log.getSuppliedArguments().add(new ClassValuePair("Range Lower Bound", long.class, LowerSpinner.getValue().toString()));
            log.getSuppliedArguments().add(new ClassValuePair("Range Upper Bound", long.class, UpperSpinner.getValue().toString()));
            rtiAmb.setRangeBounds(RegionHandleChoiceBox.getValue(), dimHandle, new RangeBounds(LowerSpinner.getValue(), UpperSpinner.getValue()));
            log.setDescription("Range bounds set successfully");
            log.setLogType(LogEntryType.REQUEST);
        } catch (FederateNotExecutionMember | RTIinternalError | InvalidRegion | NotConnected |
                RestoreInProgress | SaveInProgress | CouldNotDecode | RegionDoesNotContainSpecifiedDimension |
                InvalidRangeBound | RegionNotCreatedByThisFederate ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.ERROR);
            logger.log(Level.ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            log.setException(ex);
            log.setLogType(LogEntryType.FATAL);
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logEntries.add(log);
        ((Stage) RegionHandleChoiceBox.getScene().getWindow()).close();
        logger.exit();
    }
}