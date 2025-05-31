import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
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
    
    // PERSONAL DATA fields
    private JTextField tfName, tfPlayer, tfMetatype, tfEthnicity, tfAge,
                       tfHeight, tfWeight;
    private JComboBox<String> cbGender;
    private JTextField tfNuyen, tfPrimaryLifestyle, tfFakeIDs;
    private JTextArea taNotes;
    
    // ATTRIBUTES fields
    private JSpinner spBody, spAgility, spReaction, spStrength,
                     spWillpower, spLogic, spIntuition, spCharisma,
                     spEdge, spEssence, spMagic, spResonance;
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
    
    public ShadowrunCharacterBuilderGUI() {
        frame = new JFrame("Shadowrun 6e Character Builder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 1200);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Build each section
        buildPersonalDataSection();
        buildAttributesSection();
        // buildConditionMonitorSection(); // TODO expand later
        buildSkillsSection();
        buildQualitiesSection();
        buildContactsSection();
        buildLifestyleSection();
        // buildWeaponsArmorSection(); // TODO expand later
        // buildMatrixSection(); // TODO expand later
        // buildAugmentationsSection(); // TODO expand later
        // buildVehicleSection(); // TODO expand later
        // buildGearSection(); // TODO develop later
        // buildSpellsSection(); // TODO expand later
        // buildAdeptPowersSection(); // TODO expand later
        buildNotesSection();

        // Generate Button
        JButton btnGenerate = new JButton("Generate Report");
        btnGenerate.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGenerate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(btnGenerate);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        scrollPane = new JScrollPane(contentPanel);
        frame.getContentPane().add(scrollPane);
        frame.setVisible(true);
    }

    private void buildPersonalDataSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Personal Data", TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;

        int row = 0;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Character Name/Primary Alias:"), c);
        tfName = new JTextField(20); c.gridx = 1; panel.add(tfName, c);

        c.gridx = 2; panel.add(new JLabel("Player Name:"), c);
        tfPlayer = new JTextField(15); c.gridx = 3; panel.add(tfPlayer, c);
        row++;


        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Metatype:"), c);
        tfMetatype = new JTextField(10); c.gridx = 1; panel.add(tfMetatype, c);
        c.gridx = 2; panel.add(new JLabel("Ethnicity:"), c);
        tfEthnicity = new JTextField(10); c.gridx = 3; panel.add(tfEthnicity, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Age:"), c);
        tfAge = new JTextField(5); c.gridx = 1; panel.add(tfAge, c);
        c.gridx = 2; panel.add(new JLabel("Gender:"), c);
        cbGender = new JComboBox<>(new String[]{"Male", "Female"}); c.gridx = 3; panel.add(cbGender, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Height:"), c);
        tfHeight = new JTextField(5); c.gridx = 1; panel.add(tfHeight, c);
        c.gridx = 2; panel.add(new JLabel("Weight:"), c);
        tfWeight = new JTextField(5); c.gridx = 3; panel.add(tfWeight, c);
        row++;

        // Reputation, Heat, Karma, and primary weapons/armor will be added in future versions

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
        spBody = new JSpinner(new SpinnerNumberModel(1, 0, 10, 1)); pc.gridx = 1; physical.add(spBody, pc); prow++;
        pc.gridx = 0; pc.gridy = prow; physical.add(new JLabel("Agility:"), pc);
        spAgility = new JSpinner(new SpinnerNumberModel(1, 0, 10, 1)); pc.gridx = 1; physical.add(spAgility, pc); prow++;
        pc.gridx = 0; pc.gridy = prow; physical.add(new JLabel("Reaction:"), pc);
        spReaction = new JSpinner(new SpinnerNumberModel(1, 0, 10, 1)); pc.gridx = 1; physical.add(spReaction, pc); prow++;
        pc.gridx = 0; pc.gridy = prow; physical.add(new JLabel("Strength:"), pc);
        spStrength = new JSpinner(new SpinnerNumberModel(1, 0, 10, 1)); pc.gridx = 1; physical.add(spStrength, pc);

        JPanel mental = new JPanel(new GridBagLayout());
        mental.setBorder(BorderFactory.createTitledBorder("Mental"));
        GridBagConstraints mc = new GridBagConstraints();
        mc.insets = new Insets(2,2,2,2);
        mc.anchor = GridBagConstraints.WEST;
        int mrow = 0;
        mc.gridx = 0; mc.gridy = mrow; mental.add(new JLabel("Willpower:"), mc);
        spWillpower = new JSpinner(new SpinnerNumberModel(1, 0, 10, 1)); mc.gridx = 1; mental.add(spWillpower, mc); mrow++;
        mc.gridx = 0; mc.gridy = mrow; mental.add(new JLabel("Logic:"), mc);
        spLogic = new JSpinner(new SpinnerNumberModel(1, 0, 10, 1)); mc.gridx = 1; mental.add(spLogic, mc); mrow++;
        mc.gridx = 0; mc.gridy = mrow; mental.add(new JLabel("Intuition:"), mc);
        spIntuition = new JSpinner(new SpinnerNumberModel(1, 0, 10, 1)); mc.gridx = 1; mental.add(spIntuition, mc); mrow++;
        mc.gridx = 0; mc.gridy = mrow; mental.add(new JLabel("Charisma:"), mc);
        spCharisma = new JSpinner(new SpinnerNumberModel(1, 0, 10, 1)); mc.gridx = 1; mental.add(spCharisma, mc);

        JPanel special = new JPanel(new GridBagLayout());
        special.setBorder(BorderFactory.createTitledBorder("Special"));
        GridBagConstraints sc = new GridBagConstraints();
        sc.insets = new Insets(2,2,2,2);
        sc.anchor = GridBagConstraints.WEST;
        int srow = 0;
        sc.gridx = 0; sc.gridy = srow; special.add(new JLabel("Edge:"), sc);
        spEdge = new JSpinner(new SpinnerNumberModel(1, 0, 10, 1)); sc.gridx = 1; special.add(spEdge, sc); srow++;
        sc.gridx = 0; sc.gridy = srow; special.add(new JLabel("Essence:"), sc);
        spEssence = new JSpinner(new SpinnerNumberModel(6, 0, 6, 1)); sc.gridx = 1; special.add(spEssence, sc); srow++;
        sc.gridx = 0; sc.gridy = srow; special.add(new JLabel("Magic:"), sc);
        spMagic = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); sc.gridx = 1; special.add(spMagic, sc); srow++;
        sc.gridx = 0; sc.gridy = srow; special.add(new JLabel("Resonance:"), sc);
        spResonance = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); sc.gridx = 1; special.add(spResonance, sc);

        c.gridx = 0; c.gridy = 0; panel.add(physical, c);
        c.gridx = 1; panel.add(mental, c);
        c.gridx = 2; panel.add(special, c);

        contentPanel.add(panel);
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

    private void buildSkillsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Skills", TitledBorder.LEFT, TitledBorder.TOP));

        skillsTableModel = new DefaultTableModel(new Object[]{"Skill", "Rank", "Attribute", "Type"}, 0);
        tableSkills = new JTable(skillsTableModel);
        JScrollPane sp = new JScrollPane(tableSkills);

        JButton btnAddSkill = new JButton("Add Skill");
        btnAddSkill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                skillsTableModel.addRow(new Object[]{"", "", "", ""});
                updateSkillCount();
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
        lblSkillCount = new JLabel("0 skills");
        JPanel buttonSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonSub.add(btnAddSkill);
        buttonSub.add(btnRemoveSkill);
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.add(buttonSub, BorderLayout.WEST);
        btnPanel.add(lblSkillCount, BorderLayout.EAST);

        panel.add(new JLabel("Enter skills:"), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        updateSkillCount();
        contentPanel.add(panel);
    }

    private void buildQualitiesSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Qualities", TitledBorder.LEFT, TitledBorder.TOP));

        qualitiesTableModel = new DefaultTableModel(new Object[]{"Quality", "Type", "Karma", "Category"}, 0);
        tableQualities = new JTable(qualitiesTableModel);
        JScrollPane sp = new JScrollPane(tableQualities);

        JButton btnAddQuality = new JButton("Add Quality");
        btnAddQuality.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                qualitiesTableModel.addRow(new Object[]{"", "Positive", "", ""});
                updateQualityCount();
            }
        });
        JButton btnRemoveQuality = new JButton("Remove Quality");
        btnRemoveQuality.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = tableQualities.getSelectedRow();
                if (row != -1) {
                    qualitiesTableModel.removeRow(row);
                    updateQualityCount();
                }
            }
        });
        lblQualityCount = new JLabel("0 qualities");
        JPanel buttonSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonSub.add(btnAddQuality);
        buttonSub.add(btnRemoveQuality);
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.add(buttonSub, BorderLayout.WEST);
        btnPanel.add(lblQualityCount, BorderLayout.EAST);

        panel.add(new JLabel("Enter qualities:"), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        updateQualityCount();
        contentPanel.add(panel);
    }

    private void buildContactsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Contacts", TitledBorder.LEFT, TitledBorder.TOP));

        contactsTableModel = new DefaultTableModel(new Object[]{"Name", "Loyalty", "Connection"}, 0);
        tableContacts = new JTable(contactsTableModel);
        JScrollPane sp = new JScrollPane(tableContacts);

        JButton btnAddContact = new JButton("Add Contact");
        btnAddContact.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                contactsTableModel.addRow(new Object[]{"", "", ""});
            }
        });
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(btnAddContact);

        panel.add(new JLabel("Enter contacts:"), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        contentPanel.add(panel);
    }

    private void buildLifestyleSection() {
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

        contentPanel.add(panel);
    }

    private void buildNotesSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Notes", TitledBorder.LEFT, TitledBorder.TOP));
        taNotes = new JTextArea(4, 60);
        taNotes.setLineWrap(true);
        taNotes.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(new JScrollPane(taNotes), BorderLayout.CENTER);
        contentPanel.add(panel);
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

    private void generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Shadowrun 6e Character Report ===\n\n");
        sb.append("-- Personal Data --\n");
        sb.append(String.format("Name: %s\n", tfName.getText()));
        sb.append(String.format("Player: %s\n", tfPlayer.getText()));
        sb.append(String.format("Metatype: %s   Ethnicity: %s   Age: %s   Gender: %s   Height: %s   Weight: %s\n",
                tfMetatype.getText(), tfEthnicity.getText(), tfAge.getText(),
                cbGender.getSelectedItem(), tfHeight.getText(), tfWeight.getText()));

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
            String skill = (String) skillsTableModel.getValueAt(i, 0);
            String rank = (String) skillsTableModel.getValueAt(i, 1);
            String attribute = (String) skillsTableModel.getValueAt(i, 2);
            String type = (String) skillsTableModel.getValueAt(i, 3);
            if (skill != null && !skill.trim().isEmpty()) {
                skillsBuilder.append(String.format("%s, %s, %s, %s\n",
                        skill, rank == null ? "" : rank,
                        attribute == null ? "" : attribute,
                        type == null ? "" : type));
            }
        }
        sb.append(skillsBuilder.length() == 0 ? "None\n" : skillsBuilder.toString());

        sb.append("\n-- Qualities --\n");
        StringBuilder qualBuilder = new StringBuilder();
        for (int i = 0; i < qualitiesTableModel.getRowCount(); i++) {
            String q = (String) qualitiesTableModel.getValueAt(i, 0);
            String type = (String) qualitiesTableModel.getValueAt(i, 1);
            String karma = (String) qualitiesTableModel.getValueAt(i, 2);
            String cat = (String) qualitiesTableModel.getValueAt(i, 3);
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
