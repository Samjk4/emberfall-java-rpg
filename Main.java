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
    private JLabel 等級標籤;
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
    private String 當前攻擊方式 = "近戰";
    private double 當前傷害倍率 = 1;
    private int 盟友攻擊索引;
    private int 探索事件金幣;
    private int 最後獲得經驗;
    private int 最後升級數;
    private boolean 魔王已擊敗;

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
        JPanel 玩家資源 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        玩家資源.setOpaque(false);
        玩家資源.add(狀態標籤);
        等級標籤 = new JLabel("Lv. 1");
        等級標籤.setFont(new Font("微軟正黑體", Font.BOLD, 13));
        等級標籤.setForeground(new Color(177, 154, 235));
        玩家資源.add(等級標籤);
        右側.add(玩家資源);
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
        JLabel 角色資訊 = new JLabel("  ⚔  " + (玩家 == null ? "等待新冒險者" : 玩家.獲取名稱() + "　·　Lv."
                + 玩家.獲取等級() + "　" + 玩家.獲取武器名稱())
                + "　　✦　隊伍 " + 盟友列表.size() + "/5"
                + (玩家 != null && 玩家.獲取等級() >= 玩家.魔王出現等級() && !魔王已擊敗
                ? "　　☠　魔王的氣息降臨" : ""));
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
        if (等級標籤 != null && 玩家 != null) {
            等級標籤.setText("Lv. " + 玩家.獲取等級());
        }
    }

    private void 顯示角色狀態() {
        JPanel 內容 = new JPanel(new BorderLayout(12, 12));
        內容.setBackground(背景色);
        內容.setBorder(new EmptyBorder(18, 20, 18, 20));
        JLabel 標題 = new JLabel("冒險者檔案");
        標題.setForeground(金色);
        標題.setFont(標題字體);
        JTextArea 資訊 = new JTextArea("  " + 玩家 + "\n經驗: " + 玩家.獲取經驗() + " / "
                + 玩家.升級所需經驗() + "\n武器: " + 玩家.獲取武器名稱()
                + "\n技能: " + 玩家.顯示技能列表() + "\n\n  隊伍成員\n  " + 顯示盟友列表()
                + "\n魔王: " + (魔王已擊敗 ? "已討伐" : "Lv." + 玩家.魔王出現等級() + "現身"));
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
        if (玩家.獲取等級() >= 玩家.魔王出現等級() && !魔王已擊敗) {
            當前敵人 = new 敵人("魔王・夜燼", 260 + 玩家.獲取等級() * 18,
                    23 + 玩家.獲取等級() * 2, 300, true);
            音效.播放音效("遭遇");
            顯示探索事件("終焉之門", "大地震顫，魔王・夜燼現身了！這將是你最艱難的一戰。",
                    "enemy", new Color(195, 91, 128), () -> 戰鬥(當前敵人));
            return;
        }
        int 事件 = 隨機數.nextInt(4);
        switch (事件) {
            case 0:
                int 金幣 = 隨機數.nextInt(50) + 1;
                探索事件金幣 = 金幣;
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
                    音效.播放音效("盟友");
                    顯示探索事件("新的同伴", 新盟友.獲取名稱() + " 加入了你的隊伍！隊伍現在有 "
                                    + (盟友列表.size() + 1) + " 位夥伴。",
                            "ally", new Color(111, 204, 165), () -> {
                                盟友列表.add(新盟友);
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
            } else if (!"enemy".equals(類型) && 秒數 >= ("ally".equals(類型) ? 1.5 : 0.9)) {
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
                        double phase = (t * 1.15 + i * 0.19) % 1.8;
                        double side = Math.sin(i * 2.4) * 92;
                        int sx = (int) (cx + side + Math.sin(phase * 2.7 + i) * 15);
                        int sy = (int) (cy + 18 - phase * 100);
                        int size = 8 + (i % 3);
                        g2.setColor(new Color(255, 223 - (i % 3) * 12, 118));
                        g2.fillOval(sx - size / 2, sy - size / 2, size, size);
                        g2.setColor(new Color(255, 247, 195));
                        g2.drawLine(sx, sy - size / 2 + 1, sx, sy + size / 2 - 1);
                    }
                    g2.setColor(金色);
                    g2.setFont(new Font("微軟正黑體", Font.BOLD, 18));
                    String 獎勵文字 = "+" + 探索事件金幣 + " 金幣";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(獎勵文字, cx - fm.stringWidth(獎勵文字) / 2, cy - 92);
                } else if ("ally".equals(類型)) {
                    double 入場 = Math.min(1, (t % 2.4) / 1.1);
                    int 角色X = (int) (cx + (1 - 入場) * 190);
                    int 跳躍 = (int) (Math.abs(Math.sin(t * 4)) * 12);
                    g2.setColor(new Color(15, 20, 31));
                    g2.fillPolygon(new int[]{角色X - 30, 角色X - 45, 角色X - 25, 角色X + 28, 角色X + 42},
                            new int[]{cy + 2, cy + 68, cy + 82, cy + 78, cy + 28}, 5);
                    g2.setColor(強調色);
                    g2.fillRoundRect(角色X - 36, cy - 12 - 跳躍, 72, 81, 26, 28);
                    g2.setColor(new Color(226, 190, 151));
                    g2.fillOval(角色X - 28, cy - 67 - 跳躍, 56, 55);
                    g2.setColor(new Color(62, 48, 48));
                    g2.fillArc(角色X - 29, cy - 72 - 跳躍, 58, 34, 0, 180);
                    g2.setColor(文字色);
                    g2.fillOval(角色X - 16, cy - 42 - 跳躍, 6, 7);
                    g2.fillOval(角色X + 10, cy - 42 - 跳躍, 6, 7);
                    g2.setColor(new Color(226, 190, 151));
                    int 揮手Y = (int) (cy - 2 - 跳躍 + Math.sin(t * 8) * 14);
                    g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(角色X + 26, cy + 12 - 跳躍, 角色X + 47, 揮手Y - 38);
                    g2.fillOval(角色X + 41, 揮手Y - 45, 13, 13);
                    g2.setColor(new Color(255, 230, 150));
                    for (int i = 0; i < 8; i++) {
                        double a = t + i * Math.PI / 4;
                        int sx = (int) (角色X + Math.cos(a) * 72);
                        int sy = (int) (cy - 18 + Math.sin(a) * 85 - 跳躍);
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
        JDialog 商店視窗 = new JDialog(主框架, "補給商會", true);
        商店視窗.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel 根面板 = new JPanel(new BorderLayout(0, 16));
        根面板.setBackground(背景色);
        根面板.setBorder(new EmptyBorder(20, 22, 18, 22));

        JPanel 標題列 = new JPanel(new BorderLayout());
        標題列.setOpaque(false);
        JLabel 標題 = new JLabel("<html><span style='color:#f5be5b'>⚒　邊境補給商會</span>"
                + "<br><span style='color:#9aa5bb;font-size:12px'>鍛造傳奇武器，為遠征做好準備</span></html>");
        標題.setFont(標題字體);
        標題列.add(標題, BorderLayout.WEST);
        JLabel 金幣 = new JLabel("◈ " + 玩家.獲取金幣() + " 金幣");
        金幣.setForeground(金色);
        金幣.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        標題列.add(金幣, BorderLayout.EAST);
        根面板.add(標題列, BorderLayout.NORTH);

        JPanel 商品列表 = new JPanel(new GridLayout(0, 2, 12, 12));
        商品列表.setOpaque(false);
        JLabel 提示 = new JLabel("選擇商品購買。武器購買後立即裝備，部分商品需達到指定等級。");
        提示.setForeground(柔和文字色);
        提示.setFont(new Font("微軟正黑體", Font.PLAIN, 12));

        商品列表.add(建立商店商品("♥　生命藥水", "恢復 35 點生命", "30 金幣", new Color(90, 202, 151),
                () -> 購買商店商品("potion", 商店視窗, 金幣, 提示)));
        商品列表.add(建立商店商品("⚔　騎士長劍", "攻擊力 +6　·　近戰", "75 金幣", new Color(223, 180, 110),
                () -> 購買商店商品("sword", 商店視窗, 金幣, 提示)));
        商品列表.add(建立商店商品("✹　燼火巨刃", "攻擊力 +12　·　近戰", "145 金幣", new Color(236, 119, 86),
                () -> 購買商店商品("greatsword", 商店視窗, 金幣, 提示)));
        商品列表.add(建立商店商品("➶　月影長弓", "攻擊力 +9　·　解鎖遠程射擊", "155 金幣", new Color(111, 178, 215),
                () -> 購買商店商品("bow", 商店視窗, 金幣, 提示)));
        商品列表.add(建立商店商品("✧　星隕法杖", "攻擊力 +16　·　強化遠程攻擊", "260 金幣", new Color(172, 137, 226),
                () -> 購買商店商品("staff", 商店視窗, 金幣, 提示)));
        商品列表.add(建立商店商品("⬟　龍鱗護甲", "防禦力 +5", "120 金幣", new Color(105, 179, 157),
                () -> 購買商店商品("armor", 商店視窗, 金幣, 提示)));

        JScrollPane 商品捲動 = new JScrollPane(商品列表);
        商品捲動.setBorder(new LineBorder(new Color(48, 57, 78), 1, true));
        商品捲動.getViewport().setBackground(背景色);
        商品捲動.getVerticalScrollBar().setUnitIncrement(18);
        根面板.add(商品捲動, BorderLayout.CENTER);
        JPanel 底部 = new JPanel(new BorderLayout());
        底部.setOpaque(false);
        底部.add(提示, BorderLayout.CENTER);
        JButton 離開 = 建立戰鬥按鈕("離開商會", new Color(52, 62, 84));
        離開.addActionListener(e -> 商店視窗.dispose());
        底部.add(離開, BorderLayout.EAST);
        根面板.add(底部, BorderLayout.SOUTH);
        商店視窗.setContentPane(根面板);
        商店視窗.setSize(760, 560);
        商店視窗.setMinimumSize(new Dimension(690, 500));
        商店視窗.setLocationRelativeTo(主框架);
        商店視窗.setVisible(true);
        更新主畫面資訊();
    }

    private JButton 建立商店商品(String 名稱, String 效果, String 價格, Color 強調色, Runnable 購買動作) {
        JButton 商品 = new JButton("<html><div style='text-align:left;padding:4px'>"
                + "<span style='font-size:16px;color:" + 色碼(強調色) + "'><b>" + 名稱 + "</b></span><br>"
                + "<span style='font-size:12px;color:#c4cad8'>" + 效果 + "</span><br>"
                + "<span style='font-size:12px;color:#f5be5b'>◈　" + 價格 + "</span>"
                + "</div></html>");
        商品.setHorizontalAlignment(SwingConstants.LEFT);
        商品.setBackground(面板色);
        商品.setFocusPainted(false);
        商品.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(55, 65, 87), 1, true),
                new EmptyBorder(12, 14, 12, 14)));
        商品.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        商品.addActionListener(e -> 購買動作.run());
        return 商品;
    }

    private void 購買商店商品(String 商品, JDialog 商店視窗, JLabel 金幣標籤, JLabel 提示) {
        int 價格;
        String 結果;
        switch (商品) {
            case "potion": 價格 = 30; break;
            case "sword": 價格 = 75; break;
            case "greatsword": 價格 = 145; break;
            case "bow": 價格 = 155; break;
            case "staff": 價格 = 260; break;
            case "armor": 價格 = 120; break;
            default:
                throw new IllegalArgumentException("未知的商店商品：" + 商品);
        }

        if (玩家.獲取金幣() < 價格) {
            提示.setText("金幣不足，還差 " + (價格 - 玩家.獲取金幣()) + " 枚。");
            return;
        }

        switch (商品) {
            case "potion":
                玩家.增加生命(35);
                結果 = "生命藥水已使用，恢復生命。";
                break;
            case "sword":
                玩家.裝備武器("騎士長劍", 6, false);
                結果 = "已裝備騎士長劍，近戰攻擊力 +6。";
                break;
            case "greatsword":
                玩家.裝備武器("燼火巨刃", 12, false);
                結果 = "已裝備燼火巨刃，近戰攻擊力 +12。";
                break;
            case "bow":
                玩家.裝備武器("月影長弓", 9, true);
                結果 = "已裝備月影長弓，遠程攻擊力 +9。";
                break;
            case "staff":
                玩家.裝備武器("星隕法杖", 16, true);
                結果 = "已裝備星隕法杖，遠程攻擊力 +16。";
                break;
            case "armor":
                玩家.增加防禦力(5);
                結果 = "龍鱗護甲已穿戴，防禦力 +5。";
                break;
            default:
                throw new IllegalArgumentException("未知的商店商品：" + 商品);
        }

        玩家.增加金幣(-價格);
        金幣標籤.setText("◈ " + 玩家.獲取金幣() + " 金幣");
        更新主畫面資訊();
        提示.setText("✦　" + 結果);
        更新狀態(結果);
    }

    private void 戰鬥(敵人 敵人) {
        停止場景動畫();
        停止戰鬥動畫();
        玩家最大生命 = 玩家.獲取最大生命();
        戰鬥畫面 = new JPanel(new BorderLayout());
        戰鬥畫面.setBackground(背景色);
        戰鬥畫面.setBorder(new EmptyBorder(20, 28, 22, 28));
        JPanel 頂列 = new JPanel(new BorderLayout());
        頂列.setOpaque(false);
        JLabel 標題 = new JLabel((敵人.是否魔王() ? "魔王決戰　/　" : "遭遇戰　/　") + 敵人.獲取名稱());
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
        對決狀態.add(建立戰鬥狀態卡("✦　" + 玩家.獲取名稱() + "　Lv." + 玩家.獲取等級(),
                玩家.獲取武器名稱() + "　·　防禦 " + 玩家.獲取防禦力() + "　·　盟友 " + 盟友列表.size(),
                玩家生命條, new Color(90, 202, 151)));
        對決狀態.add(建立戰鬥狀態卡("☠　" + 敵人.獲取名稱(),
                敵人.是否魔王() ? "最終魔王　·　懸賞 " + 敵人.掉落() + " 金幣" : "邊境魔物　·　懸賞 " + 敵人.掉落() + " 金幣",
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

        JPanel 按鈕面板 = new JPanel(new FlowLayout(FlowLayout.CENTER, 7, 0));
        按鈕面板.setOpaque(false);
        List<JButton> 行動按鈕 = new ArrayList<>();
        JButton 近戰按鈕 = 建立戰鬥按鈕("⚔ 近戰", new Color(164, 77, 78));
        JButton 遠程按鈕 = 建立戰鬥按鈕("➶ 遠程", new Color(70, 111, 153));
        JButton 逃跑按鈕 = 建立戰鬥按鈕("↩　撤退", new Color(52, 62, 84));
        行動按鈕.add(近戰按鈕);
        行動按鈕.add(遠程按鈕);
        按鈕面板.add(近戰按鈕);
        按鈕面板.add(遠程按鈕);
        if (玩家.獲取等級() >= 3) {
            JButton 烈焰斬按鈕 = 建立戰鬥按鈕("✹ 烈焰斬", new Color(164, 83, 55));
            行動按鈕.add(烈焰斬按鈕);
            按鈕面板.add(烈焰斬按鈕);
            烈焰斬按鈕.addActionListener(e ->
                    開始戰鬥攻擊(敵人, "烈焰斬", 2.0, 行動按鈕, 逃跑按鈕));
        }
        if (玩家.獲取等級() >= 5) {
            JButton 奧術箭按鈕 = 建立戰鬥按鈕("✧ 奧術箭", new Color(115, 82, 177));
            行動按鈕.add(奧術箭按鈕);
            按鈕面板.add(奧術箭按鈕);
            奧術箭按鈕.addActionListener(e ->
                    開始戰鬥攻擊(敵人, "奧術箭", 2.4, 行動按鈕, 逃跑按鈕));
        }
        for (JButton 行動 : 行動按鈕) {
            行動.setEnabled(false);
        }
        按鈕面板.add(逃跑按鈕);

        近戰按鈕.addActionListener(e ->
                開始戰鬥攻擊(敵人, "近戰", 1, 行動按鈕, 逃跑按鈕));
        遠程按鈕.addActionListener(e ->
                開始戰鬥攻擊(敵人, "遠程", 0.8, 行動按鈕, 逃跑按鈕));

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
                設定戰鬥行動可用(行動按鈕, true);
                逃跑按鈕.setEnabled(true);
                戰鬥日誌.append("\n魔物現身！　你的回合。");
            }
        });
        戰鬥動畫計時器.start();
    }

    private void 開始戰鬥攻擊(敵人 敵人, String 攻擊方式, double 傷害倍率,
                            List<JButton> 行動按鈕, JButton 逃跑按鈕) {
        if (戰鬥動畫階段 != 0) {
            return;
        }
        設定戰鬥行動可用(行動按鈕, false);
        逃跑按鈕.setEnabled(false);
        當前攻擊方式 = 攻擊方式;
        當前傷害倍率 = 傷害倍率;
        盟友攻擊索引 = 0;
        音效.播放音效(攻擊方式.equals("近戰") ? "揮劍"
                : 攻擊方式.equals("遠程") ? "弓箭" : "魔法");
        戰鬥日誌.append("\n" + 攻擊方式圖示(攻擊方式) + "　" + 玩家.獲取名稱()
                + 使用攻擊描述(攻擊方式) + "！");
        開始戰鬥動畫階段(1, 敵人, 行動按鈕, 逃跑按鈕);
    }

    private void 開始戰鬥動畫階段(int 階段, 敵人 敵人, List<JButton> 行動按鈕, JButton 逃跑按鈕) {
        停止戰鬥動畫();
        戰鬥動畫階段 = 階段;
        戰鬥動畫進度 = 0;
        boolean[] 已造成傷害 = {false};
        戰鬥動畫計時器 = new Timer(30, e -> {
            戰鬥動畫進度 = Math.min(1, 戰鬥動畫進度 + 0.055);
            double 命中時間 = 階段 == 2 ? 0.55 : 0.48;
            if (!已造成傷害[0] && 戰鬥動畫進度 >= 命中時間) {
                已造成傷害[0] = true;
                if (階段 == 1) {
                    boolean 遠程攻擊 = "遠程".equals(當前攻擊方式) || "奧術箭".equals(當前攻擊方式);
                    int 傷害 = 玩家.攻擊(遠程攻擊, 當前傷害倍率);
                    敵人.受到傷害(傷害);
                    音效.播放音效("命中");
                    戰鬥日誌.append("\n✦　" + 當前攻擊方式 + "命中，造成 " + 傷害 + " 點傷害！");
                } else if (階段 == 4) {
                    盟友 同伴 = 盟友列表.get(盟友攻擊索引);
                    int 傷害 = 同伴.攻擊() + 玩家.獲取等級() / 2;
                    敵人.受到傷害(傷害);
                    音效.播放音效("盟友攻擊");
                    戰鬥日誌.append("\n✧　" + 同伴.獲取名稱() + " 協同攻擊，造成 " + 傷害 + " 點傷害！");
                } else {
                    int 傷害 = 敵人.攻擊();
                    玩家.受到傷害(傷害);
                    音效.播放音效("受擊");
                    戰鬥日誌.append("\n☠　" + 敵人.獲取名稱() + " 反擊，造成 " + 傷害 + " 點傷害。");
                }
                更新戰鬥生命條(敵人);
                戰鬥日誌.setCaretPosition(戰鬥日誌.getDocument().getLength());
            }
            戰鬥場景.repaint();

            if (戰鬥動畫進度 >= 1) {
                停止戰鬥動畫();
                if (階段 == 1 || 階段 == 4) {
                    if (敵人.獲取生命() <= 0) {
                        結算戰鬥勝利(敵人);
                    } else if (階段 == 1 && !盟友列表.isEmpty()) {
                        盟友攻擊索引 = 0;
                        開始盟友協戰(敵人, 行動按鈕, 逃跑按鈕);
                    } else if (階段 == 4 && ++盟友攻擊索引 < 盟友列表.size()) {
                        開始盟友協戰(敵人, 行動按鈕, 逃跑按鈕);
                    } else {
                        開始敵人回合(敵人, 行動按鈕, 逃跑按鈕);
                    }
                } else if (玩家.獲取生命() <= 0) {
                    戰鬥日誌.append("\n你已被擊敗……");
                    顯示戰鬥結局(false);
                } else {
                    戰鬥動畫階段 = 0;
                    戰鬥日誌.append("\n\n你的回合。");
                    設定戰鬥行動可用(行動按鈕, true);
                    逃跑按鈕.setEnabled(true);
                }
            }
        });
        戰鬥動畫計時器.start();
    }

    private void 開始盟友協戰(敵人 敵人, List<JButton> 行動按鈕, JButton 逃跑按鈕) {
        戰鬥動畫階段 = 4;
        戰鬥動畫進度 = 0;
        音效.播放音效("盟友攻擊");
        戰鬥日誌.append("\n✧　" + 盟友列表.get(盟友攻擊索引).獲取名稱() + " 衝上前線！");
        開始戰鬥動畫階段(4, 敵人, 行動按鈕, 逃跑按鈕);
    }

    private void 開始敵人回合(敵人 敵人, List<JButton> 行動按鈕, JButton 逃跑按鈕) {
        戰鬥日誌.append(敵人.是否魔王() ? "\n☠　魔王凝聚黑炎，發動攻擊！" : "\n☠　敵人發動反擊！");
        音效.播放音效("敵方攻擊");
        開始戰鬥動畫階段(2, 敵人, 行動按鈕, 逃跑按鈕);
    }

    private String 攻擊方式圖示(String 攻擊方式) {
        if ("遠程".equals(攻擊方式) || "奧術箭".equals(攻擊方式)) {
            return "➶";
        }
        return "⚔";
    }

    private String 使用攻擊描述(String 攻擊方式) {
        switch (攻擊方式) {
            case "遠程": return "拉弓射出箭矢";
            case "烈焰斬": return "施展烈焰斬";
            case "奧術箭": return "吟唱並釋放奧術箭";
            default: return "衝上前揮出一劍";
        }
    }

    private void 設定戰鬥行動可用(List<JButton> 行動按鈕, boolean 可用) {
        for (JButton 按鈕 : 行動按鈕) {
            按鈕.setEnabled(可用);
        }
    }

    private void 結算戰鬥勝利(敵人 敵人) {
        停止戰鬥動畫();
        玩家.增加金幣(敵人.掉落());
        最後獲得經驗 = 敵人.是否魔王() ? 260 : 55;
        最後升級數 = 玩家.獲得經驗(最後獲得經驗);
        if (敵人.是否魔王()) {
            魔王已擊敗 = true;
        }
        更新主畫面資訊();
        戰鬥日誌.append("\n✦　擊敗 " + 敵人.獲取名稱() + "！獲得 " + 敵人.掉落()
                + " 金幣與 " + 最後獲得經驗 + " 經驗。");
        if (最後升級數 > 0) {
            戰鬥日誌.append("\n★　升級了！現在是 Lv." + 玩家.獲取等級()
                    + "，生命與攻擊力已提升。");
            if (玩家.獲取等級() >= 3 && 玩家.獲取等級() - 最後升級數 < 3) {
                戰鬥日誌.append("\n✹　解鎖技能：烈焰斬！");
            }
            if (玩家.獲取等級() >= 5 && 玩家.獲取等級() - 最後升級數 < 5) {
                戰鬥日誌.append("\n✧　解鎖技能：奧術箭！");
            }
        }
        顯示戰鬥結局(true);
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
                ? "你戰勝了 " + 當前敵人.獲取名稱() + "，獲得 " + 最後獲得經驗 + " 經驗與 "
                        + 當前敵人.掉落() + " 金幣。"
                        + (最後升級數 > 0 ? "　升至 Lv." + 玩家.獲取等級() + "！" : "")
                        + (當前敵人.是否魔王() ? "　魔王已被討伐！" : "")
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
                    case "魔王・夜燼": 魔物色 = new Color(189, 68, 105); break;
                    default: 魔物色 = new Color(162, 124, 208);
                }
                g2.setPaint(new GradientPaint(0, 0, new Color(37, 45, 70), 0, h, new Color(20, 26, 42)));
                g2.fillRoundRect(0, 0, w, h, 24, 24);

                int cy = h / 2 + 5;
                int 玩家X = w / 2 - Math.min(205, w / 4);
                int 敵人X = w / 2 + Math.min(205, w / 4);
                double 英雄突進 = 戰鬥動畫階段 == 1
                        && ("近戰".equals(當前攻擊方式) || "烈焰斬".equals(當前攻擊方式))
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
                for (int i = 0; i < 盟友列表.size(); i++) {
                    int 同伴X = 玩家X - 62 - (i % 2) * 38;
                    int 同伴Y = cy - 48 + (i / 2) * 44;
                    double 同伴突進 = 戰鬥動畫階段 == 4 && i == 盟友攻擊索引
                            ? Math.sin(Math.min(1, 戰鬥動畫進度) * Math.PI) * 125 : 0;
                    繪製盟友戰鬥角色(g2, (int) (同伴X + 同伴突進), 同伴Y,
                            時間 + i * 0.6, i);
                }

                Graphics2D 魔物畫筆 = (Graphics2D) g2.create();
                float 透明度 = (float) Math.min(1, Math.max(0, 入場比例));
                魔物畫筆.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 透明度));
                魔物畫筆.translate(敵人X - 魔物突進 + 搖晃, cy);
                魔物畫筆.scale(入場比例, 入場比例);
                魔物畫筆.setColor(MonsterAlpha(魔物色, 45));
                魔物畫筆.fill(new Ellipse2D.Double(-70, -88, 140, 150));
                魔物畫筆.setColor((戰鬥動畫階段 == 1 || 戰鬥動畫階段 == 4)
                        && 戰鬥動畫進度 > 0.42 && 戰鬥動畫進度 < 0.68
                        ? new Color(255, 235, 205) : 魔物色);
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

                if (戰鬥動畫階段 == 1
                        && ("遠程".equals(當前攻擊方式) || "奧術箭".equals(當前攻擊方式))) {
                    double p = 戰鬥動畫進度;
                    int projectileX = (int) (玩家X + 55 + (敵人X - 玩家X - 105) * p);
                    int projectileY = (int) (cy - 28 - Math.sin(Math.PI * p) * 105);
                    Color projectileColor = "奧術箭".equals(當前攻擊方式)
                            ? new Color(183, 142, 255) : new Color(154, 218, 255);
                    g2.setColor(new Color(projectileColor.getRed(), projectileColor.getGreen(),
                            projectileColor.getBlue(), 70));
                    g2.fill(new Ellipse2D.Double(projectileX - 17, projectileY - 17, 34, 34));
                    g2.setColor(projectileColor);
                    g2.fill(new Ellipse2D.Double(projectileX - 7, projectileY - 7, 14, 14));
                    g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(projectileX - 28, projectileY + 10, projectileX - 7, projectileY);
                }
                if (戰鬥動畫階段 == 1
                        && ("近戰".equals(當前攻擊方式) || "烈焰斬".equals(當前攻擊方式))
                        && 戰鬥動畫進度 > 0.32 && 戰鬥動畫進度 < 0.86) {
                    float alpha = (float) Math.sin((戰鬥動畫進度 - 0.32) / 0.54 * Math.PI);
                    Color slashColor = "烈焰斬".equals(當前攻擊方式)
                            ? new Color(255, 126, 67, 220) : new Color(255, 234, 162, 220);
                    g2.setColor(new Color(slashColor.getRed(), slashColor.getGreen(),
                            slashColor.getBlue(), (int) (220 * alpha)));
                    g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int 起點X = 玩家X + 45;
                    int 目標X = 敵人X - 48;
                    int 刀光Y = cy - 70 + (int) (戰鬥動畫進度 * 85);
                    g2.drawLine(起點X, 刀光Y + 45, 目標X, 刀光Y - 38);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawLine(起點X + 8, 刀光Y + 52, 目標X + 4, 刀光Y - 31);
                }
                if (戰鬥動畫階段 == 4 && 戰鬥動畫進度 > 0.12 && 戰鬥動畫進度 < 0.78) {
                    float alpha = (float) Math.sin(戰鬥動畫進度 * Math.PI);
                    g2.setColor(new Color(255, 230, 150, (int) (200 * alpha)));
                    g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int startX = 玩家X - 62 - (盟友攻擊索引 % 2) * 38 + 16;
                    int targetX = 敵人X - 45;
                    int strikeY = cy - 34 + (int) (戰鬥動畫進度 * 42);
                    g2.drawLine(startX, strikeY + 24, targetX, strikeY - 20);
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
        if ("遠程".equals(當前攻擊方式) || "奧術箭".equals(當前攻擊方式)) {
            g2.setColor(new Color(170, 133, 95));
            g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawArc(x + 23, y - 42 + 呼吸, 31, 56, -90, 180);
            g2.setColor(new Color(225, 214, 190));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(x + 39, y - 40 + 呼吸, x + 39, y + 12 + 呼吸);
            g2.setColor(new Color(230, 231, 245));
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x + 28, y - 16 + 呼吸, x + 68, y - 16 + 呼吸);
            if ("奧術箭".equals(當前攻擊方式)) {
                g2.setColor(new Color(180, 138, 255, 190));
                g2.fill(new Ellipse2D.Double(x + 48, y - 26 + 呼吸, 19, 19));
            }
        } else {
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
    }

    private void 繪製盟友戰鬥角色(Graphics2D g2, int x, int y, double 時間, int index) {
        int bob = (int) (Math.sin(時間 * 3.5) * 3);
        Color[] colors = {
                new Color(112, 183, 147), new Color(183, 139, 211),
                new Color(209, 157, 111), new Color(107, 159, 207), new Color(199, 111, 130)
        };
        Color outfit = colors[index % colors.length];
        g2.setColor(new Color(13, 18, 30));
        g2.fillOval(x - 20, y + 43, 42, 9);
        g2.setColor(outfit);
        g2.fillRoundRect(x - 17, y - 9 + bob, 34, 57, 14, 16);
        g2.setColor(new Color(226, 190, 151));
        g2.fillOval(x - 16, y - 43 + bob, 32, 34);
        g2.setColor(new Color(58, 46, 48));
        g2.fillArc(x - 17, y - 47 + bob, 34, 22, 0, 180);
        g2.setColor(new Color(38, 42, 56));
        g2.fillOval(x - 9, y - 29 + bob, 3, 4);
        g2.fillOval(x + 5, y - 29 + bob, 3, 4);
        if (戰鬥動畫階段 == 4 && index == 盟友攻擊索引) {
            g2.setColor(new Color(237, 222, 188));
            g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x + 10, y + 1 + bob, x + 28, y - 16 + bob);
        }
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
    private int 最大生命;
    private int 攻擊力;
    private int 防禦力;
    private int 金幣;
    private int 等級;
    private int 經驗;
    private String 武器名稱;
    private int 武器攻擊加成;
    private boolean 武器適合遠程;

    public 玩家(String 名稱) {
        this.名稱 = 名稱;
        this.最大生命 = 100;
        this.生命 = 最大生命;
        this.攻擊力 = 10;
        this.防禦力 = 5;
        this.金幣 = 50;
        this.等級 = 1;
        this.經驗 = 0;
        this.武器名稱 = "訓練短劍";
    }

    public int 攻擊() {
        return 攻擊(false, 1);
    }

    public int 攻擊(boolean 遠程, double 倍率) {
        int 武器加成 = 遠程
                ? (武器適合遠程 ? 武器攻擊加成 : 0)
                : (武器適合遠程 ? Math.max(0, 武器攻擊加成 / 2) : 武器攻擊加成);
        int 基礎傷害 = 攻擊力 + 武器加成 + new Random().nextInt(5);
        return Math.max(1, (int) Math.round(基礎傷害 * 倍率));
    }

    public void 受到傷害(int 傷害) {
        int 實際傷害 = Math.max(0, 傷害 - 防禦力);
        this.生命 = Math.max(0, 生命 - 實際傷害);
    }

    public void 增加生命(int 數量) {
        this.生命 = Math.min(最大生命, 生命 + 數量);
    }

    public void 增加金幣(int 數量) {
        this.金幣 += 數量;
    }

    public void 升級攻擊力(int 數量) {
        this.攻擊力 += 數量;
    }

    public void 裝備武器(String 名稱, int 攻擊加成, boolean 遠程) {
        武器名稱 = 名稱;
        武器攻擊加成 = 攻擊加成;
        武器適合遠程 = 遠程;
    }

    public int 獲得經驗(int 數量) {
        經驗 += 數量;
        int 升級數 = 0;
        while (經驗 >= 升級所需經驗()) {
            經驗 -= 升級所需經驗();
            等級++;
            升級數++;
            最大生命 += 12;
            生命 = Math.min(最大生命, 生命 + 25);
            攻擊力 += 2;
            if (等級 % 2 == 0) {
                防禦力++;
            }
        }
        return 升級數;
    }

    public int 升級所需經驗() {
        return 80 + (等級 - 1) * 55;
    }

    public int 獲取經驗() {
        return 經驗;
    }

    public int 獲取等級() {
        return 等級;
    }

    public int 魔王出現等級() {
        return 5;
    }

    public String 獲取武器名稱() {
        return 武器名稱;
    }

    public String 顯示技能列表() {
        List<String> 技能 = new ArrayList<>();
        if (等級 >= 3) {
            技能.add("烈焰斬 (Lv.3)");
        }
        if (等級 >= 5) {
            技能.add("奧術箭 (Lv.5)");
        }
        return 技能.isEmpty() ? "尚未解鎖" : String.join("、", 技能);
    }

    public void 增加防禦力(int 數量) {
        this.防禦力 += 數量;
    }

    public int 獲取生命() {
        return 生命;
    }

    public int 獲取最大生命() {
        return 最大生命;
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
        return "名稱: " + 名稱 + "\n等級: " + 等級 + "\n生命: " + 生命 + "/" + 最大生命
                + "\n攻擊力: " + 攻擊力 + "\n防禦力: " + 防禦力 + "\n武器: " + 武器名稱 + "\n金幣: " + 金幣;
    }
}

