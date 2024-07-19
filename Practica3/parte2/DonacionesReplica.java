import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.DrbgParameters.Reseed;
import java.util.HashMap;
import java.util.Map;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.ArrayList;

public class DonacionesReplica extends UnicastRemoteObject implements InterfazServidorCliente, InterfazServidorReplica {
    private String servidor;
    private String replica;
    private float subtotalDonado;

    private Map<String, Cliente> clientesRegistrados;

    // Constructor 
    public DonacionesReplica(String servidor, String replica) throws RemoteException {
        this.servidor = servidor;
        this.replica = replica;
        this.subtotalDonado = 0.0f;
        clientesRegistrados = new HashMap<>();
    }

    // Comprueba si un cliente ya esta registrado o en la replica 
    @Override
    public boolean registrado(String nombreCliente) throws RemoteException {
        boolean Registrado = false;

        InterfazServidorReplica servidorReplica = this.obtenerReplica("localhost", this.replica);

        if(this.existeCliente(nombreCliente) || servidorReplica.existeCliente(nombreCliente)){
            Registrado = true;
        }
        
        return Registrado;

    }

    // Realiza el registro de un cliente a partir de su nombre y password
    // El registro se realiza en el servidor donde haya menos clientes registrados
    // Devuelve true si el cliente se registra en uno de los servidores, false si no
    // se registra
    @Override
    public boolean realizarRegistro(String nombreCliente, String password) throws RemoteException {
        // Se comprueba si existe en este servidor
        boolean registrado = this.existeCliente(nombreCliente);
        boolean resultado = false;

        if (!registrado) {
            // Se comprueba si existe en la replica
            InterfazServidorReplica servidorReplica = this.obtenerReplica("localhost", this.replica);
            boolean registradoEnReplica = servidorReplica.existeCliente(nombreCliente);

            if (!registradoEnReplica) {
                int clientesEnServidor = this.obtenerNumeroClientesRegistrados();
                int clientesEnReplica = servidorReplica.obtenerNumeroClientesRegistrados();

                if (clientesEnServidor < clientesEnReplica)
                    this.confirmarRegistroCliente(nombreCliente, password, this.servidor);
                else
                    servidorReplica.confirmarRegistroCliente(nombreCliente, password, this.replica);

                resultado = true;
            }
        }

        return resultado;
    }

    // Realiza una donacion por parte de un cliente a partir de su nombre 
    @Override
    public void realizarDonacion(String nombreCliente, float donacion) throws RemoteException {
        String nombreServidor = identificarCliente(nombreCliente);
        if(nombreServidor.equals(this.replica)){
            InterfazServidorReplica servidorReplica = this.obtenerReplica("localhost", this.replica);
            servidorReplica.donar(nombreCliente, donacion);
        }else{
            this.donar(nombreCliente,donacion);
        }

    }
    // Inicia sesion 
    @Override
    public Boolean inicioSesion(String nombreCliente, String password) throws RemoteException {
        String nombreServidor = identificarCliente(nombreCliente);
        boolean identificado = false;

        if (nombreServidor.equals(this.replica)) {
            InterfazServidorReplica servidorReplica = this.obtenerReplica("localhost", this.replica);
            boolean registradoEnReplica = servidorReplica.existeCliente(nombreCliente);
            
            if(registradoEnReplica){
                if(servidorReplica.confirmarIdentificacionCliente(nombreCliente,password)){
                    identificado = true;
                }
            }

        } else if(nombreServidor.equals(this.servidor)) {
            boolean registrado = this.existeCliente(nombreCliente);
            
            if(registrado){
                if(this.confirmarIdentificacionCliente(nombreCliente,password)){
                    identificado = true;
                }
            }
        }

        return identificado;
    }


    // Devuelve el nombre del servidor en el que se ha registrado el cliente
    @Override
    public String identificarCliente(String nombreCliente) throws RemoteException {
        String nombreServidor = "";
        boolean registrado = this.existeCliente(nombreCliente);

        if (!registrado) {
            InterfazServidorReplica servidorReplica = this.obtenerReplica("localhost", this.replica);
            boolean registradoEnReplica = servidorReplica.existeCliente(nombreCliente);
            
            if(registradoEnReplica){

                nombreServidor = this.replica;
            }

        } else if(registrado) {
                nombreServidor = this.servidor;
        }

        return nombreServidor;
    }

