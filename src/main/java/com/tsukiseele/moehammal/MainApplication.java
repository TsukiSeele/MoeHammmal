package com.tsukiseele.moehammal;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.svg.SVGGlyphLoader;
import com.tsukiseele.moehammal.app.Config;
import com.tsukiseele.moehammal.app.CrashManager;
import com.tsukiseele.moehammal.app.InstanceControl;
import com.tsukiseele.moehammal.controls.MainController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainApplication extends Application {
    @FXMLViewFlowContext
    private ViewFlowContext context;

    private static JFXDecorator decorator;

    public static void main(String[] args) {
        //LogUtil.setLevel(LogUtil.Level.CLOSE);
        //WebDriverManager.instance().init(WebDriverManager.BROWSER_TYPE_FIREFOX);
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        if (InstanceControl.checkControl()) {
            new Thread(() -> {
                try {
                    SVGGlyphLoader.loadGlyphsFont(MainApplication.class.getResourceAsStream("/icons/icomoon.svg"),
                            "icomoon.svg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            context = new ViewFlowContext();
            context.register("Stage", stage);

            Flow flow = new Flow(MainController.class);
            DefaultFlowContainer container = new DefaultFlowContainer();
            flow.createHandler(context).start(container);
            // 初始化顶层装饰
            decorator = new JFXDecorator(stage, container.getView());
            context.register("Decorator", decorator);
            decorator.setCustomMaximize(true);
            //Image icon = new Image(MainApplication.class.getResourceAsStream("/icons/icon.png"));
            Image icon = new Image(new FileInputStream(
                    new File(Config.PATH_SOURCE_IMAGES.getAbsolutePath(), "icon.png")));
            ImageView imageView = new ImageView(icon);
            imageView.setFitHeight(28);
            imageView.setFitWidth(28);
            decorator.setGraphic(imageView);
            // 初始化应用外观
            stage.setTitle(Config.APPLICTION_TITLE);
            stage.getIcons().add(icon);
            Scene scene = new Scene(decorator, 1280, 800);

            final ObservableList<String> stylesheets = scene.getStylesheets();
            stylesheets.addAll(
                    MainApplication.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                    MainApplication.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                    MainApplication.class.getResource("/css/moehammal-main.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.setOnCloseRequest(event -> System.exit(0));
            stage.setMinWidth(1080);
            stage.setMinHeight(800);
            stage.show();

            Thread.currentThread().setUncaughtExceptionHandler(new CrashManager());
        }
    }

    public static void showSnackbar(Pane pane, String message) {
        new JFXSnackbar(pane).enqueue(new JFXSnackbar.SnackbarEvent(message));
    }

    public static void showSnackbar(String message) {
        showSnackbar(decorator, message);
    }

    public static JFXDecorator getDecorator() {
        return decorator;
    }
}