// 敵人類別
class 敵人 {
    private String 類型;
    private int 生命;
    private int 攻擊力;
    private int 價值;
    private boolean 魔王;

    public 敵人(String 類型, int 生命, int 攻擊力, int 價值) {
        this(類型, 生命, 攻擊力, 價值, false);
    }

    public 敵人(String 類型, int 生命, int 攻擊力, int 價值, boolean 魔王) {
        this.類型 = 類型;
        this.生命 = 生命;
        this.攻擊力 = 攻擊力;
        this.價值 = 價值;
        this.魔王 = 魔王;
    }

    public int 攻擊() {
        return 攻擊力 + new Random().nextInt(5);
    }

    public void 受到傷害(int 傷害) {
        this.生命 = Math.max(0, 生命 - 傷害);
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

    public boolean 是否魔王() {
        return 魔王;
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

    public int 攻擊() {
        return 攻擊力 + new Random().nextInt(4);
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
                    case "弓箭": 音符 = new int[]{720, 980, 640}; 音符長度 = 65; 音量 = 0.18; break;
                    case "魔法": 音符 = new int[]{440, 660, 880}; 音符長度 = 120; 音量 = 0.2; break;
                    case "盟友攻擊": 音符 = new int[]{590, 790}; 音符長度 = 70; 音量 = 0.16; break;
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