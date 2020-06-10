/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CiscoGT;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import org.apache.log4j.Logger;

/**
 * Esta clase gestiona el envio de datos entre el servidor y el cliente al que atiende.
 *
 * @author Dommy
 */
public class ConexionClient extends Thread implements Observer
{
    private Logger log = Logger.getLogger(ConexionClient.class);
        private Socket socket; 
             private DataBaseChat mensajes;
        private DataInputStream entradaDatos;
    private DataOutputStream salidaDatos;
    
    public ConexionClient (Socket socket, DataBaseChat mensajes)
    {
        this.socket = socket;
            this.mensajes = mensajes;
        
        try 
        {
            entradaDatos = new DataInputStream(socket.getInputStream());
                salidaDatos = new DataOutputStream(socket.getOutputStream());
        } 
        
        catch (IOException ex) 
        {
            log.error("Error al crear los stream de entrada y salida : " + ex.getMessage());
        }
    }
    
    
    @Override
    public void run()
    {
        String mensajeRecibido;
            boolean conectado = true;
            
        // Se apunta a la lista de observadores de mensajes
        mensajes.addObserver(this);
        
        while (conectado) 
        {
            try 
            {
                // Lee un mensaje enviado por el cliente
                mensajeRecibido = entradaDatos.readUTF();
                
                // Pone el mensaje recibido en mensajes para que se notifique 
                // a sus observadores que hay un nuevo mensaje.
                    mensajes.setMensaje(mensajeRecibido);
                
            } 
            catch (IOException ex) 
            {
                log.info("Cliente con la IP " + socket.getInetAddress().getHostName() + " desconectado.");
                    conectado = false; 
                
                // Si se ha producido un error al recibir datos del cliente se cierra la conexion con el.
                try 
                {
                    entradaDatos.close();
                        salidaDatos.close();
                } 
                catch (IOException ex2) 
                {
                    log.error("Error al cerrar los stream de entrada y salida :" + ex2.getMessage());
                }
            }
        }   
    }
    
    @Override
    public void update(Observable o, Object arg) 
    {
        try 
        {
            // Envia el mensaje al cliente
            salidaDatos.writeUTF(arg.toString());
        } 
        catch (IOException ex) 
        {
            log.error("Error al enviar mensaje al cliente (" + ex.getMessage() + ").");
        }
    }
    
    
}
