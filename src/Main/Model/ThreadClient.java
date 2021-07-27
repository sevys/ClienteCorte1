
package Main.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadClient extends Observable implements Runnable {
    private Socket socket;
    private DataInputStream bufferDeEntrada = null;
    private Label log;
    private TextArea textArea;
    private ComboBox<String> conectados;
    datEntregado dat = new datEntregado();
    ObservableList<String> obd = FXCollections.observableArrayList();
    ObservableList<datEntregado> list = FXCollections.observableArrayList();
    boolean aux = true;

    public ThreadClient(Socket socket, Label log, TextArea mens, ComboBox conectados) {
        this.socket = socket;
        this.log = log;
        this.textArea = mens;
        this.conectados = conectados;
    }

    public void run() {

        try {
            bufferDeEntrada = new DataInputStream(socket.getInputStream());

            String st = "";
            do {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextLong(1000L)+100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    st = bufferDeEntrada.readUTF();
                    System.out.println("\n* "+st+" *\n");

                    //System.out.println("Recibiendo los mensajes del Servidor --> "+st);
                    String[] array = st.split(":");

                    if (array[0].equals("Conectado")){
                        System.out.println(array[0]);
                        date datos = new date();
                        if (array[2].equals(datos.getNombre())){
                            //log.setText(st);
                            //ObservableList<String> name = FXCollections.observableArrayList();
                            System.out.println(array[1]);
                            dat.setId(datos.getNombre());
                            dat.setSms(array[1]);
                            //list ya tiene el mensaje, se compone asi "nombre(id): mensaje
                            list.add(dat);
                            System.out.println(list);
                            //aqui necesito ver para quien es el mensaje y imprimirle
                            textArea.setText(list.get(0).getSms());
                            //name.clear();
                            obd.clear();
                            for (int i =4; i < Arrays.stream(array).count(); i++){
                                if (datos.getNombre().equals(array[i])){
                                }else{
                                    obd.add(array[i]);
                                    conectados.setItems(obd);
                                }
                            }

                        }else {
                            //ObservableList<String> name = FXCollections.observableArrayList();
                            System.out.println(array[2]);
                            String sms = list.get(0).getSms();
                            dat.setId(datos.getNombre());
                            dat.setSms(sms+"\n"+"Se a Conectado: "+array[2]);
                            list.add(dat);
                            textArea.setText(list.get(0).getSms());
                            obd.clear();
                            for (int i =4; i < Arrays.stream(array).count(); i++){
                                if (datos.getNombre().equals(array[i])){
                                }else{
                                    obd.add(array[i]);
                                    conectados.setItems(obd);
                                }
                            }
                            //name.clear();
                        }
                    }else {
                        System.out.println("Insertando en su respectiva celda --> "+st);


                        for (int i=0; i < list.size(); i++) {
                            if (list.get(i).getId().equals(array[0])) {
                                String sms = list.get(i).getSms()+"\n"+array[0]+": "+array[2];
                                dat.setId(array[0]);
                                dat.setSms(sms);
                                textArea.setText(list.get(i).getSms());
                                aux = true;
                                i = list.size();
                            }else {
                                if (i == list.size()-1 || aux ){
                                    aux = false;
                                }
                            }

                            if (aux == false){
                                dat.setId(array[0]);
                                dat.setSms(array[0]+": "+array[2]);
                                list.add(dat);
                                textArea.setText(list.get(i).getSms());
                                aux = true;
                                i = list.size();
                            }
                        }
                    }
                    this.setChanged();
                    this.notifyObservers(st);

                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }while (!st.equals("FIN"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
