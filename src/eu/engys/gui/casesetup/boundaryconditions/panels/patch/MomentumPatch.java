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
package eu.engys.gui.casesetup.boundaryconditions.panels.patch;

import static eu.engys.core.project.constant.ThermophysicalProperties.GAMMA_KEY;
import static eu.engys.core.project.zero.fields.Fields.P;
import static eu.engys.core.project.zero.fields.Fields.U;
import static eu.engys.gui.casesetup.boundaryconditions.factories.PressureFactory.fixedFluxPressure;
import static eu.engys.gui.casesetup.boundaryconditions.factories.PressureFactory.fixedValuePressure;
import static eu.engys.gui.casesetup.boundaryconditions.factories.PressureFactory.fixedValuePressure_COMP;
import static eu.engys.gui.casesetup.boundaryconditions.factories.PressureFactory.freestreamPressure;
import static eu.engys.gui.casesetup.boundaryconditions.factories.PressureFactory.totalPressure;
import static eu.engys.gui.casesetup.boundaryconditions.factories.PressureFactory.zeroGradientPressure;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.cylindricalInletVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.fixedValueVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.freestreamVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.inletOutletVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.massFlowRateInletVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.pressureDirectedInletOutletVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.pressureDirectedInletVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.pressureInletOutletVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.pressureInletVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.surfaceNormalFixedValue;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.variableHeightFlowRateInletVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.volumetricFlowRateInletVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.factories.VelocityFactory.zeroGradientVelocity;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.CYLINDRICAL_INLET_VELOCITY_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FIXED_FLUX_PRESSURE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FIXED_VALUE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FREESTREAM_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.FREESTREAM_PRESSURE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.GAMMA_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INLET_OUTLET_VELOCITY_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.INLET_VALUE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.MASS_FLOW_RATE_INLET_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.MASS_FLOW_RATE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PRESSURE_COMP_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PRESSURE_DIRECTED_INLET_OUTLET_VELOCITY_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PRESSURE_DIRECTED_INLET_VELOCITY_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PRESSURE_INCOMP_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PRESSURE_INLET_OUTLET_VELOCITY_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PRESSURE_INLET_VELOCITY_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.PRESSURE_TYPE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.SURFACE_NORMAL_FIXED_VALUE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.TOTAL_PRESSURE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.VARIABLE_HEIGHT_FLOW_RATE_INLET_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.VELOCITY_MAGNITUDE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.VELOCITY_TYPE_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.VOLUMETRIC_FLOW_RATE_INLET_LABEL;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.VOLUMETRIC_FLOW_RATE_KEY;
import static eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils.ZERO_GRADIENT_LABEL;
import static eu.engys.util.Symbols.CUBE;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import eu.engys.core.dictionary.Dictionary;
import eu.engys.core.dictionary.model.DictionaryModel;
import eu.engys.core.dictionary.model.DictionaryPanelBuilder;
import eu.engys.core.modules.boundaryconditions.BoundaryTypePanel;
import eu.engys.core.project.Model;
import eu.engys.core.project.zero.patches.BoundaryConditions;
import eu.engys.gui.casesetup.boundaryconditions.parameterspanel.MomentumParametersPanel;
import eu.engys.gui.casesetup.boundaryconditions.utils.BoundaryConditionsUtils;
import eu.engys.util.ui.builder.JComboBoxController;
import eu.engys.util.ui.textfields.DoubleField;

public class MomentumPatch extends MomentumParametersPanel {

    private DictionaryPanelBuilder velocityBuilder;
    private JComboBoxController velocityTypeChoice;

    private DictionaryPanelBuilder pressureBuilder;
    private JComboBoxController pressureChoice;

    private JLabel totalPressureLabel;
    private JLabel fixedPressureLabel;

    private DoubleField fixedPressureField;
    private DoubleField totalPressureField;

    private DictionaryModel fixedValueVelocityModel;
    private DictionaryModel cylindricalInletVelocityModel;
    private DictionaryModel surfaceNormalFixedValuemodel;
    private DictionaryModel massFlowRateModel;
    private DictionaryModel variableHeightFlowRateModel;
    private DictionaryModel volumetricFlowRateModel;
    private DictionaryModel pressureInletVelocityModel;
    private DictionaryModel pressureDirectedInletVelocityModel;
    private DictionaryModel pressureInletOutletVelocityModel;
    private DictionaryModel pressureDirectedInletOutletVelocityModel;
    private DictionaryModel freeStreamVelocityModel;
    private DictionaryModel inletOutletVelocityModel;
    private DictionaryModel zeroGradientVelocityModel;

    private DictionaryModel fixedValuePressureModel;
    private DictionaryModel fixedFluxPressureModel;
    private DictionaryModel freestreamPressureModel;
    private DictionaryModel totalPressureModel;
    private DictionaryModel zeroGradientPressureModel;

