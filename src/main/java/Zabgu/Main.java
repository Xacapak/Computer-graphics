package Zabgu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите задание:\n 1) Алгоритм рисования ЦДА или Брезенхема." +
                                                    "\n 2) Алгоритм заливки: треугольников, построченная, с затравкой." +
                                                    "\n 3) Алгоритм фильтрации.");

        int taskNumber = scanner.nextInt();

        switch (taskNumber) {
            case 1: {
                System.out.println("\n Алгоритм рисования." +
                        "\n Выберите алгоритм:" +
                        "\n 1) Алгоритм ЦДА (Цифровой дифференциальный анализатор)" +
                        "\n 2) Алгоритм Брезенхема");

                int algorithmChoice = scanner.nextInt();

                switch (algorithmChoice) {
                    case 1: {
                        AlgorithmDrawingLine.algorithmDDA();
                        break;
                    }
                    default:
                        System.out.println("Некорректный выбор алгоритма.");
                        break;
                }
                break;
            }
            case 2: {
                System.out.println("Алгоритм заливки: треугольников, построченная, с затравкой.");
                System.out.println("Выберите алгоритм:");
                System.out.println("1) Алгоритм заливки 'Треугольников'.");

                int algorithmChoice = scanner.nextInt();
                switch (algorithmChoice){
                    case 1:{
                        FillingAlgorithms.fillTriangle();
                        break;
                    }
                    default:
                        System.out.println("Некорректный выбор алгоритма.");
                        break;
                }
                break;
            }
            case 3: {
                FilteringAlgorithm.imageProcessing();
                break;
            }
            default:
                System.out.println("Выбрано некорректное задание.");
                break;
        }
    }
}