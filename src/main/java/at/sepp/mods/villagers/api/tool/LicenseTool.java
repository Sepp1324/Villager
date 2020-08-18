package at.sepp.mods.villagers.api.tool;

import at.sepp.mods.villagers.api.license.License;
import at.sepp.mods.villagers.api.license.SignatureUtil;
import at.sepp.mods.villagers.api.util.ByteHex;
import at.sepp.mods.villagers.api.util.DateParser;
import at.sepp.mods.villagers.api.util.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class LicenseTool extends JFrame {
    private static final String KEY_FILE = "dsakey.cfg";

    private static final String FEATURE_FILE = "feature.txt";

    private static final String DATE_FORMAT = "YYYY-MM-DD";

    private static final long EXPIRATION_TIME = 2592000000L;

    private JButton create;

    private JButton clear;

    private JButton quit;

    private JTextField[] featureField;

    private JTextArea result;

    private byte[] key;

    private License lic;

    private String[] names;

    private Properties feature;

    private WindowListener wl;

    public LicenseTool() {
        super("License Manager");
        this.wl = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                LicenseTool.this.dispose();
                System.exit(0);
            }
        };
        this.feature = new Properties();
        List nameList = new ArrayList();
        try {
            (new FileUtil("feature.txt")).read(nameList, this.feature);
        } catch (Exception e) {
            throw new RuntimeException("Can not load the feature list file: feature.txt\nPlease make sure it is in the CLASSPATH");
        }
        this.names = (String[])nameList.toArray((Object[])new String[0]);
        Properties prop = new Properties();
        try {
            prop.load(ClassLoader.getSystemResourceAsStream("dsakey.cfg"));
        } catch (Exception e) {
            throw new RuntimeException("Can not load the dsa key file: dsakey.cfg\nPlease make sure it is in the CLASSPATH");
        }
        String privateKey = prop.getProperty("private-key");
        this.key = ByteHex.convert(privateKey);
        this.lic = License.newLicense();
        setBackground(Color.lightGray);
        int width = 300;
        int offset = 500;
        if (this.names.length > 3) {
            width = 600;
            offset = 300;
        }
        if (this.names.length > 7) {
            width = 1000;
            offset = 100;
        }
        setSize(width, 400);
        setLocation(offset, 50);
        setupGUI();
    }

    public void create() throws Exception {
        String signature = SignatureUtil.sign(this.lic.format(), this.key);
        this.lic.setSignature(signature);
        this.lic.create();
    }

    private void setupGUI() {
        Container container = getContentPane();
        this.create = new JButton("Create");
        this.clear = new JButton("Clear");
        this.quit = new JButton("Close");
        this.result = new JTextArea();
        this.result.setText("After click on Create, it might take a few moments to create a license.\nThe result will be shown here ....");
        this.result.setEditable(false);
        this.featureField = new JTextField[this.names.length + 1];
        for (int i = 0; i < this.names.length; i++) {
            this.featureField[i] = new JTextField(14);
            String value = this.feature.getProperty(this.names[i]);
            if (value != null)
                this.featureField[i].setText(value);
        }
        this.featureField[this.names.length] = new JTextField(14);
        JPanel content = new JPanel();
        content.setLayout(new GridLayout(4, 1));
        JPanel p = null;
        for (int j = 0; j < this.names.length; j++) {
            p = new JPanel();
            p.add(new JLabel(this.names[j] + "    "));
            p.add(this.featureField[j]);
            content.add(p);
        }
        p = new JPanel();
        p.add(new JLabel("Expiration  "));
        p.add(this.featureField[this.names.length]);
        Date date = new Date(System.currentTimeMillis() + 2592000000L);
        DateParser d = new DateParser(date);
        StringBuffer buf = new StringBuffer(10);
        buf.append(d.getYear()).append('-').append(d.getMonth()).append('-').append(d.getDayOfMonth());
        this.featureField[this.names.length].setText(buf.toString());
        content.add(p);
        JPanel button = new JPanel();
        button.add(this.create);
        button.add(this.clear);
        button.add(this.quit);
        container.add(new JScrollPane(this.result), "Center");
        container.add(button, "South");
        container.add(content, "North");
        ActionListener listener = new ButtonEvent();
        this.create.addActionListener(listener);
        this.quit.addActionListener(listener);
        this.clear.addActionListener(listener);
        addWindowListener(this.wl);
    }

    private class ButtonEvent implements ActionListener {
        private ButtonEvent() {}

        public void actionPerformed(ActionEvent e) {
            Object s = e.getSource();
            if (s == LicenseTool.this.create) {
                for (int i = 0; i < LicenseTool.this.names.length; i++)
                    LicenseTool.this.lic.setFeature(LicenseTool.this.names[i], LicenseTool.this.featureField[i].getText());
                String d = LicenseTool.this.featureField[LicenseTool.this.names.length].getText();
                if (d == null || d.trim().length() == 0) {
                    LicenseTool.this.lic.setExpiration("never");
                } else if (LicenseTool.this.validDate(d)) {
                    LicenseTool.this.lic.setExpiration(d);
                } else {
                    LicenseTool.this.featureField[LicenseTool.this.names.length].setText("");
                    LicenseTool.this.result.setText("Invalid date format, please enter\nit as YYYY-MM-DD");
                    return;
                }
                try {
                    LicenseTool.this.create();
                    LicenseTool.this.result.setText("License is created successfully\nand is written to the file license.lic");
                } catch (Exception ex) {
                    LicenseTool.this.result.setText(ex.getMessage());
                }
            } else if (s == LicenseTool.this.clear) {
                for (int i = 0; i < LicenseTool.this.names.length; i++) {
                    String value = LicenseTool.this.feature.getProperty(LicenseTool.this.names[i]);
                    if (value != null)
                        LicenseTool.this.featureField[i].setText(value);
                }
                LicenseTool.this.featureField[LicenseTool.this.names.length].setText("");
                LicenseTool.this.result.setText("");
            } else if (s == LicenseTool.this.quit) {
                LicenseTool.this.dispose();
                System.exit(0);
            }
        }
    }

    private boolean validDate(String d) {
        Date date = DateParser.toUtilDate(d);
        if (date == null)
            return false;
        return true;
    }

    public static void main(String[] args) {
        LicenseTool manager = new LicenseTool();
        manager.setVisible(true);
    }
}