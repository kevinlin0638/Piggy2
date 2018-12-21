package Gui;

import com.alee.extended.label.WebHotkeyLabel;
import com.alee.extended.painter.TitledBorderPainter;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.progress.WebProgressOverlay;
import com.alee.global.StyleConstants;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.progressbar.WebProgressBar;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.scroll.WebScrollBar;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.separator.WebSeparator;
import com.alee.laf.text.WebTextField;
import com.alee.laf.text.WebTextPane;
import com.alee.utils.ThreadUtils;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import server.ShutdownServer;
import tools.types.Pair;

public class Start extends WebFrame {

    public static boolean startFinish = false;
    private static Start instance = null;
    private final StartFrame progress;
    private final Thread start_thread;
    private final String server_version;
    private WebTextPane textPane;
    private long starttime = 0;
    private ScheduledFuture<?> shutdownServer, startRunTime;
    private WebHotkeyLabel[] labels;
    private boolean autoScroll = true;
    private WebHotkeyLabel runningTimelabel;
    //數據庫連接 [暫時隱藏]
    //private DatabaseConnection.DataBaseStatus dataBaseStatus;

    public Start() {
        start_thread = new Thread(new StartThread());

        // 創建主面板
        final WebPanel contentPane = new WebPanel();

        contentPane.setPreferredSize(1000, 600);
        setMinimumSize(contentPane.getPreferredSize());

        //GUI計時器 [暫時隱藏]
        //Timer.GuiTimer.getInstance().start();
        ProgressBarObservable progressBarObservable = new ProgressBarObservable();
        ProgressBarObserver progressBarObserver = new ProgressBarObserver(progressBarObservable);

        progress = createProgressDialog();
        progress.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });

        progress.setIconImage(getMainIcon().getImage());
        progress.setTitle("服務端正在啟動...");
        setIconImage(getMainIcon().getImage());
        setLayout(new BorderLayout());

        Properties properties = new Properties();
        //載入版本 [暫時隱藏]
        /*
        try {
            properties.load(Start.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        server_version = properties.getProperty("version");
        System.setProperty("wzpath", "wz");

        progressBarObservable.setProgress(new Pair<>("初始化配置...", 0));
        //載入設定檔 [暫時隱藏]
        //configs.Config.load();
        progressBarObservable.setProgress(new Pair<>("檢查網絡狀態...", 10));

        progressBarObservable.setProgress(new Pair<>("初始化數據庫配置...", 30));
        //數據庫狀態 [暫時隱藏]
        //dataBaseStatus = DatabaseConnection.getInstance().TestConnection();
        //初始化服務器 [暫時隱藏]
        InitializeServer.initializeRedis(false, progressBarObservable);

        ThreadUtils.sleepSafely(1000);
        progress.setVisible(false);

        contentPane.add(createMainPane(), BorderLayout.CENTER);
        //創建狀態欄 [暫時隱藏]
        //contentPane.add(createStatusBar(), BorderLayout.SOUTH);

        add(contentPane);

        progressBarObserver.deleteObserver(progressBarObservable);
        progressBarObservable.deleteObservers();

        //GUI標題 [暫時隱藏]
        //setTitle("冰火家族 當前遊戲版本: v." + ServerConfig.LOGIN_MAPLE_VERSION + "." + ServerConfig.LOGIN_MAPLE_PATCH + " 服務端版本: " + server_version);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = WebOptionPane.showConfirmDialog(instance, "確定要退出？", "警告", WebOptionPane.YES_NO_OPTION);
                if (result == WebOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        //數據管理面板 [暫時隱藏]
        //SwingUtilities.invokeLater(DataManagePanel::getInstance);
        System.setOut(new PrintStream(new NewOutputStram((byte) 0)));
        System.setErr(new PrintStream(new NewOutputStram((byte) 1)));
    }

    public static Start getInstance() {
        if (instance == null) {
            instance = new Start();
        }
        return instance;
    }

    public static ImageIcon loadIcon(final String path) {
        return new ImageIcon(Start.class.getResource("/Gui/icon/" + path));
    }

    public static ImageIcon getMainIcon() {
        return loadIcon("1002140.png");
    }

    private static void checkSingleInstance() {
        try {
            new ServerSocket(26351);
        } catch (IOException ex) {
            if (ex.getMessage().contains("Address already in use: JVM_Bind")) {
                WebOptionPane.showMessageDialog(instance, "同一台電腦只能運行一個服務端，若因服務端未正常關閉，請在任務管理器內結束javaw.exe進程", "錯誤", WebOptionPane.ERROR_MESSAGE);
                System.out.println();
            }
            System.exit(0);
        }
    }

    private static void run() {
        Start.getInstance().display();
        //測試數據庫連接 [暫時隱藏]
        //Start.getInstance().testDatabaseConnection();
    }

    public static void main(String[] args) {
        checkSingleInstance();
        final FontUIResource fontUIResource = new FontUIResource("微軟雅黑", 0, 12);
        WebLookAndFeel.globalControlFont = fontUIResource;
        WebLookAndFeel.globalTooltipFont = fontUIResource;
        WebLookAndFeel.globalAlertFont = fontUIResource;
        WebLookAndFeel.globalMenuFont = fontUIResource;
        WebLookAndFeel.globalAcceleratorFont = fontUIResource;
        WebLookAndFeel.globalTitleFont = fontUIResource;
        WebLookAndFeel.globalTextFont = fontUIResource;
        WebLookAndFeel.toolTipFont = fontUIResource;
        WebLookAndFeel.textPaneFont = fontUIResource;
        WebLookAndFeel.install();

        run();
    }

    public static void showMessage(String error, String title, int type) {
        WebOptionPane.showMessageDialog(null, error, title, type);
    }

    public String getServer_version() {
        return server_version;
    }

    //設置數據庫狀態 [暫時隱藏]
    /*
    public void setDataBaseStatus(DatabaseConnection.DataBaseStatus dataBaseStatus) {
        this.dataBaseStatus = dataBaseStatus;
    }
    
    private boolean testDatabaseConnection() {
        if (!dataBaseStatus.equals(DatabaseConnection.DataBaseStatus.連接成功)) {
            if (WebOptionPane.showConfirmDialog(instance, "數據庫連接失敗，將轉到配置頁面，請務必通過測試連接，否則服務端無法啟動", "", WebOptionPane.OK_CANCEL_OPTION) == WebOptionPane.OK_OPTION) {
                showConfigPanel();
            }
            return false;
        }
        return true;
    }*/
    private void display() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
        });
    }

    private StartFrame createProgressDialog() {
        final StartFrame progress = new StartFrame();
        progress.setUndecorated(true);
        progress.pack();
        progress.setResizable(false);
        progress.setLocationRelativeTo(null);
        progress.setVisible(true);

        return progress;
    }

    private Component createMainPane() {
        final WebPanel contentPane = new WebPanel();
        contentPane.setLayout(new BorderLayout());

        // 創建運行日誌
        final WebPanel runningLogPane = new WebPanel(new BorderLayout());
        runningLogPane.setPainter(new TitledBorderPainter("")).setMargin(2);
        runningLogPane.setPreferredSize(660, 300);
        textPane = new WebTextPane();
        textPane.setEditable(false);
        textPane.setComponentPopupMenu(new WebPopupMenu() {
            {
                add(new WebMenuItem("清屏") {
                    {
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                textPane.clear();
                            }
                        });
                    }
                });
            }
        });
        
        final WebScrollPane textPaneScroll = new WebScrollPane(textPane);
        textPaneScroll.createVerticalScrollBar();

        // 實現滾動條到達底部後自動滾動，否則不自動滾動
        textPaneScroll.addMouseWheelListener(e -> {
            WebScrollBar scrollBar = textPaneScroll.getWebVerticalScrollBar();
            autoScroll = e.getWheelRotation() != -1 && scrollBar.getMaximum() - scrollBar.getValue() <= scrollBar.getHeight();
        });

        runningLogPane.add(textPaneScroll);

        // 快捷菜單
        final WebPanel menuPane = new WebPanel(new BorderLayout(5, 5));
        menuPane.setUndecorated(false);
        menuPane.setRound(StyleConstants.largeRound);
        menuPane.setMargin(5);
        menuPane.setShadeWidth(5);
        //功能列表 [暫時隱藏]
        /*
        final WebButton serverConfig = new WebButton("配置參數", e -> new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                showConfigPanel();
                return null;
            }
        }.execute());
        serverConfig.setMargin(5, 10, 5, 10);
        serverConfig.setRound(15);

        final WebButton dataManage = new WebButton("數據管理", e -> {
            DataManagePanel dataManagePanel = DataManagePanel.getInstance();
            dataManagePanel.pack();
            dataManagePanel.setLocationRelativeTo(null);
            dataManagePanel.setVisible(true);
        });
        dataManage.setMargin(5, 10, 5, 10);


        final WebButton delUserDataManage = new WebButton("數據庫管理", e -> new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                ((WebButton) e.getSource()).setEnabled(false);
                DataBaseManagePanel dataBaseManagePanel = DataBaseManagePanel.getInstance();
                dataBaseManagePanel.pack();
                dataBaseManagePanel.setLocationRelativeTo(instance);
                dataBaseManagePanel.setVisible(true);
                ((WebButton) e.getSource()).setEnabled(true);
                return null;
            }
        }.execute());
        delUserDataManage.setMargin(5, 10, 5, 10);
         */
        final ImageIcon start = loadIcon("start.png");
        final ImageIcon stop = loadIcon("stop.png");
        final WebButton startServer = new WebButton("啟動服務端", start);
        final WebProgressOverlay progressOverlay = new WebProgressOverlay();
        progressOverlay.setConsumeEvents(false);
        startServer.setMargin(5, 10, 5, 10);
        startServer.setRound(15);
        progressOverlay.setComponent(startServer);
        progressOverlay.setOpaque(false);
        
        startServer.addActionListener(e -> {
            //測試數據庫連接 [暫時隱藏]
            /*if (!testDatabaseConnection()) {
                return;
            }*/
            boolean showLoad = !progressOverlay.isShowLoad();
            if (showLoad) {
                //開始運行時間 [暫時隱藏]
                //startRunTime();
                start_thread.start();
            } else {
                //伺服器關閉 [暫時隱藏]
                /*final String input = WebOptionPane.showInputDialog(instance, "關閉倒計時(分鐘)：", 0);
                if (input == null) {
                    return;
                }
                startServer.setEnabled(false);
                final int time = Integer.valueOf(StringUtil.isNumber(input) ? input : "0");
                final ShutdownServer si = ShutdownServer.getInstance();
                if (si == null) {
                    WebOptionPane.showMessageDialog(instance, "停止服務端發生錯誤，服務端似乎沒有啟動？\r\n\r\n請關閉服務端，確保進程內的java.exe和javaw.exe進程完全關閉，再啟動服務端試試吧~", "錯誤", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
                si.setTime(time);
                Thread stop_thread = new Thread(() -> shutdownServer = Timer.GuiTimer.getInstance().register(() -> {
                    ShutdownServer.getInstance().shutdown();
                    if (si.getTime() > 0) {
                        System.out.println("距離服務端完全關閉還剩 " + si.getTime() + " 分鐘，已通知玩家，請耐心等待...");
                    } else {
                        shutdownServer.cancel(false);
                        startRunTime.cancel(false);
                    }
                    si.setTime(si.getTime() - 1);
                }, 60000));
                stop_thread.start();
                try {
                    stop_thread.join();
                } catch (InterruptedException e1) {
                    //log.error("停止服務端失敗", e);
                }*/
//                    start_thread.interrupt();
//                    try {
//                        start_thread.join();
//                    } catch (InterruptedException e1) {
//                        e1.printStackTrace();
//                    }
            }

            progressOverlay.setShowLoad(showLoad);
            startServer.setText(showLoad ? "停止服務端" : "啟動服務端");
            startServer.setIcon(showLoad ? stop : start);
        });

        menuPane.add(new GroupPanel(false,
                //功能列表 [暫時隱藏]
                /*serverConfig,
                        new WebSeparator(false, true).setMargin(4, 0, 4, 0),
                        dataManage,
                        delUserDataManage,*/
                new WebSeparator(false, true).setMargin(4, 0, 0, 0)),
                BorderLayout.NORTH);
        menuPane.add(new GroupPanel(false, new WebSeparator(false, true).setMargin(4, 0, 4, 0), progressOverlay), BorderLayout.SOUTH);

        contentPane.add(runningLogPane, BorderLayout.CENTER);
        contentPane.add(menuPane, BorderLayout.EAST);

        // 設置默認焦點
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                startServer.requestFocus();
            }
        });

        return contentPane;
    }

    //創建在線狀態 [暫時隱藏]
    /*
    private Component createOnlineStatus() {
        final GroupPanel groupPanel = new GroupPanel(5);
        groupPanel.setPainter(new TitledBorderPainter("在線人數"));
        groupPanel.setMargin(5);
        labels = new WebHotkeyLabel[ServerConfig.CHANNEL_PORTS + 1];
        for (int i = 0; i <= ServerConfig.CHANNEL_PORTS; i++) {
            final WebHotkeyLabel label = new WebHotkeyLabel("頻道" + (i + 1) + " : 0");
            labels[i] = label;
            groupPanel.add(label);
        }
        return groupPanel;
    }*/
    //設置在線狀態 [暫時隱藏]
    /*
    public void setupOnlineStatus(final int channel) {
        final ChannelServer channelServer = ChannelServer.getInstance(channel);
        if (channelServer == null) {
            return;
        }
        final PlayerStorage.PlayerObservable playerObservable = channelServer.getPlayerStorage().getPlayerObservable();
        Observer observer = (o, arg) -> labels[channel - 1].setText("頻道" + channel + " : " + playerObservable.getCount());
        playerObservable.addObserver(observer);
    }
     */
    private Component createBroadCastMsg() {
        final WebPanel contentPanel = new WebPanel(new BorderLayout(5, 5));
        contentPanel.setPainter(new TitledBorderPainter("系統公告"));
        contentPanel.setMargin(5);

        String[] items = {"頂部黃色公告", "信息提示框", "藍色公告", "紅色公告", "白色公告"};

        final WebComboBox comboBox = new WebComboBox(items);
        contentPanel.add(comboBox, BorderLayout.WEST);

        final WebTextField textField = new WebTextField(ServerConfig.LOGIN_SERVERMESSAGE);
        textField.setInputPrompt("點此處輸入要發送消息的內容...");
        textField.setHideInputPromptOnFocus(false);
        contentPanel.add(textField, BorderLayout.CENTER);

        comboBox.addItemListener(e -> {
            if (e.getItem().equals("頂部黃色公告")) {
                textField.setText(ServerConfig.LOGIN_SERVERMESSAGE);
            } else {
                textField.setText("");
            }
        });

        final WebButton send = new WebButton("發送消息", e -> new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                if (!startFinish) {
                    WebOptionPane.showMessageDialog(instance, "服務端暫未啟動，無法使用該功能");
                    return null;
                }
                //發送消息 [暫時隱藏]
                /*String msg = textField.getText();
                byte[] packet = new byte[0];
                switch (comboBox.getSelectedIndex()) {
                    case 0:
                        ServerConfig.LOGIN_SERVERMESSAGE = msg;
                        Config.setProperty("login.server.message", msg);
                        packet = MaplePacketCreator.serverMessage(msg);
                        break;
                    case 1:
                        packet = MaplePacketCreator.serverNotice(1, msg);
                        break;
                    case 2:
                        packet = MaplePacketCreator.serverNotice(6, msg);
                        break;
                    case 3:
                        packet = MaplePacketCreator.serverNotice(5, msg);
                        break;
                    case 4:
                        packet = MaplePacketCreator.spouseMessage(0x0A, msg);
                        break;
                }
                WorldBroadcastService.getInstance().broadcastMessage(packet);*/
                WebOptionPane.showMessageDialog(instance, "發送完成");
                return null;
            }
        }.execute());
        contentPanel.add(send, BorderLayout.EAST);

        return contentPanel;
    }

    //創建狀態欄 [暫時隱藏]
    /*
    private Component createStatusBar() {
        final WebPanel contentPane = new WebPanel(new BorderLayout(5, 5));
        final WebStatusBar statusBar = new WebStatusBar();

        runningTimelabel = new WebHotkeyLabel("運行總時間: 00天00:00:00");
        statusBar.addToEnd(runningTimelabel);
        statusBar.addSeparatorToEnd();

        WebMemoryBar memoryBar = new WebMemoryBar();
        memoryBar.setShowMaximumMemory(false);
        memoryBar.setPreferredWidth(memoryBar.getPreferredSize().width + 20);
        statusBar.addToEnd(memoryBar);

        contentPane.add(createBroadCastMsg(), BorderLayout.NORTH);
        contentPane.add(createOnlineStatus(), BorderLayout.CENTER);
        contentPane.add(statusBar, BorderLayout.SOUTH);

        return contentPane;
    }

    private void startRunTime() {
        starttime = System.currentTimeMillis();
        startRunTime = Timer.GuiTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                runningTimelabel.setText(formatDuring(System.currentTimeMillis() - starttime));
            }

            private String formatDuring(long mss) {
                long days = mss / (1000 * 60 * 60 * 24);
                long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (mss % (1000 * 60)) / 1000;
                return "運行時長: " + (days / 10 == 0 ? "0" : "") + days + "天" + (hours / 10 == 0 ? "0" : "") + hours + ":" + (minutes / 10 == 0 ? "0" : "") + minutes + ":"
                        + (seconds / 10 == 0 ? "0" : "") + seconds;
            }
        }, 1000);
    }
     */
    private static class StartThread implements Runnable {

        @Override
        public void run() {
            try {
                System.out.println("準備啟動服務端...");
                server.Start.main(null);
                //初始化服務器 [暫時隱藏]
                /*if (!InitializeServer.Initial()) {
                    System.out.println("服務端初始化失敗。");
                    return;
                }*/

                System.err.println("服務端啟動完成！");
                startFinish = true;
            } catch (Exception e) {
                System.err.println("服務端啟動失敗！");
                //log.fatal("服務端啟動失敗", e);
            }
        }
    }

    static class NewOutputStram extends OutputStream {

        private final byte type;

        public NewOutputStram(byte type) {
            this.type = type;
        }

        @Override
        public void write(int b) throws IOException {

        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
//            super.write(b, off, len);
            final SimpleAttributeSet set = new SimpleAttributeSet();
            switch (type) {
                case 0:
                    javax.swing.text.StyleConstants.setForeground(set, Color.BLACK);
                    break;
                case 1:
                    javax.swing.text.StyleConstants.setForeground(set, Color.RED);
                    break;
                case 2:
                    javax.swing.text.StyleConstants.setForeground(set, Color.BLUE);
                    break;
            }

            try {
                WebTextPane textPane = Start.getInstance().textPane;

                textPane.getDocument().insertString(textPane.getDocument().getLength(), new String(b, off, len), set);
                if (Start.getInstance().autoScroll) {
                    textPane.setCaretPosition(textPane.getDocument().getLength());
                }
            } catch (BadLocationException e) {
                //log.fatal("控制台輸出失敗", e);
            }
        }
    }

    private static class Shutdown implements Runnable {

        @Override
        public void run() {
            ShutdownServer.getInstance().run();
        }
    }

    public class StartFrame extends WebFrame {

        private final WebLabel titleText;
        private final WebProgressBar progressBar;
        private final ImageIcon background;

        {
            background = loadIcon("LOGO.png");
        }

        StartFrame() {
            super();

            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setPreferredSize(new Dimension(background.getIconWidth(), background.getIconHeight()));

            BackgroundPanel backgroundPanel = new BackgroundPanel();

            backgroundPanel.setLayout(new BorderLayout());

            titleText = new WebLabel("正在啟動……", SwingConstants.CENTER).setMargin(0, 0, 3, 0);
            titleText.setForeground(Color.BLUE);
            titleText.setFont(new FontUIResource("微軟雅黑", 0, 12));
            progressBar = new WebProgressBar(0, 100);
            progressBar.setPreferredHeight(5);
            progressBar.setStringPainted(false);
            progressBar.setRound(0);
            progressBar.setValue(0);
            progressBar.setShadeWidth(0);
            progressBar.setBorderPainted(false);
            progressBar.setProgressBottomColor(Color.CYAN);
            progressBar.setProgressTopColor(Color.BLACK);

            backgroundPanel.add(new GroupPanel(false, titleText, progressBar), BorderLayout.SOUTH);

            add(backgroundPanel);
        }

        public void setText(String text) {
            this.titleText.setText(text);
        }

        public void setProgress(int value) {
            this.progressBar.setValue(value);
        }

        private class BackgroundPanel extends WebPanel {

            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(background.getImage(), 0, 0, null);
            }
        }
    }

    public class ProgressBarObservable extends Observable {

        private String text;
        private int progress;

        public int getProgress() {
            return progress;
        }

        public void setProgress(Pair<String, Integer> value) {
            this.text = value.getLeft();
            this.progress = value.getRight();
            setChanged();
            notifyObservers(value);
        }
    }

    private class ProgressBarObserver implements Observer {

        ProgressBarObserver(ProgressBarObservable progressBarObservable) {
            progressBarObservable.addObserver(this);
        }

        public void deleteObserver(ProgressBarObservable progressBarObservable) {
            progressBarObservable.deleteObserver(this);
        }

        @Override
        public void update(Observable o, Object arg) {
            if (arg instanceof Pair) {
                Pair pair = (Pair) arg;
                progress.setText((String) pair.getLeft());
                progress.setProgress((Integer) pair.getRight());
            }
        }
    }
}
