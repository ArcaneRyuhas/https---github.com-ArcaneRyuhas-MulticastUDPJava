import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Scanner;

public class MulticastUDP {
    
    @SuppressWarnings("deprecation")
    public static void main (String [] args){

        try {
            int puerto = 8080;
            InetAddress grupo = InetAddress.getByName("224.0.0.0");
            MulticastSocket socket = new MulticastSocket(puerto);

            socket.joinGroup(grupo);

            Scanner scan = new Scanner(System.in);
            System.out.println("Envié un mensaje al grupo: ");
            String msj = scan.nextLine();

            byte[] mensaje = msj.getBytes();
            DatagramPacket mensajeSalida = new DatagramPacket(mensaje, mensaje.length, grupo, puerto);
            socket.send(mensajeSalida);

            byte[] buffer = new byte [1024];
            String linea;

            while (true) {
                DatagramPacket mensajeEntrada = new DatagramPacket(buffer, buffer.length);
                socket.receive(mensajeEntrada);

                linea = new String(mensajeEntrada.getData(), 0, mensajeEntrada.getLength());
                System.out.println("Recibido: " + linea);

                if(linea.equalsIgnoreCase("Adios")){
                    socket.leaveGroup(grupo);
                    break;
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
}
