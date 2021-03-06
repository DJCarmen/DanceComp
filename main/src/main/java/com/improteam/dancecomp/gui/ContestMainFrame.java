package com.improteam.dancecomp.gui;

import com.improteam.dancecomp.model.ContestModel;
import com.improteam.dancecomp.model.dto.ParticipantDTO;
import com.improteam.dancecomp.model.dto.ScoreDTO;
import com.improteam.dancecomp.scoring.Score;
import com.improteam.dancecomp.scoring.rpss.RpssParticipantRate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.FileURLMapper;
import sun.nio.ch.IOUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jury
 */
public class ContestMainFrame implements DataChangeController {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(ContestMainFrame.class);

    private ContestModel contestModel = new ContestModel();

    private JudgeTable judgeTable;
    private ResultTable resultTable;

    private JFrame mainFrame;
    private JButton jAddButton;
    private JButton jDelButton;
    private JButton jUpButton;
    private JButton jDownButton;
    private JButton pAddButton;
    private JButton pDelButton;
    private JButton pUpButton;
    private JButton pDownButton;
    private JButton saveButton;
    private JButton loadButton;
    private JButton calcButton;
    private JButton printButton;
    private JFileChooser fileChooser;

    public ContestModel getContestModel() {
        return contestModel;
    }

    public void setContestModel(ContestModel contestModel) {
        this.contestModel = contestModel;
    }

    public void createFrame() {
        if (mainFrame != null) {
            logger.error("Application is started already");
        }
        mainFrame = new JFrame("RPSS contest calculation");
        mainFrame .setMinimumSize(new Dimension(600, 500));
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        createControls();
        mainFrame.add(createMailPanel());
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private void createControls() {
        judgeTable = new JudgeTable(contestModel, this);
        judgeTable.addNewJudge();
        resultTable = new ResultTable(contestModel, this);

        jAddButton = newButton("Add Judge");
        jAddButton.addActionListener(new AddJudgeListener());
        jDelButton = newButton("Delete Judge");
        jDelButton.addActionListener(new DeleteJudgeListener());
        jUpButton = newButton("Move Up");
        jUpButton.addActionListener(new MoveUpJudgeListener());
        jDownButton = newButton("Move Down");
        jDownButton.addActionListener(new MoveDownJudgeListener());

        pAddButton = newButton("Add Participant");
        pAddButton.addActionListener(new AddParticipantListener());
        pDelButton = newButton("Delete Participant");
        pDelButton.addActionListener(new DeleteParticipantListener());
        pUpButton = newButton("Move Up");
        pUpButton.addActionListener(new MoveUpParticipantListener());
        pDownButton = newButton("Move Down");
        pDownButton.addActionListener(new MoveDownParticipantListener());

        saveButton = newButton("Save");
        saveButton.addActionListener(new SaveResultsListener());
        loadButton = newButton("Load");
        loadButton.addActionListener(new LoadResultsListener());
        calcButton = newButton("Calculate");
        calcButton.addActionListener(new ResultCalculationListener());
        printButton = newButton("Print");

        fileChooser = new JFileChooser();
    }

    private JButton newButton(String name) {
        JButton result = new JButton(name);
        result.setMinimumSize(new Dimension(130, 25));
        result.setMaximumSize(new Dimension(130, 25));
        return result;
    }

    private JPanel createMailPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(createControlPanel(), BorderLayout.NORTH);
        mainPanel.add(createResultsPanel(), BorderLayout.SOUTH);
        return mainPanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.add(new JScrollPane(judgeTable.getTable()));
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(createJudgeButtonsPanel());
        controlPanel.add(Box.createHorizontalStrut(200));
        controlPanel.add(createDataButtonsPanel());
        return controlPanel;
    }

    private JPanel createResultsPanel() {
        JPanel resultPanel = new JPanel();
        resultPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        resultPanel.setLayout(new BorderLayout());
        resultPanel.add(createParticipantButtonsPanel(), BorderLayout.NORTH);
        resultPanel.add(new JScrollPane(resultTable.getTable()), BorderLayout.CENTER);
        return resultPanel;
    }

