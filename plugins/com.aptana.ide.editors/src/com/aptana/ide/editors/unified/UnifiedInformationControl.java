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
package com.aptana.ide.editors.unified; 

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlExtension;
import org.eclipse.jface.text.IInformationControlExtension3;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.DefaultInformationControl.IInformationPresenter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.core.StringUtils;

/**
 * 
 */
public class UnifiedInformationControl implements IInformationControl, IInformationControlExtension, IInformationControlExtension3,  DisposeListener {

	/**
	 * Outer border thickness in pixels.
	 * @since 3.1
	 */
	private static final int OUTER_BORDER= 1;
	/**
	 * Inner border thickness in pixels.
	 * @since 3.1
	 */
	private static final int INNER_BORDER= 3;

	/** The control's shell */
	private Shell fShell;
	/** The control's text widget */
	private StyledText fText;
	//private Browser fText;
	/** The information presenter */
	private IInformationPresenter fPresenter;
	/** A cached text presentation */
	private TextPresentation fPresentation = new TextPresentation();
	/** The control width constraint */
	//private int fMaxWidth= -1;
	/** The control height constraint */
	private int fMaxHeight= -1;
	private String statusFieldText = null;
	/**
	 * The font of the optional status text label.
	 *
	 * @since 3.0
	 */
	private Font fStatusTextFont;

	/**
	 * Creates a default information control with the given shell as parent. The given
	 * information presenter is used to process the information to be displayed. The given
	 * styles are applied to the created styled text widget.
	 *
	 * @param parent the parent shell
	 * @param shellStyle the additional styles for the shell
	 * @param style the additional styles for the styled text widget
	 * @param presenter the presenter to be used
	 */
	public UnifiedInformationControl(Shell parent, int shellStyle, int style, IInformationPresenter presenter) {
		this(parent, shellStyle, style, presenter, null);
	}

	/**
	 * Creates a default information control with the given shell as parent. The given
	 * information presenter is used to process the information to be displayed. The given
	 * styles are applied to the created styled text widget.
	 *
	 * @param parent the parent shell
	 * @param shellStyle the additional styles for the shell
	 * @param style the additional styles for the styled text widget
	 * @param presenter the presenter to be used
	 * @param statusFieldText the text to be used in the optional status field
	 *                         or <code>null</code> if the status field should be hidden
	 * @since 3.0
	 */
	public UnifiedInformationControl(Shell parent, int shellStyle, int style, IInformationPresenter presenter, String statusFieldText) {
		GridLayout layout;
		GridData gd;

		fShell= new Shell(parent, SWT.NO_FOCUS | SWT.ON_TOP | shellStyle);
		Display display= fShell.getDisplay();
		fShell.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

		Composite composite= fShell;
		layout= new GridLayout(1, false);
		int border= ((shellStyle & SWT.NO_TRIM) == 0) ? 0 : OUTER_BORDER;
		layout.marginHeight= border;
		layout.marginWidth= border;
		composite.setLayout(layout);
		gd= new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gd);

