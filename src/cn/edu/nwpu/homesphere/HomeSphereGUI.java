package cn.edu.nwpu.homesphere;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * HomeSphere 图形用户界面
 */
public class HomeSphereGUI extends JFrame {
    private HomeSphereSystem system;
    private HouseholdDataLoader dataLoader;

    // 界面组件
    private JButton loadFileButton;
    private JComboBox<String> sceneComboBox;
    private JButton triggerSceneButton;
    private JButton jsonButton, htmlButton, xmlButton;
    private JTextArea outputTextArea;
    private JLabel statusLabel;

    public HomeSphereGUI() {
        system = HomeSphereSystem.getInstance();
        dataLoader = new HouseholdDataLoader(system);

        initializeGUI();
        setupEventHandlers();
    }

    private void initializeGUI() {
        setTitle("HomeSphere 智能家居系统 v4.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 顶部控制面板
        mainPanel.add(createTopPanel(), BorderLayout.NORTH);

        // 中部格式选择面板
        mainPanel.add(createFormatPanel(), BorderLayout.CENTER);

        // 底部输出面板
        mainPanel.add(createOutputPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(new TitledBorder("数据加载与场景控制"));

        // 加载文件按钮
        loadFileButton = new JButton("加载本地文件");
        loadFileButton.setPreferredSize(new Dimension(120, 30));

        // 场景选择
        JLabel sceneLabel = new JLabel("智能场景:");
        sceneComboBox = new JComboBox<>();
        sceneComboBox.setPreferredSize(new Dimension(150, 30));

        // 触发按钮
        triggerSceneButton = new JButton("触发");
        triggerSceneButton.setPreferredSize(new Dimension(80, 30));
        triggerSceneButton.setEnabled(false);

        topPanel.add(loadFileButton);
        topPanel.add(sceneLabel);
        topPanel.add(sceneComboBox);
        topPanel.add(triggerSceneButton);

        return topPanel;
    }

    private JPanel createFormatPanel() {
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        formatPanel.setBorder(new TitledBorder("日志格式选择"));

        jsonButton = new JButton("JSON格式");
        htmlButton = new JButton("HTML格式");
        xmlButton = new JButton("XML格式");

        // 设置按钮大小
        Dimension buttonSize = new Dimension(120, 40);
        jsonButton.setPreferredSize(buttonSize);
        htmlButton.setPreferredSize(buttonSize);
        xmlButton.setPreferredSize(buttonSize);

        // 设置按钮颜色
        jsonButton.setBackground(new Color(70, 130, 180));
        htmlButton.setBackground(new Color(60, 179, 113));
        xmlButton.setBackground(new Color(205, 92, 92));
        jsonButton.setForeground(Color.BLACK);
        htmlButton.setForeground(Color.BLACK);
        xmlButton.setForeground(Color.BLACK);

        formatPanel.add(jsonButton);
        formatPanel.add(htmlButton);
        formatPanel.add(xmlButton);

        return formatPanel;
    }

    private JPanel createOutputPanel() {
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(new TitledBorder("输出显示"));
        // 创建输出文本区域，设置中文支持字体
        outputTextArea = new JTextArea(15, 60);
        outputTextArea.setEditable(false);
        // 设置支持中文的字体（避免乱码）
        outputTextArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        // 状态标签
        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        outputPanel.add(scrollPane, BorderLayout.CENTER);
        outputPanel.add(statusLabel, BorderLayout.SOUTH);
        return outputPanel;
    }

    private void setupEventHandlers() {
        // 加载文件按钮事件
        loadFileButton.addActionListener(e -> loadHouseholdData());

        // 触发场景按钮事件
        triggerSceneButton.addActionListener(e -> triggerSelectedScene());

        // 格式按钮事件
        jsonButton.addActionListener(e -> showLogsInFormat(new JsonRunningLogFormatter()));
        htmlButton.addActionListener(e -> showLogsInFormat(new HtmlRunningLogFormatter()));
        xmlButton.addActionListener(e -> showLogsInFormat(new XmlRunningLogFormatter()));
    }

    private void loadHouseholdData() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("选择 household.dat 文件");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("数据文件 (*.dat)", "dat"));
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                // 清空之前的数据
                system = HomeSphereSystem.getInstance();
                dataLoader = new HouseholdDataLoader(system);

                // 加载数据
                dataLoader.loadFromFile(selectedFile.getAbsolutePath());

                // 检查家庭是否创建成功
                if (system.getHousehold() == null) {
                    showError("家庭数据加载失败，请检查数据文件格式");
                    return;
                }

                // 自动登录管理员用户
                system.autoLoginAdmin();

                // 更新场景下拉框
                updateSceneComboBox();

                // 启用触发按钮
                triggerSceneButton.setEnabled(system.getHousehold().getAutoScenes().size() > 0);

                statusLabel.setText("数据加载成功: " + selectedFile.getName());
                outputTextArea.setText("数据加载成功！\n");
                outputTextArea.append("家庭: " + system.getHousehold().getAddress() + "\n");
                outputTextArea.append("房间数量: " + system.getHousehold().getRooms().size() + "\n");
                outputTextArea.append("设备数量: " + system.getAllDevices().size() + "\n");
                outputTextArea.append("场景数量: " + system.getHousehold().getAutoScenes().size() + "\n");

                // 显示当前登录用户
                if (system.getCurrentUser() != null) {
                    outputTextArea.append("当前用户: " + system.getCurrentUser().getUserName() + "\n");
                }
            }
        } catch (Exception ex) {
            showError("加载文件失败: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void updateSceneComboBox() {
        sceneComboBox.removeAllItems();
        for (AutomationScene scene : system.getHousehold().getAutoScenes()) {
            sceneComboBox.addItem(scene.getName() + " (ID: " + scene.getSceneld() + ")");
        }
    }

    private void triggerSelectedScene() {
        int selectedIndex = sceneComboBox.getSelectedIndex();
        if (selectedIndex >= 0) {
            AutomationScene scene = system.getHousehold().getAutoScenes().get(selectedIndex);
            try {
                scene.manualTrig();
                JOptionPane.showMessageDialog(this,
                        "场景 '" + scene.getName() + "' 执行成功！",
                        "执行成功",
                        JOptionPane.INFORMATION_MESSAGE);

                outputTextArea.append("场景 '" + scene.getName() + "' 已触发执行\n");
            } catch (Exception ex) {
                showError("触发场景失败: " + ex.getMessage());
            }
        }
    }

    private void showLogsInFormat(RunningLogFormatter formatter) {
        try {
            if (system.getHousehold() == null) {
                showError("请先加载家庭数据");
                return;
            }

            String formattedLogs = system.exportLogsWithFormatter(formatter);
            outputTextArea.setText(formattedLogs);
            statusLabel.setText("显示: " + formatter.getFormatterName());

        } catch (Exception ex) {
            showError("格式化日志失败: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
        statusLabel.setText("错误: " + message);
        outputTextArea.append("错误: " + message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            HomeSphereGUI gui = new HomeSphereGUI();
            gui.setVisible(true);
        });
    }
}