import java.util.ArrayList;
import java.io.Serializable;

// Clase utilizada para representar a los clientes
public class Cliente implements Serializable{
    private String nombre;
    private String password;
    private ArrayList<Float> donaciones;
    private float donacionTotal;
    private int numeroDonaciones;
    private float donacionMaxima;

    Cliente(String nombre, String password)
    {
        this.nombre = nombre;
        this.password = password;
        this.donaciones = new ArrayList<>();
        this.donacionTotal = 0.0f;
        this.numeroDonaciones = 0;
        this.donacionMaxima = 0.0f;
    }

    public String obtenerNombre()
    {
        return nombre;
    }

    public String obtenerPassword()
    {
        return password;
    }

    public ArrayList<Float> obtenerHistorialDonaciones()
    {
        return donaciones;
    }

    public float obtenerUltimaDonacion()
    {
        return donaciones.get(donaciones.size() - 1);
    }

    public float obtenerDonacionTotal()
    {
        return donacionTotal;
    }

    public int obtenerNumeroDonaciones()
    {
        return numeroDonaciones;
    }

    public float obtenerDonacionMaxima()
    {
        return donacionMaxima;
    }

    public void cambiarNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public void cambiarPassword(String password)
    {
        this.password = password;
    }

    public void donar(float donacion)
    {
        this.donaciones.add(donacion);
        this.donacionTotal += donacion;

        if(donacion > this.donacionMaxima)
            this.donacionMaxima = donacion;
        
            this.incrementarNumeroDonaciones();
    }

    public void incrementarNumeroDonaciones()
    {
        this.numeroDonaciones++;
    }
}