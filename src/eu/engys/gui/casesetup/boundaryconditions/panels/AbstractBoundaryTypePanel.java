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


package eu.engys.gui.casesetup.boundaryconditions.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.modules.boundaryconditions.ParametersPanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.state.State;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.core.project.zero.patches.Patch;

public abstract class AbstractBoundaryTypePanel extends JPanel implements BoundaryTypePanel {

    private static final Logger logger = LoggerFactory.getLogger(BoundaryTypePanel.class);

    private ParametersPanel momentumPanel;
    private ParametersPanel turbulencePanel;
    private ParametersPanel thermalPanel;
    private ParametersPanel pScalarsPanel;
    private ParametersPanel phasePanel;

    private Map<String, Integer> indexes = new HashMap<>();
    private Map<String, JPanel> components = new HashMap<>();
    private Map<String, ParametersPanel> parametersPanels = new HashMap<>();

    private JTabbedPane tabPanel;

    protected final Model model;

    public AbstractBoundaryTypePanel(Model model) {
        super(new BorderLayout());
        this.model = model;
    }

    @Override
    public void layoutPanel() {
        tabPanel = new JTabbedPane();
        tabPanel.setName("boundary.conditions.tab");
        tabPanel.putClientProperty("Synthetica.tabbedPane.tabIndex", 0);
        tabPanel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                tabChanged();
            }
        });
        add(tabPanel, BorderLayout.CENTER);
        setName(getClass().getSimpleName());
    }

    @Override
    public void addMomentumPanel(ParametersPanel momentumPanel) {
        if (tabPanel.indexOfTab(MOMENTUM) < 0) {
            this.momentumPanel = momentumPanel;
            components.put(MOMENTUM, momentumPanel.getComponent());
        }
    }

    @Override
    public void addTurbulencePanel(ParametersPanel turbulencePanel) {
        if (tabPanel.indexOfTab(TURBULENCE) < 0) {
            this.turbulencePanel = turbulencePanel;
            components.put(TURBULENCE, turbulencePanel.getComponent());
        }
    }

    @Override
    public void addThermalPanel(ParametersPanel thermalPanel) {
        if (tabPanel.indexOfTab(THERMAL) < 0) {
            this.thermalPanel = thermalPanel;
            components.put(THERMAL, thermalPanel.getComponent());
        }
    }

    protected void addPScalarsPanel(ParametersPanel pScalarsPanel) {
        if (tabPanel.indexOfTab(PASSIVE_SCALARS) < 0) {
            this.pScalarsPanel = pScalarsPanel;
            components.put(PASSIVE_SCALARS, pScalarsPanel.getComponent());
        }
    }
    
    protected void addPhasePanel(ParametersPanel phasePanel) {
        if (tabPanel.indexOfTab(PHASE_FRACTION) < 0) {
            this.phasePanel = phasePanel;
            components.put(PHASE_FRACTION, phasePanel.getComponent());
        }
    }
    
    @Override
    public void addPanel(String name, ParametersPanel pPanel, int index) {
        if (tabPanel.indexOfTab(name) < 0) {
            components.put(name, pPanel.getComponent());
            if (index >= 0)
                indexes.put(name, index);
            parametersPanels.put(name, pPanel);
        }
    }

    @Override
    public void addPanel(String name, ParametersPanel pPanel) {
        addPanel(name, pPanel, -1);
    }


    private void tabChanged() {
        int selectedIndex = tabPanel.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }
        String title = tabPanel.getTitleAt(selectedIndex);
        if (model == null || model.getState() == null) {
            return;
        }
        if (title.equals(MOMENTUM)) {
            momentumPanel.tabChanged(model);
        } else if (title.equals(THERMAL)) {
            thermalPanel.tabChanged(model);
        } else if (title.equals(TURBULENCE)) {
            turbulencePanel.tabChanged(model);
        } else if (title.equals(PASSIVE_SCALARS)) {
            pScalarsPanel.tabChanged(model);
        } else if (title.equals(PHASE_FRACTION)) {
            phasePanel.tabChanged(model);
        } else {
            if (parametersPanels.containsKey(title)) {
                parametersPanels.get(title).tabChanged(model);
            }
        }
    }

    @Override
    public void stateChanged() {
        State state = model.getState();
        setEnabledAt(MOMENTUM, !state.getMultiphaseModel().isMultiphase());
        setEnabledAt(TURBULENCE, (state.getTurbulenceModel() != null && !state.getTurbulenceModel().getType().isLaminar()));
        setEnabledAt(THERMAL, state.isEnergy());
        setEnabledAt(PHASE_FRACTION, state.getMultiphaseModel().isMultiphase());

        for (String title : parametersPanels.keySet()) {
            setEnabledAt(title, parametersPanels.get(title).isEnabled(model));
        }

        if (isEnabledAt(MOMENTUM))
            momentumPanel.stateChanged(model);
        if (isEnabledAt(THERMAL))
            thermalPanel.stateChanged(model);
        if (isEnabledAt(TURBULENCE))
            turbulencePanel.stateChanged(model);
        if (isEnabledAt(PASSIVE_SCALARS))
            pScalarsPanel.stateChanged(model);
        if (isEnabledAt(PHASE_FRACTION))
            phasePanel.stateChanged(model);

        for (String title : parametersPanels.keySet()) {
            if (isEnabledAt(title))
                parametersPanels.get(title).stateChanged(model);
        }
    }

    @Override
    public void resetToDefault() {
    	if (isEnabledAt(MOMENTUM))
    		momentumPanel.resetToDefault(model);
    	if (isEnabledAt(THERMAL))
    		thermalPanel.resetToDefault(model);
    	if (isEnabledAt(TURBULENCE))
    		turbulencePanel.resetToDefault(model);
    	if (isEnabledAt(PASSIVE_SCALARS))
    		pScalarsPanel.resetToDefault(model);
    	if (isEnabledAt(PHASE_FRACTION))
    		phasePanel.resetToDefault(model);
    	
    	for (String title : parametersPanels.keySet()) {
    		if (isEnabledAt(title))
    			parametersPanels.get(title).resetToDefault(model);
    	}
    }

    @Override
    public void materialsChanged() {
        if (isEnabledAt(MOMENTUM))
            momentumPanel.materialsChanged(model);
        if (isEnabledAt(PHASE_FRACTION))
            phasePanel.materialsChanged(model);

        for (String title : parametersPanels.keySet()) {
            if (isEnabledAt(title)) {
                parametersPanels.get(title).materialsChanged(model);
            }
        }
    }

    private void setEnabledAt(String name, boolean enable) {
        // System.out.println("AbstractBoundaryTypePanel.setEnabledAt() "+name+" is "+
        // (enable? "ENABLED" : "DISABLED"));
        int index = tabPanel.indexOfTab(name);
        if (enable && index < 0 && components.containsKey(name)) {
            if (indexes.containsKey(name)) {
                tabPanel.insertTab(name, null, components.get(name), null, indexes.get(name));
            } else {
                tabPanel.addTab(name, components.get(name));
            }
        } else if (!enable && index >= 0) {
            tabPanel.removeTabAt(index);
        }
    }

    private boolean isEnabledAt(String name) {
        int index = tabPanel.indexOfTab(name);
        return index >= 0;
    }

    @Override
    public void loadFromPatches(Patch... patches) {
        BoundaryConditions bc = patches[0].getBoundaryConditions();
        String patchName = patches[0].getName();
        if (bc != null) {
            boolean multipleSelection = patches.length > 1;
            // System.out.println("AbstractBoundaryTypePanel.loadFromPatches() multipleSelection: "+multipleSelection);
            if (momentumPanel != null && isEnabledAt(MOMENTUM)) {
                momentumPanel.setMultipleEditing(multipleSelection);
                momentumPanel.loadFromBoundaryConditions(patchName, bc);
            }
            if (turbulencePanel != null && isEnabledAt(TURBULENCE)) {
                turbulencePanel.setMultipleEditing(multipleSelection);
                turbulencePanel.loadFromBoundaryConditions(patchName, bc);
            }
            if (thermalPanel != null && isEnabledAt(THERMAL)) {
                thermalPanel.setMultipleEditing(multipleSelection);
                thermalPanel.loadFromBoundaryConditions(patchName, bc);
            }
            if (pScalarsPanel != null && isEnabledAt(PASSIVE_SCALARS)) {
                pScalarsPanel.setMultipleEditing(multipleSelection);
                pScalarsPanel.loadFromBoundaryConditions(patchName, bc);
            }
            if (phasePanel != null && isEnabledAt(PHASE_FRACTION)) {
                phasePanel.setMultipleEditing(multipleSelection);
                phasePanel.loadFromBoundaryConditions(patchName, bc);
            }
            for (String title : parametersPanels.keySet()) {
                if (isEnabledAt(title)) {
                    ParametersPanel parametersPanel = parametersPanels.get(title);
                    parametersPanel.setMultipleEditing(multipleSelection);
                    parametersPanel.loadFromBoundaryConditions(patchName, bc);
                }
            }
        } else {
            logger.warn("BoundaryConditions are null");
        }
    }

    @Override
    public void saveToPatch(Patch patch) {
        if (patch.getBoundaryConditions() == null) {
            patch.setBoundaryConditions(new BoundaryConditions());
        }
        BoundaryConditions bc = patch.getBoundaryConditions();
        String patchName = patch.getName();
        if (momentumPanel != null && isEnabledAt(MOMENTUM) && momentumPanel.canEdit())
            momentumPanel.saveToBoundaryConditions(patchName, bc);
        if (turbulencePanel != null && isEnabledAt(TURBULENCE) && turbulencePanel.canEdit())
            turbulencePanel.saveToBoundaryConditions(patchName, bc);
        if (thermalPanel != null && isEnabledAt(THERMAL) && thermalPanel.canEdit())
            thermalPanel.saveToBoundaryConditions(patchName, bc);
        if (pScalarsPanel != null && isEnabledAt(PASSIVE_SCALARS) && pScalarsPanel.canEdit())
            pScalarsPanel.saveToBoundaryConditions(patchName, bc);
        if (phasePanel != null && isEnabledAt(PHASE_FRACTION) && phasePanel.canEdit())
            phasePanel.saveToBoundaryConditions(patchName, bc);
        for (String title : parametersPanels.keySet()) {
            if (isEnabledAt(title)) {
                parametersPanels.get(title).saveToBoundaryConditions(patchName, bc);
            }
        }
    }

    @Override
    public Component getPanel() {
        return this;
    }

    @Override
    public ParametersPanel getMomentumPanel() {
        return momentumPanel;
    }

    @Override
    public ParametersPanel getTurbulencePanel() {
        return turbulencePanel;
    }

    @Override
    public ParametersPanel getThermalPanel() {
        return thermalPanel;
    }

    @Override
    public ParametersPanel getPanel(String name) {
        return parametersPanels.get(name);
    }
    
    public ParametersPanel getPhasePanel() {
        return phasePanel;
    }

}
