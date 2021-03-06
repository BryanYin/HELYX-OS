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

package eu.engys.core.project.geometry;

import static eu.engys.core.project.system.BlockMeshDict.BLOCKS_KEY;
import static eu.engys.core.project.system.BlockMeshDict.HEX_KEY;
import static eu.engys.core.project.system.BlockMeshDict.PATCHES_KEY;
import static eu.engys.core.project.system.BlockMeshDict.SIMPLE_GRADING_KEY;
import static eu.engys.core.project.system.BlockMeshDict.VERTICES_KEY;
import static eu.engys.core.project.system.BlockMeshDict.WALL_KEY;
import static java.lang.String.valueOf;

import eu.engys.core.dictionary.FieldElement;
import eu.engys.core.dictionary.parser.ListField2;
import eu.engys.core.project.Model;
import eu.engys.core.project.geometry.surface.MultiPlane;
import eu.engys.core.project.geometry.surface.PlaneRegion;
import eu.engys.core.project.system.BlockMeshDict;

public class BlockSaver {

    private Model model;

    public BlockSaver(Model model) {
        this.model = model;
    }

    public void saveAutomaticBlock(double spacing, boolean shouldConsiderSpacing) {
        BlockMeshDict blockMeshDict = model.getProject().getSystemFolder().getBlockMeshDict();
        if(blockMeshDict != null){
            blockMeshDict.setBoundingBox(model.getGeometry().computeBoundingBox(), spacing, shouldConsiderSpacing);
        }
    }
    /*
     * Here I take the user defined block mesh parameters from the GUI and I
     * save them in blockMeshDict. If the current blockMeshDict has been
     * imported from external file I need to clean its data because there can be
     * stuff I cannot visualise in the GUI
     */
    public void saveUserDefinedBlock(MultiPlane block) {
        BlockMeshDict blockMeshDict = model.getProject().getSystemFolder().getBlockMeshDict();
        if (blockMeshDict.isFromFile()) {
            blockMeshDict = new BlockMeshDict();
        }
        saveBlocks(blockMeshDict, block);
        saveVertices(blockMeshDict, block);
        savePatches(blockMeshDict, block);
    }
    

    private void saveBlocks(BlockMeshDict blockMeshDict, MultiPlane block) {
        ListField2 blocksList = new ListField2(BLOCKS_KEY);

        // 1
        blocksList.add(new FieldElement("", HEX_KEY));

        // 2
        ListField2 hexList = new ListField2("");
        hexList.add("0", "1", "2", "3", "4", "5", "6", "7");
        blocksList.add(hexList);

        // 3
        ListField2 elementsList = new ListField2("");
        int[] elements = block.getElements();
        for (int el : elements) {
            elementsList.add(new FieldElement("", String.valueOf(el)));
        }
        blocksList.add(elementsList);

        // 4
        blocksList.add(new FieldElement("", SIMPLE_GRADING_KEY));

        // 5
        ListField2 lastList = new ListField2("");
        lastList.add("1", "1", "1");
        blocksList.add(lastList);

        blockMeshDict.add(blocksList);
    }

    private void savePatches(BlockMeshDict blockMeshDict, MultiPlane block) {
        PlaneRegion[] regions = block.getPlanes();

        ListField2 patchesList = new ListField2(PATCHES_KEY);

        patchesList.add(WALL_KEY, regions[0].getName());
        patchesList.add(getValuesList("0", "4", "7", "3"));

        patchesList.add(WALL_KEY, regions[1].getName());
        patchesList.add(getValuesList("1", "2", "6", "5"));

        patchesList.add(WALL_KEY, regions[2].getName());
        patchesList.add(getValuesList("0", "1", "5", "4"));

        patchesList.add(WALL_KEY, regions[3].getName());
        patchesList.add(getValuesList("3", "7", "6", "2"));

        patchesList.add(WALL_KEY, regions[4].getName());
        patchesList.add(getValuesList("0", "3", "2", "1"));

        patchesList.add(WALL_KEY, regions[5].getName());
        patchesList.add(getValuesList("4", "5", "6", "7"));

        blockMeshDict.add(patchesList);
    }

    private void saveVertices(BlockMeshDict blockMeshDict, MultiPlane block) {
        double[] min = block.getMin();
        double[] max = block.getMax();

        ListField2 verticesList = new ListField2(VERTICES_KEY);
        verticesList.add(getPointList(min[0], min[1], min[2]));
        verticesList.add(getPointList(max[0], min[1], min[2]));
        verticesList.add(getPointList(max[0], max[1], min[2]));
        verticesList.add(getPointList(min[0], max[1], min[2]));
        verticesList.add(getPointList(min[0], min[1], max[2]));
        verticesList.add(getPointList(max[0], min[1], max[2]));
        verticesList.add(getPointList(max[0], max[1], max[2]));
        verticesList.add(getPointList(min[0], max[1], max[2]));

        blockMeshDict.add(verticesList);
    }

    /*
     * Utils
     */

    private ListField2 getValuesList(String v1, String v2, String v3, String v4) {
        ListField2 valuesList = new ListField2("");
        valuesList.add(v1, v2, v3, v4);
        ListField2 valuesContainerList = new ListField2("");
        valuesContainerList.add(valuesList);
        return valuesContainerList;
    }

    private ListField2 getPointList(double x, double y, double z) {
        ListField2 list = new ListField2("");
        list.add(valueOf(x), valueOf(y), valueOf(z));
        return list;
    }

}
