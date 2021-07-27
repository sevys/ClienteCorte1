package Main.Controller;


import Main.Model.date;
import Main.Model.ThreadClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class Controller implements  Observer, Initializable {
    private Socket socket;
    private DataOutputStream bufferDeSalida = null;
    private DataInputStream bufferDeEntrada=null;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    @FXML
    private TextArea textArea;

    @FXML
    private ComboBox<String> usuarios;
    @FXML
    private Label conexion;

    @FXML
    private TextField txtEnviar;

    @FXML
    private Button btnEnviar;

    @FXML
    private Circle circle;
    boolean escuchando;

    ObservableList<String> mensajes = FXCollections.observableArrayList();




    @FXML
    void btnConectarOnMouseClicked() {
        //usuarios.setItems(conectados);
        date datos = new date();
        try {
            socket = new Socket(datos.getIp(), datos.getPuerto());
            //log.setText( "Creado");
            bufferDeSalida = new DataOutputStream(socket.getOutputStream());
            bufferDeSalida.writeUTF(datos.getNombre());
            bufferDeSalida.flush();

            System.out.println(datos.getNombre()+"  "+ datos.getIp());
            //solicitarConexion();
            ThreadClient cliente = new ThreadClient(socket, conexion, textArea, usuarios);
            cliente.addObserver( this);
            new Thread(cliente).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void escuchar() {
        try {
            while (escuchando) {
                Object aux = objectInputStream.readObject();
                if (aux != null) {
                    if (aux instanceof LinkedList) {
                        //Si se recibe una LinkedList entonces se procesa
                        ejecutar((LinkedList<String>)aux);
                    } else {
                        System.err.println("Se recibió un Objeto desconocido a través del socket");
                    }
                } else {
                    System.err.println("Se recibió un null a través del socket");
                }
            }
        } catch (Exception e) {
//
            e.printStackTrace();
        }
    }
    /**
     * Método que ejecuta una serie de instruccines dependiendo del mensaje que el cliente reciba del servidor.
     * @param lista
     */
    public void ejecutar(LinkedList<String> lista){
        date datos = new date();
        String identificador = datos.getNombre();
        // 0 - El primer elemento de la lista es siempre el tipo
        String tipo = lista.get(0);
        switch (tipo) {
            case "CONEXION_ACEPTADA":
                // 1      - Identificador propio del nuevo usuario
                // 2 .. n - Identificadores de los clientes conectados actualmente
                identificador = lista.get(1);
                //ventana.sesionIniciada(identificador);
                for(int i=2;i<lista.size();i++){
                    usuarios.setAccessibleText(lista.get(i));
                    //ventana.addContacto(lista.get(i));
                }
                break;
            case "NUEVO_USUARIO_CONECTADO":
                // 1      - Identificador propio del cliente que se acaba de conectar
                //ventana.addContacto(lista.get(1));
                usuarios.setAccessibleText(lista.get(1));
//                conectados.add(lista);
//                usuarios.setItems(conectados);
                break;
            case "USUARIO_DESCONECTADO":
                // 1      - Identificador propio del cliente que se acaba de conectar
                //ventana.eliminarContacto(lista.get(1));
                break;
            case "MENSAJE":
                // 1      - Cliente emisor
                // 2      - Cliente receptor
                // 3      - Mensaje
                //ventana.addMensaje(lista.get(1), lista.get(3));
                mensajes.add(lista.get(1)+": "+lista.get(3));
                break;
            default:
                break;
        }
    }

    public void solicitarConexion() throws IOException {
        date datos = new date();
        LinkedList<String> list = new LinkedList<>();
        list.add("Solicitud_Conexion");
        list.add(datos.getNombre());
        try {
            objectOutputStream.writeObject(list);
        } catch (IOException ex) {
            System.out.println("Error de lectura y escritura al enviar mensaje al servidor.");
        }
    }


    @FXML
    void btnEnviarOnMouseClicked() {
        date datos = new date();
        if (usuarios.getValue() == null){
            System.out.println("Seleccione a un destinatario");
        }else{
            try {
                bufferDeSalida.writeUTF(datos.getNombre()+":"+usuarios.getValue()+":"+txtEnviar.getText());
                //bufferDeSalida.writeUTF("30");
                bufferDeSalida.flush();
                txtEnviar.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnConectarOnMouseClicked();

    }


    @Override
    public void update(Observable o, Object arg) {
        String color = (String) arg;
        switch (color){
            case "1":
                circle.setFill(Color.RED);
                break;
            case "2":
                circle.setFill(Color.GREEN);
                break;
            case "3":
                circle.setFill(Color.BLUE);
                break;
            case "4":
                circle.setFill(Color.YELLOW);
                break;
        }
    }
    public void ponerButton(){
        URL linkEnviar = getClass().getResource("../iamgen/enviar.png");
        Image imagenNuevo = new Image(linkEnviar.toString(),24,24,false,true);

        btnEnviar.setGraphic((new ImageView(imagenNuevo)));
    }
}
