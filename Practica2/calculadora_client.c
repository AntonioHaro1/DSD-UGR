/*
* This is sample code generated by rpcgen.
* These are only templates and you can use them
* as a guideline for developing your own functions.
*/


#include "calculadora.h"


void
calculadora_basica(char *host, float numero1, float numero2, char operador)
{
 CLIENT *clnt;
 float  *result;


#ifndef DEBUG
 clnt = clnt_create (host, CALCULADORA, CALCULADORA_SIMPLE, "udp");
 if (clnt == NULL) {
     clnt_pcreateerror (host);
     exit (1);
 }
#endif  /* DEBUG */

    switch (operador){
        case '+':
            result = sumar_1(numero1,numero2, clnt);
            break;
        case '-':
            result = restar_1(numero1,numero2,clnt);
            break;
        case 'x':
            result = multiplicar_1(numero1,numero2,clnt);
            break;
        case '/':
            result = dividir_1(numero1,numero2,clnt);
            break;
        default:
            printf("Operador no válido.\n");
            result = NULL;
            break;
    }

    printf("Valor resultante: %f\n", *result);

#ifndef DEBUG
 clnt_destroy (clnt);
#endif   /* DEBUG */
}

void calculadora_vectores(char *host, VectorNode VectorA, VectorNode VectorB,int longitud, int opcion){
    CLIENT *clnt;
    struct VectorNode *result;

#ifndef DEBUG
    clnt = clnt_create (host, CALCULADORA, CALCULADORA_VECTORES, "udp");

    if (clnt == NULL) {
        clnt_pcreateerror (host);
        exit (1);
    }
#endif  /* DEBUG */

    switch (opcion){
        case 1:
            result = suma_vectores_2(VectorA,VectorB,longitud,clnt);
            break;
        case 2:
            result = resta_vectores_2(VectorA,VectorB,longitud,clnt);
            break;
        case 3:
            result = producto_escalar_2(VectorA,VectorB,longitud,clnt);
            break;
        case 4:
            result = producto_vectores_2(VectorA,VectorB,longitud,clnt);
            break;
        default:
            printf("Opcion no valida.\n");
            result = NULL;
            break;
    }
    if(opcion == 3){
        printf("\nValor resultante: %f",result->vector[0]);
    }else{
        printf("El vector resultante es: \n");
        for(int i= 0; i< longitud; i++){
            printf("\n%d: %f ", i+1,result->vector[i]);
        } 
    }
    printf("\n");
}


int operador_valido(char operador, const char *operadores, int tam)
{
    int resultado = 0;
    for (int i = 0; i < tam && resultado == 0; i++)
    {
        if(operador == operadores[i]){
            resultado = 1;
        }
    }
    return resultado;
}

int
main (int argc, char *argv[])
{
    char *host;

    if (argc != 2)
    {
        printf("Número de parámetros incorrecto\n");
        printf("Sintaxis: <programa> <servidor>\n");
        exit(1);
    }
    
    host = argv[1];

    system("clear");
    
    Eleccion:
        printf("Calculadora, Seleccione una operacion:\n");
        printf("1 --> Calculadora simple: + - / x \n");
        printf("2 --> Calculadora de vectores. \n");
  
    int opcion;
    scanf("%d", &opcion);

    if(opcion != 1 && opcion != 2){
        system("clear");
        while (getchar() != '\n');
        goto Eleccion;
    }


    switch (opcion)
    {
    case 1:
        printf("Calculadora Basica\n");
        printf("Los operadores válidos son: + - x /\n");
        printf("Escriba la expresión como se indica: <numero1><operador><numero2>\n");

        float numero1 = 0.0f, numero2 = 0.0f, resultado = 0.0f;
        const char operadores[4] = {'+', '-', 'x', '/'};
        char operador;

        do
        {            
            scanf("%f %c %f", &numero1, &operador, &numero2);
/*
            success = numero1;
            success2 = numero2;

            if (success != 1) {
                printf("Error: Entrada no válida. Introduce numeros.\n");
                while (getchar() != '\n');
            }
            if(success2 != 1){
                printf("Error: Entrada no válida. Introduce numeros.\n");
                while (getchar() != '\n');  
            }
*/
            if(!operador_valido(operador, operadores, 4))
            {
                printf("No se ha reconocido el operador introducido\n");
                printf("Los operadores válidos son: + - x / \n");
            }
        }while(!operador_valido(operador, operadores, 4));
       
        calculadora_basica(host, numero1, numero2, operador);

    break;
    
    case 2:
        printf("Calculadora Vectorial:\n");
        printf("El formato es primero introducir la longitud de los vectores(MAXIMO 20)\n");
        printf("Luego el primer Vector y luego el segundo vector, introduzca un numero, intro, numero\n");

        struct VectorNode VectorA;
        struct VectorNode VectorB;
        int longitud;

        do{
            scanf("%d" , &longitud);
            if(longitud > 20 || longitud < 1){
                printf("Longitud incorrecta, introduzca una longitud permitida: \n"); 
            }
        }while(longitud > 20 || longitud < 1);

        for(int i= 0; i< longitud; i++){
            int success;
            do {
                printf("%dº numero del primer vector: ", i + 1);
                success = scanf("%f", &VectorA.vector[i]);

                if (success != 1) {
                    printf("Error: Entrada no válida. Introduce un número.\n");
                    while (getchar() != '\n');
                }
            }while(success != 1);
        }

        for(int i= 0; i< longitud; i++){
            int success;
            do {
                printf("%dº numero del segundo vector: ", i + 1);
                success = scanf("%f", &VectorB.vector[i]);

                if (success != 1) {
                    printf("Error: Entrada no válida. Introduce un número.\n");
                    while (getchar() != '\n');
                }
            }while(success != 1);
        }


        int opcionVectores;
        while (TRUE) {
            printf("Seleccione una operacion:\n");
            printf("1 --> Suma Vectores\n");
            printf("2 --> Resta Vectores\n");
            printf("3 --> Producto escalar\n");
            printf("4 --> Producto por componentes\n");
            scanf("%d", &opcionVectores);

            if(opcionVectores == 1 || opcionVectores == 2 || opcionVectores == 3 || opcionVectores == 4) {
                break; 
            }else{
                printf("Opción no válida. Intente de nuevo.\n");
            }
        }
        calculadora_vectores(host, VectorA,VectorB,longitud,opcionVectores);

    break;

exit (0);
}
}




