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

package ca.mali.customcontrol;

import ca.mali.fomparser.FddObjectModel;
import hla.rti1516e.RegionHandle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static ca.mali.hlalistener.PublicVariables.regionHandles;

/**
 * Created by Mostafa Ali on 8/31/2015.
 */
public class RegionListController extends VBox {

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TableView<RegionState> RegionTableView;
    @FXML
    private TableColumn<RegionState, String> RegionTableColumn;
    @FXML
    private TableColumn CheckTableColumn;

    CheckBox cb = new CheckBox();
    ObservableList<RegionState> regions = FXCollections.observableArrayList();

    public RegionListController() {
        logger.entry();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/customcontrol/RegionsList.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();

        } catch (IOException ex) {
            logger.log(Level.FATAL, ex.getMessage(), ex);
        }
        logger.exit();
    }

    public void setFddObjectModel(FddObjectModel fddObjectModel) {
        logger.entry();
        if (fddObjectModel != null){
            regionHandles.forEach((regionHandle -> regions.add(new RegionState(regionHandle))));
            RegionTableView.setItems(regions);
            regions.forEach(regionState -> regionState.onProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    cb.setSelected(false);
                } else if (regions.stream().allMatch(regionState1 -> regionState1.isOn())) {
                    cb.setSelected(true);
                }
            }));
            RegionTableColumn.setCellValueFactory(new PropertyValueFactory<>("regionName"));
            CheckTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RegionState, Boolean>, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue call(TableColumn.CellDataFeatures<RegionState, Boolean> param) {
                    return param.getValue().onProperty();
                }
            });
            CheckTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(CheckTableColumn));
            cb.setUserData(CheckTableColumn);
            cb.setOnAction(event -> {
                CheckBox cb1 = (CheckBox) event.getSource();
                TableColumn tc = (TableColumn) cb1.getUserData();
                RegionTableView.getItems().stream().forEach((item) -> item.setOn(cb1.isSelected()));
            });
            CheckTableColumn.setGraphic(cb);
        }
        logger.exit();
    }

    public List<RegionHandle> getRegions(){
        return regions.stream().filter(RegionState::isOn).map(a -> a.getRegionHandle()).collect(Collectors.toList());
    }

    public static class RegionState{

        private final ReadOnlyStringWrapper regionName = new ReadOnlyStringWrapper();
        private final RegionHandle regionHandle;
        private final BooleanProperty on = new SimpleBooleanProperty();


        public RegionState(RegionHandle regionHandle) {
            this.regionHandle = regionHandle;
            regionName.set(regionHandle.toString());
        }

        public String getRegionName() {
            return regionName.get();
        }

        public ReadOnlyStringProperty regionNameProperty() {
            return regionName.getReadOnlyProperty();
        }

        public boolean isOn() {
            return on.get();
        }

        public void setOn(boolean value) {
            on.set(value);
        }

        public BooleanProperty onProperty() {
            return on;
        }

        @Override
        public String toString() {
            return getRegionHandle().toString();
        }

        public RegionHandle getRegionHandle() {
            return regionHandle;
        }
    }
}
