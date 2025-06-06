import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Shadowrun 6e Character Builder with GUI
 * 
 * This program launches a Swing-based interface that mimics the layout and appearance of the
 * two-page Shadowrun 6th Edition character sheet PDF. Users can fill out fields directly and
 * generate a formatted character report.
 */
public class ShadowrunCharacterBuilderGUI {
    private JFrame frame;
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private JTabbedPane tabs;
    
    // PERSONAL DATA fields
    private JTextField tfName, tfPlayer, tfAge, tfNationality,
                       tfHeight, tfHeightFt, tfWeight, tfWeightLbs,
                       tfKarma, tfTotalKarma;
    private JComboBox<String> cbRole;
    private JComboBox<String> cbStatus;
    private JComboBox<MetaItem> cbMetatype;
    private JCheckBox chkSurge;
    private JLabel lblSurgeCollective;
    private JComboBox<String> cbSurgeCollective;
    private JComboBox<String> cbGender;
    private JTextField tfNuyen, tfPrimaryLifestyle, tfFakeIDs;
    private JTextArea taNotes;
    
    // ATTRIBUTES fields
    private JSpinner spBody, spAgility, spReaction, spStrength,
                     spWillpower, spLogic, spIntuition, spCharisma,
                     spEdge, spEssence, spMagic, spResonance;
    private JTextField tfComposure, tfJudgeIntentions, tfMemory, tfLiftCarry;
    // TODO later: initiative and other derived stats
    // private JTextField tfInitiative, tfMatrixInitiative, tfAstralInitiative,
    //                    tfJudgeIntentions, tfMemory, tfLiftCarry, tfMovement,
    //                    tfUnarmedAR, tfDefenseRating;
    
    // CONDITION MONITOR fields (future feature)
    // private JTextField tfPhysicalBoxes, tfStunBoxes;
    
    // SKILLS table and QUALITIES/CONTACTS tables
    private JTable tableSkills;
    private DefaultTableModel skillsTableModel;
    private JTable tableQualities;
    private DefaultTableModel qualitiesTableModel;
    private JTable tableContacts;
    private DefaultTableModel contactsTableModel;
    private JLabel lblSkillCount;
    private JLabel lblQualityCount;
    private java.util.Map<String, double[]> metatypeMap = new java.util.LinkedHashMap<>();
    private java.util.Map<String, Integer> metatypeKarmaMap = new java.util.LinkedHashMap<>();
    private java.util.Map<String, Integer> surgeKarmaMap = new java.util.LinkedHashMap<>();
    private java.util.Map<String, String[]> archetypeMap = new java.util.LinkedHashMap<>();
    private java.util.Map<String, String[]> skillMap = new java.util.LinkedHashMap<>();
    private java.util.Map<String, String[]> specializationMap = new java.util.LinkedHashMap<>();
    private java.util.List<QualityEntry> qualityEntries = new java.util.ArrayList<>();
    private int editingQualityRow = -1;

    private String lastSurgeCollective = null;
    private String lastMetatype = null;

    private JTable tableKarmaLog;
    private DefaultTableModel karmaLogModel;
    private JLabel lblLoggedKarma;
    private JPanel karmaLogPanel;
    private JButton btnToggleKarmaLog;

    // Life Path Wizard components
    private JPanel wizardPanel;
    private JComboBox<String> wizardMetaCombo;
    private JCheckBox wizardSurgeCheck;
    private JComboBox<String> wizardSurgeCombo;
    private JComboBox<String> wizardStatusCombo;
    private JTextField wizardNationalityField;
    private JTextField wizardLanguageField;

    private static final String[] QUALITY_CATEGORIES = {
            "Magic","Matrix","Mental","Metagenic","Physical","Social","Vehicle"
    };

    private static final String[] RANK_OPTIONS = {
            "1 - Novice",
            "2 - Advanced Beginner",
            "3 - Journeyman",
            "4 - Professional",
            "5 - Advanced Professional",
            "6 - Local Legend",
            "7 - Elite",
            "8 - Professional Elite",
            "9 - National Elite",
            "10 - Multinational Elite",
            "11 - Global Elite",
            "12 - GOAT"
    };
    
    // WEAPONS, ARMOR: future feature
    // private JTextArea taRangedWeapons, taMeleeWeapons, taArmor;
    
    // MATRIX STATS - future feature
    // private JTextField tfMatrixAttack, tfMatrixSleaze, tfMatrixDataProc, tfMatrixFirewall,
    //                    tfMatrixConditionBoxes;
    // private JTextArea taMatrixDevices;
    
    // AUGMENTATIONS - future feature
    // private JTextArea taAugmentations;
    
    // VEHICLE - future feature
    // private JTextField tfVehicleName, tfVehicleHandling, tfVehicleAcceleration,
    //                    tfVehicleSpeedInterval, tfVehicleTopSpeed, tfVehicleBody,
    //                    tfVehicleArmor, tfVehiclePilot, tfVehicleSensor, tfVehicleSeats;
    // private JTextArea taVehicleNotes;
    
    // GEAR - to be developed later
    // private JTextArea taGear;
    
    // SPELLS/ RITUALS/ COMPLEX FORMS - future feature
    // private JTextArea taSpellsRituals;
    
    // ADEPT POWERS - future feature
    // private JTextArea taAdeptPowers;

    private static class MetaItem {
        String name;
        boolean variant;
        MetaItem(String n, boolean v) { this.name = n; this.variant = v; }
        public String toString() { return name; }
    }

    private static class QualityEntry {
        String name;
        String type;
        String category;
        int karma;
        String instance;
        int min;
        int max;
        QualityEntry(String n, String t, String c, int k, String i, int mn, int mx) {
            name = n; type = t; category = c; karma = k; instance = i; min = mn; max = mx;
        }
        public String toString() { return name; }
    }
    
    public ShadowrunCharacterBuilderGUI() {
        frame = new JFrame("Shadowrun 6e Character Builder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Build each section
        buildPersonalDataSection();
        buildAttributesSection();
        // buildConditionMonitorSection(); // TODO expand later

        tabs = new JTabbedPane();
        tabs.setPreferredSize(new Dimension(850, 250));
        tabs.addTab("Skills", buildSkillsSection());
        tabs.addTab("Qualities", buildQualitiesSection());
        tabs.addTab("Contacts", buildContactsSection());
        tabs.addTab("Lifestyle", buildLifestyleSection());
        tabs.addTab("Notes", buildNotesSection());
        contentPanel.add(tabs);

        // Life Path Wizard and Generate buttons
        JButton btnWizard = new JButton("Run Life Path Wizard");
        btnWizard.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnWizard.addActionListener(e -> runLifePathWizard());

        JButton btnGenerate = new JButton("Generate Report");
        btnGenerate.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGenerate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
        btnToggleKarmaLog = new JButton("Karma Log >>>");
        btnToggleKarmaLog.addActionListener(e -> {
            boolean vis = karmaLogPanel.isVisible();
            karmaLogPanel.setVisible(!vis);
            btnToggleKarmaLog.setText(vis ? "Karma Log >>>" : "<<< Karma Log");
            frame.revalidate();
        });

        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomButtons.add(btnWizard);
        bottomButtons.add(btnGenerate);
        bottomButtons.add(btnToggleKarmaLog);

        scrollPane = new JScrollPane(contentPanel);
        karmaLogPanel = buildKarmaLogPanel();
        karmaLogPanel.setVisible(false);
        wizardPanel = buildWizardPanel();
        wizardPanel.setVisible(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(wizardPanel, BorderLayout.EAST);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(mainPanel, BorderLayout.CENTER);
        rootPanel.add(karmaLogPanel, BorderLayout.EAST);
        rootPanel.add(bottomButtons, BorderLayout.SOUTH);
        frame.getContentPane().add(rootPanel);
        frame.setVisible(true);
    }

    private void buildPersonalDataSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Personal Data", TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;

        int row = 0;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Character Name:"), c);
        tfName = new JTextField(15); c.gridx = 1; panel.add(tfName, c);
        Dimension leftDim = new Dimension(150, tfName.getPreferredSize().height);
        tfName.setPreferredSize(leftDim);
        c.gridx = 2; panel.add(new JLabel("Gender:"), c);
        cbGender = new JComboBox<>(new String[]{"Male", "Female"}); c.gridx = 3; panel.add(cbGender, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Player Name:"), c);
        tfPlayer = new JTextField(15); tfPlayer.setPreferredSize(leftDim); c.gridx = 1; panel.add(tfPlayer, c);
        c.gridx = 2; panel.add(new JLabel("Age:"), c);
        tfAge = new JTextField(5); c.gridx = 3; panel.add(tfAge, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Status:"), c);
        cbStatus = new JComboBox<>(new String[]{"Mundane","Full Magician","Aspected Magician","Mystic Adept","Adept","Technomancer"});
        c.gridx = 1; panel.add(cbStatus, c);
        c.gridx = 2; panel.add(new JLabel("Nationality:"), c);
        tfNationality = new JTextField(15); tfNationality.setPreferredSize(leftDim); c.gridx = 3; panel.add(tfNationality, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Archetype/Role:"), c);
        cbRole = new JComboBox<>(); cbRole.setPreferredSize(leftDim); c.gridx = 1; panel.add(cbRole, c);
        JButton btnRoleInfo = new JButton("\u2139");
        btnRoleInfo.setMargin(new Insets(0,0,0,0));
        btnRoleInfo.setVisible(false);
        c.gridx = 2; panel.add(btnRoleInfo, c);
        c.gridx = 3; panel.add(new JLabel("Height (cm):"), c);
        tfHeight = new JTextField(5); c.gridx = 4; panel.add(tfHeight, c);
        c.gridx = 5; panel.add(new JLabel("Height (ft):"), c);
        tfHeightFt = new JTextField(6); tfHeightFt.setEditable(false); c.gridx = 6; panel.add(tfHeightFt, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Metatype:"), c);
        cbMetatype = new JComboBox<>(); cbMetatype.setPreferredSize(leftDim);
        cbMetatype.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MetaItem) {
                    MetaItem mi = (MetaItem) value;
                    String text = mi.name;
                    if (index >= 0 && mi.variant) text = " - " + text;
                    lbl.setText(text);
                }
                return lbl;
            }
        });
        c.gridx = 1; panel.add(cbMetatype, c);
        c.gridx = 3; panel.add(new JLabel("Weight (kg):"), c);
        tfWeight = new JTextField(5); c.gridx = 4; panel.add(tfWeight, c);
        c.gridx = 5; panel.add(new JLabel("Weight (lbs):"), c);
        tfWeightLbs = new JTextField(6); tfWeightLbs.setEditable(false); c.gridx = 6; panel.add(tfWeightLbs, c);
        row++;

        c.gridx = 3; c.gridy = row; panel.add(new JLabel("Karma:"), c);
        tfKarma = new JTextField(5); tfKarma.setText("50"); c.gridx = 4; panel.add(tfKarma, c);
        c.gridx = 5; panel.add(new JLabel("Total Karma:"), c);
        tfTotalKarma = new JTextField(6); tfTotalKarma.setEditable(false); c.gridx = 6; panel.add(tfTotalKarma, c);

        chkSurge = new JCheckBox("SURGE");
        chkSurge.setPreferredSize(leftDim);
        c.gridx = 1; c.gridy = row; panel.add(chkSurge, c);
        row++;

        lblSurgeCollective = new JLabel("SURGE Collective:");
        cbSurgeCollective = new JComboBox<>();
        cbSurgeCollective.setPreferredSize(leftDim);
        c.gridx = 0; c.gridy = row; panel.add(lblSurgeCollective, c);
        c.gridx = 1; panel.add(cbSurgeCollective, c);
        lblSurgeCollective.setVisible(false);
        cbSurgeCollective.setVisible(false);
        row++;

        loadMetatypes();
        loadSurgeCollectives();
        loadArchetypes();
        cbRole.setSelectedIndex(-1);
        cbMetatype.setSelectedIndex(-1);
        cbGender.setSelectedIndex(-1);
        cbStatus.setSelectedIndex(-1);

        tfHeight.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateHeightFeet(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateHeightFeet(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateHeightFeet(); }
        });
        tfWeight.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateWeightLbs(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateWeightLbs(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateWeightLbs(); }
        });

