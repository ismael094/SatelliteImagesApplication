package utils;

import javafx.scene.control.Alert;


public class AlertFactory {
    public static void showErrorDialog(String title, String headerText, String contentText) {
        Alert alert = createDialog(Alert.AlertType.ERROR, title, headerText, contentText);
        alert.showAndWait();
    }

    public static void showInfoDialog(String title, String headerText, String contentText) {
        Alert alert = createDialog(Alert.AlertType.INFORMATION, title, headerText, contentText);
        alert.showAndWait();
    }

    public static void showSuccessDialog(String title, String headerText, String contentText) {
        Alert alert = createDialog(Alert.AlertType.CONFIRMATION, title, headerText, contentText);
        alert.showAndWait();
    }

    private static Alert createDialog(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert;
    }
}
