/*
 * calculadora.x
 * Archivo de especificación de RPC para una calculadora simple
 */

struct VectorNode {
    float vector[20]; 
};
typedef struct VectorNode *VectorList;

program CALCULADORA{
    version CALCULADORA_SIMPLE{
        float SUMAR(float a, float b) = 1;
        float RESTAR(float a, float b) = 2;
        float MULTIPLICAR(float a, float b) = 3;
        float DIVIDIR(float a, float b) = 4;
    } = 1;

    version CALCULADORA_VECTORES{
        VectorNode SUMA_VECTORES(VectorNode VectorA, VectorNode VectorB,int longitud) = 5;
        VectorNode RESTA_VECTORES(VectorNode VectorA, VectorNode VectorB, int longitud) = 6;
        float PRODUCTO_ESCALAR(VectorNode VectorA, VectorNode VectorB, int longitud) = 7;
        VectorNode PRODUCTO_VECTORES(VectorNode VectorA, VectorNode VectorB, int longitud) = 8;
    } = 2;
} = 0x20000004;
