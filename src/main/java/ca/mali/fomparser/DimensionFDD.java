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

package ca.mali.fomparser;

import hla.rti1516e.DimensionHandle;
import javafx.beans.property.*;

/**
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class DimensionFDD {

    private final ReadOnlyStringWrapper name;
    private final DimensionHandle handle;
    public StringProperty DataType = new SimpleStringProperty();
    public StringProperty Normalization = new SimpleStringProperty();
    public IntegerProperty UpperBound = new SimpleIntegerProperty();

    public DimensionFDD(String name, DimensionHandle handle) {
        this.name = new ReadOnlyStringWrapper(name);
        this.handle = handle;
    }

    public String getDataType() {
        return DataType.get();
    }

    public StringProperty dataTypeProperty() {
        return DataType;
    }

    public void setDataType(String dataType) {
        this.DataType.set(dataType);
    }

    public String getNormalization() {
        return Normalization.get();
    }

    public StringProperty normalizationProperty() {
        return Normalization;
    }

    public void setNormalization(String normalization) {
        this.Normalization.set(normalization);
    }

    public int getUpperBound() {
        return UpperBound.get();
    }

    public IntegerProperty upperBoundProperty() {
        return UpperBound;
    }

    public void setUpperBound(int upperBound) {
        this.UpperBound.set(upperBound);
    }

    public String getName() {
        return name.get();
    }

    public ReadOnlyStringWrapper nameProperty() {
        return name;
    }

    public DimensionHandle getHandle() {
        return handle;
    }
}
