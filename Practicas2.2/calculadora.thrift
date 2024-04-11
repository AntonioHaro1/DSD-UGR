service Calculadora{
    void ping(),
    double suma(1:double num1,2:double num2),
    double resta(1:double num1,2:double num2),
    double multiplicacion(1:double num1,2:double num2),
    double division(1:double num1,2:double num2),

    double seno(1:double num),
    double coseno(1:double num),
    double tangente(1:double num),
    double grados_radianes(1:double num),
    double radianes_grados(1:double num),

    list<double> suma_vectores(1: list<double> vec1, 2: list<double> vec2),
    list<double> resta_vectores(1: list<double> vec1, 2: list<double> vec2),
    list<double> producto_vectores(1: list<double> vec1, 2: list<double> vec2),
    double producto_escalar(1: list<double> vec1, 2: list<double> vec2),
}