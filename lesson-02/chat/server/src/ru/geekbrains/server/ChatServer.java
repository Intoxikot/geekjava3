package ru.geekbrains.server;

import ru.geekbrains.network.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer implements TCPConnectionListener {

    public static void main(String[] args) { // в среде Intellij конфигурация запуска может быть создана автоматически кликом на этом методе
        new ChatServer();
    }

    private final List<TCPConnection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server is running");
        try (ServerSocket serverSocket = new ServerSocket(8880)) {
            while (true) { // сервер достаточно прост и не рассчитан на какое-либо управление извне (только на запуск)
                try {
                    new TCPConnection(this, serverSocket.accept()); // чтобы использовать этот класс TCPConnection, необходимо прописать зависимость от нашего network
                    // accept ждет подключения - пока не подключится клиент, программа дальше выполняться не будет
                    // в качестве листенера - передаем себя и рабочий цикл готов
                } catch (IOException e) {
                    System.out.println("TCPConnection: " + e); // логируем подобные ошибки при подключении клиента
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Синхронизация позволит сделать методы потокобезопасными и запретит доступ из других потоков, пока не завершится их работа в одном потоке
    // Этот механизм обеспечит корректную многопоточность приложения
    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("Client was connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value); // при получении сообщения, серверу достаточно разослать его всем клиентам
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Client was disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection-error: " + e);
    }

    // Отправляет сообщение всем клиентам
    private void sendToAllConnections(String message) {
        System.out.println(message); // в любом случае необходимо залогировать
        final int peers = connections.size(); // небольшая оптимизация: чтобы не вызывать метод при каждой итерации
        for (int c = 0; c < peers; c++) {
            connections.get(c).sendString(message);
        }
    }
}