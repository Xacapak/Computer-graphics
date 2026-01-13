package Zabgu;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FillingAlgorithms {

    public static void fillTriangle() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nАлгоритм заливки треугольника");
        //System.out.println("Введите координаты трех вершин треугольника:");

        ArrayList<Point> triangleVertices = new ArrayList<>();

        System.out.println("Выберите заполнение вершин");
        System.out.println("1) Ручной ввод координат вершин");
        System.out.println("2) Обычный треугольник");
        System.out.println("3) Равнобедренный треугольник");
        System.out.println("4) Вытянутый треугольник");

        int algorithmChoice = scanner.nextInt();
        switch (algorithmChoice){
            case 1:{
                for (int i = 0; i < 3; i++) {
                    System.out.println("Вершина " + (i + 1) + ":");
                    System.out.print("x: ");
                    int x = scanner.nextInt();
                    System.out.print("y: ");
                    int y = scanner.nextInt();
                    triangleVertices.add(new Point(x, y));
                }
                break;
            }
            case 2:{
                triangleVertices.add(new Point(100, 10));
                triangleVertices.add(new Point(50, 50));
                triangleVertices.add(new Point(150, 90));
                break;
            }
            case 3:{
                triangleVertices.add(new Point(100, 20));
                triangleVertices.add(new Point(50, 80));
                triangleVertices.add(new Point(150, 80));
                break;
            }
            case 4:{
                triangleVertices.add(new Point(50, 0));
                triangleVertices.add(new Point(50, 40));
                triangleVertices.add(new Point(200, 100));
                break;
            }
            default:
                System.out.println("Некорректный выбор алгоритма.");
                break;
        }

        // Вычисление всех точек заливки
        ArrayList<Point> points = fillTriangleScanline(triangleVertices);

        // Вывод информации в консоль
        printTriangleInfo(triangleVertices, points);

        // Отображение треугольника
        showTriangle(triangleVertices, points);
    }

    // Основной метод заливки
    public static ArrayList<Point> fillTriangleScanline(ArrayList<Point> vertices) {
        if (vertices.size() != 3) {
            return new ArrayList<>();
        }

        // Извлечение координат из точек
        int x0 = vertices.get(0).x;
        int y0 = vertices.get(0).y;
        int x1 = vertices.get(1).x;
        int y1 = vertices.get(1).y;
        int x2 = vertices.get(2).x;
        int y2 = vertices.get(2).y;

        ArrayList<Point> points = new ArrayList<>();

        // Сортировка координат по оси Y
        if (y0 > y1) { int t = x0; x0 = x1; x1 = t; t = y0; y0 = y1; y1 = t; }
        if (y0 > y2) { int t = x0; x0 = x2; x2 = t; t = y0; y0 = y2; y2 = t; }
        if (y1 > y2) { int t = x1; x1 = x2; x2 = t; t = y1; y1 = y2; y2 = t; }

        // Нахождение точки х3 на длинном ребре
        int x3 = x0;
        if (y2 != y0) {
            x3 = x0 + (int)(((float)(y1 - y0) / (y2 - y0)) * (x2 - x0));
        }

        // Верхняя часть треугольника (y0..y1)
        int yMin = y0;
        int yMax = y1;
        float xMin, xMax, ix0, ix1;

        int height = yMax - yMin;
        if (height > 0) {
            xMin = (float)(x0);
            xMax = (float)(x0);

            // Вычисление шагов интерполяции для левого и правого краев
            ix0 = (float)(x1 - x0) / height;    // шаг для ребра V0-V1
            ix1 = (float)(x3 - x0) / height;    // шаг для ребра V0-V3

            // Обеспечение: xMin всегда левый край, xMax - правый
            if (ix0 > ix1) {
                float t = ix0; ix0 = ix1; ix1 = t;
            }

            // Заполнение верхней части треугольника
            fillTrianglePart(points, yMin, yMax, xMin, xMax, ix0, ix1);
        }

        // Нижняя часть треугольника (y1..y2)
        if (yMax < y2) {
            yMin = y1;
            yMax = y2;

            height = yMax - yMin;
            if (height > 0) {
                xMin = (float)(x1);
                xMax = (float)(x3);

                ix0 = (float)(x2 - x1) / height;
                ix1 = (float)(x2 - x3) / height;

                if (ix0 < ix1) {
                    float t = xMin; xMin = xMax; xMax = t;
                    t = ix0; ix0 = ix1; ix1 = t;
                }

                // Заполнение нижней части треугольника
                fillTrianglePart(points, yMin, yMax, xMin, xMax, ix0, ix1);
            }
        }

        return points;
    }

    // Вспомогательный метод для заполнения части треугольника
    private static void fillTrianglePart(ArrayList<Point> points,
                                         int yMin, int yMax,
                                         float xMin, float xMax,
                                         float ix0, float ix1) {
        // Обработка отрицательных Y
        if (yMin < 0) {
            xMin -= ix0 * yMin;
            xMax -= ix1 * yMin;
            yMin = 0;
        }

        // Заполнение каждой строки
        for (int y = yMin; y < yMax; ++y) {
            int startX = (int)xMin;         // левый край строки
            int endX = (int)xMax;           // правый край строки

            // Добавление всех точек в строке
            for (int x = startX; x <= endX; ++x) {
                points.add(new Point(x, y));
            }

            // Обновление координат краев для следующей строки
            xMin += ix0;
            xMax += ix1;
        }
    }

    // Вывод информации в консоль
    private static void printTriangleInfo(ArrayList<Point> vertices, ArrayList<Point> points) {
        System.out.println("\nРезультат алгоритма заливки треугольника");
        System.out.println("Вершины треугольника:");

        char vertexName = 'A';
        for (Point p : vertices) {
            System.out.println(vertexName + ": (" + p.x + ", " + p.y + ")");
            vertexName++;
        }

        System.out.println("Всего залитых точек: " + points.size());
    }

    private static void showTriangle(ArrayList<Point> vertices, ArrayList<Point> points) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Отрисовка: 'Алгоритм заливки треугольника'");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 500);
            frame.setLocationRelativeTo(null);

            TrianglePanel panel = new TrianglePanel(vertices, points);
            frame.add(panel);
            frame.setVisible(true);
        });
    }

