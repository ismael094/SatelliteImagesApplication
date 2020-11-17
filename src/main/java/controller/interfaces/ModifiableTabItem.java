package controller.interfaces;

/**
 * Interface to allow menu component to handle controllers with editable mode
 */
public interface ModifiableTabItem {
    /**
     * undo operation
     */
    void undo();

    /**
     * redo operation
     */
    void redo();

    /**
     * Name of the operation to redo
     * @return null if controller has not redo, or the name of the operation to redo
     */
    String getRedo();
    /**
     * Name of the operation to undo
     * @return null if controller has not undo, or the name of the operation to undo
     */
    String getUndo();
}


