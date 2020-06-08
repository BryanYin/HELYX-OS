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

import java.awt.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.project.Model;
import eu.engys.core.project.zero.cellzones.CellZone;
import eu.engys.core.project.zero.cellzones.CellZones;
import eu.engys.core.project.zero.facezones.FaceZone;
import eu.engys.core.project.zero.facezones.FaceZones;
import eu.engys.core.project.zero.patches.Patch;
import eu.engys.core.project.zero.patches.Patches;
import eu.engys.gui.GUIPanel;
import eu.engys.gui.events.EventManager;
import eu.engys.gui.events.view3D.SelectCellZonesEvent;
import eu.engys.gui.events.view3D.SelectFaceZonesEvent;
import eu.engys.gui.events.view3D.SelectPatchesEvent;
import eu.engys.gui.tree.AbstractSelectionHandler;
import eu.engys.gui.tree.DefaultTreeNodeManager;
import eu.engys.gui.tree.SelectionHandler;
import eu.engys.gui.view3D.Actor;
import eu.engys.gui.view3D.Picker;
import eu.engys.util.ui.ExecUtil;
import eu.engys.util.ui.TreeUtil;
import eu.engys.util.ui.checkboxtree.RootVisibleLoadableTreeNode;
import eu.engys.util.ui.checkboxtree.VisibleItem;

public class BoundaryMeshTreeNodeManager extends DefaultTreeNodeManager<VisibleItem> {

    private static final Logger logger = LoggerFactory.getLogger(BoundaryMeshTreeNodeManager.class);

    private Map<DefaultMutableTreeNode, Patch> patchesMap;
    private Map<DefaultMutableTreeNode, CellZone> cellZonesMap;
    private Map<DefaultMutableTreeNode, FaceZone> faceZonesMap;

    private SelectionHandler selectionHandler;
    private DefaultMutableTreeNode patches;
    private DefaultMutableTreeNode cellZones;
//    private DefaultMutableTreeNode faceZones;

    public BoundaryMeshTreeNodeManager(Model model, GUIPanel panel) {
        super(model, panel);
        this.root = new RootVisibleLoadableTreeNode(panel.getTitle());
        this.selectionHandler = new BoundaryMeshSelectionHandler();
        this.patchesMap = new HashMap<>();
        this.cellZonesMap = new HashMap<>();
        this.faceZonesMap = new HashMap<>();

        patches = new RootVisibleLoadableTreeNode("切面");
        cellZones = new RootVisibleLoadableTreeNode("网格区域");
//        faceZones = new RootVisibleLoadableTreeNode("Face Zones");
        root.add(patches);
        root.add(cellZones);
//        root.add(faceZones);
    }

    @Override
    public void update(Observable o, final Object arg) {
        if (arg instanceof Patches || arg instanceof CellZones || arg instanceof FaceZones) {
            logger.debug("[CHANGE OBSERVERD] arg: " + arg.getClass().getSimpleName());
            ExecUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    selectionHandler.disable();
                    loadTree();
                    makeVisibleItemsChecked();
                    expandTree();
                    selectionHandler.enable();
                }
            });
        }
    }

    private void loadTree() {
        logger.debug("Load 'Mesh' tree");
        clear();
        for (Patch patch : model.getPatches().patchesToDisplay()) {
            addPatch(patches, patch);
        }
        for (CellZone zone : model.getCellZones()) {
            addCellZone(cellZones, zone);
        }
//        for (FaceZone zone : model.getFaceZones()) {
//            addFaceZone(faceZones, zone);
//        }

        treeChanged(root);
    }

    private void makeVisibleItemsChecked() {
        logger.debug("Make visible items checked: DO NOTHING!");
    }

    private void expandTree() {
        getTree().expandNode(getRoot());
    }

    private void addPatch(DefaultMutableTreeNode parent, Patch patch) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(patch);
        parent.add(node);
        nodeMap.put(patch, node);
        patchesMap.put(node, patch);
    }

    private void addCellZone(DefaultMutableTreeNode parent, CellZone zone) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(zone);
        parent.add(node);
        nodeMap.put(zone, node);
        cellZonesMap.put(node, zone);
    }

