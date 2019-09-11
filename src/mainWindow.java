import javax.swing.*;
import java.awt.event.*;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class mainWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonSend;
    private JButton buttonExit;
    private JTextField url;
    private JTextField msg;
    private JTextField statusTextField;
    private JLabel estado;
    private ConnectionFactory factory;
    private final static String QUEUE_NAME = "prueba";

    public mainWindow() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonSend);

        buttonSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSend();
            }
        });

        buttonExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { onExit(); }
        });

        // call onExit() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        // call onExit() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) { onExit(); }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        factory = new ConnectionFactory();
    }

    private void onSend() {
        try {
            factory.setUri(url.getText());
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = msg.getText();
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
            msg.setText("");
            statusTextField.setText("Mensaje enviado.");
        } catch (Exception e) {
            statusTextField.setText(e.toString());
        }
    }

    private void onExit() {
        dispose();
    }

    public static void main(String[] args) {
        mainWindow dialog = new mainWindow();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
