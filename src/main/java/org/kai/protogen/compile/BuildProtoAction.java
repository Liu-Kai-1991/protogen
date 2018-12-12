package org.kai.protogen.compile;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

public class BuildProtoAction extends AnAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getDataContext().getData(CommonDataKeys.PROJECT);
    VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
    assert file != null : "This file should not be null, otherwise user won't see the build button";

    BuildProtoService buildProtoService = BuildProtoService.getInstance(project);
    BuildProtoDialog buildProtoDialog =
        new BuildProtoDialog(e, buildProtoService.getSupportedLanguageList());
    buildProtoService.prefillBuildProtoDialog(buildProtoDialog, file);
    buildProtoDialog.pack();
    buildProtoDialog.setLocationRelativeTo(WindowManager.getInstance().getFrame(project));
    buildProtoDialog.setVisible(true);
  }
}
