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
package eu.engys.gui.mesh.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.engys.core.controller.Controller;
import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.presentation.Action;
import eu.engys.core.presentation.ActionContainer;
import eu.engys.core.presentation.ActionManager;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.Surface;
import eu.engys.core.project.geometry.surface.Stl;
import eu.engys.gui.AbstractGUIPanel;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.AddSurfaceEvent;
import eu.engys.gui.events.view3D.ChangeSurfaceEvent;
import eu.engys.gui.events.view3D.RenameSurfaceEvent;
import eu.engys.gui.mesh.GeometryPanel;
import eu.engys.gui.mesh.actions.AddSTL;
import eu.engys.gui.tree.TreeNodeManager;
import eu.engys.util.Util;
import eu.engys.util.progress.ProgressMonitor;
import eu.engys.util.ui.UiUtil;
import eu.engys.util.ui.builder.JComboBoxController;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.IntegerField;

public abstract class AbstractGeometryPanel extends AbstractGUIPanel implements GeometryPanel, ActionContainer {

    public static final String ZONES_LABEL = "区域";
    public static final String LAYERS_LABEL = "层";
    public static final String REFINEMENT_LABEL = "微调";
    public static final String GEOMETRY = "几何体";

    public static final String DISTANCE_M_LABEL = "距离 [m]";

    public static final String SURFACE_LABEL = "表面";
    public static final String BAFFLE_LABEL = "阻隔";
    public static final String BOUNDARY_LABEL = "边界";
    public static final String INTERNAL_LABEL = "内部";
    public static final String TYPE_LABEL = "类型";
    public static final String NAME_LABEL = "名称";
    public static final String CELL_ZONE_LABEL = "网格区域";
    public static final String LEVEL_LABEL = "级别";
    public static final String PROXIMITY_REFINEMENT_LABEL = "贴体微调";
    public static final String FINAL_LAYER_THICKNESS_LABEL = "最终层厚度";
    public static final String LAYER_STRETCHING_LABEL = "层拉伸";
    public static final String NUMBER_OF_LAYERS_LABEL = "层数";
    public static final String CELL_SIZE_LABEL = "网格大小 [m]";

    public static final String INSIDE_LEVEL_LABEL = "内部级别";
    public static final String OUTSIDE_LEVEL_LABEL = "外部级别";
    public static final String DISTANCE_LEVEL_LABEL = "距离级别";

    public static final String MODE_LABEL = "模式";
    public static final String NONE = "无";
    public static final String INSIDE = "内部";
    public static final String OUTSIDE = "外部";
    public static final String DISTANCE = "距离";
    public static final String NONE_LABEL = "无";
    public static final String INSIDE_LABEL = "内部";
    public static final String OUTSIDE_LABEL = "外部";
    public static final String DISTANCE_LABEL = "距离";

    protected DictionaryModel surfaceModel;
    protected DictionaryModel volumeModel;
    protected DictionaryModel layerModel;
    protected DictionaryModel zoneModel;

    private ShapesPanel shapesPanel;

    protected PanelBuilder layersBuilder;
    protected PanelBuilder surfaceBuilder;
    protected PanelBuilder volumesBuilder;
    protected PanelBuilder zonesBuilder;

    private JTabbedPane tabbedPane;

    protected final GeometryTreeNodeManager treeNodeManager;
    private Controller controller;
    private ProgressMonitor monitor;

    public AbstractGeometryPanel(Model model, Controller controller, ProgressMonitor monitor) {
        super(GEOMETRY, model);
        this.controller = controller;
        this.monitor = monitor;
        this.treeNodeManager = new GeometryTreeNodeManager(model, controller, this, getGeometryActions(controller));
        model.addObserver(treeNodeManager);
        ActionManager.getInstance().parseActions(this);
    }

    protected abstract DefaultGeometryActions getGeometryActions(Controller controller);

    public abstract JButton[] getShapeButtons();