    // Comprueba si existe el cliente en clientesRegistrados
    // Devuelve true si existe, false si no existe
    @Override
    public boolean existeCliente(String nombreCliente) throws RemoteException {
        return clientesRegistrados.containsKey(nombreCliente);
    }

    // Devuelve el numero de clientes registrados
    @Override
    public int obtenerNumeroClientesRegistrados() throws RemoteException {
        return clientesRegistrados.size();
    }

    
    

    // Comprueba que servidor esta el cliente registrado para buscar el subtotal del servidor
    @Override
    public float obtenerSubtotalDonado(String nombreCliente) throws RemoteException {
        String nombreServidor = identificarCliente(nombreCliente);
        float donado = 0.0f;

        if (nombreServidor.equals(this.replica)) {
            InterfazServidorReplica servidorReplica = this.obtenerReplica("localhost", this.replica);
            donado = servidorReplica.getSubtotalDonado();

        } else if(nombreServidor.equals(this.servidor)) {
            donado = this.getSubtotalDonado();
            
        }
        
        return donado;
    }

    // Devuelve el subtotaldonado
    @Override
    public float getSubtotalDonado()throws RemoteException {
        return this.subtotalDonado;
    }
    

    // Introduce a un cliente en clientesRegistrados
    @Override
    public void confirmarRegistroCliente(String nombreCliente, String password, String nombreServidor) throws RemoteException {
        clientesRegistrados.put(nombreCliente, new Cliente(nombreCliente, password));
        System.out.println("Se acaba de registrar el cliente " + nombreCliente);
    }

    // Devuelve una referencia a la replica del servidor, es una referencia a null si no se encuentra la replica
    @Override
    public InterfazServidorReplica obtenerReplica(String host, String nombreReplica) throws RemoteException {
        InterfazServidorReplica servidorReplica = null;

        try {
            Registry registroRMI = LocateRegistry.getRegistry(host, 1099);
            servidorReplica = (InterfazServidorReplica) registroRMI.lookup(nombreReplica);
        } catch (NotBoundException | RemoteException e) {
            System.out.println("No se encuentra el servidor replica con el nombre " + nombreReplica);
        }

        return servidorReplica;
    }

    // Devuelve el nombre del servidor
    @Override
    public String obtenerNombreServidor() throws RemoteException {
        return this.servidor;
    }

    // Incrementa el subtotal donado al servidor en una cantidad
    @Override
    public void incrementarSubtotalDonado(float donacion) throws RemoteException {
        this.subtotalDonado += donacion;
    }

    // Comprueba si el nombre y password de un cliente son correctos entre servidores
    @Override
    public boolean confirmarIdentificacionCliente(String nombreCliente, String password) throws RemoteException {
        String passwordCliente = clientesRegistrados.get(nombreCliente).obtenerPassword();

        return password.equals(passwordCliente);
    }

    // Obtiene la cantidad total donada por un cliente
    @Override
    public float obtenerDonacionCliente(String nombreCliente) throws RemoteException {
        String nombreServidor = identificarCliente(nombreCliente);

        if(nombreServidor.equals(this.replica)){
            InterfazServidorReplica servidorReplica = this.obtenerReplica("localhost", this.replica);
            Cliente cliente = servidorReplica.getClientesRegistrados().get(nombreCliente);
            return cliente.obtenerDonacionTotal();   
        }else if(nombreServidor.equals(this.servidor)){
            Cliente cliente = this.clientesRegistrados.get(nombreCliente);
            return cliente.obtenerDonacionTotal(); 
        }
        return 0;
    }

    // Obtiene el numero de donaciones que ha hecho un cliente
    @Override
    public int obtenerNumeroDonacionesCliente(String nombreCliente) throws RemoteException {
        String nombreServidor = identificarCliente(nombreCliente);

        if(nombreServidor.equals(this.replica)){
            InterfazServidorReplica servidorReplica = this.obtenerReplica("localhost", this.replica);
            Cliente cliente = servidorReplica.getClientesRegistrados().get(nombreCliente);
            return cliente.obtenerNumeroDonaciones();   
        }else if(nombreServidor.equals(this.servidor)){
            Cliente cliente = this.clientesRegistrados.get(nombreCliente);
            return cliente.obtenerNumeroDonaciones(); 
        }
        return 0;
    }