//    private void addFaceZone(DefaultMutableTreeNode parent, FaceZone zone) {
//        DefaultMutableTreeNode node = new DefaultMutableTreeNode(zone);
//        parent.add(node);
//        nodeMap.put(zone, node);
//        faceZonesMap.put(node, zone);
//    }

    public void setSelectedValue(String name) {
    }

    public void clear() {
        // clear node before selection handler!
        clearNode(patches);
        clearNode(cellZones);
//        clearNode(faceZones);
        selectionHandler.clear();
        nodeMap.clear();
        patchesMap.clear();
        cellZonesMap.clear();
        faceZonesMap.clear();
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }

    @Override
    public Class<?> getRendererClass() {
        return VisibleItem.class;
    }

    public DefaultTreeCellRenderer getRenderer() {
        return new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();

                if (userObject instanceof VisibleItem) {
                    VisibleItem item = (VisibleItem) userObject;
                    setText(item.getName());
                }
                setIcon(null);
                return this;
            }
        };
    }

    @Override
    public SelectionHandler getSelectionHandler() {
        return selectionHandler;
    }

    private final class BoundaryMeshSelectionHandler extends AbstractSelectionHandler {

        private VisibleItem[] currentSelection;

        @Override
        public void handleSelection(boolean fire3DEvent, Object... selection) {
            boolean isValidPatchSelection = TreeUtil.isConsistent(selection, Patch.class) && fire3DEvent;
            boolean isValidCellZoneSelection = TreeUtil.isConsistent(selection, CellZone.class) && fire3DEvent;
            boolean isValidFaceZoneSelection = TreeUtil.isConsistent(selection, FaceZone.class) && fire3DEvent;
            if (isValidPatchSelection) {
                handleValidPatchSelection(fire3DEvent, selection);
            } else if (isValidCellZoneSelection) {
                handleValidCellZoneSelection(fire3DEvent, selection);
            } else if (isValidFaceZoneSelection) {
                handleValidFaceZoneSelection(fire3DEvent, selection);
            } else {
                handleInvalidSelection();
            }
        }

        private void handleValidPatchSelection(boolean fire3DEvent, Object... selection) {
            logger.debug("handleSelection: {} selected, fire3D {} {}", selection.length, fire3DEvent, selection.length == 1 ? ", selection is: " + selection[0] : "");

            this.currentSelection = Arrays.copyOf(selection, selection.length, Patch[].class);
            if (fire3DEvent) {
                EventManager.triggerEvent(this, new SelectPatchesEvent((Patch[]) currentSelection));
            }
        }

        private void handleValidCellZoneSelection(boolean fire3DEvent, Object... selection) {
            logger.debug("handleSelection: {} selected, fire3D {} {}", selection.length, fire3DEvent, selection.length == 1 ? ", selection is: " + selection[0] : "");

            this.currentSelection = Arrays.copyOf(selection, selection.length, CellZone[].class);
            if (fire3DEvent) {
                EventManager.triggerEvent(this, new SelectCellZonesEvent((CellZone[]) currentSelection));
            }
        }

        private void handleValidFaceZoneSelection(boolean fire3DEvent, Object... selection) {
            logger.debug("handleSelection: {} selected, fire3D {} {}", selection.length, fire3DEvent, selection.length == 1 ? ", selection is: " + selection[0] : "");

            this.currentSelection = Arrays.copyOf(selection, selection.length, FaceZone[].class);
            if (fire3DEvent) {
                EventManager.triggerEvent(this, new SelectFaceZonesEvent((FaceZone[]) currentSelection));
            }
        }

        private void handleInvalidSelection() {
            boolean shouldClearPatchesSelection = TreeUtil.isConsistent(currentSelection, Patch.class);
            boolean shouldClearCellZoneSelection = TreeUtil.isConsistent(currentSelection, CellZone.class);
            boolean shouldClearFaceZoneSelection = TreeUtil.isConsistent(currentSelection, FaceZone.class);

            if (shouldClearPatchesSelection) {
                EventManager.triggerEvent(this, new SelectPatchesEvent(new Patch[0]));
            } else if (shouldClearCellZoneSelection) {
                EventManager.triggerEvent(this, new SelectCellZonesEvent(new CellZone[0]));
            } else if (shouldClearFaceZoneSelection) {
                EventManager.triggerEvent(this, new SelectFaceZonesEvent(new FaceZone[0]));
            }
        }

        @Override
        public void handleVisibility(VisibleItem item) {
            logger.debug("handleVisibility: {}", item);
        }

        @Override
        public void process3DSelectionEvent(Picker picker, Actor actor, boolean keep) {
            if (getTree() != null && picker.canPickMesh()) {
                VisibleItem visibleItem = actor.getVisibleItem();
                DefaultMutableTreeNode selectedNode = nodeMap.get(visibleItem);
                logger.debug("Handle selection from 3D {}", actor.getName());
                if (selectedNode != null) {
                    if (keep) {
                        getTree().addSelectedNode(selectedNode);
                    } else {
                        getTree().setSelectedNode(selectedNode);
                    }
                }
            }
        }

        @Override
        public void process3DVisibilityEvent(boolean selected) {
            if (getTree() != null) {
                if (selected) {
                    getTree().getCheckManager().selectNode(patches);
//                    getTree().getCheckManager().selectNode(cellZones);
//                    getTree().getCheckManager().selectNode(faceZones);
                } else {
                    getTree().getCheckManager().deselectNode(patches);
//                    getTree().getCheckManager().deselectNode(cellZones);
//                    getTree().getCheckManager().deselectNode(faceZones);
                }
            }
        }

        @Override
        public void clear() {
            currentSelection = null;
        }
    }

}
