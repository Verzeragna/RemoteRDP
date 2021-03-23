import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.fasterxml.jackson.core.JsonProcessingException;
import dao.DataBase;
import dao.IDataBase;
import lock.Lock;
import util.IUtil;
import util.ReleeseResources;
import util.Util;

public class Main extends JFrame {

    private static final long serialVersionUID = 1L;
    private IDataBase dataBase = new DataBase();
    private IUtil util = new Util();
    private boolean isConnected = false;
    private Lock lock;

    private JPanel contentPane;
    private JLabel connectLabel;
    private JLabel statusLabel;
    private JButton connectBtn;

    private final static Image ICON_IMAGE = Toolkit.getDefaultToolkit()
            .getImage(new File("").getAbsolutePath() + "\\ic_image.ico");
    //private static final String PATH_TO_PROPERTIES = new File("").getAbsolutePath() + "\\settings.properties";
    private static final String PATH_TO_PROPERTIES = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent() + "\\settings.properties";
    //private static final String PATH_TO_KONTUR = new File("").getAbsolutePath() + "\\kontur.rdp";
    private static final String PATH_TO_KONTUR = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent() + "\\kontur.rdp";

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main frame = new Main();
                    frame.setVisible(true);
                    frame.setIconImage(ICON_IMAGE);
                    frame.addWindowListener(new WindowListener() {

                        public void windowActivated(WindowEvent event) {
                        }

                        public void windowClosed(WindowEvent event) {
                        }

                        public void windowDeactivated(WindowEvent event) {
                        }

                        public void windowDeiconified(WindowEvent event) {
                        }

                        public void windowIconified(WindowEvent event) {
                        }

                        public void windowOpened(WindowEvent event) {
                        }

                        public void windowClosing(final WindowEvent event) {
                            // TODO Auto-generated method stub
                            if (ReleeseResources.getMongoClient()!=null) {
                                ReleeseResources.closeConnection();
                            }
                        }

                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Create the frame.
     */
    public Main() {
        setTitle("\u041A\u043E\u043D\u0442\u0443\u0440 \u0426\u0411\u0421");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 220);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        connectBtn = new JButton("\u041F\u043E\u0434\u043A\u043B\u044E\u0447\u0438\u0442\u044C\u0441\u044F");
        connectBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        connectBtn.setBounds(12, 13, 204, 43);
        contentPane.add(connectBtn);

        JButton disconnectBtn = new JButton("\u041E\u0442\u043A\u043B\u044E\u0447\u0438\u0442\u044C\u0441\u044F");
        disconnectBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        disconnectBtn.setBounds(12, 69, 204, 43);
        contentPane.add(disconnectBtn);
        disconnectBtn.setEnabled(false);

        JButton checkStatusBtn = new JButton(
                "\u041F\u0440\u043E\u0432\u0435\u0440\u0438\u0442\u044C \u0441\u0442\u0430\u0442\u0443\u0441");
        checkStatusBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        checkStatusBtn.setBounds(12, 125, 204, 43);
        contentPane.add(checkStatusBtn);

        statusLabel = new JLabel("");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        statusLabel.setBounds(239, 125, 181, 43);
        contentPane.add(statusLabel);

        connectLabel = new JLabel("dynamic");
        connectLabel.setHorizontalAlignment(SwingConstants.CENTER);
        connectLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        connectLabel.setBounds(228, 13, 192, 43);
        contentPane.add(connectLabel);
        connectLabel.setText("Не активно");
        connectLabel.setForeground(Color.red);

        connectBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                connectToDatabase();

                try {
                    lock = dataBase.checkLock();
                    if (lock == null) {
                        dataBase.lockConnection();
                        dataBase.recordLog();
                        connectBtn.setEnabled(false);
                        connectLabel.setText("Активно");
                        connectLabel.setForeground(Color.green);
                        disconnectBtn.setEnabled(true);
                        checkStatusBtn.setEnabled(false);
                        try {
                            dataBase.openKontur(PATH_TO_KONTUR);
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            JOptionPane.showMessageDialog(null, "Ошибка запуска контура: " + e1.getMessage());
                        }
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Контур сейчас занят пользователем: " + lock.user + " с " + lock.dateStart);
                        //dataBase.disconnect();
                        connectBtn.setEnabled(true);
                        connectLabel.setText("Не активно");
                        connectLabel.setForeground(Color.red);
                    }
                } catch (JsonProcessingException jme) {
                    JOptionPane.showMessageDialog(null, "Ошибка чтения из базы данных: " + jme.getMessage());
                }

            }
        });

        disconnectBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {


                dataBase.unlockConnections();
                try {
                    dataBase.updateLog();
                } catch (JsonProcessingException ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка бновления колекции log: " + ex.getMessage());
                }
                dataBase.disconnect();
                connectBtn.setEnabled(true);
                connectLabel.setText("Не активно");
                connectLabel.setForeground(Color.red);
                isConnected = false;
                disconnectBtn.setEnabled(false);
                checkStatusBtn.setEnabled(true);
            }
        });

        checkStatusBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                connectToDatabase();

                try {
                    lock = dataBase.checkSatus();
                } catch (JsonProcessingException j) {
                    JOptionPane.showMessageDialog(null, "Ошибка чтения из базы данных: " + j.getMessage());
                }
                if (lock==null) {
                    statusLabel.setText("Контур свободен");
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Контур сейчас занят пользователем: " + lock.user + " с " + lock.dateStart);
                    statusLabel.setText("Контур занят");
                }

                dataBase.disconnect();
                isConnected = false;
            }
        });
    }

    private void connectToDatabase() {

        Properties prop = null;
        if (!isConnected) {
            try {
                prop = util.getProperties(PATH_TO_PROPERTIES);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Ошибка чтения файла настроек: " + ex.getMessage());
            }

            try {
                isConnected = dataBase.connect(prop);
            } catch (Exception exDb) {
                JOptionPane.showMessageDialog(null, "Ошибка подключения к базе данных: " + exDb.getMessage());
            }
        }
    }

}
