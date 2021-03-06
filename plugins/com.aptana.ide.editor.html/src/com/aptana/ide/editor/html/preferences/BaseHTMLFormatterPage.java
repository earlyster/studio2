/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.editor.html.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.ui.editors.preferences.formatter.CompilationUnitPreview;
import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;
import com.aptana.ide.ui.editors.preferences.formatter.FormatterTabPage;
import com.aptana.ide.ui.editors.preferences.formatter.Preview;

/**
 * @author Pavel Petrochenko
 */
public abstract class BaseHTMLFormatterPage extends FormatterTabPage
{

	/**
	 * 
	 */
	protected String editor;
	/**
	 * Constant array for boolean selection
	 */
	protected static String[] FALSE_TRUE = { DefaultCodeFormatterConstants.FALSE, DefaultCodeFormatterConstants.TRUE };
	/**
	 * PREVIEW
	 */
	protected static final String PREVIEW = "<!DOCTYPE html\r\n" + //$NON-NLS-1$
			"    PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\r\n" + //$NON-NLS-1$
			"    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n" + //$NON-NLS-1$
			"<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">\r\n" + //$NON-NLS-1$
			"<head>\r\n" + //$NON-NLS-1$
			" <title>HTML Formatting Sample</title><style type=\"text/css\">\r\n" + //$NON-NLS-1$
			"\r\n" + //$NON-NLS-1$
			"@import url(\"/shared.css\");\r\n" + //$NON-NLS-1$
			"\r\n" + //$NON-NLS-1$
			"#footer {\r\n" + //$NON-NLS-1$
			"	border:1px solid white;\r\n" + //$NON-NLS-1$
			"}\r\n" + //$NON-NLS-1$
			"\r\n" + //$NON-NLS-1$
			"#banner {\r\n" + //$NON-NLS-1$
			"  background-color: #636D84;\r\n" + //$NON-NLS-1$
			"  padding-right:40px;\r\n" + //$NON-NLS-1$
			"  padding-top:10px;\r\n" + //$NON-NLS-1$
			"  height:40px;\r\n" + //$NON-NLS-1$
			"}\r\n" + //$NON-NLS-1$
			"</style>\r\n" + //$NON-NLS-1$
			" <script type=\"text/javascript\" src=\"/trac/chrome/common/js/trac.js\"></script>\r\n" + //$NON-NLS-1$
			"</head>\r\n" + //$NON-NLS-1$
			"<body>\r\n" + //$NON-NLS-1$
			"\r\n" + //$NON-NLS-1$
			"<div id=\"navigation\">\r\n" + //$NON-NLS-1$
			"   		<div id=\"header\">\r\n" + //$NON-NLS-1$
			"		<h1><a title=\"Return to home page\" accesskey=\"1\" href=\"/\">Aptana</a></h1>\r\n" + //$NON-NLS-1$
			"	</div>\r\n" + //$NON-NLS-1$
			"	<div>\r\n" + //$NON-NLS-1$
			"		<ul>\r\n" + //$NON-NLS-1$
			"			<li><a href=\"/dev\">contribute</a></li>\r\n" + //$NON-NLS-1$
			"			<li><a href=\"/forums\">forums</a></li>\r\n" + //$NON-NLS-1$
			"			<li><a href=\"/download_all.php\">products</a></li>\r\n" + //$NON-NLS-1$
			"			<li><a href=\"/support.php\">support</a></li>\r\n" + //$NON-NLS-1$
			"			<li><a href=\"/about.php\">about</a></li>\r\n" + //$NON-NLS-1$
			"		</ul>\r\n" + //$NON-NLS-1$
			"	</div>\r\n" + //$NON-NLS-1$
			"</div>\r\n" + //$NON-NLS-1$
			" </body>\r\n" + //$NON-NLS-1$
			"</html>"; //$NON-NLS-1$

	/**
	 * 
	 */
	protected CompilationUnitPreview fPreview;

	/**
	 * 
	 *
	 */
	protected class NewLineController
	{
		private Group nodeGroup;

		private Composite buttons;

		private Table foldingTable;

		private Button add;

		private Button remove;

		private String title;

		private String key;

		/**
		 * @param title
		 * @param key
		 */
		public NewLineController(String title, String key)
		{
			this.title = title;
			this.key = key;
		}