    public MomentumPatch(BoundaryTypePanel parent) {
        super(parent);
    }

    @Override
    protected void init() {
        // Velocity
        fixedValueVelocityModel = new DictionaryModel();
        cylindricalInletVelocityModel = new DictionaryModel();
        surfaceNormalFixedValuemodel = new DictionaryModel();
        massFlowRateModel = new DictionaryModel();
        volumetricFlowRateModel = new DictionaryModel();
        variableHeightFlowRateModel = new DictionaryModel();
        pressureInletVelocityModel = new DictionaryModel();
        pressureDirectedInletVelocityModel = new DictionaryModel();
        pressureInletOutletVelocityModel = new DictionaryModel();
        pressureDirectedInletOutletVelocityModel = new DictionaryModel();
        freeStreamVelocityModel = new DictionaryModel();
        inletOutletVelocityModel = new DictionaryModel();
        zeroGradientVelocityModel = new DictionaryModel();

        // Pressure
        fixedValuePressureModel = new DictionaryModel();
        fixedFluxPressureModel = new DictionaryModel();
        freestreamPressureModel = new DictionaryModel();
        totalPressureModel = new DictionaryModel();
        zeroGradientPressureModel = new DictionaryModel();
    }

    @Override
    public void resetToDefault(Model model) {
        // Velocity
        fixedValueVelocityModel.setDictionary(new Dictionary(fixedValueVelocity));
        cylindricalInletVelocityModel.setDictionary(new Dictionary(cylindricalInletVelocity));
        surfaceNormalFixedValuemodel.setDictionary(new Dictionary(surfaceNormalFixedValue));
        variableHeightFlowRateModel.setDictionary(new Dictionary(variableHeightFlowRateInletVelocity));
        massFlowRateModel.setDictionary(new Dictionary(massFlowRateInletVelocity));
        volumetricFlowRateModel.setDictionary(new Dictionary(volumetricFlowRateInletVelocity));
        pressureInletVelocityModel.setDictionary(new Dictionary(pressureInletVelocity));
        pressureDirectedInletVelocityModel.setDictionary(new Dictionary(pressureDirectedInletVelocity));
        pressureInletOutletVelocityModel.setDictionary(new Dictionary(pressureInletOutletVelocity));
        pressureDirectedInletOutletVelocityModel.setDictionary(new Dictionary(pressureDirectedInletOutletVelocity));
        freeStreamVelocityModel.setDictionary(new Dictionary(freestreamVelocity));
        inletOutletVelocityModel.setDictionary(new Dictionary(inletOutletVelocity));
        zeroGradientVelocityModel.setDictionary(new Dictionary(zeroGradientVelocity));

        // Pressure
        updateFixedValuePressureModel(model);
        totalPressureModel.setDictionary(new Dictionary(totalPressure));
        freestreamPressureModel.setDictionary(new Dictionary(freestreamPressure));
        fixedFluxPressureModel.setDictionary(new Dictionary(fixedFluxPressure));
        zeroGradientPressureModel.setDictionary(new Dictionary(zeroGradientPressure));

    }

    @Override
    public DictionaryModel getDictionaryModel() {
        DictionaryModel velocityModel = velocityBuilder.getSelectedModel();
        velocityModel.setCompanion(pressureBuilder.getSelectedModel());
        return velocityModel;
    }

    @Override
    public void populatePanel() {
        resetToDefault(null);
        /* VELOCITY */
        velocityBuilder = new DictionaryPanelBuilder();
        velocityTypeChoice = (JComboBoxController) velocityBuilder.startChoice(VELOCITY_TYPE_LABEL);

        buildFixedValueVelocity();
        buildCylindricalInletVelocity();
        buildSurfaceNormalFixedValue();
        buildMassFlowRate();
        buildVolumetricFlowRate();
        buildVariableHeightFlowRate();
        buildPressureInletVelocity();
        buildPressureDirectedInletVelocity();
        buildPressureInletOutletVelocity();
        buildPressureDirectedInletOutletVelocity();
        buildFreestream();
        buildInletOutlet();
        buildZeroGradientVelocity();
        velocityBuilder.endChoice();

        /* PRESSURE */
        pressureBuilder = new DictionaryPanelBuilder();
        pressureChoice = (JComboBoxController) pressureBuilder.startChoice(PRESSURE_TYPE_LABEL);
        buildFixedValuePressure();
        buildTotalPressure();
        buildFreestreamPressure();
        buildBuoyantPressure();
        buildZeroGradientPressure();
        pressureBuilder.endChoice();

        /* ---- */
        JPanel velocityPanel = velocityBuilder.getPanel();
        JPanel pressurePanel = pressureBuilder.getPanel();
        velocityPanel.setBorder(BorderFactory.createTitledBorder("速度"));
        pressurePanel.setBorder(BorderFactory.createTitledBorder("压力"));
        builder.addComponent(velocityPanel);
        builder.addComponent(pressurePanel);
    }

