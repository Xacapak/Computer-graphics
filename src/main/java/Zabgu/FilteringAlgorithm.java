package Zabgu;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FilteringAlgorithm {

    public static void imageProcessing(){

        // Путь к тестовому изображению
        String imagePath = "src/main/resources/Test_foto.jpg";

        // Проверка существования файла
        File file = new File(imagePath);
        if (!file.exists()) {
            System.out.println("Файл не найден: " + imagePath);
            return;
        }

        try {
            BufferedImage originalImage = ImageIO.read(file);

            if (originalImage == null){
                System.out.println("Не удалось загрузить изображение");
                return;
            }

            int threshold = 128;

            BufferedImage bwImage = processBlackWhite(originalImage, threshold);

            String parentDir = file.getParent();
            String outputPath = parentDir + File.separator + "result_bw.jpg";
            File outputFile = new File(outputPath);
            ImageIO.write(bwImage, "jpg", outputFile);

            showImages(originalImage, bwImage);
        }
        catch (IOException e){
            System.out.println("e");
        }
    }

    public static BufferedImage processBlackWhite(BufferedImage original, int threshold){

        int width = original.getWidth();
        int height = original.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                int rgb = original.getRGB(x,y);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                int brightness = (red + green + blue) / 3;

                int newColor;
                if (brightness > threshold){
                    newColor = 0xFFFFFF;
                }else {
                    newColor = 0x000000;
                }
                result.setRGB(x, y, newColor);
            }
        }

        return result;
    }

    private static void showImages(BufferedImage original, BufferedImage processed){

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Результат обработки фильтрации");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 500);
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
            processedPanel.setBorder(BorderFactory.createTitledBorder("ЧЕРНО-БЕЛОЕ"));

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

            frame.setVisible(true);
        });
    }

    private static Image scaleImage(BufferedImage image, int maxWidth, int maxHeight) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Вычисление новых размеров с сохранением пропорций
        double ratio = Math.min((double)maxWidth / width, (double)maxHeight / height);
        int newWidth = (int)(width * ratio);
        int newHeight = (int)(height * ratio);

        return image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }
}