        chkSurge.addActionListener(e -> {
            boolean sel = chkSurge.isSelected();
            lblSurgeCollective.setVisible(sel);
            cbSurgeCollective.setVisible(sel);
            cbSurgeCollective.setSelectedItem("No Collective");
            if (!sel) {
                removeMetagenicQualities();
                if (lastSurgeCollective != null) {
                    removeKarma("SURGE", lastSurgeCollective);
                    lastSurgeCollective = null;
                }
            }
        });

        cbSurgeCollective.addActionListener(e -> {
            if (chkSurge.isSelected()) {
                String owner = (String) cbSurgeCollective.getSelectedItem();
                loadRacialTraitsForCollective(owner);
                if (lastSurgeCollective != null) {
                    removeKarma("SURGE", lastSurgeCollective);
                }
                if (!"No Collective".equals(owner)) {
                    int cost = surgeKarmaMap.getOrDefault(owner, 0);
                    addOrUpdateKarma("SURGE", owner, cost);
                    lastSurgeCollective = owner;
                } else {
                    lastSurgeCollective = null;
                }
            }
        });

        cbRole.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sel = (String) cbRole.getSelectedItem();
                btnRoleInfo.setVisible(sel != null && !sel.isEmpty());
            }
        });
        btnRoleInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sel = (String) cbRole.getSelectedItem();
                if (sel != null && archetypeMap.containsKey(sel)) {
                    String[] vals = archetypeMap.get(sel);
                    JOptionPane.showMessageDialog(frame,
                            "Archetype: " + sel + "\nPrimary Focus: " + vals[2] + "\n" + vals[1],
                            "Archetype Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        contentPanel.add(panel);
    }

    private void buildAttributesSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Attributes", TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.NORTHWEST;

        JPanel physical = new JPanel(new GridBagLayout());
        physical.setBorder(BorderFactory.createTitledBorder("Physical"));
        GridBagConstraints pc = new GridBagConstraints();
        pc.insets = new Insets(2,2,2,2);
        pc.anchor = GridBagConstraints.WEST;
        int prow = 0;
        pc.gridx = 0; pc.gridy = prow; physical.add(new JLabel("Body:"), pc);
        spBody = new JSpinner(new SpinnerNumberModel(1, 1, null, 1));
        Dimension attrDim = new Dimension(60, spBody.getPreferredSize().height);
        spBody.setPreferredSize(attrDim); pc.gridx = 1; physical.add(spBody, pc); prow++;
        pc.gridx = 0; pc.gridy = prow; physical.add(new JLabel("Agility:"), pc);
        spAgility = new JSpinner(new SpinnerNumberModel(1, 1, null, 1)); spAgility.setPreferredSize(attrDim); pc.gridx = 1; physical.add(spAgility, pc); prow++;
        pc.gridx = 0; pc.gridy = prow; physical.add(new JLabel("Reaction:"), pc);
        spReaction = new JSpinner(new SpinnerNumberModel(1, 1, null, 1)); spReaction.setPreferredSize(attrDim); pc.gridx = 1; physical.add(spReaction, pc); prow++;
        pc.gridx = 0; pc.gridy = prow; physical.add(new JLabel("Strength:"), pc);
        spStrength = new JSpinner(new SpinnerNumberModel(1, 1, null, 1)); spStrength.setPreferredSize(attrDim); pc.gridx = 1; physical.add(spStrength, pc);

        JPanel mental = new JPanel(new GridBagLayout());
        mental.setBorder(BorderFactory.createTitledBorder("Mental"));
        GridBagConstraints mc = new GridBagConstraints();
        mc.insets = new Insets(2,2,2,2);
        mc.anchor = GridBagConstraints.WEST;
        int mrow = 0;
        mc.gridx = 0; mc.gridy = mrow; mental.add(new JLabel("Willpower:"), mc);
        spWillpower = new JSpinner(new SpinnerNumberModel(1, 1, null, 1)); spWillpower.setPreferredSize(attrDim); mc.gridx = 1; mental.add(spWillpower, mc); mrow++;
        mc.gridx = 0; mc.gridy = mrow; mental.add(new JLabel("Logic:"), mc);
        spLogic = new JSpinner(new SpinnerNumberModel(1, 1, null, 1)); spLogic.setPreferredSize(attrDim); mc.gridx = 1; mental.add(spLogic, mc); mrow++;
        mc.gridx = 0; mc.gridy = mrow; mental.add(new JLabel("Intuition:"), mc);
        spIntuition = new JSpinner(new SpinnerNumberModel(1, 1, null, 1)); spIntuition.setPreferredSize(attrDim); mc.gridx = 1; mental.add(spIntuition, mc); mrow++;
        mc.gridx = 0; mc.gridy = mrow; mental.add(new JLabel("Charisma:"), mc);
        spCharisma = new JSpinner(new SpinnerNumberModel(1, 1, null, 1)); spCharisma.setPreferredSize(attrDim); mc.gridx = 1; mental.add(spCharisma, mc);

        JPanel special = new JPanel(new GridBagLayout());
        special.setBorder(BorderFactory.createTitledBorder("Special"));
        GridBagConstraints sc = new GridBagConstraints();
        sc.insets = new Insets(2,2,2,2);
        sc.anchor = GridBagConstraints.WEST;
        int srow = 0;
        sc.gridx = 0; sc.gridy = srow; special.add(new JLabel("Edge:"), sc);
        spEdge = new JSpinner(new SpinnerNumberModel(1, 1, null, 1));
        Dimension specialDim = new Dimension(60, spEdge.getPreferredSize().height);
        spEdge.setPreferredSize(specialDim);
        sc.gridx = 1; special.add(spEdge, sc); srow++;
        sc.gridx = 0; sc.gridy = srow; special.add(new JLabel("Essence:"), sc);
        spEssence = new JSpinner(new SpinnerNumberModel(6.00, 0.01, 6.00, 0.01));
        spEssence.setPreferredSize(specialDim);
        sc.gridx = 1; special.add(spEssence, sc); srow++;
        sc.gridx = 0; sc.gridy = srow; special.add(new JLabel("Magic:"), sc);
        spMagic = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
        spMagic.setPreferredSize(specialDim);
        sc.gridx = 1; special.add(spMagic, sc); srow++;
        sc.gridx = 0; sc.gridy = srow; special.add(new JLabel("Resonance:"), sc);
        spResonance = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
        spResonance.setPreferredSize(specialDim);
        sc.gridx = 1; special.add(spResonance, sc);

        JPanel derived = new JPanel(new GridBagLayout());
        derived.setBorder(BorderFactory.createTitledBorder("Derived"));
        GridBagConstraints dc = new GridBagConstraints();
        dc.insets = new Insets(2,2,2,2);
        dc.anchor = GridBagConstraints.WEST;
        int drow = 0;
        dc.gridx = 0; dc.gridy = drow; derived.add(new JLabel("Composure:"), dc);
        tfComposure = new JTextField(5); tfComposure.setEditable(false); dc.gridx = 1; derived.add(tfComposure, dc); drow++;
        dc.gridx = 0; dc.gridy = drow; derived.add(new JLabel("Judge Intentions:"), dc);
        tfJudgeIntentions = new JTextField(5); tfJudgeIntentions.setEditable(false); dc.gridx = 1; derived.add(tfJudgeIntentions, dc); drow++;
        dc.gridx = 0; dc.gridy = drow; derived.add(new JLabel("Memory:"), dc);
        tfMemory = new JTextField(5); tfMemory.setEditable(false); dc.gridx = 1; derived.add(tfMemory, dc); drow++;
        dc.gridx = 0; dc.gridy = drow; derived.add(new JLabel("Lift/Carry:"), dc);
        tfLiftCarry = new JTextField(5); tfLiftCarry.setEditable(false); dc.gridx = 1; derived.add(tfLiftCarry, dc);

        c.gridx = 0; c.gridy = 0; panel.add(physical, c);
        c.gridx = 1; panel.add(mental, c);
        c.gridx = 2; panel.add(special, c);
        c.gridx = 3; panel.add(derived, c);

        contentPanel.add(panel);
        ChangeListener derivedListener = e -> updateDerivedAttributes();
        spWillpower.addChangeListener(derivedListener);
        spCharisma.addChangeListener(derivedListener);
        spIntuition.addChangeListener(derivedListener);
        spLogic.addChangeListener(derivedListener);
        spStrength.addChangeListener(derivedListener);
        spBody.addChangeListener(derivedListener);
        updateDerivedAttributes();
    }

