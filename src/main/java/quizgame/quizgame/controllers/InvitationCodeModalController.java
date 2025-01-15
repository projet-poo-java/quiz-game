package quizgame.quizgame.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class InvitationCodeModalController {
    @FXML private TextField codeField;
    private boolean confirmed = false;
    private String invitationCode;
    private String subject;

    public void initData(String subject) {
        this.subject = subject;
    }

    @FXML
    private void onSubmit() {
        if (codeField.getText() != null && !codeField.getText().trim().isEmpty()) {
            invitationCode = codeField.getText().trim();
            confirmed = true;
            closeModal();
        }
    }

    @FXML
    private void onCancel() {
        confirmed = false;
        closeModal();
    }

    private void closeModal() {
        ((Stage) codeField.getScene().getWindow()).close();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getInvitationCode() {
        return invitationCode;
    }
}
