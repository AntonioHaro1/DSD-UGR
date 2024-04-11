import glob
import sys
import math
import numpy as np

from calculadora import Calculadora

from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer

import logging

logging.basicConfig(level=logging.DEBUG)


class CalculadoraHandler:
    def __init__(self):
        self.log = {}

    def suma(self, n1, n2):
        print("sumando " + str(n1) + " con " + str(n2))
        return n1 + n2

    def resta(self, n1, n2):    
        print("restando " + str(n1) + " con " + str(n2))
        return n1 - n2
    
    def multiplicacion(self,n1,n2):
        print("multiplicando " + str(n1) + " con " + str(n2))
        return n1 * n2
    
    def division(self,n1,n2):
        if(n2 == 0):
            print("No se puede dividir entre cero el resultado es 0")
            return 0
        else:
            print("diviendo " + str(n1) + " con " + str(n2))
            return n1/n2
    
    def suma_vectores(self,VectorA,VectorB):
        result = []

        print("sumando vectores")

        for i in range(len(VectorA)):
            result.append(VectorA[i] + VectorB[i])
        
        return result
    
    def resta_vectores(self,VectorA,VectorB):
        result = []

        print("restando vectores")

        for i in range(len(VectorA)):
            result.append(VectorA[i] - VectorB[i])
        
        return result
       
    def producto_vectores(self,VectorA,VectorB):
        result = []

        print("multiplicando vectores")

        for i in range(len(VectorA)):
            result.append(VectorA[i] * VectorB[i])
        
        return result
    
    def producto_escalar(self,VectorA,VectorB):
        result = 0
        print("calculando el producto vectorial")
        for i in range(len(VectorA)):
            result += (VectorA[i] * VectorB[i])
        
        return result
    
    def seno(self,a):
        print("Realizando el seno de " + str(a))
        resultado = math.radians(a)
        return (math.sin(resultado  ))

    def coseno(self,a):
        print("Realizando el coseno de " + str(a))
        resultado = math.radians(a)
        return (math.cos(resultado))

    def tangente(self,a):
        print("Realizando la tangente de " + str(a))
        resultado = math.radians(a)
        return (math.tan(resultado)) 

    def grados_radianes(self,a):
        print("Pasando " + str(a) + " a radianes")
        return (a * (math.pi/180))

    def radianes_grados(self,a):
        print("Pasando " + str(a) + " a grados")
        return (a * (180/math.pi))



if __name__ == "__main__":
    handler = CalculadoraHandler()
    processor = Calculadora.Processor(handler)
    transport = TSocket.TServerSocket(host="127.0.0.1", port=9090)
    tfactory = TTransport.TBufferedTransportFactory()
    pfactory = TBinaryProtocol.TBinaryProtocolFactory()

    server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)

    print("iniciando servidor...")
    server.serve()
    print("fin")
