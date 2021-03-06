/* ***** BEGIN LICENSE BLOCK *****
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
 * 
 * Contributor(s):
 *     Max Stepanov (Aptana, Inc.)
 *
 * ***** END LICENSE BLOCK ***** */

(function() {

// ************************************************************************************************

const nsIWebProgress = Components.interfaces.nsIWebProgress;
const nsIWebProgressListener = Components.interfaces.nsIWebProgressListener;

// ************************************************************************************************

this.EXTENSION_ID = "firebug@software.joehewitt.com";

var acceptNextContext = false;
var attachNextContext = true;

var releaseContext = false;
var topWindow;
var browser;
var currentUrl;
var currentContext = null;

var DebuggerListener;
var XHRSpyListener;
var ConsoleListener;
var fb14p;
var fb143p;
var fb15p;
var fb16p;
var firebug_unMinimize;

const self = this;

function deinit()
{
	Firebug.Console.removeListener(ConsoleListener);
	Firebug.Spy.removeListener(XHRSpyListener);
	Firebug.Debugger.removeListener(DebuggerListener);
	if (fb16p) {
		Firebug.PanelActivation.setPanelState(Firebug.ScriptPanel, false);
		Firebug.PanelActivation.setPanelState(Firebug.ConsolePanel, false);
		Firebug.PanelActivation.setPanelState(Firebug.getPanelType("net"), false);
	}
}

this.setHook("init",function(debuggr)
{
	fb14p = (AptanaUtils.compareVersion(Firebug.version.substr(0,3), "1.4") >= 0);
	fb143p = (AptanaUtils.compareVersion(Firebug.version.substr(0,5), "1.4.3") >= 0);
	fb15p = (AptanaUtils.compareVersion(Firebug.version.substr(0,3), "1.5") >= 0);
	fb16p = (AptanaUtils.compareVersion(Firebug.version.substr(0,3), "1.6") >= 0);
	const AptanaDebuggerExtension = FBL.extend(Firebug.Extension,
	{
		// Firebug 1.4
		shouldCreateContext: function(_browser, url, userCommands)
		{
			var win = _browser.contentWindow;			
			if ( acceptNextContext ) {
				acceptNextContext = false;
				if ( debuggr.acceptWindow(self,win) ) {
					topWindow = win;
					browser = _browser;
					addTabCloseListener();
					currentUrl = url;
					return true;
				} else {
					deinit();
				}
			} else if ( topWindow == win && browser == _browser ) {
				if ( currentContext != null && releaseContext ) {
					if ( debuggr.acceptWindow(self,win) ) {
						currentUrl = url;
						return true;
					}
				} else if (fb143p && currentContext == null && !releaseContext) {
					return true;
				}
			} else if ( attachNextContext ) {
				attachNextContext = false;
				browser = _browser;
				addTabCloseListener();
				attachLoadingWindow(win);
			}
			return false;
		},
		
		shouldNotCreateContext: function(_browser, url, userCommands)
		{
			if ( topWindow ) {
				return true;
			}
			return false;
		},

		shouldShowContext: function(context)
		{
			if ( context == currentContext && currentContext ) {
				return true;
			}
			return false;
		},
		
		// Firebug 1.2/1.3
		acceptContext: function(win,uri)
		{
			if ( acceptNextContext ) {
				acceptNextContext = false;
				if ( debuggr.acceptWindow(self,win) ) {
					topWindow = win;
					browser = AptanaUtils.getBrowserByWindow(win);
					addTabCloseListener();
					currentUrl = uri.prePath+uri.path;
					return true;
				} else {
					deinit();
				}
			} else if ( topWindow == win && browser == AptanaUtils.getBrowserByWindow(win) ) {
				if ( currentContext != null && releaseContext ) {
					if ( debuggr.acceptWindow(self,win) ) {
						currentUrl = uri.prePath+uri.path;
						return true;
					}
				}
			} else if ( attachNextContext ) {
				attachNextContext = false;
				browser = AptanaUtils.getBrowserByWindow(win);
				addTabCloseListener();
				attachLoadingWindow(win);
			}
			return false;
		},

		declineContext: function(win,uri)
		{
			if ( topWindow ) {
				return true;
			}
			return false;
		},
		
		initContext: function(context)
		{
			if ( topWindow && context.window == topWindow ) {
				currentContext = context;
				releaseContext = false;
				attachNextContext = false;
				debuggr.onInit(self);
				//browser.showFirebug = false;
			}
		},
		
		destroyContext: function(context)
		{
			if ( context == currentContext && currentContext ) {
				releaseContext = true;
				debuggr.onDestroy(self);
			}
		},
		
		loadedContext: function(context)
		{
			if ( context == currentContext && currentContext ) {
				debuggr.onLoaded(currentUrl);
				delete currentUrl;
				if (fb14p && !Firebug.isMinimized() && !Firebug.isDetached()) {
					Firebug.setPlacement("minimized");
				}
			}
		},
		
		watchWindow: function(context, win)
		{
			if ( context == currentContext && currentContext ) {
				debuggr.attachToWindow(win);
			}
		},
		
		unwatchWindow: function(context, win)
		{
			if ( context == currentContext && currentContext ) {
				debuggr.detachFromWindow(win);
			}
		},
		
		showContext: function(_browser, context)
		{
			var show = (context == currentContext && currentContext != null) || "__aptanaAttached" in _browser;
			if (show && Firebug.getSuspended()) {
				Firebug.resume();
			}
			self.onShow(show);
		}
	});

	DebuggerListener = FBL.extend(Firebug.DebuggerListener,
	{
		onStop: function(context, frame, type, rv)
		{
			if ( context == currentContext ) {
				context.hideDebuggerUI = true;
				return debuggr.onStop(self, frame, type, rv);
			}
		},
		
		onResume: function(context)
		{
			if ( context == currentContext && currentContext ) {
				//if ( releaseContext )
				//	currentContext = null;
				debuggr.onResume();
				context.hideDebuggerUI = false;
			}
		},

		onThrow: function(context, frame, rv)
		{
			if ( context == currentContext && currentContext ) {
				return debuggr.onThrow(frame,rv);
			}			
		},
		
		onError: function(context, frame, error)
		{
			if ( context == currentContext && currentContext ) {
				return debuggr.onError(frame,error);
			}			
		},
		
		onTopLevelScriptCreated: function(context, frame, href)
		{
			if ( context == currentContext && currentContext ) {
				debuggr.onTopLevel(frame, href);
			}			
		},
		
		onStartDebugging: function(context)
		{
			if ( context == currentContext ) {
				context.hideDebuggerUI = false;
			}	
		}
	});

	ConsoleListener = FBL.extend(Firebug.ConsoleListener,
	{
		log: function(context, object, className, sourceLink)
		{
			if ( context == currentContext && currentContext ) {
				log(object,className,sourceLink);
			}
		},
		
		logFormatted: function(context, objects, className, sourceLink)
		{
			if ( context == currentContext && currentContext ) {
				log(objects,className,sourceLink);
			}
		}
	});
	
	XHRSpyListener = FBL.extend(Firebug.XHRSpyListener,
	{
		onStart: function(context, spy)
		{
			if ( context == currentContext && currentContext ) {
				spy.id = AptanaUtils.genUID();
				debuggr.xhrStart(spy.id, spy.method, spy.href, spy.postText ? spy.postText : "", spy.requestHeaders);
			}
		},
		
		onLoad: function(context, spy)
		{
			if ( context == currentContext && currentContext ) {
				debuggr.xhrLoad(spy.id, spy.statusCode, spy.statusText, spy.responseText, spy.responseHeaders);
			}
		},
	});
	
	function log(object,className,sourceLink)
	{
		var type = "out";
		var message = object;
		if ( className == "error" )
			type = "err";
		if (fb16p) {
			if ( typeof(object) != "string" && "trace" in object ) {
				debuggr.log(type,"Stack trace:",convertStackTrace(object.trace.frames));
				return;
			}			
		}
		if ( typeof(object) != "string" && "frames" in object ) {
			debuggr.log(type,"Stack trace:",convertStackTrace(object.frames));
			return;
		}
		if (fb16p) {
			if (object instanceof Firebug.Spy.XMLHttpRequestSpy)
				return;
			if (object instanceof FBL.ErrorMessage) {
				message = object.message;
			}
		} else {
			if ((object instanceof Firebug.Spy.XMLHttpRequestSpy) || (object instanceof FBL.ErrorMessage)) {
				return;
			}
		}
		try {
			if ( typeof(object) != "string" && object[0] )
				message = object[0];
		} catch(exc) {
		}
		debuggr.log(type,message);
	}
		
	function addTabCloseListener()
	{
		browser._destroyFunc = browser.destroy;
		browser.destroy = function() {
			debuggr.shutdown();
			if(this._destroyFunc) {
				this.destroy = this._destroyFunc;
				this.destroy();
			}
		}
	}
			
	function attachLoadingWindow(win)
	{
		var frameProgressListener = new AptanaUtils.WebProgressListener();
		frameProgressListener.onStateChange = function(aWebProgress, aRequest, aStateFlags, aStatus) {
			if ( aStateFlags & (nsIWebProgressListener.STATE_START|nsIWebProgressListener.STATE_TRANSFERRING) ) {
				debuggr.attachToWindow(aWebProgress.DOMWindow);
			}
		};

		var _browser = AptanaUtils.getBrowserByWindow(win);		
		if ( !_browser.__aptanaAttached ) {
			_browser.addProgressListener(frameProgressListener, nsIWebProgress.NOTIFY_STATE_ALL);
			_browser.__aptanaAttached = true;
		}	
	}
	
	function convertStackTrace(fbFrames)
	{
		var frames = [];	
		for (var i = 0; i < fbFrames.length; ++i )
		{
			var frame = fbFrames[i];
			if (frame.href.indexOf("chrome:") != 0)
			{
				var functionName = frame.script.functionName;
				if ( functionName == "null" )
					functionName = "";
				var functionArguments = "...";
				if ( frame.args ) {
					functionArguments = [];
					for( var j = 0; j < frame.args.length; ++j )
						functionArguments.push(frame.args[j].name);
					functionArguments = functionArguments.join(", ");
				}
				frames.push({
					functionName: functionName,
					fileName: (fb16p) ? frame.script.fileName : frame.href,
					lineNumber: (fb16p) ? frame.line : frame.lineNo,
					functionArguments: functionArguments
				});
			}
		}
		return frames;
	}

	const AptanaEditor = {
		id: "#AptanaEditor",
		label: "Aptana Studio <current>",
		image: "chrome://aptanadebugger/skin/aptana1616.png",
		handler: function(location)
		{
			debuggr.openInEditor(location);
		}
	};
	
	if (fb14p) {
		if (fb143p) {
			Firebug.Activation.toggleAll("off");
		} else {
			Firebug.toggleAll("off");
		}
	}
	Firebug.registerExtension(AptanaDebuggerExtension);
	Firebug.Console.addListener(ConsoleListener);
	Firebug.Spy.addListener(XHRSpyListener);
	Firebug.registerEditor(AptanaEditor);
	
	if (!fb15p)
		Firebug.Console.isHostEnabled = 
		Firebug.Debugger.isHostEnabled = 
		Firebug.NetMonitor.isHostEnabled = 
		function(context) {
			return (topWindow && context.window == topWindow);
		};
	
	if (fb16p) {
		Firebug.PanelActivation.setPanelState(Firebug.ScriptPanel, true);
		Firebug.PanelActivation.setPanelState(Firebug.ConsolePanel, true);
		Firebug.PanelActivation.setPanelState(Firebug.getPanelType("net"), true);
	} else if (fb14p) {
		Firebug.Console.isAlwaysEnabled = 
		Firebug.Debugger.isAlwaysEnabled = 
		Firebug.NetMonitor.isAlwaysEnabled = 
		function() {
			return true;
		};
	}
	
	if (fb15p) {
		firebug_unMinimize = Firebug.unMinimize;
		Firebug.unMinimize = function() {
			if (!currentContext || !currentContext.hideDebuggerUI)
				firebug_unMinimize.apply(Firebug);
		}
	}	
});

function removeTabCloseListener()
{
	if (browser && browser._destroyFunc)
	{
		browser.destroy = browser._destroyFunc;
		delete browser._destroyFunc;
	}
}

this.setHook("shutdown",function()
{
	if ((currentContext == null) || releaseContext) {
		deinit();
		return true;
	}
	return false;
});

this.setHook("enable",function()
{
	acceptNextContext = true;
	attachNextContext = false;
	Firebug.Debugger.addListener(DebuggerListener);
});

this.setHook("disable",function()
{
	acceptNextContext = false;
	Firebug.Debugger.removeListener(DebuggerListener);
	if ( topWindow && currentContext && !releaseContext ) {
		TabWatcher.unwatchTopWindow(topWindow);
	}
	topWindow = null;
	removeTabCloseListener();
	browser = null;
});

this.setHook("openURL",function(url)
{
	if (fb143p)
		Firebug.Activation.removePageAnnotation(url);
});

this.setHook("suspend",function()
{
	Firebug.Debugger.suspend(currentContext);
});

this.setHook("resume",function()
{
	Firebug.Debugger.resume(currentContext);
});

this.setHook("abort",function()
{
	Firebug.Debugger.abort(currentContext);
});

this.setHook("stepInto",function()
{
	Firebug.Debugger.stepInto(currentContext);
});

this.setHook("stepOver",function()
{
	Firebug.Debugger.stepOver(currentContext);
});

this.setHook("stepReturn",function()
{
	Firebug.Debugger.stepOut(currentContext);
});

this.setHook("stepToFrame",function(frame)
{
	currentContext.debugFrame = frame;
	Firebug.Debugger.stepOut(currentContext);
});

this.setHook("setBreakpoint",function(href, line, props)
{
	var sourceFile = getSourceFileByHref(href);
	if (!sourceFile) {
		namespace = fb15p ? Firebug : FBL
		sourceFile = new namespace.NoScriptSourceFile(currentContext, href);
	}
	return Firebug.Debugger.fbs.setBreakpoint(sourceFile, line, props, Firebug.Debugger);
});

function getSourceFileByHref(href) {
	return fb14p ?
		FBL.getSourceFileByHref(href, currentContext) :
		FBL.getScriptFileByHref(href, currentContext)
}
this.setHook("clearAllBreakpoints",function(hrefs)
{
	var sourceFiles = [];
	for (var i = 0 ; i < hrefs.length; ++i) {
		var sourceFile;
		try {
			sourceFile = getSourceFileByHref(hrefs[i]);
		} catch(exc) {}
		if (!sourceFile) {
			namespace = fb15p ? Firebug : FBL
			sourceFile = new namespace.NoScriptSourceFile(currentContext, hrefs[i]);
		}
		sourceFiles.push(sourceFile);
		Firebug.Debugger.fbs.clearAllBreakpoints([sourceFile]);
	}
	return Firebug.Debugger.fbs.clearAllBreakpoints(sourceFiles);
});

this.setHook("getSourceLines",function(href)
{
	return currentContext.sourceCache.load(href, "GET");
});

// ************************************************************************************************

function ddd(text)
{
	AptanaLogger.logError(text);
}

function dd(text)
{
	AptanaLogger.logConsole(text);
}

// ************************************************************************************************

}).apply(AptanaDebugger);
