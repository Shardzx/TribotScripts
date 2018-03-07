package scripts.ezhellratcatcher;

import org.tribot.api.General;
import org.tribot.api2007.ext.Filters;
import scripts.Utilities.EzPaint;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame {

    /**
     * All images/game sprites are property of Jagex Ltd.
     */
    public boolean isCompleted = false;

    private static final long serialVersionUID = -5624161068204250091L;
    private final JCheckBox brownAll;
    private final JCheckBox brown4;
    private final JCheckBox brown3;
    private final JCheckBox brown2;
    private final JCheckBox brown1;
    private final JCheckBox redAll;
    private final JCheckBox red4;
    private final JCheckBox red3;
    private final JCheckBox red2;
    private final JCheckBox red1;
    private final JCheckBox yellowAll;
    private final JCheckBox yellow4;
    private final JCheckBox yellow3;
    private final JCheckBox yellow2;
    private final JCheckBox yellow1;
    private final JCheckBox orangeAll;
    private final JCheckBox orange4;
    private final JCheckBox orange3;
    private final JCheckBox orange2;
    private final JCheckBox orange1;
    private final JSlider slider;
    private final JCheckBox manageKitten;
    private final JRadioButton battlerBrown;
    private final JRadioButton battlerOrange;
    private final JRadioButton battlerYellow;
    private final JRadioButton battlerRed;
    private final JTextField foodNames;
    private final JTextField numFoodToWithdraw;
    private final JCheckBox chckbxUseAbcEat;
    private final JSlider eatAtSlider;
    private final JCheckBox kittenIdlerBox;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GUI frame = new GUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Create the frame.
     */
    public GUI() {
        setTitle("EzHellratCatcher");
        this.setVisible(true);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(0, 0, 434, 261);
        contentPane.add(tabbedPane);

        JPanel panel = new JPanel();
        tabbedPane.addTab("Catcher", null, panel, null);
        panel.setLayout(null);

        JLabel lblBrownSpice = new JLabel();
        lblBrownSpice.setBounds(29, 26, 19, 28);
        panel.add(lblBrownSpice);

        JLabel lblRedSpice = new JLabel();
        lblRedSpice.setBounds(29, 55, 19, 28);
        panel.add(lblRedSpice);

        JLabel lblYellowSpice = new JLabel();
        lblYellowSpice.setBounds(29, 84, 19, 28);
        panel.add(lblYellowSpice);

        JLabel lblOrangeSpice = new JLabel();
        lblOrangeSpice.setBounds(29, 114, 19, 28);
        panel.add(lblOrangeSpice);

        lblBrownSpice.setIcon(new ImageIcon(EzPaint.downloadImageFile("https://vignette.wikia.nocookie.net/2007scape/images/e/e9/Brown_spice_4.png")));
        lblRedSpice.setIcon(new ImageIcon(EzPaint.downloadImageFile("https://vignette.wikia.nocookie.net/2007scape/images/8/80/Red_spice_4.png")));
        lblOrangeSpice.setIcon(new ImageIcon(EzPaint.downloadImageFile("https://vignette.wikia.nocookie.net/2007scape/images/a/a5/Orange_spice_4.png")));
        lblYellowSpice.setIcon(new ImageIcon(EzPaint.downloadImageFile("https://vignette.wikia.nocookie.net/2007scape/images/6/6f/Yellow_spice_4.png")));

        JLabel lblAll = new JLabel("All");
        lblAll.setBounds(44, 11, 46, 14);
        panel.add(lblAll);
        lblAll.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel label_4 = new JLabel("4");
        label_4.setBounds(82, 11, 19, 14);
        panel.add(label_4);
        label_4.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel label_5 = new JLabel("3");
        label_5.setBounds(100, 11, 35, 14);
        panel.add(label_5);
        label_5.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel label_6 = new JLabel("2");
        label_6.setBounds(139, 11, 6, 14);
        panel.add(label_6);
        label_6.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel label_7 = new JLabel("1");
        label_7.setBounds(150, 11, 35, 14);
        panel.add(label_7);
        label_7.setHorizontalAlignment(SwingConstants.CENTER);

        brownAll = new JCheckBox("");
        brownAll.setBounds(57, 31, 21, 21);
        panel.add(brownAll);

        brown4 = new JCheckBox("");
        brown4.setBounds(82, 31, 21, 21);
        panel.add(brown4);

        brown3 = new JCheckBox("");
        brown3.setBounds(107, 31, 21, 21);
        panel.add(brown3);

        brown2 = new JCheckBox("");
        brown2.setBounds(132, 31, 21, 21);
        panel.add(brown2);

        brown1 = new JCheckBox("");
        brown1.setBounds(157, 31, 21, 21);
        panel.add(brown1);

        redAll = new JCheckBox("");
        redAll.setBounds(57, 59, 21, 21);
        panel.add(redAll);

        red4 = new JCheckBox("");
        red4.setBounds(82, 59, 21, 21);
        panel.add(red4);

        red3 = new JCheckBox("");
        red3.setBounds(107, 59, 21, 21);
        panel.add(red3);

        red2 = new JCheckBox("");
        red2.setBounds(132, 59, 21, 21);
        panel.add(red2);

        red1 = new JCheckBox("");
        red1.setBounds(157, 59, 21, 21);
        panel.add(red1);

        yellowAll = new JCheckBox("");
        yellowAll.setBounds(57, 87, 21, 21);
        panel.add(yellowAll);

        yellow4 = new JCheckBox("");
        yellow4.setBounds(82, 87, 21, 21);
        panel.add(yellow4);

        yellow3 = new JCheckBox("");
        yellow3.setBounds(107, 87, 21, 21);
        panel.add(yellow3);

        yellow2 = new JCheckBox("");
        yellow2.setBounds(132, 87, 21, 21);
        panel.add(yellow2);

        yellow1 = new JCheckBox("");
        yellow1.setBounds(157, 87, 21, 21);
        panel.add(yellow1);

        orangeAll = new JCheckBox("");
        orangeAll.setBounds(57, 114, 21, 21);
        panel.add(orangeAll);

        orange4 = new JCheckBox("");
        orange4.setBounds(82, 114, 21, 21);
        panel.add(orange4);

        orange3 = new JCheckBox("");
        orange3.setBounds(107, 114, 21, 21);
        panel.add(orange3);

        orange2 = new JCheckBox("");
        orange2.setBounds(132, 114, 21, 21);
        panel.add(orange2);

        orange1 = new JCheckBox("");
        orange1.setBounds(157, 114, 21, 21);
        panel.add(orange1);

        JLabel lblManageKitten = new JLabel("Manage kitten?");
        lblManageKitten.setBounds(247, 28, 100, 20);
        panel.add(lblManageKitten);

        manageKitten = new JCheckBox("");
        manageKitten.setBounds(333, 27, 21, 21);
        panel.add(manageKitten);

        JLabel lblMouseSpeed = new JLabel("Mouse speed:");
        lblMouseSpeed.setBounds(10, 176, 80, 14);
        panel.add(lblMouseSpeed);

        slider = new JSlider();
        slider.setBounds(82, 171, 200, 45);
        panel.add(slider);
        slider.setValue(100);
        slider.setMajorTickSpacing(25);
        slider.setPaintTicks(true);
        slider.setMaximum(200);
        slider.setMinimum(75);
        slider.setPaintLabels(true);

        JButton startCatcherButton = new JButton("START CATCHER");
        startCatcherButton.setBounds(298, 176, 125, 23);
        panel.add(startCatcherButton);

        JLabel catGif = new JLabel("");
        catGif.setBounds(263, 61, 131, 102);
        catGif.setIcon(new ImageIcon(EzPaint.downloadImageFile("https://vignette.wikia.nocookie.net/2007scape/images/2/29/Cat.gif")));
        panel.add(catGif);

        JLabel lblKittenIdler = new JLabel("Kitten idler?");
        lblKittenIdler.setBounds(248, 54, 70, 14);
        panel.add(lblKittenIdler);

        kittenIdlerBox = new JCheckBox("");
        kittenIdlerBox.setBounds(333, 51, 21, 21);
        panel.add(kittenIdlerBox);

        startCatcherButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                passCatcherSettings();
                close();
            }
        });
        orangeAll.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (arg0.getStateChange() == 2) {
                    orange4.setEnabled(true);
                    orange3.setEnabled(true);
                    orange2.setEnabled(true);
                    orange1.setEnabled(true);
                } else {
                    orange4.setEnabled(false);
                    orange3.setEnabled(false);
                    orange2.setEnabled(false);
                    orange1.setEnabled(false);
                }
            }
        });
        yellowAll.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (arg0.getStateChange() == 2) {
                    yellow4.setEnabled(true);
                    yellow3.setEnabled(true);
                    yellow2.setEnabled(true);
                    yellow1.setEnabled(true);
                } else {
                    yellow4.setEnabled(false);
                    yellow3.setEnabled(false);
                    yellow2.setEnabled(false);
                    yellow1.setEnabled(false);
                }
            }
        });
        redAll.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (arg0.getStateChange() == 2) {
                    red4.setEnabled(true);
                    red3.setEnabled(true);
                    red2.setEnabled(true);
                    red1.setEnabled(true);
                } else {
                    red4.setEnabled(false);
                    red3.setEnabled(false);
                    red2.setEnabled(false);
                    red1.setEnabled(false);
                }
            }
        });
        brownAll.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (arg0.getStateChange() == 2) {
                    brown4.setEnabled(true);
                    brown3.setEnabled(true);
                    brown2.setEnabled(true);
                    brown1.setEnabled(true);
                } else {
                    brown4.setEnabled(false);
                    brown3.setEnabled(false);
                    brown2.setEnabled(false);
                    brown1.setEnabled(false);
                }
