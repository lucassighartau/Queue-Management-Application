package GUI;

import BusinessLogic.SelectionPolicy;
import BusinessLogic.SimulationManager;
import Model.Server;
import Model.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class SimulationFrame extends JFrame {

    private JTextField tfClients, tfQueues, tfSimInterval;
    private JTextField tfMinArrival, tfMaxArrival, tfMinService, tfMaxService;
    private JComboBox<SelectionPolicy> cbPolicy;
    private JButton btnStart;


    private JTextArea taLog;
    private JScrollPane scrollPane;

    public SimulationFrame() {
        initializeUI();
    }

    private void initializeUI()
    {
        this.setTitle("Queues Management Simulation");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());


        JPanel pnlSetup = new JPanel(new GridLayout(4, 4, 10, 10));
        pnlSetup.setBorder(BorderFactory.createTitledBorder("Simulation Setup"));

        pnlSetup.add(new JLabel("Clients:")); tfClients = new JTextField("4");
        pnlSetup.add(tfClients);
        pnlSetup.add(new JLabel("Queues:")); tfQueues = new JTextField("2");
        pnlSetup.add(tfQueues);

        pnlSetup.add(new JLabel("Simulation Time:")); tfSimInterval = new JTextField("60");
        pnlSetup.add(tfSimInterval);
        pnlSetup.add(new JLabel("Policy:"));
        cbPolicy = new JComboBox<>(SelectionPolicy.values());
        pnlSetup.add(cbPolicy);

        pnlSetup.add(new JLabel("Min Arrival:")); tfMinArrival = new JTextField("2");
        pnlSetup.add(tfMinArrival);
        pnlSetup.add(new JLabel("Max Arrival:")); tfMaxArrival = new JTextField("30");
        pnlSetup.add(tfMaxArrival);

        pnlSetup.add(new JLabel("Min Service:")); tfMinService = new JTextField("2");
        pnlSetup.add(tfMinService);
        pnlSetup.add(new JLabel("Max Service:")); tfMaxService = new JTextField("4");
        pnlSetup.add(tfMaxService);

        btnStart = new JButton("Start Simulation");
        this.add(pnlSetup, BorderLayout.NORTH);
        this.add(btnStart, BorderLayout.SOUTH);

        taLog = new JTextArea();
        taLog.setEditable(false);
        taLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scrollPane = new JScrollPane(taLog);
        this.add(scrollPane, BorderLayout.CENTER);

        this.setVisible(true);
    }


    public JButton getBtnStart()
    {
        return btnStart;
    }


    public int getNumClients() { return Integer.parseInt(tfClients.getText()); }
    public int getNumQueues() { return Integer.parseInt(tfQueues.getText()); }
    public int getSimTime() { return Integer.parseInt(tfSimInterval.getText()); }
    public int getMinArrival() { return Integer.parseInt(tfMinArrival.getText()); }
    public int getMaxArrival() { return Integer.parseInt(tfMaxArrival.getText()); }
    public int getMinService() { return Integer.parseInt(tfMinService.getText()); }
    public int getMaxService() { return Integer.parseInt(tfMaxService.getText()); }
    public SelectionPolicy getSelectedPolicy() { return (SelectionPolicy) cbPolicy.getSelectedItem(); }

    public void addStartListener(ActionListener action)
    {
        btnStart.addActionListener(action);
    }
    public void updateDisplay(int currentTime, List<Task> waitingTasks, List<Server> servers)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Time ").append(currentTime).append("\n");

        sb.append("Waiting clients: ");
        for (Task t : waitingTasks)
        {
            sb.append(t.toString()).append("; ");
        }
        sb.append("\n");

        for (int i = 0; i < servers.size(); i++)
        {
            sb.append("Queue ").append(i + 1).append(": ");
            Server s = servers.get(i);
            if (s.getTasks().isEmpty()) {
                sb.append("closed");
            } else {
                for (Task t : s.getTasks()) {
                    sb.append(t.toString()).append("; ");
                }
            }
            sb.append("\n");
        }
        sb.append("--------------------------------------------------\n");

        SwingUtilities.invokeLater(() -> {
            taLog.append(sb.toString());
            taLog.setCaretPosition(taLog.getDocument().getLength());
        });
    }

    public void displayMessage(String message)
    {
        JOptionPane.showMessageDialog(this, message);
    }
}