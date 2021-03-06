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
package com.aptana.ide.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ServiceErrors extends CoreGroupObject<ServiceError> implements IServiceErrors
{

	/**
	 * ERRORS_ELEMENT
	 */
	public static final String ERRORS_ELEMENT = "errors"; //$NON-NLS-1$

	private int status = IServiceResponse.STATUS_UNSET;
	
	private String contents = ""; //$NON-NLS-1$

	/**
	 * Creates a new service errors group
	 */
	public ServiceErrors()
	{
		super();
		this.requireUniqueIds = false;
	}

	/**
	 * Adds an error
	 * 
	 * @param error
	 */
	public synchronized void addError(ServiceError error)
	{
		if (this.children != null)
		{
			this.children.add(error);
		}
	}

	/**
	 * @param newErrors
	 */
	public void cloneErrors(ServiceErrors newErrors)
	{
		if (newErrors != null)
		{
			for (ServiceError error : newErrors.getItems())
			{
				this.children.add(error);
			}
		}
	}

	/**
	 * @see com.aptana.ide.core.model.CoreModelObject#getLoggingPrefix()
	 */
	public String getLoggingPrefix()
	{
		return Messages.getString("ServiceErrors.Logging_Prefix"); //$NON-NLS-1$
	}

	/**
	 * Get the error strings
	 * 
	 * @return - string array for the content of each error
	 */
	public String[] getErrorStrings()
	{
		List<String> errors = new ArrayList<String>();
		for (ServiceError error : getItems())
		{
			if (error != null && error.getMessage() != null)
			{
				errors.add(error.getMessage());
			}
		}
		return errors.toArray(new String[0]);
	}

	/**
	 * @param item
	 * @return - true if the item should be added
	 * @see com.aptana.ide.core.model.CoreGroupObject#shouldAdd(com.aptana.ide.core.model.CoreModelObject)
	 */
	public boolean shouldAdd(ServiceError item)
	{
		return item.getMessage() != null;
	}

	/**
	 * @see com.aptana.ide.core.model.CoreGroupObject#createEmptyArray()
	 */
	protected ServiceError[] createEmptyArray()
	{
		return new ServiceError[0];
	}

	/**
	 * @see com.aptana.ide.core.model.CoreGroupObject#createItem()
	 */
	public ServiceError createItem()
	{
		return new ServiceError();
	}

	/**
	 * @see com.aptana.ide.core.model.CoreGroupObject#getGroupString()
	 */
	protected String getGroupString()
	{
		return ERRORS_ELEMENT;
	}

	/**
	 * @see com.aptana.ide.core.model.IServiceErrors#getStatus()
	 */
	public int getStatus()
	{
		return this.status;
	}

	/**
	 * @see com.aptana.ide.core.model.IServiceErrors#getContents()
	 */
	public String getContents()
	{
		return this.contents;
	}
	
	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status)
	{
		this.status = status;
	}

	/**
	 * @param contents
	 *            the contents to set
	 */
	public void setContents(String contents)
	{
		this.contents = contents;
	}
	
	/**
	 * @see com.aptana.ide.core.model.CoreGroupObject#getItemString()
	 */
	protected String getItemString()
	{
		return ServiceError.ERROR_ELEMENT;
	}

}
