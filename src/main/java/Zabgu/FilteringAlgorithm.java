package Zabgu;

import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FilteringAlgorithm {

    public static void imageProcessing() {
        Scanner scanner = new Scanner(System.in);

        // Путь к тестовому изображению
        String imagePath = "src/main/resources/Test_foto.jpg";

        // Проверка существования файла
        File file = new File(imagePath);
        if (!file.exists()) {
            System.out.println("Файл не найден: " + imagePath);
            scanner.close();
            return;
        }

        try {
            BufferedImage originalImage = ImageIO.read(file);

            if (originalImage == null) {
                System.out.println("Не удалось загрузить изображение");
                scanner.close();
                return;
            }

            // Меню выбора фильтра
            System.out.println("\n" + "=".repeat(40));
            System.out.println("ВЫБОР ФИЛЬТРА ИЗОБРАЖЕНИЙ");
            System.out.println("=".repeat(40));
            System.out.println("1) Черно-белый фильтр");
            System.out.println("2) Фильтр размытия (матрица 3x3)");
            System.out.println("=".repeat(40));
            System.out.print("Выберите фильтр (1-2): ");

            int filterChoice;
            try {
                filterChoice = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Ошибка ввода! Введите 1 или 2.");
                scanner.nextLine();
                scanner.close();
                return;
            }

            BufferedImage processedImage;
            String filterName;

            switch (filterChoice) {
                case 1 -> {
                    System.out.print("Введите порог яркости (0-255, по умолчанию 128): ");
                    try {
                        int threshold = scanner.nextInt();
                        if (threshold < 0 || threshold > 255) {
                            System.out.println("Некорректное значение! Используется порог по умолчанию 128.");
                            threshold = 128;
                        }
                        processedImage = processBlackWhite(originalImage, threshold);
                    } catch (Exception e) {
                        System.out.println("Ошибка ввода! Используется порог по умолчанию 128.");
                        processedImage = processBlackWhite(originalImage, 128);
                    }
                    filterName = "ЧЕРНО-БЕЛЫЙ";
                }
                case 2 -> {
                    processedImage = applySharpen(originalImage);
                    filterName = "Фильтр улучшения чёткости";
                }
                default -> {
                    System.out.println("Некорректный выбор! Применяется черно-белый фильтр по умолчанию.");
                    processedImage = processBlackWhite(originalImage, 128);
                    filterName = "ЧЕРНО-БЕЛЫЙ";
                }
            }

            // Сохранение результата
            String parentDir = file.getParent();
            String outputFileName = (filterChoice == 1) ? "result_bw.jpg" : "result_blur.jpg";
            String outputPath = parentDir + File.separator + outputFileName;
            File outputFile = new File(outputPath);
            ImageIO.write(processedImage, "jpg", outputFile);

            System.out.println("Изображение обработано и сохранено как: " + outputFileName);

            // Показ результатов
            showImages(originalImage, processedImage, filterName);

        } catch (IOException e) {
            System.out.println("Ошибка при обработке изображения: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
    public static BufferedImage applySharpen(BufferedImage original) {
        float[][] blurMatrix = {
                {-1, -1, -1},
                {-1, 9, -1},
                {-1, -1, -1}
        };

        return applyConvolutionFilter(original, blurMatrix, 1);
    }

    public static BufferedImage applyConvolutionFilter(BufferedImage original,
                                                       float[][] kernel,
                                                       float divisor) {
        int width = original.getWidth();
        int height = original.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Основной цикл обработки (исключая граничные пиксели)
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {

                float redSum = 0, greenSum = 0, blueSum = 0;

                // Свертка с окном 3x3
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int pixelX = x + kx;
                        int pixelY = y + ky;

                        int rgb = original.getRGB(pixelX, pixelY);

                        // Извлечение компонентов цвета
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;

                        float weight = kernel[ky + 1][kx + 1];

                        // Накопление взвешенных сумм
                        redSum += red * weight;
                        greenSum += green * weight;
                        blueSum += blue * weight;
                    }
                }

                // Нормализация и ограничение значений
                int newRed = clamp((int)(redSum / divisor));
                int newGreen = clamp((int)(greenSum / divisor));
                int newBlue = clamp((int)(blueSum / divisor));

                // Формирование нового цвета
                int newRGB = (newRed << 16) | (newGreen << 8) | newBlue;
                result.setRGB(x, y, newRGB);
            }
        }

        copyBorderPixels(original, result);

        return result;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static void copyBorderPixels(BufferedImage source, BufferedImage destination) {
        int width = source.getWidth();
        int height = source.getHeight();

        // Верхняя и нижняя границы
        for (int x = 0; x < width; x++) {
            destination.setRGB(x, 0, source.getRGB(x, 0));
            destination.setRGB(x, height - 1, source.getRGB(x, height - 1));
        }

        // Левая и правая границы (исключая углы)
        for (int y = 1; y < height - 1; y++) {
            destination.setRGB(0, y, source.getRGB(0, y));
            destination.setRGB(width - 1, y, source.getRGB(width - 1, y));
        }
    }

    // ==================== ЧЕРНО-БЕЛЫЙ ФИЛЬТР ====================

    public static BufferedImage processBlackWhite(BufferedImage original, int threshold) {
        int width = original.getWidth();
        int height = original.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = original.getRGB(x, y);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                int brightness = (red + green + blue) / 3;

                int newColor = (brightness > threshold) ? 0xFFFFFF : 0x000000;
                result.setRGB(x, y, newColor);
            }
        }

        return result;
    }

    // ==================== ПОКАЗ ИЗОБРАЖЕНИЙ ====================

    private static void showImages(BufferedImage original, BufferedImage processed, String filterName) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Результат обработки фильтрации");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(900, 500);
            frame.setLocationRelativeTo(null);

            // Основная панель с двумя изображениями
            JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Панель для оригинального изображения
            JPanel originalPanel = new JPanel(new BorderLayout());
            originalPanel.setBorder(BorderFactory.createTitledBorder("ОРИГИНАЛЬНОЕ ИЗОБРАЖЕНИЕ"));

            ImageIcon originalIcon = new ImageIcon(scaleImage(original, 400, 300));
            JLabel originalLabel = new JLabel(originalIcon);
            originalPanel.add(originalLabel, BorderLayout.CENTER);

            // Панель для обработанного изображения
            JPanel processedPanel = new JPanel(new BorderLayout());
            processedPanel.setBorder(BorderFactory.createTitledBorder(filterName + " ФИЛЬТР"));

            ImageIcon processedIcon = new ImageIcon(scaleImage(processed, 400, 300));
            JLabel processedLabel = new JLabel(processedIcon);
            processedPanel.add(processedLabel, BorderLayout.CENTER);

            // Добавление панели в основную
            mainPanel.add(originalPanel);
            mainPanel.add(processedPanel);

            // Сборка в основной панели
            Container contentPane = frame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(mainPanel, BorderLayout.CENTER);

            // Кнопка закрытия
            JButton closeButton = new JButton("Закрыть");
            closeButton.addActionListener(e -> frame.dispose());
            contentPane.add(closeButton, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }

    private static Image scaleImage(BufferedImage image, int maxWidth, int maxHeight) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Вычисление новых размеров с сохранением пропорций
        double ratio = Math.min((double) maxWidth / width, (double) maxHeight / height);
        int newWidth = (int) (width * ratio);
        int newHeight = (int) (height * ratio);

        return image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }
}