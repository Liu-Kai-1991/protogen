package org.kai.protogen.compile;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class BuildProtoActionGroup extends DefaultActionGroup {
  @Override
  public void update(@NotNull AnActionEvent event) {
    Project project = event.getDataContext().getData(CommonDataKeys.PROJECT);
    VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
    // Show the build proto action only for proto file
    event.getPresentation().setEnabled(file != null && project != null);
    event
        .getPresentation()
        .setVisible(file != null && project != null && file.getPath().endsWith("proto"));
  }
}