/*
 * Future feature: Condition Monitor section
private void buildConditionMonitorSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Condition Monitor", TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;

        int row = 0;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Physical Damage Track Boxes:"), c);
        tfPhysicalBoxes = new JTextField(5); c.gridx = 1; panel.add(tfPhysicalBoxes, c);
        c.gridx = 2; panel.add(new JLabel("Stun Damage Track Boxes:"), c);
        tfStunBoxes = new JTextField(5); c.gridx = 3; panel.add(tfStunBoxes, c);

        contentPanel.add(panel);
    }
*/

    private JPanel buildSkillsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Skills", TitledBorder.LEFT, TitledBorder.TOP));

        skillsTableModel = new DefaultTableModel(new Object[]{"Type", "Skill", "Rank", "Attribute", "Category"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableSkills = new JTable(skillsTableModel);
        tableSkills.setAutoCreateRowSorter(true);

        loadSkills();
        loadSpecializations();

        TableColumn skillCol = tableSkills.getColumnModel().getColumn(1);
        JComboBox<String> cbSkillNames = new JComboBox<>(skillMap.keySet().toArray(new String[0]));
        skillCol.setCellEditor(new DefaultCellEditor(cbSkillNames));

        TableColumn rankCol = tableSkills.getColumnModel().getColumn(2);
        rankCol.setCellEditor(new DefaultCellEditor(new JComboBox<>(RANK_OPTIONS)));

        skillsTableModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 1) {
                int r = e.getFirstRow();
                Object val = skillsTableModel.getValueAt(r, 1);
                if (val != null && skillMap.containsKey(val.toString())) {
                    String[] info = skillMap.get(val.toString());
                    skillsTableModel.setValueAt("General", r, 0);
                    skillsTableModel.setValueAt(info[0], r, 3);
                    skillsTableModel.setValueAt(info[1], r, 4);
                } else {
                    skillsTableModel.setValueAt("", r, 0);
                    skillsTableModel.setValueAt("", r, 3);
                    skillsTableModel.setValueAt("", r, 4);
                }
            }
        });

        JScrollPane sp = new JScrollPane(tableSkills);
        tableSkills.setPreferredScrollableViewportSize(new Dimension(500, 150));

        JButton btnAddSkill = new JButton("Add Skill");
        btnAddSkill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddSkillDialog();
            }
        });
        JButton btnRemoveSkill = new JButton("Remove Skill");
        btnRemoveSkill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = tableSkills.getSelectedRow();
                if (row != -1) {
                    skillsTableModel.removeRow(row);
                    updateSkillCount();
                }
            }
        });
        JButton btnRaiseSkill = new JButton("Raise Skill");
        btnRaiseSkill.addActionListener(e -> {
            int row = tableSkills.getSelectedRow();
            if (row != -1) {
                int modelRow = tableSkills.convertRowIndexToModel(row);
                Object rankObj = skillsTableModel.getValueAt(modelRow, 2);
                if (rankObj != null) {
                    int idx = java.util.Arrays.asList(RANK_OPTIONS).indexOf(rankObj.toString());
                    if (idx >= 0 && idx < RANK_OPTIONS.length - 1) {
                        skillsTableModel.setValueAt(RANK_OPTIONS[idx + 1], modelRow, 2);
                    }
                }
            }
        });
        JButton btnLowerSkill = new JButton("Lower Skill");
        btnLowerSkill.addActionListener(e -> {
            int row = tableSkills.getSelectedRow();
            if (row != -1) {
                int modelRow = tableSkills.convertRowIndexToModel(row);
                Object rankObj = skillsTableModel.getValueAt(modelRow, 2);
                if (rankObj != null) {
                    int idx = java.util.Arrays.asList(RANK_OPTIONS).indexOf(rankObj.toString());
                    if (idx > 0) {
                        skillsTableModel.setValueAt(RANK_OPTIONS[idx - 1], modelRow, 2);
                    }
                }
            }
        });
        lblSkillCount = new JLabel("0 skills");
        JPanel buttonSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonSub.add(btnAddSkill);
        buttonSub.add(btnRemoveSkill);
        buttonSub.add(btnRaiseSkill);
        buttonSub.add(btnLowerSkill);
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.add(buttonSub, BorderLayout.WEST);
        btnPanel.add(lblSkillCount, BorderLayout.EAST);

        panel.add(new JLabel("Enter skills:"), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        updateSkillCount();
        return panel;
    }

    private JPanel buildQualitiesSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Qualities", TitledBorder.LEFT, TitledBorder.TOP));

        qualitiesTableModel = new DefaultTableModel(new Object[]{"Category", "Type", "Quality", "Karma"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return r == editingQualityRow && c < 3;
            }
        };
        tableQualities = new JTable(qualitiesTableModel);
        tableQualities.setAutoCreateRowSorter(true);
        tableQualities.setPreferredScrollableViewportSize(new Dimension(500, 150));

        String[] qualityCats = {"Magic","Matrix","Mental","Metagenic","Physical","Social","Vehicle"};
        TableColumn catCol = tableQualities.getColumnModel().getColumn(0);
        catCol.setCellEditor(new DefaultCellEditor(new JComboBox<>(qualityCats)));
        TableColumn typeCol = tableQualities.getColumnModel().getColumn(1);
        typeCol.setCellEditor(new DefaultCellEditor(new JComboBox<>(new String[]{"Positive","Negative"})));

        TableColumn qualCol = tableQualities.getColumnModel().getColumn(2);
        JComboBox<String> qualityBox = new JComboBox<>();
        qualCol.setCellEditor(new DefaultCellEditor(qualityBox) {
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                qualityBox.removeAllItems();
                Object catObj = table.getValueAt(row, 0);
                String cat = catObj == null ? "" : catObj.toString();
                Object typeObj = table.getValueAt(row, 1);
                String type = typeObj == null ? "" : typeObj.toString();
                for (QualityEntry qe : qualityEntries) {
                    if (cat.equalsIgnoreCase(qe.category) && type.equalsIgnoreCase(qe.type)) {
                        qualityBox.addItem(qe.name);
                    }
                }
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }
        });

        JScrollPane sp = new JScrollPane(tableQualities);

        loadQualities();

        qualitiesTableModel.addTableModelListener(e -> {
            if (e.getType() != javax.swing.event.TableModelEvent.UPDATE) return;
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (row < 0) return;
            if (col == 0 || col == 1) {
                qualitiesTableModel.setValueAt("", row, 2);
                qualitiesTableModel.setValueAt("", row, 3);
            } else if (col == 2) {
                Object qVal = qualitiesTableModel.getValueAt(row, 2);
                if (qVal != null) {
                    for (QualityEntry qe : qualityEntries) {
                        if (qe.name.equals(qVal.toString())) {
                            qualitiesTableModel.setValueAt(qe.type, row, 1);
                            qualitiesTableModel.setValueAt(String.valueOf(qe.karma), row, 3);
                            break;
                        }
                    }
                }
            }
        });

        JButton btnAddQuality = new JButton("Add Quality");
        JButton btnSaveQuality = new JButton("Save Quality");
        CardLayout addSaveLayout = new CardLayout();
        JPanel addSavePanel = new JPanel(addSaveLayout);
        addSavePanel.add(btnAddQuality, "ADD");
        addSavePanel.add(btnSaveQuality, "SAVE");
        addSaveLayout.show(addSavePanel, "ADD");

        btnAddQuality.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                qualitiesTableModel.addRow(new Object[]{"", "", "", ""});
                int newRow = qualitiesTableModel.getRowCount() - 1;
                tableQualities.setRowSelectionInterval(newRow, newRow);
                editingQualityRow = newRow;
                updateQualityCount();
                addSaveLayout.show(addSavePanel, "SAVE");
            }
        });
        JButton btnRemoveQuality = new JButton("Remove Quality");
        btnRemoveQuality.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = tableQualities.getSelectedRow();
                if (row != -1) {
                    int modelRow = tableQualities.convertRowIndexToModel(row);
                    Object cat = qualitiesTableModel.getValueAt(modelRow, 0);
                    if ("Metatype".equals(cat)) {
                        JOptionPane.showMessageDialog(frame,
                                "ERROR: Qualities inherited from a Metatype cannot be removed.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if ("Metagenic".equals(cat)) {
                        JOptionPane.showMessageDialog(frame,
                                "ERROR: Qualities inherited by SURGE Collective cannot be removed.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    removeQualityRow(modelRow);
                    if (editingQualityRow == modelRow) {
                        editingQualityRow = -1;
                    } else if (editingQualityRow > modelRow) {
                        editingQualityRow--;
                    }
                    updateQualityCount();
                }
            }
        });
        btnSaveQuality.addActionListener(e -> {
            if (editingQualityRow != -1) {
                if (tableQualities.isEditing()) {
                    tableQualities.getCellEditor().stopCellEditing();
                }
                int modelRow = editingQualityRow;
                String name = (String) qualitiesTableModel.getValueAt(modelRow, 2);
                String karmaStr = (String) qualitiesTableModel.getValueAt(modelRow, 3);
                int cost = 0;
                try { cost = Integer.parseInt(karmaStr); } catch (Exception ex) {}
                addOrUpdateKarma("Quality", name == null ? "" : name, cost);
                tableQualities.clearSelection();
                editingQualityRow = -1;
            }
            addSaveLayout.show(addSavePanel, "ADD");
        });
        lblQualityCount = new JLabel("0 qualities");
        JPanel buttonSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonSub.add(addSavePanel);
        buttonSub.add(btnRemoveQuality);
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.add(buttonSub, BorderLayout.WEST);
        btnPanel.add(lblQualityCount, BorderLayout.EAST);

        panel.add(new JLabel("Enter qualities:"), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        updateQualityCount();
        return panel;
    }

    private JPanel buildContactsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Contacts", TitledBorder.LEFT, TitledBorder.TOP));

        contactsTableModel = new DefaultTableModel(new Object[]{"Name", "Loyalty", "Connection"}, 0);
        tableContacts = new JTable(contactsTableModel);
        tableContacts.setPreferredScrollableViewportSize(new Dimension(500, 150));
        JScrollPane sp = new JScrollPane(tableContacts);

        JButton btnAddContact = new JButton("Add Contact");
        btnAddContact.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                contactsTableModel.addRow(new Object[]{"", "", ""});
            }
        });
        JButton btnRemoveContact = new JButton("Remove Contact");
        btnRemoveContact.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = tableContacts.getSelectedRow();
                if (row != -1) {
                    int modelRow = tableContacts.convertRowIndexToModel(row);
                    contactsTableModel.removeRow(modelRow);
                }
            }
        });
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(btnAddContact);
        btnPanel.add(btnRemoveContact);

        panel.add(new JLabel("Enter contacts:"), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildLifestyleSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Lifestyle", TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;

        int row = 0;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Nuyen:"), c);
        tfNuyen = new JTextField(10); c.gridx = 1; panel.add(tfNuyen, c);
        c.gridx = 2; panel.add(new JLabel("Primary Lifestyle:"), c);
        tfPrimaryLifestyle = new JTextField(15); c.gridx = 3; panel.add(tfPrimaryLifestyle, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Fake IDs / Lifestyles / Funds / Licenses:"), c);
        tfFakeIDs = new JTextField(25); c.gridx = 1; c.gridwidth = 3; panel.add(tfFakeIDs, c);
        c.gridwidth = 1; row++;

        return panel;
    }

    private JPanel buildNotesSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Notes", TitledBorder.LEFT, TitledBorder.TOP));
        taNotes = new JTextArea(4, 60);
        taNotes.setLineWrap(true);
        taNotes.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(new JScrollPane(taNotes), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildKarmaLogPanel() {
        karmaLogModel = new DefaultTableModel(new Object[]{"Type", "Name", "Cost"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableKarmaLog = new JTable(karmaLogModel);
        tableKarmaLog.setPreferredScrollableViewportSize(new Dimension(250, 500));
        JScrollPane sp = new JScrollPane(tableKarmaLog);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Karma Log", TitledBorder.LEFT, TitledBorder.TOP));
        panel.add(sp, BorderLayout.CENTER);
        lblLoggedKarma = new JLabel("Logged Karma: 0");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.add(lblLoggedKarma);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void updateSkillCount() {
        if (lblSkillCount != null) {
            lblSkillCount.setText(skillsTableModel.getRowCount() + " skills");
        }
    }

    private void updateQualityCount() {
        if (lblQualityCount != null) {
            lblQualityCount.setText(qualitiesTableModel.getRowCount() + " qualities");
        }
    }

    private void removeQualityRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= qualitiesTableModel.getRowCount()) return;
        Object nameObj = qualitiesTableModel.getValueAt(rowIndex, 2);
        String qName = nameObj == null ? "" : nameObj.toString();
        qualitiesTableModel.removeRow(rowIndex);
        removeKarma("Quality", qName);
    }

    private void addQuality(String category, String name) {
        qualitiesTableModel.addRow(new Object[]{category, "", name, ""});
        int row = qualitiesTableModel.getRowCount() - 1;
        for (QualityEntry qe : qualityEntries) {
            if (qe.name.equals(name)) {
                qualitiesTableModel.setValueAt(qe.type, row, 1);
                qualitiesTableModel.setValueAt(String.valueOf(qe.karma), row, 3);
                addOrUpdateKarma("Quality", name, qe.karma);
                break;
            }
        }
        updateQualityCount();
    }

    private Integer findKarmaRow(String type, String name) {
        for (int i = 0; i < karmaLogModel.getRowCount(); i++) {
            Object t = karmaLogModel.getValueAt(i, 0);
            Object n = karmaLogModel.getValueAt(i, 1);
            if (type.equals(t) && name.equals(n)) {
                return i;
            }
        }
        return null;
    }

    private void addOrUpdateKarma(String type, String name, int cost) {
        Integer row = findKarmaRow(type, name);
        if (row == null) {
            karmaLogModel.addRow(new Object[]{type, name, cost});
        } else {
            karmaLogModel.setValueAt(cost, row, 2);
        }
        updateLoggedKarma();
    }

    private void removeKarma(String type, String name) {
        Integer row = findKarmaRow(type, name);
        if (row != null) {
            karmaLogModel.removeRow(row.intValue());
        }
        updateLoggedKarma();
    }

    private void updateLoggedKarma() {
        int total = 0;
        for (int i = 0; i < karmaLogModel.getRowCount(); i++) {
            Object v = karmaLogModel.getValueAt(i, 2);
            try {
                total += Integer.parseInt(v.toString());
            } catch (Exception ignored) {}
        }
        if (lblLoggedKarma != null) {
            lblLoggedKarma.setText("Logged Karma: " + total);
        }
    }

    private JPanel buildWizardPanel() {
        wizardMetaCombo = new JComboBox<>();
        wizardSurgeCheck = new JCheckBox("SURGE");
        wizardSurgeCombo = new JComboBox<>();
        wizardStatusCombo = new JComboBox<>();
        wizardNationalityField = new JTextField(10);
        wizardLanguageField = new JTextField(10);

        wizardSurgeCheck.addActionListener(e -> {
            boolean sel = wizardSurgeCheck.isSelected();
            wizardSurgeCombo.setVisible(sel);
            if (!sel) {
                wizardSurgeCombo.setSelectedItem("No Collective");
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Life Path Wizard", TitledBorder.LEFT, TitledBorder.TOP));

        JPanel stage1 = new JPanel(new GridBagLayout());
        stage1.setBorder(BorderFactory.createTitledBorder("Stage 1: Born This Way"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.anchor = GridBagConstraints.WEST;
        int row = 0;

        c.gridx=0; c.gridy=row; stage1.add(new JLabel("Metatype:"), c);
        c.gridx=1; stage1.add(wizardMetaCombo, c); row++;

        c.gridx=0; c.gridy=row; stage1.add(wizardSurgeCheck, c); row++;

        c.gridx=0; c.gridy=row; stage1.add(new JLabel("SURGE Collective:"), c);
        c.gridx=1; stage1.add(wizardSurgeCombo, c); row++;

        c.gridx=0; c.gridy=row; stage1.add(new JLabel("Status:"), c);
        c.gridx=1; stage1.add(wizardStatusCombo, c); row++;

        c.gridx=0; c.gridy=row; stage1.add(new JLabel("Nationality:"), c);
        c.gridx=1; stage1.add(wizardNationalityField, c); row++;

        c.gridx=0; c.gridy=row; stage1.add(new JLabel("Native Language:"), c);
        c.gridx=1; stage1.add(wizardLanguageField, c); row++;

        JPanel stage2 = new JPanel();
        stage2.setBorder(BorderFactory.createTitledBorder("Stage 2: Growing Up"));

        JPanel stage3 = new JPanel();
        stage3.setBorder(BorderFactory.createTitledBorder("Stage 3: Coming of Age"));

        JButton btnApply = new JButton("Apply Changes");
        JButton btnCancel = new JButton("Cancel Wizard");
        btnApply.addActionListener(e -> applyWizardSelections());
        btnCancel.addActionListener(e -> { wizardPanel.setVisible(false); frame.revalidate(); });
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnApply);
        btnPanel.add(btnCancel);

        panel.add(stage1);
        panel.add(stage2);
        panel.add(stage3);
        panel.add(btnPanel);

        return panel;
    }

    private void applyWizardSelections() {
        String meta = (String) wizardMetaCombo.getSelectedItem();
        if (meta != null) {
            for (int i = 0; i < cbMetatype.getItemCount(); i++) {
                MetaItem mi = cbMetatype.getItemAt(i);
                if (mi.name.equals(meta)) { cbMetatype.setSelectedIndex(i); break; }
            }
            setBaseAttributesForMetatype(meta);
        }

        boolean surge = wizardSurgeCheck.isSelected();
        chkSurge.setSelected(surge);
        lblSurgeCollective.setVisible(surge);
        cbSurgeCollective.setVisible(surge);
        if (surge) {
            cbSurgeCollective.setSelectedItem(wizardSurgeCombo.getSelectedItem());
        } else {
            cbSurgeCollective.setSelectedItem("No Collective");
        }

        String statusSel = (String) wizardStatusCombo.getSelectedItem();
        if (statusSel != null) {
            cbStatus.setSelectedItem(statusSel);
            switch (statusSel) {
                case "Technomancer":
                    spResonance.setValue(1);
                    break;
                case "Aspected Magician":
                    spMagic.setValue(2);
                    break;
                case "Full Magician":
                case "Mystic Adept":
                case "Adept":
                    spMagic.setValue(1);
                    break;
                case "Mundane":
                    spEdge.setValue(((Number) spEdge.getValue()).intValue() + 1);
                    break;
            }
        }

        tfNationality.setText(wizardNationalityField.getText());
        String lang = wizardLanguageField.getText().trim();
        if (!lang.isEmpty()) {
            skillsTableModel.addRow(new Object[]{"Language", "LG: " + lang, "Native", "", ""});
            updateSkillCount();
        }

        wizardPanel.setVisible(false);
        frame.revalidate();
    }

    private void loadMetatypes() {
        cbMetatype.removeAllItems();
        metatypeMap.clear();
        metatypeKarmaMap.clear();
        java.io.File file = new java.io.File("Shadowrun_Metatype.csv");
        if (!file.exists()) return;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line = br.readLine(); // skip header
            java.util.List<String[]> mains = new java.util.ArrayList<>();
            java.util.Map<String, java.util.List<String[]>> variants = new java.util.LinkedHashMap<>();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 6) continue;
                String name = parts[0].trim();
                int karma = 0;
                try { karma = Integer.parseInt(parts[1].trim()); } catch(Exception ex) {}
                String type = parts[2].trim();
                String root = parts[3].trim();
                if (type.equals("Metahuman") || type.equals("Metasapient")) {
                    mains.add(parts);
                } else if (type.equals("Metavariant")) {
                    variants.computeIfAbsent(root, k -> new java.util.ArrayList<>()).add(parts);
                }
            }
            for (String[] m : mains) {
                String name = m[0].trim();
                int karma = 0;
                try { karma = Integer.parseInt(m[1].trim()); } catch(Exception ex) {}
                double h = Double.parseDouble(m[4]);
                double w = Double.parseDouble(m[5]);
                MetaItem item = new MetaItem(name, false);
                cbMetatype.addItem(item);
                metatypeMap.put(name, new double[]{h, w});
                metatypeKarmaMap.put(name, karma);
                java.util.List<String[]> varList = variants.get(name);
                if (varList != null) {
                    for (String[] v : varList) {
                        String varName = v[0].trim();
                        int vkarma = 0;
                        try { vkarma = Integer.parseInt(v[1].trim()); } catch(Exception ex) {}
                        double vh = Double.parseDouble(v[4]);
                        double vw = Double.parseDouble(v[5]);
                        cbMetatype.addItem(new MetaItem(varName, true));
                        metatypeMap.put(varName, new double[]{vh, vw});
                        metatypeKarmaMap.put(varName, vkarma);
                    }
                }
            }
        } catch (Exception ignored) {}

        cbMetatype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                MetaItem item = (MetaItem) cbMetatype.getSelectedItem();
                if (item != null && metatypeMap.containsKey(item.name)) {
                    double[] vals = metatypeMap.get(item.name);
                    tfHeight.setText(String.format("%.0f", vals[0]));
                    tfWeight.setText(String.format("%.0f", vals[1]));
                    updateHeightFeet();
                    updateWeightLbs();
                    loadRacialTraitsForMetatype(item.name);
                    if (lastMetatype != null) {
                        removeKarma("Metatype", lastMetatype);
                    }
                    int cost = metatypeKarmaMap.getOrDefault(item.name, 0);
                    addOrUpdateKarma("Metatype", item.name, cost);
                    lastMetatype = item.name;
                }
            }
        });
    }

    private void loadSurgeCollectives() {
        cbSurgeCollective.removeAllItems();
        cbSurgeCollective.addItem("No Collective");
        surgeKarmaMap.clear();
        java.io.File file = new java.io.File("Shadowrun_Metatype.csv");
        if (!file.exists()) return;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    String name = parts[0].trim();
                    int karma = 0;
                    try { karma = Integer.parseInt(parts[1].trim()); } catch(Exception ex) {}
                    String type = parts[2].trim();
                    if ("Changeling".equalsIgnoreCase(type)) {
                        cbSurgeCollective.addItem(name);
                        surgeKarmaMap.put(name, karma);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private void updateHeightFeet() {
        try {
            double cm = Double.parseDouble(tfHeight.getText());
            int totalInches = (int) Math.round(cm / 2.54);
            int feet = totalInches / 12;
            int inches = totalInches % 12;
            tfHeightFt.setText(String.format("%d'%d\"", feet, inches));
        } catch (Exception ex) {
            tfHeightFt.setText("");
        }
    }

    private void updateWeightLbs() {
        try {
            double kg = Double.parseDouble(tfWeight.getText());
            int lbs = (int) Math.round(kg * 2.20462);
            tfWeightLbs.setText(String.valueOf(lbs));
        } catch (Exception ex) {
            tfWeightLbs.setText("");
        }
    }

    private void updateDerivedAttributes() {
        try {
            int will = ((Number) spWillpower.getValue()).intValue();
            int cha = ((Number) spCharisma.getValue()).intValue();
            int intui = ((Number) spIntuition.getValue()).intValue();
            int log = ((Number) spLogic.getValue()).intValue();
            int str = ((Number) spStrength.getValue()).intValue();
            int bod = ((Number) spBody.getValue()).intValue();
            tfComposure.setText(String.valueOf(will + cha));
            tfJudgeIntentions.setText(String.valueOf(intui + cha));
            tfMemory.setText(String.valueOf(log + will));
            tfLiftCarry.setText(String.valueOf(str + bod));
        } catch (Exception ex) {
            // ignore
        }
    }

    private void clearForm() {
        tfName.setText("");
        tfPlayer.setText("");
        tfAge.setText("");
        tfNationality.setText("");
        tfHeight.setText("");
        tfHeightFt.setText("");
        tfWeight.setText("");
        tfWeightLbs.setText("");
        tfKarma.setText("50");
        tfTotalKarma.setText("");
        tfNuyen.setText("");
        tfPrimaryLifestyle.setText("");
        tfFakeIDs.setText("");
        taNotes.setText("");

        cbRole.setSelectedIndex(-1);
        cbStatus.setSelectedIndex(-1);
        cbMetatype.setSelectedIndex(-1);
        cbGender.setSelectedIndex(-1);
        chkSurge.setSelected(false);
        cbSurgeCollective.setSelectedIndex(0);
        lblSurgeCollective.setVisible(false);
        cbSurgeCollective.setVisible(false);

        spBody.setValue(1);
        spAgility.setValue(1);
        spReaction.setValue(1);
        spStrength.setValue(1);
        spWillpower.setValue(1);
        spLogic.setValue(1);
        spIntuition.setValue(1);
        spCharisma.setValue(1);
        spEdge.setValue(1);
        spEssence.setValue(6.0);
        spMagic.setValue(0);
        spResonance.setValue(0);

        skillsTableModel.setRowCount(0);
        updateSkillCount();
        qualitiesTableModel.setRowCount(0);
        updateQualityCount();
        contactsTableModel.setRowCount(0);
        karmaLogModel.setRowCount(0);
        updateLoggedKarma();

        lastMetatype = null;
        lastSurgeCollective = null;

        updateDerivedAttributes();
    }

    private String[][] showQualityPairDialog(String title) {
        JComboBox<String> cbPosCat = new JComboBox<>(QUALITY_CATEGORIES);
        JComboBox<String> cbNegCat = new JComboBox<>(QUALITY_CATEGORIES);
        JComboBox<String> cbPosQual = new JComboBox<>();
        JComboBox<String> cbNegQual = new JComboBox<>();

        java.awt.event.ActionListener posListener = e -> {
            cbPosQual.removeAllItems();
            String cat = (String) cbPosCat.getSelectedItem();
            for (QualityEntry qe : qualityEntries) {
                if ("Positive".equalsIgnoreCase(qe.type) && qe.category.equalsIgnoreCase(cat)) {
                    cbPosQual.addItem(qe.name);
                }
            }
        };
        java.awt.event.ActionListener negListener = e -> {
            cbNegQual.removeAllItems();
            String cat = (String) cbNegCat.getSelectedItem();
            for (QualityEntry qe : qualityEntries) {
                if ("Negative".equalsIgnoreCase(qe.type) && qe.category.equalsIgnoreCase(cat)) {
                    cbNegQual.addItem(qe.name);
                }
            }
        };
        cbPosCat.addActionListener(posListener);
        cbNegCat.addActionListener(negListener);
        if (cbPosCat.getItemCount() > 0) cbPosCat.setSelectedIndex(0);
        if (cbNegCat.getItemCount() > 0) cbNegCat.setSelectedIndex(0);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.anchor = GridBagConstraints.WEST;

        c.gridx=0; c.gridy=0; panel.add(new JLabel("Positive"), c);
        c.gridx=1; panel.add(new JLabel("Negative"), c);

        c.gridy=1; c.gridx=0; panel.add(cbPosCat, c);
        c.gridx=1; panel.add(cbNegCat, c);

        c.gridy=2; c.gridx=0; panel.add(cbPosQual, c);
        c.gridx=1; panel.add(cbNegQual, c);

        while (true) {
            int opt = JOptionPane.showOptionDialog(frame, panel, title,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, new Object[]{"OK","Skip"}, "OK");
            if (opt != JOptionPane.OK_OPTION) return null;
            Object pq = cbPosQual.getSelectedItem();
            Object nq = cbNegQual.getSelectedItem();
            if (pq != null && nq != null) {
                return new String[][]{
                        {(String) cbPosCat.getSelectedItem(), pq.toString()},
                        {(String) cbNegCat.getSelectedItem(), nq.toString()}
                };
            }
            JOptionPane.showMessageDialog(frame,
                    "Both qualities must be selected or choose Skip.",
                    title, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setBaseAttributesForMetatype(String meta) {
        java.io.File file = new java.io.File("Shadowrun_Metatype.csv");
        if (!file.exists() || meta == null) return;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 15 && p[0].trim().equalsIgnoreCase(meta)) {
                    try {
                        if (!p[6].trim().isEmpty() && Integer.parseInt(p[6].trim()) > 6) spBody.setValue(2);
                        if (!p[7].trim().isEmpty() && Integer.parseInt(p[7].trim()) > 6) spAgility.setValue(2);
                        if (!p[8].trim().isEmpty() && Integer.parseInt(p[8].trim()) > 6) spReaction.setValue(2);
                        if (!p[9].trim().isEmpty() && Integer.parseInt(p[9].trim()) > 6) spStrength.setValue(2);
                        if (!p[10].trim().isEmpty() && Integer.parseInt(p[10].trim()) > 6) spWillpower.setValue(2);
                        if (!p[11].trim().isEmpty() && Integer.parseInt(p[11].trim()) > 6) spLogic.setValue(2);
                        if (!p[12].trim().isEmpty() && Integer.parseInt(p[12].trim()) > 6) spIntuition.setValue(2);
                        if (!p[13].trim().isEmpty() && Integer.parseInt(p[13].trim()) > 6) spCharisma.setValue(2);
                        if (!p[14].trim().isEmpty() && Integer.parseInt(p[14].trim()) > 6) spEdge.setValue(2);
                    } catch (NumberFormatException ex) {
                        // ignore malformed numbers
                    }
                    break;
                }
            }
        } catch (Exception ignored) {}
        updateDerivedAttributes();
    }

    private void loadRacialTraitsForMetatype(String owner) {
        if (qualitiesTableModel == null) return;
        for (int i = qualitiesTableModel.getRowCount() - 1; i >= 0; i--) {
            Object cat = qualitiesTableModel.getValueAt(i, 0);
            if ("Metatype".equals(cat)) {
                removeQualityRow(i);
            }
        }
        java.io.File file = new java.io.File("Shadowrun_RacialTraits.csv");
        if (!file.exists()) { updateQualityCount(); return; }
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (parts.length >= 3 && parts[2].trim().equalsIgnoreCase(owner)) {
                    String trait = parts[0].replaceAll("^\"|\"$", "").trim();
                    String type = parts[1].trim();
                    qualitiesTableModel.addRow(new Object[]{"Metatype", type, trait, "0"});
                }
            }
        } catch (Exception ignored) {}
        updateQualityCount();
    }

    private void removeMetagenicQualities() {
        if (qualitiesTableModel == null) return;
        for (int i = qualitiesTableModel.getRowCount() - 1; i >= 0; i--) {
            Object cat = qualitiesTableModel.getValueAt(i, 0);
            if ("Metagenic".equals(cat)) {
                removeQualityRow(i);
            }
        }
        updateQualityCount();
    }

    private void loadRacialTraitsForCollective(String owner) {
        removeMetagenicQualities();
        if (owner == null || owner.equals("No Collective")) { return; }
        java.io.File file = new java.io.File("Shadowrun_RacialTraits.csv");
        if (!file.exists()) { return; }
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (parts.length >= 3 && parts[2].trim().equalsIgnoreCase(owner)) {
                    String trait = parts[0].replaceAll("^\"|\"$", "").trim();
                    String type = parts[1].trim();
                    qualitiesTableModel.addRow(new Object[]{"Metagenic", type, trait, "0"});
                }
            }
        } catch (Exception ignored) {}
        updateQualityCount();
    }

    private void loadSkills() {
        skillMap.clear();
        java.io.File file = new java.io.File("Shadowrun_Core_Skills.csv");
        if (!file.exists()) return;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    String name = parts[0].trim();
                    String attr = parts[1].trim();
                    String category = parts[2].trim();
                    skillMap.put(name, new String[]{attr, category});
                }
            }
        } catch (Exception ignored) {}
    }

    private void loadSpecializations() {
        specializationMap.clear();
        java.io.File file = new java.io.File("Shadowrun_Specializations.csv");
        if (!file.exists()) return;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    String spec = parts[0].trim();
                    String parent = parts[1].trim();
                    String attr = parts[2].trim();
                    String cat = parts[3].trim();
                    specializationMap.put(spec, new String[]{parent, attr, cat});
                }
            }
        } catch (Exception ignored) {}
    }

    private void loadQualities() {
        qualityEntries.clear();
        java.io.File file = new java.io.File("Shadowrun_Qualities.csv");
        if (!file.exists()) return;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (parts.length >= 7) {
                    String name = parts[0].replaceAll("^\"|\"$", "").trim();
                    String type = parts[1].trim();
                    String category = parts[2].trim();
                    int karma = 0;
                    try { karma = Integer.parseInt(parts[3].trim()); } catch (Exception ex) {}
                    String instance = parts[4].trim();
                    int mn = 1, mx = 1;
                    try { mn = Integer.parseInt(parts[5].trim()); } catch (Exception ex) {}
                    try { mx = Integer.parseInt(parts[6].trim()); } catch (Exception ex) {}
                    qualityEntries.add(new QualityEntry(name, type, category, karma, instance, mn, mx));
                }
            }
        } catch (Exception ignored) {}
    }

    private void showAddSkillDialog() {
        JDialog dialog = new JDialog(frame, "Add Skill Dialog", true);
        JPanel main = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.anchor = GridBagConstraints.WEST;
        int row = 0;

        c.gridx = 0; c.gridy = row; main.add(new JLabel("Type:"), c);
        JComboBox<String> cbType = new JComboBox<>(new String[]{"General","Specialization","Knowledge","Language"});
        c.gridx = 1; main.add(cbType, c); row++;

        CardLayout cl = new CardLayout();
        JPanel cards = new JPanel(cl);

        // General card
        JPanel general = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2,2,2,2); gc.anchor = GridBagConstraints.WEST;
        int gr = 0;
        gc.gridx=0; gc.gridy=gr; general.add(new JLabel("Skill Name:"), gc);
        java.util.List<String> genSkills = new java.util.ArrayList<>(skillMap.keySet());
        java.util.Collections.sort(genSkills);
        JComboBox<String> cbGenSkill = new JComboBox<>(genSkills.toArray(new String[0]));
        gc.gridx=1; general.add(cbGenSkill, gc); gr++;
        gc.gridx=0; gc.gridy=gr; general.add(new JLabel("Category:"), gc);
        JTextField tfGenCat = new JTextField(15); tfGenCat.setEditable(false);
        gc.gridx=1; general.add(tfGenCat, gc); gr++;
        gc.gridx=0; gc.gridy=gr; general.add(new JLabel("Primary Attribute:"), gc);
        JTextField tfGenAttr = new JTextField(15); tfGenAttr.setEditable(false);
        gc.gridx=1; general.add(tfGenAttr, gc); gr++;
        gc.gridx=0; gc.gridy=gr; general.add(new JLabel("Rank:"), gc);
        JComboBox<String> cbGenRank = new JComboBox<>(RANK_OPTIONS);
        gc.gridx=1; general.add(cbGenRank, gc);

        cbGenSkill.addActionListener(e -> {
            String s = (String) cbGenSkill.getSelectedItem();
            if (s != null && skillMap.containsKey(s)) {
                String[] info = skillMap.get(s);
                tfGenAttr.setText(info[0]);
                tfGenCat.setText(info[1]);
            } else {
                tfGenAttr.setText("");
                tfGenCat.setText("");
            }
        });
        if(cbGenSkill.getItemCount()>0) cbGenSkill.setSelectedIndex(0);

        // Specialization card
        JPanel spec = new JPanel(new GridBagLayout());
        GridBagConstraints sc = new GridBagConstraints();
        sc.insets = new Insets(2,2,2,2); sc.anchor = GridBagConstraints.WEST;
        int sr = 0;
        sc.gridx=0; sc.gridy=sr; spec.add(new JLabel("Specialization:"), sc);
        java.util.List<String> specSkills = new java.util.ArrayList<>(specializationMap.keySet());
        java.util.Collections.sort(specSkills);
        JComboBox<String> cbSpec = new JComboBox<>(specSkills.toArray(new String[0]));
        sc.gridx=1; spec.add(cbSpec, sc); sr++;
        sc.gridx=0; sc.gridy=sr; spec.add(new JLabel("Parent Skill:"), sc);
        JTextField tfParent = new JTextField(15); tfParent.setEditable(false);
        sc.gridx=1; spec.add(tfParent, sc); sr++;
        sc.gridx=0; sc.gridy=sr; spec.add(new JLabel("Category:"), sc);
        JTextField tfSpecCat = new JTextField(15); tfSpecCat.setEditable(false);
        sc.gridx=1; spec.add(tfSpecCat, sc); sr++;
        sc.gridx=0; sc.gridy=sr; spec.add(new JLabel("Primary Attribute:"), sc);
        JTextField tfSpecAttr = new JTextField(15); tfSpecAttr.setEditable(false);
        sc.gridx=1; spec.add(tfSpecAttr, sc); sr++;
        sc.gridx=0; sc.gridy=sr; spec.add(new JLabel("Rank:"), sc);
        JComboBox<String> cbSpecRank = new JComboBox<>(RANK_OPTIONS);
        sc.gridx=1; spec.add(cbSpecRank, sc);

        cbSpec.addActionListener(e -> {
            String s = (String) cbSpec.getSelectedItem();
            if (s != null && specializationMap.containsKey(s)) {
                String[] info = specializationMap.get(s);
                tfParent.setText(info[0]);
                tfSpecAttr.setText(info[1]);
                tfSpecCat.setText(info[2]);
            } else {
                tfParent.setText("");
                tfSpecAttr.setText("");
                tfSpecCat.setText("");
            }
        });
        if(cbSpec.getItemCount()>0) cbSpec.setSelectedIndex(0);

        // Knowledge card
        JPanel knowledge = new JPanel(new GridBagLayout());
        GridBagConstraints kc = new GridBagConstraints();
        kc.insets = new Insets(2,2,2,2); kc.anchor = GridBagConstraints.WEST;
        kc.gridx=0; kc.gridy=0; knowledge.add(new JLabel("Area/Field:"), kc);
        JTextField tfKnowledge = new JTextField(15);
        kc.gridx=1; knowledge.add(tfKnowledge, kc);

        // Language card
        JPanel language = new JPanel(new GridBagLayout());
        GridBagConstraints lc = new GridBagConstraints();
        lc.insets = new Insets(2,2,2,2); lc.anchor = GridBagConstraints.WEST;
        lc.gridx=0; lc.gridy=0; language.add(new JLabel("Language:"), lc);
        JTextField tfLanguage = new JTextField(15);
        lc.gridx=1; language.add(tfLanguage, lc); lc.gridy=1; lc.gridx=0; language.add(new JLabel("Proficiency:"), lc);
        JComboBox<String> cbProf = new JComboBox<>(new String[]{"1 - Elementary","2 - Intermediate","3 - Proficient","4 - Native"});
        lc.gridx=1; language.add(cbProf, lc);

        cards.add(general, "General");
        cards.add(spec, "Specialization");
        cards.add(knowledge, "Knowledge");
        cards.add(language, "Language");

        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        main.add(cards, c); row++;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        c.gridx=0; c.gridy=row; c.gridwidth=2; main.add(btnPanel,c);

        cbType.addActionListener(e -> {
            String t = (String) cbType.getSelectedItem();
            cl.show(cards, t);
        });
        cbType.setSelectedIndex(0);

        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String t = (String) cbType.getSelectedItem();
            String skill=""; String rank=""; String attr=""; String cat="";
            if ("General".equals(t)) {
                skill = (String) cbGenSkill.getSelectedItem();
                rank = (String) cbGenRank.getSelectedItem();
                attr = tfGenAttr.getText();
                cat = tfGenCat.getText();
            } else if ("Specialization".equals(t)) {
                skill = (String) cbSpec.getSelectedItem();
                rank = (String) cbSpecRank.getSelectedItem();
                attr = tfSpecAttr.getText();
                cat = tfSpecCat.getText();
            } else if ("Knowledge".equals(t)) {
                skill = "KB: " + tfKnowledge.getText();
                rank = "N/A";
            } else if ("Language".equals(t)) {
                skill = "LG: " + tfLanguage.getText();
                rank = (String) cbProf.getSelectedItem();
            }
            if (skill != null && !skill.trim().isEmpty()) {
                skillsTableModel.addRow(new Object[]{t, skill, rank, attr, cat});
                updateSkillCount();
            }
            dialog.dispose();
        });

        dialog.getContentPane().add(main);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }


    private void loadArchetypes() {
        cbRole.removeAllItems();
        archetypeMap.clear();
        java.io.File file = new java.io.File("Shadowrun_Archetype.csv");
        if (!file.exists()) return;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (parts.length >= 3) {
                    String name = parts[0].trim();
                    String desc = parts[1].replaceAll("^\"|\"$", "").trim();
                    String focus = parts[2].trim();
                    cbRole.addItem(name);
                    archetypeMap.put(name, new String[]{name, desc, focus});
                }
            }
        } catch (Exception ignored) {}
    }

