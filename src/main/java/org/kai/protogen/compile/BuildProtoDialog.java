package org.kai.protogen.compile;

import com.google.common.collect.ImmutableList;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BuildProtoDialog extends JDialog {

  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JTextField protoPathTextField;
  private JTextField outputTextField;
  private JComboBox<String> languageComboBox;
  private JTextField protoCompilerPathTextField;
  private JButton getSystemProtoCompilerPathButton;
  private AnActionEvent intellijAction;

  BuildProtoDialog(AnActionEvent intellijAction, ImmutableList<String> languageList) {
    this.intellijAction = intellijAction;
    languageList.forEach(language -> languageComboBox.addItem(language));
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    buttonOK.addActionListener(e -> onOk());
    buttonCancel.addActionListener(e -> onCancel());

    // call onCancel() when cross is clicked
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            onCancel();
          }
        });
  }

  void setProtoPathText(String protoPath) {
    protoPathTextField.setText(protoPath);
  }

  void setOutputText(String outputPath) {
    outputTextField.setText(outputPath);
  }

  void setLanguage(String language) {
    languageComboBox.setSelectedItem(language);
  }

  void setProtoCompilerPath(String protoCompilerPath) {
    protoCompilerPathTextField.setText(protoCompilerPath);
  }

  private void onCancel() {
    dispose();
  }

  private void onOk() {
    VirtualFile virtualFile = intellijAction.getData(CommonDataKeys.VIRTUAL_FILE);
    assert virtualFile != null
        : "This file should not be null, otherwise user won't see the build button";
    Project project = intellijAction.getDataContext().getData(CommonDataKeys.PROJECT);
    assert project != null
        : "Project should not be null, otherwise user won't see the build button";
    String projectPath = project.getBasePath();

    String protoPath = protoPathTextField.getText();
    if (BuildProtoUtil.invalidDirectory(projectPath, protoPath)) {
      Messages.showErrorDialog(protoPath + " not exist", "Error");
      return;
    }
    String language = String.valueOf(languageComboBox.getSelectedItem());
    String outputPath = outputTextField.getText();
    if (BuildProtoUtil.invalidDirectory(projectPath, outputPath)) {
      Messages.showErrorDialog(outputPath + " not exist", "Error");
      return;
    }
    String protoCompilerPath = protoCompilerPathTextField.getText();
    if (!ProtoCompilerUtil.checkProtoCompilerPathValid(protoCompilerPath)) {
      Messages.showErrorDialog(protoCompilerPath + " not exist or not valid", "Error");
      return;
    }

    BuildParameter buildParameter =
        BuildParameter.builder()
            .addFile(virtualFile)
            .addLanguage(language)
            .addProtoPath(protoPath)
            .addOutputPath(outputPath)
            .addProtoCompilerPath(protoCompilerPath)
            .addProjectBasePath(projectPath)
            .build();

    BuildProtoService buildProtoService = BuildProtoService.getInstance(project);
    buildProtoService.executeBuildCommand(buildParameter);
    onCancel();
  }
}
