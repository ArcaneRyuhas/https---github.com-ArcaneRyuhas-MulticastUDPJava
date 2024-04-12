import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Scanner;

public class MulsticastUDPChat implements Runnable{

    private static MulticastSocket socket;
    private static volatile boolean alreadyLeaved;
    private static String usuario;

    @SuppressWarnings("deprecation")
    public static void main (String [] args){

        try {
            int puerto = 8080;
            InetAddress grupo = InetAddress.getByName("224.0.0.0");
            socket = new MulticastSocket(puerto);
            alreadyLeaved = false;

            socket.joinGroup(grupo);
            Scanner scan = new Scanner(System.in);
            Thread hiloDeLectura = new Thread(new MulsticastUDPChat());
            hiloDeLectura.start();

            System.out.println("Ingrese su nombre de usuario: ");
            usuario = scan.nextLine();
            System.out.println("Envié un mensaje al grupo: ");

            while (true){
                
                String msj = scan.nextLine();
                
                if(msj.equalsIgnoreCase("Adios")){
                    String mensajeFinal = usuario + ": Ha cerrado la conexión";
                    byte[] mensaje = mensajeFinal.getBytes();
                    DatagramPacket mensajeSalida = new DatagramPacket(mensaje, mensaje.length, grupo, puerto);
                    socket.send(mensajeSalida);


                    socket.leaveGroup(grupo);
                    alreadyLeaved = true;
                    break;
                } else{
                    
                    String mensajeFinal = usuario + ": " + msj;
                    byte[] mensaje = mensajeFinal.getBytes();
                    DatagramPacket mensajeSalida = new DatagramPacket(mensaje, mensaje.length, grupo, puerto);
                    socket.send(mensajeSalida);
                }
            }

            scan.close();
            socket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run () {
        byte[] buffer = new byte [1024];
        String linea;

        try {
            while (!alreadyLeaved) {
                DatagramPacket mensajeEntrada = new DatagramPacket(buffer, buffer.length);
                socket.receive(mensajeEntrada);

                linea = new String(mensajeEntrada.getData(), 0, mensajeEntrada.getLength());

                if(!linea.startsWith(usuario)){
                    System.out.println(linea);
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

            
    }
}