    /**
     * VELOCITY
     */
    private void buildFixedValueVelocity() {
        velocityBuilder.startDictionary(FIXED_VALUE_LABEL, fixedValueVelocityModel);
        BoundaryConditionsUtils.buildSimpleFixedVelocityPanel(velocityBuilder, fixedValueVelocityModel);
        velocityBuilder.endDictionary();
    }

    private void buildCylindricalInletVelocity() {
        velocityBuilder.startDictionary(CYLINDRICAL_INLET_VELOCITY_LABEL, cylindricalInletVelocityModel);
        BoundaryConditionsUtils.buildFixedCylindricalVelocityPanel(velocityBuilder, cylindricalInletVelocityModel);
        velocityBuilder.endDictionary();
    }

    private void buildSurfaceNormalFixedValue() {
        velocityBuilder.startDictionary(SURFACE_NORMAL_FIXED_VALUE_LABEL, surfaceNormalFixedValuemodel);
        velocityBuilder.addComponent(VELOCITY_MAGNITUDE_LABEL, surfaceNormalFixedValuemodel.bindUniformDouble("refValue", -Double.MAX_VALUE, 0, 0));
        velocityBuilder.endDictionary();
    }

    private void buildMassFlowRate() {
        velocityBuilder.startDictionary(MASS_FLOW_RATE_INLET_LABEL, massFlowRateModel);
        velocityBuilder.addComponent("Mass Flow Rate [kg/s]", massFlowRateModel.bindConstantDouble("massFlowRate"));
        velocityBuilder.endDictionary();
    }

    private void buildVolumetricFlowRate() {
        velocityBuilder.startDictionary(VOLUMETRIC_FLOW_RATE_INLET_LABEL, volumetricFlowRateModel);
        velocityBuilder.addComponent("Volumetric Flow Rate [m" + CUBE + "/s]", volumetricFlowRateModel.bindConstantDouble("volumetricFlowRate"));
        velocityBuilder.endDictionary();
    }

    private void buildVariableHeightFlowRate() {
        velocityBuilder.startDictionary(VARIABLE_HEIGHT_FLOW_RATE_INLET_LABEL, variableHeightFlowRateModel);
        velocityBuilder.addComponent("Volumetric Flow Rate [m" + CUBE + "/s]", variableHeightFlowRateModel.bindDouble("flowRate"));
        velocityBuilder.endDictionary();
    }

    public void buildPressureInletVelocity() {
        velocityBuilder.startDictionary(PRESSURE_INLET_VELOCITY_LABEL, pressureInletVelocityModel);
        velocityBuilder.endDictionary();
    }

    public void buildPressureDirectedInletVelocity() {
        velocityBuilder.startDictionary(PRESSURE_DIRECTED_INLET_VELOCITY_LABEL, pressureDirectedInletVelocityModel);
        velocityBuilder.addComponent("Inlet Direction", pressureDirectedInletVelocityModel.bindPoint("inletDirection"));
        velocityBuilder.endDictionary();
    }

    public void buildPressureInletOutletVelocity() {
        velocityBuilder.startDictionary(PRESSURE_INLET_OUTLET_VELOCITY_LABEL, pressureInletOutletVelocityModel);
        velocityBuilder.endDictionary();
    }

    public void buildPressureDirectedInletOutletVelocity() {
        velocityBuilder.startDictionary(PRESSURE_DIRECTED_INLET_OUTLET_VELOCITY_LABEL, pressureDirectedInletOutletVelocityModel);
        velocityBuilder.addComponent("Inlet Direction", pressureDirectedInletOutletVelocityModel.bindPoint("inletDirection"));
        velocityBuilder.endDictionary();
    }

    private void buildFreestream() {
        velocityBuilder.startDictionary(FREESTREAM_LABEL, freeStreamVelocityModel);
        BoundaryConditionsUtils.buildFreestreamVelocityPanel(velocityBuilder, freeStreamVelocityModel);
        velocityBuilder.endDictionary();
    }

    public void buildInletOutlet() {
        velocityBuilder.startDictionary(INLET_OUTLET_VELOCITY_LABEL, inletOutletVelocityModel);
        velocityBuilder.addComponent(INLET_VALUE_LABEL, inletOutletVelocityModel.bindUniformPoint("inletValue"));
        velocityBuilder.endDictionary();
    }

    private void buildZeroGradientVelocity() {
        velocityBuilder.startDictionary(ZERO_GRADIENT_LABEL, zeroGradientVelocityModel);
        velocityBuilder.endDictionary();
    }

