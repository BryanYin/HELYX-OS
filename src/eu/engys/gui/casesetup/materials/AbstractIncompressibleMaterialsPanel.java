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
package eu.engys.gui.casesetup.materials;

import static eu.engys.core.project.constant.TransportProperties.MU_KEY;
import static eu.engys.core.project.constant.TransportProperties.NU_KEY;
import static eu.engys.core.project.constant.TransportProperties.RHO_KEY;
import static eu.engys.core.project.constant.TransportProperties.SIGMA_KEY;
import static eu.engys.util.Symbols.CP;
import static eu.engys.util.Symbols.DENSITY;
import static eu.engys.util.Symbols.DOT;
import static eu.engys.util.Symbols.MINUS_ONE;
import static eu.engys.util.Symbols.MU_MEASURE;
import static eu.engys.util.Symbols.NU_MEASURE;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.DimensionedScalar;
import eu.engys.core.modules.materials.MaterialsBuilder;
import eu.engys.core.project.Model;
import eu.engys.core.project.materials.incompressible.IncompressibleMaterial;
import eu.engys.core.project.state.State;
import eu.engys.util.DimensionalUnits;
import eu.engys.util.bean.BeanModel;
import eu.engys.util.ui.builder.PanelBuilder;
import eu.engys.util.ui.textfields.DoubleField;
import eu.engys.util.ui.textfields.StringField;

public abstract class AbstractIncompressibleMaterialsPanel implements IncompressibleMaterialsPanel, PropertyChangeListener {

    public static final String REFERENCE_TEMPERATURE_K_LABEL = "参考温度 [K]";
    public static final String REFERENCE_ABSOLUTE_PRESSURE_PA_LABEL = "参考（绝对）压强 [Pa]";
    public static final String THERMAL_CONDUCTIVITY_LABEL = "导热系数 [W/m" + DOT + "K]";
    public static final String TURBULENT_PRANDTL_NUMBER_LABEL = "湍流普朗特数";
    public static final String SPECIFIC_HEAT_CAPACITY_LABEL = "比热容量 " + CP;
    public static final String KINEMATIC_VISCOSITY_LABEL = "运动粘度 " + NU_MEASURE;
    public static final String DYNAMIC_VISCOSITY_LABEL = "动力粘度 " + MU_MEASURE;
    public static final String DENSITY_LABEL = "密度 " + DENSITY;
    public static final String THERMAL_EXPANTION_COEFF_LABEL = "热膨胀系数 [K" + MINUS_ONE + "]";

//    protected DictionaryModel incompressibleModel = new DictionaryModel("newMaterial", getEmptyMaterial());

    protected BeanModel<IncompressibleMaterial> materialModel = new BeanModel<>(new IncompressibleMaterial());
    
    private StringField nameField;
    private DoubleField rho;
    private DoubleField mu;
    private DoubleField nu;

    protected PanelBuilder builder;
    protected MaterialsBuilder materialsBuilder;
    
    public AbstractIncompressibleMaterialsPanel() {
        this.builder = new PanelBuilder();
    }
    
    public abstract IncompressibleMaterial getMaterial(Model model);

    public abstract void setMaterial(IncompressibleMaterial material);
    
    @Override
    public void setEnabled(boolean enabled) {
        builder.setEnabled(enabled);
    }

    @Override
    public void stateChanged(State state) {
    }

    @Override
    public StringField getNameField() {
        return nameField;
    }

    protected void buildIncompressibleMaterialPanel() {
        builder.addComponent("名称", nameField = materialModel.bindLabel(IncompressibleMaterial.NAME_KEY));
        builder.addComponent(DENSITY_LABEL, rho = materialModel.bindDoublePositive(IncompressibleMaterial.RHO_KEY));
        builder.addComponent(DYNAMIC_VISCOSITY_LABEL, mu = materialModel.bindDoublePositive(IncompressibleMaterial.MU_KEY));
        builder.addComponent(KINEMATIC_VISCOSITY_LABEL, nu = materialModel.bindDoublePositive(IncompressibleMaterial.NU_KEY));

        nameField.setEnabled(false);

        addListeners();

        builder.addComponent(SPECIFIC_HEAT_CAPACITY_LABEL, materialModel.bindDoublePositive(IncompressibleMaterial.CP_KEY));
        builder.addComponent(TURBULENT_PRANDTL_NUMBER_LABEL, materialModel.bindDoublePositive(IncompressibleMaterial.PRT_KEY));
        builder.addComponent(THERMAL_CONDUCTIVITY_LABEL, materialModel.bindDoublePositive(IncompressibleMaterial.LAMBDA_KEY));
        builder.addComponent(REFERENCE_ABSOLUTE_PRESSURE_PA_LABEL, materialModel.bindDoublePositive(IncompressibleMaterial.P_REF_KEY));
        builder.addComponent(REFERENCE_TEMPERATURE_K_LABEL, materialModel.bindDoublePositive(IncompressibleMaterial.T_REF_KEY));
    }

    protected void removeListeners() {
        rho.removePropertyChangeListener("value", this);
        mu.removePropertyChangeListener("value", this);
        nu.removePropertyChangeListener("value", this);
    }
    protected void addListeners() {
        rho.addPropertyChangeListener("value", this);
        mu.addPropertyChangeListener("value", this);
        nu.addPropertyChangeListener("value", this);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == nu) {
            double rhoValue = rho.getDoubleValue();
            double nuValue = nu.getDoubleValue();

            mu.setDoubleValue(nuValue * rhoValue);
        } else if (evt.getSource() == rho || evt.getSource() == mu) {
            double rhoValue = rho.getDoubleValue();
            double muValue = mu.getDoubleValue();

            nu.setDoubleValue(rhoValue == 0.0 ? 0.0 : muValue / rhoValue);
        }
    }

    protected void adjustNuIfNeeded(Dictionary transportProps) {
        if (transportProps != null && !transportProps.found(NU_KEY)) {
            if (transportProps.found(RHO_KEY) && transportProps.found(MU_KEY)) {
                double rho = Double.valueOf(transportProps.lookupScalar(RHO_KEY).doubleValue());
                double mu = Double.valueOf(transportProps.lookupScalar(MU_KEY).doubleValue());
                double nuValue = mu == 0.0 ? 0.0 : mu / rho;
                transportProps.add(new DimensionedScalar(NU_KEY, Double.toString(nuValue), DimensionalUnits.M2_S));
            }
        }
    }

    @Override
    public Dictionary saveSigma(Model model, Dictionary sigmaDict) {
        Dictionary dict = new Dictionary(SIGMA_KEY);
        if (model.getState().getMultiphaseModel().isMultiphase()) {
            if (sigmaDict.found(SIGMA_KEY)) {
                dict.add(sigmaDict.lookupScalar(SIGMA_KEY));
            }
        }
        return dict;
    }

}
