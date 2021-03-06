/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.server.jetty.portal;

import com.aptana.ide.server.jetty.comet.ClientLoader;
import com.aptana.ide.server.jetty.comet.CometResponderClient;
import com.aptana.ide.server.jetty.comet.ICometClient;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ClientLister extends CometResponderClient
{

	private ClientLoader loader;

	/**
	 * Client lister constructor
	 * 
	 * @param loader
	 */
	public ClientLister(ClientLoader loader)
	{
		this.loader = loader;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getResponse(java.lang.String, java.lang.Object)
	 */
	protected Object getResponse(String toChannel, Object request)
	{
		if (loader != null)
		{
			ICometClient[] clients = loader.getClients();
			if (clients.length > 0)
			{
				String[] ids = new String[clients.length];
				for (int i = 0; i < clients.length; i++)
				{
					ids[i] = clients[i].getID();
				}
				return ids;
			}
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getSubscriptionIDs()
	 */
	protected String[] getSubscriptionIDs()
	{
		return new String[] { "/portal/client/listings" }; //$NON-NLS-1$
	}

	/**
	 * @return the loader
	 */
	public ClientLoader getLoader()
	{
		return loader;
	}

	/**
	 * @param loader
	 *            the loader to set
	 */
	public void setLoader(ClientLoader loader)
	{
		this.loader = loader;
	}
	
	@Override
	public void destroy()
	{
		super.destroy();
		loader = null;
	}

}
