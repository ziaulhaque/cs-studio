package org.csstudio.opibuilder.runmode;

import org.csstudio.opibuilder.persistence.URLPath;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
/**
 * Factory for saving and restoring a <code>RunnerInput</code>. 
 * The stored representation of a <code>RunnerInput</code> remembers
 * the full path of the file (that is, <code>IFile.getFullPath</code>) and <code>
 * MacrosInput</code>.
 * <p>
 * The workbench will automatically create instances of this class as required.
 * It is not intended to be instantiated or subclassed by the client.
 * </p>
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 * @author Xihui Chen
 *
 */
public class RunnerInputFactory implements IElementFactory {

	private static final String ID_FACTORY = 
		"org.csstudio.opibuilder.runmode.RunnerInputFactory"; //$NON-NLS-1$
	
	private static final String TAG_PATH = "path"; //$NON-NLS-1$
	private static final String TAG_MACRO = "macro"; //$NON-NLS-1$


	public IAdaptable createElement(IMemento memento) {
		 // Get the file name.
        String pathString = memento.getString(TAG_PATH);
        if (pathString == null) {
			return null;
		}

        // Get a handle to the IFile...which can be a handle
        // to a resource that does not exist in workspace
        IPath path;
        if(ResourceUtil.isURL(pathString))
        	path = new URLPath(pathString);
        else
        	path = new Path(pathString);
        MacrosInput macrosInput = null;
        String macroString = memento.getString(TAG_MACRO);
        if(macroString != null)
			try {
				macrosInput = MacrosInput.recoverFromString(macroString);
			} catch (Exception e) {
				CentralLogger.getInstance().error(this, "Failed to recover macro", e);
			}       
		return new RunnerInput(path, null, macrosInput);
		
	}
	
	/**
     * Returns the element factory id for this class.
     * 
     * @return the element factory id
     */
    public static String getFactoryId() {
        return ID_FACTORY;
    }
    
    /**
     * Saves the state of the given RunnerInput into the given memento.
     *
     * @param memento the storage area for element state
     * @param input the opi runner input
     */
    public static void saveState(IMemento memento, RunnerInput input) {
        IPath path = input.getPath();
        memento.putString(TAG_PATH, path.toString());
        MacrosInput macros = input.getMacrosInput();
        if(macros != null)
        	memento.putString(TAG_MACRO, macros.toPersistenceString());
    }
}
