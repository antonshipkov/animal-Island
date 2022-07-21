package swing;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class WindowPanel extends JFrame {

    private JLabel infoLabel;
    private FieldPanel fieldPanel;

    public WindowPanel ( ) throws HeadlessException {
        super ( "Animal Island" );
    }

    public void init ( ) {
        setDefaultCloseOperation ( WindowConstants.EXIT_ON_CLOSE );

        JPanel rootPanel = new JPanel ( );
        rootPanel.setLayout ( new BoxLayout ( rootPanel ,BoxLayout.PAGE_AXIS ) );
        rootPanel.setBackground ( Color.WHITE );

        JPanel managementPanel = new JPanel ( );
        managementPanel.setBackground ( Color.RED );
        managementPanel.setLayout ( new GridBagLayout ( ) );

        JButton stopStartButton = styled ( new JButton ( "Старт/Стоп" ) );
        stopStartButton.addActionListener ( e -> fieldPanel.stopOrResume ( ) );
        JButton updateButton = styled ( new JButton ( "Запуск одного цикла" ) );
        updateButton.addActionListener ( e -> fieldPanel.updateGame ( ) );
        JButton restartButton = styled ( new JButton ( "Перезагрузка" ) );
        restartButton.addActionListener ( e -> fieldPanel.init ( ) );

        managementPanel.add ( stopStartButton ,gbc ( 0 ,0 ) );
        managementPanel.add ( updateButton ,gbc ( 1 ,0 ) );
        managementPanel.add ( restartButton ,gbc ( 2 ,0 ) );

        fieldPanel = new FieldPanel ( );
        fieldPanel.setBackground ( Color.BLUE );
        fieldPanel.setInfoConsumer ( info -> infoLabel.setText ( info ) );

        fieldPanel.init ( );


        infoLabel = new JLabel ( "Информация:" );
        infoLabel.setAlignmentX ( Component.RIGHT_ALIGNMENT );

        rootPanel.add ( managementPanel ,BorderLayout.CENTER );
        rootPanel.add ( fieldPanel ,BorderLayout.CENTER );
        rootPanel.add ( infoLabel ,BorderLayout.EAST );

        setContentPane ( rootPanel );
        pack ( );
        setLocationRelativeTo ( null );
        setResizable ( false );
        setVisible ( true );
    }

    private GridBagConstraints gbc (int x ,int y) {
        GridBagConstraints gbc = new GridBagConstraints ( );
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets ( 2 ,2 ,2 ,2 );
        return gbc;
    }

    private JButton styled (JButton button) {
        button.setForeground ( Color.BLACK );
        button.setBackground ( Color.WHITE );
        Border line = new LineBorder ( Color.BLUE );
        Border margin = new EmptyBorder ( 5 ,15 ,5 ,15 );
        Border compound = new CompoundBorder ( line ,margin );
        button.setBorder ( compound );
        button.setMinimumSize ( new Dimension ( 100 ,30 ) );
        return button;
    }
}
