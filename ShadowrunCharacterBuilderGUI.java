import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private JTextField tfName, tfPlayer, tfNotes, tfMetatype, tfEthnicity, tfAge, tfSex,
                       tfHeight, tfWeight, tfReputation, tfHeat, tfKarmaTotal, tfKarmaMisc,
                       tfNuyen, tfPrimaryLifestyle, tfPrimaryArmor, tfPrimaryRanged,
                       tfPrimaryMelee, tfFakeIDs;
    
    // ATTRIBUTES fields
    private JTextField tfBody, tfAgility, tfReaction, tfStrength, tfWillpower,
                       tfLogic, tfIntuition, tfCharisma, tfEdge, tfEssence,
                       tfMagicResonance, tfInitiative, tfMatrixInitiative, tfAstralInitiative,
                       tfJudgeIntentions, tfMemory, tfLiftCarry, tfMovement, tfUnarmedAR,
                       tfDefenseRating;
    
    // CONDITION MONITOR fields
    private JTextField tfPhysicalBoxes, tfStunBoxes;
    
    // SKILLS, QUALITIES, CONTACTS: use JTextArea to allow multiple lines
    private JTextArea taSkills, taQualities, taContacts;
    
    // WEAPONS, ARMOR: similarly multi-line
    private JTextArea taRangedWeapons, taMeleeWeapons, taArmor;
    
    // MATRIX STATS
    private JTextField tfMatrixAttack, tfMatrixSleaze, tfMatrixDataProc, tfMatrixFirewall,
                       tfMatrixConditionBoxes;
    private JTextArea taMatrixDevices;
    
    // AUGMENTATIONS
    private JTextArea taAugmentations;
    
    // VEHICLE
    private JTextField tfVehicleName, tfVehicleHandling, tfVehicleAcceleration,
                       tfVehicleSpeedInterval, tfVehicleTopSpeed, tfVehicleBody,
                       tfVehicleArmor, tfVehiclePilot, tfVehicleSensor, tfVehicleSeats;
    private JTextArea taVehicleNotes;
    
    // GEAR
    private JTextArea taGear;
    
    // SPELLS/ RITUALS/ COMPLEX FORMS
    private JTextArea taSpellsRituals;
    
    // ADEPT POWERS
    private JTextArea taAdeptPowers;
    
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
        buildConditionMonitorSection();
        buildSkillsSection();
        buildQualitiesSection();
        buildContactsSection();
        buildWeaponsArmorSection();
        buildMatrixSection();
        buildAugmentationsSection();
        buildVehicleSection();
        buildGearSection();
        buildSpellsSection();
        buildAdeptPowersSection();

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

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Notes:"), c);
        tfNotes = new JTextField(50); c.gridx = 1; c.gridwidth = 3; panel.add(tfNotes, c);
        c.gridwidth = 1; row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Metatype:"), c);
        tfMetatype = new JTextField(10); c.gridx = 1; panel.add(tfMetatype, c);
        c.gridx = 2; panel.add(new JLabel("Ethnicity:"), c);
        tfEthnicity = new JTextField(10); c.gridx = 3; panel.add(tfEthnicity, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Age:"), c);
        tfAge = new JTextField(5); c.gridx = 1; panel.add(tfAge, c);
        c.gridx = 2; panel.add(new JLabel("Sex:"), c);
        tfSex = new JTextField(5); c.gridx = 3; panel.add(tfSex, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Height:"), c);
        tfHeight = new JTextField(5); c.gridx = 1; panel.add(tfHeight, c);
        c.gridx = 2; panel.add(new JLabel("Weight:"), c);
        tfWeight = new JTextField(5); c.gridx = 3; panel.add(tfWeight, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Reputation:"), c);
        tfReputation = new JTextField(10); c.gridx = 1; panel.add(tfReputation, c);
        c.gridx = 2; panel.add(new JLabel("Heat:"), c);
        tfHeat = new JTextField(10); c.gridx = 3; panel.add(tfHeat, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Karma (Total):"), c);
        tfKarmaTotal = new JTextField(5); c.gridx = 1; panel.add(tfKarmaTotal, c);
        c.gridx = 2; panel.add(new JLabel("Karma (Misc):"), c);
        tfKarmaMisc = new JTextField(5); c.gridx = 3; panel.add(tfKarmaMisc, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Nuyen:"), c);
        tfNuyen = new JTextField(10); c.gridx = 1; panel.add(tfNuyen, c);
        c.gridx = 2; panel.add(new JLabel("Primary Lifestyle:"), c);
        tfPrimaryLifestyle = new JTextField(15); c.gridx = 3; panel.add(tfPrimaryLifestyle, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Primary Armor:"), c);
        tfPrimaryArmor = new JTextField(15); c.gridx = 1; panel.add(tfPrimaryArmor, c);
        c.gridx = 2; panel.add(new JLabel("Primary Ranged Weapon:"), c);
        tfPrimaryRanged = new JTextField(20); c.gridx = 3; panel.add(tfPrimaryRanged, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Primary Melee Weapon:"), c);
        tfPrimaryMelee = new JTextField(20); c.gridx = 1; panel.add(tfPrimaryMelee, c);
        c.gridx = 2; panel.add(new JLabel("Fake IDs / Lifestyles / Funds / Licenses:"), c);
        tfFakeIDs = new JTextField(25); c.gridx = 3; panel.add(tfFakeIDs, c);
        row++;

        contentPanel.add(panel);
    }

    private void buildAttributesSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Attributes", TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;

        int row = 0;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Body:"), c);
        tfBody = new JTextField(3); c.gridx = 1; panel.add(tfBody, c);
        c.gridx = 2; panel.add(new JLabel("Agility:"), c);
        tfAgility = new JTextField(3); c.gridx = 3; panel.add(tfAgility, c);
        c.gridx = 4; panel.add(new JLabel("Reaction:"), c);
        tfReaction = new JTextField(3); c.gridx = 5; panel.add(tfReaction, c);
        c.gridx = 6; panel.add(new JLabel("Strength:"), c);
        tfStrength = new JTextField(3); c.gridx = 7; panel.add(tfStrength, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Willpower:"), c);
        tfWillpower = new JTextField(3); c.gridx = 1; panel.add(tfWillpower, c);
        c.gridx = 2; panel.add(new JLabel("Logic:"), c);
        tfLogic = new JTextField(3); c.gridx = 3; panel.add(tfLogic, c);
        c.gridx = 4; panel.add(new JLabel("Intuition:"), c);
        tfIntuition = new JTextField(3); c.gridx = 5; panel.add(tfIntuition, c);
        c.gridx = 6; panel.add(new JLabel("Charisma:"), c);
        tfCharisma = new JTextField(3); c.gridx = 7; panel.add(tfCharisma, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Edge:"), c);
        tfEdge = new JTextField(3); c.gridx = 1; panel.add(tfEdge, c);
        c.gridx = 2; panel.add(new JLabel("Essence:"), c);
        tfEssence = new JTextField(3); c.gridx = 3; panel.add(tfEssence, c);
        c.gridx = 4; panel.add(new JLabel("Magic/Resonance:"), c);
        tfMagicResonance = new JTextField(3); c.gridx = 5; panel.add(tfMagicResonance, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Initiative:"), c);
        tfInitiative = new JTextField(3); c.gridx = 1; panel.add(tfInitiative, c);
        c.gridx = 2; panel.add(new JLabel("Matrix Initiative:"), c);
        tfMatrixInitiative = new JTextField(3); c.gridx = 3; panel.add(tfMatrixInitiative, c);
        c.gridx = 4; panel.add(new JLabel("Astral Initiative:"), c);
        tfAstralInitiative = new JTextField(3); c.gridx = 5; panel.add(tfAstralInitiative, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Judge Intentions:"), c);
        tfJudgeIntentions = new JTextField(3); c.gridx = 1; panel.add(tfJudgeIntentions, c);
        c.gridx = 2; panel.add(new JLabel("Memory:"), c);
        tfMemory = new JTextField(3); c.gridx = 3; panel.add(tfMemory, c);
        c.gridx = 4; panel.add(new JLabel("Lift/Carry:"), c);
        tfLiftCarry = new JTextField(5); c.gridx = 5; panel.add(tfLiftCarry, c);
        row++;

        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Movement:"), c);
        tfMovement = new JTextField(3); c.gridx = 1; panel.add(tfMovement, c);
        c.gridx = 2; panel.add(new JLabel("Unarmed AR:"), c);
        tfUnarmedAR = new JTextField(3); c.gridx = 3; panel.add(tfUnarmedAR, c);
        c.gridx = 4; panel.add(new JLabel("Defense Rating:"), c);
        tfDefenseRating = new JTextField(3); c.gridx = 5; panel.add(tfDefenseRating, c);
        row++;

        contentPanel.add(panel);
    }

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

    private void buildSkillsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Skills", TitledBorder.LEFT, TitledBorder.TOP));
        taSkills = new JTextArea(5, 60);
        taSkills.setLineWrap(true);
        taSkills.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane sp = new JScrollPane(taSkills);
        panel.add(new JLabel("Enter skills (one per line, format: Skill, Rank, Attribute, Type):"), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        contentPanel.add(panel);
    }

    private void buildQualitiesSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Qualities", TitledBorder.LEFT, TitledBorder.TOP));
        taQualities = new JTextArea(4, 60);
        taQualities.setLineWrap(true);
        taQualities.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane sp = new JScrollPane(taQualities);
        panel.add(new JLabel("Enter qualities (one per line, format: Quality, Notes, Type):"), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        contentPanel.add(panel);
    }

    private void buildContactsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Contacts", TitledBorder.LEFT, TitledBorder.TOP));
        taContacts = new JTextArea(4, 60);
        taContacts.setLineWrap(true);
        taContacts.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane sp = new JScrollPane(taContacts);
        panel.add(new JLabel("Enter contacts (one per line, format: Name, Loyalty, Connection):"), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        contentPanel.add(panel);
    }

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

    private void generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Shadowrun 6e Character Report ===\n\n");
        sb.append("-- Personal Data --\n");
        sb.append(String.format("Name: %s\n", tfName.getText()));
        sb.append(String.format("Player: %s\n", tfPlayer.getText()));
        sb.append(String.format("Notes: %s\n", tfNotes.getText()));
        sb.append(String.format("Metatype: %s   Ethnicity: %s   Age: %s   Sex: %s   Height: %s   Weight: %s\n", tfMetatype.getText(), tfEthnicity.getText(), tfAge.getText(), tfSex.getText(), tfHeight.getText(), tfWeight.getText()));
        sb.append(String.format("Reputation: %s   Heat: %s\n", tfReputation.getText(), tfHeat.getText()));
        sb.append(String.format("Karma (Total/Misc): %s / %s\n", tfKarmaTotal.getText(), tfKarmaMisc.getText()));
        sb.append(String.format("Nuyen: %s   Primary Lifestyle: %s\n", tfNuyen.getText(), tfPrimaryLifestyle.getText()));
        sb.append(String.format("Primary Armor: %s   Primary Ranged Weapon: %s   Primary Melee Weapon: %s\n", tfPrimaryArmor.getText(), tfPrimaryRanged.getText(), tfPrimaryMelee.getText()));
        sb.append(String.format("Fake IDs / Lifestyles / Funds / Licenses: %s\n", tfFakeIDs.getText()));

        sb.append("\n-- Attributes --\n");
        sb.append(String.format("Body: %s   Agility: %s   Reaction: %s   Strength: %s   Willpower: %s\n", tfBody.getText(), tfAgility.getText(), tfReaction.getText(), tfStrength.getText(), tfWillpower.getText()));
        sb.append(String.format("Logic: %s   Intuition: %s   Charisma: %s   Edge: %s   Essence: %s\n", tfLogic.getText(), tfIntuition.getText(), tfCharisma.getText(), tfEdge.getText(), tfEssence.getText()));
        sb.append(String.format("Magic/Resonance: %s   Initiative: %s   Matrix Init: %s   Astral Init: %s\n", tfMagicResonance.getText(), tfInitiative.getText(), tfMatrixInitiative.getText(), tfAstralInitiative.getText()));
        sb.append(String.format("Judge Intentions: %s   Memory: %s   Lift/Carry: %s   Movement: %s   Unarmed AR: %s   Defense Rating: %s\n", tfJudgeIntentions.getText(), tfMemory.getText(), tfLiftCarry.getText(), tfMovement.getText(), tfUnarmedAR.getText(), tfDefenseRating.getText()));

        sb.append("\n-- Condition Monitor --\n");
        sb.append(String.format("Physical Damage Track Boxes: %s   Stun Damage Track Boxes: %s\n", tfPhysicalBoxes.getText(), tfStunBoxes.getText()));

        sb.append("\n-- Skills --\n");
        sb.append(taSkills.getText().isEmpty() ? "None\n" : taSkills.getText() + "\n");

        sb.append("\n-- Qualities --\n");
        sb.append(taQualities.getText().isEmpty() ? "None\n" : taQualities.getText() + "\n");

        sb.append("\n-- Contacts --\n");
        sb.append(taContacts.getText().isEmpty() ? "None\n" : taContacts.getText() + "\n");

        sb.append("\n-- Ranged Weapons --\n");
        sb.append(taRangedWeapons.getText().isEmpty() ? "None\n" : taRangedWeapons.getText() + "\n");

        sb.append("\n-- Melee Weapons --\n");
        sb.append(taMeleeWeapons.getText().isEmpty() ? "None\n" : taMeleeWeapons.getText() + "\n");

        sb.append("\n-- Armor --\n");
        sb.append(taArmor.getText().isEmpty() ? "None\n" : taArmor.getText() + "\n");

        sb.append("\n-- Matrix Stats --\n");
        sb.append(String.format("Attack: %s   Sleaze: %s   Data Proc: %s   Firewall: %s\n", tfMatrixAttack.getText(), tfMatrixSleaze.getText(), tfMatrixDataProc.getText(), tfMatrixFirewall.getText()));
        sb.append("Devices/DR and Programs:\n");
        sb.append(taMatrixDevices.getText().isEmpty() ? "None\n" : taMatrixDevices.getText() + "\n");
        sb.append(String.format("Matrix Condition Monitor Boxes: %s\n", tfMatrixConditionBoxes.getText()));

        sb.append("\n-- Augmentations --\n");
        sb.append(taAugmentations.getText().isEmpty() ? "None\n" : taAugmentations.getText() + "\n");

        sb.append("\n-- Vehicle --\n");
        sb.append(String.format("Name: %s   Handling: %s   Acceleration: %s   Speed Interval: %s   Top Speed: %s\n", tfVehicleName.getText(), tfVehicleHandling.getText(), tfVehicleAcceleration.getText(), tfVehicleSpeedInterval.getText(), tfVehicleTopSpeed.getText()));
        sb.append(String.format("Body: %s   Armor: %s   Pilot: %s   Sensor: %s   Seats: %s\n", tfVehicleBody.getText(), tfVehicleArmor.getText(), tfVehiclePilot.getText(), tfVehicleSensor.getText(), tfVehicleSeats.getText()));
        sb.append(String.format("Notes: %s\n", taVehicleNotes.getText()));

        sb.append("\n-- Gear --\n");
        sb.append(taGear.getText().isEmpty() ? "None\n" : taGear.getText() + "\n");

        sb.append("\n-- Spells / Rituals / Complex Forms --\n");
        sb.append(taSpellsRituals.getText().isEmpty() ? "None\n" : taSpellsRituals.getText() + "\n");

        sb.append("\n-- Adept Powers or Other Abilities --\n");
        sb.append(taAdeptPowers.getText().isEmpty() ? "None\n" : taAdeptPowers.getText() + "\n");

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
