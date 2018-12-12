package org.kai.protogen.notification;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.notification.Notification;

public class BuildNotifier {
  private Project project;

  public BuildNotifier(Project project) {
    this.project = project;
  }

  public void showBuildSuccess(String content) {
    Notification notification =
        new Notification("ProtoGen", "Build Succeeded", content, NotificationType.INFORMATION);
    notification.notify(project);
  }

  public void showBuildCommand(String command) {
    Notification notification =
        new Notification(
            "ProtoGen",
            "Execute Build",
            String.format("Executing: %s", command),
            NotificationType.INFORMATION);
    notification.notify(project);
  }

  public void showBuildError(String errorMessage) {
    Notification notification =
        new Notification("ProtoGen", "Build Error", errorMessage, NotificationType.ERROR);
    notification.notify(project);
  }
}
