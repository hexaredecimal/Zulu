package com.zulu.monty;

import com.zulu.runtime.ZuluAtom;
import com.zulu.runtime.ZuluNumber;
import com.zulu.runtime.ZuluString;
import com.zulu.runtime.Modules;
import com.zulu.runtime.ZuluFunction;
import com.zulu.runtime.ZuluReference;
import com.zulu.utils.Arguments;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import com.zulu.runtime.ZuluValue;

public class Monty {

	private static JFrame frame;
	private static LayoutManager layoutManager = new BorderLayout();
	private static int progressID = 0;
	private static ArrayList<JProgressBar> bars = new ArrayList<>();

	// Layout functions
	public static ZuluValue BorderLayout(ZuluValue... args) {
		layoutManager = new BorderLayout();
		frame.setLayout(layoutManager);
		return new ZuluReference(frame);
	}

	public static ZuluValue FlowLayout(ZuluValue... args) {
		layoutManager = new FlowLayout(args.length == 1 ? args[0].asInteger().intValue() : SwingConstants.LEADING);
		frame.setLayout(layoutManager);
		return new ZuluReference(frame);
	}

	public static ZuluValue GridLayout(ZuluValue... args) {
		Arguments.check(2, args.length);
		layoutManager = new GridLayout(args[0].asInteger().intValue(), args[1].asInteger().intValue());
		frame.setLayout(layoutManager);
		return new ZuluReference(frame);
	}

	public static ZuluValue Window(ZuluValue... args) {
		Arguments.checkOrOr(0, 1, args.length);
		String tittle = (args.length == 1) ? args[0].toString() : "";
		frame = new JFrame(tittle);
		frame.setLayout(null);
		ImageIcon icon = new ImageIcon("monty/monty_icon.jpg");
		frame.setIconImage(icon.getImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return new ZuluReference(frame);
	}

	public static ZuluValue SetVisible(ZuluValue... args) {
		Arguments.check(1, args.length);
		frame.setVisible(args[0].toString().equals("true"));
		return new ZuluReference(frame);
	}

	public static ZuluValue Panel(ZuluValue... args) {
		Arguments.checkOrOr(0, 1, args.length);
		final JPanel panel = new JPanel();
		panel.setLayout(layoutManager);
		frame.add(panel);
		return new ZuluReference(frame);
	}

	public static ZuluValue Text(ZuluValue... args) {
		Arguments.check(6, args.length);
		final JLabel text = new JLabel(args[0].toString());
		text.setForeground((Color) ((ZuluReference) args[5]).getRef());
		text.setBounds(args[1].asInteger().intValue(), args[2].asInteger().intValue(), args[3].asInteger().intValue(), args[4].asInteger().intValue());
		frame.add(text);
		return new ZuluReference(frame);
	}

	public static ZuluValue SetSize(ZuluValue... args) {
		Arguments.check(2, args.length);
		frame.setSize(new Dimension(args[0].asInteger().intValue(), args[1].asInteger().intValue()));
		return new ZuluReference(frame);
	}

	public static ZuluValue SetResizable(ZuluValue... args) {
		Arguments.check(1, args.length);
		frame.setResizable(args[0].toString().equals("true"));
		return new ZuluReference(frame);
	}

	public static ZuluValue Center(ZuluValue... args) {
		Arguments.check(0, args.length);
		frame.setLocationRelativeTo(null);
		return new ZuluReference(frame);
	}

	public static ZuluValue Button(ZuluValue... args) {
		Arguments.check(5, args.length);
		final JButton button = new JButton(args[0].toString());
		button.setBounds(args[1].asInteger().intValue(), args[2].asInteger().intValue(), args[3].asInteger().intValue(), args[4].asInteger().intValue());
		frame.add(button);
		return new ZuluReference(frame);
	}

	public static ZuluValue ActionButton(ZuluValue... args) {
		Arguments.check(6, args.length);
		final JButton button = new JButton(args[0].toString());
		button.setBounds(args[1].asInteger().intValue(), args[2].asInteger().intValue(), args[3].asInteger().intValue(), args[4].asInteger().intValue());
		button.addActionListener((e) -> {
			((ZuluFunction) args[5]).execute();
		});
		frame.add(button);
		return new ZuluReference(frame);
	}

	public static ZuluValue Slider(ZuluValue... args) {
		Arguments.check(8, args.length);
		final JSlider slider = new JSlider(args[0].asInteger().intValue(), args[1].asInteger().intValue(), args[2].asInteger().intValue());
		slider.setBounds(args[3].asInteger().intValue(), args[4].asInteger().intValue(), args[5].asInteger().intValue(), args[6].asInteger().intValue());
		slider.addChangeListener(e -> {
			JSlider source = (JSlider) e.getSource();
			((ZuluFunction) args[7]).execute(new ZuluNumber(source.getValue()));
		});
		frame.add(slider);
		return new ZuluReference(frame);
	}

	public static ZuluValue Pack(ZuluValue... args) {
		frame.pack();
		return new ZuluReference(frame);
	}

	public static ZuluValue CheckBox(ZuluValue... args) {
		Arguments.check(7, args.length);
		final JCheckBox checkBox = new JCheckBox(args[0].toString(), args[1].toString().equals("true"));
		checkBox.setBounds(args[2].asInteger().intValue(), args[3].asInteger().intValue(), args[4].asInteger().intValue(), args[5].asInteger().intValue());
		checkBox.addActionListener((l) -> {
			JCheckBox source = (JCheckBox) l.getSource();
			((ZuluFunction) args[6]).execute(new ZuluAtom(String.valueOf(source.isSelected())), new ZuluString(source.getText()));
		});
		frame.add(checkBox);
		return new ZuluReference(frame);
	}

	public static ZuluValue ClearFrame(ZuluValue... args) {
		synchronized (new Object()) {
			frame.getContentPane().removeAll();
			Modules.get("barley").get("sleep").execute(new ZuluNumber(10));
		}
		return new ZuluReference(frame);
	}

	public static ZuluValue Image(ZuluValue... args) {
		ImageIcon icon = new ImageIcon(args[0].toString());
		//Image img = icon.getImage();
		final JLabel labelPic = new JLabel("");
		labelPic.setBounds(args[1].asInteger().intValue(), args[2].asInteger().intValue(), args[3].asInteger().intValue(), args[4].asInteger().intValue());
		labelPic.setIcon(icon);
		frame.add(labelPic);
		return new ZuluReference(frame);
	}

	public static ZuluValue ProgressBar(ZuluValue... args) {
		Arguments.check(6, args.length);
		final JProgressBar progressBar = new JProgressBar(args[0].asInteger().intValue(), args[1].asInteger().intValue());
		progressBar.setBounds(args[2].asInteger().intValue(), args[3].asInteger().intValue(), args[4].asInteger().intValue(), args[5].asInteger().intValue());
		progressID++;
		bars.add(progressBar);
		frame.add(progressBar);
		return new ZuluNumber(progressID - 1);
	}

	public static ZuluValue StepBar(ZuluValue... args) {
		Arguments.check(2, args.length);
		bars.get(args[0].asInteger().intValue()).setValue(bars.get(args[0].asInteger().intValue()).getValue() + args[1].asInteger().intValue());
		frame.repaint();
		frame.revalidate();
		return args[0];
	}
}
