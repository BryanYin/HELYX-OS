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

import static eu.engys.core.modules.materials.MaterialsDatabase.AIR;
import static eu.engys.core.modules.materials.MaterialsDatabase.MERCURY;
import static eu.engys.core.modules.materials.MaterialsDatabase.OIL;
import static eu.engys.core.modules.materials.MaterialsDatabase.WATER;
import eu.engys.core.project.Model;
import eu.engys.core.project.materials.Material;
import eu.engys.core.project.materials.Materials;

public class PhaseBuilder {

    public static void saveDefaultMaterialsToProject(Model model) {
        model.getMaterials().clear();
        if (model.getState().getMultiphaseModel().isMultiphase()) {
            checkMultiMaterials(model);
        } else {
            check1Material(model);
        }
    }

    private static void check1Material(Model model) {
        Materials materials = model.getMaterials();
        if (materials.isEmpty()) {
            materials.add(getMaterial(model, AIR));
        } else if (materials.size() > 1) {
            int airIndex = 0;
            for (int i = 0; i < materials.size(); i++) {
                if (materials.get(i).getName().equals(AIR)) {
                    airIndex = i;
                    break;
                }
            }
            for (int i = 0; i < materials.size(); i++) {
                if (i != airIndex) {
                    materials.remove(i);
                }
            }
        }
        model.materialsChanged();
    }

    private static void checkMultiMaterials(Model model) {
        Materials materials = model.getMaterials();
        materials.clear();

        Material[] knownMaterials = null;
        if(model.getState().isIncompressible()){
            knownMaterials = new Material[] { getMaterial(model, AIR), getMaterial(model, WATER), getMaterial(model, OIL), getMaterial(model, MERCURY) };
        } else {
            knownMaterials = new Material[] { getMaterial(model, AIR), getMaterial(model, WATER) };
        }

        int phases = model.getState().getPhases();
        
        for (int i = 0; i < Math.min(phases, knownMaterials.length); i++) {
            materials.add(knownMaterials[i]);
        }
        for (int i = Math.min(phases, knownMaterials.length); i < phases; i++) {
            materials.add(new Material("Air" + i, knownMaterials[0].getDictionary()));
        }

        model.materialsChanged();
    }

    public static Material getMaterial(Model model, String materialName) {
        if (model.getState().isCompressible()) {
            return new Material(materialName, model.getMaterialsDatabase().getCompressibleMaterial(materialName));
        } else {
            return new Material(materialName, model.getMaterialsDatabase().getIncompressibleMaterial(materialName));
        }
    }

}
