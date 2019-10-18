package ru.geekbrains.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThread; // поток, который слушает входящие сообщения
    private final TCPConnectionListener eventListener; // обработчик событий, представлен интерфейсом, т.к. может являться как сервером, так и клиентом : каждый должен по-своему описать ключевые методы, иметь свою реализацию
    private final BufferedReader in;
    private final BufferedWriter out;

    // Конструктор с внутренним сокетом
    public TCPConnection(TCPConnectionListener eventListener, String ip, int port) throws IOException {
        this(eventListener, new Socket(ip, port));
    }

    // Конструктор с уже определенным сокетом
    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException  {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8"))); // во избежание ошибочных интерпретаций, заранее определяем кодировку
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this); // если не указать TCPConnection, будет передан this анонимного класса
                    while (!rxThread.isInterrupted()) { // любое сетевое взаимодействие - это бесконечный цикл (в данном случае, пока не прерван поток)
                        String msg = in.readLine();
                        eventListener.onReceiveString(TCPConnection.this, msg);
                    }
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();
    }

    public synchronized void sendString(String message) { // synchonized делает методы потокобезопасными и позволяет обращаться к ним из нескольких потоков
        try {
            out.write(message + "\r\n"); // отправить сообщение, добавляется перевод строки для корректного вывода (сочетание \r\n - более подходящий вариант для win, чем просто \n)
            out.flush(); // "реализовать" все буферы
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect(); // аварийно завершаем соединение
        }
    }

    public synchronized void disconnect() {
        rxThread.interrupt(); // завершить основной поток (установить флаг завершения)
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return socket.getInetAddress().getHostAddress() + ": " + socket.getPort();
    }
}