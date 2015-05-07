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

import javafx.beans.property.*;

/**
 *
 * @author Mostafa
 */
public class LogEntry {

    private final FloatProperty sectionNo ;
    private final StringProperty title;
    private final StringProperty simulationTime = new SimpleStringProperty();

    public LogEntry(float sectionNo, String title) {
        this.sectionNo = new SimpleFloatProperty(sectionNo);
        this.title = new SimpleStringProperty(title);
    }

    
    public float getSectionNo() {
        return sectionNo.get();
    }

    public void setSectionNo(float value) {
        sectionNo.set(value);
    }

    public FloatProperty sectionNoProperty() {
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

    public String getSimulationTime() {
        return simulationTime.get();
    }

    public void setSimulationTime(String value) {
        simulationTime.set(value);
    }

    public StringProperty simulationTimeProperty() {
        return simulationTime;
    }

}
