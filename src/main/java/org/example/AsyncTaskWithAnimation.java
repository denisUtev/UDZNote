package org.example;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AsyncTaskWithAnimation {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    static JFrame frame;

    public void runAsync(Runnable task, Runnable onComplete) {
        // Создаем окно с прогресс-баром
        if (frame == null) {
            frame = new JFrame("Загрузка...");
            //frame.setSize(400, 400);
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);

            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setStringPainted(true);
            progressBar.setFont(Params.BIG_TAB_TITLE_FONT);
            progressBar.setSize(200, 35);

            frame.add(progressBar);
            frame.pack();
            frame.setVisible(true);
        } else {
            frame.setVisible(true);
        }
        // Запускаем задачу в отдельном потоке
        executor.execute(() -> {
            try {
                task.run(); // Выполняем основную задачу
            } finally {
                SwingUtilities.invokeLater(() -> { // Завершаем работу в основном потоке Swing
                    frame.dispose(); // Закрываем окно с прогресс-баром
                    onComplete.run(); // Вызываем функцию обратного вызова
                });
            }
        });
    }
}
