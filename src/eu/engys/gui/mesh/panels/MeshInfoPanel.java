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

import static eu.engys.util.ui.ComponentsFactory.labelField;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;

import eu.engys.core.project.geometry.BoundingBox;
import eu.engys.core.project.mesh.Mesh;
import eu.engys.core.project.mesh.MeshInfo;
import eu.engys.util.ui.builder.PanelBuilder;

public class MeshInfoPanel {

    private static final String NONE = "-";
    private JLabel cells;
    private JLabel points;
    private JLabel faces;
    private JLabel internalFaces;

    private JLabel patches;
    private JLabel pointZones;
    private JLabel faceZones;
    private JLabel cellZones;

    private JLabel hexahedra;
    private JLabel prisms;
    private JLabel wedges;
    private JLabel pyramids;
    private JLabel tetWedges;
    private JLabel tetrahedra;
    private JLabel polyhedra;

    // private JTextArea cellsPerLevel;
    // private JLabel memory;

    private JLabel xBounds;
    private JLabel yBounds;
    private JLabel zBounds;

    private PanelBuilder dataArrays;
    private PanelBuilder cellTypes;
    private PanelBuilder statistics;
    private PanelBuilder bounds;

    public MeshInfoPanel() {
        layoutComponents();
    }

    private void layoutComponents() {
        points = labelField(NONE);
        cells = labelField(NONE);
        faces = labelField(NONE);
        internalFaces = labelField(NONE);

        patches = labelField(NONE);
        pointZones = labelField(NONE);
        faceZones = labelField(NONE);
        cellZones = labelField(NONE);

        hexahedra = labelField(NONE);
        prisms = labelField(NONE);
        wedges = labelField(NONE);
        pyramids = labelField(NONE);
        tetWedges = labelField(NONE);
        tetrahedra = labelField(NONE);
        polyhedra = labelField(NONE);

        // cellsPerLevel = ComponentsFactory.labelArea();
        // cellsPerLevel.setEditable(false);
        //
        // memory = labelField("");

        xBounds = labelField("");
        yBounds = labelField("");
        zBounds = labelField("");

        statistics = new PanelBuilder();
        statistics.addComponent("点数", points);
        statistics.addComponent("网格数", cells);
        statistics.addComponent("面数", faces);
        statistics.addComponent("内部面", internalFaces);

        statistics.addComponent("边界", patches);
        statistics.addComponent("点区域", pointZones);
        statistics.addComponent("面区域", faceZones);
        statistics.addComponent("网格区域", cellZones);

        cellTypes = new PanelBuilder();
        cellTypes.addComponent("六面体", hexahedra);
        cellTypes.addComponent("三棱柱", prisms);
        cellTypes.addComponent("楔形体", wedges);
        cellTypes.addComponent("多棱锥", pyramids);
        cellTypes.addComponent("四棱柱", tetWedges);
        cellTypes.addComponent("四面体", tetrahedra);
        cellTypes.addComponent("多面体", polyhedra);
        cellTypes.addComponent("", new JLabel(" "));

        // statistics.addComponent("Cells per Refinement Level", cellsPerLevel);
        // statistics.addComponent("Memory [MB]", memory);

        dataArrays = new PanelBuilder();

        bounds = new PanelBuilder();
        bounds.addComponent("X 范围", xBounds);
        bounds.addComponent("Y 范围", yBounds);
        bounds.addComponent("Z 范围", zBounds);
    }

    public void load(Mesh mesh) {
        if (mesh != null) {
            MeshInfo mi = mesh.getMeshInfo();
            if (mi.isValid()) {
                cells.setText(mi.getCells() == -1 ? NONE : Long.toString(mi.getCells()));
                points.setText(mi.getPoints() == -1 ? NONE : Long.toString(mi.getPoints()));
                faces.setText(mi.getFaces() == -1 ? NONE : Long.toString(mi.getFaces()));
                internalFaces.setText(mi.getInternalFaces() == -1 ? NONE : Long.toString(mi.getInternalFaces()));

                patches.setText(mi.getBoundaryPatches() == -1 ? NONE : Long.toString(mi.getBoundaryPatches()));
                pointZones.setText(mi.getPointZones() == -1 ? NONE : Long.toString(mi.getPointZones()));
                faceZones.setText(mi.getFaceZones() == -1 ? NONE : Long.toString(mi.getFaceZones()));
                cellZones.setText(mi.getCellZones() == -1 ? NONE : Long.toString(mi.getCellZones()));

                hexahedra.setText(mi.getHexahedra() == -1 ? NONE : Long.toString(mi.getHexahedra()));
                prisms.setText(mi.getPrisms() == -1 ? NONE : Long.toString(mi.getPrisms()));
                wedges.setText(mi.getWedges() == -1 ? NONE : Long.toString(mi.getWedges()));
                pyramids.setText(mi.getPyramids() == -1 ? NONE : Long.toString(mi.getPyramids()));
                tetWedges.setText(mi.getTetWedges() == -1 ? NONE : Long.toString(mi.getTetWedges()));
                tetrahedra.setText(mi.getTetrahedra() == -1 ? NONE : Long.toString(mi.getTetrahedra()));
                polyhedra.setText(mi.getPolyhedra() == -1 ? NONE : Long.toString(mi.getPolyhedra()));

                BoundingBox bounds = mesh.getExternalMesh().getBounds();
                xBounds.setText(getTextForBounds(bounds.getXmin(), bounds.getXmax()));
                yBounds.setText(getTextForBounds(bounds.getYmin(), bounds.getYmax()));
                zBounds.setText(getTextForBounds(bounds.getZmin(), bounds.getZmax()));
            } else {
                reset();
            }
        } else {
            reset();
        }
    }

    public JPanel getStatistics() {
        return statistics.withTitle("统计信息").getPanel();
    }

    public JPanel getCellType() {
        return cellTypes.withTitle("单元格类型").getPanel();
    }

    public JPanel getDataArrays() {
        return dataArrays.withTitle("数据组").getPanel();
    }

    public JPanel getBounds() {
        return bounds.withTitle("边界").getPanel();
    }

    public void reset() {
        cells.setText(NONE);
        points.setText(NONE);
        faces.setText(NONE);

        cells.setText(NONE);
        points.setText(NONE);
        faces.setText(NONE);
        internalFaces.setText(NONE);

        patches.setText(NONE);
        pointZones.setText(NONE);
        faceZones.setText(NONE);
        cellZones.setText(NONE);

        hexahedra.setText(NONE);
        prisms.setText(NONE);
        wedges.setText(NONE);
        pyramids.setText(NONE);
        tetWedges.setText(NONE);
        tetrahedra.setText(NONE);
        polyhedra.setText(NONE);

        // cellsPerLevel.setText(" - ");
        // memory.setText("-");
        xBounds.setText("[- , -]");
        yBounds.setText("[- , -]");
        zBounds.setText("[- , -]");
    }

    private static final DecimalFormat formatter = new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.US));

    private String getTextForBounds(double min, double max) {
        if (areValid(min, max)) {
            return "[" + formatter.format(min) + " , " + formatter.format(max) + "] (差值 " + formatter.format(max - min) + ")";
        } else {
            return "[0 , 0]";
        }
    }

    private boolean areValid(double min, double max) {
        return min < Double.MAX_VALUE && max > -Double.MAX_VALUE;
    }

}