    private JPanel createJudgeButtonsPanel() {
        JPanel jButtonPanel = new JPanel();
        jButtonPanel.setLayout(new BoxLayout(jButtonPanel, BoxLayout.Y_AXIS));
        jButtonPanel.add(jAddButton);
        jButtonPanel.add(Box.createVerticalStrut(5));
        jButtonPanel.add(jDelButton);
        jButtonPanel.add(Box.createVerticalStrut(5));
        jButtonPanel.add(jUpButton);
        jButtonPanel.add(Box.createVerticalStrut(5));
        jButtonPanel.add(jDownButton);
        return jButtonPanel;
    }

    private JPanel createParticipantButtonsPanel() {
        JPanel jButtonPanel = new JPanel();
        jButtonPanel.setLayout(new BoxLayout(jButtonPanel, BoxLayout.X_AXIS));
        jButtonPanel.add(pAddButton);
        jButtonPanel.add(Box.createHorizontalStrut(10));
        jButtonPanel.add(pDelButton);
        jButtonPanel.add(Box.createHorizontalStrut(10));
        jButtonPanel.add(pUpButton);
        jButtonPanel.add(Box.createHorizontalStrut(10));
        jButtonPanel.add(pDownButton);
        return jButtonPanel;
    }

    private JPanel createDataButtonsPanel() {
        JPanel jButtonPanel = new JPanel();
        jButtonPanel.setLayout(new BoxLayout(jButtonPanel, BoxLayout.Y_AXIS));
        jButtonPanel.add(saveButton);
        jButtonPanel.add(Box.createVerticalStrut(5));
        jButtonPanel.add(loadButton);
        jButtonPanel.add(Box.createVerticalStrut(5));
        jButtonPanel.add(calcButton);
        jButtonPanel.add(Box.createVerticalStrut(5));
        jButtonPanel.add(printButton);
        return jButtonPanel;
    }

    private class AddJudgeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            judgeTable.addNewJudge();
            fireJudgeDataChanged();
        }
    }

    private class DeleteJudgeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            judgeTable.removeSelectedJudge();
            fireJudgeDataChanged();
        }
    }

    private class MoveUpJudgeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            judgeTable.moveSelectedJudge(true);
            fireJudgeDataChanged();
        }
    }

    private class MoveDownJudgeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            judgeTable.moveSelectedJudge(false);
            fireJudgeDataChanged();
        }
    }

    private class AddParticipantListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            resultTable.addParticipant();
            fireResultDataChanged();
        }
    }

    private class DeleteParticipantListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            resultTable.removeSelectedParticipant();
            fireResultDataChanged();
        }
    }

    private class MoveUpParticipantListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            resultTable.moveSelectedParticipant(true);
            fireResultDataChanged();
        }
    }

    private class MoveDownParticipantListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            resultTable.moveSelectedParticipant(false);
            fireResultDataChanged();
        }
    }

    private class ResultCalculationListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int partsCount = contestModel.getParticipants().size();
            List<RpssParticipantRate> rates = new ArrayList<>(partsCount);
            for (ParticipantDTO part : contestModel.getParticipants()) {
                List<Score> partScores = new ArrayList<>(contestModel.getJudges().size());
                for (ScoreDTO score : contestModel.getScores()) {
                    if (score.getParticipant().equals(part)) partScores.add(score);
                }
                rates.add(new RpssParticipantRate(part, partScores, partsCount));
            }
            RpssParticipantRate.rank(rates);
            contestModel.getPlaces().clear();
            contestModel.getPlaces().addAll(rates);
            fireResultDataChanged();
        }
    }

    private class SaveResultsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (fileChooser.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                try {
                    FileUtils.write(fileChooser.getSelectedFile(), contestModel.serializeModel());
                } catch (IOException ioError) {
                    JOptionPane.showMessageDialog(null, "Can't write to file: " + ioError.getMessage());
                }
            }
        }
    }

    private class LoadResultsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                try {
                    contestModel.deserializeModel(
                            FileUtils.readFileToString(fileChooser.getSelectedFile())
                    );
                    fireJudgeDataChanged();
                    fireResultTableChanged();
                } catch (IOException ioError) {
                    JOptionPane.showMessageDialog(null, "Can't read file: " + ioError.getMessage());
                }
            }
        }
    }

    public void fireJudgeDataChanged() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                judgeTable.fireTableDataChanged();
                fireResultTableChanged();
            }
        });
    }

    public void fireResultDataChanged() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                resultTable.fireTableDataChanged();
            }
        });
    }

    public void fireResultTableChanged() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                resultTable.fireTableStructureChanged();
            }
        });
    }
}
