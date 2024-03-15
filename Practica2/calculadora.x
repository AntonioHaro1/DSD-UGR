/*
 * calculadora.x
 * Archivo de especificación de RPC para una calculadora simple
 */

program CALCULADORA{
    version CALCULADORA_SIMPLE{
        float SUMAR(float a, float b) = 1;
        float RESTAR(float a, float b)= 2;
        float MULTIPLICAR(float a,float b)= 3;
        float DIVIDIR(float a,float b) = 4;
    } = 1;
} = 0x20000001;
