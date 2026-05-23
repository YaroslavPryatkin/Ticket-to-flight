package com.game.Ticket_To_Flight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FirewallManager {
    private static final String RULE_NAME = "Ticket_to_flight_exclusion";

    public static void ensureFirewallRule(int tcpPort, int udpPort) {
        if (isRuleExists(RULE_NAME)) {
            System.out.println("Firewall exclusion found");
        } else {
            System.out.println("Creating firewall exclusion");
            createFirewallRule(tcpPort, udpPort);
        }
    }

    private static boolean isRuleExists(String ruleName) {
        try {
            Process p = Runtime.getRuntime().exec("netsh advfirewall firewall show rule name=\"" + ruleName + "\"");
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), "cp866"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(ruleName)) return true; // Правило найдено
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void createFirewallRule(int tcpPort, int udpPort) {
        try {
            String command = String.format(
                "netsh advfirewall firewall add rule name=\"%s\" dir=in action=allow protocol=ANY localport=%d,%d",
                RULE_NAME, tcpPort, udpPort
            );

            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();

            if (p.exitValue() == 0) {
                System.out.println("Правило успешно создано!");
            } else {
                System.err.println("Ошибка при создании правила. Запустите программу от имени Администратора.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
