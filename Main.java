import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
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

    public 遊戲框架() {
        this.主框架 = new JFrame("冒險遊戲");
        this.狀態標籤 = new JLabel("歡迎來到冒險遊戲！", JLabel.CENTER);
        this.盟友列表 = new ArrayList<>();
        this.隨機數 = new Random();
        初始化();
    }

    public void 啟動() {
        String 玩家名稱 = JOptionPane.showInputDialog(主框架, "請輸入你的角色名稱：", "角色名稱", JOptionPane.PLAIN_MESSAGE);
        if (玩家名稱 == null || 玩家名稱.trim().isEmpty()) {
            玩家名稱 = "勇者";
        }
        this.玩家 = new 玩家(玩家名稱);
        更新主畫面資訊();
        主框架.setVisible(true);
    }

    private void 初始化() {
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
        int 事件 = 隨機數.nextInt(4); // 隨機選擇一個事件
        switch (事件) {
            case 0:
                int 金幣 = 隨機數.nextInt(50) + 1;
                玩家.增加金幣(金幣);
                更新狀態("你找到了一個寶箱，獲得了 " + 金幣 + " 金幣！");
                break;
            case 1:
                if (玩家.獲取生命() > 0) { // 確保玩家在探索時有足夠的生命
                    更新狀態("你遇到了一個敵人！");
                    當前敵人 = 隨機生成敵人();
                    戰鬥(當前敵人); // 進入戰鬥畫面
                } else {
                    更新狀態("你的生命值不足，無法繼續探索！");
                }
                break;
            case 2:
                更新狀態("什麼也沒發生。");
                break;
            case 3:
                if (盟友列表.size() < 5) { // 限制最多只能有5個盟友
                    盟友 新盟友 = 隨機生成盟友();
                    更新狀態("你遇到了一位盟友" + 新盟友.獲取名稱() + "，加入你的隊伍！");
                    盟友列表.add(新盟友);
                } else {
                    更新狀態("你的盟友隊伍已滿，無法再加入新的盟友！");
                }
                break;
        }
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
        按鈕面板.add(攻擊按鈕);
        按鈕面板.add(逃跑按鈕);

        攻擊按鈕.addActionListener(e -> {
            int 傷害 = 玩家.攻擊();
            敵人.受到傷害(傷害);
            戰鬥日誌.append("\n⚔　你發動攻擊，對 " + 敵人.獲取名稱() + " 造成 " + 傷害 + " 點傷害。");

            if (敵人.獲取生命() > 0) {
                int 敵人傷害 = 敵人.攻擊();
                玩家.受到傷害(敵人傷害);
                戰鬥日誌.append("\n☠　" + 敵人.獲取名稱() + " 反擊，造成 " + 敵人傷害 + " 點傷害。");
            } else {
                戰鬥日誌.append("\n✦　勝利！你擊敗了 " + 敵人.獲取名稱() + "，獲得 " + 敵人.掉落() + " 金幣。");
                玩家.增加金幣(敵人.掉落());
                攻擊按鈕.setEnabled(false);
                逃跑按鈕.setText("返回公會");
            }

            玩家生命條.setValue(Math.max(0, Math.min(玩家最大生命, 玩家.獲取生命())));
            敵人生命條.setValue(Math.max(0, 敵人.獲取生命()));
            玩家生命條.setString("生命　" + Math.max(0, 玩家.獲取生命()) + " / " + 玩家最大生命);
            敵人生命條.setString("生命　" + Math.max(0, 敵人.獲取生命()) + " / " + 敵人生命條.getMaximum());
            更新主畫面資訊();
            戰鬥場景.repaint();
            戰鬥日誌.setCaretPosition(戰鬥日誌.getDocument().getLength());

            if (玩家.獲取生命() <= 0) {
                戰鬥日誌.append("\n\n你已被擊敗……冒險就此結束。");
                攻擊按鈕.setEnabled(false);
                逃跑按鈕.setEnabled(false);
                SwingUtilities.invokeLater(this::重新開始);
            }
        });

        逃跑按鈕.addActionListener(e -> {
            初始化();
            if (敵人.獲取生命() <= 0){
                更新狀態("你完成了戰鬥！");
            }else{
                更新狀態("你逃離了戰鬥！");
            }
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
        主框架.getContentPane().removeAll();
        主框架.add(戰鬥畫面, BorderLayout.CENTER);
        主框架.revalidate();
        主框架.repaint();
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
                Color 魔物色;
                switch (敵人.獲取名稱()) {
                    case "哥布林": 魔物色 = new Color(117, 184, 116); break;
                    case "骷髏兵": 魔物色 = new Color(210, 206, 181); break;
                    case "巨狼": 魔物色 = new Color(148, 160, 181); break;
                    default: 魔物色 = new Color(162, 124, 208);
                }
                g2.setPaint(new GradientPaint(0, 0, new Color(37, 45, 70), 0, h, new Color(20, 26, 42)));
                g2.fillRoundRect(0, 0, w, h, 24, 24);
                int cx = w / 2;
                int cy = h / 2 + 8;
                g2.setColor(new Color(245, 190, 91, 24));
                g2.fill(new Ellipse2D.Double(cx - 145, cy - 145, 290, 290));
                g2.setColor(new Color(245, 190, 91, 85));
                g2.setStroke(new BasicStroke(1.2f));
                for (int i = 0; i < 3; i++) {
                    int r = 104 + i * 18;
                    g2.draw(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
                }
                g2.setColor(new Color(245, 190, 91, 120));
                for (int i = 0; i < 12; i++) {
                    double angle = Math.PI * 2 * i / 12;
                    int sx = (int) (cx + Math.cos(angle) * 142);
                    int sy = (int) (cy + Math.sin(angle) * 142);
                    g2.fill(new Ellipse2D.Double(sx - 2, sy - 2, 4, 4));
                }

                g2.setColor(new Color(0, 0, 0, 70));
                g2.fillOval(cx - 78, cy + 74, 156, 23);
                g2.setColor(MonsterAlpha(魔物色, 45));
                g2.fill(new Ellipse2D.Double(cx - 70, cy - 88, 140, 150));
                g2.setColor(MonsterAlpha(魔物色, 255));
                g2.fill(new RoundRectangle2D.Double(cx - 48, cy - 30, 96, 104, 35, 38));
                g2.fillOval(cx - 54, cy - 92, 108, 91);
                if (!"骷髏兵".equals(敵人.獲取名稱())) {
                    g2.fillPolygon(new int[]{cx - 44, cx - 74, cx - 39}, new int[]{cy - 61, cy - 111, cy - 83}, 3);
                    g2.fillPolygon(new int[]{cx + 44, cx + 74, cx + 39}, new int[]{cy - 61, cy - 111, cy - 83}, 3);
                }
                g2.setColor(new Color(31, 35, 48));
                g2.fillOval(cx - 29, cy - 52, 20, 24);
                g2.fillOval(cx + 9, cy - 52, 20, 24);
                g2.setColor(new Color(255, 104, 99));
                g2.fillOval(cx - 23, cy - 45, 7, 8);
                g2.fillOval(cx + 15, cy - 45, 7, 8);
                g2.setColor(new Color(255, 255, 255, 110));
                g2.setStroke(new BasicStroke(2f));
                g2.drawArc(cx - 17, cy - 23, 34, 20, 200, 140);

                g2.setColor(文字色);
                g2.setFont(new Font("微軟正黑體", Font.BOLD, 17));
                FontMetrics fm = g2.getFontMetrics();
                String 名稱 = 敵人.獲取名稱();
                g2.drawString(名稱, cx - fm.stringWidth(名稱) / 2, h - 24);
                g2.dispose();
            }

            private Color MonsterAlpha(Color color, int alpha) {
                return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            }
        };
        場景.setOpaque(false);
        場景.setPreferredSize(new Dimension(0, 260));
        return 場景;
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
    private void 重新開始() {
        int 確認 = JOptionPane.showConfirmDialog(主框架, "重新開始？", "離開確認", JOptionPane.YES_NO_OPTION);
        if (確認 == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
        else {
            SwingUtilities.invokeLater(() -> new 遊戲框架().啟動());
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
        
        return -1;  // 這行理論上不會執行，如果權重為正數
    }
}