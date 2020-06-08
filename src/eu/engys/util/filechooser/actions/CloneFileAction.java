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
package eu.engys.util.filechooser.actions;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JOptionPane.showOptionDialog;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.engys.util.filechooser.gui.FileChooserController;
import eu.engys.util.filechooser.table.FileSystemTableModel;
import eu.engys.util.filechooser.util.VFSUtils;
import eu.engys.util.ui.ResourcesUtil;
import eu.engys.util.ui.textfields.StringField;

public class CloneFileAction extends AbstractAction {

	private static final Logger logger = LoggerFactory.getLogger(CloneFileAction.class);

	private static final String TITLE = "复制文件";
	private static final String PROMPT = "文件名";
	public static final String TEXTFIELD_NAME = "fileobject.name";
	public static final String CANCEL_LABEL = "取消";
	public static final String CREATE_LABEL = "创建";
	private static final String FILE_ALREADY_EXISTS = "文件已经存在";
	private static final String EMPTY_NAME_MESSAGE = "不能用空名称创建文件或者目录";

	private FileChooserController controller;
	private JTable table;

	public CloneFileAction(FileChooserController controller, JTable table) {
		super(LABEL, ICON);
		putValue(SHORT_DESCRIPTION, TOOLTIP);
		this.table = table;
		this.controller = controller;
		setEnabled(!controller.isRemote());
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (table.getSelectedRows().length == 1) {
			FileSystemTableModel model = (FileSystemTableModel) table.getModel();
			FileObject fileObject = model.get(table.getSelectedRow());
			if (fileObject != null) {
				cloneFile(fileObject);
			}
		}
	}

	private void cloneFile(FileObject fileObject) {
		String currentName = fileObject.getName().getBaseName();
		String newName = promptName(currentName);
		if (newName == null) {
			return;
		} else if (newName.isEmpty()) {
			showMessageDialog(SwingUtilities.getRoot(controller.getUriPanel()), EMPTY_NAME_MESSAGE, TITLE, ERROR_MESSAGE);
			cloneFile(fileObject);
		} else {
			try {
				clone(fileObject, newName);
			} catch (FileSystemException e) {
				logger.error("Rename error: {}", e.getMessage());
			}
		}
	}

	private void clone(FileObject fileObject, String newName) throws FileSystemException {
		FileObject parentFileObject = fileObject.getParent();
		String parentFile = VFSUtils.decode(parentFileObject.getName().getURI(), controller.getSshParameters());
		File newFile = new File(parentFile, newName);
		if (newFile.exists()) {
			showMessageDialog(SwingUtilities.getRoot(controller.getUriPanel()), FILE_ALREADY_EXISTS, TITLE, ERROR_MESSAGE);
			cloneFile(fileObject);
		} else {
			cloneInAThread(fileObject, newName, parentFileObject);
		}
	}

	private void cloneInAThread(final FileObject fileObject, final String newName, final FileObject parentFileObject) {
		controller.showLoading();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					FileObject newFO = parentFileObject.resolveFile(newName);
					newFO.copyFrom(fileObject, new AllFileSelector());
					controller.refreshLocation(parentFileObject);
					controller.showTable();
				} catch (FileSystemException e) {
					logger.error("Rename error: {}", e.getMessage());
				}
			}
		}).start();
	}

	private String promptName(String initialValue) {
		StringField nameField = new StringField(initialValue);
		nameField.setName(TEXTFIELD_NAME);
		nameField.setPrompt(PROMPT);

		Object[] options = { CREATE_LABEL, CANCEL_LABEL };
		Component parent = SwingUtilities.getRoot(controller.getUriPanel());
		int response = showOptionDialog(parent, nameField, TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (response == YES_OPTION) {
			return nameField.getText();
		}
		return null;
	}

	/**
	 * Resources
	 */

	private static final String LABEL = ResourcesUtil.getString("clone.file.label");
	private static final String TOOLTIP = ResourcesUtil.getString("clone.file.tooltip");
	private static final Icon ICON = ResourcesUtil.getIcon("clone.file.icon");
}
