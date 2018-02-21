import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Andrei on 21.02.2018.
 */
class BaseCalculator {
    private String fileName;
    private String outputFileName;
    private List<String> listLines = new ArrayList<>();
    private static final String PACKAGE = "./resources/";
    private static final String TOTAL = "=";
    private List<String> results = new ArrayList<>();

    BaseCalculator(String fileName, String outputFileName) {
        this.fileName = fileName;
        this.outputFileName = outputFileName;
    }

    /**
     * Функция проверяет, является ли текущий символ оператором
     */
    boolean isOperator(char ch) {
        switch (ch) {
            case '-':
            case '+':
            case '*':
            case '/':
            case '^':
            case '%':
                return true;
        }
        return false;
    }

    void readFileByLine() {

        try (Stream<String> stream = Files.lines(Paths.get(PACKAGE + fileName))) {
            stream.forEach(s -> listLines.add(s));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeResults() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(PACKAGE + outputFileName))) {
            for (String result : results) {
                writer.write(result);
                writer.write("\r\n");
            }
        }
    }


    protected String convertToReversePolishNotation(String line) throws Exception {
        StringBuilder changedLine = new StringBuilder("");
        char operator = 0;
        char tempSymbol;

        for (int i = 0; i < line.length(); i++) {
            tempSymbol = line.charAt(i);
            if (isOperator(tempSymbol)) {
                changedLine.append(" ");
                operator = tempSymbol;
            } else {
                changedLine.append(tempSymbol);
            }
        }

        return changedLine.append(" ").append(operator).toString();
    }


    /**
     * Считает выражение, записанное в обратной польской нотации
     *
     * @return double result
     */
    List<String> calculate() throws Exception {
        for (String line : listLines) {
            String sIn = convertToReversePolishNotation(line);

            double first , second;
            boolean isDivisionByZero = false;
            String literal;
            Deque<Double> stack = new ArrayDeque<>();
            StringTokenizer st = new StringTokenizer(sIn);
            while (st.hasMoreTokens()) {
                try {
                    literal = st.nextToken().trim();
                    if (1 == literal.length() && isOperator(literal.charAt(0))) {
                        if (stack.size() < 2) {
                            throw new Exception("Неверное количество данных в стеке для операции " + literal);
                        }
                        second = stack.pop();
                        first = stack.pop();
                        switch (literal.charAt(0)) {
                            case '+':
                                first += second;
                                break;
                            case '-':
                                first -= second;
                                break;
                            case '/':
                                if (second != 0) {
                                    first /= second;
                                } else isDivisionByZero = true;
                                break;
                            case '*':
                                first *= second;
                                break;
                            case '%':
                                first = (first / 100) * second;
                                break;
                            case '^':
                                first = Math.pow(first, second);
                                break;
                            default:
                                throw new Exception("Недопустимая операция " + literal);
                        }
                        stack.push(first);
                    } else {
                        first = Double.parseDouble(literal);
                        stack.push(first);

                    }
                } catch (Exception e) {
                    throw new Exception("Недопустимый символ в выражении");
                }
            }

            if (stack.size() > 1) {
                throw new Exception("Количество операторов не соответствует количеству операндов");
            }
            String rez;
            double res = stack.pop();
            if (!isDivisionByZero) {
                if ((res % 1) == 0) {
                    rez = String.valueOf((int) Math.round(res));
                } else {
                    rez = formatNumber(res);
                }

            } else rez = "Division by zero";

            results.add(line + TOTAL + rez);
        }
        return results;
    }

    private String formatNumber(double number) {
        String[] array = String.valueOf(number).split("\\.");
        if (array[1].length() > 5) {
            while (array[1].length() > 5) {
                array[1] = array[1].substring(0, 5);
            }
        }

        if (array[1].length() < 5) {
            while (array[1].length() < 5) {
                array[1] = array[1] + "0";
            }
        }

        return array[0] + "." + array[1];
    }
}
