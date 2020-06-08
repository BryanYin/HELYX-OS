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
package eu.engys.gui.mesh.panels.lines;

import static eu.engys.core.project.system.SnappyHexMeshDict.EXPANSION_RATIO_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.FINAL_LAYER_THICKNESS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.MIN_THICKNESS_KEY;
import static eu.engys.core.project.system.SnappyHexMeshDict.N_SURFACE_LAYERS_KEY;
import static eu.engys.util.ui.ComponentsFactory.stringField;

import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.project.geometry.surface.PlaneRegion;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.StringField;

public class BoundingBoxFacesPanel {

    public static final String NUMBER_OF_LAYERS_LABEL = "层数";
    public static final String LAYER_STRETCHING_LABEL = "分层拉伸";
    public static final String FINAL_LAYER_THICKNESS_LABEL = "最终层厚度";
    private static final String LAYER_MIN_THICKNESS_LABEL = "层最小厚度";
    public static final String FACE_NAME_LABEL = "面名称";
    public static final String BOUNDING_BOX_FACES_LABEL = "边界面";

    private PanelBuilder planeBuilder;
    private DictionaryModel planeModel;
    private StringField planeName;
    private PropertyChangeListener listener;

    public BoundingBoxFacesPanel(PropertyChangeListener listener) {
        this.listener = listener;
        this.planeModel = new DictionaryModel();
        this.planeBuilder = new PanelBuilder();
        layoutComponents();
        addNameListener();
    }

    private void layoutComponents() {
        planeModel = new DictionaryModel();

        planeBuilder = new DictionaryPanelBuilder();
        planeBuilder.addComponent(FACE_NAME_LABEL, planeName = stringField());
        planeBuilder.addComponent(NUMBER_OF_LAYERS_LABEL, planeModel.bindIntegerPositive(N_SURFACE_LAYERS_KEY));
        planeBuilder.addComponent(FINAL_LAYER_THICKNESS_LABEL, planeModel.bindDouble(FINAL_LAYER_THICKNESS_KEY, (Double) null));
        planeBuilder.addComponent(LAYER_STRETCHING_LABEL, planeModel.bindDouble(EXPANSION_RATIO_KEY, (Double) null));
        planeBuilder.addComponent(LAYER_MIN_THICKNESS_LABEL, planeModel.bindDouble(MIN_THICKNESS_KEY, (Double) null));
        planeBuilder.setEnabled(false);
    }

    public JPanel getPanel() {
        JPanel panel = planeBuilder.getPanel();
        panel.setBorder(BorderFactory.createTitledBorder(BOUNDING_BOX_FACES_LABEL));
        panel.setName("plane.panel");
        return panel;
    }

    public void save(PlaneRegion... planes) {
        for (PlaneRegion plane : planes) {
            plane.setLayerDictionary(new Dictionary(planeModel.getDictionary()));
        }
    }

    public void selectPlane(PlaneRegion[] selection) {
        setEnabled(true);
        
        PlaneRegion plane = selection[0];
        planeModel.setDictionary(plane.getLayerDictionary());
        setPlaneName(plane.getName());
    }

    public void setEnabled(boolean enabled) {
        planeName.setEnabled(enabled);
        planeBuilder.setEnabled(enabled);
    }

    public void disableNameField() {
        planeName.setEnabled(false);
    }

    public void setPlaneName(String name) {
        removeNameListener();
        planeName.setValue(name);
        addNameListener();
    }

    public String getPlaneName() {
        return planeName.getText();
    }

    private void addNameListener() {
        planeName.addPropertyChangeListener(listener);
    }

    private void removeNameListener() {
        planeName.removePropertyChangeListener(listener);
    }

}