// Панель для отображения треугольника
    static class TrianglePanel extends JPanel {
        private ArrayList<Point> vertices;
        private ArrayList<Point> points;

        public TrianglePanel(ArrayList<Point> vertices, ArrayList<Point> points) {
            this.vertices = vertices;
            this.points = points;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // Нахождение границы для масштабирования
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;

            // Поиск границы среди вершин
            for (Point p : vertices) {
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }

            // Добавление отступов
            int indent = 10;
            minX -= indent;
            maxX += indent;
            minY -= indent;
            maxY += indent;

            // Вычисление масштаба
            int dataWidth = maxX - minX;
            int dataHeight = maxY - minY;

            float scaleX = (panelWidth - 100) / (float) Math.max(1, dataWidth);
            float scaleY = (panelHeight - 100) / (float) Math.max(1, dataHeight);
            float scale = Math.min(scaleX, scaleY);

            // Смещение для центрирования
            int offsetX = 50;
            int offsetY = 50;

            // Отрисовка координатных осей
            g2d.setColor(Color.LIGHT_GRAY);

            // Ось X (ближе к низу, так как Y растет вниз)
            int xAxisY = offsetY + Math.round((0 - minY) * scale);
            g2d.drawLine(offsetX, xAxisY, panelWidth - offsetX, xAxisY);
            for (int x = minX; x <= maxX; x += Math.max(1, dataWidth / 10)) {
                int screenX = offsetX + Math.round((x - minX) * scale);
                g2d.drawLine(screenX, xAxisY - 3, screenX, xAxisY + 3);
                g2d.drawString(String.valueOf(x), screenX - 5, xAxisY + 15);
            }

            // Ось Y
            int yAxisX = offsetX + Math.round((0 - minX) * scale);
            g2d.drawLine(yAxisX, offsetY, yAxisX, panelHeight - offsetY);

            // Деления на оси Y (Y растет ВНИЗ!)
            for (int y = minY; y <= maxY; y += Math.max(1, dataHeight / 10)) {
                // В растровой системе: больше Y → ниже на экране
                int screenY = offsetY + Math.round((y - minY) * scale);
                g2d.drawLine(yAxisX - 3, screenY, yAxisX + 3, screenY);
                g2d.drawString(String.valueOf(y), yAxisX - 25, screenY + 5);
            }

            g2d.setColor(Color.BLACK);
            g2d.drawString("X", panelWidth - offsetX + 10, xAxisY);
            g2d.drawString("Y", yAxisX, offsetY - 10);

            // Отрисовка всех залитых точек
            g2d.setColor(new Color(100, 150, 255));
            for (Point p : points) {
                int screenX = offsetX + Math.round((p.x - minX) * scale);
                int screenY = offsetY + Math.round((p.y - minY) * scale);
                g2d.fillRect(screenX - 2, screenY - 2, 4, 4);
            }

            // Отрисовка вершины треугольника
            char vertexName = 'A';
            for (Point p : vertices) {
                int screenX = offsetX + Math.round((p.x - minX) * scale);
                int screenY = offsetY + Math.round((p.y - minY) * scale); // Тоже p.y-minY

                g2d.setColor(Color.RED);
                g2d.fillOval(screenX - 5, screenY - 5, 10, 10);

                g2d.setColor(Color.BLACK);
                g2d.drawString(vertexName + "(" + p.x + "," + p.y + ")",
                        screenX + 10, screenY - 10);

                vertexName++;
            }
        }
    }
}