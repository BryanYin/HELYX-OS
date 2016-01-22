/*--------------------------------*- Java -*---------------------------------*\
 |		 o                                                                   |                                                                                     
 |    o     o       | HelyxOS: The Open Source GUI for OpenFOAM              |
 |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 |    o     o       | http://www.engys.com                                   |
 |       o          |                                                        |
 |---------------------------------------------------------------------------|
 |	 License                                                                 |
 |   This file is part of HelyxOS.                                           |
 |                                                                           |
 |   HelyxOS is free software; you can redistribute it and/or modify it      |
 |   under the terms of the GNU General Public License as published by the   |
 |   Free Software Foundation; either version 2 of the License, or (at your  |
 |   option) any later version.                                              |
 |                                                                           |
 |   HelyxOS is distributed in the hope that it will be useful, but WITHOUT  |
 |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 |   for more details.                                                       |
 |                                                                           |
 |   You should have received a copy of the GNU General Public License       |
 |   along with HelyxOS; if not, write to the Free Software Foundation,      |
 |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
\*---------------------------------------------------------------------------*/

package eu.engys.core.project.state;

public enum SolverFamily {
    
    COUPLED("Coupled"),
    SIMPLE("Simple"),
    PISO("Piso"), 
    PIMPLE("Pimple"), 
    CENTRAL("Central"), 
    NONE("None");

    private String text;

    private SolverFamily(String text) {
        this.text = text;
    }
    
    public boolean isCoupled() {
        return this == COUPLED;
    }

    public boolean isSimple() {
        return this == SIMPLE;
    }

    public boolean isPimple() {
        return this == PIMPLE;
    }

    public boolean isPiso() {
        return this == PISO;
    }

    public boolean isCentral() {
        return this == CENTRAL;
    }

    public boolean isNone() {
        return this == NONE;
    }

    public String getKey() {
        return text.toUpperCase();
    }

    @Override
    public String toString() {
        return text;
    }
}