//				}
            }

        });

        JPanel panel_1 = new JPanel();
        tabbedPane.addTab("Battler", null, panel_1, null);
        panel_1.setLayout(null);

        JLabel lblBrownSpice2 = new JLabel();
        lblBrownSpice2.setBounds(10, 23, 19, 28);
        panel_1.add(lblBrownSpice2);

        JLabel lblRedSpice2 = new JLabel();
        lblRedSpice2.setBounds(10, 52, 19, 28);
        panel_1.add(lblRedSpice2);

        JLabel lblOrangeSpice2 = new JLabel();
        lblOrangeSpice2.setBounds(10, 110, 19, 28);
        panel_1.add(lblOrangeSpice2);

        JLabel lblYellowSpice2 = new JLabel();
        lblYellowSpice2.setBounds(10, 81, 19, 28);
        panel_1.add(lblYellowSpice2);

        lblBrownSpice2.setIcon(new ImageIcon(EzPaint.downloadImageFile("https://vignette.wikia.nocookie.net/2007scape/images/e/e9/Brown_spice_4.png")));
        lblRedSpice2.setIcon(new ImageIcon(EzPaint.downloadImageFile("https://vignette.wikia.nocookie.net/2007scape/images/8/80/Red_spice_4.png")));
        lblOrangeSpice2.setIcon(new ImageIcon(EzPaint.downloadImageFile("https://vignette.wikia.nocookie.net/2007scape/images/a/a5/Orange_spice_4.png")));
        lblYellowSpice2.setIcon(new ImageIcon(EzPaint.downloadImageFile("https://vignette.wikia.nocookie.net/2007scape/images/6/6f/Yellow_spice_4.png")));

        battlerBrown = new JRadioButton("");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(battlerBrown);
        battlerBrown.setBounds(38, 28, 21, 21);
        panel_1.add(battlerBrown);

        battlerOrange = new JRadioButton("");
        buttonGroup.add(battlerOrange);
        battlerOrange.setBounds(38, 117, 21, 21);
        panel_1.add(battlerOrange);

        battlerYellow = new JRadioButton("");
        buttonGroup.add(battlerYellow);
        battlerYellow.setBounds(38, 88, 21, 21);
        panel_1.add(battlerYellow);

        battlerRed = new JRadioButton("");
        buttonGroup.add(battlerRed);
        battlerRed.setBounds(38, 59, 21, 21);
        panel_1.add(battlerRed);

        JLabel lblBattlerFight = new JLabel("Battler - fight Hell-Rat Behemoths after saving Evil Dave");
        lblBattlerFight.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblBattlerFight.setBounds(38, 0, 337, 17);
        panel_1.add(lblBattlerFight);

        chckbxUseAbcEat = new JCheckBox("use abc2 eat %");
        chckbxUseAbcEat.setSelected(true);
        chckbxUseAbcEat.setBounds(176, 44, 120, 23);
        panel_1.add(chckbxUseAbcEat);

        eatAtSlider = new JSlider();
        eatAtSlider.setPaintLabels(true);
        eatAtSlider.setPaintTicks(true);
        eatAtSlider.setMajorTickSpacing(1);
        eatAtSlider.setMaximum(4);
        eatAtSlider.setMinimum(1);
        eatAtSlider.setBounds(135, 100, 200, 45);
        panel_1.add(eatAtSlider);

        JLabel lblOr = new JLabel("OR feed cat at health:");
        lblOr.setBounds(179, 81, 120, 14);
        panel_1.add(lblOr);

        JLabel behemothLabel = new JLabel();
        behemothLabel.setBounds(118, 151, 178, 73);
        behemothLabel.setIcon(new ImageIcon(new ImageIcon(EzPaint.downloadImageFile("https://vignette.wikia.nocookie.net/2007scape/images/d/dc/Hell-Rat_Behemoth.png")).getImage().getScaledInstance(178, 73, Image.SCALE_DEFAULT)));
        panel_1.add(behemothLabel);

        JLabel hellcatLabel = new JLabel("");
        hellcatLabel.setBounds(20, 136, 74, 88);
        hellcatLabel.setIcon(new ImageIcon(EzPaint.downloadImageFile("https://vignette.wikia.nocookie.net/2007scape/images/2/2b/Hellcat.png")));
        panel_1.add(hellcatLabel);

        JButton startBattlerButton = new JButton("START BATTLER");
        startBattlerButton.setBounds(298, 176, 121, 23);
        panel_1.add(startBattlerButton);

        startBattlerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                passBattlerSettings();
                if (Vars.color != null)
                    close();
            }
        });

        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Food", null, panel_2, null);
        panel_2.setLayout(null);

        JLabel label = new JLabel("# of food to withdraw:");
        label.setBounds(85, 62, 120, 14);
        panel_2.add(label);

        foodNames = new JTextField();
        foodNames.setToolTipText("");
        foodNames.setColumns(10);
        foodNames.setBounds(206, 109, 146, 20);
        panel_2.add(foodNames);

        numFoodToWithdraw = new JTextField();
        numFoodToWithdraw.setColumns(10);
        numFoodToWithdraw.setBounds(206, 59, 146, 20);
        panel_2.add(numFoodToWithdraw);

        JLabel label_1 = new JLabel("<html>Food name(s),<br>spell correctly and<br>separate with comma</html>");
        label_1.setBounds(85, 97, 150, 50);
        panel_2.add(label_1);
    }

    private void passCatcherSettings() {
        List<String> spicesToLoot = new ArrayList<String>();
        String base = "Brown spice ";
        if (brownAll.isSelected()) {
            spicesToLoot.add(base + "(4)");
            spicesToLoot.add(base + "(3)");
            spicesToLoot.add(base + "(2)");
            spicesToLoot.add(base + "(1)");
        } else {
            if (brown4.isSelected()) {
                spicesToLoot.add(base + "(4)");
            }
            if (brown3.isSelected()) {
                spicesToLoot.add(base + "(3)");
            }
            if (brown2.isSelected()) {
                spicesToLoot.add(base + "(2)");
            }
            if (brown1.isSelected()) {
                spicesToLoot.add(base + "(1)");
            }
        }
        base = "Red spice ";
        if (redAll.isSelected()) {
            spicesToLoot.add(base + "(4)");
            spicesToLoot.add(base + "(3)");
            spicesToLoot.add(base + "(2)");
            spicesToLoot.add(base + "(1)");
        } else {
            if (red4.isSelected()) {
                spicesToLoot.add(base + "(4)");
            }
            if (red3.isSelected()) {
                spicesToLoot.add(base + "(3)");
            }
            if (red2.isSelected()) {
                spicesToLoot.add(base + "(2)");
            }
            if (red1.isSelected()) {
                spicesToLoot.add(base + "(1)");
            }
        }
        base = "Orange spice ";
        if (orangeAll.isSelected()) {
            spicesToLoot.add(base + "(4)");
            spicesToLoot.add(base + "(3)");
            spicesToLoot.add(base + "(2)");
            spicesToLoot.add(base + "(1)");
        } else {
            if (orange4.isSelected()) {
                spicesToLoot.add(base + "(4)");
            }
            if (orange3.isSelected()) {
                spicesToLoot.add(base + "(3)");
            }
            if (orange2.isSelected()) {
                spicesToLoot.add(base + "(2)");
            }
            if (orange1.isSelected()) {
                spicesToLoot.add(base + "(1)");
            }
        }
        base = "Yellow spice ";
        if (yellowAll.isSelected()) {
            spicesToLoot.add(base + "(4)");
            spicesToLoot.add(base + "(3)");
            spicesToLoot.add(base + "(2)");
            spicesToLoot.add(base + "(1)");
        } else {
            if (yellow4.isSelected()) {
                spicesToLoot.add(base + "(4)");
            }
            if (yellow3.isSelected()) {
                spicesToLoot.add(base + "(3)");
            }
            if (yellow2.isSelected()) {
                spicesToLoot.add(base + "(2)");
            }
            if (yellow1.isSelected()) {
                spicesToLoot.add(base + "(1)");
            }
        }
        if (spicesToLoot.size() > 0) {
            Vars.spiceFilter = Filters.GroundItems.nameEquals(spicesToLoot.toArray(new String[spicesToLoot.size()]));
        }
        if (manageKitten.isSelected()) {
            Vars.shouldManageKitten = true;
        }
        if (kittenIdlerBox.isSelected()) {
            Vars.idleKittenOnly = true;
        }
        if (slider.getValue() != 100) {
            Vars.mouseSpeed = slider.getValue();
        }
        passFoodSettings();
    }

    private void passBattlerSettings() {
        Vars.battleMode = true;
        if (!chckbxUseAbcEat.isSelected()) {
            Vars.abc2eat = false;
            Vars.nextEat = eatAtSlider.getValue();
        }
        passFoodSettings();
        if (battlerBrown.isSelected()) {
            Vars.color = Const.COLOR.BROWN;
            return;
        }
        if (battlerOrange.isSelected()) {
            Vars.color = Const.COLOR.ORANGE;
            return;
        }
        if (battlerYellow.isSelected()) {
            Vars.color = Const.COLOR.YELLOW;
            return;
        }
        if (battlerRed.isSelected()) {
            Vars.color = Const.COLOR.RED;
        }
    }

    private void passFoodSettings() {
        String names = foodNames.getText();
        if (names != null && names.length() > 0) {
            try {
                Vars.catFoodFilter = Filters.Items.nameEquals(names.split(","));
            } catch (Exception e) {
                General.println("Error parsing custom food name input. Please separate with commas.");
            }
        }
        String num = numFoodToWithdraw.getText();
        if (num != null && num.length() > 0) {
            try {
                Vars.withdrawAmount = Integer.parseInt(num);
            } catch (Exception e) {
                General.println("Error parsing custom food amount input. Please only enter an integer.");
            }
        }
    }

    private void close() {
        isCompleted = true;
        this.dispose();
    }
}
