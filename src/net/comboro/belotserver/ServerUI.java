package net.comboro.belotserver;

import net.comboro.belotserver.bots.BelotBot;
import net.comboro.belotserver.networking.SerializableMessage;
import networking.Token;
import networking.client.BelotClient;
import networking.server.BelotServer;
import networking.server.ServerListener;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class ServerUI extends javax.swing.JFrame {

    private javax.swing.JButton btn_start;
    private javax.swing.JButton btn_stop;
    private javax.swing.JCheckBox cb_7s8samount20;
    private javax.swing.JCheckBox cb_enableBots;
    private javax.swing.JCheckBox cb_overtrumpInNoTrumps;
    private javax.swing.JCheckBox cb_squareAceNoTrump;
    private javax.swing.JCheckBox cb_valat44;
    private javax.swing.JLabel lbl_amount;
    private javax.swing.JLabel lbl_port;
    private javax.swing.JLabel lbl_ports;
    private javax.swing.JLabel lbl_rating;
    private javax.swing.JPanel pnl_bot_properties;
    private javax.swing.JPanel pnl_console;
    private javax.swing.JPanel pnl_properties;
    private javax.swing.JPanel pnl_rules;
    private javax.swing.JPanel pnl_title;
    private javax.swing.JScrollPane scroll_console;
    private javax.swing.JSpinner spinner_amount;
    private javax.swing.JSpinner spinner_rating;
    private javax.swing.JSpinner spnr_port;
    private javax.swing.JTextArea txtArea_console;
    private javax.swing.JLabel txt_title;

    private BelotServer belotServer;
    private Game game = new Game();


    public ServerUI() {
        initComponents();

        setTitle("Belot Masters Server");

        lbl_amount.setVisible(false);
        spinner_amount.setVisible(false);
        lbl_rating.setVisible(false);
        spinner_rating.setVisible(false);

        try {
            System.setOut(new PrintStream(new ConsoleOutput(txtArea_console), true, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ServerUI(int port, int bots) {
        startBelotServer(port, bots);
    }

    public static void main(String args[]) {
        boolean gui = true;
        int port = 47047;
        int bots = 0;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.matches("-?nogui")) {
                gui = false;
            } else if (arg.matches("-?port")) {
                port = Integer.parseInt(args[++i]);
            } else if (arg.matches("-?bots")) {
                bots = Integer.parseInt(args[++i]);
            }
        }

        if (gui)
            java.awt.EventQueue.invokeLater(() -> new ServerUI().setVisible(true));
        else new ServerUI(port, bots);

    }

    private void initComponents() {

        pnl_title = new javax.swing.JPanel();
        txt_title = new javax.swing.JLabel();
        pnl_properties = new javax.swing.JPanel();
        lbl_port = new javax.swing.JLabel();
        spnr_port = new javax.swing.JSpinner();
        lbl_ports = new javax.swing.JLabel();
        pnl_bot_properties = new javax.swing.JPanel();
        cb_enableBots = new javax.swing.JCheckBox();
        lbl_amount = new javax.swing.JLabel();
        spinner_amount = new javax.swing.JSpinner();
        lbl_rating = new javax.swing.JLabel();
        spinner_rating = new javax.swing.JSpinner();
        pnl_rules = new javax.swing.JPanel();
        cb_overtrumpInNoTrumps = new javax.swing.JCheckBox();
        cb_valat44 = new javax.swing.JCheckBox();
        cb_squareAceNoTrump = new javax.swing.JCheckBox();
        cb_7s8samount20 = new javax.swing.JCheckBox();
        pnl_console = new javax.swing.JPanel();
        scroll_console = new javax.swing.JScrollPane();
        txtArea_console = new javax.swing.JTextArea();
        btn_stop = new javax.swing.JButton();
        btn_start = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txt_title.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txt_title.setText("Belot Masters Server Configuration");

        javax.swing.GroupLayout pnl_titleLayout = new javax.swing.GroupLayout(pnl_title);
        pnl_title.setLayout(pnl_titleLayout);
        pnl_titleLayout.setHorizontalGroup(
                pnl_titleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_titleLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(txt_title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        pnl_titleLayout.setVerticalGroup(
                pnl_titleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnl_titleLayout.createSequentialGroup()
                                .addContainerGap(27, Short.MAX_VALUE)
                                .addComponent(txt_title)
                                .addGap(23, 23, 23))
        );

        lbl_port.setText("TCP Port: ");

        spnr_port.setModel(new javax.swing.SpinnerNumberModel(47047, 1024, 49151, 1));

        lbl_ports.setText("(1,024 - 49,151 / registered ports)");

        pnl_bot_properties.setBorder(javax.swing.BorderFactory.createTitledBorder("BOT Properties"));

        cb_enableBots.setText("Enable BOTs");
        cb_enableBots.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_enableBotsActionPerformed(evt);
            }
        });

        lbl_amount.setText("Amount");

        spinner_amount.setModel(new javax.swing.SpinnerNumberModel(1, 1, 4, 1));

        lbl_rating.setText("Rating");

        spinner_rating.setModel(new javax.swing.SpinnerNumberModel(800, 400, 2000, 50));

        javax.swing.GroupLayout pnl_bot_propertiesLayout = new javax.swing.GroupLayout(pnl_bot_properties);
        pnl_bot_properties.setLayout(pnl_bot_propertiesLayout);
        pnl_bot_propertiesLayout.setHorizontalGroup(
                pnl_bot_propertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnl_bot_propertiesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(cb_enableBots)
                                .addGap(18, 18, 18)
                                .addComponent(lbl_amount)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(spinner_amount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lbl_rating)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(spinner_rating, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnl_bot_propertiesLayout.setVerticalGroup(
                pnl_bot_propertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnl_bot_propertiesLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(pnl_bot_propertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pnl_bot_propertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(lbl_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(spinner_amount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(lbl_rating)
                                                .addComponent(spinner_rating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(cb_enableBots)))
        );

        pnl_rules.setBorder(javax.swing.BorderFactory.createTitledBorder("Customisable Rules"));

        cb_overtrumpInNoTrumps.setText("Overtrumping in 'No Trumps'");

        cb_valat44.setText("Valat in 'No Trumps' is 44");

        cb_squareAceNoTrump.setText("Square of Aces in 'No Trumps' is 100");

        cb_7s8samount20.setText("Ace of 7s or 8s account for 20 points (Except in 'No Trumps')");

        javax.swing.GroupLayout pnl_rulesLayout = new javax.swing.GroupLayout(pnl_rules);
        pnl_rules.setLayout(pnl_rulesLayout);
        pnl_rulesLayout.setHorizontalGroup(
                pnl_rulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnl_rulesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnl_rulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cb_overtrumpInNoTrumps)
                                        .addComponent(cb_valat44)
                                        .addComponent(cb_squareAceNoTrump)
                                        .addComponent(cb_7s8samount20))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnl_rulesLayout.setVerticalGroup(
                pnl_rulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnl_rulesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(cb_overtrumpInNoTrumps)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cb_valat44)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cb_squareAceNoTrump)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cb_7s8samount20)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnl_propertiesLayout = new javax.swing.GroupLayout(pnl_properties);
        pnl_properties.setLayout(pnl_propertiesLayout);
        pnl_propertiesLayout.setHorizontalGroup(
                pnl_propertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnl_propertiesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnl_propertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(pnl_bot_properties, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(pnl_propertiesLayout.createSequentialGroup()
                                                .addComponent(lbl_port)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(spnr_port, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(lbl_ports)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(pnl_rules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        pnl_propertiesLayout.setVerticalGroup(
                pnl_propertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnl_propertiesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnl_propertiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lbl_port, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(spnr_port, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lbl_ports))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(pnl_bot_properties, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pnl_rules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        txtArea_console.setEditable(false);
        txtArea_console.setColumns(20);
        txtArea_console.setRows(5);
        scroll_console.setViewportView(txtArea_console);

        javax.swing.GroupLayout pnl_consoleLayout = new javax.swing.GroupLayout(pnl_console);
        pnl_console.setLayout(pnl_consoleLayout);
        pnl_consoleLayout.setHorizontalGroup(
                pnl_consoleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(scroll_console)
        );
        pnl_consoleLayout.setVerticalGroup(
                pnl_consoleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(scroll_console, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
        );

        btn_stop.setText("Stop");
        btn_stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_stopActionPerformed(evt);
            }
        });

        btn_start.setText("Start");
        btn_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_startActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(pnl_title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(pnl_properties, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(pnl_console, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(13, 13, 13))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(btn_start)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btn_stop)
                                                .addContainerGap())))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(pnl_title, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(pnl_properties, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btn_stop)
                                        .addComponent(btn_start))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnl_console, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }

    private void cb_enableBotsActionPerformed(java.awt.event.ActionEvent evt) {
        boolean selected = cb_enableBots.isSelected();

        lbl_amount.setVisible(selected);
        spinner_amount.setVisible(selected);
        lbl_rating.setVisible(selected);
        spinner_rating.setVisible(selected);
    }

    private void btn_startActionPerformed(java.awt.event.ActionEvent evt) {
        pnl_bot_properties.setVisible(false);
        pnl_rules.setVisible(false);
        btn_start.setEnabled(false);
        btn_stop.setEnabled(true);

        try {
            spnr_port.commitEdit();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int port = (int) spnr_port.getValue(), bot_amount = 0;
        if (cb_enableBots.isSelected()) {
            try {
                spinner_amount.commitEdit();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            bot_amount = (int) spinner_amount.getValue();
        }
        startBelotServer(port, bot_amount);
    }

    private void btn_stopActionPerformed(java.awt.event.ActionEvent evt) {
        pnl_bot_properties.setVisible(true);
        pnl_rules.setVisible(true);
        btn_stop.setEnabled(false);
        btn_start.setEnabled(true);

        belotServer.stopServer();
    }

    public void startBelotServer(int port, int bot_amount) {
        belotServer = new BelotServer(port);
        belotServer.addLister(new ServerListener.ServerAdapter() {
            @Override
            public void onClientInput(BelotClient client, SerializableMessage message) {
                if (message.getData() instanceof String) {
                    String str = (String) message.getData();
                    System.out.println(str);
                    //Login
                    if (str.startsWith("token:") || str.startsWith("login:")) {
                        str = str.substring(6);

                        try {
                            Token token = new Token(str);
                            game.addPlayer(token, client);
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                            client.send(e.getMessage());
                            belotServer.removeClient(client);
                        }
                    }
                }
            }

            @Override
            public void onClientDisconnect(BelotClient client) {
                System.out.println("A client disconnected.");
            }

            @Override
            public void onServerStartError(Exception e) {
                e.printStackTrace();
            }
        });

        belotServer.startServer();

        List<BelotBot> bots = new ArrayList<>();
        for (int i = 0; i < bot_amount; i++) {
            try {
                Socket clientSocket = new Socket(InetAddress.getByName("localhost"), port);
                BelotBot belotBot = new BelotBot(clientSocket, i + 1);
                bots.add(belotBot);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