/*
 * Future feature: Weapons and Armor section
private void buildWeaponsArmorSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Weapons & Armor", TitledBorder.LEFT, TitledBorder.TOP));

        // Ranged Weapons
        JPanel sub1 = new JPanel(new BorderLayout());
        sub1.setBorder(BorderFactory.createTitledBorder("Ranged Weapons"));
        taRangedWeapons = new JTextArea(4, 60);
        taRangedWeapons.setLineWrap(true);
        taRangedWeapons.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        sub1.add(new JLabel("Enter ranged weapons (one per line, format: Weapon, DV, Mode, Close, Near, Far, Extreme, Ammo):"), BorderLayout.NORTH);
        sub1.add(new JScrollPane(taRangedWeapons), BorderLayout.CENTER);
        panel.add(sub1);

        // Melee Weapons
        JPanel sub2 = new JPanel(new BorderLayout());
        sub2.setBorder(BorderFactory.createTitledBorder("Melee Weapons"));
        taMeleeWeapons = new JTextArea(3, 60);
        taMeleeWeapons.setLineWrap(true);
        taMeleeWeapons.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        sub2.add(new JLabel("Enter melee weapons (one per line, format: Weapon, DV, Close):"), BorderLayout.NORTH);
        sub2.add(new JScrollPane(taMeleeWeapons), BorderLayout.CENTER);
        panel.add(sub2);

        // Armor
        JPanel sub3 = new JPanel(new BorderLayout());
        sub3.setBorder(BorderFactory.createTitledBorder("Armor"));
        taArmor = new JTextArea(3, 60);
        taArmor.setLineWrap(true);
        taArmor.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        sub3.add(new JLabel("Enter armor (one per line, format: Armor Name, Rating, Notes):"), BorderLayout.NORTH);
        sub3.add(new JScrollPane(taArmor), BorderLayout.CENTER);
        panel.add(sub3);

        contentPanel.add(panel);
    }
*/

