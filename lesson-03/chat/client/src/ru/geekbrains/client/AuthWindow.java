package ru.geekbrains.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.*;

// В базе у нас три пользователя, по этим данным можно авторизоваться и войти в чат:
// log:nik, key:3401
// log:kot, key:8776
// log:neo, key:2323

// Механизм авторизации максимально упрощен - подключение к базе осуществляется напрямаую без участия основного сервера
// Регистрации здесь нет и добавить нового пользователя не получится
public class AuthWindow extends JFrame implements ActionListener {

    static {
        try {
            java.sql.DriverManager.registerDriver(new org.sqlite.JDBC()) ;
            // System.out.print("driver successfull registered");
        } catch (SQLException e) {
            throw new RuntimeException("driver registration error") ;
        }
    }

    private final int WINDOW_WIDTH = 250;
    private final int WINDOW_HEIGHT = 100;

    public static void main(String[] args) {
        new AuthWindow();
    }

    JTextField fieldName = new JTextField();
    JPasswordField fieldKey = new JPasswordField();
    Connection conn = null;

    public AuthWindow() {
        setTitle("auth");
        fieldKey.setEchoChar('*'); // парольная маска для поля ввода пароля

        GridLayout layout = new GridLayout(2, 1, 0, 0);
        setLayout(layout);

        add(fieldName);
        add(fieldKey);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // дефолтная операция на закрытие приложения "крестиком" - завершает приложение
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null); // установить окно приложения по центру экрана
        setAlwaysOnTop(true); // установить окно приложения на первый план (неизменяемый параметр)

        setVisible(true);
        fieldKey.addActionListener(this);
        try {
            Class.forName("org.sqlite.JDBC"); // подключение драйвера
            String url = "jdbc:sqlite:data.db"; // имя базы данных в папке проекта
            conn = DriverManager.getConnection(url); // создать соединение к базе
        } catch (Exception e) {
            System.out.println("database-error");
        }
    }

    public void auth() {
        String name = fieldName.getText().trim(); // Здесь я явно забыл "обрезать" входные данные в прошлый раз, хотя в окне отправки сообщений это уже использовал
        String key = fieldKey.getText().trim();

        try {
            // Такой способ не очень-то безопасен, тем более, что он выполняется на стороне клиента
            // Впрочем мы не получаем конкретных данных и паролей - в качестве ответа на авторизацию служит число записей count в ResultSet
            // Если есть совпадение (1) - значит успешно, если нет (0) - неуспешно
            PreparedStatement st = conn.prepareStatement("select count(*) from users where name=? and key=?");
            st.setString(1, name);
            st.setString(2, key);
            ResultSet rs = st.executeQuery();
            int count = rs.getInt(1);
            if (count == 1) {
                setVisible(false);
                new ClientWindow(name); // если успех, я решил просто передавать никнейм в окно клиента
            }
            throw new SQLException();
        } catch (SQLException e) {
            System.out.println("auth-error"); // мы не будем прерывать выполнение программы (хотя это явно упрощает подбор пароля)
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        auth();
    }
}