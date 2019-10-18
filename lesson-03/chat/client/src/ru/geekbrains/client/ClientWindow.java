package ru.geekbrains.client;

import ru.geekbrains.network.*;

import javax.swing.*; // Swing является наиболее простым и наглядным инструментом для создания графического интерфейса (JavaFX - нет, не проще)
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener, WindowListener { // JFrame - элемент Swing, ActionListener позволяет перехватывать нажатия, а наш TCPConnListener и тот вовсе, чтобы вообще обмазаться интерфейсами

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
                new ClientWindow("username"); // позволяет выполниться строке в потоке EDT (он идет следом за после главного потока-main)
            }
        });
    }

    private JTextArea log = new JTextArea();
    private JTextField fieldInput = new JTextField();
    private String nickname;
    private boolean isConnectionExists = false; // переменная наличия соединения (используется при отправке сообщений)
    private PrintWriter historyLog; // лог истории, будет осуществляться запись

    // Шаблон для отображения даты
    private final static DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public ClientWindow(String name) {
        this.nickname = name;
        initWindow();
        setContentOptions();
        showWindow(); // инициализация окна завершена, теперь можно его отобразить на экран
        createConnection();
        openHistoryLog();
    }

    // Задает базовые параметры для окна
    private void initWindow() {
        setTitle("chatroom"); // задать заголовок для окна
        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // дефолтная операция на закрытие приложения "крестиком" - завершает приложение
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null); // установить окно приложения по центру экрана
        setAlwaysOnTop(true); // установить окно приложения на первый план (неизменяемый параметр)

        this.addWindowListener(this); // чтобы не писать анонимный класс - наследуем окно от windowListener, которое ссылается на себя (методы переопределяем в классе)
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
        fieldInput.addActionListener(this);

        // Установить шрифт/оформление
        Font font = new Font("Courier New", java.awt.Font.PLAIN, 14); // начертание: BOLD - жирный, ITALIC - курсив
        log.setFont(font);
        fieldInput.setFont(font);
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

    // Реализация функций TCPConnectionListener - типовые события сервера/клиента (синхронизировать нет смысла т.к. они вызываются из одного потока)

    @Override
    public void actionPerformed(ActionEvent e) { // Механизм отправки сообщений (по нажатию enter)
        if (isConnectionExists) { // Отправить сообщение можно только при наличии соединения (иначе зачем жить? в чем смысл бытия?)
            String text = fieldInput.getText().trim(); // не забываем удалять лишние пробелы
            if (text.isEmpty()) return;
            fieldInput.setText(null);

            String time = "[" + pattern.format(LocalDateTime.now()) + "]"; // текущее время (отформатированное по шаблону)

            // Сообщение одновременно отправляется сразу на два потока - на экран и в файл
            String message = nickname + " " + time + ": " + text;
            connection.sendString(message);
            historyLog.println(message);
        }
    }

    // Выполнение функции происходит один раз при подключении (при инициализации объекта)
    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        isConnectionExists = true; // соединение установлено
        printMessage("Connection to server is successful");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String msg) {
        printMessage(msg);
        historyLog.println(msg); // если этого не сделать, мы будем сохранять только свои сообщения в историю (что весьма забавно и бессмысленно)
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        isConnectionExists = false; // соединение потеряно
        printMessage("You was disconnected from server");
        if (historyLog != null) historyLog.close(); // закрыть историю
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

    private void openHistoryLog() {
        try {
            String filename = nickname + ".hist";
            File file = new File(filename);

            // Если файла истории нет - его нужно создать, иначе вывести на экран
            if (!file.exists()) file.createNewFile(); else showLastHistory(file);

            historyLog = new PrintWriter(new FileWriter(file, true));

        } catch (IOException io) {
            // ну здесь что-то там нехорошее
        }
    }

    // Показать последние строки чата (для простоты делаю через список, позже реализацию можно будет заменить на RandomAccessFile)
    private void showLastHistory(File file) throws FileNotFoundException {
        final int limit = 5; // количество выводимых последних строк
        List lines = new ArrayList<String>();
        Scanner in = new Scanner(new FileReader(file));

        // Необходимо прочитать весь файл, но запишутся только последние строки
        while (in.hasNext()) {
            if (lines.size() >= limit) lines.remove(0); // по превышению ограничения, элементы в начале затираются
            lines.add(in.nextLine());
        }

        // Вывести результат
        for (int i = 0; i < lines.size(); i++)
            printMessage((String) lines.get(i));
        in.close();
    }

    @Override public void windowClosing(WindowEvent e) {
        onDisconnect(connection); // достаточно закыть соединение, все нужные операции находятся там
        System.exit(0);
    }
    @Override public void windowOpened(WindowEvent e) { }
    @Override public void windowClosed(WindowEvent e) { }
    @Override public void windowIconified(WindowEvent e) { }
    @Override public void windowDeiconified(WindowEvent e) { }
    @Override public void windowActivated(WindowEvent e) { }
    @Override public void windowDeactivated(WindowEvent e) { }

}