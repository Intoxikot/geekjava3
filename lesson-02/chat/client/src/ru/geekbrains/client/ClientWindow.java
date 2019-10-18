package ru.geekbrains.client;

import ru.geekbrains.network.*;

import javax.swing.*; // Swing является наиболее простым и наглядным инструментом для создания графического интерфейса (JavaFX - нет, не проще)
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener { // JFrame - элемент Swing, ActionListener позволяет перехватывать нажатия, а наш TCPConnListener и тот вовсе, чтобы вообще обмазаться интерфейсами

    // Данные для подключения к серверу
    private final static String SERVER_HOST = "localhost";
    private final static int SERVER_PORT = 8880;

    // Константы для интерфейса (окно приложения)
    private final int WINDOW_WIDTH = 450;
    private final int WINDOW_HEIGHT = 300;

    private TCPConnection connection;

    public static void main(String[] args) {
        // Во всех графических интерфейсах есть ограничение по многопоточности: практически нельзя работать из нескольких потоков,
        // какие-то могут работать только из одного-главного потока, а со Swing ограничения еще более жесткие - только из потока EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow(); // позволяет выполниться строке в потоке EDT (он идет следом за после главного потока-main)
            }
        });
    }

    private JTextArea log = new JTextArea();
    private JTextField fieldNickname = new JTextField("username");
    private JTextField fieldInput = new JTextField();

    private ClientWindow() {
        initWindow();
        setContentOptions();
        showWindow(); // инициализация окна завершена, теперь можно его отобразить на экран
        createConnection();
    }

    // Задает базовые параметры для окна
    private void initWindow() {
        setTitle("chatroom"); // задать заголовок для окна
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // дефолтная операция на закрытие приложения "крестиком" - завершает приложение
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null); // установить окно приложения по центру экрана
        setAlwaysOnTop(true); // установить окно приложения на первый план (неизменяемый параметр)
    }

    // Установить настройки для содержимого и элементов окна
    private void setContentOptions() {
        // Установить параметры для компонентов
        log.setEditable(false); // отправленные/полученные сообщения редактировать нельзя
        log.setLineWrap(true); // поддержка многострочного текста в поле чата

        // Задать расположение элементов
        // Компоновщиком по-умолчанию является BorderLayout - позволяет добавлять элементы "по сторонам света": север, юг, запад, восток и центр
        JScrollPane msgArea = new JScrollPane(log); // JScrollPane - панель c поддержкой скролла по полю (основное поле чата)
        add(msgArea, BorderLayout.CENTER);
        JPanel sendPanel = new JPanel(new BorderLayout());  // панель для отправки сообщений (никнейм + сообщение)
        add(sendPanel, BorderLayout.SOUTH); // юг - находится снизу (предполагается, что "компас" направлен на север и мы держим его перед собой)
        sendPanel.add(fieldInput, BorderLayout.CENTER); // эти поля крепяться на нашу панель (сама панель располагается снизу)
        sendPanel.add(fieldNickname, BorderLayout.WEST);
        fieldInput.addActionListener(this);

        // Установить шрифт/оформление
        Font font = new Font("Courier New", java.awt.Font.PLAIN, 14); // начертание: BOLD - жирный, ITALIC - курсив
        log.setFont(font);
        fieldInput.setFont(font);
        fieldNickname.setFont(font);

        // При получении фокуса - очищает поле ввода имени
        fieldNickname.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { fieldNickname.setText("");
            }
        });
    }

    // Вывод окна на экран
    private void showWindow() {
        setVisible(true);
    }

    private void createConnection() {
        try {
            connection = new TCPConnection(this, SERVER_HOST, SERVER_PORT);
        } catch (IOException e) {
            onException(connection, e);
        }
    }

    // Реализация TCPConnectionListener - типовые события сервера/клиента (синхронизировать нет смысла т.к. они вызываются из одного потока)
    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText().trim(); // не забываем удалять лишние пробелы
        String name = fieldNickname.getText().trim();
        if (msg.isEmpty()) return;
        fieldInput.setText(null);
        connection.sendString(name + ": " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection to server is successful");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String msg) {
        printMessage(msg);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection to server was closed");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection error to server"); // в случае сбоев - выводим все в окно чата
        System.out.println(e);
    }

    // Метод для вывода сообщения на экан, должен поддерживать работу с потоками - будет вызываться отовсюду
    private synchronized void printMessage(String msg) {
        SwingUtilities.invokeLater(new Runnable() { // обращение к потоку EDT (Swing работает только через EDT)
            @Override
            public void run() {
                log.append(msg + '\n');
                log.setCaretPosition(log.getDocument().getLength()); // двигает экран вниз
            }
        });

    }
}