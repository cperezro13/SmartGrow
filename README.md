# Proyecto SmartGrow
Esta aplicación es un sistema de cuidado de plantas que permite a los usuarios crear y gestionar un jardín virtual. Utiliza Firebase para la autenticación y almacenamiento, integra el reconocimiento de plantas mediante una API y TensorFlow Lite, y envía notificaciones para recordar el riego oportuno.<br/>
Toda la informacion de la aplicacion se encuentra en el siguiente documento:<br/>
https://docs.google.com/document/d/1lzc8PjHaTsfgDxGshXcekS7u7Q-o6BncgWY1GTJy8vs/edit?usp=sharing<br/>
Y este es un video con la explicacion de la app, una muestra de funcionamiento y explicacion del codigo:<br/>
https://www.youtube.com/watch?v=3cnEqp80PaM<br/>
</pre>
# Historias de usuario  
<pre>Sprint	 		Historia de usuario
	
  			1.El cliente quiere ver una estructura básica de la aplicación móvil para establecer sus expectativas
	
				1.1	Desarrollar los mockups incluyendo el splashscreen, login, home, pantalla de pedidos, pantalla de comentarios y pantalla de configuración
	
				1.2	Agregar en los mockups los textos de la aplicación				
	
				1.3	Agregar en los mockups las imagenes de la aplicación		
	

#1			2.El cliente quiere que se escoja la paleta de colores y tipo de letra para definir su identidad empresarial
	
				2.1	Determinar la paleta de colores mas conveniente de acuerdo a la tienda			Elegir el color verde para una aplicación sobre plantas puede ayudar a transmitir la esencia de la naturaleza, la vitalidad y la 																	tranquilidad, además de hacer que la aplicación sea fácilmente reconocible y agradable visualmente para los usuarios.
	
				2.2	Determinar el tipo de letra a utilizar en la aplicación					Aileron puede ser una excelente opción para una aplicación sobre plantas debido a su estilo moderno y limpio, su legibilidad en pantallas 																digitales, su variedad de estilos y su capacidad para mantener la consistencia de la marca.

  			3.El cliente quiere desarrollar un logo y un eslogan para dar a conocer su marca  
	
				3.1	Diseñar el logo de la tienda	
	
				3.2	Diseña el eslogan de la tienda	

  						
			4.El cliente quiere que la aplicación cuente con un Register y un login para identificar a sus clientes	
	
				4.1 Desarollar y probar el funcionamiento del registro
	
				4.2 implementar un login que autorize el ingreso en base a un registro previo
	
#2 			5.El cliente quiere implementar una base de datos que guarde plantas
	
				5.1 Crear una realtime database en Firebase y un modulo de autentificacion
	
				5.2 Implementar la autentificacion por correo y una forma de administrar las claves por tokens
	
  			6.El cliente quiere implementar una forma de realizar un registro de la planta dentro de la aplicacion	
	
				5.1 Vincular el UID de cada usuario con su propio jardin
	
				5.2 Realizar que la aplicacion pueda leer la base de datos y mostrar las plantas añadidas

  			7.El cliente quiere tener una interfaz grafica mas avanzada mas legible e intuitiva
	
				7.1 Quiere hacer uso de paneles y de botones con colores llamativos
	
				7.2 El cliente quiere tener una carpeta de recursos variados entre lo que se encuentran imagenes fuentes y formas

#3  			8.El cliente quiere poder identificar las plantas a traves de la aplicacion	
	
				8.1 Quiere implementar una API ya creada para identificarla 
	
				8.2 Realizar una base de datos sencilla para entrenar un modelo de reconocimiento
	
  			9.El cliente quiere incluir un apartado donde le indican el nombre cientifico de la planta identificada e informacion sobre la planta
	
				9.1 Incorporarar funcion de la API para proporcionar el nombre cientifico e informacion de la planta
	
				9.2 Incorporarar en el codigo informacion de la planta para que las plantas reconocidas con el modelo tambien muestren informacion de la planta		
	
  			10.El cliente quiere poder identificar la planta mediante una foto tomada en la app y tambien seleccionando desde la galeria
	
				10.1 Incorporarar funcion de camara en la app para tomar fotos
	
				10.2 Incorporarar funcion de abrir galeria para seleccionar una foto

