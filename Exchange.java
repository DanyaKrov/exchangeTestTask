import java.util.*;
import java.util.stream.Collectors;

public class Exchange{
    static Random random = new Random();
    static String[] moneySymbols = new String[] {"₿", "€", "$", "₮", "₽"};
    public static void main(String[] args) {
        Map<String, double[]> exchangeBalance = new HashMap<String, double[]>(); // словарь - баланс, где ключ - валюта
        exchangeBalance.put("rub", new double[]{10000, 1});
        exchangeBalance.put("usd", new double[]{1000, 90});
        exchangeBalance.put("eur", new double[]{1000, 99});
        exchangeBalance.put("usdt", new double[]{1000, 90});
        exchangeBalance.put("btc", new double[]{1.5, 59708});
        Map<String, Double> userBalance = new HashMap<String, Double>();
        userBalance.put("rub", 1000000.0);
        userBalance.put("usd", 0.0);
        userBalance.put("eur", 0.0);
        userBalance.put("usdt", 0.0);
        userBalance.put("btc", 0.0);
        HashSet<String> availableConvertations = new HashSet<String>(); // валютные пары
        availableConvertations.add("usdrub");
        availableConvertations.add("eurrub");
        availableConvertations.add("usdbtc");
        availableConvertations.add("usdusdt");
        availableConvertations.add("usdeur");

        System.out.println("Для совершения операции введите: <количество для конвертации> USD/RUB\n" +
                "Введите 1, чтобы открыть свой баланс\n" + "Введите 2 для завершения работы");
        Scanner userInput = new Scanner(System.in);
        String[] inputArgs;
        String[] inputCurrency;
        double amount;
        double amountAfterConvert;
        List<String> currencies;
        boolean running = true;
        while (running) {
            String input = userInput.nextLine();
            if (!input.isEmpty()) {
                if (input.equals("2"))
                    running = false;
                if (input.equals("1"))
                    printBalance(userBalance);
                else {
                    inputArgs = input.split(" "); // отделение количества от валют
                    /* проверка запроса на корректность */
                    if (inputArgs.length == 2) {
                        if (isNumeric(inputArgs[0]))
                            amount = Double.parseDouble(inputArgs[0]);
                        else {
                            System.out.println("Введите корректное число");
                            continue;
                        }
                        currencies = List.of(inputArgs[1].split("/")); // разделение валют
                        currencies = currencies.stream().map(String::toLowerCase).toList(); // смена на нижний регистр
                    }
                    else {
                        System.out.println("Некорректный запрос, повторите попытку");
                        continue;
                    }
                    if (currencies.size() != 2) {
                        System.out.println("Некорректный запрос, необходимо указать две валюты");
                        continue;
                    }
                    if (!(availableConvertations.contains(currencies.get(0) + currencies.get(1)) ||
                            availableConvertations.contains(currencies.get(1) + currencies.get(0)))) {
                        System.out.println("Данная конвертация не поддерживается");
                        continue;
                    }
                    if (userBalance.get(currencies.get(0)) < amount) {
                        System.out.println("К сожалению на вашем балансе недостаточно средств для совершения данной операции");
                        continue;
                    }
                    amountAfterConvert = amount * exchangeBalance.get(currencies.get(0))[1]
                            / exchangeBalance.get(currencies.get(1))[1];
                    if (exchangeBalance.get(currencies.get(1))[0] < amountAfterConvert) {
                        System.out.println("К сожалению в данный момент обменник не может совершить данную операцию\n"
                                + "Повторите попытку позже");
                        continue;
                    }
                    /* совершение операции */
                    userBalance.put(currencies.get(0), userBalance.get(currencies.get(0)) - amount);
                    exchangeBalance.get(currencies.get(0))[0] += amount;
                    exchangeBalance.get(currencies.get(1))[0] -= amountAfterConvert;
                    userBalance.put(currencies.get(1), userBalance.get(currencies.get(1)) + amountAfterConvert);
                    if (currencies.get(1).equals("rub")) // все валюты привязаны к рублю, поэтому его курс не может поменяться(1:1)
                        exchangeBalance.get(currencies.get(0))[1] *= randomChange();
                    else
                        exchangeBalance.get(currencies.get(1))[1] *= randomChange();
                }

            }
        }
    }

    private static boolean isNumeric(String str) { // проверка на положительное число
        for (char c : str.toCharArray())
        {
            if (!(Character.isDigit(c) || c == '.' || c == ',')) return false;
        }
        return true;
    }


    private static double randomChange() {
        return (100.0 - random.nextDouble(-5.0, 5.0)) / 100.0; // случайно выбирает процент изменения курса
    }


    private static void printBalance(Map<String, Double> userBalance) {
        for (int i = 0; i < userBalance.values().parallelStream().toList().size(); i++)
            System.out.println(userBalance.values().parallelStream().toList().get(i) + moneySymbols[i]);
    }
}