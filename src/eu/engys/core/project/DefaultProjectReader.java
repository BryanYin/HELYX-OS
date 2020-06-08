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
package eu.engys.core.project;

import java.io.File;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.core.modules.ApplicationModule;
import eu.engys.core.modules.ModulesUtil;
import eu.engys.core.project.geometry.factory.DefaultGeometryFactory;
import eu.engys.core.project.materials.MaterialsReader;
import eu.engys.core.project.mesh.MeshInfoReader;
import eu.engys.core.project.state.StateBuilder;
import eu.engys.core.project.state.Table15;
import eu.engys.core.project.system.ControlDict;
import eu.engys.core.project.system.fieldmanipulationfunctionobjects.FieldManipulationFunctionObjectType;
import eu.engys.core.project.system.monitoringfunctionobjects.MonitoringFunctionObjectType;
import eu.engys.core.project.zero.cellzones.CellZonesBuilder;
import eu.engys.core.project.zero.fields.Initialisations;
import eu.engys.util.progress.ProgressMonitor;

public class DefaultProjectReader extends AbstractProjectReader {

    private static final Logger logger = LoggerFactory.getLogger(ProjectReader.class);
    private Set<ApplicationModule> modules;
    private Set<FieldManipulationFunctionObjectType> ffoTypes;
    private Set<MonitoringFunctionObjectType> mfoTypes;
    private final Initialisations initialisation;
    private MaterialsReader materialsReader;
    private Table15 solversTable;
    private CellZonesBuilder cellZoneBuilder;

    @Inject
    public DefaultProjectReader(Model model, Table15 solversTable, MaterialsReader materialsReader, CellZonesBuilder cellZoneBuilder, Set<ApplicationModule> modules, Set<FieldManipulationFunctionObjectType> ffoTypes, Set<MonitoringFunctionObjectType> mfoTypes, Initialisations initialisation, ProgressMonitor monitor) {
        super(model, monitor);
        this.solversTable = solversTable;
        this.materialsReader = materialsReader;
        this.cellZoneBuilder = cellZoneBuilder;
        this.modules = modules;
        this.initialisation = initialisation;
        this.ffoTypes = ffoTypes;
        this.mfoTypes = mfoTypes;
    }

    @Override
    public void readMesh() {
        File baseDir = model.getProject().getBaseDir();
        if (baseDir.exists() && baseDir.isDirectory()) {
            openFOAMProject prj = model.getProject();
            ControlDict controlDict = prj.getSystemFolder().getControlDict();
            if (controlDict != null) {
                if (controlDict.isBinary()) {
                    monitor.error("不支持二进制字段格式");
                } else {
                    prj.getSystemFolder().readProjectDict(model, monitor, prj.getSystemFolder().getFileManager().getFile());
                    logger.info("### Read mesh: '{}' ### ", prj.getZeroFolder().getFileManager().getFile());
                    prj.getZeroFolder().read(model, cellZoneBuilder, modules, initialisation, monitor);
                }
            }
            
            new MeshInfoReader(model).read();
            
            if (!model.getPatches().isEmpty()) {
                model.getGeometry().hideSurfaces();
            }
        } else {
            monitor.error(baseDir + " not found");
        }
    }
    
    @Override
    public void read() {
        File baseDir = model.getProject().getBaseDir();
        logger.info("################## Read '{}' ################## ", baseDir.getName());
        if (baseDir.exists() && baseDir.isDirectory()) {
            defaultRead();
            for (ProjectReader reader : readers) {
                reader.read();
            }
            DefaultGeometryFactory.clearSTLCache();
        } else {
            monitor.error(baseDir + " 无此目录");
        }
        logger.info("################## End Read ################## ");
    }

    
    private void defaultRead() {
        monitor.info("");
        monitor.info("正在读取项目");
        openFOAMProject project = model.getProject();

        monitor.info("-> 读取 Constant 目录");
        project.getConstantFolder().read(model, monitor);

        monitor.info("-> 读取 System 目录");
        project.getSystemFolder().read(model, ffoTypes, mfoTypes, monitor);

        new SolverModelReader(model.getSolverModel()).load(project.getSystemFolder().getProjectDict());
        
        new MeshInfoReader(model).read();

        monitor.info("-> 读取 几何图形");
        model.getGeometry().loadGeometry(model, monitor);
        
        monitor.info("-> 读取 模块数据");
        ModulesUtil.read(modules);
        
        monitor.info("-> 读取 状态");
        StateBuilder.loadState(model, solversTable, monitor);

        monitor.info("-> 读取 模块状态");
        ModulesUtil.loadState(modules);

        /*
         * Call updateSolver after loadState because some module may need some other module 
         * state in order to select the correct solver (e.g. Dynamic and VOF)
         */
        monitor.info("-> 读取 求解器");
        solversTable.updateSolver(model.getState());

        monitor.info("-> 读取 模块求解器");
        ModulesUtil.updateSolver(modules, model.getState());

        monitor.info("-> 读取 流体材料");
        model.getMaterials().loadMaterials(model, materialsReader, monitor);
        ModulesUtil.loadMaterials(modules);

        monitor.info("-> 读取 0 目录");
        ControlDict controlDict = project.getSystemFolder().getControlDict();
        if (controlDict != null) {
            if (controlDict.isBinary()) {
                monitor.error("不支持二进制字段格式", 1);
            } else {
                project.getZeroFolder().read(model, cellZoneBuilder, modules, initialisation, monitor);
            }
        } else {
            monitor.error("没有发现控制 dict 文件", 1);
        }

        if (!model.getPatches().isEmpty()) {
            model.getGeometry().hideSurfaces();
        }
    }
}
