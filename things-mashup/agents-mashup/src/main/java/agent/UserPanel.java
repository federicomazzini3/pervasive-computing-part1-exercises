package agent;

import io.vertx.core.eventbus.EventBus;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;

public class UserPanel extends JFrame{
    /**
     * SET BY USER
     * Set lightLevel
     * Set presence
     * Trigger vocal command
     */

    private JTextField lightLevelTextEdit;
    private JTextField vocalCommandTextEdit;
    private JButton setPresenceButton;
    private JButton setVocalCommandButton;
    private JButton setLightLevelButton;

    private JLabel lightStateLabel;
    private JLabel lightIntensityLabel;
    private JLabel presenceLabel;
    private JLabel lightInRoomLabel;
    private JLabel lightInRoomWithLightOnLabel;
    private JLabel lastUserCommandLabel;

    public UserPanel(EventBus eb){
        setTitle("..:: Debug Panel ::..");
        setSize(600,250);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);

        JPanel facadePanel = new JPanel();
        facadePanel.setLayout(new BoxLayout(facadePanel, BoxLayout.X_AXIS));
        facadePanel.setBorder(BorderFactory.createBevelBorder(1));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createBevelBorder(1));
        facadePanel.add(infoPanel);

        JPanel editPanel = new JPanel();
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.X_AXIS));
        editPanel.setBorder(BorderFactory.createBevelBorder(1));


        lightStateLabel = new JLabel("Light state: ");
        lightStateLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lightStateLabel.setSize(300, 30);
        lightStateLabel.setMinimumSize(lightStateLabel.getSize());
        lightStateLabel.setMaximumSize(lightStateLabel.getSize());
        infoPanel.add(lightStateLabel);

        lightIntensityLabel = new JLabel("Intensity: ");
        lightIntensityLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lightIntensityLabel.setSize(300, 30);
        lightIntensityLabel.setMinimumSize(lightIntensityLabel.getSize());
        lightIntensityLabel.setMaximumSize(lightIntensityLabel.getSize());
        infoPanel.add(lightIntensityLabel);

        lightInRoomLabel = new JLabel("Light in room: ");
        lightInRoomLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lightInRoomLabel.setSize(300, 30);
        lightInRoomLabel.setMinimumSize(lightInRoomLabel.getSize());
        lightInRoomLabel.setMaximumSize(lightInRoomLabel.getSize());
        infoPanel.add(lightInRoomLabel);

        lightInRoomWithLightOnLabel = new JLabel("Light in room consider lamp: ");
        lightInRoomWithLightOnLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lightInRoomWithLightOnLabel.setSize(300, 30);
        lightInRoomWithLightOnLabel.setMinimumSize(lightInRoomWithLightOnLabel.getSize());
        lightInRoomWithLightOnLabel.setMaximumSize(lightInRoomWithLightOnLabel.getSize());
        infoPanel.add(lightInRoomWithLightOnLabel);

        presenceLabel = new JLabel("Presence: ");
        presenceLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        presenceLabel.setSize(300, 30);
        presenceLabel.setMinimumSize(presenceLabel.getSize());
        presenceLabel.setMaximumSize(presenceLabel.getSize());
        infoPanel.add(presenceLabel);

        lastUserCommandLabel = new JLabel("Last user command: ");
        lastUserCommandLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lastUserCommandLabel.setSize(300, 30);
        lastUserCommandLabel.setMinimumSize(lastUserCommandLabel.getSize());
        lastUserCommandLabel.setMaximumSize(lastUserCommandLabel.getSize());
        infoPanel.add(lastUserCommandLabel);


        JPanel setPresencePanel = new JPanel();
        setPresencePanel.setLayout(new BoxLayout(setPresencePanel, BoxLayout.X_AXIS));
        editPanel.add(setPresencePanel);

        setPresenceButton = new JButton("Set presence");
        setPresencePanel.add(setPresenceButton);
        setPresenceButton.addActionListener((ActionEvent ev) -> {
            eb.publish("gui-set-presence", "");
        });

        JPanel setLightLevelPanel = new JPanel();
        setLightLevelPanel.setLayout(new BoxLayout(setLightLevelPanel, BoxLayout.X_AXIS));
        editPanel.add(setLightLevelPanel);

        lightLevelTextEdit = new JTextField();
        lightLevelTextEdit.setText("0000");
        lightLevelTextEdit.setToolTipText("Light level");
        lightLevelTextEdit.setSize(500, 30);
        lightLevelTextEdit.setMinimumSize(lightLevelTextEdit.getSize());
        lightLevelTextEdit.setMaximumSize(lightLevelTextEdit.getSize());
        setLightLevelPanel.add(lightLevelTextEdit);

        setLightLevelButton = new JButton("Set light");
        setLightLevelPanel.add(setLightLevelButton);
        setLightLevelButton.addActionListener((ActionEvent ev) -> {
            try{
              Integer lightLevelInt = Integer.parseInt(lightLevelTextEdit.getText());
              String lightLevel = lightLevelInt.toString();
              eb.publish("gui-set-lightLevel", lightLevel);
            }catch (Exception e){
                e.getMessage();
            }
        });


        JPanel setVocalCommandPanel = new JPanel();
        setVocalCommandPanel.setLayout(new BoxLayout(setVocalCommandPanel, BoxLayout.X_AXIS));
        editPanel.add(setVocalCommandPanel);

        vocalCommandTextEdit = new JTextField();
        vocalCommandTextEdit.setText("New command");
        vocalCommandTextEdit.setToolTipText("New command");
        vocalCommandTextEdit.setSize(500, 30);
        vocalCommandTextEdit.setMinimumSize(vocalCommandTextEdit.getSize());
        vocalCommandTextEdit.setMaximumSize(vocalCommandTextEdit.getSize());
        setVocalCommandPanel.add(vocalCommandTextEdit);

        setVocalCommandButton = new JButton("Trigger command");
        setVocalCommandPanel.add(setVocalCommandButton);
        setVocalCommandButton.addActionListener((ActionEvent ev) -> {
            if(vocalCommandTextEdit.getText() != null && !vocalCommandTextEdit.getText().isEmpty())
                eb.publish("gui-set-vocalCommand", vocalCommandTextEdit.getText());
        });

        mainPanel.add(editPanel);
        mainPanel.add(facadePanel);

    }

    public void setLightStateLabel(String lightStateLabel){
        SwingUtilities.invokeLater(() -> {
            this.lightStateLabel.setText("Light state: " + lightStateLabel);
        });
    }

    public void setLightIntensity(String lightIntensity){
        SwingUtilities.invokeLater(() -> {
            this.lightIntensityLabel.setText("Light intensity: " + lightIntensity);
        });
    }

    public void setPresenceLabel(String presenceLabel){
        SwingUtilities.invokeLater(() -> {
            this.presenceLabel.setText("Presence: " + presenceLabel);
        });
    }

    public void setLightInRoomLabel(String lightInRoomLabel){
        SwingUtilities.invokeLater(() -> {
            this.lightInRoomLabel.setText("Light in room: " + lightInRoomLabel);
        });
    }

    public void setLightInRoomWithLightOnLabel(String lightInRoomWithLightOnLabel){
        SwingUtilities.invokeLater(() -> {
            this.lightInRoomWithLightOnLabel.setText("Light in room with lamp: " + lightInRoomWithLightOnLabel);
        });
    }

    public void setLastUserCommandLabel(String lastUserCommandLabel){
        SwingUtilities.invokeLater(() -> {
            this.lastUserCommandLabel.setText("Last user command: " + lastUserCommandLabel);
        });
    }

    public void display() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
    }

}
