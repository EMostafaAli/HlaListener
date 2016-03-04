/*
 * Copyright (c) 2015-2016, Mostafa Ali (engabdomostafa@gmail.com)
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met: Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer. Redistributions
 * in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package ca.mali.hlalistener;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.reactfx.EventStream;
import org.reactfx.value.Val;

import javax.tools.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ca.mali.hlalistener.PublicVariables.primaryStage;

//import org.fxmisc.flowless.VirtualizedScrollPane;

/**
 * @author Mostafa Ali <engabdomostafa@gmail.com>
 */
public class ScriptWindowController implements Initializable {
    private static final String[] KEYWORDS = new String[]{
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    private CodeArea codeArea;
    private ExecutorService executor;

    //Logger
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private BorderPane borderPane;

    @FXML
    private TextField JdkFolderLocation;

    @FXML
    private Button OkButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executor = Executors.newSingleThreadExecutor();
        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        OkButton.disableProperty().bind(Val.map(codeArea.lengthProperty(), n -> n == 0));
        EventStream<?> richChanges = codeArea.richChanges();
        richChanges
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(richChanges)
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);
        codeArea.replaceText(0, 0, getSampleCode());
        borderPane.setCenter(codeArea);
        borderPane.getStylesheets().add(getClass().getResource("/styles/JavaKeywords.css").toExternalForm());
        JdkFolderLocation.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select JDK installation folder");
                File file = new File("C:\\Program Files\\Java");
                if (file.isDirectory()) { //check if it exists and a folder
                    directoryChooser.setInitialDirectory(file);
                }
                File directory = directoryChooser.showDialog(primaryStage);
                if (directory != null) {
                    JdkFolderLocation.setText(directory.getAbsolutePath());
                }
            }
        });
