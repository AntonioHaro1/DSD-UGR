import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorReplica {
    public static void main(String[] args) {
        // Crea e instala el gestor de seguridad
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            String nombreServidor = "servidorReplica";
            String nombreReplica = "servidor";
            Registry reg=LocateRegistry.createRegistry(1098);
            DonacionesReplica donacionesReplica = new DonacionesReplica(nombreServidor, nombreReplica);
            Naming.rebind(nombreServidor, donacionesReplica);
            System.out.println("El servidor replica esta operativo");
        } catch (RemoteException | MalformedURLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}