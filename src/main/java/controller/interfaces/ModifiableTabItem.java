package controller.interfaces;

public interface ModifiableTabItem {
    void undo();
    void redo();
    String getRedo();
    String getUndo();
}