		/**
		 * @param composite
		 */
		protected void doCreatePartControl(Composite composite)
		{
			nodeGroup = new Group(composite, SWT.NONE);
			GridLayout groupLayout = new GridLayout(1, true);
			nodeGroup.setLayout(groupLayout);
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			gridData.horizontalSpan = 3;
			nodeGroup.setLayoutData(gridData);
			nodeGroup.setText(title);
			buttons = new Composite(nodeGroup, SWT.NONE);
			GridLayout buttonsLayout = new GridLayout(2, true);
			buttonsLayout.marginHeight = 0;
			buttonsLayout.marginWidth = 0;
			buttons.setLayout(buttonsLayout);
			add = new Button(buttons, SWT.PUSH);
			add.setImage(HTMLPlugin.getImage("icons/add_obj.gif")); //$NON-NLS-1$
			add.setToolTipText(Messages.NewLinesTabPage_ADD_TOOLTIP);
			add.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					InputDialog dialog = new InputDialog(nodeGroup.getShell(), Messages.NewLinesTabPage_ADD_TITLE,
							Messages.NewLinesTabPage_ADD_DESCRIPTION, null, null);
					int rc = dialog.open();
					if (rc == InputDialog.OK)
					{
						String newNodes = dialog.getValue();
						String[] nodes = newNodes.split(","); //$NON-NLS-1$
						List<String> nodeList = new ArrayList<String>();
						for (int i = 0; i < nodes.length; i++)
						{
							String trimmed = nodes[i].trim();
							if (trimmed.length() > 0)
							{
								nodeList.add(trimmed);
							}
						}
						TableItem[] items = foldingTable.getItems();
						for (int i = 0; items != null && i < items.length; i++)
						{
							if (!nodeList.contains(items[i].getText()))
							{
								nodeList.add(items[i].getText());
							}
						}
						Collections.sort(nodeList);
						foldingTable.removeAll();
						for (int i = 0; i < nodeList.size(); i++)
						{
							TableItem item = new TableItem(foldingTable, SWT.LEFT);
							item.setText(nodeList.get(i));
							item.setImage(HTMLPlugin.getImage("icons/element_icon.gif")); //$NON-NLS-1$
						}
						update();
					}
				}

			});
			remove = new Button(buttons, SWT.PUSH);
			remove.setImage(HTMLPlugin.getImage("icons/delete_obj.gif")); //$NON-NLS-1$
			remove.setToolTipText(Messages.NewLinesTabPage_REMOVE_TOOLTIP);
			remove.setEnabled(false);
			remove.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					TableItem[] items = foldingTable.getSelection();
					int[] indices = foldingTable.getSelectionIndices();
					if (items != null)
					{
						for (int i = 0; i < items.length; i++)
						{
							items[i].dispose();
						}
						if (indices.length > 1)
						{
							int last = indices[indices.length - 1];
							if (foldingTable.getItemCount() - 1 >= last)
							{
								foldingTable.setSelection(last);
							}
							else if (foldingTable.getItemCount() > 0)
							{
								foldingTable.setSelection(foldingTable.getItemCount() - 1);
							}
						}
						else if (indices.length == 1)
						{
							if (foldingTable.getItemCount() - 1 >= indices[0])
							{
								foldingTable.setSelection(indices[0]);
							}
							else if (foldingTable.getItemCount() > 0)
							{
								foldingTable.setSelection(foldingTable.getItemCount() - 1);
							}
						}
					}
					update();
					remove.setEnabled(foldingTable.getSelectionCount() > 0);
				}

			});
			buttons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			foldingTable = new Table(nodeGroup, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
			foldingTable.setLayoutData(gridData);
			foldingTable.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					remove.setEnabled(true);
				}

			});
			String foldedNodeList = (String) fWorkingValues.get(key);
			if (foldedNodeList == null)
			{
				return;
			}
			String[] nodes = foldedNodeList.split(","); //$NON-NLS-1$
			for (int i = 0; i < nodes.length; i++)
			{
				String trim = nodes[i].trim();
				if (trim.length() > 0)
				{
					TableItem item = new TableItem(foldingTable, SWT.LEFT);
					item.setText(trim);
					item.setImage(HTMLPlugin.getImage("icons/element_icon.gif")); //$NON-NLS-1$
				}
			}

		}

		/**
		 * @return
		 */
		protected String createString()
		{
			String nodeListPref = ""; //$NON-NLS-1$
			for (int i = 0; i < foldingTable.getItemCount(); i++)
			{
				TableItem item = foldingTable.getItem(i);
				nodeListPref += item.getText() + Messages.NewLinesTabPage_9;
			}
			return nodeListPref;
		}
	}

	/**
	 * @param modifyListener
	 * @param workingValues
	 */
	public BaseHTMLFormatterPage(IModificationListener modifyListener, Map workingValues)
	{
		super(modifyListener, workingValues);
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#doCreateJavaPreview(org.eclipse.swt.widgets.Composite)
	 */
	protected Preview doCreateJavaPreview(Composite parent)
	{
		fPreview = new CompilationUnitPreview(fWorkingValues, parent, editor, null);
		return fPreview;
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.FormatterTabPage#doUpdatePreview()
	 */
	protected void doUpdatePreview()
	{
		super.doUpdatePreview();
		fPreview.update();
	}

	/**
	 * 
	 */
	protected void update()
	{

	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#initializePage()
	 */
	protected void initializePage()
	{
		fPreview.setPreviewText(PREVIEW);
	}

}