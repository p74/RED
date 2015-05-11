package org.robotframework.ide.eclipse.main.plugin.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.robotframework.ide.eclipse.main.plugin.debug.model.RobotLineBreakpoint;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.RobotFormEditor;

/**
 * @author mmarzec
 *
 */
public class RobotModelPresentation extends LabelProvider implements IDebugModelPresentation {

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.ui.IDebugModelPresentation#setAttribute(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void setAttribute(final String attribute, final Object value) {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage(final Object element) {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(final Object element) {
        try {
            if (element instanceof IThread) {
                return ((IThread) element).getName();
            } else if (element instanceof IDebugTarget) {
                return ((IDebugTarget) element).getName();
            } else if (element instanceof IStackFrame) {
                return ((IStackFrame) element).getName();
            } else if (element instanceof RobotLineBreakpoint) {
                final IMarker breakpointMarker = ((RobotLineBreakpoint) element).getMarker();
                final String file = breakpointMarker.getAttribute(IMarker.LOCATION, "");
                final Integer line = (Integer) breakpointMarker.getAttribute(IMarker.LINE_NUMBER);
                return file + " [line: " + line + "]";
            }
        } catch (final CoreException e) {
            e.printStackTrace();
        }

        return "RED";
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.debug.ui.IDebugModelPresentation#computeDetail(org.eclipse.debug.core.model.IValue
     * , org.eclipse.debug.ui.IValueDetailListener)
     */
    @Override
    public void computeDetail(final IValue value, final IValueDetailListener listener) {
        String detail = "";
        try {
            detail = value.getValueString();
        } catch (final DebugException e) {
        }
        listener.detailComputed(value, detail);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.ui.ISourcePresentation#getEditorInput(java.lang.Object)
     */
    @Override
    public IEditorInput getEditorInput(final Object element) {
        if (element instanceof IFile) {
            return new FileEditorInput((IFile) element);
        }
        if (element instanceof ILineBreakpoint) {
            return new FileEditorInput((IFile) ((ILineBreakpoint) element).getMarker().getResource());
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.debug.ui.ISourcePresentation#getEditorId(org.eclipse.ui.IEditorInput,
     * java.lang.Object)
     */
    @Override
    public String getEditorId(final IEditorInput input, final Object element) {
        if (element instanceof IFile || element instanceof ILineBreakpoint) {
            return RobotFormEditor.ID;
        }
        return null;
    }
}
