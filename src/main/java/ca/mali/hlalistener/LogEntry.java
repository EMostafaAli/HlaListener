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
package ca.mali.hlalistener;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author Mostafa
 */
public class LogEntry {

    private Image iconImage;

    private final ReadOnlyIntegerWrapper logID = new ReadOnlyIntegerWrapper();
    private final StringProperty sectionNo;
    private final StringProperty title;
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty simulationTime = new SimpleStringProperty("NA");
    private final ObjectProperty<LogEntryType> logType = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> icon = new SimpleObjectProperty<>();
    private final ObjectProperty<Exception> exception = new SimpleObjectProperty<>();
    private final ReadOnlyStringWrapper stackTrace = new ReadOnlyStringWrapper("");
    private String tooltipText = "";

    static int id = 0;
    private final ObservableList<ClassValuePair> suppliedArguments = FXCollections.observableArrayList();
    private final ObservableList<ClassValuePair> returnedArguments = FXCollections.observableArrayList();

    public ObservableList<ClassValuePair> getSuppliedArguments() {
        return suppliedArguments;
    }

    public ObservableList<ClassValuePair> getReturnedArguments() {
        return returnedArguments;
    }

    public boolean getSuppliedArgumentsIsNotEmpty() {
        return !suppliedArguments.isEmpty();
    }

    public boolean getReturnedArguementsIsNotEmpty() {
        return !returnedArguments.isEmpty();
    }

    public LogEntry(String sectionNo, String title) {
        logID.set(++id);
        this.sectionNo = new SimpleStringProperty(sectionNo);
        this.title = new SimpleStringProperty(title);
        logType.addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case REQUEST:
                    iconImage = new Image(getClass().getResourceAsStream("/icons/green.png"));
                    icon.set(iconImage);
                    tooltipText = "Request";
                    break;
                case CALLBACK:
                    iconImage = new Image(getClass().getResourceAsStream("/icons/blue.png"));
                    icon.set(iconImage);
                    tooltipText = "Callback";
                    break;
                case ERROR:
                    iconImage = new Image(getClass().getResourceAsStream("/icons/yellow.png"));
                    icon.set(iconImage);
                    tooltipText = "Error";
                    break;
                case FATAL:
                    iconImage = new Image(getClass().getResourceAsStream("/icons/red.png"));
                    icon.set(iconImage);
                    tooltipText = "Fatal";
                    break;
                default:
                    break;
            }
        });

        exception.addListener((observable, oldValue, newValue) -> {
            stackTrace.set(getStackTrace(newValue));
        });
    }

    private static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public int getLogID() {
        return logID.get();
    }

    public ReadOnlyIntegerProperty logIDProperty() {
        return logID.getReadOnlyProperty();
    }

    public String getSectionNo() {
        return sectionNo.get();
    }

    public void setSectionNo(String value) {
        sectionNo.set(value);
    }

    public StringProperty sectionNoProperty() {
        return sectionNo;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String value) {
        title.set(value);
    }

    public StringProperty TitleProperty() {
        return title;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String value) {
        description.set(value);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getSimulationTime() {
        return simulationTime.get();
    }

    public void setSimulationTime(String value) {
        simulationTime.set(value);
    }

    public StringProperty simulationTimeProperty() {
        return simulationTime;
    }

    public LogEntryType getLogType() {
        return logType.get();
    }

    public void setLogType(LogEntryType value) {
        logType.set(value);
    }

    public ObjectProperty logTypeProperty() {
        return logType;
    }

    public Image getIcon() {
        return icon.get();
    }

    public void setIcon(Image value) {
        icon.set(value);
    }

    public ObjectProperty stringProperty() {
        return icon;
    }

    public Exception getException() {
        return exception.get();
    }

    public void setException(Exception value) {
        setDescription(value.getClass().getCanonicalName());
        exception.set(value);
    }

    public ObjectProperty exceptionProperty() {
        return exception;
    }

    public String getStackTrace() {
        return stackTrace.get();
    }

    public ReadOnlyStringProperty stackTraceProperty() {
        return stackTrace.getReadOnlyProperty();
    }

    public String getTooltipText() {
        return tooltipText;
    }
}
