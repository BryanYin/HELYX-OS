/*******************************************************************************
 *  |       o                                                                   |
 *  |    o     o       | HELYX-OS: The Open Source GUI for OpenFOAM             |
 *  |   o   O   o      | Copyright (C) 2012-2016 ENGYS                          |
 *  |    o     o       | http://www.engys.com                                   |
 *  |       o          |                                                        |
 *  |---------------------------------------------------------------------------|
 *  |   License                                                                 |
 *  |   This file is part of HELYX-OS.                                          |
 *  |                                                                           |
 *  |   HELYX-OS is free software; you can redistribute it and/or modify it     |
 *  |   under the terms of the GNU General Public License as published by the   |
 *  |   Free Software Foundation; either version 2 of the License, or (at your  |
 *  |   option) any later version.                                              |
 *  |                                                                           |
 *  |   HELYX-OS is distributed in the hope that it will be useful, but WITHOUT |
 *  |   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   |
 *  |   FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   |
 *  |   for more details.                                                       |
 *  |                                                                           |
 *  |   You should have received a copy of the GNU General Public License       |
 *  |   along with HELYX-OS; if not, write to the Free Software Foundation,     |
 *  |   Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA            |
 *******************************************************************************/
package eu.engys.gui.casesetup.solver.panels;

import static eu.engys.core.project.system.FvSolution.RELAXATION_FACTORS_KEY;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.SolverFamily;

public class CoupledSettingsPanel implements SolverPanel {

    private DictionaryModel coupledDictModel;
    private JPanel mainPanel;

    public CoupledSettingsPanel() {
        coupledDictModel = new DictionaryModel(new Dictionary(getKey()));
        
        mainPanel = new JPanel(new BorderLayout());
    }

    @Override
    public String getKey() {
        return SolverFamily.COUPLED.key();
    }

    @Override
    public JPanel getPanel() {
        return mainPanel;
    }

    @Override
    public Dictionary getSolverDictionary() {
        return coupledDictModel.getDictionary();
    }

    @Override
    public Dictionary getRelaxationFactorsDictionary() {
        return new Dictionary(RELAXATION_FACTORS_KEY);
    }

    @Override
    public Dictionary getResidualControlDictionary() {
        return null;
    }

    @Override
    public void load(Model model) {
    }

}