#4 			11.El cliente quiere que la aplicación cuente con la funcion de registrar y personalizar el ciclo de riego de cada planta
	
				11.1 Incorporarar funcion en la aplicacion para que cada planta tenga su propio ciclo de riego elejido por el usuario
	
				11.2 Incorporarar funcion en la aplicacion para que el usuario pueda elejir los dias de ciclo de riego y el ultimo dia en que rego la planta

			12.El cliente quiere que la aplicación guarde las personalizaciones del usuario para ofrecer un mejor servicio
	
				12.1 Incorporarar funcion en la aplicacion para que el ciclo de riego de cada planta se guarde en firebase
	
				12.2 Incorporarar funcion en la aplicacion para que el ultimo dia de riego de cada planta se guarde en firebase

   			13.El cliente quiere poder saber cuantos dias faltan para regar la planta
	
				13.1 Incorporarar funcion en la aplicacion para que cuente cuantos dias faltan para regar la planta
	
				13.2 Implementar la funcion y mostrar cuantos dias faltan para regar la planta en el apartado de cada una

#5 			14.El cliente quiere que la aplicación cuente con sistema de notificaciones
	
				14.1 Implementar una funcion en la aplicacion para el dia que deba regar una planta la aplicacion le mande una notificacion
	
				14.2 Conectar la funcion de notificacion con firebase para que pueda saber si alguna planta se debe regar ese dia

			15.El cliente quiere que la aplicación muestre su cuenta de instagram para mejorar la interacción con sus clientes			
	
</pre>
# Requerimientos 
<pre>Requerimientos	
    Momentos	
	1.	Utilizar Java como lenguaje principal para el desarrollo de la aplicación.

	2.	Desarrollar la aplicación en Android Studio aprovechando sus herramientas de diseño, depuración y pruebas.

	3.	Implementar un sistema de login y registro usando Firebase Authentication.

	4.	Utilizar Firebase Realtime Database para almacenar y gestionar la información de las plantas.

	5.	Permitir que los usuarios registren múltiples plantas en un “jardín virtual”.

	6.	Cada planta deberá contar con su nombre científico (inmutable) que se muestre en la aplicación.

	7.	Permitir que el usuario asigne un nombre personalizado a cada planta.

	8.	Guardar la imagen de cada planta, pudiendo ser seleccionada desde la galería o capturada directamente con la cámara.

	9.	Incluir un campo para almacenar información extra (por ejemplo, extracto de Wikipedia) sobre cada planta.

	10.	Registrar el ciclo de riego en días, que determine la frecuencia de riego de la planta.

	11.	Registrar la fecha del último riego mediante un timestamp, para calcular cuándo debe regarse la planta.

	12.	Permitir que el usuario seleccione imágenes desde la galería para identificar o registrar una planta.

	13.	Permitir que el usuario capture una foto directamente desde la aplicación usando la cámara.

	14.	Utilizar FileProvider para crear y gestionar archivos temporales cuando se captura una imagen.

	15.	Mostrar la lista de plantas registradas en un RecyclerView, facilitando la navegación por el “jardín virtual”.

	16.	Utilizar Glide para cargar y mostrar las imágenes de las plantas de forma eficiente.

	17.	Implementar un sistema de reconocimiento de plantas que envíe la imagen (convertida a Base64) a una API externa (Plant.id).

	18.	Convertir la imagen a Base64 mediante un método utilitario para enviarla a la API.

	19.	Parsear la respuesta JSON de la API para obtener sugerencias (nombre, probabilidad, extracto de Wikipedia, imagen similar).

	20.	Mostrar las sugerencias de la API en un diálogo usando un adapter (SuggestionAdapter) y permitir la selección de una opción.

	21.	Al seleccionar una sugerencia, crear un PlantModel con los datos recibidos y guardarlo en Firebase.

	22.	Implementar el reconocimiento de plantas de forma local mediante un modelo TensorFlow Lite.

	23.	Redimensionar y normalizar las imágenes (por ejemplo, a 224x224) antes de enviarlas al modelo, usando ImageUtils.preprocessImage().

	24.	Utilizar un clasificador (Classifier) para predecir la especie de la planta y obtener un vector de probabilidades.

	25.	Con base en el resultado del modelo TFLite, asignar manualmente información extra a la planta mediante un bloque switch-case.

	26.	Crear un PlantModel con los datos del reconocimiento TensorFlow (nombre, infoText, imagen) y guardarlo en Firebase.

	27.	Permitir que el usuario modifique el nombre personalizado y otros datos de la planta, actualizándolos en Firebase.

	28.	Incluir un diálogo que permita al usuario actualizar el ciclo de riego (número de días) y seleccionar la fecha del último riego mediante un DatePicker.

	29.	Calcular, para cada planta, la diferencia entre el tiempo actual y la fecha en que se debe regar (lastWatered + wateringCycle en milisegundos) y mostrar este dato en la interfaz.

	30.	Implementar un Worker (WateringWorker) usando WorkManager que consulte periódicamente las plantas en Firebase y envíe notificaciones push cuando se cumplan las condiciones de riego.
