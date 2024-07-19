import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

// Interfaz utilizada por los servidores

public interface InterfazServidorReplica extends Remote {

    // Comprueba si un cliente estar registrado en el servidor a partir de su nombre
    // Devuelve true si existe, false si no existe
    public boolean existeCliente(String nombreCliente) throws RemoteException;

    
    // Devuelve el nombre del servidor donde esta registrado para seguir comunicandose con ese servidor 
    public String identificarCliente(String nombreCliente) throws RemoteException;

    // Devuelve el numero de clientes registrados en el servidor
    // Necesario para hacer los registros en el servidor con menor numero de clientes
    public int obtenerNumeroClientesRegistrados() throws RemoteException;

    // Confirma el registro de un cliente en un servidor a partir de su nombre, password y nombre del servidor
    public void confirmarRegistroCliente(String nombreCliente, String password, String nombreServidor) throws RemoteException;

    // Devuelve una referencia a la replica del servidor a partir del host y el nombre del servidor replica
    public InterfazServidorReplica obtenerReplica(String host, String nombreReplica) throws RemoteException;

    // Devuelve el nombre del servidor en el rmiregistry
    public String obtenerNombreServidor() throws RemoteException;

    // Incrementa el subtotal donado en el servidor
    public void incrementarSubtotalDonado(float donacion) throws RemoteException;
    // Obtiene el subtotalDonado
    public float getSubtotalDonado() throws RemoteException;

    // Confirma si un cliente se ha identificado correctamente entre servidores
    // Devuelve true si su nombre y password son correctas
    public boolean confirmarIdentificacionCliente(String nombreCliente, String password) throws RemoteException;
    
    //Obtiene los clientes registrados
    public Map<String, Cliente> getClientesRegistrados() throws RemoteException;

    // Donar segun el nombre del cliente y la donacion introducida
    public void donar(String nombreCliente, float donacion) throws RemoteException;
}