    @Override
    protected JComponent layoutComponents() {
        surfaceModel = new DictionaryModel();
        volumeModel = new DictionaryModel();
        layerModel = new DictionaryModel();
        zoneModel = new DictionaryModel();

        shapesPanel = new ShapesPanel(this);

        tabbedPane = new JTabbedPane();
        tabbedPane.setName("geometry.tabbed.pane");
        tabbedPane.putClientProperty("Synthetica.tabbedPane.tabIndex", 0);

        JPanel refinemetPanel = getRefinemetPanel();
        JPanel layersPanel = getLayersPanel();
        JPanel zonesPanel = getZonesPanel();

        refinemetPanel.setName("refinement.panel");
        layersPanel.setName("layers.panel");
        zonesPanel.setName("zones.panel");

        tabbedPane.addTab(REFINEMENT_LABEL, refinemetPanel);
        tabbedPane.addTab(LAYERS_LABEL, layersPanel);
        tabbedPane.addTab(ZONES_LABEL, zonesPanel);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(shapesPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                saveSurfaces(treeNodeManager.getSelectedValues());
                selectSurface(treeNodeManager.getSelectedValues());
            }
        });

        return mainPanel;
    }

    private JPanel getRefinemetPanel() {
        PanelBuilder builder = new PanelBuilder();

        JPanel surfacesPanel = getSurfacesPanel();
        JPanel volumesPanel = getVolumesPanel();

        surfacesPanel.setName("refinement.surfaces");
        volumesPanel.setName("refinement.volumes");

        builder.addComponent(surfacesPanel);
        builder.addComponent(volumesPanel);
        return builder.getPanel();
    }

    protected abstract JPanel getSurfacesPanel();

    protected JPanel getVolumesPanel() {
        volumesBuilder = new PanelBuilder();
        final JComboBoxController comboBoxController = volumeModel.bindComboBoxController("mode");
        volumesBuilder.startChoice(MODE_LABEL, comboBoxController);

        volumesBuilder.startGroup(NONE, NONE_LABEL);
        volumesBuilder.endGroup();

        volumesBuilder.startGroup(INSIDE, INSIDE_LABEL);
        IntegerField inside = volumeModel.bindIntegerLevels("levels", INSIDE);
        volumesBuilder.addComponent(INSIDE_LEVEL_LABEL, inside);
        volumesBuilder.addComponent(CELL_SIZE_LABEL, new Size(model, inside));
        volumesBuilder.endGroup();

        volumesBuilder.startGroup(OUTSIDE, OUTSIDE_LABEL);
        IntegerField outside = volumeModel.bindIntegerLevels("levels", OUTSIDE);
        volumesBuilder.addComponent(OUTSIDE_LEVEL_LABEL, outside);
        volumesBuilder.addComponent(CELL_SIZE_LABEL, new Size(model, outside));
        volumesBuilder.endGroup();

        volumesBuilder.startGroup(DISTANCE, DISTANCE_LABEL);
        String[] columnNames = { DISTANCE_M_LABEL, LEVEL_LABEL };
        Class<?>[] type = { Double.class, Integer.class };
        volumesBuilder.addComponent(DISTANCE_LEVEL_LABEL, volumeModel.bindTableLevels(columnNames, type));
        volumesBuilder.endGroup();

        volumesBuilder.endChoice();

        comboBoxController.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean distanceRefinementOrNone = isDistanceRefinementOrNone(comboBoxController.getSelectedKey());
                surfaceBuilder.setEnabled(distanceRefinementOrNone || isCellZone());
                layersBuilder.setEnabled(distanceRefinementOrNone || isCellZone());
            }
        });
        volumesBuilder.getPanel().setBorder(BorderFactory.createTitledBorder("容量"));
        return volumesBuilder.getPanel();
    }

    protected boolean isCellZone() {
        return false;
    }

    protected JPanel getLayersPanel() {
        return new JPanel();
    }

    protected JPanel getZonesPanel() {
        return new JPanel();
    }

    @Override
    public TreeNodeManager getTreeNodeManager() {
        return treeNodeManager;
    }

    @Override
    public void save() {
        super.save();
        treeNodeManager.getSelectionHandler().handleSelection(false, (Object[]) treeNodeManager.getSelectedValues());
        model.getGeometry().saveGeometry(model);
    }

    @Override
    public void stop() {
        super.stop();
        shapesPanel.stop();
    }

    public void saveSurfaces(Surface... surfaces) {
        Surface delegate = shapesPanel.getSelectedSurface();
        if (delegate != null) {
            delegate.setSurfaceDictionary(surfaceModel.getDictionary());
            delegate.setVolumeDictionary(volumeModel.getDictionary());
            delegate.setLayerDictionary(layerModel.getDictionary());
            delegate.setZoneDictionary(zoneModel.getDictionary());

            new SurfacesSaver().saveSurfaces(delegate, surfaces);
        }
    }

    @Override
    public void changeSurface() {
        Surface[] selection = treeNodeManager.getSelectedValues();
        if (selection.length == 1) {
            Surface surface = selection[0];
            saveSurfaces(surface);
            EventManager.triggerEvent(this, new ChangeSurfaceEvent(surface, false));
        }
    }

    @Override
    public void renameSurface(String newName) {
        Surface[] selection = treeNodeManager.getSelectedValues();
        if (selection.length == 1) {
            Surface surface = selection[0];

            if (model.getGeometry().contains(newName)) {
                JOptionPane.showMessageDialog(UiUtil.getActiveWindow(), "Name already in use", "Name Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String oldPatchName = surface.getPatchName();
            surface.rename(newName);

            saveSurfaces(surface);

            treeNodeManager.refreshNode(surface);

            EventManager.triggerEvent(this, new RenameSurfaceEvent(surface, oldPatchName, surface.getPatchName()));
        }
    }

    @Override
    public void clear() {
        treeNodeManager.getSelectionHandler().handleSelection(false, (Object[]) new Surface[0]);
    }

    @Action(key = "mesh.stl")
    public void addSTL() {
        new AddSTL(model, monitor) {
            @Override
            public void postLoad(List<Stl> stls) {
                addSTL(stls.toArray(new Stl[0]));
            }
        }.execute();
    }

    public void addSTL(Stl... stls) {
        if (Util.isVarArgsNotNull(stls)) {
            for (Stl stl : stls) {
                String name = Util.replaceForbiddenCharacters(stl.getName());
                String newName = getModel().getGeometry().getAName(name);
                stl.rename(newName);
                getModel().getGeometry().addSurface(stl);
                getModel().geometryChanged(stl);
            }
            EventManager.triggerEvent(this, new AddSurfaceEvent(stls));
        }
    }

    @Action(key = "mesh.box")
    public void addBox() {
        treeNodeManager.clear();
        Surface box = model.getGeometry().getABox();

        getModel().getGeometry().addSurface(box);
        getModel().geometryChanged(box);

        EventManager.triggerEvent(this, new AddSurfaceEvent(box));
    }

    @Action(key = "mesh.cylinder")
    public void addCylinder() {
        treeNodeManager.clear();
        Surface cyl = model.getGeometry().getACylinder();

        getModel().getGeometry().addSurface(cyl);
        getModel().geometryChanged(cyl);

        EventManager.triggerEvent(this, new AddSurfaceEvent(cyl));
    }

    @Action(key = "mesh.sphere")
    public void addSphere() {
        treeNodeManager.clear();
        Surface sphere = model.getGeometry().getASphere();

        getModel().getGeometry().addSurface(sphere);
        getModel().geometryChanged(sphere);

        EventManager.triggerEvent(this, new AddSurfaceEvent(sphere));
    }

    @Action(key = "mesh.plane")
    public void addPlane() {
        treeNodeManager.clear();
        Surface plane = model.getGeometry().getAPlane();

        getModel().getGeometry().addSurface(plane);
        getModel().geometryChanged(plane);

        EventManager.triggerEvent(this, new AddSurfaceEvent(plane));
    }

    @Action(key = "mesh.ring")
    public void addRing() {
        treeNodeManager.clear();
        Surface ring = model.getGeometry().getARing();

        getModel().getGeometry().addSurface(ring);
        getModel().geometryChanged(ring);

        EventManager.triggerEvent(this, new AddSurfaceEvent(ring));
    }

    private boolean hasDistanceRefinementOrNone(Surface surface) {
        Dictionary volumeDictionary = surface.getVolumeDictionary();
        if (volumeDictionary.found("mode")) {
            String mode = volumeDictionary.lookup("mode");
            return isDistanceRefinementOrNone(mode);
        }
        return true;
    }

    private boolean isDistanceRefinementOrNone(String mode) {
        return mode == null || "distance".equals(mode) || "none".equals(mode);
    }

    public void selectSurface(Surface[] surfaces) {
        if (Util.isVarArgsNotNull(surfaces)) {
            shapesPanel.showPanel(surfaces);

            updateGUIOnSelection(surfaces[0]);

            selectATab();

            Dictionary surfaceDictionary = surfaces[0].getSurfaceDictionary();
            Dictionary volumeDictionary = surfaces[0].getVolumeDictionary();
            Dictionary layerDictionary = surfaces[0].getLayerDictionary();
            Dictionary zoneDictionary = surfaces[0].getZoneDictionary();

            // System.out.println("DefaultGeometryPanel.selectSurface() "+surfaceDictionary+volumeDictionary+layerDictionary);

            surfaceModel.setDictionary(new Dictionary(surfaceDictionary));
            volumeModel.setDictionary(new Dictionary(volumeDictionary));
            layerModel.setDictionary(new Dictionary(layerDictionary));
            zoneModel.setDictionary(new Dictionary(zoneDictionary));
        } else {
            deselectAll();
        }
    }

    public void deselectAll() {
        shapesPanel.showPanel(null);

        surfaceModel.setDictionary(new Dictionary(""));
        volumeModel.setDictionary(new Dictionary(""));
        layerModel.setDictionary(new Dictionary(""));
        zoneModel.setDictionary(new Dictionary(""));

        updateGUIOnSelection(null);
    }

    protected void updateGUIOnSelection(Surface surface) {
        if (surface == null) {
            surfaceBuilder.setEnabled(false);
            volumesBuilder.setEnabled(false);
            layersBuilder.setEnabled(false);
            zonesBuilder.setEnabled(false);
        } else {
            surfaceBuilder.setEnabled(true);
            volumesBuilder.setEnabled(true);
            layersBuilder.setEnabled(true);
            zonesBuilder.setEnabled(true);

            surfaceBuilder.setEnabled(surface.hasSurfaceRefinement() && hasDistanceRefinementOrNone(surface));
            volumesBuilder.setEnabled(surface.hasVolumeRefinement());
            layersBuilder.setEnabled(surface.hasLayers());
            zonesBuilder.setEnabled(surface.hasZones());
        }
    }

    private void selectATab() {
        if (tabbedPane.isEnabledAt(tabbedPane.getSelectedIndex()))
            return;
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.isEnabledAt(i)) {
                tabbedPane.setSelectedIndex(i);
                return;
            }
        }
    }

    @Override
    public boolean isDemo() {
        return false;
    }

    public static class Size extends DoubleField {
        private Model model;
        private IntegerField level;

        public Size(Model model, IntegerField level) {
            super(3);
            this.model = model;
            this.level = level;
            setEnabled(false);
            PropertyChangeListener listener = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("value")) {
                        recalculate();
                    }
                }
            };
            level.addPropertyChangeListener(listener);
        }

        public void recalculate() {
            if (level.getValue() != null) {
                double[] d1 = model.getGeometry().getCellSize(level.getIntValue());
                setDoubleValue(d1[0]);
            } else {
                setValue(null);
            }
        }
    }

}
