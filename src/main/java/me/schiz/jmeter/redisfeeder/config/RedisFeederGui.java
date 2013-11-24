package me.schiz.jmeter.redisfeeder.config;

import org.apache.jmeter.config.gui.AbstractConfigGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import javax.swing.*;
import java.awt.*;

public class RedisFeederGui extends AbstractConfigGui {
	private static Logger log = LoggingManager.getLoggerForClass();
	private JPanel          jpGeneralPanel;
	private JTextField      tfName;
	private JTextField      tfComments;

	private JTextField      tfInstanceName;
	private JTextField		tfHost;
	private JTextField		tfPort;

	private String DEFAULT_NAME = "Redis Feeder";
	private String DEFAULT_COMMENT = "Developed by Epikhin Mikhail https://github.com/sch1z0phren1a/jmeter-redisfeeder";

	public RedisFeederGui() {
		super();
		init();
		initFields();
	}

	public String getStaticLabel() {
		return DEFAULT_NAME;
	}

	public String getStaticLabelResource() {
		return getStaticLabel();
	}

	@Override
	public String getLabelResource() {
		return this.getClass().getSimpleName();
	}

	@Override
	public TestElement createTestElement() {
		TestElement redisFeeder = new RedisFeeder();
		modifyTestElement(redisFeeder);
		return redisFeeder;
	}

	@Override
	public void modifyTestElement(TestElement testElement) {
		if(testElement == null)	log.error("testElement is null");
		super.configureTestElement(testElement);
		if(testElement instanceof RedisFeeder) {
			RedisFeeder redisFeeder = (RedisFeeder)testElement;
			redisFeeder.setInstanceName(tfInstanceName.getText().trim());
			redisFeeder.setHost(tfHost.getText().trim());
			try{
				redisFeeder.setPort(Integer.parseInt(tfPort.getText().trim()));
			} catch(NumberFormatException nfe) {
				redisFeeder.setPort(RedisFeeder.DEFAULT_PORT);
			}
		}
	}

	public void configure(TestElement testElement) {
		super.configure(testElement);

		if(testElement instanceof RedisFeeder) {
			RedisFeeder redisFeeder =(RedisFeeder)testElement;
			tfName.setText(redisFeeder.getName());
			tfComments.setText(redisFeeder.getComment());

			tfInstanceName.setText(redisFeeder.getInstanceName());
			tfHost.setText(redisFeeder.getHost());
			tfPort.setText(String.valueOf(redisFeeder.getPort()));
		}
	}

	private void init() {
		setLayout(new BorderLayout(0, 5));
		setBorder(makeBorder());

		JPanel mainPanel = new JPanel(new GridBagLayout());


		jpGeneralPanel = new JPanel(new GridBagLayout());
		jpGeneralPanel.setAlignmentX(0);
		jpGeneralPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(),
				""));

		GridBagConstraints labelConstraints = new GridBagConstraints();
		labelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;

		GridBagConstraints editConstraints = new GridBagConstraints();
		editConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		editConstraints.weightx = 1.0;
		editConstraints.fill = GridBagConstraints.HORIZONTAL;

		editConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
		labelConstraints.insets = new java.awt.Insets(2, 0, 0, 0);

		JPanel jpHeaderPanel = new JPanel(new GridBagLayout());
		addToPanel(jpHeaderPanel, labelConstraints, 0, 0, new JLabel("Name: ", JLabel.LEFT));
		addToPanel(jpHeaderPanel, editConstraints, 1, 0, tfName = new JTextField(20));
		addToPanel(jpHeaderPanel, labelConstraints, 0, 1, new JLabel("Comments: ", JLabel.LEFT));
		addToPanel(jpHeaderPanel, editConstraints, 1, 1, tfComments = new JTextField(20));

		addToPanel(jpGeneralPanel, labelConstraints, 0, 0, new JLabel("Instance: ", JLabel.RIGHT));
		addToPanel(jpGeneralPanel, editConstraints, 1, 0, tfInstanceName = new JTextField(16));
		addToPanel(jpGeneralPanel, labelConstraints, 0, 1, new JLabel("Host: ", JLabel.RIGHT));
		addToPanel(jpGeneralPanel, editConstraints, 1, 1, tfHost = new JTextField(24));
		addToPanel(jpGeneralPanel, labelConstraints, 0, 2, new JLabel("Port: ", JLabel.RIGHT));
		addToPanel(jpGeneralPanel, editConstraints, 1, 2, tfPort = new JTextField(8));

		// Compilation panels
		addToPanel(mainPanel, editConstraints, 0, 0, jpHeaderPanel);
		addToPanel(mainPanel, editConstraints, 0, 1, jpGeneralPanel);

		JPanel container = new JPanel(new BorderLayout());
		container.add(makeTitlePanel(), BorderLayout.NORTH);
		container.add(mainPanel, BorderLayout.NORTH);
		add(container, BorderLayout.CENTER);
	}

	private void initFields() {
		tfName.setText(DEFAULT_NAME);
		tfComments.setText(DEFAULT_COMMENT);

		tfInstanceName.setText(RedisFeeder.DEFAULT_INSTANCE_NAME);
		tfHost.setText(RedisFeeder.DEFAULT_HOST);
		tfPort.setText(String.valueOf(RedisFeeder.DEFAULT_PORT));
	}

	private void addToPanel(JPanel panel, GridBagConstraints constraints, int col, int row, JComponent component) {
		constraints.gridx = col;
		constraints.gridy = row;
		panel.add(component, constraints);
	}
}