/*
 * Future feature: Matrix section
private void buildMatrixSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Matrix", TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        int row = 0;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Attack:"), c);
        tfMatrixAttack = new JTextField(3); c.gridx = 1; panel.add(tfMatrixAttack, c);
        c.gridx = 2; panel.add(new JLabel("Sleaze:"), c);
        tfMatrixSleaze = new JTextField(3); c.gridx = 3; panel.add(tfMatrixSleaze, c);
        c.gridx = 4; panel.add(new JLabel("Data Processing:"), c);
        tfMatrixDataProc = new JTextField(3); c.gridx = 5; panel.add(tfMatrixDataProc, c);
        c.gridx = 6; panel.add(new JLabel("Firewall:"), c);
        tfMatrixFirewall = new JTextField(3); c.gridx = 7; panel.add(tfMatrixFirewall, c);
        row++;

        // Devices/Programs
        c.gridx = 0; c.gridy = row; c.gridwidth = 8;
        c.fill = GridBagConstraints.BOTH;
        taMatrixDevices = new JTextArea(4, 60);
        taMatrixDevices.setLineWrap(true);
        taMatrixDevices.setBorder(BorderFactory.createTitledBorder("Devices/DR and Programs (one per line)"));
        panel.add(new JScrollPane(taMatrixDevices), c);
        c.gridwidth = 1; c.fill = GridBagConstraints.NONE;
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Matrix Condition Monitor Boxes:"), c);
        tfMatrixConditionBoxes = new JTextField(5); c.gridx = 1; panel.add(tfMatrixConditionBoxes, c);

        contentPanel.add(panel);
    }
*/
/*
 * Future feature: Augmentations section
private void buildAugmentationsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Augmentations", TitledBorder.LEFT, TitledBorder.TOP));
        taAugmentations = new JTextArea(4, 60);
        taAugmentations.setLineWrap(true);
        taAugmentations.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane sp = new JScrollPane(taAugmentations);
        panel.add(new JLabel("Enter augmentations (one per line, format: Name, Rating, Notes, Essence):"), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        contentPanel.add(panel);
    }

*/
/*
 * Future feature: Vehicle section
private void buildVehicleSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Vehicle", TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        int row = 0;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Name:"), c);
        tfVehicleName = new JTextField(10); c.gridx = 1; panel.add(tfVehicleName, c);
        c.gridx = 2; panel.add(new JLabel("Handling:"), c);
        tfVehicleHandling = new JTextField(3); c.gridx = 3; panel.add(tfVehicleHandling, c);
        c.gridx = 4; panel.add(new JLabel("Acceleration:"), c);
        tfVehicleAcceleration = new JTextField(3); c.gridx = 5; panel.add(tfVehicleAcceleration, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Speed Interval:"), c);
        tfVehicleSpeedInterval = new JTextField(3); c.gridx = 1; panel.add(tfVehicleSpeedInterval, c);
        c.gridx = 2; panel.add(new JLabel("Top Speed:"), c);
        tfVehicleTopSpeed = new JTextField(3); c.gridx = 3; panel.add(tfVehicleTopSpeed, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Body:"), c);
        tfVehicleBody = new JTextField(3); c.gridx = 1; panel.add(tfVehicleBody, c);
        c.gridx = 2; panel.add(new JLabel("Armor:"), c);
        tfVehicleArmor = new JTextField(3); c.gridx = 3; panel.add(tfVehicleArmor, c);
        c.gridx = 4; panel.add(new JLabel("Pilot:"), c);
        tfVehiclePilot = new JTextField(3); c.gridx = 5; panel.add(tfVehiclePilot, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Sensor:"), c);
        tfVehicleSensor = new JTextField(3); c.gridx = 1; panel.add(tfVehicleSensor, c);
        c.gridx = 2; panel.add(new JLabel("Seats:"), c);
        tfVehicleSeats = new JTextField(3); c.gridx = 3; panel.add(tfVehicleSeats, c);
        row++;

        c.gridx = 0; c.gridy = row; c.gridwidth = 8; c.fill = GridBagConstraints.BOTH;
        taVehicleNotes = new JTextArea(3, 60);
        taVehicleNotes.setLineWrap(true);
        taVehicleNotes.setBorder(BorderFactory.createTitledBorder("Vehicle Notes:"));
        panel.add(new JScrollPane(taVehicleNotes), c);
        c.gridwidth = 1; c.fill = GridBagConstraints.NONE;
        
        contentPanel.add(panel);
    }
*/