//        borderPane.setCenter(new VirtualizedScrollPane<>(codeArea));
    }

    private static String getSampleCode() {
        return  "package scripts;\n\n" +
                "import ca.mali.hlalistener.IScript;\nimport ca.mali.hlalistener.ListenerFederateAmb;\n" +
                "import hla.rti1516e.AttributeHandleSet;\nimport hla.rti1516e.CallbackModel;\n" +
                "import hla.rti1516e.ObjectClassHandle;\nimport hla.rti1516e.exceptions.FederateInternalError;\n" +
                "import javafx.application.Platform;\nimport javafx.scene.control.Alert;\n" +
                "import org.apache.logging.log4j.Level;\nimport org.apache.logging.log4j.LogManager;\n\n" +
                "import java.io.File;\n\nimport static ca.mali.hlalistener.PublicVariables.*;\n\n" +
                "public class Script implements IScript {\n" +
                "\n" +
                "    //Logger\n" +
                "    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();\n" +
                "    @Override\n" +
                "    public void runScript() {\n" +
                "        try {\n" +
                "            logger.log(Level.INFO, \"Use the logger to log any message to the log file\");" +
                "\n" +
                "            //Uncomment next line if you want to customize the federate ambassador\n" +
                "            //fedAmb = new UpdatedListener();\n" +
                "\n" +
                "            //Connect to RTI\n" +
                "            rtiAmb.connect(fedAmb, CallbackModel.HLA_IMMEDIATE);\n" +
                "\n" +
                "            //Create Federation Execution\n" +
                "            rtiAmb.createFederationExecution(\"HLA Listener Test\",\n" +
                "                    new File(\"FOMs/RestaurantFOMmodule.xml\").toURI().toURL());\n" +
                "\n" +
                "            //Join Federation Execution\n" +
                "            rtiAmb.joinFederationExecution(\"Script fed\", \"HLA Listener Test\");\n" +
                "\n" +
                "            //IMPORTANT: Keep the following lines if you want publish / subscribe through the interface\n" +
                "            //subscribe to HLAcurrentFDD to retrieve FDD\n" +
                "            ObjectClassHandle FederationHandle = rtiAmb.getObjectClassHandle(\"HLAobjectRoot.HLAmanager.HLAfederation\");\n" +
                "            currentFDDHandle = rtiAmb.getAttributeHandle(FederationHandle, \"HLAcurrentFDD\");\n" +
                "            AttributeHandleSet set = rtiAmb.getAttributeHandleSetFactory().create();\n" +
                "            set.add(currentFDDHandle);\n" +
                "            rtiAmb.subscribeObjectClassAttributes(FederationHandle, set);\n" +
                "            rtiAmb.requestAttributeValueUpdate(FederationHandle, set, null);\n" +
                "            //In case of HLA_EVOKED we require this line to receive the FDD\n" +
                "            //evoke one callback will not be enough because the reflect attribute is the second one\n" +
                "            rtiAmb.evokeMultipleCallbacks(.05, 1);\n" +
                "\n" +
                "        } catch (Exception ex) {\n" +
                "            logger.log(Level.ERROR, \"Error in the script\", ex);\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class UpdatedListener extends ListenerFederateAmb {\n" +
                "\n" +
                "    //A sample of overriding, feel free to override any other function\n" +
                "    @Override\n" +
                "    public void announceSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag) throws FederateInternalError {\n" +
                "        //Removing next line might cause unexpected results\n" +
                "        //This line is used to provide updates to HLA listner UI\n" +
                "        //Don't remove unless you know what you are doing\n" +
                "        super.announceSynchronizationPoint(synchronizationPointLabel, userSuppliedTag);\n" +
                "        //Replace the following sample code with your genius code\n" +
                "        //I am using platform here because we change the UI from background thread\n" +
                "        Platform.runLater(() -> {\n" +
                "            Alert x = new Alert(Alert.AlertType.INFORMATION);\n" +
                "            x.setHeaderText(String.format(\"Label: %s announced\", synchronizationPointLabel));\n" +
                "            x.showAndWait();\n" +
                "        });\n" +
                "    }\n" +
                "}";
    }

    @FXML
    private void Cancel_click(ActionEvent event) {
        logger.entry();
        executor.shutdown();
        ((Stage) borderPane.getScene().getWindow()).close();
        logger.exit();
    }

    public void OK_click(ActionEvent actionEvent) {
        logger.entry();
        File helloWorldJava = new File("scripts/Script.java");
        if (helloWorldJava.getParentFile().exists() || helloWorldJava.getParentFile().mkdirs()) {

            try {
                Writer writer = null;
                try {
                    writer = new FileWriter(helloWorldJava);
                    writer.write(codeArea.getText());
                    writer.flush();
                } finally {
                    if (writer != null)
                        writer.close();
                }
                if (!JdkFolderLocation.getText().isEmpty()) {
                    System.setProperty("java.home", JdkFolderLocation.getText());
                }
                DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                if (compiler == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Compiler error");
                    alert.setHeaderText("Error locating JDK compiler");
                    alert.setContentText("Please make sure you have the JDK 8u66 (or higher) installed and paste " +
                            "the installation path to the compiler directory above");
                    alert.showAndWait();
                    return;
                }
                StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

                // This sets up the class path that the compiler will use.
                // I've added the .jar file that contains the DoStuff interface within in it...
//                List<String> optionList = new ArrayList<>();
//                optionList.add("-classpath");
//                optionList.add(System.getProperty("java.class.path") + ";dist/InlineCompiler.jar");

                Iterable<? extends JavaFileObject> compilationUnit
                        = fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(helloWorldJava));
                JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnit);
                if (task.call()) {
                    // Create a new custom class loader, pointing to the directory that contains the compiled
                    // classes, this should point to the top of the package structure!
                    URLClassLoader classLoader = new URLClassLoader(new URL[]{new File("./").toURI().toURL()});
                    // Load the class from the classloader by name....
                    Class<?> loadedClass = classLoader.loadClass("scripts.Script");
                    // Create a new instance...
                    Object obj = loadedClass.newInstance();
                    if (obj instanceof IScript) {
                        IScript iScript = (IScript) obj;
                        iScript.runScript();
                    }
                    executor.shutdown();
                    ((Stage) borderPane.getScene().getWindow()).close();
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                        sb.append(String.format("%s on line %d: %s%n",
                                diagnostic.getKind(),
                                diagnostic.getLineNumber(),
                                diagnostic.getMessage(null)));
                    }
                    logger.log(Level.ERROR, sb.toString());
                    Alert errorMessage = new Alert(Alert.AlertType.ERROR);
                    errorMessage.setTitle("Syntax error");
                    errorMessage.setHeaderText("Unable to parse the script:");
                    errorMessage.setContentText(sb.toString());
                    errorMessage.showAndWait();
                }
                fileManager.close();
            } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                logger.log(Level.FATAL, "Exception", ex);
            }
        }
        logger.exit();
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}