from calculadora import Calculadora

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol

transport = TSocket.TSocket("localhost", 9090)
transport = TTransport.TBufferedTransport(transport)
protocol = TBinaryProtocol.TBinaryProtocol(transport)

client = Calculadora.Client(protocol)

transport.open()

# Operadores usados inicializados
Operadores = ['+','x','-','/']
Operadores2 = ['seno', 'coseno', 'tangente']

# Funciones auxiliares para comprobar si es float o int
def isFloat(num):
    try:
        float(num)
        return True
    except ValueError:
        return False

def isInt(num):
    try:
        int(num)
        return True
    except ValueError:
        return False

# Empieza 
while True:
    print("Calculadora, Seleccione una operacion\n")
    print("1 --> Calculadora simple: + - / x \n")
    print("2 --> Calculadora trigonometrica. \n")
    print("3 --> Calculadora de vectores\n")
    opcion = input("Opcion: ")

    #Comprueba si es int, lo transforma en int y si es valida esa opcion sale del bucle
    if isInt(opcion):
        opcion = int(opcion)
        if opcion > 0 and opcion <= 3:
            break
    

if opcion == 1:
         
    print("Los operadores válidos son: + - x /")
    print("Escriba la expresión separada por espacios: <numero1> <operador> <numero2>")

    while True:
        operacion = input("operacion: ")
        #lo separa en un vector, lo separa por espacios
        operacion = operacion.split()

        #comprueba si son float los numeros, el operador es valido y el numero de argumento
        #si esta bien sale del bucle
        if (len(operacion) == 3 and operacion[1] in Operadores and isFloat(operacion[0]) and isFloat(operacion[2])):
            break
        else:
            #Salta un mensaje error dependiendo del error que hayas cometido
            if len(operacion) != 3:
                print("Numeros de parametros incorrectos, use <numero1> <operador> <numero2>")
            else:
                if(not isFloat(operacion[0]) or not isFloat(operacion[2])):
                    print("No has introducido un numero en algun parametro")
                if operacion[1] not in Operadores:
                    print("Operador no valido")


    numero1 = float(operacion[0])
    operador = operacion[1]
    numero2 = float(operacion[2])

    #Elige una funcion dependiendo del operador introducido
    if operador == '+':
        print("Resultado = " + str(client.suma(numero1,numero2)))
    elif operador == '-':
        print("Resultado = " + str(client.resta(numero1,numero2)))
    elif operador == 'x':
        print("Resultado = " + str(client.multiplicacion(numero1,numero2)))
    elif operador == '/':
        print("Resultado = " + str(client.division(numero1,numero2)))

elif opcion == 2:       
    while True:
        print("Calculadora trigonometrica, Seleccione una operacion\n")
        print("1 --> seno, coseno o tangente\n")
        print("2 --> cambio de radianes a grados o viceversa \n")
        opcion = input("opcion: ")
        
        #comprobacion de opcion
        if isInt(opcion):
            opcion = int(opcion)
            if opcion == 1 or opcion == 2:
                break
        else:
            print("introduzca una opcion valida")

    if opcion == 1:
        print("los operadores válidos son: seno,coseno, tangente") 
        print("Escriba la expresion separada por espacios: <operador> <numero>") 

        while True:
            operacion = input("operacion: ")
            #separa los argumentos en un vector
            operacion = operacion.split()
            #comprueba si son dos elementos, operador valido y si es float el numero
            #sale si es valido todo
            if (len(operacion) == 2 and operacion[0] in Operadores2 and isFloat(operacion[1])):
                break
            else:
                #Sale un mensaje de error dependiendo del error cometido
                if len(operacion) != 2:
                    print("Numeros de parametros incorrectos, use <operador> <numero>")
                else:
                    if not isFloat(operacion[1]):
                        print("No has introducido un numero en valido")
                    if operacion[0] not in Operadores2:
                        print("Operador no valido")
    
        numero = float(operacion[1])
        operador = operacion[0]

        #elige funcion del cliente dependiendo del operador
        if operador == 'seno':
            print("Resultado = " + str(client.seno(numero)))
        elif operador == 'coseno':
            print("Resultado = " + str(client.coseno(numero)))
        elif operador == 'tangente':
            print("Resultado = " + str(client.tangente(numero)))

    elif opcion == 2:
        while True:

            print("Eliga una opcion")
            print("1 --> cambio de radianes a grados")
            print("2 --> cambio de grados a radianes")
            opcion = input("opcion: ")
            #comprueba si es una opcion valida
            if isInt(opcion):
                opcion = int(opcion)
                if opcion == 1 or opcion == 2:
                    break
            else:
                print("introduzca una opcion valida")
        
        while True:
            print("introduzca un numero")
            numero = input("numero: ")
            #comprueba que es float el numero introducido
            #si es valido sale del bucle
            if isFloat(numero):
                numero = float(numero)
                break
            else:
                print("introduzca un numero valido")
    #elige funcion clientre dependiendo de la opcion elegida
    if opcion == 1:
        print("Resultado = " + str(client.radianes_grados(numero)))
    elif opcion == 2:
        print("Resultado = " + str(client.grados_radianes(numero)))

elif opcion == 3:

    print("Seleccione una operacion:\n")
    print("1 --> Suma Vectores\n")
    print("2 --> Resta Vectores\n")
    print("3 --> Producto escalar\n")
    print("4 --> Producto por componentes\n")
    #comprueba opcion si es valida
    while True:
        opcion = input("operacion: ")

        if isInt(opcion):
            opcion = int(opcion)
            if  opcion > 0 and opcion <= 4:
                break
        else:
            #Mensaje de error correspondiente
            if isInt(opcion):
                print("No ha introducido un numero")
                if  opcion <= 0 or opcion > 4:
                    print("No has introducido un numero en valido")

    print("introduce la longitud de los vectores")

    #comprueba que introduce una longitud correcta
    while True:
        longitud = input("longitud: ")

        if isInt(longitud):
            longitud = int(longitud)
            break

    #inizialicacion de los vectores
    VectorA = []
    VectorB = []

    for i in range(longitud):
        #se introducen los numero despues de comprobrar que son validos
        while True:
            numero = input("numero " + str(i) + " del primer vector: ")
            if isFloat(numero):
                numero = float(numero)
                VectorA.append(numero)
                break
            else:
                print("no ha introducido un numero")

    for i in range(longitud):
        #se introducen los numero despues de comprobrar que son validos
        while True:
            numero = input("numero " + str(i) + " del segundo vector: ")
            if isFloat(numero):
                numero = float(numero)
                VectorB.append(numero)
                break
            else:
                print("no ha introducido un numero")

    #elige la funcion cliente correspondiente segun la opcion introducida
    if opcion == 1:
        print("Resultado = " + str(client.suma_vectores(VectorA,VectorB)))
    elif opcion == 2:
        print("Resultado = " + str(client.resta_vectores(VectorA,VectorB)))
    elif opcion == 3:
        print("Resultado = " + str(client.producto_escalar(VectorA,VectorB)))
    elif opcion == 4:
        print("Resultado = " + str(client.producto_vectores(VectorA,VectorB)))




transport.close()
