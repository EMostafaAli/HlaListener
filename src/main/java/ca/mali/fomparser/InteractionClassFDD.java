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
package ca.mali.fomparser;

import hla.rti1516e.InteractionClassHandle;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mostafa
 */
public class InteractionClassFDD {

    private final String name;
    private String fullName;
    private List<ParameterFDD> parameters;
    private final InteractionClassFDD parent;
    private InteractionClassHandle handle;

    public InteractionClassFDD(String name, InteractionClassFDD parent) {
        this.name = name;
        this.parent = parent;
        this.fullName = this.name;

        if (this.parent != null) {
            parameters = new ArrayList<>(this.parent.getParameters());
            fullName = this.parent.fullName + "." + this.name;
        }
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public InteractionClassFDD getParent() {
        return parent;
    }

    public List<ParameterFDD> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        return parameters;
    }

    public InteractionClassHandle getHandle() {
        return handle;
    }

    public void setHandle(InteractionClassHandle handle) {
        this.handle = handle;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
