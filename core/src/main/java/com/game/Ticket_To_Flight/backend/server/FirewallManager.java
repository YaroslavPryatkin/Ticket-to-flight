package com.game.Ticket_To_Flight.backend.server;

import com.sun.tools.javac.Main;

import java.io.BufferedReader;
import java.io.File;
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

    public static void createFirewallRule(int tcpPort, int udpPort) {
        createRule(RULE_NAME + "_TCP", "TCP", tcpPort);
        createRule(RULE_NAME + "_UDP", "UDP", udpPort);
    }

    private static void createRule(String ruleName, String protocol, int port) {
        try {
            String command = String.format(
                "netsh advfirewall firewall add rule name=\"%s\" dir=in action=allow protocol=%s localport=%d",
                ruleName, protocol, port
            );

            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", command);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            p.getInputStream().transferTo(System.out);
            int exitCode = p.waitFor();

            if (exitCode == 0) {
                System.out.println("Rule " + ruleName + " successfully created.");
            } else {
                System.out.println("No admin rights, rule " + ruleName + " was not created. Continuing anyway...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
