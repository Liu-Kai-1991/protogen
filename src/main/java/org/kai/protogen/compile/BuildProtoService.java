package org.kai.protogen.compile;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.kai.protogen.notification.BuildNotifier;

@State(name = "BuildProtoService")
public final class BuildProtoService implements PersistentStateComponent<BuildProtoService.Config> {
  private Config config = new Config();
  private BuildNotifier buildNotifier;

  private static ImmutableMap<String, String> languageParameterMap =
      ImmutableMap.<String, String>builder()
          .put("C++", "cpp_out")
          .put("C#", "csharp_out")
          .put("Java", "java_out")
          .put("Javascript", "js_out")
          .put("Objective C", "objc_out")
          .put("Php", "php_out")
          .put("Pyhton", "python_out")
          .put("Ruby", "ruby_out")
          .put("Go", "go_out")
          .build();

  ImmutableList<String> getSupportedLanguageList() {
    return ImmutableList.copyOf(languageParameterMap.keySet());
  }

  void prefillBuildProtoDialog(BuildProtoDialog buildProtoDialog, VirtualFile virtualFile) {
    String filePath = virtualFile.getPath();
    String language =
        config.getFileToLanguageMap().getOrDefault(filePath, config.getLastLanguage());
    String protoPath =
        config.getFileToProtoPathMap().getOrDefault(filePath, config.getLastProtoPath());
    String outputPath =
        config.getFileToOutputPathMap().getOrDefault(filePath, config.getLastOutputPath());
    String protoCompilerPath = config.getProtoCompilerPath();
    if (protoCompilerPath.isEmpty()) {
      protoCompilerPath = ProtoCompilerUtil.getSystemProtoCompilerPath();
    }
    buildProtoDialog.setLanguage(language);
    buildProtoDialog.setProtoPathText(protoPath);
    buildProtoDialog.setOutputText(outputPath);
    buildProtoDialog.setProtoCompilerPath(protoCompilerPath);
  }

  void executeBuildCommand(BuildParameter buildParameter) {
    memorizeParameters(buildParameter);
    callingExternalBuildProcess(buildParameter);
  }

  private void callingExternalBuildProcess(BuildParameter buildParameter) {
    ProcessBuilder processBuilder =
        new ProcessBuilder(
            buildParameter.getProtoCompilerPath(),
            String.format(
                "--proto_path=%s",
                Paths.get(buildParameter.getProjectBasePath(), buildParameter.getProtoPath())),
            String.format(
                "--%s=%s",
                languageParameterMap.get(buildParameter.getLanguage()),
                Paths.get(buildParameter.getProjectBasePath(), buildParameter.getOutputPath())),
            buildParameter.getFile().getPath());
    buildNotifier.showBuildCommand(String.join(" ", processBuilder.command()));
    try {
      Process process = processBuilder.start();
      BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
      BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      StringBuilder stdInputStringBuilder = new StringBuilder();
      StringBuilder stdErrorStringBuilder = new StringBuilder();
      String line;
      while ((line = stdInput.readLine()) != null) {
        stdInputStringBuilder.append(line);
      }
      while ((line = stdError.readLine()) != null) {
        stdErrorStringBuilder.append(line);
      }
      String stdInputString = stdInputStringBuilder.toString();
      String stdErrorString = stdErrorStringBuilder.toString();
      if (process.waitFor() == 0 && stdErrorString.isEmpty()) {
        buildNotifier.showBuildSuccess(stdInputString);
        VirtualFileManager.getInstance().asyncRefresh(null);
      } else {
        buildNotifier.showBuildError(stdErrorString);
      }
    } catch (IOException | InterruptedException e) {
      buildNotifier.showBuildError(e.getMessage());
    }
  }

  private void memorizeParameters(BuildParameter buildParameter) {
    config.setLastLanguage(buildParameter.getLanguage());
    config.setLastOutputPath(buildParameter.getOutputPath());
    config.setLastProtoPath(buildParameter.getProtoPath());
    config.setProtoCompilerPath(buildParameter.getProtoCompilerPath());
    String filePath = buildParameter.getFile().getPath();
    config.getFileToLanguageMap().put(filePath, buildParameter.getLanguage());
    config.getFileToOutputPathMap().put(filePath, buildParameter.getOutputPath());
    config.getFileToProtoPathMap().put(filePath, buildParameter.getProtoPath());
  }

  @Override
  @NotNull
  public Config getState() {
    return config;
  }

  @Override
  public void loadState(@NotNull Config state) {
    config = state;
  }

  private BuildProtoService withBuildNotifier(BuildNotifier buildNotifier) {
    this.buildNotifier = buildNotifier;
    return this;
  }

  static BuildProtoService getInstance(Project project) {
    return ServiceManager.getService(project, BuildProtoService.class)
        .withBuildNotifier(new BuildNotifier(project));
  }

  static final class Config {
    private String lastProtoPath = "";
    private String lastLanguage = "";
    private String lastOutputPath = "";
    private String protoCompilerPath = "";

    private Map<String, String> fileToProtoPathMap = new HashMap<>();
    private Map<String, String> fileToLanguageMap = new HashMap<>();
    private Map<String, String> fileToOutputPathMap = new HashMap<>();

    public Map<String, String> getFileToLanguageMap() {
      return fileToLanguageMap;
    }

    public Map<String, String> getFileToOutputPathMap() {
      return fileToOutputPathMap;
    }

    public Map<String, String> getFileToProtoPathMap() {
      return fileToProtoPathMap;
    }

    public String getLastLanguage() {
      return lastLanguage;
    }

    public String getLastOutputPath() {
      return lastOutputPath;
    }

    public String getLastProtoPath() {
      return lastProtoPath;
    }

    public String getProtoCompilerPath() {
      return protoCompilerPath;
    }

    public void setFileToLanguageMap(Map<String, String> fileToLanguageMap) {
      this.fileToLanguageMap = fileToLanguageMap;
    }

    public void setFileToOutputPathMap(Map<String, String> fileToOutputPathMap) {
      this.fileToOutputPathMap = fileToOutputPathMap;
    }

    public void setFileToProtoPathMap(Map<String, String> fileToProtoPathMap) {
      this.fileToProtoPathMap = fileToProtoPathMap;
    }

    public void setProtoCompilerPath(String protoCompilerPath) {
      this.protoCompilerPath = protoCompilerPath;
    }

    public void setLastLanguage(String lastLanguage) {
      this.lastLanguage = lastLanguage;
    }

    public void setLastOutputPath(String lastOutputPath) {
      this.lastOutputPath = lastOutputPath;
    }

    public void setLastProtoPath(String lastProtoPath) {
      this.lastProtoPath = lastProtoPath;
    }
  }
}
