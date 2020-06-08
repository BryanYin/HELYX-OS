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
package eu.engys.gui.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.Model;
import eu.engys.core.project.SolverState;
import eu.engys.util.ui.ExecUtil;

public class ElementSelector {

    private static final Logger logger = LoggerFactory.getLogger(ElementSelector.class);

    public static final String MESH = "网格";
    private static final String MESH_ELEMENT = "eu.engys.gui.mesh.MeshElement";

    private static final String CASE_SETUP_ELEMENT = "eu.engys.gui.casesetup.CaseSetupElement";
    public static final String FIELDS_INITIALISATION = "场初始化";

    private static final String SOLVER_ELEMENT = "eu.engys.gui.solver.SolverElement";
    public static final String RESIDUALS = "残差";

    private Model model;
    private View view;

    private Class<? extends ViewElement> currentTab;
    private String currentNode;

    public ElementSelector(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    public void selectDestinationAndGo() {
        if (model.hasProject()) {
            if (model.getSolverModel() != null && model.getSolverModel().getServerState() != null && model.getSolverModel().getServerState().getSolverState().isDoingSomething()) {
                SolverState solverState = model.getSolverModel().getServerState().getSolverState();
                if (solverState.isMeshing()) {
                    goToTabAndPanel(getMeshElementClass(), null);
                } else if (solverState.isInitialising()) {
                    goToFieldsInitialisation();
                } else if (solverState.isRunning()) {
                    goToResiduals();
                } else {
                    goToFirstElement();
                }
            } else {
                goToFirstElement();
            }
        }
    }

    public void saveLocation() {
        if (view.getMainPanel().getCurrentElement() != null) {
            this.currentTab = view.getMainPanel().getCurrentElement().getClass();
            this.currentNode = view.getMainPanel().getElement(currentTab).getPanel().getSelectedNode();
        } else {
            this.currentTab = null;
            this.currentNode = null;
        }
        logger.debug("Location saved: {} - {}", currentTab, currentNode);
    }

    public void goToLocation() {
        goToTabAndPanel(currentTab, currentNode);
    }

    public void goToFirstElement() {
        goToTabAndPanel((Class<? extends ViewElement>) null, null);
    }

    public void goToBoundaryMesh() {
        try {
            goToTabAndPanel(getMeshElementClass(), MESH);
            view.getMainPanel().getElement(getMeshElementClass()).getPanel().getNode(MESH).start();
        } catch (Exception e) {
            // TO avoid null pointer from HELYX-SAS
        }
    }

    private void goToFieldsInitialisation() {
        goToTabAndPanel(getCaseSetupElementClass(), FIELDS_INITIALISATION);
    }

    public void goToResiduals() {
        goToTabAndPanel(getSolverElementClass(), RESIDUALS);
    }

    private void goToTabAndPanel(final Class<? extends ViewElement> klass, final String panel) {
        ExecUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    view.getMainPanel().stop(null);
                    view.getCanvasPanel().stop(null);
                    view.getMainPanel().start(klass);
                    view.getCanvasPanel().start(klass);

                    if (panel != null) {
                        view.getMainPanel().getElement(klass).getPanel().selectNode(panel);
                    }

                    logger.debug("Location selected: {} - {}", klass, panel);
                } catch (Exception e) {
                    // TO avoid null pointer from HELYX-SAS
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    static Class<? extends ViewElement> getCaseSetupElementClass() {
        try {
            return (Class<? extends ViewElement>) Class.forName(CASE_SETUP_ELEMENT);
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    static Class<? extends ViewElement> getSolverElementClass() {
        try {
            return (Class<? extends ViewElement>) Class.forName(SOLVER_ELEMENT);
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    static Class<? extends ViewElement> getMeshElementClass() {
        try {
            return (Class<? extends ViewElement>) Class.forName(MESH_ELEMENT);
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

}
