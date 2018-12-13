# ProtoGen

This is a plugin to add a build button to proto file in intellij.

Click the colorful "B" to show the build proto dialog:

![alt text](image/build_button.png "Build proto button")

In the following build proto dialog, we can specific:

1) include path, the path to find proto imports
2) language
3) output directory, the path to generate the proto file
4) the proto compiler path

![alt text](image/build_dialog.png "Build proto dialog")

Each parameter is memorized so the next time user execute build proto, they don't need to input the 
parameter again. 

After clicking the OK button, either build success or build error message will show in the intellij 
notification.

![alt text](image/build_succeed.png "Build succeed")

![alt text](image/build_error.png "Build error")