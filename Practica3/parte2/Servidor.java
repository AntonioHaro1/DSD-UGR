import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Servidor {
    public static void main(String[] args) {
        // Crea e instala el gestor de seguridad
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            String nombreServidor = "servidor";
            String nombreReplica = "servidorReplica";
            Registry reg=LocateRegistry.createRegistry(1099);
            DonacionesServidor donacionesServidor = new DonacionesServidor(nombreServidor, nombreReplica);
            Naming.rebind(nombreServidor, donacionesServidor);
            System.out.println("El servidor principal esta operativo");
        } catch (RemoteException | MalformedURLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}