    // Obtiene la donacion maxima hecha por un cliente al servidor
    @Override
    public float obtenerDonacionMaximaCliente(String nombreCliente) throws RemoteException {
        String nombreServidor = identificarCliente(nombreCliente);

        if(nombreServidor.equals(this.replica)){
            InterfazServidorReplica servidorReplica = this.obtenerReplica("localhost", this.replica);
            Cliente cliente = servidorReplica.getClientesRegistrados().get(nombreCliente);
            return cliente.obtenerDonacionMaxima();   
        }else if(nombreServidor.equals(this.servidor)){
            Cliente cliente = this.clientesRegistrados.get(nombreCliente);
            return cliente.obtenerDonacionMaxima(); 
        }
        return 0;
    }

    // Obtiene el historial de donaciones de un cliente que ha hecho al servidor
    @Override
    public ArrayList<Float> obtenerHistorialDonaciones(String nombreCliente) throws RemoteException {
        String nombreServidor = identificarCliente(nombreCliente);

        if(nombreServidor.equals(this.replica)){
            InterfazServidorReplica servidorReplica = this.obtenerReplica("localhost", this.replica);
            Cliente cliente = servidorReplica.getClientesRegistrados().get(nombreCliente);
            return cliente.obtenerHistorialDonaciones();   
        }else if(nombreServidor.equals(this.servidor)){
            Cliente cliente = this.clientesRegistrados.get(nombreCliente);
            return cliente.obtenerHistorialDonaciones(); 
        }
        ArrayList<Float> lista = new ArrayList<Float>();
        return lista;
    }

    public Map<String, Cliente> getClientesRegistrados() {
        return clientesRegistrados;
    }
    // Donar en el servidor correspondiente cuando ya se ha comprobado en que servidor esta el cliente
    public void donar(String nombreCliente, float donacion) throws RemoteException{
        Cliente cliente = clientesRegistrados.get(nombreCliente);
        this.incrementarSubtotalDonado(donacion);
        cliente.donar(donacion);
        System.out.println("El cliente con el nombre " + nombreCliente + " ha donado " + donacion);
    }

    public float obtenerTotalDonadoServidores() throws RemoteException{
        InterfazServidorReplica servidorReplica = this.obtenerReplica("localhost", this.replica);
        return servidorReplica.getSubtotalDonado() + this.getSubtotalDonado();
    }
    // obtiene la lista de clientes que han donado en ambos servidores
    public ArrayList<Cliente> obtenerListeDonantes() throws RemoteException{
        InterfazServidorReplica servidorReplica = this.obtenerReplica("localhost", this.replica);
        Map<String,Cliente> clientesReplica = servidorReplica.getClientesRegistrados();

        Map<String,Cliente> clientesServidor = this.getClientesRegistrados();

        ArrayList<Cliente> listaClientes = new ArrayList<>();

          // Agregar los clientes de la réplica al ArrayList
        for (Cliente cliente : clientesReplica.values()) {
            if(cliente.obtenerNumeroDonaciones() > 0){
                listaClientes.add(cliente);
            }
        }

        // Agregar los clientes del servidor actual al ArrayList
        for (Cliente cliente : clientesServidor.values()) {
            if(cliente.obtenerNumeroDonaciones() > 0){
                listaClientes.add(cliente);
            }
        }

        return listaClientes;
    }
    //obtiene la lista de clientes que ordenado de mayor a menor segun el total donado
    public ArrayList<Cliente> obtenerRanking() throws RemoteException{
        InterfazServidorReplica servidorReplica = this.obtenerReplica("localhost", this.replica);
        Map<String,Cliente> clientesReplica = servidorReplica.getClientesRegistrados();

        Map<String,Cliente> clientesServidor = this.getClientesRegistrados();

        ArrayList<Cliente> listaClientes = new ArrayList<>();

        // Agregar los clientes de la réplica al ArrayList
        for (Cliente cliente : clientesReplica.values()) {
            if(cliente.obtenerNumeroDonaciones() > 0){
                listaClientes.add(cliente);
            }
        }

        // Agregar los clientes del servidor actual al ArrayList
        for (Cliente cliente : clientesServidor.values()) {
            if(cliente.obtenerNumeroDonaciones() > 0){
                listaClientes.add(cliente);
            }
        }

        listaClientes.sort((c1, c2) -> Float.compare(c2.obtenerDonacionTotal(), c1.obtenerDonacionTotal()));

            return listaClientes;
    }

    

}