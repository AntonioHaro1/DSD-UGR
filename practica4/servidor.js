import http from 'node:http';
//import url from 'node:url';
import { promises as fs, readFile} from 'node:fs';
import {join, extname} from 'node:path';
import {Server} from 'socket.io';
import { MongoClient } from 'mongodb';
//import { rejects } from 'node:assert';
//var mimeTypes = { "html": "text/html", "jpeg": "image/jpeg", "jpg": "image/jpeg", "png": "image/png", "js": "text/javascript", "css": "text/css", "swf": "application/x-shockwave-flash"};
//import socketio from 'socket.io';


var temp_maxima = 30;
var lum_maxima = 50;
var temp_actual = 0;
var lum_actual = 0;
var estado_persiana = 'abierta';
var estado_ac = 'apagado';



const httpServer = http
    .createServer((request, response) => {
        let {url} = request;
        if(url == '/') {
            url = '/client.html';
            const filename = join(process.cwd(), url);

            readFile(filename, (err, data) => {
                if(!err) {
                    response.writeHead(200, {'Content-Type': 'text/html; charset=utf-8'});
                    response.write(data);
                } else {
                    response.writeHead(500, {"Content-Type": "text/plain"});
                    response.write(`Error en la lectura del fichero: ${url}`);
                }
                response.end();
            });
        }
        else {
            console.log('Peticion invalida: ' + url);
            response.writeHead(404, {'Content-Type': 'text/plain'});
            response.write('404 Not Found\n');
            response.end();
        }
    });



MongoClient.connect("mongodb://localhost:27017").then((db) => {

    const dbo = db.db("practica4");
	const collection = dbo.collection("datos");
	
	const io = new Server(httpServer);
	//Funcion para insertar un mensaje con la fecha actual en la base de datos
	function insertarRegistro(mensaje) {
		// Obtener fecha y hora actual
		const fechaActual = new Date().toLocaleString();
	
		// Insertar documento en la colección de datos
		collection.insertOne({
			mensaje: mensaje,
			fecha: fechaActual
		})
		.then(() => console.log("Registro insertado en la base de datos"))
		.catch((error) => console.error("Error al insertar registro:", error));
	}
	//Funcion para devolver los registros
	function obtenerRegistros() {
		return new Promise((resolve, reject) => {
			collection.find({}).toArray()
				.then((registros) => {
					//console.log("Registros en la colección 'datos':", registros);
					// Crear un nuevo array con el mensaje y la fecha de cada registro
					const registrosFormateados = registros.map(registro => {
						return {
							mensaje: registro.mensaje,
							fecha: registro.fecha
						};
					});
					resolve(registrosFormateados); // Resuelve la promesa con los registros formateados
				})
				.catch((error) => {
					console.error("Error al obtener registros:", error);
					reject(error); // Rechaza la promesa en caso de error
				});
		});
	}


	io.on('connection',  async (client) => {

			// funcion que se ejecuta cuando el cliente pide los ultimos datos disponibles
			client.on('obtenerDatos', () => {
				io.emit('datosA', {
					temperatura: temp_actual,
					luminosidad: lum_actual,
					estado_persiana: estado_persiana,
					estado_ac: estado_ac
				});
			});
					
			// lo siguiente guarda los usuarios cuando se conectan
			const cAddress = client.request.socket.remoteAddress;
        	const cPort = client.request.socket.remotePort;
			// Guardo en la base de datos que se ha conectado el usuario X
			insertarRegistro(`El usuario con address: ${cAddress}:${cPort} se ha conectado`);
			const registors = await obtenerRegistros();
			io.emit('tomaregistros', registors);

			// Cuando se desconecta  lo registro en la base de datos
			client.on('disconnect', async () => {
				//console.log(`El usuario ${cAddress}:${cPort} se ha desconectado`);
				insertarRegistro(`El usuario con address: ${cAddress}:${cPort} se ha desconectado`);
				const registors = await obtenerRegistros();
				io.emit('tomaregistros', registors);
	
			});

			// funcion que se ejecuta cuando se reciben datos nuevos de los sensores
	    	client.on('datos_actualizados', async (data) => {
				console.log("recibo los datos de los sensores");
							
				temp_actual = data.temperatura;
				lum_actual = data.luminosidad;

				//Meto los cambio de temperatura en los registros
				insertarRegistro('La temperatura ha cambiado a ' + temp_actual);
				insertarRegistro('La luminosidad ha cambiado a ' + lum_actual);
				const registors = await obtenerRegistros();
				io.emit('tomaregistros', registors);

				// Compruebo si el agente cambia algo de los actuadores y en
				// caso positivo se lo notifico al usuario y lo registro
				if (temp_actual > temp_maxima && estado_ac == "apagado"){
					io.emit('encender_ac');
					estado_ac = 'encendido';
					//Meto en el registro y envio a todos los clientes
					insertarRegistro('El agente ha encendido el AC');
					const registors = await obtenerRegistros();
					io.emit('tomaregistros', registors);

				}else if(temp_actual <= temp_maxima && estado_ac == "encendido"){
					io.emit('apagar_ac');
					estado_ac = 'apagado';

					//Meto en el registro y envio a todos los clientes
					insertarRegistro('El agente ha apagado el AC');
					const registors = await obtenerRegistros();
					io.emit('tomaregistros', registors);
				}
				if (lum_actual > lum_maxima && estado_persiana == "abierta"){
					io.emit('cerrar_persiana');
					estado_persiana = 'cerrada';
					insertarRegistro('El agente ha cerrado la persiana');
					const registors = await obtenerRegistros();
					io.emit('tomaregistros', registors);
				}
				// Envio para que actualiza la advertencia siempre aunque no se
				// muestra si no sube del umbral
				io.emit('actualizar_advertencia_temp',temp_actual);
				io.emit('actualizar_advertencia_lum',lum_actual);

				// Actualizo las temperaturas de los clientes
				io.emit('actualizar',{
					temperatura: temp_actual,
					luminosidad: lum_actual,
				}
				);

			});


			// funcion que abre/cierra la persiana cuando lo solicita el cliente
			client.on('cambiar_estado_persiana', async () => {

				if (estado_persiana == 'abierta')
					estado_persiana = 'cerrada';
				else if(estado_persiana == 'cerrada')
					estado_persiana = 'abierta';


				insertarRegistro("Servidor: la persiana ha cambiado de estado");
				const registors = await obtenerRegistros();
				io.emit('tomaregistros', registors);
				
				io.emit('obtener_estado_persiana', estado_persiana);
			});

			// funcion que enciende/apaga el aire acondicionado cuando lo solicita el cliente
			client.on('cambiar_estado_ac', async () => {

				if (estado_ac == 'encendido')
					estado_ac = 'apagado';
				else
					estado_ac = 'encendido';

				
				insertarRegistro("Servidor: el aire acondicionado ha cambiado de estado");
				const registors = await obtenerRegistros();
				io.emit('tomaregistros', registors);

				io.emit('obtener_estado_ac', estado_ac);
			});


	    });

		 httpServer.listen(8080);
	}).catch((err) => {console.error(err);});

console.log("Servicio MongoDB iniciado");

