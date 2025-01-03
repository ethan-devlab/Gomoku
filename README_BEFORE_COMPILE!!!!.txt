if "package not found error" occurs or to avoid related files' source problem, try compile the files with command below then it should work:

Environment: Win11 + JDK 22 (Java SE Development Kit 22)
https://www.oracle.com/java/technologies/javase/jdk22-archive-downloads.html

1. Assume you are in /Gomoku/src
2. run "javac -cp . ui/InitialUI.java" in terminal or cmd
3. then run "java ui/InitialUI.java" in terminal or cmd

Note that these 3 steps are essential!!!