		composite= new Composite(composite, SWT.NONE);
		layout= new GridLayout(1, false);
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		layout.verticalSpacing= 1;
		composite.setLayout(layout);
		gd= new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gd);
		composite.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		composite.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

		// Browser control
		//fText = new Browser(composite, style);
		
		// Text field
		fText= new StyledText(composite, SWT.MULTI | SWT.READ_ONLY | style);
		gd= new GridData(GridData.BEGINNING | GridData.FILL_BOTH);
		gd.horizontalIndent= INNER_BORDER;
		gd.verticalIndent= INNER_BORDER;
		fText.setLayoutData(gd);
		fText.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		fText.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		fText.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e)  {
				if (e.character == 0x1B) // ESC
				{
					fShell.dispose();
				}
			}

			public void keyReleased(KeyEvent e) {}
		});

		fPresenter= presenter;

		this.statusFieldText = statusFieldText;
		
		// Status field
		if (statusFieldText != null) {

			// Horizontal separator line
			Label separator= new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_DOT);
			separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			// Status field label
			Label statusField= new Label(composite, SWT.RIGHT);
			statusField.setText(statusFieldText);
			Font font= statusField.getFont();
			FontData[] fontDatas= font.getFontData();
			for (int i= 0; i < fontDatas.length; i++)
			{
				fontDatas[i].setHeight(fontDatas[i].getHeight() * 9 / 10);
			}
			fStatusTextFont= new Font(statusField.getDisplay(), fontDatas);
			statusField.setFont(fStatusTextFont);
			gd= new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
			statusField.setLayoutData(gd);

			statusField.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));

			statusField.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		}

		addDisposeListener(this);
	}

	/**
	 * Creates a default information control with the given shell as parent. The given
	 * information presenter is used to process the information to be displayed. The given
	 * styles are applied to the created styled text widget.
	 *
	 * @param parent the parent shell
	 * @param style the additional styles for the styled text widget
	 * @param presenter the presenter to be used
	 */
	public UnifiedInformationControl(Shell parent,int style, IInformationPresenter presenter) {
		this(parent, SWT.TOOL | SWT.NO_TRIM, style, presenter);
	}

	/**
	 * Creates a default information control with the given shell as parent. The given
	 * information presenter is used to process the information to be displayed. The given
	 * styles are applied to the created styled text widget.
	 *
	 * @param parent the parent shell
	 * @param style the additional styles for the styled text widget
	 * @param presenter the presenter to be used
	 * @param statusFieldText the text to be used in the optional status field
	 *                         or <code>null</code> if the status field should be hidden
	 * @since 3.0
	 */
	public UnifiedInformationControl(Shell parent, int style, IInformationPresenter presenter, String statusFieldText) {
		this(parent, SWT.TOOL | SWT.NO_TRIM, style, presenter, statusFieldText);
	}

	/**
	 * Creates a default information control with the given shell as parent.
	 * No information presenter is used to process the information
	 * to be displayed. No additional styles are applied to the styled text widget.
	 *
	 * @param parent the parent shell
	 */
	public UnifiedInformationControl(Shell parent) {
		this(parent, SWT.NONE, null);
	}

	/**
	 * Creates a default information control with the given shell as parent. The given
	 * information presenter is used to process the information to be displayed.
	 * No additional styles are applied to the styled text widget.
	 *
	 * @param parent the parent shell
	 * @param presenter the presenter to be used
	 */
	public UnifiedInformationControl(Shell parent, IInformationPresenter presenter) {
		this(parent, SWT.NONE, presenter);
	}

	/**
	 * Constructor
	 * @param parent
	 * @param presenter
	 * @param status
	 */
	public UnifiedInformationControl(Shell parent, IInformationPresenter presenter, String status) {
		this(parent, SWT.NONE, presenter, status);
	}

	/**
	 * @see IInformationControl#setInformation(String)
	 */
	public void setInformation(String content) {
		if (fPresenter == null) {
			fText.setText(content);
		} else {
			fPresentation.clear();
			content= fPresenter.updatePresentation(fShell.getDisplay(), content, fPresentation, 260, fMaxHeight);
			if (content != null) {
				fText.setText(content);
				TextPresentation.applyTextPresentation(fPresentation, fText);
			} else {
				fText.setText(StringUtils.EMPTY); 
			}
		}
	}

	/**
	 * @see IInformationControl#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
			fShell.setVisible(visible);
	}

	/**
	 * @see IInformationControl#dispose()
	 */
	public void dispose() {
		if (fShell != null && !fShell.isDisposed())
		{
			fShell.dispose();
		}
		else
		{
			widgetDisposed(null);
		}
	}

	/**
	 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
	 * @since 3.0
	 */
	public void widgetDisposed(DisposeEvent event) {
		if (fStatusTextFont != null && !fStatusTextFont.isDisposed())
		{
			fStatusTextFont.dispose();
		}

		fShell= null;
		fText= null;
		fStatusTextFont= null;
	}

	/**
	 * @see IInformationControl#setSize(int, int)
	 */
	public void setSize(int width, int height) {
		int extraLines = (statusFieldText == null) ? 1 : 2;
		int computedHeight = (fText.getLineCount() + extraLines) * fText.getLineHeight();
		fShell.setSize(width, computedHeight);
	}

	/**
	 * @see IInformationControl#setLocation(Point)
	 */
	public void setLocation(Point location) {
		Rectangle trim= fShell.computeTrim(0, 0, 0, 0);
		Point textLocation= fText.getLocation();
		location.x += trim.x - textLocation.x + INNER_BORDER;
		location.y += trim.y - textLocation.y + INNER_BORDER;
		fShell.setLocation(location);
	}

	/**
	 * @see IInformationControl#setSizeConstraints(int, int)
	 */
	public void setSizeConstraints(int maxWidth, int maxHeight) {
		//fMaxWidth= maxWidth;
		fMaxHeight= maxHeight;
	}

	/**
	 * @see IInformationControl#computeSizeHint()
	 */
	public Point computeSizeHint()
	{
		return fShell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	}

	/**
	 * @see org.eclipse.jface.text.IInformationControlExtension3#computeTrim()
	 * @since 3.0
	 */
	public Rectangle computeTrim() {
		return fShell.computeTrim(0, 0, 0, 0);
	}

	/**
	 * @see org.eclipse.jface.text.IInformationControlExtension3#getBounds()
	 * @since 3.0
	 */
	public Rectangle getBounds() {
		return fShell.getBounds();
	}

	/**
	 * @see org.eclipse.jface.text.IInformationControlExtension3#restoresLocation()
	 * @since 3.0
	 */
	public boolean restoresLocation() {
		return false;
	}

	/**
	 * @see org.eclipse.jface.text.IInformationControlExtension3#restoresSize()
	 * @since 3.0
	 */
	public boolean restoresSize() {
		return false;
	}

	/**
	 * @see IInformationControl#addDisposeListener(DisposeListener)
	 */
	public void addDisposeListener(DisposeListener listener) {
		fShell.addDisposeListener(listener);
	}

	/**
	 * @see IInformationControl#removeDisposeListener(DisposeListener)
	 */
	public void removeDisposeListener(DisposeListener listener) {
		fShell.removeDisposeListener(listener);
	}

	/**
	 * @see IInformationControl#setForegroundColor(Color)
	 */
	public void setForegroundColor(Color foreground) {
		fText.setForeground(foreground);
	}

	/**
	 * @see IInformationControl#setBackgroundColor(Color)
	 */
	public void setBackgroundColor(Color background) {
		fText.setBackground(background);
	}

	/**
	 * @see IInformationControl#isFocusControl()
	 */
	public boolean isFocusControl() {
		return fText.isFocusControl();
	}

	/**
	 * @see IInformationControl#setFocus()
	 */
	public void setFocus() {
		fShell.forceFocus();
		fText.setFocus();
	}

	/**
	 * @see IInformationControl#addFocusListener(FocusListener)
	 */
	public void addFocusListener(FocusListener listener) {
		fText.addFocusListener(listener);
	}

	/**
	 * @see IInformationControl#removeFocusListener(FocusListener)
	 */
	public void removeFocusListener(FocusListener listener) {
		fText.removeFocusListener(listener);
	}

	/**
	 * @see IInformationControlExtension#hasContents()
	 */
	public boolean hasContents() {
		return true; //TODO: fText.getCharCount() > 0;
	}
	
	/**
	 * Gets the current StyledText widget
	 * @return The styled text widget
	 */
	public StyledText getStyledTextWidget()
	{
		return this.fText;
	}
	
	/**
	 * Returns the current shell
	 * @return The current Shell
	 */
	public Shell getShell()
	{
		return this.fShell;
	}
}


