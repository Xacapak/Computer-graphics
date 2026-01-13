package Zabgu;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

public class AlgorithmDrawingLine {

    public static void algorithmDDA(){

        Scanner scanner = new Scanner(System.in);

        System.out.println("Алгоритм ЦДА (Цифровой дифференциальный анализатор)");

        // Ввод данных от пользователя
        System.out.println("Введите x начальной точки: ");
        int x0 = scanner.nextInt();
        System.out.println("Введите y начальной точки: ");
        int y0 = scanner.nextInt();

        System.out.println("Введите x конечной точки: ");
        int x1 = scanner.nextInt();
        System.out.println("Введите y конечной точки: ");
        int y1 = scanner.nextInt();

        // Вычисление точек
        ArrayList<Point> points = calculateDDALine(x0, y0, x1, y1);

        // Вывод точек в консоль
        printPointsToConsole(points,x0, y0, x1, y1);

        // Отображение точек
        showPointsInWindow(points,x0, y0, x1, y1);

    }

    private static ArrayList<Point> calculateDDALine(int x0, int y0, int x1, int y1){

        ArrayList<Point> points = new ArrayList<>();

        int dx = x1 - x0;
        int dy = y1 - y0;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if(steps == 0){
            points.add(new Point(x0, y0));
            return points;
        }

        // Вычисление шагов приращения (вещественные значения)
        float xIncrement = (float) dx / steps;
        float yIncrement = (float) dy / steps;

        float x = x0;
        float y = y0;

        for(int i = 0; i <= steps; i++){
            int roundedX = Math.round(x); // округление до ближайшего целого
            int roundedY = Math.round(y);

            points.add(new Point(roundedX, roundedY));

            // Приращение координат (кроме последней итерации)
            if(i < steps){
                x += xIncrement;
                y += yIncrement;
            }
        }

        return points;
    }

    private static void printPointsToConsole(ArrayList<Point> points,int x0, int y0, int x1, int y1){

        System.out.println("\nРезультат алгоритма ЦДА");
        System.out.println("Начальная точка: (" + x0 + "," + y0 + ")");
        System.out.println("Конечная точка: (" + x1 + "," + y1 + ")");
        System.out.println("Всего точек: " + points.size());
        System.out.println("Координаты точек:");

        for (int i = 0; i < points.size(); i++){
            Point p = points.get(i);
            System.out.println("Точка " + i + ": (" + p.x + ", " + p.y + ")");
        }

        // Вывод параметров алгоритма
        int dx = x1 - x0;
        int dy = y1 - y0;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        System.out.println("\nПараметры алгоритма:");
        System.out.println("dx = " + dx + ", dy = " + dy);
        System.out.println("Количество шагов: " + steps);
        System.out.println("Приращение по X: " + (float)dx / steps);
        System.out.println("Приращение по Y: " + (float)dy / steps);

    }

    private static void showPointsInWindow(ArrayList<Point> points, int x0, int y0, int x1, int y1){

        // Создание и настройка окна
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Алгоритм ЦДА - Результат");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 500);
                frame.setLocationRelativeTo(null);

                // Создание панели для отображения точек
                JPanel panel = new PointsPanel(points,x0, y0, x1, y1);

                frame.add(panel);
                frame.setVisible(true);
            }
        });
    }

    // Внутренний класс для отображения точек
    static class PointsPanel extends JPanel{
        private ArrayList<Point> points;
        private int x0, y0, x1, y1;

        public PointsPanel(ArrayList<Point> points, int x0, int y0, int x1, int y1){
            this.points = points;
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // Нахождение границы координат для масштабирования
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;

            for (Point p : points){
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }

            // Добавление начальной и конечной точки
            minX = Math.min(minX, Math.min(x0, x1));
            maxX = Math.max(maxX, Math.max(x0, x1));
            minY = Math.min(minY, Math.min(y0, y1));
            maxY = Math.max(maxY, Math.max(y0, y1));

            // Добавление отступов
            int indent = 5;
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
            int offSetX = 50;
            int offSetY = 50;

            // Рисование координатных осей
            g2d.setColor(Color.LIGHT_GRAY);

            // Ось Х
            int xAxisY = offSetY + Math.round((maxY - 0) * scale);
            g2d.drawLine(offSetX, xAxisY, panelWidth - offSetX, xAxisY);
            for (int x = minX; x <= maxX; x += Math.max(1, dataWidth / 10)) {
                int screenX = offSetX + Math.round((x - minX) * scale);
                g2d.drawLine(screenX, xAxisY - 3, screenX, xAxisY + 3);
                g2d.drawString(String.valueOf(x), screenX - 5, xAxisY + 15);
            }

            // Ось Y
            int yAxisX = offSetX + Math.round((0 - minX) * scale);
            g2d.drawLine(yAxisX, offSetY, yAxisX, panelHeight - offSetY);
            for (int y = minY; y <= maxY; y += Math.max(1, dataHeight / 10)) {
                int screenY = offSetY + Math.round((maxY - y) * scale);
                g2d.drawLine(yAxisX - 3, screenY, yAxisX + 3, screenY);
                g2d.drawString(String.valueOf(y), yAxisX - 25, screenY + 5);
            }

            g2d.setColor(Color.BLACK);
            g2d.drawString("X", panelWidth - offSetX + 10, xAxisY);
            g2d.drawString("Y", yAxisX, offSetY - 10);

            // Рисуем точки
            for (Point p : points) {
                int screenX = offSetX + Math.round((p.x - minX) * scale);
                int screenY = offSetY + Math.round((maxY - p.y) * scale);

                // Выбираем цвет в зависимости от точки
                if (p.x == x0 && p.y == y0) {
                    g2d.setColor(Color.RED); // Начальная точка
                    g2d.fillOval(screenX - 5, screenY - 5, 10, 10);

                    // Подпись начальной точки
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("A(" + x0 + "," + y0 + ")", screenX + 10, screenY - 10);
                } else if (p.x == x1 && p.y == y1) {
                    g2d.setColor(Color.GREEN); // Конечная точка
                    g2d.fillOval(screenX - 5, screenY - 5, 10, 10);

                    // Подпись конечной точки
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("B(" + x1 + "," + y1 + ")", screenX + 10, screenY + 15);
                } else {
                    g2d.setColor(Color.BLUE); // Промежуточные точки
                    g2d.fillOval(screenX - 3, screenY - 3, 6, 6);
                }
            }

            // Рисуем идеальную линию для сравнения
            g2d.setColor(Color.LIGHT_GRAY);
            int startX = offSetX + Math.round((x0 - minX) * scale);
            int startY = offSetY + Math.round((maxY - y0) * scale);
            int endX = offSetX + Math.round((x1 - minX) * scale);
            int endY = offSetY + Math.round((maxY - y1) * scale);
            g2d.drawLine(startX, startY, endX, endY);

        }
    }

}
