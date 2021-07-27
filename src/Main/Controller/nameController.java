package Main.Controller;

import Main.Model.date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class nameController {
    @FXML
    private TextField nombre;
    @FXML
    Label sms;

    private String id;

    public void iniciar(ActionEvent eventbtn) throws IOException {

        if (nombre.getText().isEmpty()) {
            sms.setText("se necesita un nombre");
        }else {
            id = nombre.getText();
            System.out.println(id);
            date datos = new date();
            datos.setNombre(id);
            datos.setIp("localhost");
            datos.setPuerto(3001);

            Stage st;
            Parent root = FXMLLoader.load(getClass().getResource("../View/name.fxml"));

            Scene scene = new Scene(root);

            st = (Stage) ((Node) eventbtn.getSource()).getScene().getWindow();

            st.setScene(scene);
            st.setTitle("Tu chat");
            st.show();

//
        }

    }

}