/*
 * Gear section - to be developed later
private void buildGearSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Gear", TitledBorder.LEFT, TitledBorder.TOP));
        taGear = new JTextArea(4, 60);
        taGear.setLineWrap(true);
        taGear.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane sp = new JScrollPane(taGear);
        panel.add(new JLabel("Enter gear items (one per line, format: Item Name, Rating):"), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        contentPanel.add(panel);
    }
*/

/*
 * Future feature: Spells/Preparations/Rituals/Complex Forms section
private void buildSpellsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Spells / Rituals / Complex Forms", TitledBorder.LEFT, TitledBorder.TOP));
        taSpellsRituals = new JTextArea(4, 60);
        taSpellsRituals.setLineWrap(true);
        taSpellsRituals.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane sp = new JScrollPane(taSpellsRituals);
        panel.add(new JLabel("Enter spells/rituals/complex forms (one per line, format: Type, Name, Target/Range, Duration, Drain):"), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        contentPanel.add(panel);
    }

*/
/*
 * Future feature: Adept Powers or Other Abilities section
private void buildAdeptPowersSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Adept Powers / Other Abilities", TitledBorder.LEFT, TitledBorder.TOP));
        taAdeptPowers = new JTextArea(3, 60);
        taAdeptPowers.setLineWrap(true);
        taAdeptPowers.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane sp = new JScrollPane(taAdeptPowers);
        panel.add(new JLabel("Enter adept powers or other abilities (one per line, format: Name, Level, Notes):"), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        contentPanel.add(panel);
    }
*/

    private void runLifePathWizard() {
        wizardMetaCombo.removeAllItems();
        for (int i = 0; i < cbMetatype.getItemCount(); i++) {
            wizardMetaCombo.addItem(cbMetatype.getItemAt(i).name);
        }

        wizardSurgeCombo.removeAllItems();
        for (int i = 0; i < cbSurgeCollective.getItemCount(); i++) {
            wizardSurgeCombo.addItem(cbSurgeCollective.getItemAt(i));
        }

        wizardStatusCombo.removeAllItems();
        for (int i = 0; i < cbStatus.getItemCount(); i++) {
            wizardStatusCombo.addItem(cbStatus.getItemAt(i));
        }

        MetaItem selMeta = (MetaItem) cbMetatype.getSelectedItem();
        if (selMeta != null) wizardMetaCombo.setSelectedItem(selMeta.name); else wizardMetaCombo.setSelectedIndex(-1);
        wizardSurgeCheck.setSelected(chkSurge.isSelected());
        boolean sel = chkSurge.isSelected();
        wizardSurgeCombo.setVisible(sel);
        if (sel) {
            wizardSurgeCombo.setSelectedItem(cbSurgeCollective.getSelectedItem());
        } else {
            wizardSurgeCombo.setSelectedItem("No Collective");
        }
        wizardStatusCombo.setSelectedItem(cbStatus.getSelectedItem());
        wizardNationalityField.setText(tfNationality.getText());
        wizardLanguageField.setText("");

        wizardPanel.setVisible(true);
        frame.revalidate();
    }

    private void generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Shadowrun 6e Character Report ===\n\n");
        sb.append("-- Personal Data --\n");
        sb.append(String.format("Name: %s\n", tfName.getText()));
        sb.append(String.format("Player: %s\n", tfPlayer.getText()));
        sb.append(String.format("Role: %s   Metatype: %s   Gender: %s   Age: %s   Height (cm): %s   Weight (kg): %s\n",
                cbRole.getSelectedItem(), cbMetatype.getSelectedItem(),
                cbGender.getSelectedItem(), tfAge.getText(),
                tfHeight.getText(), tfWeight.getText()));
        sb.append(String.format("Nationality: %s   Status: %s\n",
                tfNationality.getText(), cbStatus.getSelectedItem()));

        sb.append("\n-- Attributes --\n");
        sb.append(String.format("Body: %s   Agility: %s   Reaction: %s   Strength: %s   Willpower: %s\n",
                spBody.getValue(), spAgility.getValue(), spReaction.getValue(), spStrength.getValue(), spWillpower.getValue()));
        sb.append(String.format("Logic: %s   Intuition: %s   Charisma: %s   Edge: %s   Essence: %s\n",
                spLogic.getValue(), spIntuition.getValue(), spCharisma.getValue(), spEdge.getValue(), spEssence.getValue()));
        sb.append(String.format("Magic: %s   Resonance: %s\n",
                spMagic.getValue(), spResonance.getValue()));
        // Initiative and other derived stats will be added later

        // Condition Monitor will be added in a future version

        sb.append("\n-- Skills --\n");
        StringBuilder skillsBuilder = new StringBuilder();
        for (int i = 0; i < skillsTableModel.getRowCount(); i++) {
            String type = (String) skillsTableModel.getValueAt(i, 0);
            String skill = (String) skillsTableModel.getValueAt(i, 1);
            String rank = (String) skillsTableModel.getValueAt(i, 2);
            String attribute = (String) skillsTableModel.getValueAt(i, 3);
            String category = (String) skillsTableModel.getValueAt(i, 4);
            if (skill != null && !skill.trim().isEmpty()) {
                skillsBuilder.append(String.format("%s, %s, %s, %s, %s\n",
                        type == null ? "" : type,
                        skill,
                        rank == null ? "" : rank,
                        attribute == null ? "" : attribute,
                        category == null ? "" : category));
            }
        }
        sb.append(skillsBuilder.length() == 0 ? "None\n" : skillsBuilder.toString());

        sb.append("\n-- Qualities --\n");
        StringBuilder qualBuilder = new StringBuilder();
        for (int i = 0; i < qualitiesTableModel.getRowCount(); i++) {
            String cat = (String) qualitiesTableModel.getValueAt(i, 0);
            String type = (String) qualitiesTableModel.getValueAt(i, 1);
            String q = (String) qualitiesTableModel.getValueAt(i, 2);
            String karma = (String) qualitiesTableModel.getValueAt(i, 3);
            if (q != null && !q.trim().isEmpty()) {
                qualBuilder.append(String.format("%s, %s, %s, %s\n",
                        q, type == null ? "" : type,
                        karma == null ? "" : karma,
                        cat == null ? "" : cat));
            }
        }
        sb.append(qualBuilder.length() == 0 ? "None\n" : qualBuilder.toString());

        sb.append("\n-- Contacts --\n");
        StringBuilder contactBuilder = new StringBuilder();
        for (int i = 0; i < contactsTableModel.getRowCount(); i++) {
            String name = (String) contactsTableModel.getValueAt(i, 0);
            String loyalty = (String) contactsTableModel.getValueAt(i, 1);
            String connection = (String) contactsTableModel.getValueAt(i, 2);
            if (name != null && !name.trim().isEmpty()) {
                contactBuilder.append(String.format("%s, %s, %s\n",
                        name, loyalty == null ? "" : loyalty,
                        connection == null ? "" : connection));
            }
        }
        sb.append(contactBuilder.length() == 0 ? "None\n" : contactBuilder.toString());

        sb.append("\n-- Lifestyle --\n");
        sb.append(String.format("Nuyen: %s   Primary Lifestyle: %s\n", tfNuyen.getText(), tfPrimaryLifestyle.getText()));
        sb.append(String.format("Fake IDs / Lifestyles / Funds / Licenses: %s\n", tfFakeIDs.getText()));

        // Ranged Weapons, Melee Weapons, and Armor sections will be added later
        // Matrix stats will be added later
        // Augmentations will be added later
        // Vehicle section will be added later

        // sb.append("\n-- Gear --\n");
        // sb.append(taGear.getText().isEmpty() ? "None\n" : taGear.getText() + "\n");
        // Spells and Adept Powers sections will be added later

        sb.append("\n-- Notes --\n");
        sb.append(taNotes.getText().isEmpty() ? "None\n" : taNotes.getText() + "\n");

        // Display in a dialog
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setCaretPosition(0);
        JScrollPane sp = new JScrollPane(textArea);
        sp.setPreferredSize(new Dimension(800, 600));
        JOptionPane.showMessageDialog(frame, sp, "Character Report", JOptionPane.INFORMATION_MESSAGE);

        // Save to text file
        String safeName = tfName.getText().replaceAll("\\s+", "");
        if (!safeName.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(safeName + "_CharacterReport.txt"))) {
                writer.write(sb.toString());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error saving report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        // Ensure UI uses native look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ShadowrunCharacterBuilderGUI();
            }
        });
    }
}