    /**
     * PRESSURE
     */
    private void buildFixedValuePressure() {
        fixedPressureLabel = new JLabel(PRESSURE_INCOMP_LABEL);
        fixedPressureField = fixedValuePressureModel.bindUniformDouble("value");
        pressureBuilder.startDictionary(FIXED_VALUE_LABEL, fixedValuePressureModel);
        pressureBuilder.addComponent(fixedPressureLabel, fixedPressureField);
        pressureBuilder.endDictionary();
    }

    private void buildTotalPressure() {
        totalPressureLabel = new JLabel(PRESSURE_INCOMP_LABEL);
        pressureBuilder.startDictionary(TOTAL_PRESSURE_LABEL, totalPressureModel);
        totalPressureField = totalPressureModel.bindUniformDouble("p0");
        pressureBuilder.addComponent(totalPressureLabel, totalPressureField);
        pressureBuilder.addComponent(GAMMA_LABEL, totalPressureModel.bindDouble(GAMMA_KEY));
        pressureBuilder.endDictionary();
    }

    private void buildFreestreamPressure() {
        pressureBuilder.startDictionary(FREESTREAM_PRESSURE_LABEL, freestreamPressureModel);
        pressureBuilder.endDictionary();
    }

    private void buildBuoyantPressure() {
        pressureBuilder.startDictionary(FIXED_FLUX_PRESSURE_LABEL, fixedFluxPressureModel);
        pressureBuilder.endDictionary();
    }

    private void buildZeroGradientPressure() {
        pressureBuilder.startDictionary(ZERO_GRADIENT_LABEL, zeroGradientPressureModel);
        pressureBuilder.endDictionary();
    }

    /**
     * END
     */

    @Override
    public void loadFromBoundaryConditions(BoundaryConditions bc) {
        Dictionary dictionary = bc.getMomentum();

        if (dictionary.subDict(U) != null) {
            Dictionary uDict = new Dictionary(dictionary.subDict(U));
            if (uDict.found(MASS_FLOW_RATE_KEY)) {
                velocityBuilder.selectDictionaryByKey(massFlowRateModel.getKey(), uDict);
            } else if (uDict.found(VOLUMETRIC_FLOW_RATE_KEY)) {
                velocityBuilder.selectDictionaryByKey(volumetricFlowRateModel.getKey(), uDict);
            } else {
                velocityBuilder.selectDictionary(uDict);
            }
        } else {
            velocityBuilder.selectDictionary(null);
        }

        Dictionary p = dictionary.subDict(P);
        if (p != null) {
            pressureBuilder.selectDictionary(new Dictionary(p));
        } else {
            pressureBuilder.selectDictionary(null);
        }
    }

    @Override
    public void saveToBoundaryConditions(BoundaryConditions bc) {
        Dictionary momentum = bc.getMomentum();
        DictionaryModel velocityModel = velocityBuilder.getSelectedModel();
        DictionaryModel pressureModel = pressureBuilder.getSelectedModel();

        momentum.add(new Dictionary(velocityModel.getDictionary()));
        momentum.add(new Dictionary(pressureModel.getDictionary()));
    }

    @Override
    public void stateChanged(Model model) {
        updateGUI(model);
        updateFixedValuePressureModel(model);
    }

    private void updateGUI(Model model) {
        pressureChoice.clearDisabledIndexes();
        velocityTypeChoice.clearDisabledIndexes();

        if (model.getState() != null && model.getState().isCompressible()) {
            fixedPressureLabel.setText(PRESSURE_COMP_LABEL);
            totalPressureLabel.setText(PRESSURE_COMP_LABEL);

            fixedPressureField.setName(PRESSURE_COMP_LABEL);
            totalPressureField.setName(PRESSURE_COMP_LABEL);

            velocityTypeChoice.clearDisabledIndexes();

            velocityTypeChoice.addDisabledItem(VOLUMETRIC_FLOW_RATE_INLET_LABEL);
        } else {
            fixedPressureLabel.setText(PRESSURE_INCOMP_LABEL);
            totalPressureLabel.setText(PRESSURE_INCOMP_LABEL);

            fixedPressureField.setName(PRESSURE_INCOMP_LABEL);
            totalPressureField.setName(PRESSURE_INCOMP_LABEL);

            velocityTypeChoice.addDisabledItem(MASS_FLOW_RATE_INLET_LABEL);
        }

        if (!model.getState().getMultiphaseModel().isMultiphase()) {
            velocityTypeChoice.addDisabledItem(VARIABLE_HEIGHT_FLOW_RATE_INLET_LABEL);
        }
    }

    private void updateFixedValuePressureModel(Model model) {
        if (model != null && model.getState() != null && model.getState().isCompressible()) {
            fixedValuePressureModel.setDictionary(new Dictionary(fixedValuePressure_COMP));
        } else {
            fixedValuePressureModel.setDictionary(new Dictionary(fixedValuePressure));
        }
    }
}
