import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new 遊戲框架().啟動());
    }
}

class 遊戲框架 {
    private static final Color 背景色 = new Color(12, 16, 28);
    private static final Color 面板色 = new Color(23, 29, 46);
    private static final Color 文字色 = new Color(237, 239, 248);
    private static final Color 柔和文字色 = new Color(154, 165, 187);
    private static final Color 金色 = new Color(245, 190, 91);
    private static final Font 標題字體 = new Font("微軟正黑體", Font.BOLD, 24);
    private static final Font 內文字體 = new Font("微軟正黑體", Font.PLAIN, 15);
    private JFrame 主框架;
    private JLabel 狀態標籤;
    private JLabel 金幣標籤;
    private 玩家 玩家;
    private 敵人 當前敵人;
    private List<盟友> 盟友列表;
    private Random 隨機數;
    private JPanel 戰鬥畫面;
    private JTextArea 戰鬥日誌;
    private JProgressBar 玩家生命條;
    private JProgressBar 敵人生命條;
    private int 玩家最大生命;
    private JPanel 戰鬥場景;
    private JButton 音樂按鈕;
    private final 遊戲音效 音效 = new 遊戲音效();
    private Timer 場景動畫計時器;
    private Timer 戰鬥動畫計時器;
    private int 戰鬥動畫階段;
    private double 戰鬥動畫進度;

    public 遊戲框架() {
        this.主框架 = new JFrame("冒險遊戲");
        this.狀態標籤 = new JLabel("歡迎來到冒險遊戲！", JLabel.CENTER);
        this.盟友列表 = new ArrayList<>();
        this.隨機數 = new Random();
        初始化();
    }

    public void 啟動() {
        String 玩家名稱 = 顯示角色建立畫面();
        this.玩家 = new 玩家(玩家名稱);
        更新主畫面資訊();
        音效.設定背景音樂(true);
        音樂按鈕.setText("♫ BGM ON");
        音樂按鈕.setForeground(金色);
        主框架.setVisible(true);
    }

