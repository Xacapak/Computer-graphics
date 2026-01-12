package Zabgu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        boolean exitProgram =false;

        while (!exitProgram){

            System.out.println("=".repeat(40));
            System.out.println("ГРАФИЧЕСКИЕ АЛГОРИТМЫ");
            System.out.println("=".repeat(40));
            System.out.println("1) Алгоритм рисования линий");
            System.out.println("2) Алгоритм заливки треугольника");
            System.out.println("3) Алгоритм фильтрации изображений");
            System.out.println("4) Выход из программы");
            System.out.println("=".repeat(40));
            System.out.print("Выберите задание (1-4): ");

            int taskNumber;
            try {
                taskNumber = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Ошибка ввода! Введите число от 1 до 3.");
                scanner.nextLine();
                continue;
            }

            switch (taskNumber) {
                case 1 -> {
                    System.out.println("\nАлгоритм рисования линий:");
                    AlgorithmDrawingLine.algorithmDDA();
                }
                case 2 -> {
                    System.out.println("\nАлгоритм заливки треугольника");
                    FillingAlgorithms.fillTriangle();
                }
                case 3 -> {
                    System.out.println("\nАлгоритм фильтрации изображений");
                    FilteringAlgorithm.imageProcessing();
                }
                case 4 -> {
                    System.out.println("Выход из программы.");
                    exitProgram = true;
                }
                default -> System.out.println("Некорректный номер задания!\n");
            }
        }

        scanner.close();

    }
}