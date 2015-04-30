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
package ca.mali.customcontrol;

import java.io.*;
import java.util.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import javafx.util.*;

/**
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class FilesList extends VBox {

    private ListView<String> filesList;
    private HashMap<String, File> filesDict = new HashMap<>(5);

    public FilesList() {
        filesList = new ListView();        
        Label label = new Label("Drag Files, Folders or double click to open browser");
        label.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        filesList.setPlaceholder(label);

        //Drag & drop handle
        filesList.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });
        filesList.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    ProcessFiles(db.getFiles());
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });

        //Double click handle
        filesList.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select FOM files file");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("FOM file", "*.xml"));
                List<File> files = fileChooser.showOpenMultipleDialog((Stage) this.getScene().getWindow());
                if (files != null) {
                    ProcessFiles(files);
                }
            }
        });

        filesList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                return new StringandButtonCell();
            }
        });

        this.getChildren().add(filesList);
    }

    public Collection<File> getFiles() {
        return filesDict.values();
    }

    public ObservableList<String> getFileNames() {
        return filesList.getItems();
    }

    
    private void ProcessFiles(List<File> files) {
        for (File file : files) {
            if (file.isDirectory()) {
                File[] subFiles = file.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        String extension = name.substring(name.lastIndexOf(".") + 1, name.length());
                        if ("xml".equalsIgnoreCase(extension)) {
                            return true;
                        }
                        return false;
                    }
                });
                for (File subFile : subFiles) {
                    addFile(subFile);
                }
            } else {
                addFile(file);
            }
        }
    }

    private void addFile(File file) {
        String name = file.getName();
        if (filesDict.containsKey(name)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Name conflict");
            alert.setHeaderText(null);
            alert.setContentText("File with the same name '" + name + "' has been added, \nthis file will not be added");
            alert.show();
            return;
        }
        String extension = name.substring(name.lastIndexOf(".") + 1, name.length());
        if ("xml".equalsIgnoreCase(extension)) {
            filesDict.put(file.getName(), file);
            filesList.getItems().add(file.getName());
        }
    }

    class StringandButtonCell extends ListCell<String> {

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                Button button = new Button();
                Image deleteImage = new Image(getClass().getResourceAsStream("/deleteButton.jpg"));
                button.setGraphic(new ImageView(deleteImage));
                button.setOnAction((event) -> {
                    filesDict.remove(item);
                    filesList.getItems().remove(item);
                });
                setText(item);
                HBox hbox = new HBox();
                hbox.getChildren().addAll(button);
                setContentDisplay(ContentDisplay.LEFT);
                setGraphic(hbox);

            } else {
                setGraphic(null);
                setText("");
            }
        }
    }
}
