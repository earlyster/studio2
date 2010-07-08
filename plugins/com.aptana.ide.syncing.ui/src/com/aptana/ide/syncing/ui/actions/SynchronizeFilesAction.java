/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.syncing.ui.actions;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.syncing.ConnectionPointSyncPair;
import com.aptana.ide.core.io.syncing.VirtualFileSyncPair;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.ui.handlers.SyncEventHandlerAdapter;
import com.aptana.ide.syncing.ui.internal.SyncUtils;
import com.aptana.ide.syncing.ui.views.SmartSyncDialog;

public class SynchronizeFilesAction extends BaseSyncAction {

	private static String MESSAGE_TITLE = StringUtils.ellipsify(Messages.SynchronizeFilesAction_MessageTitle);

	@Override
	protected void performAction(final IAdaptable[] files, final ISiteConnection site) throws CoreException {
		final IConnectionPoint source = site.getSource();
		final IConnectionPoint dest = site.getDestination();
		CoreUIUtils.getDisplay().asyncExec(new Runnable() {

			public void run() {
				try {
					IFileStore[] fileStores = SyncUtils.getFileStores(files);
			        ConnectionPointSyncPair cpsp = new ConnectionPointSyncPair(source, dest);
					SmartSyncDialog dialog = new SmartSyncDialog(getShell(), cpsp, fileStores);
					dialog.open();
					dialog.setHandler(new SyncEventHandlerAdapter() {
						public void syncDone(VirtualFileSyncPair item) {
							// refresh();
						}
					});
				} catch (CoreException e) {
					MessageBox error = new MessageBox(CoreUIUtils.getActiveShell(), SWT.ICON_ERROR | SWT.OK);
					error.setMessage("Unable to open synchronization dialog.");
					error.open();
				}
			}
		});
	}

	@Override
	protected String getMessageTitle() {
		return MESSAGE_TITLE;
	}
}