    private String 顯示角色建立畫面() {
        JTextField 名稱輸入框 = new JTextField("勇者", 16);
        名稱輸入框.setFont(new Font("微軟正黑體", Font.PLAIN, 18));
        名稱輸入框.setForeground(文字色);
        名稱輸入框.setCaretColor(金色);
        名稱輸入框.setBackground(new Color(17, 22, 36));
        名稱輸入框.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(76, 86, 112), 1, true),
                new EmptyBorder(10, 12, 10, 12)));

        JPanel 卡片 = new JPanel(new BorderLayout(0, 18));
        卡片.setBackground(背景色);
        卡片.setBorder(new EmptyBorder(22, 26, 22, 26));
        JLabel 標題 = new JLabel("<html><div style='text-align:center'>"
                + "<span style='color:#f5be5b;font-size:12px'>✦　EMBERFALL　✦</span><br><br>"
                + "<span style='color:#edf0f8;font-size:26px'>你的傳說，即將啟程</span><br>"
                + "<span style='color:#9aa5bb;font-size:13px'>為這位邊境冒險者取個名字</span></div></html>",
                JLabel.CENTER);
        卡片.add(標題, BorderLayout.NORTH);
        JPanel 輸入區 = new JPanel(new BorderLayout(0, 8));
        輸入區.setOpaque(false);
        JLabel 名稱提示 = new JLabel("  冒險者名稱");
        名稱提示.setForeground(柔和文字色);
        名稱提示.setFont(內文字體);
        輸入區.add(名稱提示, BorderLayout.NORTH);
        輸入區.add(名稱輸入框, BorderLayout.CENTER);
        卡片.add(輸入區, BorderLayout.CENTER);
        JLabel 提示 = new JLabel("  ✧　踏入迷霧邊境，遇見未知的命運。");
        提示.setForeground(new Color(123, 137, 166));
        提示.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        卡片.add(提示, BorderLayout.SOUTH);

        int 選擇 = JOptionPane.showConfirmDialog(主框架, 卡片, "建立冒險者",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        String 名稱 = 選擇 == JOptionPane.OK_OPTION ? 名稱輸入框.getText().trim() : "";
        return 名稱.isEmpty() ? "勇者" : 名稱;
    }

    private void 初始化() {
        停止場景動畫();
        停止戰鬥動畫();
        主框架.setSize(980, 720);
        主框架.setMinimumSize(new Dimension(820, 620));
        主框架.setLocationRelativeTo(null);
        主框架.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        主框架.getContentPane().removeAll();
        主框架.getContentPane().setBackground(背景色);

        JPanel 根面板 = new JPanel(new BorderLayout(0, 20));
        根面板.setBackground(背景色);
        根面板.setBorder(new EmptyBorder(24, 34, 24, 34));
        根面板.add(建立頂部列(), BorderLayout.NORTH);

        JPanel 內容 = new JPanel(new BorderLayout(0, 20));
        內容.setOpaque(false);
        內容.add(建立主視覺(), BorderLayout.CENTER);
        內容.add(建立主選單(), BorderLayout.SOUTH);
        根面板.add(內容, BorderLayout.CENTER);

        JLabel 頁尾 = new JLabel("  ✦  踏入迷霧，書寫你的傳說  ✦", JLabel.CENTER);
        頁尾.setForeground(柔和文字色);
        頁尾.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
        根面板.add(頁尾, BorderLayout.SOUTH);
        主框架.add(根面板);
        主框架.revalidate();
        主框架.repaint();
    }

    private JPanel 建立頂部列() {
        JPanel 頂部 = new JPanel(new BorderLayout());
        頂部.setOpaque(false);
        JLabel 標誌 = new JLabel("<html><span style='color:#f5be5b'>✦ EMBERFALL</span>"
                + "<span style='color:#9aa5bb'>　冒險者公會</span></html>");
        標誌.setFont(new Font("微軟正黑體", Font.BOLD, 17));
        頂部.add(標誌, BorderLayout.WEST);

        JPanel 右側 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        右側.setOpaque(false);
        狀態標籤.setFont(內文字體);
        狀態標籤.setForeground(柔和文字色);
        右側.add(狀態標籤);
        金幣標籤 = new JLabel("◈ 50");
        金幣標籤.setFont(new Font("微軟正黑體", Font.BOLD, 15));
        金幣標籤.setForeground(金色);
        金幣標籤.setBorder(new EmptyBorder(7, 14, 7, 14));
        金幣標籤.setOpaque(true);
        金幣標籤.setBackground(new Color(54, 43, 35));
        右側.add(金幣標籤);
        音樂按鈕 = new JButton("♫ BGM");
        音樂按鈕.setFont(new Font("微軟正黑體", Font.BOLD, 12));
        音樂按鈕.setForeground(柔和文字色);
        音樂按鈕.setBackground(面板色);
        音樂按鈕.setFocusPainted(false);
        音樂按鈕.setBorder(new LineBorder(new Color(55, 65, 87), 1, true));
        音樂按鈕.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        音樂按鈕.addActionListener(e -> {
            音效.設定背景音樂(!音效.背景音樂已啟用());
            音樂按鈕.setText(音效.背景音樂已啟用() ? "♫ BGM ON" : "♫ BGM OFF");
            音樂按鈕.setForeground(音效.背景音樂已啟用() ? 金色 : 柔和文字色);
        });
        右側.add(音樂按鈕);
        頂部.add(右側, BorderLayout.EAST);
        return 頂部;
    }

    private JPanel 建立主視覺() {
        JPanel 主視覺 = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint 漸層 = new GradientPaint(0, 0, new Color(35, 40, 65),
                        getWidth(), getHeight(), new Color(24, 30, 48));
                g2.setPaint(漸層);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g2.setColor(new Color(245, 190, 91, 24));
                int 中心X = getWidth() - 185;
                int 中心Y = getHeight() / 2;
                for (int i = 0; i < 5; i++) {
                    int 直徑 = 150 + i * 36;
                    g2.draw(new Ellipse2D.Double(中心X - 直徑 / 2.0, 中心Y - 直徑 / 2.0, 直徑, 直徑));
                }
                g2.setColor(new Color(255, 255, 255, 70));
                for (int i = 0; i < 36; i++) {
                    int x = (i * 137 + 41) % Math.max(getWidth(), 1);
                    int y = (i * 71 + 23) % Math.max(getHeight(), 1);
                    g2.fillOval(x, y, i % 4 == 0 ? 3 : 2, i % 4 == 0 ? 3 : 2);
                }
                繪製剪影(g2, 中心X, getHeight() - 24);
                g2.dispose();
            }
        };
        主視覺.setOpaque(false);
        主視覺.setBorder(new EmptyBorder(34, 42, 34, 42));

        JPanel 文字區 = new JPanel();
        文字區.setOpaque(false);
        文字區.setLayout(new BoxLayout(文字區, BoxLayout.Y_AXIS));
        JLabel 眉題 = new JLabel("THE FORGOTTEN FRONTIER");
        眉題.setForeground(金色);
        眉題.setFont(new Font("微軟正黑體", Font.BOLD, 12));
        JLabel 標題 = new JLabel("迷霧邊境");
        標題.setForeground(文字色);
        標題.setFont(new Font("微軟正黑體", Font.BOLD, 40));
        JLabel 副標題 = new JLabel("傳說，從踏出安全區的那一刻開始。");
        副標題.setForeground(new Color(194, 201, 218));
        副標題.setFont(new Font("微軟正黑體", Font.PLAIN, 17));
        JLabel 角色資訊 = new JLabel("  ⚔  " + (玩家 == null ? "等待新冒險者" : 玩家.獲取名稱() + "　·　冒險者")
                + "　　✦　隊伍 " + 盟友列表.size() + "/5");
        角色資訊.setForeground(柔和文字色);
        角色資訊.setFont(內文字體);
        眉題.setAlignmentX(Component.LEFT_ALIGNMENT);
        標題.setAlignmentX(Component.LEFT_ALIGNMENT);
        副標題.setAlignmentX(Component.LEFT_ALIGNMENT);
        角色資訊.setAlignmentX(Component.LEFT_ALIGNMENT);
        文字區.add(眉題);
        文字區.add(Box.createVerticalStrut(12));
        文字區.add(標題);
        文字區.add(Box.createVerticalStrut(8));
        文字區.add(副標題);
        文字區.add(Box.createVerticalStrut(24));
        文字區.add(角色資訊);

        GridBagConstraints 約束 = new GridBagConstraints();
        約束.gridx = 0;
        約束.gridy = 0;
        約束.weightx = 1;
        約束.weighty = 1;
        約束.anchor = GridBagConstraints.WEST;
        約束.fill = GridBagConstraints.HORIZONTAL;
        主視覺.add(文字區, 約束);
        return 主視覺;
    }

    private void 繪製剪影(Graphics2D g2, int x, int 底部) {
        g2.setColor(new Color(12, 16, 28, 190));
        int[] 山X = {x - 250, x - 80, x + 20, x + 170, x + 280};
        int[] 山Y = {底部 - 95, 底部 - 210, 底部 - 130, 底部 - 255, 底部 - 75};
        g2.fillPolygon(山X, 山Y, 山X.length);
        g2.setColor(new Color(12, 16, 28, 225));
        g2.fillRoundRect(x - 24, 底部 - 116, 48, 116, 18, 18);
        g2.fillOval(x - 28, 底部 - 145, 56, 56);
        g2.fillPolygon(new int[]{x - 24, x - 72, x - 18}, new int[]{底部 - 110, 底部 - 20, 底部 - 32}, 3);
        g2.fillPolygon(new int[]{x + 24, x + 72, x + 18}, new int[]{底部 - 110, 底部 - 20, 底部 - 32}, 3);
        g2.setColor(new Color(245, 190, 91, 160));
        g2.fillOval(x + 2, 底部 - 123, 5, 5);
    }

    private JPanel 建立主選單() {
        JPanel 選項面板 = new JPanel(new GridLayout(1, 4, 12, 0));
        選項面板.setOpaque(false);
        選項面板.setPreferredSize(new Dimension(0, 82));
        選項面板.add(建立選單按鈕("⚔", "探索邊境", "尋找寶藏、盟友與對手", new Color(204, 139, 73), this::探索));
        選項面板.add(建立選單按鈕("◈", "冒險者檔案", "查看角色與隊伍狀態", new Color(115, 145, 206),
                () -> 顯示角色狀態()));
        選項面板.add(建立選單按鈕("⚒", "補給商會", "強化裝備與補充生命", new Color(94, 171, 148), this::打開商店));
        選項面板.add(建立選單按鈕("↗", "離開旅程", "結束本次冒險", new Color(174, 111, 121), this::離開遊戲));
        return 選項面板;
    }

    private JButton 建立選單按鈕(String 圖示, String 標題, String 說明, Color 強調色, Runnable 動作) {
        JButton 按鈕 = new JButton("<html><div style='text-align:left;padding:4px 2px'>"
                + "<span style='font-size:20px;color:" + 色碼(強調色) + "'>" + 圖示 + "</span>"
                + "<span style='font-size:15px;color:#edf0f8'><b>　" + 標題 + "</b></span><br>"
                + "<span style='font-size:11px;color:#9aa5bb'>　　" + 說明 + "</span></div></html>");
        按鈕.setHorizontalAlignment(SwingConstants.LEFT);
        按鈕.setFocusPainted(false);
        按鈕.setBackground(面板色);
        按鈕.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(55, 65, 87), 1, true),
                new EmptyBorder(10, 12, 10, 8)));
        按鈕.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        按鈕.addActionListener(e -> 動作.run());
        return 按鈕;
    }

    private String 色碼(Color 顏色) {
        return String.format("#%02x%02x%02x", 顏色.getRed(), 顏色.getGreen(), 顏色.getBlue());
    }

    private void 更新主畫面資訊() {
        if (金幣標籤 != null && 玩家 != null) {
            金幣標籤.setText("◈ " + 玩家.獲取金幣());
        }
    }

    private void 顯示角色狀態() {
        JPanel 內容 = new JPanel(new BorderLayout(12, 12));
        內容.setBackground(背景色);
        內容.setBorder(new EmptyBorder(18, 20, 18, 20));
        JLabel 標題 = new JLabel("冒險者檔案");
        標題.setForeground(金色);
        標題.setFont(標題字體);
        JTextArea 資訊 = new JTextArea("  " + 玩家 + "\n\n  隊伍成員\n  " + 顯示盟友列表());
        資訊.setEditable(false);
        資訊.setFont(內文字體);
        資訊.setForeground(文字色);
        資訊.setBackground(面板色);
        資訊.setBorder(new EmptyBorder(16, 16, 16, 16));
        內容.add(標題, BorderLayout.NORTH);
        內容.add(資訊, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(主框架, 內容, "冒險者檔案", JOptionPane.PLAIN_MESSAGE);
    }

    
    private String 顯示盟友列表() {
        if (盟友列表.isEmpty()) {
            return "目前沒有盟友。";
        } else {
            StringBuilder 盟友顯示 = new StringBuilder();
            for (盟友 ally : 盟友列表) {
                盟友顯示.append(ally).append("\n");
            }
            return 盟友顯示.toString();
        }
    }

    private void 探索() {
        if (玩家.獲取生命() <= 0) {
            顯示戰鬥結局(false);
            return;
        }
        int 事件 = 隨機數.nextInt(4);
        switch (事件) {
            case 0:
                int 金幣 = 隨機數.nextInt(50) + 1;
                音效.播放音效("寶箱");
                顯示探索事件("遺忘的寶箱", "你在古老遺跡中發現寶箱，取得 " + 金幣 + " 枚金幣。",
                        "treasure", 金色, () -> {
                            玩家.增加金幣(金幣);
                            更新主畫面資訊();
                            初始化();
                            更新狀態("你找到了一個寶箱，獲得了 " + 金幣 + " 金幣！");
                        });
                break;
            case 1:
                當前敵人 = 隨機生成敵人();
                音效.播放音效("遭遇");
                顯示探索事件("危險正在逼近", "迷霧中傳來低吼——" + 當前敵人.獲取名稱() + " 擋住了去路！",
                        "enemy", new Color(231, 91, 105), () -> 戰鬥(當前敵人));
                break;
            case 2:
                音效.播放音效("微風");
                顯示探索事件("寂靜的邊境", "微風掠過荒野，除此之外，一切如常。",
                        "quiet", new Color(125, 163, 205), () -> {
                            初始化();
                            更新狀態("你在邊境走了一段路，什麼也沒發生。");
                        });
                break;
            case 3:
                if (盟友列表.size() < 5) {
                    盟友 新盟友 = 隨機生成盟友();
                    盟友列表.add(新盟友);
                    音效.播放音效("盟友");
                    顯示探索事件("新的同伴", 新盟友.獲取名稱() + " 加入了你的隊伍！隊伍現在有 "
                                    + 盟友列表.size() + " 位夥伴。",
                            "ally", new Color(111, 204, 165), () -> {
                                更新主畫面資訊();
                                初始化();
                                更新狀態("你遇到了一位盟友 " + 新盟友.獲取名稱() + "，加入你的隊伍！");
                            });
                } else {
                    音效.播放音效("微風");
                    顯示探索事件("隊伍已滿", "你遇見一位旅人，但隊伍已滿（最多 5 位），只能揮手道別。",
                            "quiet", new Color(125, 163, 205), () -> {
                                初始化();
                                更新狀態("你的盟友隊伍已滿，無法再加入新的盟友！");
                            });
                }
                break;
        }
    }

    private void 顯示探索事件(String 標題文字, String 說明文字, String 類型, Color 強調色, Runnable 完成動作) {
        停止場景動畫();
        JPanel 根面板 = new JPanel(new BorderLayout(0, 12));
        根面板.setBackground(背景色);
        根面板.setBorder(new EmptyBorder(24, 34, 24, 34));
        JLabel 眉題 = new JLabel("✦　迷霧邊境　/　遭遇事件");
        眉題.setForeground(強調色);
        眉題.setFont(new Font("微軟正黑體", Font.BOLD, 13));
        根面板.add(眉題, BorderLayout.NORTH);

        JPanel 中央區 = new JPanel(new BorderLayout(0, 10));
        中央區.setOpaque(false);
        JPanel 場景 = 建立探索事件場景(類型, 強調色);
        場景.setPreferredSize(new Dimension(0, 330));
        中央區.add(場景, BorderLayout.CENTER);
        JPanel 文字區 = new JPanel(new GridLayout(2, 1, 0, 4));
        文字區.setOpaque(false);
        JLabel 標題 = new JLabel(標題文字, JLabel.CENTER);
        標題.setForeground(文字色);
        標題.setFont(new Font("微軟正黑體", Font.BOLD, 27));
        JLabel 說明 = new JLabel(說明文字, JLabel.CENTER);
        說明.setForeground(柔和文字色);
        說明.setFont(內文字體);
        文字區.add(標題);
        文字區.add(說明);
        中央區.add(文字區, BorderLayout.SOUTH);
        根面板.add(中央區, BorderLayout.CENTER);

        JPanel 底部 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        底部.setOpaque(false);
        JButton 繼續按鈕 = 建立戰鬥按鈕("繼續旅程　→", new Color(71, 86, 116));
        if ("enemy".equals(類型)) {
            繼續按鈕.setText("魔物正在逼近……");
        }
        繼續按鈕.setEnabled(false);
        底部.add(繼續按鈕);
        根面板.add(底部, BorderLayout.SOUTH);
        設定主視圖(根面板);

        long 開始時間 = System.nanoTime();
        AtomicBoolean 已完成 = new AtomicBoolean(false);
        場景動畫計時器 = new Timer(35, e -> {
            double 秒數 = (System.nanoTime() - 開始時間) / 1_000_000_000.0;
            場景.repaint();
            if ("enemy".equals(類型) && 秒數 >= 1.65 && 已完成.compareAndSet(false, true)) {
                停止場景動畫();
                完成動作.run();
            } else if (!"enemy".equals(類型) && 秒數 >= 0.9) {
                繼續按鈕.setEnabled(true);
            }
        });
        場景動畫計時器.start();
        繼續按鈕.addActionListener(e -> {
            if (已完成.compareAndSet(false, true)) {
                停止場景動畫();
                完成動作.run();
            }
        });
    }

    private void 設定主視圖(Component 視圖) {
        主框架.getContentPane().removeAll();
        主框架.add(視圖, BorderLayout.CENTER);
        主框架.revalidate();
        主框架.repaint();
    }

    private void 停止場景動畫() {
        if (場景動畫計時器 != null) {
            場景動畫計時器.stop();
            場景動畫計時器 = null;
        }
    }

    private JPanel 建立探索事件場景(String 類型, Color 強調色) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                double t = (System.nanoTime() / 1_000_000_000.0) % 4;
                int cx = w / 2;
                int cy = h / 2;
                g2.setPaint(new GradientPaint(0, 0, new Color(36, 44, 68), w, h, new Color(16, 22, 38)));
                g2.fillRoundRect(0, 0, w, h, 26, 26);
                for (int i = 0; i < 7; i++) {
                    double radius = 48 + ((t * 42 + i * 31) % 230);
                    int alpha = Math.max(0, 90 - (int) radius / 3);
                    g2.setColor(new Color(強調色.getRed(), 強調色.getGreen(), 強調色.getBlue(), alpha));
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.draw(new Ellipse2D.Double(cx - radius, cy - radius, radius * 2, radius * 2));
                }
                g2.setColor(new Color(強調色.getRed(), 強調色.getGreen(), 強調色.getBlue(), 42));
                g2.fill(new Ellipse2D.Double(cx - 94, cy - 94, 188, 188));
                g2.setColor(強調色);
                if ("treasure".equals(類型)) {
                    g2.fillRoundRect(cx - 48, cy - 16, 96, 65, 12, 12);
                    g2.fillRoundRect(cx - 53, cy - 30, 106, 28, 12, 12);
                    g2.setColor(new Color(91, 61, 43));
                    g2.fillRect(cx - 5, cy - 12, 10, 52);
                    g2.setColor(new Color(255, 230, 150));
                    g2.fillOval(cx - 4, cy + 10, 8, 8);
                    for (int i = 0; i < 10; i++) {
                        double a = t * 1.7 + i * Math.PI / 5;
                        int sx = (int) (cx + Math.cos(a) * (112 + (i % 3) * 9));
                        int sy = (int) (cy + Math.sin(a) * (82 + (i % 3) * 8));
                        g2.fillOval(sx - 3, sy - 3, 6, 6);
                    }
                } else if ("ally".equals(類型)) {
                    g2.fillOval(cx - 28, cy - 56, 56, 56);
                    g2.fillRoundRect(cx - 43, cy + 2, 86, 75, 28, 28);
                    g2.setColor(文字色);
                    g2.fillOval(cx - 17, cy - 34, 6, 7);
                    g2.fillOval(cx + 11, cy - 34, 6, 7);
                    g2.setColor(new Color(255, 230, 150));
                    for (int i = 0; i < 8; i++) {
                        double a = t + i * Math.PI / 4;
                        int sx = (int) (cx + Math.cos(a) * 112);
                        int sy = (int) (cy + Math.sin(a) * 105);
                        g2.fillOval(sx - 3, sy - 3, 6, 6);
                    }
                } else if ("enemy".equals(類型)) {
                    double pulse = 1 + 0.06 * Math.sin(t * 5);
                    g2.translate(cx, cy);
                    g2.scale(pulse, pulse);
                    g2.fillRoundRect(-49, -30, 98, 102, 30, 34);
                    g2.fillOval(-54, -90, 108, 88);
                    g2.fillPolygon(new int[]{-43, -76, -39}, new int[]{-62, -116, -81}, 3);
                    g2.fillPolygon(new int[]{43, 76, 39}, new int[]{-62, -116, -81}, 3);
                    g2.setColor(new Color(255, 88, 85));
                    g2.fillOval(-27, -49, 12, 14);
                    g2.fillOval(15, -49, 12, 14);
                } else {
                    g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    for (int i = 0; i < 4; i++) {
                        double x = Math.sin(t + i * 1.3) * 54;
                        double y = Math.cos(t * 0.8 + i) * 39;
                        g2.drawArc(cx - 50 + (int) x, cy - 30 + (int) y, 100, 60, (int) (t * 70 + i * 80), 150);
                    }
                    g2.setColor(new Color(220, 230, 245));
                    g2.setFont(new Font("微軟正黑體", Font.BOLD, 62));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString("…", cx - fm.stringWidth("…") / 2, cy + 22);
                }
                g2.dispose();
            }
        };
    }

    private 敵人 隨機生成敵人() {
        // 設定敵人類型和對應的價格範圍
        String[] 敵人類型 = {"哥布林", "骷髏兵", "巨狼", "黑巫師"};
        int[] 基礎價格 = {10, 20, 30, 50};  // 每個敵人對應的基礎價格
        int[] 價格範圍 = {3, 5, 7, 10};    // 每個敵人價格波動範圍（±）
        
        // 設定價格的反比權重，價格越高，權重越低
        int[] 權重 = {10, 7, 4, 2};  // 越高價格的敵人權重越低
        int order = tool.權重選擇(權重);// 選擇權重敵人類型和對應的價格

        String 類型 = 敵人類型[order];
        int 價格 = 基礎價格[order] + 隨機數.nextInt(價格範圍[order] * 2 + 1) - 價格範圍[order];  // 隨機波動的價格
        
        // 根據價格設定敵人生命和攻擊力
        int 生命值 = 隨機數.nextInt(20) + 30 + 價格 / 2;  // 價格越高生命值越高
        int 攻擊力 = 隨機數.nextInt(10) + 5 + 價格 / 10;  // 價格越高攻擊力越高
        
        return new 敵人(類型, 生命值, 攻擊力, 價格);
    }
    

    private 盟友 隨機生成盟友() {
        String[] 盟友名字 = {"亞瑟", "莉亞", "梅根", "羅恩"};
        String 名字 = 盟友名字[隨機數.nextInt(盟友名字.length)];
        int 攻擊力 = 隨機數.nextInt(5) + 5;
        return new 盟友(名字, 攻擊力);
    }

    private void 打開商店() {
        Object[] 選項 = {
                "♥  生命藥水　　恢復 20 點生命　 ·　10 金幣",
                "⚔  鍛造武器　　攻擊力 +5　 ·　20 金幣",
                "⬟  守護盾牌　　防禦力 +3　 ·　15 金幣",
                "離開商會"
        };
        int 選擇 = JOptionPane.showOptionDialog(主框架,
                "  ◈　你的金幣：" + 玩家.獲取金幣() + "\n\n  選擇一項補給，準備好再出發。",
                "補給商會", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, 選項, 選項[3]);
        switch (選擇) {
            case 0:
                if (玩家.獲取金幣() >= 10) {
                    玩家.增加生命(20);
                    玩家.增加金幣(-10);
                    更新狀態("你購買了一瓶生命藥水，恢復了20點生命值。");
                } else {
                    更新狀態("金幣不足！");
                }
                break;
            case 1:
                if (玩家.獲取金幣() >= 20) {
                    玩家.升級攻擊力(5);
                    玩家.增加金幣(-20);
                    更新狀態("你的攻擊力提升了5點。");
                } else {
                    更新狀態("金幣不足！");
                }
                break;
            case 2:
                if (玩家.獲取金幣() >= 15) {
                    玩家.增加防禦力(3);
                    玩家.增加金幣(-15);
                    更新狀態("你購買了一個防禦盾牌，提升了3點防禦力。");
                } else {
                    更新狀態("金幣不足！");
                }
                break;
            default:
                更新狀態("你離開了商店。");
        }
        更新主畫面資訊();
    }

    private void 戰鬥(敵人 敵人) {
        停止場景動畫();
        停止戰鬥動畫();
        玩家最大生命 = Math.max(100, 玩家.獲取生命());
        戰鬥畫面 = new JPanel(new BorderLayout());
        戰鬥畫面.setBackground(背景色);
        戰鬥畫面.setBorder(new EmptyBorder(20, 28, 22, 28));
        JPanel 頂列 = new JPanel(new BorderLayout());
        頂列.setOpaque(false);
        JLabel 標題 = new JLabel("遭遇戰　/　" + 敵人.獲取名稱());
        標題.setForeground(文字色);
        標題.setFont(標題字體);
        JLabel 金幣 = new JLabel("◈ " + 玩家.獲取金幣() + " 金幣");
        金幣.setForeground(金色);
        金幣.setFont(內文字體);
        頂列.add(標題, BorderLayout.WEST);
        頂列.add(金幣, BorderLayout.EAST);

        JPanel 對決狀態 = new JPanel(new GridLayout(1, 2, 14, 0));
        對決狀態.setOpaque(false);
        玩家生命條 = 建立生命條(玩家最大生命, new Color(90, 202, 151));
        敵人生命條 = 建立生命條(Math.max(1, 敵人.獲取生命()), new Color(231, 91, 105));
        對決狀態.add(建立戰鬥狀態卡("✦　" + 玩家.獲取名稱(), "冒險者　·　防禦 " + 玩家.獲取防禦力(),
                玩家生命條, new Color(90, 202, 151)));
        對決狀態.add(建立戰鬥狀態卡("☠　" + 敵人.獲取名稱(), "邊境魔物　·　懸賞 " + 敵人.掉落() + " 金幣",
                敵人生命條, new Color(231, 91, 105)));

        戰鬥場景 = 建立戰鬥場景(敵人);
        戰鬥日誌 = new JTextArea(4, 50);
        戰鬥日誌.setEditable(false);
        戰鬥日誌.setLineWrap(true);
        戰鬥日誌.setWrapStyleWord(true);
        戰鬥日誌.setFont(內文字體);
        戰鬥日誌.setForeground(new Color(205, 212, 229));
        戰鬥日誌.setBackground(new Color(17, 22, 36));
        戰鬥日誌.setBorder(new EmptyBorder(12, 14, 12, 14));
        戰鬥日誌.setText("遭遇 " + 敵人.獲取名稱() + "！　選擇你的行動。");
        JScrollPane 滾動面板 = new JScrollPane(戰鬥日誌);
        滾動面板.setBorder(new LineBorder(new Color(48, 57, 78), 1, true));
        滾動面板.getViewport().setBackground(new Color(17, 22, 36));
        滾動面板.setPreferredSize(new Dimension(0, 112));

        JPanel 按鈕面板 = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        按鈕面板.setOpaque(false);
        JButton 攻擊按鈕 = 建立戰鬥按鈕("⚔　揮劍攻擊", new Color(164, 77, 78));
        JButton 逃跑按鈕 = 建立戰鬥按鈕("↩　撤退", new Color(52, 62, 84));
        攻擊按鈕.setEnabled(false);
        逃跑按鈕.setEnabled(false);
        按鈕面板.add(攻擊按鈕);
        按鈕面板.add(逃跑按鈕);

        攻擊按鈕.addActionListener(e -> 開始戰鬥攻擊(敵人, 攻擊按鈕, 逃跑按鈕));

        逃跑按鈕.addActionListener(e -> {
            停止戰鬥動畫();
            音效.播放音效("撤退");
            初始化();
            更新狀態("你逃離了戰鬥！");
        });

        JPanel 中央內容 = new JPanel(new BorderLayout(0, 14));
        中央內容.setOpaque(false);
        中央內容.add(戰鬥場景, BorderLayout.CENTER);
        中央內容.add(滾動面板, BorderLayout.SOUTH);
        戰鬥畫面.add(頂列, BorderLayout.NORTH);
        JPanel 戰鬥版面 = new JPanel(new BorderLayout(0, 16));
        戰鬥版面.setOpaque(false);
        戰鬥版面.add(對決狀態, BorderLayout.NORTH);
        戰鬥版面.add(中央內容, BorderLayout.CENTER);
        戰鬥版面.add(按鈕面板, BorderLayout.SOUTH);
        戰鬥畫面.add(戰鬥版面, BorderLayout.CENTER);
        設定主視圖(戰鬥畫面);

        戰鬥動畫階段 = 3;
        戰鬥動畫進度 = 0;
        音效.播放音效("戰鬥進場");
        戰鬥動畫計時器 = new Timer(30, e -> {
            戰鬥動畫進度 = Math.min(1, 戰鬥動畫進度 + 0.045);
            戰鬥場景.repaint();
            if (戰鬥動畫進度 >= 1) {
                停止戰鬥動畫();
                戰鬥動畫階段 = 0;
                攻擊按鈕.setEnabled(true);
                逃跑按鈕.setEnabled(true);
                戰鬥日誌.append("\n魔物現身！　你的回合。");
            }
        });
        戰鬥動畫計時器.start();
    }

    private void 開始戰鬥攻擊(敵人 敵人, JButton 攻擊按鈕, JButton 逃跑按鈕) {
        if (戰鬥動畫階段 != 0) {
            return;
        }
        攻擊按鈕.setEnabled(false);
        逃跑按鈕.setEnabled(false);
        戰鬥動畫階段 = 1;
        戰鬥動畫進度 = 0;
        boolean[] 已造成傷害 = {false};
        音效.播放音效("揮劍");
        戰鬥日誌.append("\n⚔　" + 玩家.獲取名稱() + " 衝向敵人，揮出一劍！");

        戰鬥動畫計時器 = new Timer(30, e -> {
            戰鬥動畫進度 = Math.min(1, 戰鬥動畫進度 + 0.055);
            if (!已造成傷害[0] && 戰鬥動畫階段 == 1 && 戰鬥動畫進度 >= 0.48) {
                已造成傷害[0] = true;
                int 傷害 = 玩家.攻擊();
                敵人.受到傷害(傷害);
                音效.播放音效("命中");
                戰鬥日誌.append("\n✦　造成 " + 傷害 + " 點傷害！");
                更新戰鬥生命條(敵人);
            } else if (!已造成傷害[0] && 戰鬥動畫階段 == 2 && 戰鬥動畫進度 >= 0.55) {
                已造成傷害[0] = true;
                int 傷害 = 敵人.攻擊();
                玩家.受到傷害(傷害);
                音效.播放音效("受擊");
                戰鬥日誌.append("\n☠　" + 敵人.獲取名稱() + " 反擊，造成 " + 傷害 + " 點傷害。");
                更新戰鬥生命條(敵人);
            }
            戰鬥場景.repaint();
            戰鬥日誌.setCaretPosition(戰鬥日誌.getDocument().getLength());

            if (戰鬥動畫進度 >= 1 && 戰鬥動畫階段 == 1) {
                if (敵人.獲取生命() <= 0) {
                    停止戰鬥動畫();
                    玩家.增加金幣(敵人.掉落());
                    更新主畫面資訊();
                    戰鬥日誌.append("\n✦　擊敗 " + 敵人.獲取名稱() + "！獲得 " + 敵人.掉落() + " 金幣。");
                    顯示戰鬥結局(true);
                } else {
                    戰鬥動畫階段 = 2;
                    戰鬥動畫進度 = 0;
                    已造成傷害[0] = false;
                    音效.播放音效("敵方攻擊");
                }
            } else if (戰鬥動畫進度 >= 1 && 戰鬥動畫階段 == 2) {
                停止戰鬥動畫();
                if (玩家.獲取生命() <= 0) {
                    戰鬥日誌.append("\n你已被擊敗……");
                    顯示戰鬥結局(false);
                } else {
                    戰鬥動畫階段 = 0;
                    戰鬥日誌.append("\n\n你的回合。");
                    攻擊按鈕.setEnabled(true);
                    逃跑按鈕.setEnabled(true);
                }
            }
        });
        戰鬥動畫計時器.start();
    }

    private void 更新戰鬥生命條(敵人 敵人) {
        玩家生命條.setValue(Math.max(0, Math.min(玩家最大生命, 玩家.獲取生命())));
        敵人生命條.setValue(Math.max(0, 敵人.獲取生命()));
        玩家生命條.setString("生命　" + Math.max(0, 玩家.獲取生命()) + " / " + 玩家最大生命);
        敵人生命條.setString("生命　" + Math.max(0, 敵人.獲取生命()) + " / " + 敵人生命條.getMaximum());
        更新主畫面資訊();
    }

    private void 停止戰鬥動畫() {
        if (戰鬥動畫計時器 != null) {
            戰鬥動畫計時器.stop();
            戰鬥動畫計時器 = null;
        }
    }

    private void 顯示戰鬥結局(boolean 勝利) {
        停止戰鬥動畫();
        音效.播放音效(勝利 ? "勝利" : "失敗");
        JPanel 結局畫面 = new JPanel(new BorderLayout(0, 18));
        結局畫面.setBackground(背景色);
        結局畫面.setBorder(new EmptyBorder(24, 34, 24, 34));
        JPanel 插畫 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                double t = (System.nanoTime() / 1_000_000_000.0) % 4;
                Color c = 勝利 ? 金色 : new Color(132, 145, 171);
                g2.setPaint(new GradientPaint(0, 0, new Color(37, 45, 70), 0, getHeight(), new Color(17, 22, 38)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 26, 26);
                for (int i = 0; i < 6; i++) {
                    double radius = 48 + ((t * 35 + i * 38) % 220);
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(),
                            Math.max(0, 80 - (int) radius / 3)));
                    g2.draw(new Ellipse2D.Double(cx - radius, cy - radius, radius * 2, radius * 2));
                }
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 38));
                g2.fill(new Ellipse2D.Double(cx - 88, cy - 88, 176, 176));
                g2.setColor(c);
                g2.setFont(new Font("Dialog", Font.BOLD, 76));
                String 符號 = 勝利 ? "✦" : "†";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(符號, cx - fm.stringWidth(符號) / 2, cy + 26);
                g2.dispose();
            }
        };
        JLabel 標題 = new JLabel(勝利 ? "勝利！" : "旅程暫歇", JLabel.CENTER);
        標題.setForeground(勝利 ? 金色 : 文字色);
        標題.setFont(new Font("微軟正黑體", Font.BOLD, 34));
        JLabel 說明 = new JLabel(勝利
                ? "你戰勝了 " + 當前敵人.獲取名稱() + "，帶著戰利品繼續前行。"
                : "你倒在邊境的迷霧中。重新整裝，再次出發吧。",
                JLabel.CENTER);
        說明.setForeground(柔和文字色);
        說明.setFont(內文字體);
        JPanel 文字區 = new JPanel(new GridLayout(2, 1, 0, 8));
        文字區.setOpaque(false);
        文字區.add(標題);
        文字區.add(說明);
        結局畫面.add(文字區, BorderLayout.NORTH);
        結局畫面.add(插畫, BorderLayout.CENTER);

        JPanel 按鈕區 = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        按鈕區.setOpaque(false);
        JButton 主要按鈕 = 建立戰鬥按鈕(勝利 ? "返回公會　→" : "重新開始旅程　↻",
                勝利 ? new Color(133, 101, 57) : new Color(71, 86, 116));
        主要按鈕.addActionListener(e -> {
            if (勝利) {
                初始化();
                更新狀態("你完成了戰鬥，獲得 " + 當前敵人.掉落() + " 金幣！");
            } else {
                停止場景動畫();
                音效.設定背景音樂(false);
                主框架.dispose();
                SwingUtilities.invokeLater(() -> new 遊戲框架().啟動());
            }
        });
        按鈕區.add(主要按鈕);
        if (!勝利) {
            JButton 離開按鈕 = 建立戰鬥按鈕("離開遊戲", new Color(52, 62, 84));
            離開按鈕.addActionListener(e -> 離開遊戲());
            按鈕區.add(離開按鈕);
        }
        結局畫面.add(按鈕區, BorderLayout.SOUTH);
        設定主視圖(結局畫面);
        場景動畫計時器 = new Timer(35, e -> 插畫.repaint());
        場景動畫計時器.start();
    }
    private JProgressBar 建立生命條(int 最大值, Color 顏色) {
        JProgressBar 生命條 = new JProgressBar(0, 最大值);
        生命條.setValue(最大值);
        生命條.setString("生命　" + 最大值 + " / " + 最大值);
        生命條.setStringPainted(true);
        生命條.setForeground(顏色);
        生命條.setBackground(new Color(43, 49, 67));
        生命條.setBorderPainted(false);
        生命條.setFont(new Font("微軟正黑體", Font.BOLD, 11));
        生命條.setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionForeground() {
                return Color.WHITE;
            }

            @Override
            protected Color getSelectionBackground() {
                return Color.WHITE;
            }
        });
        生命條.setPreferredSize(new Dimension(200, 21));
        return 生命條;
    }

    private JPanel 建立戰鬥狀態卡(String 名稱, String 說明, JProgressBar 生命條, Color 強調色) {
        JPanel 卡片 = new JPanel(new BorderLayout(0, 8));
        卡片.setBackground(面板色);
        卡片.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(54, 64, 86), 1, true),
                new EmptyBorder(12, 16, 12, 16)));
        JLabel 名稱標籤 = new JLabel(名稱);
        名稱標籤.setForeground(文字色);
        名稱標籤.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        JLabel 說明標籤 = new JLabel(說明);
        說明標籤.setForeground(強調色);
        說明標籤.setFont(new Font("微軟正黑體", Font.PLAIN, 11));
        JPanel 文字 = new JPanel(new BorderLayout(0, 4));
        文字.setOpaque(false);
        文字.add(名稱標籤, BorderLayout.NORTH);
        文字.add(說明標籤, BorderLayout.SOUTH);
        卡片.add(文字, BorderLayout.CENTER);
        卡片.add(生命條, BorderLayout.SOUTH);
        return 卡片;
    }

    private JButton 建立戰鬥按鈕(String 文字, Color 顏色) {
        JButton 按鈕 = new JButton(文字);
        按鈕.setFont(new Font("微軟正黑體", Font.BOLD, 15));
        按鈕.setForeground(文字色);
        按鈕.setBackground(顏色);
        按鈕.setFocusPainted(false);
        按鈕.setBorder(new EmptyBorder(12, 28, 12, 28));
        按鈕.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return 按鈕;
    }

    private JPanel 建立戰鬥場景(敵人 敵人) {
        JPanel 場景 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                double 時間 = System.nanoTime() / 1_000_000_000.0;
                Color 魔物色;
                switch (敵人.獲取名稱()) {
                    case "哥布林": 魔物色 = new Color(117, 184, 116); break;
                    case "骷髏兵": 魔物色 = new Color(210, 206, 181); break;
                    case "巨狼": 魔物色 = new Color(148, 160, 181); break;
                    default: 魔物色 = new Color(162, 124, 208);
                }
                g2.setPaint(new GradientPaint(0, 0, new Color(37, 45, 70), 0, h, new Color(20, 26, 42)));
                g2.fillRoundRect(0, 0, w, h, 24, 24);

                int cy = h / 2 + 5;
                int 玩家X = w / 2 - Math.min(205, w / 4);
                int 敵人X = w / 2 + Math.min(205, w / 4);
                double 英雄突進 = 戰鬥動畫階段 == 1
                        ? Math.sin(Math.min(1, 戰鬥動畫進度) * Math.PI) * 92 : 0;
                double 魔物突進 = 戰鬥動畫階段 == 2
                        ? Math.sin(Math.min(1, 戰鬥動畫進度) * Math.PI) * 62 : 0;
                double 入場比例 = 戰鬥動畫階段 == 3 ? 0.45 + 戰鬥動畫進度 * 0.55 : 1;
                double 搖晃 = 戰鬥動畫階段 == 2 && 戰鬥動畫進度 > 0.5
                        && 戰鬥動畫進度 < 0.72 ? Math.sin(時間 * 70) * 7 : 0;

                g2.setColor(new Color(245, 190, 91, 18));
                g2.fill(new Ellipse2D.Double(w / 2.0 - 165, cy - 155, 330, 300));
                g2.setColor(new Color(245, 190, 91, 85));
                g2.setStroke(new BasicStroke(1.2f));
                for (int i = 0; i < 4; i++) {
                    int r = 70 + i * 22;
                    g2.draw(new Ellipse2D.Double(w / 2.0 - r, cy - r * .68, r * 2, r * 1.36));
                }
                g2.setColor(new Color(255, 255, 255, 85));
                for (int i = 0; i < 22; i++) {
                    int sx = (i * 97 + 31) % Math.max(w, 1);
                    int sy = (int) ((i * 53 + 時間 * (8 + i % 5)) % Math.max(h, 1));
                    g2.fillOval(sx, sy, i % 5 == 0 ? 3 : 2, i % 5 == 0 ? 3 : 2);
                }

                g2.setColor(new Color(0, 0, 0, 75));
                g2.fillOval(玩家X - 56, cy + 64, 112, 19);
                g2.fillOval(敵人X - 70, cy + 70, 140, 20);
                繪製主角(g2, (int) (玩家X + 英雄突進), cy, 時間);

                Graphics2D 魔物畫筆 = (Graphics2D) g2.create();
                float 透明度 = (float) Math.min(1, Math.max(0, 入場比例));
                魔物畫筆.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 透明度));
                魔物畫筆.translate(敵人X - 魔物突進 + 搖晃, cy);
                魔物畫筆.scale(入場比例, 入場比例);
                魔物畫筆.setColor(MonsterAlpha(魔物色, 45));
                魔物畫筆.fill(new Ellipse2D.Double(-70, -88, 140, 150));
                魔物畫筆.setColor(戰鬥動畫階段 == 1 && 戰鬥動畫進度 > 0.42
                        && 戰鬥動畫進度 < 0.68 ? new Color(255, 235, 205) : 魔物色);
                魔物畫筆.fill(new RoundRectangle2D.Double(-48, -30, 96, 104, 35, 38));
                魔物畫筆.fillOval(-54, -92, 108, 91);
                if (!"骷髏兵".equals(敵人.獲取名稱())) {
                    魔物畫筆.fillPolygon(new int[]{-44, -74, -39}, new int[]{-61, -111, -83}, 3);
                    魔物畫筆.fillPolygon(new int[]{44, 74, 39}, new int[]{-61, -111, -83}, 3);
                }
                魔物畫筆.setColor(new Color(31, 35, 48));
                魔物畫筆.fillOval(-29, -52, 20, 24);
                魔物畫筆.fillOval(9, -52, 20, 24);
                魔物畫筆.setColor(new Color(255, 104, 99));
                魔物畫筆.fillOval(-23, -45, 7, 8);
                魔物畫筆.fillOval(15, -45, 7, 8);
                魔物畫筆.setColor(new Color(255, 255, 255, 110));
                魔物畫筆.setStroke(new BasicStroke(2f));
                魔物畫筆.drawArc(-17, -23, 34, 20, 200, 140);
                魔物畫筆.dispose();

                if (戰鬥動畫階段 == 1 && 戰鬥動畫進度 > 0.32 && 戰鬥動畫進度 < 0.86) {
                    float alpha = (float) Math.sin((戰鬥動畫進度 - 0.32) / 0.54 * Math.PI);
                    g2.setColor(new Color(255, 234, 162, (int) (220 * alpha)));
                    g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int 起點X = 玩家X + 45;
                    int 目標X = 敵人X - 48;
                    int 刀光Y = cy - 70 + (int) (戰鬥動畫進度 * 85);
                    g2.drawLine(起點X, 刀光Y + 45, 目標X, 刀光Y - 38);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawLine(起點X + 8, 刀光Y + 52, 目標X + 4, 刀光Y - 31);
                }
                if (戰鬥動畫階段 == 2 && 戰鬥動畫進度 > 0.48 && 戰鬥動畫進度 < 0.74) {
                    g2.setColor(new Color(255, 104, 99, 145));
                    g2.setFont(new Font("微軟正黑體", Font.BOLD, 18));
                    g2.drawString("受擊！", 玩家X - 25, cy - 103);
                }

                g2.setColor(文字色);
                g2.setFont(new Font("微軟正黑體", Font.BOLD, 17));
                FontMetrics fm = g2.getFontMetrics();
                String 主角名稱 = 玩家.獲取名稱();
                String 魔物名稱 = 敵人.獲取名稱();
                g2.drawString(主角名稱, 玩家X - fm.stringWidth(主角名稱) / 2, h - 22);
                g2.drawString(魔物名稱, 敵人X - fm.stringWidth(魔物名稱) / 2, h - 22);
                g2.dispose();
            }

            private Color MonsterAlpha(Color color, int alpha) {
                return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            }
        };
        場景.setOpaque(false);
        場景.setPreferredSize(new Dimension(0, 300));
        return 場景;
    }

    private void 繪製主角(Graphics2D g2, int x, int y, double 時間) {
        int 呼吸 = (int) (Math.sin(時間 * 3) * 2);
        g2.setColor(new Color(15, 20, 31));
        g2.fillPolygon(new int[]{x - 29, x - 41, x - 14, x + 24, x + 41, x + 27},
                new int[]{y - 10, y + 52, y + 77, y + 74, y + 30, y - 10}, 6);
        g2.setColor(new Color(66, 91, 132));
        g2.fillRoundRect(x - 24, y - 13 + 呼吸, 48, 79, 18, 19);
        g2.setColor(new Color(226, 190, 151));
        g2.fillOval(x - 22, y - 68 + 呼吸, 44, 48);
        g2.setColor(new Color(62, 48, 48));
        g2.fillArc(x - 24, y - 74 + 呼吸, 48, 36, 0, 180);
        g2.setColor(new Color(37, 42, 59));
        g2.fillOval(x - 13, y - 49 + 呼吸, 4, 5);
        g2.fillOval(x + 9, y - 49 + 呼吸, 4, 5);
        g2.setColor(new Color(170, 190, 218));
        g2.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + 14, y + 3 + 呼吸, x + 39, y - 18 + 呼吸);
        double 劍角 = 戰鬥動畫階段 == 1 ? -0.75 + 戰鬥動畫進度 * 1.6 : -0.35;
        int 劍柄X = x + 38;
        int 劍柄Y = y - 17 + 呼吸;
        int 劍尖X = 劍柄X + (int) (Math.cos(劍角) * 66);
        int 劍尖Y = 劍柄Y + (int) (Math.sin(劍角) * 66);
        g2.setColor(new Color(201, 168, 105));
        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(劍柄X - 7, 劍柄Y + 3, 劍柄X + 7, 劍柄Y - 3);
        g2.setColor(new Color(220, 232, 247));
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(劍柄X, 劍柄Y, 劍尖X, 劍尖Y);
    }

    private void 更新狀態(String 狀態) {
        String 顯示內容 = "<html>" + 狀態.replace("\n", "<br>") + "<br><br></html>";
        狀態標籤.setText(顯示內容);
    }


    private void 離開遊戲() {
        int 確認 = JOptionPane.showConfirmDialog(主框架, "確定要離開遊戲嗎？", "離開確認", JOptionPane.YES_NO_OPTION);
        if (確認 == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}

// 玩家類別
class 玩家 {
    private String 名稱;
    private int 生命;
    private int 攻擊力;
    private int 防禦力;
    private int 金幣;

    public 玩家(String 名稱) {
        this.名稱 = 名稱;
        this.生命 = 100;
        this.攻擊力 = 10;
        this.防禦力 = 5;
        this.金幣 = 50;
    }

    public int 攻擊() {
        return 攻擊力 + new Random().nextInt(5);  // 隨機傷害範圍
    }

    public void 受到傷害(int 傷害) {
        int 實際傷害 = Math.max(0, 傷害 - 防禦力); // 防禦減少傷害
        this.生命 -= 實際傷害;
    }

    public void 增加生命(int 數量) {
        this.生命 += 數量;
    }

    public void 增加金幣(int 數量) {
        this.金幣 += 數量;
    }

    public void 升級攻擊力(int 數量) {
        this.攻擊力 += 數量;
    }

    public void 增加防禦力(int 數量) {
        this.防禦力 += 數量;
    }

    public int 獲取生命() {
        return 生命;
    }

    public int 獲取金幣() {
        return 金幣;
    }

    public int 獲取防禦力() {
        return 防禦力;
    }

    public String 獲取名稱() {
        return 名稱;
    }

    @Override
    public String toString() {
        return "名稱: " + 名稱 + "\n生命: " + 生命 + "\n攻擊力: " + 攻擊力 + "\n防禦力: " + 防禦力 + "\n金幣: " + 金幣;
    }
}

// 敵人類別
class 敵人 {
    private String 類型;
    private int 生命;
    private int 攻擊力;
    private int 價值;

    public 敵人(String 類型, int 生命, int 攻擊力, int 價值) {
        this.類型 = 類型;
        this.生命 = 生命;
        this.攻擊力 = 攻擊力;
        this.價值 = 價值;
    }

    public int 攻擊() {
        return 攻擊力 + new Random().nextInt(5);
    }

    public void 受到傷害(int 傷害) {
        this.生命 -= 傷害;
    }

    public int 獲取生命() {
        return 生命;
    }

    public String 獲取名稱() {
        return 類型;
    }

    public int 掉落(){
        return 價值;
    }

    @Override
    public String toString() {
        return "敵人類型: " + 類型 + "\n生命: " + 生命 + "\n攻擊力: " + 攻擊力;
    }
}

// 盟友類別
class 盟友 {
    private String 名字;
    private int 攻擊力;

    public 盟友(String 名字, int 攻擊力) {
        this.名字 = 名字;
        this.攻擊力 = 攻擊力;
    }

    public String 獲取名稱(){
        return 名字;
    }

    @Override
    public String toString() {
        return "盟友: " + 名字 + " 攻擊力: " + 攻擊力;
    }
}

class tool{
    public static int 權重選擇(int[] 權重) {
        int 總權重 = 0;
        for (int w : 權重) {
            總權重 += w;  // 計算所有權重的總和
        }
        
        Random 隨機數 = new Random();
        int 隨機數值 = 隨機數.nextInt(總權重);  // 生成一個隨機數，範圍是[0, 總權重)
        
        int 累計權重 = 0;
        for (int i = 0; i < 權重.length; i++) {
            累計權重 += 權重[i];
            if (隨機數值 < 累計權重) {
                return i;  // 返回對應的索引
            }
        }

        return -1;
    }
}

class 遊戲音效 {
            private static final AudioFormat 音訊格式 = new AudioFormat(22050, 16, 1, true, false);
            private volatile boolean 背景音樂啟用;
            private volatile Clip 背景音樂;
            private boolean 背景音樂載入中;
            private final AtomicBoolean 已提示音訊錯誤 = new AtomicBoolean();

            public boolean 背景音樂已啟用() {
                return 背景音樂啟用;
            }

            public synchronized void 設定背景音樂(boolean 啟用) {
                背景音樂啟用 = 啟用;
                if (!啟用) {
                    if (背景音樂 != null) {
                        背景音樂.stop();
                        背景音樂.close();
                        背景音樂 = null;
                    }
                    return;
                }
                if (背景音樂 == null && !背景音樂載入中) {
                    背景音樂載入中 = true;
                    Thread 音樂執行緒 = new Thread(this::載入背景音樂, "emberfall-background-music");
                    音樂執行緒.setDaemon(true);
                    音樂執行緒.start();
                }
            }

            private void 載入背景音樂() {
                Clip 新音樂 = null;
                try {
                    新音樂 = AudioSystem.getClip();
                    byte[] 樂曲 = 合成音符(new int[]{
                            110, 165, 220, 165, 130, 196, 261, 196,
                            98, 147, 196, 147, 110, 165, 220, 165
                    }, 720, 0.13, true);
                    新音樂.open(音訊格式, 樂曲, 0, 樂曲.length);
                    synchronized (this) {
                        背景音樂載入中 = false;
                        if (!背景音樂啟用) {
                            新音樂.close();
                            return;
                        }
                        背景音樂 = 新音樂;
                        新音樂.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                } catch (LineUnavailableException ex) {
                    synchronized (this) {
                        背景音樂載入中 = false;
                    }
                    if (新音樂 != null) {
                        新音樂.close();
                    }
                    回報音訊錯誤(ex);
                }
            }

            public void 播放音效(String 類型) {
                int[] 音符;
                int 音符長度 = 105;
                double 音量 = 0.22;
                switch (類型) {
                    case "寶箱": 音符 = new int[]{523, 659, 784, 1046}; break;
                    case "盟友": 音符 = new int[]{392, 523, 659, 784}; break;
                    case "微風": 音符 = new int[]{392, 349, 330}; 音符長度 = 150; 音量 = 0.12; break;
                    case "遭遇": 音符 = new int[]{220, 185, 147, 110}; 音符長度 = 130; 音量 = 0.24; break;
                    case "戰鬥進場": 音符 = new int[]{330, 440, 554}; 音符長度 = 170; break;
                    case "揮劍": 音符 = new int[]{760, 420, 920}; 音符長度 = 55; 音量 = 0.2; break;
                    case "命中": 音符 = new int[]{185, 125, 250}; 音符長度 = 65; 音量 = 0.25; break;
                    case "敵方攻擊": 音符 = new int[]{196, 147, 110}; 音符長度 = 105; break;
                    case "受擊": 音符 = new int[]{260, 185, 140}; 音符長度 = 90; break;
                    case "勝利": 音符 = new int[]{523, 659, 784, 1046, 784, 1046}; 音符長度 = 190; 音量 = 0.24; break;
                    case "失敗": 音符 = new int[]{392, 349, 294, 220, 165}; 音符長度 = 220; 音量 = 0.2; break;
                    case "撤退": 音符 = new int[]{392, 330}; 音符長度 = 100; 音量 = 0.15; break;
                    default:
                        throw new IllegalArgumentException("未知的音效類型：" + 類型);
                }

                final int[] 要播放的音符 = 音符;
                final int 單音時長 = 音符長度;
                final double 音效音量 = 音量;
                Thread 音效執行緒 = new Thread(() -> {
                    SourceDataLine 音效線路 = null;
                    try {
                        音效線路 = AudioSystem.getSourceDataLine(音訊格式);
                        音效線路.open(音訊格式);
                        音效線路.start();
                        byte[] 音效資料 = 合成音符(要播放的音符, 單音時長, 音效音量, false);
                        int 位移 = 0;
                        while (位移 < 音效資料.length) {
                            位移 += 音效線路.write(音效資料, 位移, Math.min(4096, 音效資料.length - 位移));
                        }
                        音效線路.drain();
                    } catch (LineUnavailableException ex) {
                        回報音訊錯誤(ex);
                    } finally {
                        if (音效線路 != null) {
                            音效線路.close();
                        }
                    }
                }, "emberfall-sound-effect");
                音效執行緒.setDaemon(true);
                音效執行緒.start();
            }

            private byte[] 合成音符(int[] 音符, int 單音時長, double 音量, boolean 背景樂) {
                int 每音取樣數 = (int) (音訊格式.getSampleRate() * 單音時長 / 1000);
                byte[] 音訊 = new byte[每音取樣數 * 音符.length * 2];
                int 位移 = 0;
                for (int 頻率 : 音符) {
                    for (int i = 0; i < 每音取樣數; i++) {
                        double 時間 = i / 音訊格式.getSampleRate();
                        double 起音 = Math.min(1, i / (音訊格式.getSampleRate() * 0.025));
                        double 收音 = Math.min(1, (每音取樣數 - i) / (音訊格式.getSampleRate() * 0.075));
                        double 包絡 = Math.min(起音, 收音);
                        double 基音 = Math.sin(2 * Math.PI * 頻率 * 時間);
                        double 泛音 = Math.sin(2 * Math.PI * 頻率 * 2 * 時間) * 0.24;
                        double 低音 = 背景樂 ? Math.sin(2 * Math.PI * 頻率 * 0.5 * 時間) * 0.5 : 0;
                        short 樣本 = (short) (Math.max(-1, Math.min(1,
                                (基音 + 泛音 + 低音) * 包絡 * 音量)) * Short.MAX_VALUE);
                        音訊[位移++] = (byte) (樣本 & 0xff);
                        音訊[位移++] = (byte) ((樣本 >> 8) & 0xff);
                    }
                }
                return 音訊;
            }

            private void 回報音訊錯誤(LineUnavailableException ex) {
                if (已提示音訊錯誤.compareAndSet(false, true)) {
                    System.err.println("Emberfall audio is unavailable; the game will continue without audio.");
                    ex.printStackTrace(System.err);
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                            "無法使用音訊裝置，遊戲仍可繼續。請檢查系統音訊輸出設定。",
                            "音訊無法播放", JOptionPane.WARNING_MESSAGE));
                }
            }
}